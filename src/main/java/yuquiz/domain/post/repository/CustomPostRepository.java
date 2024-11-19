package yuquiz.domain.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import yuquiz.domain.post.dto.PostSortType;
import yuquiz.domain.post.entity.Post;
import yuquiz.domain.studyPost.entity.StudyPostType;

public interface CustomPostRepository {
    Page<Post> getPosts(String keyword, Long categoryId, Pageable pageable, PostSortType sort);

    Page<Post> getPostsByStudy(Long studyId, StudyPostType type, String keyword, Long categoryId, Pageable pageable, PostSortType sort);
}
