package yuquiz.domain.study.dto;

import yuquiz.domain.study.entity.Study;
import yuquiz.domain.study.entity.StudyState;

import java.time.LocalDateTime;

public record StudySummaryRes(
        Long id,
        String name,
        String leaderName,
        int maxUser,
        int curUser,
        LocalDateTime registerDuration,
        StudyState state
) {
    public static StudySummaryRes fromEntity(Study study) {
        return new StudySummaryRes(
                study.getId(),
                study.getStudyName(),
                study.getLeader().getNickname(),
                study.getMaxUser(),
                study.getCurrentUser(),
                study.getRegisterDuration(),
                study.getState());
    }
}
