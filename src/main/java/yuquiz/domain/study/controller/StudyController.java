package yuquiz.domain.study.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yuquiz.common.api.SuccessRes;
import yuquiz.domain.post.dto.PostReq;
import yuquiz.domain.series.dto.SeriesSortType;
import yuquiz.domain.study.api.StudyApi;
import yuquiz.domain.study.dto.StudyFilter;
import yuquiz.domain.study.dto.StudyReq;
import yuquiz.domain.study.dto.StudySortType;
import yuquiz.domain.study.dto.StudySummaryRes;
import yuquiz.domain.study.service.StudyService;
import yuquiz.security.auth.SecurityUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/study")
public class StudyController implements StudyApi {
    private final StudyService studyService;

    @Override
    @PostMapping
    public ResponseEntity<?> createStudy(@Valid @RequestBody StudyReq studyReq,
                                         @AuthenticationPrincipal SecurityUserDetails userDetails) {

        studyService.createStudy(studyReq, userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessRes.from("성공적으로 생성되었습니다."));
    }

    @Override
    @DeleteMapping("/{studyId}")
    public ResponseEntity<?> deleteStudy(@PathVariable(value = "studyId") Long studyId,
                                         @AuthenticationPrincipal SecurityUserDetails userDetails) {

        studyService.deleteStudy(studyId, userDetails.getId());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{studyId}")
    public ResponseEntity<?> updateStudy(@PathVariable(value = "studyId") Long studyId,
                                         @Valid @RequestBody StudyReq studyReq,
                                         @AuthenticationPrincipal SecurityUserDetails userDetails) {
        studyService.updateStudy(studyReq, studyId, userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(SuccessRes.from("성공적으로 수정되었습니다."));
    }

    @GetMapping
    public ResponseEntity<?> getStudies(@RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "sort", defaultValue = "CREATED_DESC") StudySortType sort,
                                        @RequestParam(value = "filter", defaultValue = "ONGOING") StudyFilter filter,
                                        @PageableDefault(size = 20) Pageable pageable) {

        Page<StudySummaryRes> studies = studyService.getStudies(keyword, pageable, sort, filter);

        return ResponseEntity.status(HttpStatus.OK).body(studies);
    }

    @GetMapping("/{studyId}")
    public ResponseEntity<?> getStudy(@PathVariable(value = "studyId") Long studyId,
                                      @AuthenticationPrincipal SecurityUserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.OK).body(studyService.getStudy(studyId, userDetails.getId()));
    }

    @PostMapping("/{studyId}/request")
    public ResponseEntity<?> requestRegister(@PathVariable(value = "studyId") Long studyId,
                                             @AuthenticationPrincipal SecurityUserDetails userDetails) {

        studyService.requestRegister(studyId, userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessRes.from("성공적으로 신청되었습니다."));
    }

    @GetMapping("/{studyId}/request")
    public ResponseEntity<?> getRequests(@PathVariable(value = "studyId") Long studyId,
                                         @AuthenticationPrincipal SecurityUserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.OK).body(studyService.getRegisterRequests(studyId, userDetails.getId()));
    }

    @PostMapping("/{studyId}/accept")
    public ResponseEntity<?> acceptRequest(@PathVariable(value = "studyId") Long studyId,
                                           @RequestParam(value = "id") Long pendingUserId,
                                           @AuthenticationPrincipal SecurityUserDetails userDetails) {

        studyService.acceptRequest(studyId, pendingUserId, userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(SuccessRes.from("성공적으로 승인되었습니다."));
    }

    @GetMapping("/{studyId}/member")
    public ResponseEntity<?> getMembers(@PathVariable(value = "studyId") Long studyId,
                                        @AuthenticationPrincipal SecurityUserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.OK).body(studyService.getMembers(studyId, userDetails.getId()));
    }

    @DeleteMapping("/{studyId}/member")
    public ResponseEntity<?> deleteMember(@PathVariable(value = "studyId") Long studyId,
                                          @RequestParam(value = "id") Long deleteUserId,
                                          @AuthenticationPrincipal SecurityUserDetails userDetails) {

        studyService.deleteUser(studyId, userDetails.getId(), deleteUserId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{studyId}/series")
    public ResponseEntity<?> getStudySeries(@PathVariable(value = "studyId") Long studyId,
                                            @RequestParam(value = "sort", defaultValue = "DATE_DESC") SeriesSortType sort,
                                            @RequestParam(value = "page", defaultValue = "0") Integer page,
                                            @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                            @AuthenticationPrincipal SecurityUserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.OK).body(studyService.getStudySeries(keyword, studyId, userDetails.getId(), sort, page));
    }

    @PostMapping("/{studyId}/notice")
    public ResponseEntity<?> createStudyNotice(@PathVariable(value = "studyId") Long studyId,
                                               @RequestBody PostReq postReq,
                                               @AuthenticationPrincipal SecurityUserDetails userDetails) {

        studyService.createStudyPost(postReq, userDetails.getId(), studyId, true);

        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessRes.from("성공적으로 생성되었습니다."));
    }

    @PostMapping("/{studyId}/post")
    public ResponseEntity<?> createStudyPost(@PathVariable(value = "studyId") Long studyId,
                                               @RequestBody PostReq postReq,
                                               @AuthenticationPrincipal SecurityUserDetails userDetails) {

        studyService.createStudyPost(postReq, userDetails.getId(), studyId, false);

        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessRes.from("성공적으로 생성되었습니다."));
    }
}
