package yuquiz.domain.chatRoom.dto;

import java.time.LocalDateTime;

public record MessageRes(
        String sender,
        String content,
        LocalDateTime createdAt
) {
    public static MessageRes of(String sender, String content, LocalDateTime createdAt) {
        return new MessageRes(sender, content, createdAt);
    }
}
