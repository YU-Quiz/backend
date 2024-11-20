package yuquiz.domain.quizSeries.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuquiz.common.exception.CustomException;
import yuquiz.domain.quiz.dto.quiz.QuizSortType;
import yuquiz.domain.quiz.dto.quiz.QuizSummaryRes;
import yuquiz.domain.quiz.entity.Quiz;
import yuquiz.domain.quiz.exception.QuizExceptionCode;
import yuquiz.domain.quiz.repository.QuizRepository;
import yuquiz.domain.quiz.repository.TriedQuizRepository;
import yuquiz.domain.quizSeries.entity.QuizSeries;
import yuquiz.domain.quizSeries.exception.QuizSeriesExceptionCode;
import yuquiz.domain.quizSeries.repository.QuizSeriesRepository;
import yuquiz.domain.series.entity.Series;
import yuquiz.domain.series.exception.SeriesExceptionCode;
import yuquiz.domain.series.repository.SeriesRepository;
import yuquiz.domain.studyUser.repository.StudyUserRepository;

@Service
@RequiredArgsConstructor
public class QuizSeriesService {

    private final QuizSeriesRepository quizSeriesRepository;
    private final QuizRepository quizRepository;
    private final SeriesRepository seriesRepository;
    private final TriedQuizRepository triedQuizRepository;
    private final StudyUserRepository studyUserRepository;

    private static final int QUIZ_PER_PAGE = 20;

    @Transactional(readOnly = true)
    public Page<QuizSummaryRes> getQuizzesBySeriesId(Long seriesId, Integer page, long userId) {
        if (!validateMember(seriesId, userId)) {
            throw new CustomException(QuizSeriesExceptionCode.UNAUTHORIZED_ACTION);
        }

        Pageable pageable = PageRequest.of(page, QUIZ_PER_PAGE);
        Page<Quiz> quizzes = quizSeriesRepository.getQuizzesBySeriesId(seriesId, pageable, QuizSortType.DATE_DESC.getOrder());

        return quizzes.map(quiz -> {
            boolean isSolved = triedQuizRepository.getIsSolvedByUser_IdAndQuiz_Id(quiz.getId(), userId)
                    .orElse(false);

            return QuizSummaryRes.fromEntity(quiz, isSolved);
        });
    }

    @Transactional
    public void addQuiz(Long seriesId, Long quizId, Long userId) {

        if (!validateCreator(seriesId, userId)) {
            throw new CustomException(QuizSeriesExceptionCode.UNAUTHORIZED_ACTION);
        }

        if (quizSeriesRepository.existsBySeries_IdAndQuiz_Id(seriesId, quizId)) {
            throw new CustomException(QuizSeriesExceptionCode.ALREADY_ADDED);
        }

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new CustomException(SeriesExceptionCode.INVALID_ID));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new CustomException(QuizExceptionCode.INVALID_ID));

        QuizSeries quizSeries = QuizSeries.builder()
                .series(series)
                .quiz(quiz)
                .build();

        quizSeriesRepository.save(quizSeries);
    }

    @Transactional
    public void deleteQuiz(Long seriesId, Long quizId, Long userId) {

        if (!validateCreator(seriesId, userId)) {
            throw new CustomException(QuizSeriesExceptionCode.UNAUTHORIZED_ACTION);
        }

        quizSeriesRepository.deleteBySeries_IdAndQuiz_Id(seriesId, quizId);
    }

    private boolean validateCreator(Long seriesId, Long userId) {
        return seriesRepository.findCreatorIdById(seriesId)
                .map(creatorId -> creatorId.equals(userId))
                .orElse(false);
    }

    private boolean validateMember(Long seriesId, Long userId) {

        return seriesRepository.findStudyIdById(seriesId)
                .map(studyId -> studyUserRepository.existsByStudy_IdAndUser_Id(studyId, userId))
                .orElse(true);
    }
}
