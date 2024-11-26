package yuquiz.domain.subject.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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

    public void addSubject(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);


        for (Row row : sheet) {
            if (row.getRowNum() <= 3) {
                continue;
            }
            String majorName = row.getCell(9).getStringCellValue();

            Major major = majorRepository.findByMajorName(majorName)
                    .orElseGet(() -> {
                        Major newMajor = Major.builder()
                                .majorName(majorName)
                                .build();
                        return majorRepository.save(newMajor);
                    });

            // 셀에서 subjectCode 가져오기
            Cell codeCell = row.getCell(0);
            String subjectCode = null;

            if (codeCell != null) {
                if (codeCell.getCellType() == CellType.NUMERIC) {
                    // 숫자일 경우
                    double numericValue = codeCell.getNumericCellValue();
                    subjectCode = String.valueOf((long) numericValue).substring(0, 4);
                } else if (codeCell.getCellType() == CellType.STRING) {
                    // 문자열일 경우
                    subjectCode = codeCell.getStringCellValue();
                } else {
                    throw new IOException();
                }

                Subject subject = Subject.builder()
                        .subjectCode(subjectCode)
                        .subjectName(row.getCell(3).getStringCellValue())
                        .major(major)
                        .build();

                subjectRepository.save(subject);

                System.out.println(subjectCode);
                System.out.println(row.getCell(3).getStringCellValue());
                System.out.println(major.getMajorName());
                System.out.println("-------------------------");
            }

            workbook.close();
        }
    }

}
