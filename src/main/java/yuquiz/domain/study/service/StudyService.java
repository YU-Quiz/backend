package yuquiz.domain.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuquiz.common.exception.CustomException;
import yuquiz.domain.chatRoom.entity.ChatRoom;
import yuquiz.domain.chatRoom.exception.ChatRoomExceptionCode;
import yuquiz.domain.chatRoom.repository.ChatRoomRepository;
import yuquiz.domain.post.dto.PostReq;
import yuquiz.domain.post.entity.Post;
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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_USERID));

        StudyUser studyUser = StudyUser.builder()
                .user(user)
                .study(study)
                .state(UserState.PENDING)
                .role(StudyRole.USER)
                .build();

        studyUserRepository.save(studyUser);
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

        studyUser.accept(chatRoom);
    }

    @Transactional(readOnly = true)
    public List<StudyUserRes> getMembers(Long studyId, Long userId) {
        if (!studyUserRepository.existsByStudy_IdAndUser_Id(studyId, userId)) {
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

        studyUserRepository.deleteByStudy_IdAndUser_Id(studyId, deleteId);
    }

    @Transactional(readOnly = true)
    public Page<SeriesSummaryRes> getStudySeries(String keyword, Long studyId, Long userId, SeriesSortType sort, Integer page) {
        if (!studyUserRepository.existsByStudy_IdAndUser_Id(studyId, userId)) {
            throw new CustomException(StudyExceptionCode.UNAUTHORIZED_ACTION);
        }

        return seriesService.getStudySeriesSummary(keyword, studyId, sort, page);
    }

    @Transactional
    public void createStudyNotice(PostReq postReq, Long userId, Long studyId) {
        if (!validateLeader(studyId, userId)) {
            throw new CustomException(StudyExceptionCode.UNAUTHORIZED_ACTION);
        }

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new CustomException(StudyExceptionCode.INVALID_ID));

        Post post = postService.createPost(postReq, userId);

        StudyPost studyPost = StudyPost.builder()
                .study(study)
                .post(post)
                .type(StudyPostType.NOTICE)
                .build();

        studyPostRepository.save(studyPost);
    }

    private boolean validateLeader(Long studyId, Long userId) {
        return studyRepository.findLeaderById(studyId)
                .map(leaderId -> leaderId.equals(userId))
                .orElse(false);
    }
}
