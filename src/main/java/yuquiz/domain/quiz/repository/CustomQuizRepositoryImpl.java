package yuquiz.domain.quiz.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import yuquiz.domain.quiz.dto.quiz.QuizSortType;
import yuquiz.domain.quiz.entity.Quiz;

import java.util.List;
import java.util.Optional;

import static yuquiz.domain.quiz.entity.QQuiz.quiz;


public class CustomQuizRepositoryImpl implements CustomQuizRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public CustomQuizRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Quiz> getQuizzes(String keyword, Pageable pageable, QuizSortType sort, Long subjectId, Long userId) {
        List<Quiz> quizzes = jpaQueryFactory
                .select(quiz)
                .from(quiz)
                .where(wordContain(keyword), subjectEqual(subjectId))
                .orderBy(sort.getOrder())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                jpaQueryFactory
                        .select(quiz.count())
                        .from(quiz)
                        .where(wordContain(keyword), subjectEqual(subjectId))
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(quizzes, pageable, total);
    }

    private BooleanExpression wordContain(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        return Expressions.booleanTemplate(
                "function('match', {0}, {1}, {2}) > 0",
                quiz.title,
                quiz.question,
                keyword
        );
    }

    private BooleanExpression subjectEqual(Long subjectId) {
        if (subjectId == null) {
            return null;
        }
        return quiz.subject.id.eq(subjectId);
    }

}
