package yuquiz.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuquiz.common.exception.CustomException;
import yuquiz.domain.user.dto.UserSortType;
import yuquiz.domain.user.dto.req.UserStatusReq;
import yuquiz.domain.user.dto.res.UserSummaryRes;
import yuquiz.domain.user.entity.SuspendDay;
import yuquiz.domain.user.entity.User;
import yuquiz.domain.user.exception.UserExceptionCode;
import yuquiz.domain.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    private static final Integer USER_PER_PAGE = 20;

    @Transactional(readOnly = true)
    public Page<UserSummaryRes> getAllUsers(UserSortType sort, Integer page) {

        Pageable pageable = PageRequest.of(page, USER_PER_PAGE, sort.getSort());
        Page<User> users = userRepository.findAll(pageable);

        return users.map(UserSummaryRes::fromEntity);
    }

    @Transactional
    public void deleteUser(Long userId) {

        userRepository.deleteById(userId);
    }

    @Transactional
    public void updateSuspendStatus(Long userId, UserStatusReq status) {

        User user = userRepository.findById(userId).orElseThrow(()->
                new CustomException(UserExceptionCode.INVALID_USERID));

        switch (status) {
            case SUSPEND -> suspendUser(user);
            case UNSUSPEND -> unsuspendUser(user);
        }
    }

    private void suspendUser(User user) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime unlockedAt = user.getUnlockedAt();
        int bannedCnt = user.getBannedCnt();

        if(unlockedAt == null || unlockedAt.isBefore(now)) {
            user.updateSuspendStatus(now.plusDays(SuspendDay.getDayPerCount(bannedCnt)), bannedCnt + 1);
        }
    }

    private void unsuspendUser(User user) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime unlockedAt = user.getUnlockedAt();
        int bannedCnt = user.getBannedCnt();

        if(unlockedAt != null && unlockedAt.isAfter(now)) {
            user.updateSuspendStatus(now, bannedCnt);
        }
    }
}
