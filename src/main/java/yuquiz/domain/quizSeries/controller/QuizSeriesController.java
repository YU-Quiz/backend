package yuquiz.domain.quizSeries.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yuquiz.common.api.SuccessRes;
import yuquiz.domain.quiz.dto.quiz.QuizSummaryRes;
import yuquiz.domain.quizSeries.api.QuizSeriesApi;
import yuquiz.domain.quizSeries.service.QuizSeriesService;
import yuquiz.security.auth.SecurityUserDetails;

@RestController
@RequestMapping("/api/v1/series")
@RequiredArgsConstructor
public class QuizSeriesController implements QuizSeriesApi {

    private final QuizSeriesService quizSeriesService;

    @GetMapping("/quizzes/{seriesId}")
    public ResponseEntity<?> getQuizzesBySeriesId(@PathVariable(value = "seriesId") Long seriesId,
                                                  @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                  @AuthenticationPrincipal SecurityUserDetails userDetails) {

        Page<QuizSummaryRes> quizSeriesRes = quizSeriesService.getQuizzesBySeriesId(seriesId, page, userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(quizSeriesRes);
    }

    @Override
    @PostMapping("/quizzes/{seriesId}/{quizId}")
    public ResponseEntity<?> addQuizToSeries(@PathVariable(value = "seriesId") Long seriesId,
                                             @PathVariable(value = "quizId") Long quizId,
                                             @AuthenticationPrincipal SecurityUserDetails userDetails) {

        quizSeriesService.addQuiz(seriesId, quizId, userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessRes.from("문제집에 문제 추가 성공"));
    }

    @Override
    @DeleteMapping("/quizzses/{seriesId}/{quizId}")
    public ResponseEntity<?> deleteQuizFromSeries(@PathVariable(value = "seriesId") Long seriesId,
                                                  @PathVariable(value = "quizId") Long quizId,
                                                  @AuthenticationPrincipal SecurityUserDetails userDetails) {

        quizSeriesService.deleteQuiz(seriesId, quizId, userDetails.getId());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
