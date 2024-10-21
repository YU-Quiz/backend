package yuquiz.domain.series.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import yuquiz.domain.series.dto.SeriesReq;
import yuquiz.domain.series.dto.SeriesSortType;
import yuquiz.security.auth.SecurityUserDetails;

@Tag(name = "[문제집 API]", description = "문제집 관련 API")
public interface SeriesApi {

    @Operation(summary = "문제집 생성", description = "문제집을 생성하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "문제집 생성 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "response": "문제집이 생성되었습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "name": "문제집 이름은 필수 입력입니다.",
                                        "studyId": "스터디ID는 1 이상이어야 합니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> createSeries(@RequestBody @Valid SeriesReq seriesReq, @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "문제집 상세 정보 조회", description = "문제집을 상세 정보를 조회하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문제집 상세 정보 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "id": 1,
                                        "name": "새로운 문제집",
                                        "creator": "테스터",
                                        "studyName": null
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> findSeriesById(@PathVariable(value = "seriesId") Long seriesId, @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "문제집 삭제", description = "문제집을 삭제하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "문제집 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> deleteSeriesById(@PathVariable(value = "seriesId") Long seriesId, @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "문제집 상세정보 수정", description = "문제집 상세정보를 수정하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "response": "문제집 수정 성공"
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "name": "문제집 이름은 필수 입력입니다.",
                                        "studyId": "스터디ID는 1 이상이어야 합니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> updateSeriesById(@PathVariable(value = "seriesId") Long seriesId, @RequestBody @Valid SeriesReq seriesReq, @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "문제집 목록 조회", description = "문제집 목록을 조회하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
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
                                                "id": 1,
                                                "name": "새로운 문제집",
                                                "creator": "테스터"
                                            }
                                        ],
                                        "number": 0,
                                        "sort": {
                                            "empty": false,
                                            "sorted": true,
                                            "unsorted": false
                                        },
                                        "numberOfElements": 1,
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
                                        "empty": false
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getSeriesSummary(@RequestParam(value = "keyword", required = false) String keyword,
                                       @RequestParam(value = "sort", defaultValue = "DATE_DESC") SeriesSortType sort,
                                       @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page);
}
