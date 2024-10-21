package yuquiz.domain.quizSeries.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import yuquiz.security.auth.SecurityUserDetails;

@Tag(name = "[문제집내 퀴즈 API]", description = "문제집내 퀴즈 관련 API")
public interface QuizSeriesApi {
    @Operation(summary = "문제집에 퀴즈 추가", description = "문제집에 퀴즈를 추가하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "퀴즈 추가 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "response": "문제집에 문제 추가 성공"
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
                    })),
            @ApiResponse(responseCode = "404", description = "중복된 퀴즈",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "이미 문제집에 추가된 문제입니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> addQuizToSeries(@PathVariable(value = "seriesId") Long seriesId,
                                      @PathVariable(value = "quizId") Long quizId,
                                      @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "문제집에서 퀴즈 삭제", description = "문제집에서 퀴즈를 삭제하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "문제집에서 퀴즈 삭제 성공"),
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
    ResponseEntity<?> deleteQuizFromSeries(@PathVariable(value = "seriesId") Long seriesId,
                                           @PathVariable(value = "quizId") Long quizId,
                                           @AuthenticationPrincipal SecurityUserDetails userDetails);
}
