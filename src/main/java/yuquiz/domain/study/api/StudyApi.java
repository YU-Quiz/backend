package yuquiz.domain.study.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import yuquiz.domain.post.dto.PostReq;
import yuquiz.domain.post.dto.PostSortType;
import yuquiz.domain.series.dto.SeriesSortType;
import yuquiz.domain.study.dto.StudyFilter;
import yuquiz.domain.study.dto.StudyReq;
import yuquiz.domain.study.dto.StudySortType;
import yuquiz.security.auth.SecurityUserDetails;

@Tag(name = "[스터디 API]", description = "스터디 관련 API")
public interface StudyApi {
    @Operation(summary = "스터디 생성", description = "스터디 생성 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스터디 생성 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "response": "성공적으로 생성되었습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "필수 입력 사항 누락", value = """
                                    {
                                        "name": "제목은 필수 입력입니다.",
                                        "description": "설명은 필수 입력입니다.",
                                        "maxUser": "최대 인원은 필수 입력입니다."
                                    }
                                    """),
                            @ExampleObject(name = "최대 인원 최소 2명", value = """
                                    {
                                        "maxUser": "최소 인원은 2명입니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> createStudy(@Valid @RequestBody StudyReq studyReq,
                                  @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "스터디 삭제", description = "스터디 삭제 API")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "스터디 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "스터디장이 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> deleteStudy(@PathVariable(value = "studyId") Long studyId,
                                  @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "스터디 수정", description = "스터디 수정 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스터디 수정 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "name": "제목은 필수 입력입니다.",
                                        "description": "설명은 필수 입력입니다.",
                                        "maxUser": "최대 인원은 필수 입력입니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "스터디 장이 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> updateStudy(@PathVariable(value = "studyId") Long studyId,
                                  @Valid @RequestBody StudyReq studyReq,
                                  @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "스터디 목록 조회", description = "스터디 목록 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스터디 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        {
                                         "totalPages": 1,
                                         "totalElements": 1,
                                         "first": true,
                                         "last": true,
                                         "size": 20,
                                         "content": [
                                             {
                                                 "id": 17,
                                                 "name": "내 스터디 어디갔노",
                                                 "leaderName": "dryice",
                                                 "maxUser": 10,
                                                 "curUser": 2,
                                                 "registerDuration": "2024-11-23T16:08:00",
                                                 "state": "ACTIVE"
                                             }
                                         ],
                                         "number": 0,
                                         "sort": {
                                             "empty": true,
                                             "unsorted": true,
                                             "sorted": false
                                         },
                                         "pageable": {
                                             "pageNumber": 0,
                                             "pageSize": 20,
                                             "sort": {
                                                 "empty": true,
                                                 "unsorted": true,
                                                 "sorted": false
                                             },
                                             "offset": 0,
                                             "unpaged": false,
                                             "paged": true
                                         },
                                         "numberOfElements": 1,
                                         "empty": false
                                     }
                                    """)
                    }))
    })
    ResponseEntity<?> getStudies(@RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "sort", defaultValue = "CREATED_DESC") StudySortType sort,
                                 @RequestParam(value = "filter", defaultValue = "ONGOING") StudyFilter filter,
                                 @PageableDefault(size = 20) Pageable pageable);

    @Operation(summary = "스터디 조회", description = "스터디 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스터디 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "id": 5,
                                        "Name": "코테라고",
                                        "description": "코테 스터디입니다?",
                                        "registerDuration": "2024-10-08T16:50:08",
                                        "maxUser": 5,
                                        "curUser": 1,
                                        "state": "ACTIVE",
                                        "isMember": true,
                                        "role": "LEADER"
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 스터디",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "존재하지 않는 스터디입니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getStudy(@PathVariable(value = "studyId") Long studyId,
                               @AuthenticationPrincipal SecurityUserDetails userDetails);


    @Operation(summary = "스터디 가입 신청", description = "스터디 가입 신청 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "가입 신청 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "response": "성공적으로 신청되었습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "이미 가입된 스터디",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 409,
                                        "message": "이미 가입되었습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 스터디",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "존재하지 않는 스터디입니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> requestRegister(@PathVariable(value = "studyId") Long studyId,
                                      @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "스터디 가입 신청 목록", description = "스터디 가입 신청 목록 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스터디 가입 신청 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    [
                                        {
                                            "userId": 48,
                                            "name": "test11111",
                                            "requestAt": "2024-10-14T19:49:42.910382"
                                        }
                                    ]
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "스터디 장이 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 스터디",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "존재하지 않는 스터디입니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getRequests(@PathVariable(value = "studyId") Long studyId,
                                  @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "스터디 가입 수락", description = "스터디 가입 수락 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가입 수락 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "response": "성공적으로 승인되었습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 가입 신청",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "존재하지 않는 가입 신청입니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "스터디 장이 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 스터디",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "존재하지 않는 스터디입니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "스터디 최대 인원수 초과",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 409,
                                        "message": "스터디의 최대 인원수를 초과하였습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> acceptRequest(@PathVariable(value = "studyId") Long studyId,
                                    @RequestParam(value = "id") Long pendingUserId,
                                    @AuthenticationPrincipal SecurityUserDetails userDetails);


    @Operation(summary = "스터디원 목록 조회", description = "스터티원 목록 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    [
                                        {
                                            "userId": 1,
                                            "username": "test",
                                            "role": "LEADER",
                                            "joinedAt": "2024-10-09T17:46:07.775782"
                                        }
                                    ]
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "스터디원이 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 스터디",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "존재하지 않는 스터디입니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getMembers(@PathVariable(value = "studyId") Long studyId,
                                 @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "스터디원 삭제", description = "스터디원 삭제 API")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "퀴즈 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "스터디장이 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                         "status": 404,
                                         "message": "존재하지 않는 사용자입니다."
                                     }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "스터디 최대 인원수 초과",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 409,
                                        "message": "스터디의 최대 인원수를 초과하였습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> deleteMember(@PathVariable(value = "studyId") Long studyId,
                                   @RequestParam(value = "id") Long deleteUserId,
                                   @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "스터디 문제집 목록 조회", description = "스터디 문제집 목록 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스터디 문제집 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                                {
                                                    "totalPages": 1,
                                                    "totalElements": 2,
                                                    "first": true,
                                                    "last": true,
                                                    "size": 20,
                                                    "content": [
                                                        {
                                                            "id": 22,
                                                            "name": "스터디 문제집2",
                                                            "creator": "어드민"
                                                        },
                                                        {
                                                            "id": 21,
                                                            "name": "스터디 문제집1",
                                                            "creator": "어드민"
                                                        }
                                                    ],
                                                    "number": 0,
                                                    "sort": {
                                                        "empty": false,
                                                        "unsorted": false,
                                                        "sorted": true
                                                    },
                                                    "pageable": {
                                                        "pageNumber": 0,
                                                        "pageSize": 20,
                                                        "sort": {
                                                            "empty": false,
                                                            "unsorted": false,
                                                            "sorted": true
                                                        },
                                                        "offset": 0,
                                                        "unpaged": false,
                                                        "paged": true
                                                    },
                                                    "numberOfElements": 2,
                                                    "empty": false
                                                }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "스터디원이 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 스터디",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "존재하지 않는 스터디입니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getStudySeries(@PathVariable(value = "studyId") Long studyId,
                                     @RequestParam(value = "sort", defaultValue = "DATE_DESC") SeriesSortType sort,
                                     @RequestParam(value = "page", defaultValue = "0") Integer page,
                                     @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                     @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "스터디 공지 작성", description = "스터디 공지 작성 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "스터디 공지 작성 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "response": "성공적으로 생성되었습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "스터디장이 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 스터디",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "존재하지 않는 스터디입니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> createStudyNotice(@PathVariable(value = "studyId") Long studyId,
                                        @Valid @RequestBody PostReq postReq,
                                        @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "스터디 게시글 생성", description = "스터디 게시글 생성 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "스터디 게시글 생성 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "response": "성공적으로 생성되었습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "스터디원이 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 스터디",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "존재하지 않는 스터디입니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> createStudyPost(@PathVariable(value = "studyId") Long studyId,
                                      @Valid @RequestBody PostReq postReq,
                                      @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "스터디 공지 목록 조회", description = "스터디 공지 목록 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스터디 공지 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "totalPages": 1,
                                        "totalElements": 1,
                                        "first": true,
                                        "last": true,
                                        "size": 20,
                                        "content": [
                                            {
                                                "postId": 41,
                                                "postTitle": "공지 테스트123",
                                                "nickname": "어드민",
                                                "categoryName": "스터디",
                                                "createdAt": "2024-11-19T18:26:22.185715",
                                                "likeCount": 0,
                                                "viewCount": 0
                                            }
                                        ],
                                        "number": 0,
                                        "sort": {
                                            "empty": true,
                                            "unsorted": true,
                                            "sorted": false
                                        },
                                        "pageable": {
                                            "pageNumber": 0,
                                            "pageSize": 20,
                                            "sort": {
                                                "empty": true,
                                                "unsorted": true,
                                                "sorted": false
                                            },
                                            "offset": 0,
                                            "unpaged": false,
                                            "paged": true
                                        },
                                        "numberOfElements": 1,
                                        "empty": false
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "스터디원이 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 스터디",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "존재하지 않는 스터디입니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getStudyNotices(@PathVariable(value = "studyId") Long studyId,
                                      @RequestParam(value = "keyword", required = false) String keyword,
                                      @RequestParam(value = "sort", defaultValue = "DATE_DESC") PostSortType sort,
                                      @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                      @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "스터디 게시글 목록 조회", description = "스터디 게시글 목록 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스더티 게시글 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "totalPages": 1,
                                        "totalElements": 1,
                                        "first": true,
                                        "last": true,
                                        "size": 20,
                                        "content": [
                                            {
                                                "postId": 42,
                                                "postTitle": "스터디 게시글 테스트",
                                                "nickname": "어드민",
                                                "categoryName": "스터디",
                                                "createdAt": "2024-11-19T18:26:31.51824",
                                                "likeCount": 0,
                                                "viewCount": 0
                                            }
                                        ],
                                        "number": 0,
                                        "sort": {
                                            "empty": true,
                                            "sorted": false,
                                            "unsorted": true
                                        },
                                        "numberOfElements": 1,
                                        "pageable": {
                                            "pageNumber": 0,
                                            "pageSize": 20,
                                            "sort": {
                                                "empty": true,
                                                "sorted": false,
                                                "unsorted": true
                                            },
                                            "offset": 0,
                                            "paged": true,
                                            "unpaged": false
                                        },
                                        "empty": false
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "스터디원이 아님",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 스터디",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "존재하지 않는 스터디입니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getStudyPosts(@PathVariable(value = "studyId") Long studyId,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "sort", defaultValue = "DATE_DESC") PostSortType sort,
                                    @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                    @AuthenticationPrincipal SecurityUserDetails userDetails);
}
