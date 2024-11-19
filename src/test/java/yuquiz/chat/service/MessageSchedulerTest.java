package yuquiz.chat.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import yuquiz.domain.chatRoom.dto.Message;
import yuquiz.domain.chatRoom.dto.MessageType;
import yuquiz.domain.chatRoom.service.ChatMessageService;
import yuquiz.domain.chatRoom.service.MessageScheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageSchedulerTest {

    @Mock
    private ChatMessageService chatMessageService;

    @InjectMocks
    private MessageScheduler messageScheduler;

    @Test
    public void testRunDailyMessageProcessJobTest() {

        Map<Long, List<Message>> testMessages = new HashMap<>();
        testMessages.put(1L, List.of(new Message("1", "테스터1", 1L, "내용1", "2024-11-05 12:00:00", MessageType.TALK)));
        testMessages.put(2L, List.of(new Message("1", "테스터2", 2L, "내용2", "2024-11-05 12:00:00", MessageType.TALK)));

        when(chatMessageService.getAllMessagesGroupedByRoomId()).thenReturn(testMessages);

        ReflectionTestUtils.invokeMethod(messageScheduler, "runDailyMessageProcessJob");

        verify(chatMessageService, times(1)).saveMessageInDB(1L, testMessages.get(1L));
        verify(chatMessageService, times(1)).saveMessageInDB(2L, testMessages.get(2L));
    }
}