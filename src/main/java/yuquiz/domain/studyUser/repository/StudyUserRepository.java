package yuquiz.domain.studyUser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuquiz.domain.studyUser.entity.StudyUser;
import yuquiz.domain.studyUser.entity.UserState;

import java.util.List;
import java.util.Optional;

public interface StudyUserRepository extends JpaRepository<StudyUser, Long> {
    boolean existsByStudy_IdAndUser_Id(Long studyId, Long userId);

    boolean existsByChatRoom_IdAndUser_Id(Long roomId, Long userId);

    Optional<StudyUser> findStudyUserByStudy_IdAndUser_IdAndState(Long studyId, Long userId, UserState state);

    List<StudyUser> findByStudyIdAndState(Long studyId, UserState state);

    void deleteByStudy_IdAndUser_Id(Long studyId, Long userId);
}
