package yuquiz.domain.study.exception;

import lombok.AllArgsConstructor;
import yuquiz.common.exception.exceptionCode.ExceptionCode;

@AllArgsConstructor
public enum StudyExceptionCode implements ExceptionCode {

    INVALID_ID(404, "존재하지 않는 스터디입니다."),
    UNAUTHORIZED_ACTION(403, "권한이 없습니다."),
    ALREADY_REGISTERED(409, "이미 가입되었습니다."),
    STUDY_FULL(409, "스터디의 최대 인원수를 초과하였습니다."),
    REQUEST_NOT_EXIST(404, "존재하지 않는 가입 신청입니다.");


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
