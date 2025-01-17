package yuquiz.domain.report.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yuquiz.domain.report.api.AdminReportApi;
import yuquiz.domain.report.dto.ReportSortType;
import yuquiz.domain.report.dto.ReportSummaryRes;
import yuquiz.domain.report.service.AdminReportService;

@RestController
@RequestMapping("/api/v1/admin/reports")
@RequiredArgsConstructor
public class AdminReportController implements AdminReportApi {

    private final AdminReportService adminReportService;

    @Override
    @GetMapping
    public ResponseEntity<?> getAllReports(@RequestParam ReportSortType sort,
                                           @RequestParam @Min(0) Integer page) {

        Page<ReportSummaryRes> reports = adminReportService.getAllReports(sort, page);

        return ResponseEntity.status(HttpStatus.OK).body(reports);
    }
}
