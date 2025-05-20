package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import udpm.hn.server.entity.Report;

public interface ReportRepository extends JpaRepository<Report, String> {
}
