package yuquiz.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import yuquiz.common.exception.CustomException;
import yuquiz.domain.notification.dto.*;
import yuquiz.domain.notification.entity.Notification;
import yuquiz.domain.notification.repository.EmitterRepository;
import yuquiz.domain.notification.repository.NotificationRepository;
import yuquiz.domain.study.exception.NotificationExceptionCode;
import yuquiz.domain.user.entity.User;
import yuquiz.domain.user.exception.UserExceptionCode;
import yuquiz.domain.user.repository.UserRepository;

import java.io.IOException;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private final EmitterRepository emitterRepository;
    private static final Integer NOTIFICATION_PER_PAGE = 20;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;



    @Transactional(readOnly = true)
    public Page<NotificationRes> getAllNotification(Long userId, Integer page, NotificationSortType sort, DisplayType displayType) {
        Pageable pageable = PageRequest.of(page, NOTIFICATION_PER_PAGE, sort.getSort());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_USERID));

        Page<Notification> notifications;

        if (displayType == DisplayType.ALL) {
            notifications = notificationRepository.findAllByUser(user, pageable);
        } else {
            if(displayType == DisplayType.CHECKED)
                notifications = notificationRepository.findAllByUserAndIsChecked(user, true, pageable);
            else
                notifications = notificationRepository.findAllByUserAndIsChecked(user, false, pageable);
        }

        return notifications.map(NotificationRes::fromEntity);
    }

    @Transactional
    public void readNotification(Long[] notifications, Long userId) {
        if (notifications == null || notifications.length == 0) {
            throw new CustomException(NotificationExceptionCode.INVALID_REQUEST);
        }

        notificationRepository.readNotificationsWithValidation(notifications, userId);
    }

    public SseEmitter subscribe(Long userId, String lastEventId) {
        String emitterId = userId + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        sendClient(emitter, emitterId, "EventStream created. Id : "+emitterId);

        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));

            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> {
                        sendClient(emitter, entry.getKey() , entry.getValue());
                    });
        }

        return emitter;
    }

    public void send(User user, NotificationType notificationType, String content, String url) {
        Notification notification = notificationRepository.save(createNotification(user, notificationType, content, url));
        String userId = String.valueOf(user.getId());

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByUserId(userId);
        String eventId = userId+"_"+System.currentTimeMillis();
        emitterRepository.saveEventCache(eventId, NotificationRes.fromEntity(notification));
        sseEmitters.forEach(
                (key, emitter) -> {
                    sendClient(emitter, eventId, NotificationRes.fromEntity(notification));
                }
        );
    }

    private void sendClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data));
        } catch (IOException e) {
            emitterRepository.deleteById(emitterId);
        }
    }

    public Notification createNotification(User user, NotificationType notificationType, String content, String url) {
        return Notification.builder()
                .user(user)
                .notificationType(notificationType)
                .message(content)
                .title(content)
                .redirectUrl(url)
                .build();
    }
}
