package yuquiz.domain.study.dto;

import yuquiz.domain.study.entity.Study;
import yuquiz.domain.study.entity.StudyState;
import yuquiz.domain.studyUser.entity.StudyRole;

import java.time.LocalDateTime;

public record StudyRes(
        Long id,
        String Name,
        String description,
        LocalDateTime registerDuration,
        Integer maxUser,
        Integer curUser,
        StudyState state,
        boolean isMember,
        StudyRole role
) {
    public static StudyRes fromEntity(Study study, boolean isMember, StudyRole role) {
        return new StudyRes(
                study.getId(),
                study.getStudyName(),
                study.getDescription(),
                study.getRegisterDuration(),
                study.getMaxUser(),
                study.getCurrentUser(),
                study.getState(),
                isMember,
                role
        );
    }
}
