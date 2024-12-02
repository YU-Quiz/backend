package yuquiz.domain.quiz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yuquiz.common.entity.BaseTimeEntity;
import yuquiz.domain.like.entity.LikedQuiz;
import yuquiz.domain.quiz.converter.StringListConverter;
import yuquiz.domain.quiz.dto.quiz.QuizReq;
import yuquiz.domain.quizSeries.entity.QuizSeries;
import yuquiz.domain.report.entity.Report;
import yuquiz.domain.subject.entity.Subject;
import yuquiz.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Quiz extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String question;

    @Convert(converter = StringListConverter.class)
    private List<String> choices;

    @Convert(converter = StringListConverter.class)
    @Column(name = "quiz_imgs")
    private List<String> quizImgs;

    @NotNull
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(name = "quiz_type")
    private QuizType quizType;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "view_count")
    private int viewCount;

    @Column(columnDefinition = "boolean default true")
    private boolean visibility = true;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.REMOVE)
    private List<TriedQuiz> triedQuizzes = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.REMOVE)
    private List<PinnedQuiz> pinnedQuizzes = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.REMOVE)
    private List<LikedQuiz> likedQuizs = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.REMOVE)
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.REMOVE)
    private List<QuizSeries> series = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Builder
    public Quiz(String title, String question, List<String> choices,
                List<String> quizImgs, String answer, QuizType quizType, User writer, Subject subject) {

        this.title = title;
        this.question = question;
        this.choices = choices;
        this.quizImgs = quizImgs;
        this.answer = answer;
        this.quizType = quizType;
        this.writer = writer;
        this.subject = subject;
        this.likeCount = 0;
        this.viewCount = 0;
    }

    public void increaseViewCount() {
        this.viewCount += 1;
    }

    public void decreaseLikeCount() {
        this.likeCount -= 1;
    }

    public void increaseLikeCount() {
        this.likeCount += 1;
    }

    public void changeVisibility() {
        if (this.visibility) {
            this.visibility = false;
        }
    }

    public void update(QuizReq quizReq, Subject subject) {
        this.title = quizReq.title();
        this.question = quizReq.question();
        this.answer = quizReq.answer();
        this.choices = quizReq.choices();
        this.quizImgs = quizReq.quizImg();
        this.quizType = quizReq.quizType();
        this.subject = subject;
    }

    public void uploadImage(String url) {
        this.quizImgs.add(url);
    }
}
