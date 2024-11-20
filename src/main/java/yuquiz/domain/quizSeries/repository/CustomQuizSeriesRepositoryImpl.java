package yuquiz.domain.quizSeries.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import yuquiz.domain.quiz.entity.Quiz;

import java.util.List;
import java.util.Optional;

import static yuquiz.domain.quiz.entity.QQuiz.quiz;
import static yuquiz.domain.quizSeries.entity.QQuizSeries.quizSeries;


public class CustomQuizSeriesRepositoryImpl implements CustomQuizSeriesRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public CustomQuizSeriesRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Quiz> getQuizzesBySeriesId(long seriesId, Pageable pageable, OrderSpecifier<?> order) {
        List<Quiz> quizzes = jpaQueryFactory
                .select(quiz)
                .from(quizSeries)
                .join(quizSeries.quiz, quiz)
                .where(quizSeries.series.id.eq(seriesId))
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(jpaQueryFactory
                .select(quiz.count())
                .from(quizSeries)
                .join(quizSeries.quiz, quiz)
                .where(quizSeries.series.id.eq(seriesId))
                .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(quizzes, pageable, total);
    }
}
