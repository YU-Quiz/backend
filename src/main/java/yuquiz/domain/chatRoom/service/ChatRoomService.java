package yuquiz.domain.chatRoom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuquiz.common.utils.redis.RedisUtil;
import yuquiz.domain.studyUser.repository.StudyUserRepository;

import static yuquiz.common.utils.redis.RedisProperties.CHAT_EXPIRATION_TIME;
import static yuquiz.common.utils.redis.RedisProperties.CHAT_PREFIX;
import static yuquiz.common.utils.redis.RedisProperties.MEMBER_PREFIX;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final StudyUserRepository studyUserRepository;
    private final RedisUtil redisUtil;

    /* 채팅방 입장 시, 권한 확인 */
    @Transactional(readOnly = true)
    public boolean isChatMemberForEnterChat(Long userId, Long roomId) {

        return studyUserRepository.existsByChatRoom_IdAndUser_Id(roomId, userId);
    }

    /* 채팅방 권한 인증 성공 시, redis에 캐싱 */
    public void saveChatMemberInRedis(Long userId, Long roomId) {

        String key = CHAT_PREFIX + roomId + MEMBER_PREFIX + userId;
        redisUtil.set(key, userId);
        redisUtil.expire(key, CHAT_EXPIRATION_TIME);
    }

    /* 채팅 보낼 때, redis에 캐싱되어 있는 값으로 확인 */
    public boolean isChatMemberForSendMessage(Long userId, Long roomId) {

        String key = CHAT_PREFIX + roomId + MEMBER_PREFIX + userId;
        return redisUtil.existed(key);
    }

    /* 채팅방 퇴장 시 */
    public void exitChatRoom(Long userId, Long roomId) {

        String key = CHAT_PREFIX + roomId + MEMBER_PREFIX + userId;
        redisUtil.del(key);
    }
}
