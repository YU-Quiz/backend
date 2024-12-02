package yuquiz.domain.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuquiz.common.exception.CustomException;
import yuquiz.domain.chatRoom.entity.ChatRoom;
import yuquiz.domain.chatRoom.exception.ChatRoomExceptionCode;
import yuquiz.domain.chatRoom.repository.ChatRoomRepository;
import yuquiz.domain.notification.dto.NotificationType;
import yuquiz.domain.notification.service.NotificationService;
import yuquiz.domain.post.dto.PostReq;
import yuquiz.domain.post.dto.PostSortType;
import yuquiz.domain.post.dto.PostSummaryRes;
import yuquiz.domain.post.entity.Post;
import yuquiz.domain.post.repository.PostRepository;
import yuquiz.domain.post.service.PostService;
import yuquiz.domain.series.dto.SeriesSortType;
import yuquiz.domain.series.dto.SeriesSummaryRes;
import yuquiz.domain.series.service.SeriesService;
import yuquiz.domain.study.dto.StudyFilter;
import yuquiz.domain.study.dto.StudyReq;
import yuquiz.domain.study.dto.StudyRequestRes;
import yuquiz.domain.study.dto.StudyRes;
import yuquiz.domain.study.dto.StudySortType;
import yuquiz.domain.study.dto.StudySummaryRes;
import yuquiz.domain.study.entity.Study;
import yuquiz.domain.study.exception.StudyExceptionCode;
import yuquiz.domain.study.repository.StudyRepository;
import yuquiz.domain.studyPost.entity.StudyPost;
import yuquiz.domain.studyPost.entity.StudyPostType;
import yuquiz.domain.studyPost.repository.StudyPostRepository;
import yuquiz.domain.studyUser.dto.StudyUserRes;
import yuquiz.domain.studyUser.entity.StudyRole;
import yuquiz.domain.studyUser.entity.StudyUser;
import yuquiz.domain.studyUser.entity.UserState;
import yuquiz.domain.studyUser.repository.StudyUserRepository;
import yuquiz.domain.user.entity.User;
import yuquiz.domain.user.exception.UserExceptionCode;
import yuquiz.domain.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;
    private final StudyUserRepository studyUserRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SeriesService seriesService;
    private final PostService postService;
    private final StudyPostRepository studyPostRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    private final Integer POST_PER_PAGE = 20;

    @Transactional
    public void createStudy(StudyReq studyReq, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_USERID));

        ChatRoom chatRoom = ChatRoom.builder().build();
        Study study = studyReq.toEntity(user, chatRoom);

        StudyUser studyUser = StudyUser.builder()
                .study(study)
                .user(user)
                .chatRoom(chatRoom)
                .role(StudyRole.LEADER)
                .state(UserState.REGISTERED)
                .build();

        studyRepository.save(study);
        chatRoomRepository.save(chatRoom);
        studyUserRepository.save(studyUser);
    }

    @Transactional
    public void deleteStudy(Long studyId, Long userId) {
        if (!validateLeader(studyId, userId)) {
            throw new CustomException(StudyExceptionCode.UNAUTHORIZED_ACTION);
        }

        studyRepository.deleteById(studyId);
    }

    @Transactional
    public void updateStudy(StudyReq studyReq, Long studyId, Long userId) {
        if (!validateLeader(studyId, userId)) {
            throw new CustomException(StudyExceptionCode.UNAUTHORIZED_ACTION);
        }

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(StudyExceptionCode.INVALID_ID));

        study.update(studyReq);
    }

    @Transactional(readOnly = true)
    public Page<StudySummaryRes> getStudies(String keyword, Pageable pageable, StudySortType sort, StudyFilter filter) {

        Page<Study> studies = studyRepository.getStudies(keyword, pageable, sort, filter);

        return studies.map(StudySummaryRes::fromEntity);
    }

    @Transactional(readOnly = true)
    public StudyRes getStudy(Long studyId, Long userId) {

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(StudyExceptionCode.INVALID_ID));

        return studyUserRepository.findStudyUserByStudy_IdAndUser_IdAndState(studyId, userId, UserState.REGISTERED)
                .map(studyUser -> StudyRes.fromEntity(study, true, studyUser.getRole()))
                .orElseGet(() -> StudyRes.fromEntity(study, false, null));
    }

    @Transactional
    public void requestRegister(Long studyId, Long userId) {

        if (studyUserRepository.existsByStudy_IdAndUser_Id(studyId, userId)) {
            throw new CustomException(StudyExceptionCode.ALREADY_REGISTERED);
        }

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(StudyExceptionCode.INVALID_ID));

        if (study.getRegisterDuration().isBefore(LocalDateTime.now())) {
            throw new CustomException(StudyExceptionCode.EXPIRED_SIGNUP_PERIOD);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_USERID));

        StudyUser studyUser = StudyUser.builder()
                .user(user)
                .study(study)
                .state(UserState.PENDING)
                .role(StudyRole.USER)
                .build();

        studyUserRepository.save(studyUser);

        studyNotification(study, study.getLeader(), NotificationType.STUDY_JOIN_REQUEST, "스터디에 새로운 참여 신청이 있습니다.");
    }

    @Transactional(readOnly = true)
    public List<StudyRequestRes> getRegisterRequests(Long studyId, Long userId) {
        if (!validateLeader(studyId, userId)) {
            throw new CustomException(StudyExceptionCode.UNAUTHORIZED_ACTION);
        }

        List<StudyUser> studyUsers = studyUserRepository.findByStudyIdAndState(studyId, UserState.PENDING);

        return studyUsers.stream().map(StudyRequestRes::fromEntity).toList();
    }

    @Transactional
    public void acceptRequest(Long studyId, Long pendingUserId, Long userId) {
        if (!validateLeader(studyId, userId)) {
            throw new CustomException(StudyExceptionCode.UNAUTHORIZED_ACTION);
        }

        StudyUser studyUser = studyUserRepository.findStudyUserByStudy_IdAndUser_IdAndState(studyId, pendingUserId, UserState.PENDING)
                .orElseThrow(() -> new CustomException(StudyExceptionCode.REQUEST_NOT_EXIST));

        ChatRoom chatRoom = chatRoomRepository.findByStudy_Id(studyId)
                .orElseThrow(() -> new CustomException(ChatRoomExceptionCode.INVALID_ID));

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(StudyExceptionCode.INVALID_ID));

        if (study.getMaxUser() <= study.getCurrentUser()) {
            throw new CustomException(StudyExceptionCode.STUDY_FULL);
        }

        study.increaseUser();

        studyUser.accept(chatRoom);

        studyNotification(study, studyUser.getUser(), NotificationType.STUDY_JOIN_ACCEPTED, "스터디에 참가 승인되었습니다.");
    }

    @Transactional(readOnly = true)
    public List<StudyUserRes> getMembers(Long studyId, Long userId) {
        if (!studyUserRepository.existsByStudy_IdAndUser_IdAndState(studyId, userId, UserState.REGISTERED)) {
            throw new CustomException(StudyExceptionCode.UNAUTHORIZED_ACTION);
        }

        List<StudyUser> studyUsers = studyUserRepository.findByStudyIdAndState(studyId, UserState.REGISTERED);

        return studyUsers.stream().map(StudyUserRes::fromEntity).toList();
    }

    @Transactional
    public void deleteUser(Long studyId, Long userId, Long deleteId) {
        if (!validateLeader(studyId, userId)) {
            throw new CustomException(StudyExceptionCode.UNAUTHORIZED_ACTION);
        }

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(StudyExceptionCode.INVALID_ID));


        int result = studyUserRepository.deleteByStudy_IdAndUser_Id(studyId, deleteId);

        if (result != 1) {
            throw new CustomException(StudyExceptionCode.INVALID_USER);
        }

        // todo : 사용자 조회 없이 처리할 방법은?
        User user = userRepository.findById(deleteId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_USERID));

        study.decreaseUser();
        studyNotification(study, user, NotificationType.STUDY_KICKED, "스터디에서 강제 퇴장 당했습니다.");
    }

    @Transactional(readOnly = true)
    public Page<SeriesSummaryRes> getStudySeries(String keyword, Long studyId, Long userId, SeriesSortType sort, Integer page) {
        if (!studyUserRepository.existsByStudy_IdAndUser_IdAndState(studyId, userId, UserState.REGISTERED)) {
            throw new CustomException(StudyExceptionCode.UNAUTHORIZED_ACTION);
        }

        return seriesService.getStudySeriesSummary(keyword, studyId, sort, page);
    }

    @Transactional
    public void createStudyPost(PostReq postReq, Long userId, Long studyId, boolean isNotice) {
      boolean isAuthorized;
      
      if (isNotice) {
          isAuthorized = validateLeader(studyId, userId);
      } else {
          isAuthorized = studyUserRepository.existsByStudy_IdAndUser_IdAndState(studyId, userId, UserState.REGISTERED);
      }
      
      if (!isAuthorized) {
          throw new CustomException(StudyExceptionCode.UNAUTHORIZED_ACTION);
      }

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(StudyExceptionCode.INVALID_ID));

        Post post = postService.createPost(postReq, userId);

        StudyPost studyPost = StudyPost.builder()
                .study(study)
                .post(post)
                .type(isNotice ? StudyPostType.NOTICE : StudyPostType.NORMAL)
                .build();

        studyPostRepository.save(studyPost);
    }

    @Transactional(readOnly = true)
    public Page<PostSummaryRes> getStudyPosts(Long studyId, Long userId, StudyPostType type, String keyword, PostSortType sort, Integer page) {
        if (!studyUserRepository.existsByStudy_IdAndUser_IdAndState(studyId, userId, UserState.REGISTERED)) {
            throw new CustomException(StudyExceptionCode.UNAUTHORIZED_ACTION);
        }

        Pageable pageable = PageRequest.of(page, POST_PER_PAGE);

        Page<Post> posts = postRepository.getPostsByStudy(studyId, type, keyword,3L, pageable, sort);

        return posts.map(PostSummaryRes::fromEntity);
    }

    private boolean validateLeader(Long studyId, Long userId) {
        return studyRepository.findLeaderById(studyId)
                .map(leaderId -> leaderId.equals(userId))
                .orElse(false);
    }

    public void studyNotification(Study study, User user, NotificationType type, String content) {

        String message = "\"" + study.getStudyName() + "\"" + content;
        String url = "/study/" + study.getId();

        notificationService.send(user, type, message, url);
    }
}
