package yuquiz.domain.quiz.repository;

import yuquiz.domain.quiz.entity.TriedQuiz;

import java.util.List;
import java.util.Optional;

public interface CustomTriedQuizRepository {
    List<TriedQuiz> getTriedQuizzes(Long userId);

    Optional<Boolean> getIsSolvedByUser_IdAndQuiz_Id(long userId, long quizId);
}
