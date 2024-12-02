package yuquiz.domain.quiz.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yuquiz.common.api.SuccessRes;
import yuquiz.domain.quiz.api.QuizApi;
import yuquiz.domain.quiz.dto.quiz.*;
import yuquiz.domain.quiz.service.QuizService;
import yuquiz.security.auth.SecurityUserDetails;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
public class QuizController implements QuizApi {

    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<?> createQuiz(@Valid @RequestBody QuizReq quizReq,
                                        @RequestPart(value = "image") MultipartFile image,
                                        @AuthenticationPrincipal SecurityUserDetails userDetails) {

        quizService.createQuiz(quizReq, userDetails.getId(), image);

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
    public ResponseEntity<?> getQuizById(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @PathVariable(value = "quizId") Long quizId) {

        return ResponseEntity.status(HttpStatus.OK).body(quizService.getQuizById(userDetails.getId(), quizId));
    }

    @PostMapping("/{quizId}/grade")
    public ResponseEntity<?> gradeQuiz(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @PathVariable(value = "quizId") Long quizId,
            @Valid @RequestBody AnswerReq answerReq) {

        return ResponseEntity.status(HttpStatus.OK).body(SuccessRes.from(quizService.gradeQuiz(userDetails.getId(), quizId, answerReq.answer())));
    }

    @GetMapping("/{quizId}/answer")
    public ResponseEntity<?> getAnswer(@PathVariable(value = "quizId") Long quizId) {

        return ResponseEntity.status(HttpStatus.OK).body(SuccessRes.from(quizService.getAnswer(quizId)));
    }

    @GetMapping
    public ResponseEntity<?> getQuizzesByKeywordAndSubject(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "subject", required = false) Long subjectId,
            @RequestParam(value = "sort", defaultValue = "DATE_DESC") QuizSortType sort,
            @PageableDefault(size=20) Pageable pageable) {

        Page<QuizSummaryRes> quizzes = quizService.getQuizzesByKeywordAndSubject(userDetails.getId(), keyword, subjectId, sort, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(quizzes);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getQuizzesByWriter(
            @AuthenticationPrincipal SecurityUserDetails userDetails,
            @RequestParam(value = "sort", defaultValue = "DATE_DESC") QuizSortType sort,
            @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page) {

        Page<QuizSummaryRes> quizzes = quizService.getQuizzesByWriter(userDetails.getId(), sort, page);

        return ResponseEntity.status(HttpStatus.OK).body(quizzes);
    }
}
