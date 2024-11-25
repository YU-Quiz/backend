package yuquiz.domain.chatRoom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuquiz.domain.chatRoom.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, CustomMessageRepository {
}
