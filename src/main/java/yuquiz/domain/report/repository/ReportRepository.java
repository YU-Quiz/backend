package yuquiz.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuquiz.domain.report.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}