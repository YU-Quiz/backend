package yuquiz.domain.major.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuquiz.domain.major.entity.Major;

import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, Long> {
    public Optional<Major> findByMajorName(String name);
}
