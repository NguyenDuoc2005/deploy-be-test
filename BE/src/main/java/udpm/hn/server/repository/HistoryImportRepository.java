package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.HistoryImport;
import udpm.hn.server.infrastructure.constant.LogFileType;

import java.util.List;

@Repository
public interface HistoryImportRepository extends JpaRepository<HistoryImport, String> {

    List<HistoryImport> findAllByStaff_IdAndLogFileType(
            String staffId,
            LogFileType logFileType
    );

}
