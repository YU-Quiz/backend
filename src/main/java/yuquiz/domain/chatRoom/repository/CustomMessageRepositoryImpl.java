package yuquiz.domain.chatRoom.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import yuquiz.domain.chatRoom.dto.Message;
import yuquiz.domain.chatRoom.entity.QChatMessage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CustomMessageRepositoryImpl implements CustomMessageRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public CustomMessageRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Message> findMessagesByChatRoomIdAndDate(Long roomId, LocalDate date) {
        QChatMessage chatMessage = QChatMessage.chatMessage;

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        return jpaQueryFactory.select(chatMessage.messages)
                .from(chatMessage)
                .where(chatMessage.chatRoom.id.eq(roomId)
                        .and(chatMessage.sendAt.between(startOfDay, endOfDay)))
                .fetch()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
