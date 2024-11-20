package yuquiz.domain.quiz.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import yuquiz.domain.quiz.entity.TriedQuiz;

import java.util.List;
import java.util.Optional;

import static yuquiz.domain.quiz.entity.QTriedQuiz.triedQuiz;

public class CustomTriedQuizRepositoryImpl implements CustomTriedQuizRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public CustomTriedQuizRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<TriedQuiz> getTriedQuizzes(Long userId) {

        return jpaQueryFactory
                .select(triedQuiz)
                .from(triedQuiz)
                .where(triedQuiz.user.id.eq(userId))
                .fetch();
    }

    @Override
    public Optional<Boolean> getIsSolvedByUser_IdAndQuiz_Id(long userId, long quizId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(triedQuiz.isSolved)
                        .from(triedQuiz)
                        .where(
                                triedQuiz.user.id.eq(userId),
                                triedQuiz.quiz.id.eq(quizId))
                        .fetchOne()
        );
    }
}
