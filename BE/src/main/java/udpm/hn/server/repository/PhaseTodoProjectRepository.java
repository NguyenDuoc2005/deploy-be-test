package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import udpm.hn.server.entity.PhaseProject;
import udpm.hn.server.entity.PhaseTodoProject;
import udpm.hn.server.entity.Todo;

import java.util.Optional;

public interface PhaseTodoProjectRepository extends JpaRepository<PhaseTodoProject,String> {

    boolean existsDistinctByTodoAndPhaseProject(Todo todo, PhaseProject phaseProject);

    Optional<PhaseTodoProject> findByTodo(Todo todo);

    Optional<PhaseTodoProject> findByTodoId(String id);

}
