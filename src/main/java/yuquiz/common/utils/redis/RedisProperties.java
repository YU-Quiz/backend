package yuquiz.common.utils.redis;

public interface RedisProperties {

    String REFRESH_TOKEN_KEY_PREFIX = "refreshToken::";
    String BLACKLIST_KEY_PREFIX = "blackList::";
    String CODE_KEY_PREFIX = "code::";
    String PASS_KEY_PREFIX = "pass::";
    String CHAT_PREFIX = "chat::";
    String MEMBER_PREFIX = "::member::";
    String MESSAGE_PREFIX = "chat::message::";

    long CODE_EXPIRATION_TIME = 3*60;
    long PASS_EXPIRATION_TIME = 5*60;
    long CHAT_EXPIRATION_TIME = 6*60*60;
}
