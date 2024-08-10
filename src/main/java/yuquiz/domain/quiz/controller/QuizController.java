package yuquiz.domain.quiz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yuquiz.common.api.SuccessRes;
import yuquiz.domain.quiz.dto.AnswerReq;
import yuquiz.domain.quiz.dto.QuizReq;
import yuquiz.domain.quiz.service.QuizService;
import yuquiz.security.auth.SecurityUserDetails;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<?> createQuiz(@Valid  @RequestBody QuizReq quizReq, @AuthenticationPrincipal SecurityUserDetails userDetails) {
        quizService.createQuiz(quizReq, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessRes.from("퀴즈 생성 성공."));
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable(value = "quizId") Long quizId, @AuthenticationPrincipal SecurityUserDetails userDetails) {
        quizService.deleteQuiz(quizId, userDetails.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{quizId}")
    public ResponseEntity<?> updateQuiz(
            @PathVariable(value = "quizId") Long quizId,
            @Valid @RequestBody QuizReq quizReq,
            @AuthenticationPrincipal SecurityUserDetails userDetails) {
        quizService.updateQuiz(quizId, quizReq, userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(SuccessRes.from("퀴즈 수정 성공."));
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<?> getQuizById(@PathVariable(value = "quizId") Long quizId){
        return ResponseEntity.status(HttpStatus.OK).body(quizService.getQuizById(quizId));
    }

    @PostMapping("/{quizId}/grade")
    public ResponseEntity<?> gradeQuiz(
            @PathVariable(value = "quizId") Long quizId,
            @Valid @RequestBody AnswerReq answerReq) {
        return ResponseEntity.status(HttpStatus.OK).body(quizService.gradeQuiz(quizId, answerReq.answer()));
    }

    @GetMapping("/{quizId}/answer")
    public ResponseEntity<?> getAnswer(@PathVariable(value = "quizId") Long quizId) {
        return ResponseEntity.status(HttpStatus.OK).body(quizService.getAnswer(quizId));
    }
}