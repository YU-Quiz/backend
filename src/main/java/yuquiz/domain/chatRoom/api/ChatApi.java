package yuquiz.domain.chatRoom.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Tag(name = "[채팅 API]", description = "채팅 관련 API")
public interface ChatApi {

    @Operation(summary = "일간 채팅 내역 조회", description = "일간 채팅 내역을 조회하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일간 채팅 내역 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    [
                                        {
                                            "roomId": "1",
                                            "sender": "테스터1",
                                            "userId": 1,
                                            "content": "내용1",
                                            "createdAt": "2024-11-05T10:15:30",
                                            "type": "TALK"
                                        },
                                        {
                                            "roomId": "1",
                                            "sender": "테스터2",
                                            "userId": 2,
                                            "content": "내용2",
                                            "createdAt": "2024-11-05T10:15:30",
                                            "type": "TALK"
                                        }
                                    ]
                                    """)
                    }))
    })
    ResponseEntity<?> getDailyMessage(@PathVariable Long roomId);

    @Operation(summary = "날짜별 채팅 내역 조회", description = "날짜별 채팅 내역을 조회하는 API." +
            "2024-10-10 식으로 주면 됩니다. - 사용자가 위로 스크롤하면 자동으로 날짜에 대해 계산하여 보내주는 형식.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "날짜별 채팅 내역 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    [
                                        {
                                            "roomId": "1",
                                            "sender": "테스터1",
                                            "userId": 1,
                                            "content": "내용1",
                                            "createdAt": "2024-11-05T10:15:30",
                                            "type": "TALK"
                                        },
                                        {
                                            "roomId": "1",
                                            "sender": "테스터2",
                                            "userId": 2,
                                            "content": "내용2",
                                            "createdAt": "2024-11-05T10:15:30",
                                            "type": "TALK"
                                        }
                                    ]
                                    """)
                    }))
    })
    ResponseEntity<?> getDateMessage(@PathVariable Long roomId,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);
}
