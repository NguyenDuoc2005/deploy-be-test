package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.HistoryImportStudent;

@Repository
public interface HistoryImportStudentRepository extends JpaRepository<HistoryImportStudent, String> {

}
