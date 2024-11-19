package yuquiz.domain.study.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yuquiz.common.entity.BaseTimeEntity;
import yuquiz.domain.chatRoom.entity.ChatRoom;
import yuquiz.domain.study.dto.StudyReq;
import yuquiz.domain.studyPost.entity.StudyPost;
import yuquiz.domain.studyUser.entity.StudyUser;
import yuquiz.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Study extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "study_name")
    private String studyName;

    @Column(name = "description")
    private String description;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE)
    private List<StudyUser> studyUsers = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.REMOVE)
    private List<StudyPost> studyPosts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private User leader;

    @Column(name = "register_duration")
    private LocalDateTime registerDuration;

    @Column(name = "max_user")
    private Integer maxUser;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private StudyState state;

    @Column(name = "cur_user")
    private Integer currentUser;

    @Builder
    public Study(String studyName, String description, ChatRoom chatRoom, Integer maxUser, LocalDateTime registerDuration, User leader) {
        this.studyName = studyName;
        this.description = description;
        this.chatRoom = chatRoom;
        this.maxUser = maxUser;
        this.registerDuration = registerDuration;
        this.leader = leader;
        this.state = StudyState.ACTIVE;
        this.currentUser = 1;
    }

    public void update(StudyReq studyReq) {
        this.studyName = studyReq.name();
        this.description = studyReq.description();
        this.maxUser = studyReq.maxUser();
        this.registerDuration = studyReq.registerDuration();
        this.state = studyReq.state();
    }

    public void increaseUser() {
        this.currentUser += 1;
    }

    public void decreaseUser() {
        this.currentUser -= 1;
    }
}
