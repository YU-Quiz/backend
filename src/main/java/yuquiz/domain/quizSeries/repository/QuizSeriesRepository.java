package yuquiz.domain.quizSeries.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuquiz.domain.quizSeries.entity.QuizSeries;

public interface QuizSeriesRepository extends JpaRepository<QuizSeries, Long>, CustomQuizSeriesRepository {
    void deleteBySeries_IdAndQuiz_Id(Long seriesId, Long quizId);

    boolean existsBySeries_IdAndQuiz_Id(Long seriesId, Long quizId);
}
