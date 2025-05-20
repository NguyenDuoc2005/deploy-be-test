package udpm.hn.server.infrastructure.job.todolist.repository;

import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.TodoList;
import udpm.hn.server.repository.ToDoRepository;
import udpm.hn.server.repository.TodoListRepository;

import java.util.Optional;

@Repository
public interface TodoExcelRepository extends ToDoRepository {

//    List<TodoList> findByCodeAndName(String code, String name);
//
//    List<TodoList> findByCode(String code);


}