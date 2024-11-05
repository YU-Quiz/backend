package yuquiz.domain.chatRoom.dto;

public record MessageReq(
        String roomId,
        String sender,
        String content,
        String createdAt,
        MessageType type
) {
    public MessageReq of(String createdAt) {
        return new MessageReq(roomId, sender, content, createdAt, type);
    }
}
