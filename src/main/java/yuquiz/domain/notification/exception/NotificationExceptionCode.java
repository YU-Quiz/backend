package yuquiz.domain.study.exception;

import lombok.AllArgsConstructor;
import yuquiz.common.exception.exceptionCode.ExceptionCode;

@AllArgsConstructor
public enum NotificationExceptionCode implements ExceptionCode {

    INVALID_REQUEST(400, "알림 번호는 필수 사항입니다.");

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
