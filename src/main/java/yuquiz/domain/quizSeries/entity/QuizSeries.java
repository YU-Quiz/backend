package yuquiz.domain.quizSeries.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yuquiz.domain.quiz.entity.Quiz;
import yuquiz.domain.series.entity.Series;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "QuizSeries",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "quizSeries",
                        columnNames = {"series_id", "quiz_id"}
                )
        }
)
public class QuizSeries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private Series series;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Builder
    public QuizSeries(Series series, Quiz quiz) {
        this.series = series;
        this.quiz = quiz;
    }
}
