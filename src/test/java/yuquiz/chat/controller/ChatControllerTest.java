package yuquiz.chat.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import yuquiz.domain.chatRoom.controller.ChatController;
import yuquiz.domain.chatRoom.dto.Message;
import yuquiz.domain.chatRoom.dto.MessageType;
import yuquiz.domain.chatRoom.service.ChatMessageService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatMessageService chatMessageService;

    private Long roomId;
    private List<Message> messages;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        this.roomId = 1L;
        createMessage();
    }

    void createMessage() {
        messages = new ArrayList<>();

        Message message1 =
                new Message("1", "테스터1", 1L, "내용1", "2024-11-05 12:00:00", MessageType.TALK);
        Message message2 =
                new Message("1", "테스터2", 2L, "내용2", "2024-11-05 12:00:00", MessageType.TALK);


        messages.add(message1);
        messages.add(message2);
    }

    @Test
    @DisplayName("일간 채팅 메시지 불러오기 테스트")
    void getDailyMessageTest() throws Exception {
        // given
        given(chatMessageService.fetchMessagesFromRedis(roomId)).willReturn(messages);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/chat/{roomId}/messages/daily", roomId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomId").value("1"))
                .andExpect(jsonPath("$[0].sender").value("테스터1"))
                .andExpect(jsonPath("$[0].content").value("내용1"))
                .andExpect(jsonPath("$[1].roomId").value("1"))
                .andExpect(jsonPath("$[1].sender").value("테스터2"))
                .andExpect(jsonPath("$[1].content").value("내용2"));
    }

    @Test
    @DisplayName("특정 날짜의 채팅 메시지 불러오기 테스트")
    void getDateMessageTest() throws Exception {
        // given
        LocalDate date = LocalDate.of(2024, 11, 5); // 테스트할 날짜를 설정합니다.

        List<Message> messages = List.of(
                new Message("1", "테스터1", 1L, "내용1", "2024-11-05 12:00:00", MessageType.TALK),
                new Message("1", "테스터2", 2L, "내용2", "2024-11-05 12:00:00", MessageType.TALK)
        );

        given(chatMessageService.fetchMessagesByDateAndRoomId(roomId, date)).willReturn(messages);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/chat/{roomId}/messages", roomId)
                        .param("date", date.toString()) // 요청 파라미터로 날짜 전달
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sender").value("테스터1"))
                .andExpect(jsonPath("$[0].content").value("내용1"))
                .andExpect(jsonPath("$[1].sender").value("테스터2"))
                .andExpect(jsonPath("$[1].content").value("내용2"));
    }

}
