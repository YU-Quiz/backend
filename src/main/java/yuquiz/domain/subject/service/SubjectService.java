package yuquiz.domain.subject.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yuquiz.common.exception.CustomException;
import yuquiz.domain.major.entity.Major;
import yuquiz.domain.major.repository.MajorRepository;
import yuquiz.domain.subject.dto.SubjectRes;
import yuquiz.domain.subject.entity.Subject;
import yuquiz.domain.subject.repository.SubjectRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final MajorRepository majorRepository;

    public List<SubjectRes> getSubjectByKeyword(String keyword) {
        List<Subject> subjects = subjectRepository.findSubjectsByKeyword(keyword);

        return subjects.stream()
                .map(SubjectRes::fromEntity)
                .collect(Collectors.toList());
    }

    public void addSubject(MultipartFile file) throws IOException{
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        Major major = majorRepository.findByMajorName("컴퓨터공학과")
                .orElseThrow(IOException::new);

        for (Row row : sheet) {
            if (row.getRowNum() == 0 || row.getRowNum() == 1 || row.getRowNum() == 2 || row.getRowNum() == 3) {
                continue;
            }

            Subject subject = Subject.builder()
                    .subjectCode(String.valueOf(row.getCell(0).getNumericCellValue()).substring(0,4))
                    .subjectName(row.getCell(3).getStringCellValue())
                    .major(major)
                    .build();

            subjectRepository.save(subject);

            System.out.println(String.valueOf(row.getCell(0).getNumericCellValue()).substring(0,4));
            System.out.println(row.getCell(3).getStringCellValue());
            System.out.println("-------------------------");
        }

        workbook.close();
    }

}
