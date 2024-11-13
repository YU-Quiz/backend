package yuquiz.common.utils.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static yuquiz.common.utils.redis.RedisProperties.MESSAGE_PREFIX;

@RequiredArgsConstructor
@Component
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    /* Redis 저장. */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /* Redis 정보 가져오기. */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /* Redis 삭제. */
    public void del(String key) {
        redisTemplate.delete(key);
    }

    /* Redis 존재 확인. */
    public boolean existed(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /* Redis 만료시간 설정. */
    public void expire(String key, long seconds) {
        redisTemplate.expire(key, Duration.ofSeconds(seconds));
    }

    /* 남은 expired 시간 가져오기 */
    public Long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /* List형식 저장 */
    public void setList(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    /* List형식 불러오기 */
    public List<Object> getList(String key) {
        return new ArrayList<>(redisTemplate.opsForList()
                .range(key, 0, -1));
    }

    /* 채팅방 관련 key값 불러오기 */
    public List<Long> getRoomIds() {

        Set<String> keys = redisTemplate.keys(MESSAGE_PREFIX + "*");

        List<Long> roomIds = new ArrayList<>();
        if (keys != null) {
            roomIds = keys.stream()
                    .map(key -> key.replace(MESSAGE_PREFIX, "")) // PREFIX 제거
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        }
        return roomIds;
    }
}
