package yuquiz.domain.study.dto;

import yuquiz.domain.study.entity.Study;
import yuquiz.domain.study.entity.StudyState;
import yuquiz.domain.studyUser.entity.StudyRole;
import yuquiz.domain.studyUser.entity.UserState;

import java.time.LocalDateTime;

public record StudyRes(
        Long id,
        Long chatRoomId,
        String Name,
        String description,
        LocalDateTime registerDuration,
        Integer maxUser,
        Integer curUser,
        StudyState state,
        boolean isMember,
        StudyRole role,
        UserState userState
) {
    public static StudyRes fromEntity(Study study, boolean isMember, StudyRole role, UserState userState) {
        return new StudyRes(
                study.getId(),
                study.getChatRoom().getId(),
                study.getStudyName(),
                study.getDescription(),
                study.getRegisterDuration(),
                study.getMaxUser(),
                study.getCurrentUser(),
                study.getState(),
                isMember,
                role,
                userState
        );
    }
}
