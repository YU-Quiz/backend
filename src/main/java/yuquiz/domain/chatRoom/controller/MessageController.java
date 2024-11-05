package yuquiz.domain.chatRoom.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;
import yuquiz.domain.chatRoom.dto.MessageReq;
import yuquiz.domain.chatRoom.service.ChatMessageService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;
    private final ChatMessageService chatMessageService;

    /* 방에 메시지 전송 */
    @MessageMapping("/message/{roomId}")
    public MessageReq sendMessage(@DestinationVariable Long roomId, MessageReq messageReq) {

        chatMessageService.saveMessageInRedis(roomId, messageReq);
        redisTemplate.convertAndSend(channelTopic.getTopic(), messageReq);
        return messageReq;
    }

    /* 채팅방에 유저가 입장했을 때의 메시지 처리 */
    @MessageMapping("/user/{roomId}")
    public MessageReq addUser(MessageReq messageReq) {

        redisTemplate.convertAndSend(channelTopic.getTopic(), messageReq);
        return messageReq;
    }
}