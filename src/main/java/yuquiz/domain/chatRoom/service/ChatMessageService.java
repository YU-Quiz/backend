package yuquiz.domain.chatRoom.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuquiz.common.exception.CustomException;
import yuquiz.common.utils.redis.RedisUtil;
import yuquiz.domain.chatRoom.dto.MessageReq;
import yuquiz.domain.chatRoom.dto.MessageRes;
import yuquiz.domain.chatRoom.entity.ChatMessage;
import yuquiz.domain.chatRoom.entity.ChatRoom;
import yuquiz.domain.chatRoom.exception.ChatRoomExceptionCode;
import yuquiz.domain.chatRoom.repository.ChatMessageRepository;
import yuquiz.domain.chatRoom.repository.ChatRoomRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static yuquiz.common.utils.redis.RedisProperties.MESSAGE_PREFIX;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;

    /* 일간 메시지 db에 저장 */
    public void saveMessageInDB(Long roomId, List<MessageReq> messages) {

        ChatRoom foundChatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ChatRoomExceptionCode.INVALID_ID));

        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatRoom(foundChatRoom)
                        .messages(messages)
                        .build()
        );
    }

    /* 메시지 redis에 저장 */
    public void saveMessageInRedis(Long roomId, MessageReq messageReq) {

        MessageReq message = messageReq.of(String.valueOf(LocalDateTime.now()));

        String key = MESSAGE_PREFIX + roomId;
        redisUtil.setList(key, message);
    }

    /* Redis에 저장된 메시지 불러오기 */
    public List<MessageReq> fetchMessagesFromRedis(Long roomId) {

        String key = MESSAGE_PREFIX + roomId;

        return redisUtil.getList(key).stream()
                .map(obj -> (MessageReq) obj)
                .collect(Collectors.toList());
    }

    /* Redis에 저장된 모든 채팅방 id 및 메시지 불러오기 */
    public Map<Long, List<MessageReq>> getAllMessagesGroupedByRoomId() {
        Map<Long, List<MessageReq>> messagesByRoomId = new HashMap<>();
        List<Long> roomIds = redisUtil.getRoomIds();

        for (Long roomId : roomIds) {
            List<MessageReq> messages = fetchMessagesFromRedis(roomId);
            messagesByRoomId.put(roomId, messages);

            deleteMessage(roomId);
        }

        return messagesByRoomId;
    }

    private void deleteMessage(Long roomId) {
        String key = MESSAGE_PREFIX + roomId;
        redisUtil.del(key);
    }

    /* 날짜별 메시지 불러오기 */
    @Transactional(readOnly = true)
    public List<MessageRes> fetchMessagesByDateAndRoomId(Long roomId, LocalDate date) {
        List<MessageReq> chatMessages = chatMessageRepository.findMessagesByChatRoomIdAndDate(roomId, date);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return chatMessages.stream()
                .map(chatMessage -> {
                    LocalDateTime dateTime = LocalDateTime.parse(chatMessage.createdAt(), formatter);
                    return MessageRes.of(chatMessage.sender(), chatMessage.content(), dateTime);
                })
                .collect(Collectors.toList());
    }
}
