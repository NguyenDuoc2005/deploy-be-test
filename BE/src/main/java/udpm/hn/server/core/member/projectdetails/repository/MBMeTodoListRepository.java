package udpm.hn.server.core.member.projectdetails.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.entity.TodoList;
import udpm.hn.server.repository.TodoListRepository;

import java.util.List;

public interface MBMeTodoListRepository extends TodoListRepository {

    @Modifying
    @Transactional
    @Query("""
    UPDATE TodoList t 
    SET t.indexTodoList = t.indexTodoList - 1 
    WHERE t.indexTodoList > :indexBefore 
      AND t.indexTodoList <= :indexAfter 
      AND t.project.id = :idProject
      AND t.status = 0
      AND t.phaseProject.id = :idPhase
""")
    void updateIndexTodoListDecs(@Param("indexBefore") Integer indexBefore,
                                 @Param("indexAfter") Integer indexAfter,
                                 @Param("idProject") String idProject,
                                 @Param("idPhase") String idPhase
    );


    @Modifying
    @Transactional
    @Query("""
    UPDATE TodoList t 
    SET t.indexTodoList = t.indexTodoList + 1 
    WHERE t.indexTodoList < :indexBefore 
      AND t.indexTodoList >= :indexAfter 
      AND t.project.id = :idProject
      AND t.status  =0
     AND t.phaseProject.id = :idPhase
""")
    void updateIndexTodoListAsc(@Param("indexBefore") Integer indexBefore,
                                @Param("indexAfter") Integer indexAfter,
                                @Param("idProject") String idProject,
                                @Param("idPhase") String idPhase
    );


    @Modifying
    @Transactional
    @Query("""
    UPDATE TodoList t SET t.indexTodoList = :indexAfter
     WHERE t.id = :idTodoList
      AND t.phaseProject.id = :idPhase
""")
    void updateIndexTodoList(@Param("idTodoList") String idTodoList,
                             @Param("indexAfter") Integer indexAfter,
                             @Param("idPhase") String idPhase);

    @Modifying
    @Transactional
    @Query("""
    UPDATE TodoList t
    SET t.indexTodoList = t.indexTodoList - 1
    WHERE t.indexTodoList > :indexParam
    AND t.project.id = :idProject
    AND t.status = 0
     AND t.phaseProject.id = :idPhase
""")
    void updateIndexTodoList(@Param("indexParam") Integer indexParam,
                             @Param("idProject") String idProject,
                             @Param("idPhase") String idPhase
                             );

}
