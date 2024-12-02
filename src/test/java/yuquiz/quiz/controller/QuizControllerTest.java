package yuquiz.quiz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import yuquiz.domain.quiz.controller.QuizController;
import yuquiz.domain.quiz.dto.quiz.QuizReq;
import yuquiz.domain.quiz.entity.QuizType;
import yuquiz.domain.quiz.service.QuizService;
import yuquiz.domain.user.entity.Role;
import yuquiz.domain.user.entity.User;
import yuquiz.security.auth.SecurityUserDetails;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizController.class)
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuizService quizService;

    private SecurityUserDetails userDetails;
    private User user;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(get("/**").with(csrf()))
                .defaultRequest(put("/**").with(csrf()))
                .defaultRequest(delete("/**").with(csrf()))
                .defaultRequest(patch("/**").with(csrf()))
                .build();

        this.user = User.builder()
                .username("test")
                .password("password")
                .nickname("테스터")
                .email("test@gmail.com")
                .role(Role.USER)
                .build();

        this.userDetails = new SecurityUserDetails(user);
    }

    @Test
    @DisplayName("퀴즈 생성 테스트")
    void createQuizTest() throws Exception {
        //given
        QuizReq quizReq = new QuizReq("test", "test", List.of("testImgURL"), "2", QuizType.MULTIPLE_CHOICE, List.of("1", "2", "3"), 2L);

//        doNothing().when(quizService).createQuiz(any(QuizReq.class), eq(userDetails.getId()));

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/quizzes")
                        .with(user(userDetails))
                        .content(objectMapper.writeValueAsBytes(quizReq))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response").value("퀴즈 생성 성공."));
    }

    @Test
    @DisplayName("퀴즈 생성 테스트 - 유효성 검사 실패")
    void createQuizInvalidFailedTest() throws Exception {
        //given
        QuizReq quizReq = new QuizReq(null, null, null, null, null, null, null);

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/quizzes")
                        .with(user(userDetails))
                        .content(objectMapper.writeValueAsBytes(quizReq))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("제목은 필수 입력입니다."))
                .andExpect(jsonPath("$.question").value("질문은 필수 입력입니다."))
                .andExpect(jsonPath("$.answer").value("정답은 필수 입력입니다."))
                .andExpect(jsonPath("$.quizType").value("퀴즈 유형은 필수 입력입니다."))
                .andExpect(jsonPath("$.subjectId").value("과목은 필수 입력입니다."));
    }
}