package yuquiz.domain.chatRoom.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yuquiz.domain.chatRoom.service.ChatMessageService;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatMessageService chatMessageService;

    /* 일간 채팅 메시지 불러오기 */
    @GetMapping("/{roomId}/messages/daily")
    public ResponseEntity<?> getDailyMessage(@PathVariable Long roomId) {

        return ResponseEntity.ok(chatMessageService.fetchMessagesFromRedis(roomId));
    }

    /* 날짜별로 메시지 불렁오기 */
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<?> getDateMessage(@PathVariable Long roomId,
                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(chatMessageService.fetchMessagesByDateAndRoomId(roomId, date));
    }
}
