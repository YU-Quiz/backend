package yuquiz.domain.quizSeries.repository;

import com.querydsl.core.types.OrderSpecifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import yuquiz.domain.quiz.entity.Quiz;

public interface CustomQuizSeriesRepository {

    Page<Quiz> getQuizzesBySeriesId(long seriesId, Pageable pageable, OrderSpecifier<?> order);
}
