package yuquiz.domain.subject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yuquiz.domain.major.entity.Major;
import yuquiz.domain.quiz.entity.Quiz;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "subject_name")
    private String subjectName;

    @NotNull
    @Column(name = "subject_code", unique = true)
    private String subjectCode;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.REMOVE)
    private List<Quiz> quizzes = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    @Builder
    public Subject(String subjectName, String subjectCode, Major major) {
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.major = major;
    }
}
