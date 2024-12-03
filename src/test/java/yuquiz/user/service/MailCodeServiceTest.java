package yuquiz.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yuquiz.common.exception.CustomException;
import yuquiz.common.mail.service.MailService;
import yuquiz.common.mail.strategy.MailStrategy;
import yuquiz.common.utils.redis.RedisUtil;
import yuquiz.domain.user.dto.req.CodeVerificationReq;
import yuquiz.domain.user.exception.UserExceptionCode;
import yuquiz.domain.user.repository.UserRepository;
import yuquiz.domain.user.service.MailCodeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static yuquiz.common.utils.redis.RedisProperties.CODE_EXPIRATION_TIME;
import static yuquiz.common.utils.redis.RedisProperties.CODE_KEY_PREFIX;

@ExtendWith(MockitoExtension.class)
public class MailCodeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailService mailService;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private MailStrategy codeMailStrategy;

    @InjectMocks
    private MailCodeService mailCodeService;

    private final String testEmail = "test@gmail.com";
    private final String testCode = "123456";
    private CodeVerificationReq codeReq;

    @BeforeEach
    void setUp() {
        codeReq = new CodeVerificationReq(testEmail, testCode);
    }

    @Test
    @DisplayName("이메일로 인증 코드 보내기 테스트")
    void sendCodeToMailSuccessTest() {
        // given
        when(redisUtil.existed(anyString())).thenReturn(false);
        doNothing().when(mailService).sendMail(anyString(), anyString(), eq(codeMailStrategy));

        // when
        mailCodeService.sendCodeToMail(testEmail);

        // then
        verify(mailService).sendMail(eq(testEmail), anyString(), eq(codeMailStrategy));
        verify(redisUtil).set(eq(CODE_KEY_PREFIX + testEmail), anyString());
        verify(redisUtil).expire(eq(CODE_KEY_PREFIX + testEmail), eq(CODE_EXPIRATION_TIME));
    }

    @Test
    @DisplayName("이메일로 인증 코드 보내기 테스트 - 1분 이내로 재전송 테스트")
    void sendCodeToMailAlreadyRequestedTest() {
        // given
        when(redisUtil.existed(anyString())).thenReturn(true);
        when(redisUtil.getExpire(anyString(), any())).thenReturn(130L); // 남은 시간이 130초인 경우

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            mailCodeService.sendCodeToMail(testEmail);
        });

        // then
        assertEquals(UserExceptionCode.ALREADY_MAIL_REQUEST.getStatus(), exception.getStatus());
        assertEquals(UserExceptionCode.ALREADY_MAIL_REQUEST.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("인증 코드 확인 테스트 - 성공")
    void verifiedCodeSuccessTest() {
        // given
        when(redisUtil.get(anyString())).thenReturn(testCode);

        // when
        boolean result = mailCodeService.verifiedCode(codeReq);

        // then
        assertEquals(true, result);
        verify(redisUtil).del(CODE_KEY_PREFIX + testEmail);
    }

    @Test
    @DisplayName("인증 코드 확인 테스트 - 이미 존재하는 이메일")
    void verifiedCodeEmailExistsTest() {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            mailCodeService.sendCodeToMail(testEmail);
        });

        // then
        assertEquals(UserExceptionCode.EXIST_EMAIL.getStatus(), exception.getStatus());
        assertEquals(UserExceptionCode.EXIST_EMAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("인증 코드 확인 테스트 - Redis에 코드 없음 (유효시간 만료)")
    void verifiedCodeExpiredTest() {
        // given
        when(redisUtil.get(anyString())).thenReturn(null);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            mailCodeService.verifiedCode(codeReq);
        });

        // then
        assertEquals(UserExceptionCode.CODE_EXPIRED.getStatus(), exception.getStatus());
        assertEquals(UserExceptionCode.CODE_EXPIRED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("인증 코드 확인 테스트 - 코드 불일치")
    void verifiedCodeMismatchTest() {
        // given
        when(redisUtil.get(anyString())).thenReturn("654321"); // 다른 코드

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            mailCodeService.verifiedCode(codeReq);
        });

        // then
        assertEquals(UserExceptionCode.INVALID_CODE.getStatus(), exception.getStatus());
        assertEquals(UserExceptionCode.INVALID_CODE.getMessage(), exception.getMessage());
    }

}
