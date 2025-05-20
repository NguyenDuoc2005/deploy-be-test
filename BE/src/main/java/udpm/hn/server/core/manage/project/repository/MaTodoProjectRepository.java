package udpm.hn.server.core.manage.project.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.manage.project.model.response.MaTodoSummaryResponse;
import udpm.hn.server.repository.ToDoRepository;

import java.util.List;

@Repository
public interface MaTodoProjectRepository extends ToDoRepository {
    @Query(value = """
        select 
            count(1) as amount
            ,t.statusTodo as todoStatus
        from Todo t
        join TodoList tl on t.todoList.id = tl.id
        where tl.project.id = :id
        group by t.statusTodo
        """)
    List<MaTodoSummaryResponse> countTodoByTodoStatus(@Param("id") String id);
}
