package yuquiz.domain.major.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yuquiz.domain.major.service.MajorService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/majors")
public class MajorController {
    private final MajorService majorService;

    @GetMapping()
    public ResponseEntity<?> getMajors() {
        return ResponseEntity.status(HttpStatus.OK).body(majorService.getMajors());
    }
}
