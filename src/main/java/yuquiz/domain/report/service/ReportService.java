package yuquiz.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuquiz.common.exception.CustomException;
import yuquiz.domain.notification.dto.NotificationType;
import yuquiz.domain.notification.service.NotificationService;
import yuquiz.domain.quiz.entity.Quiz;
import yuquiz.domain.quiz.exception.QuizExceptionCode;
import yuquiz.domain.quiz.repository.QuizRepository;
import yuquiz.domain.report.dto.ReportReq;
import yuquiz.domain.report.entity.Report;
import yuquiz.domain.report.entity.ReportType;
import yuquiz.domain.report.exception.ReportExceptionCode;
import yuquiz.domain.report.repository.ReportRepository;
import yuquiz.domain.user.entity.User;
import yuquiz.domain.user.exception.UserExceptionCode;
import yuquiz.domain.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public void reportQuiz(Long quizId, ReportReq reportReq, Long userId) {
        if (reportReq.type() == ReportType.OTHER &&
                Optional.ofNullable(reportReq.reason()).orElse("").isEmpty()) {
            throw new CustomException(ReportExceptionCode.REQUIRED_REASON);
        }

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new CustomException(QuizExceptionCode.INVALID_ID));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_USERID));

        if (reportRepository.existsByReporterAndQuiz(user, quiz)) {
            throw new CustomException(ReportExceptionCode.ALREADY_REPORTED);
        }

        Report report = reportReq.toEntity(quiz, user);

        reportNotification(quiz);

        if (reportRepository.countByQuiz(quiz) + 1 >= 10) {
            quiz.changeVisibility();
        }

        reportRepository.save(report);
    }

    public void reportNotification(Quiz quiz) {
        User user = quiz.getWriter();
        String content = "\"" + quiz.getTitle() + "\" 퀴즈에 대한 신고가 있습니다.";
        String url = "/quiz/play/" + quiz.getId();

        notificationService.send(user, NotificationType.REPORT, content, url);
    }
}
