package yuquiz.domain.major.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yuquiz.domain.major.dto.MajorRes;
import yuquiz.domain.major.entity.Major;
import yuquiz.domain.major.repository.MajorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MajorService {
    private final MajorRepository majorRepository;

    public List<MajorRes> getMajors() {
        List<Major> majors = majorRepository.findAll();

        return majors.stream().map(MajorRes::fromEntity).toList();
    }
}
