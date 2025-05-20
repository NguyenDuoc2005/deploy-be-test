package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.HistoryImportStudent;
import udpm.hn.server.entity.HistoryImportTodo;

@Repository
public interface HistoryImportTodoRepository extends JpaRepository<HistoryImportTodo, String> {

}
