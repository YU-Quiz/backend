package yuquiz.domain.series.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yuquiz.common.api.SuccessRes;
import yuquiz.domain.series.api.SeriesApi;
import yuquiz.domain.series.dto.SeriesReq;
import yuquiz.domain.series.dto.SeriesRes;
import yuquiz.domain.series.dto.SeriesSortType;
import yuquiz.domain.series.dto.SeriesSummaryRes;
import yuquiz.domain.series.service.SeriesService;
import yuquiz.security.auth.SecurityUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/series")
public class SeriesController implements SeriesApi {

    private final SeriesService seriesService;

    @Override
    @PostMapping
    public ResponseEntity<?> createSeries(@RequestBody @Valid SeriesReq seriesReq, @AuthenticationPrincipal SecurityUserDetails userDetails) {

        seriesService.createSeries(seriesReq, userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessRes.from("문제집 생성 성공"));
    }

    @Override
    @GetMapping("/{seriesId}")
    public ResponseEntity<?> findSeriesById(@PathVariable(value = "seriesId") Long seriesId, @AuthenticationPrincipal SecurityUserDetails userDetails) {

        SeriesRes seriesRes = seriesService.findSeriesById(seriesId, userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(seriesRes);
    }

    @Override
    @DeleteMapping("/{seriesId}")
    public ResponseEntity<?> deleteSeriesById(@PathVariable(value = "seriesId") Long seriesId, @AuthenticationPrincipal SecurityUserDetails userDetails) {

        seriesService.deleteSeriesById(seriesId, userDetails.getId());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @PutMapping("/{seriesId}")
    public ResponseEntity<?> updateSeriesById(@PathVariable(value = "seriesId") Long seriesId, @RequestBody @Valid SeriesReq seriesReq, @AuthenticationPrincipal SecurityUserDetails userDetails) {

        seriesService.updateSeriesById(seriesId, seriesReq, userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(SuccessRes.from("문제집 수정 성공"));
    }

    @Override
    @GetMapping
    public ResponseEntity<?> getSeriesSummary(@RequestParam(value = "keyword", required = false) String keyword,
                                              @RequestParam(value = "sort", defaultValue = "DATE_DESC") SeriesSortType sort,
                                              @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page) {

        Page<SeriesSummaryRes> seriesSummary = seriesService.getSeriesSummary(keyword, sort, page);

        return ResponseEntity.status(HttpStatus.OK).body(seriesSummary);
    }
}
