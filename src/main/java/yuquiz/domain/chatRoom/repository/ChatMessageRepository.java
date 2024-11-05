package yuquiz.domain.chatRoom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yuquiz.domain.chatRoom.dto.MessageReq;
import yuquiz.domain.chatRoom.entity.ChatMessage;

import java.time.LocalDate;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT c.messages FROM ChatMessage c WHERE c.chatRoom.id = :chatRoomId AND DATE(c.sendAt) = :date")
    List<MessageReq> findMessagesByChatRoomIdAndDate(@Param("chatRoomId") Long chatRoomId, @Param("date") LocalDate date);

}
