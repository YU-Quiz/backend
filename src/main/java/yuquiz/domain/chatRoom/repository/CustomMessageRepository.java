package yuquiz.domain.chatRoom.repository;

import yuquiz.domain.chatRoom.dto.Message;

import java.time.LocalDate;
import java.util.List;

public interface CustomMessageRepository {
    List<Message> findMessagesByChatRoomIdAndDate(Long roomId, LocalDate date);
}
