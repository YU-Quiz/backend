package yuquiz.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yuquiz.common.exception.CustomException;
import yuquiz.common.utils.redis.RedisUtil;
import yuquiz.domain.chatRoom.dto.MessageReq;
import yuquiz.domain.chatRoom.dto.MessageRes;
import yuquiz.domain.chatRoom.dto.MessageType;
import yuquiz.domain.chatRoom.entity.ChatMessage;
import yuquiz.domain.chatRoom.entity.ChatRoom;
import yuquiz.domain.chatRoom.exception.ChatRoomExceptionCode;
import yuquiz.domain.chatRoom.repository.ChatMessageRepository;
import yuquiz.domain.chatRoom.repository.ChatRoomRepository;
import yuquiz.domain.chatRoom.service.ChatMessageService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static yuquiz.common.utils.redis.RedisProperties.MESSAGE_PREFIX;

@ExtendWith(MockitoExtension.class)
public class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ChatMessageService chatMessageService;

    private List<MessageReq> messages;
    private Long roomId;
    private MessageReq messageReq1;
    private MessageReq messageReq2;

    @BeforeEach
    void setUp() {
        roomId = 1L;

        messages = new ArrayList<>();

        messageReq1 =
                new MessageReq("1", "테스터1", "내용1", "2024-11-05 12:00:00", MessageType.TALK);
        messageReq2 =
                new MessageReq("1", "테스터2", "내용2", "2024-11-05 12:00:00", MessageType.TALK);


        messages.add(messageReq1);
        messages.add(messageReq2);
    }

    @Test
    @DisplayName("메시지 DB 저장 테스트")
    void saveMessageInDBTest() {
        // given
        ChatRoom chatRoom = ChatRoom.builder().build();
        given(chatRoomRepository.findById(roomId)).willReturn(Optional.ofNullable(chatRoom));

        // when
        chatMessageService.saveMessageInDB(roomId, messages);

        // then
        verify(chatRoomRepository, times(1)).findById(roomId);
        verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("메시지 DB 저장 실패 테스트 - 채팅방 존재 x")
    void saveMessageInDBFailedByNotFoundChatRoomTest() {
        // given
        when(chatRoomRepository.findById(roomId))
                .thenThrow(new CustomException(ChatRoomExceptionCode.INVALID_ID));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            chatMessageService.saveMessageInDB(roomId, messages);
        });

        // then
        assertEquals(ChatRoomExceptionCode.INVALID_ID.getMessage(), exception.getMessage());
        assertEquals(ChatRoomExceptionCode.INVALID_ID.getStatus(), exception.getStatus());
        verify(chatRoomRepository, times(1)).findById(roomId);
    }

    @Test
    @DisplayName("날짜별 채팅방 메시지 불러오기")
    void fetchMessagesByDateAndRoomIdTest() {
        // given
        LocalDate date = LocalDate.now();
        given(chatMessageRepository.findMessagesByChatRoomIdAndDate(roomId, date)).willReturn(messages);

        // when
        List<MessageRes> messageRes = chatMessageService.fetchMessagesByDateAndRoomId(roomId, date);

        // then
        assertEquals(2, messageRes.size());

        MessageRes firstMessage = messageRes.get(0);
        assertEquals("테스터1", firstMessage.sender());
        assertEquals("내용1", firstMessage.content());
        assertEquals(LocalDateTime.parse("2024-11-05T12:00:00"), firstMessage.createdAt());

        MessageRes secondMessage = messageRes.get(1);
        assertEquals("테스터2", secondMessage.sender());
        assertEquals("내용2", secondMessage.content());
        assertEquals(LocalDateTime.parse("2024-11-05T12:00:00"), secondMessage.createdAt());
    }

    @Test
    @DisplayName("Redis에 메시지 저장")
    void saveMessageInRedisTest() {
        // given
        String key = MESSAGE_PREFIX + roomId;

        // when
        chatMessageService.saveMessageInRedis(roomId, messageReq1);

        // then
        verify(redisUtil).setList(eq(key), any(MessageReq.class));
    }

    @Test
    @DisplayName("Redis에서 메시지 불러오기")
    void fetchMessagesFromRedisTest() {
        // given
        String key = MESSAGE_PREFIX + roomId;

        when(redisUtil.getList(key)).
                thenReturn(
                        messages.stream()
                                .map(msg ->
                                        (Object) msg)
                                .collect(Collectors.toList())
                );

        // when
        List<MessageReq> messages = chatMessageService.fetchMessagesFromRedis(roomId);

        // then
        assertEquals(2, messages.size());

        assertEquals(messageReq1.sender(), messages.get(0).sender());
        assertEquals(messageReq1.content(), messages.get(0).content());
        assertEquals(messageReq1.createdAt(), messages.get(0).createdAt());

        assertEquals(messageReq2.sender(), messages.get(1).sender());
        assertEquals(messageReq2.content(), messages.get(1).content());
        assertEquals(messageReq2.createdAt(), messages.get(1).createdAt());
    }

    @Test
    @DisplayName("모든 채팅방의 Redis 메시지 불러오기 및 삭제")
    void getAllMessagesGroupedByRoomIdTest() {
        // given
        List<Long> roomIds = List.of(1L, 2L);
        String key1 = MESSAGE_PREFIX + "1";
        String key2 = MESSAGE_PREFIX + "2";

        when(redisUtil.getRoomIds()).thenReturn(roomIds);
        when(redisUtil.getList(key1)).thenReturn(Collections.singletonList(messageReq1));
        when(redisUtil.getList(key2)).thenReturn(Collections.singletonList(messageReq2));

        // when
        Map<Long, List<MessageReq>> result = chatMessageService.getAllMessagesGroupedByRoomId();

        // then
        assertEquals(2, result.size());
        assertEquals(List.of(messageReq1), result.get(1L));
        assertEquals(List.of(messageReq2), result.get(2L));

        verify(redisUtil).del(key1);
        verify(redisUtil).del(key2);
    }
}
