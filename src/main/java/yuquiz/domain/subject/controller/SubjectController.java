package yuquiz.domain.subject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import yuquiz.domain.subject.api.SubjectApi;
import yuquiz.domain.subject.service.SubjectService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subject")
public class SubjectController implements SubjectApi {
    private final SubjectService subjectService;

    @GetMapping()
    public ResponseEntity<?> getSubjectByKeyword(
            @RequestParam(value = "keyword", defaultValue = "") String keyword) {

        return ResponseEntity.status(HttpStatus.OK).body(subjectService.getSubjectByKeyword(keyword));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcelFile(@RequestParam("file") MultipartFile file) {
        try {
            subjectService.addSubject(file);
            return ResponseEntity.ok("데이터가 성공적으로 저장되었습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 처리 중 오류가 발생했습니다.");
        }
    }

}
