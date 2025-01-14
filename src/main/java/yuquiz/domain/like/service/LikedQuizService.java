package yuquiz.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuquiz.common.exception.CustomException;
import yuquiz.domain.like.dto.quiz.LikedQuizSortType;
import yuquiz.domain.like.entity.LikedQuiz;
import yuquiz.domain.quiz.dto.quiz.QuizSummaryRes;
import yuquiz.domain.like.repository.LikedQuizRepository;
import yuquiz.domain.quiz.entity.Quiz;
import yuquiz.domain.quiz.exception.QuizExceptionCode;
import yuquiz.domain.quiz.repository.QuizRepository;
import yuquiz.domain.quiz.repository.TriedQuizRepository;
import yuquiz.domain.user.entity.User;
import yuquiz.domain.user.exception.UserExceptionCode;
import yuquiz.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LikedQuizService {

    private final LikedQuizRepository likedQuizRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final TriedQuizRepository triedQuizRepository;

    private static final Integer QUIZ_PER_PAGE = 20;

    public Page<QuizSummaryRes> getLikedQuizzes(Long userId, Integer page, LikedQuizSortType sort) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_USERID));

        Pageable pageable = PageRequest.of(page, QUIZ_PER_PAGE, sort.getSort());

        return likedQuizRepository.findAllByUser(user, pageable)
                .map(likedQuiz -> QuizSummaryRes.fromEntity(likedQuiz.getQuiz(), triedQuizRepository.findIsSolvedByUserAndQuiz(user, likedQuiz.getQuiz())));
    }

    @Transactional
    public void likeQuiz(Long userId, Long quizId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_USERID));
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new CustomException(QuizExceptionCode.INVALID_ID));

        if (likedQuizRepository.existsByUserAndQuiz(user, quiz)) {
            throw new CustomException(QuizExceptionCode.ALREADY_LIKED);
        }

        LikedQuiz likedQuiz = LikedQuiz.builder()
                .user(user)
                .quiz(quiz)
                .build();

        quiz.increaseLikeCount();

        likedQuizRepository.save(likedQuiz);
    }

    @Transactional
    public void deleteLikeQuiz(Long userId, Long quizId) {
        likedQuizRepository.findByUserIdAndQuizId(userId, quizId).ifPresent(lq -> {
            lq.getQuiz().decreaseLikeCount();
            likedQuizRepository.delete(lq);
        });
    }

}
