package yuquiz.domain.quizSeries.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yuquiz.common.api.SuccessRes;
import yuquiz.domain.quizSeries.api.QuizSeriesApi;
import yuquiz.domain.quizSeries.service.QuizSeriesService;
import yuquiz.security.auth.SecurityUserDetails;

@RestController
@RequestMapping("/api/v1/series")
@RequiredArgsConstructor
public class QuizSeriesController implements QuizSeriesApi {

    private final QuizSeriesService quizSeriesService;

    @Override
    @PostMapping("/{seriesId}/{quizId}")
    public ResponseEntity<?> addQuizToSeries(@PathVariable(value = "seriesId") Long seriesId,
                                             @PathVariable(value = "quizId") Long quizId,
                                             @AuthenticationPrincipal SecurityUserDetails userDetails) {

        quizSeriesService.addQuiz(seriesId, quizId, userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessRes.from("문제집에 문제 추가 성공"));
    }

    @Override
    @DeleteMapping("/{seriesId}/{quizId}")
    public ResponseEntity<?> deleteQuizFromSeries(@PathVariable(value = "seriesId") Long seriesId,
                                                  @PathVariable(value = "quizId") Long quizId,
                                                  @AuthenticationPrincipal SecurityUserDetails userDetails) {

        quizSeriesService.deleteQuiz(seriesId, quizId, userDetails.getId());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
