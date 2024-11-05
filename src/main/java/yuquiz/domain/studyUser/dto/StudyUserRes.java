package yuquiz.domain.studyUser.dto;

import yuquiz.domain.studyUser.entity.StudyRole;
import yuquiz.domain.studyUser.entity.StudyUser;

import java.time.LocalDateTime;

public record StudyUserRes (
        Long userId,
        String username,
        StudyRole role,
        LocalDateTime joinedAt
){
    public static StudyUserRes fromEntity(StudyUser studyUser) {
        return new StudyUserRes(
                studyUser.getId(),
                studyUser.getUser().getUsername(),
                studyUser.getRole(),
                studyUser.getJoinedAt()
        );
    }
}
