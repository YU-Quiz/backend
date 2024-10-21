package yuquiz.domain.chatRoom.websocket.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import yuquiz.common.exception.JwtBlackListException;
import yuquiz.common.exception.JwtExpiredException;
import yuquiz.common.exception.exceptionCode.JwtExceptionCode;
import yuquiz.common.utils.jwt.JwtProvider;
import yuquiz.domain.chatRoom.exception.ChatRoomExceptionCode;
import yuquiz.domain.chatRoom.exception.ChatSendException;
import yuquiz.domain.chatRoom.service.ChatRoomService;
import yuquiz.security.token.blacklist.BlackListTokenService;

import static yuquiz.common.utils.jwt.JwtProperties.ACCESS_HEADER_VALUE;
import static yuquiz.common.utils.jwt.JwtProperties.TOKEN_PREFIX;

@RequiredArgsConstructor
@Component
@Slf4j
public class SocketChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final BlackListTokenService blackListTokenService;
    private final ChatRoomService chatRoomService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 연결 시
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            handleConnect(accessor);
        }

        // 메시지 전송 시
        if (StompCommand.SEND.equals(accessor.getCommand())) {
            handleSend(accessor);
        }

        // 웹소켓 연결 끊을 시
        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            handleDisConnect(accessor);
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String accessTokenInHeader = accessor.getFirstNativeHeader(ACCESS_HEADER_VALUE);
        String accessToken = passingAccessToken(accessTokenInHeader);

        isTokenValid(accessToken);

        Long userId = jwtProvider.getUserId(accessToken);
        Long roomId = getRoomId(accessor);

        if (!chatRoomService.isChatMemberForEnterChat(userId, roomId)) {
            throw new AccessDeniedException(ChatRoomExceptionCode.UNAUTHORIZED_ACTION.getMessage());
        }

        chatRoomService.saveChatMemberInRedis(userId, roomId);
        accessor.getSessionAttributes().put("userId", userId);
    }

    private void handleSend(StompHeaderAccessor accessor) {
        Long roomId = getRoomId(accessor);
        Long userId = getUserId(accessor);

        if (!chatRoomService.isChatMemberForSendMessage(userId, roomId)) {
            throw new ChatSendException(ChatRoomExceptionCode.CANNOT_SEND_MESSAGE);
        }
    }

    private void handleDisConnect(StompHeaderAccessor accessor) {
        String roomIdStr = accessor.getFirstNativeHeader("roomId");

        if (roomIdStr == null) {  // 서버에서 보낸 자동 disconnect는 무시
            return;
        }
        Long roomId = Long.valueOf(roomIdStr);
        Long userId = getUserId(accessor);

        chatRoomService.exitChatRoom(userId, roomId);
    }

    private Long getRoomId(StompHeaderAccessor accessor) {
        return Long.valueOf(accessor.getFirstNativeHeader("roomId"));
    }

    private Long getUserId(StompHeaderAccessor accessor) {
        return Long.valueOf(accessor.getFirstNativeHeader("userId"));
    }

    /* 토큰 유효성 검사 */
    private void isTokenValid(String accessToken) {

        if (blackListTokenService.existsBlackListCheck(accessToken)) {       // AccessToken이 블랙리스트에 있는지.
            throw new JwtBlackListException(JwtExceptionCode.BLACKLIST_ACCESS_TOKEN);
        }

        if (jwtProvider.isExpired(accessToken)) {   // 만료 되었는지
            throw new JwtExpiredException(JwtExceptionCode.ACCESS_TOKEN_EXPIRED);
        }
    }

    /* 토큰 파싱 */
    private String passingAccessToken(String accessTokenInHeader) {
        if (accessTokenInHeader == null || !accessTokenInHeader.startsWith(TOKEN_PREFIX)) {
            throw new RuntimeException("토큰이 유효하지 않음");
        }
        return accessTokenInHeader.substring(TOKEN_PREFIX.length()).trim();
    }
}