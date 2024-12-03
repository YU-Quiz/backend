package yuquiz.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import yuquiz.common.exception.CustomException;
import yuquiz.common.exception.exceptionCode.GlobalExceptionCode;
import yuquiz.common.mail.service.MailService;
import yuquiz.common.mail.strategy.MailStrategy;
import yuquiz.common.utils.redis.RedisUtil;
import yuquiz.domain.user.dto.req.CodeVerificationReq;
import yuquiz.domain.user.exception.UserExceptionCode;
import yuquiz.domain.user.repository.UserRepository;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static yuquiz.common.utils.redis.RedisProperties.CODE_EXPIRATION_TIME;
import static yuquiz.common.utils.redis.RedisProperties.CODE_KEY_PREFIX;

@Service
@RequiredArgsConstructor
public class MailCodeService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final RedisUtil redisUtil;
    @Qualifier("codeMailStrategy")
    private final MailStrategy codeMailStrategy;

    private static final int RESEND_THRESHOLD_SECONDS = 2 * 60;

    /* 회원가입 인증번호 확인 메서드 */
    public void sendCodeToMail(String email) {

        if (userRepository.existsByEmail(email)) {
            throw new CustomException(UserExceptionCode.EXIST_EMAIL);
        }

        if (!checkRetryEmail(email)) {
            throw new CustomException(UserExceptionCode.ALREADY_MAIL_REQUEST);
        }

        String authCode = createCode();

        mailService.sendMail(email, authCode, codeMailStrategy);

        String key = CODE_KEY_PREFIX + email;
        redisUtil.set(key, authCode);
        redisUtil.expire(key, CODE_EXPIRATION_TIME);
    }

    /* 인증번호 재전송 시간 확인 */
    private boolean checkRetryEmail(String email) {

        String key = CODE_KEY_PREFIX + email;
        if (!redisUtil.existed(key)) {
            return true;
        }

        long expireTime = redisUtil.getExpire(key, TimeUnit.SECONDS);
        return expireTime <= RESEND_THRESHOLD_SECONDS;
    }

    /* 인증번호 만드는 메서드 */
    private String createCode() {
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                sb.append(random.nextInt(10));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(GlobalExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    /* 인증번호 확인 메서드 */
    public boolean verifiedCode(CodeVerificationReq codeReq) {

        String key = CODE_KEY_PREFIX + codeReq.email();
        Optional<String> storedCode = Optional.ofNullable((String) redisUtil.get(key));

        if (storedCode.isEmpty()) {            // 유효시간 지나서 redis에 없음
            throw new CustomException(UserExceptionCode.CODE_EXPIRED);
        }
        if (!storedCode.get().equals(codeReq.code())) {      // 코드 일치하지 않음
            throw new CustomException(UserExceptionCode.INVALID_CODE);
        }

        redisUtil.del(key);
        return true;
    }
}
