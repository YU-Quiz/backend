package yuquiz.domain.quizSeries.exception;

import lombok.AllArgsConstructor;
import yuquiz.common.exception.exceptionCode.ExceptionCode;

@AllArgsConstructor
public enum QuizSeriesExceptionCode implements ExceptionCode {
    INVALID_ID(404, "문제집에 추가되지 않은 문제입니다."),
    UNAUTHORIZED_ACTION(403, "권한이 없습니다."),
    ALREADY_ADDED(404, "이미 문제집에 추가된 문제입니다.");

    private final int status;
    private final String message;

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
