package yuquiz.domain.chatRoom.dto;

public record Message(
        String roomId,
        String sender,
        String content,
        String createdAt,
        MessageType type
) {
    public Message of(String createdAt) {
        return new Message(roomId, sender, content, createdAt, type);
    }
}
