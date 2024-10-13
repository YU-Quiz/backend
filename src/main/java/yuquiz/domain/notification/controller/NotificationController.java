package yuquiz.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import yuquiz.domain.notification.dto.DisplayType;
import yuquiz.domain.notification.dto.NotificationRes;
import yuquiz.domain.notification.dto.NotificationSortType;
import yuquiz.domain.notification.service.NotificationService;
import yuquiz.security.auth.SecurityUserDetails;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/users/my/alert")
    public ResponseEntity<?> getAllMyAlert(@AuthenticationPrincipal SecurityUserDetails userDetails,
                                           @RequestParam(value = "page", defaultValue = "0") Integer page,
                                           @RequestParam(value = "sort", defaultValue = "DATE_DESC") NotificationSortType sort,
                                           @RequestParam(value = "view", defaultValue = "UNCHECKED") DisplayType displayType) {

        Page<NotificationRes> notifications = notificationService.getAllNotification(userDetails.getId(), page, sort, displayType);

        return ResponseEntity.status(HttpStatus.OK).body(notifications);
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal SecurityUserDetails userDetails,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return notificationService.subscribe(userDetails.getId(), lastEventId);
    }
}
