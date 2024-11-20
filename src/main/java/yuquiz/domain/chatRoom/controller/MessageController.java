package yuquiz.domain.chatRoom.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import yuquiz.domain.chatRoom.dto.Message;
import yuquiz.domain.chatRoom.service.ChatMessageService;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;
    private final ChatMessageService chatMessageService;

    /* 방에 메시지 전송 */
    @MessageMapping("/message/{roomId}")
    public Message sendMessage(@DestinationVariable Long roomId, Message message, StompHeaderAccessor accessor) {

        Long userId = (Long) accessor.getSessionAttributes().get("userId");
        Message customMessage = Message.from(message, String.valueOf(LocalDateTime.now()), userId);

        chatMessageService.saveMessageInRedis(roomId, customMessage);
        redisTemplate.convertAndSend(channelTopic.getTopic(), customMessage);
        return message;
    }

    /* 채팅방에 유저가 입장했을 때의 메시지 처리 */
    @MessageMapping("/user/{roomId}")
    public Message addUser(Message message) {

        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
        return message;
    }
}