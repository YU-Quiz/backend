package yuquiz.domain.chatRoom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import yuquiz.domain.chatRoom.dto.Message;

import java.util.List;
import java.util.Map;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class MessageScheduler {

    private final ChatMessageService chatMessageService;

    @Scheduled(cron = "0 0 0 * * *")
    public void runDailyMessageProcessJob() {

        Map<Long, List<Message>> allMessages = chatMessageService.getAllMessagesGroupedByRoomId();

        allMessages.forEach(chatMessageService::saveMessageInDB);
    }
}
