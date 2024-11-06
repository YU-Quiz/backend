package yuquiz.domain.study.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
                                        "totalPages": 1,
                                        "totalElements": 2,
                                        "first": true,
                                        "last": true,
                                        "size": 20,
                                        "content": [
                                            {
                                                "name": "임시 1",
                                                "leaderName": "테스터111",
                                                "maxUser": 10,
                                                "curUser": 1,
                                                "registerDuration": "2024-10-17T16:50:04",
                                                "state": null
                                            },
                                            {
                                                "name": "임시",
                                                "leaderName": "테스터",
                                                "maxUser": 100,
                                                "curUser": 1,
                                                "registerDuration": "2024-10-08T16:50:08",
                                                "state": null
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
                                        "numberOfElements": 2,
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
    ResponseEntity<?> deleteMember(@PathVariable(value = "studyId") Long studyId,
                                   @RequestParam(value = "id") Long deleteUserId,
                                   @AuthenticationPrincipal SecurityUserDetails userDetails);
}
