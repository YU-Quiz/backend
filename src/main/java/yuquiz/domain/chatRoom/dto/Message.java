package yuquiz.domain.chatRoom.dto;

public record Message(
        String roomId,
        String sender,
        Long userId,
        String content,
        String createdAt,
        MessageType type
) {
    public Message of(String createdAt, Long userId) {
        return new Message(roomId, sender, userId, content, createdAt, type);
    }
}
