package yuquiz.domain.studyPost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuquiz.domain.studyPost.entity.StudyPost;

public interface StudyPostRepository extends JpaRepository<StudyPost, Long> {

}
