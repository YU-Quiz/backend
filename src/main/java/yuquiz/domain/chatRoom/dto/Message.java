package yuquiz.domain.chatRoom.dto;

public record Message(
        String roomId,
        String sender,
        Long userId,
        String content,
        String createdAt,
        MessageType type
) {
    public static Message from(Message message, String createdAt, Long userId) {
        return new Message(
                message.roomId,
                message.sender,
                userId,
                message.content,
                createdAt,
                message.type
        );
    }
}
