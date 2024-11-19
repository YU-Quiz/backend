package yuquiz.domain.post.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yuquiz.common.entity.BaseTimeEntity;
import yuquiz.domain.category.entity.Category;
import yuquiz.domain.comment.entity.Comment;
import yuquiz.domain.post.dto.PostReq;
import yuquiz.domain.studyPost.entity.StudyPost;
import yuquiz.domain.studyUser.entity.StudyUser;
import yuquiz.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String content;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "view_count")
    private int viewCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<StudyPost> studyPosts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @Builder
    public Post(String title, String content, Category category, User writer) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.writer = writer;
        this.likeCount = 0;
        this.viewCount = 0;
    }

    public int decreaseLikeCount() {
        return --this.likeCount;
    }

    public int increaseLikeCount() {
        return ++this.likeCount;
    }

    public int increaseViewCount() {
        return ++this.viewCount;
    }

    public void update(PostReq postReq, Category category) {
        this.title = postReq.title();
        this.content = postReq.content();
        this.category = category;
    }
}
