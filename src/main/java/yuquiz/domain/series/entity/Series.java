package yuquiz.domain.series.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yuquiz.common.entity.BaseTimeEntity;
import yuquiz.domain.quizSeries.entity.QuizSeries;
import yuquiz.domain.series.dto.SeriesReq;
import yuquiz.domain.study.entity.Study;
import yuquiz.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Series extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @OneToMany(mappedBy = "series", cascade = CascadeType.REMOVE)
    private List<QuizSeries> quizSeries = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @Builder
    public Series(String name, User creator, Study study) {
        this.name = name;
        this.creator = creator;
        this.study = study;
    }

    public void update(SeriesReq seriesReq, Study study) {
        this.name = seriesReq.name();
        this.study = study;
    }
}