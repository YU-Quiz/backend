package yuquiz.domain.notification.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import yuquiz.domain.notification.dto.DisplayType;
import yuquiz.domain.notification.dto.NotificationSortType;
import yuquiz.security.auth.SecurityUserDetails;

@Tag(name = "[알림 API]", description = "알림 관련 API")
public interface NotificationApi {

    @Operation(summary = "전체 알림 조회", description = "사용자에 대한 전체 알림을 조회하는 api")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 조회 성공",
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
                                        "id": 134,
                                        "title": "\"아아아앙\"스터디에서 강제 퇴장 당했습니다.",
                                        "message": "\"아아아앙\"스터디에서 강제 퇴장 당했습니다.",
                                        "isChecked": false,
                                        "redirectUrl": "/api/v1/study/16",
                                        "createdAt": "2024-11-20T16:05:32.713949"
                                    },
                                    {
                                        "id": 133,
                                        "title": "\"아아아앙\"스터디에 참가 승인되었습니다.",
                                        "message": "\"아아아앙\"스터디에 참가 승인되었습니다.",
                                        "isChecked": false,
                                        "redirectUrl": "/api/v1/study/16",
                                        "createdAt": "2024-11-20T16:05:21.556135"
                                    }
                                ],
                                "number": 0,
                                "sort": {
                                    "empty": false,
                                    "sorted": true,
                                    "unsorted": false
                                },
                                "pageable": {
                                    "pageNumber": 0,
                                    "pageSize": 20,
                                    "sort": {
                                        "empty": false,
                                        "sorted": true,
                                        "unsorted": false
                                    },
                                    "offset": 0,
                                    "paged": true,
                                    "unpaged": false
                                },
                                "numberOfElements": 2,
                                "empty": false
                            }
                            """)
            }))
    })
    ResponseEntity<?> getAllMyAlert(@AuthenticationPrincipal SecurityUserDetails userDetails,
                                    @RequestParam(value = "page", defaultValue = "0") Integer page,
                                    @RequestParam(value = "sort", defaultValue = "DATE_DESC") NotificationSortType sort,
                                    @RequestParam(value = "view", defaultValue = "UNCHECKED") DisplayType displayType);


    @Operation(summary = "알림 읽음 처리", description = "사용자에 대한 특정 알림을 읽음 처리하는 api")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                            {
                                "response": "성공적으로 처리하였습니다."
                            }
                            """)
                    }))
    })
    ResponseEntity<?> readNotification(@RequestBody Long[] notifications,
                                       @AuthenticationPrincipal SecurityUserDetails userDetails);
}
