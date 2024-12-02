package yuquiz.domain.studyUser.dto;

import yuquiz.domain.studyUser.entity.StudyRole;
import yuquiz.domain.studyUser.entity.StudyUser;

import java.time.LocalDateTime;

public record StudyUserRes (
        Long userId,
        String nickname,
        String username,
        StudyRole role,
        LocalDateTime joinedAt
){
    public static StudyUserRes fromEntity(StudyUser studyUser) {
        return new StudyUserRes(
                studyUser.getUser().getId(),
                studyUser.getUser().getNickname(),
                studyUser.getUser().getUsername(),
                studyUser.getRole(),
                studyUser.getJoinedAt()
        );
    }
}
