package udpm.hn.server.core.manage.todo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.manage.todo.model.response.MATodoVoteResponse;
import udpm.hn.server.core.manage.todo.model.response.TodoVoteLevelStatistics;
import udpm.hn.server.core.manage.todo.model.response.WorkVoteResponse;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.StageVote;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.entity.TodoVote;
import udpm.hn.server.repository.TodoVoteRepository;

import java.util.List;
import java.util.Optional;

public interface MATodoVoteRepository extends TodoVoteRepository {

    @Query("""
    select 
        tv.todo.name as nameTodo,
        tv.staffProject.student.name as nameStudent,
        tv.staffProject.staff.name as  nameStaff,
        tv.id as id 
    from TodoVote tv
""")
    List<MATodoVoteResponse>  getAllVote();

    @Query(value = """
    SELECT
            td.priority_Level as level,
            COUNT(DISTINCT td.id) as total
        FROM Todo td
        JOIN Todo_Vote tv ON td.id = tv.id_todo
        JOIN Todo_List tl ON td.id_todo_list = tl.id
        JOIN Project p ON tl.id_project = p.id
        WHERE td.status = 0
        AND p.id = :idProject
        GROUP BY td.priority_Level
""", nativeQuery = true)
    List<TodoVoteLevelStatistics> countTodoVotedByPriorityLevel(@Param("idProject") String idProject);


    Optional<TodoVote> findByTodoAndStaffProjectAndStageVoteId(Todo todo, StaffProject staffProject, String stageVote);

    @Query(value = """
    SELECT
    td.name AS name,
    COALESCE(st.name, s.name) AS memberName,
    td.priority_Level AS level
    FROM Todo td
    JOIN Todo_Vote tv ON td.id = tv.id_todo
    LEFT JOIN Staff_Project sf ON tv.id_staff_Project = sf.id
    LEFT JOIN Student st ON st.id = sf.id_student
    LEFT JOIN Staff s ON s.id = sf.id_staff
    WHERE td.status = 0
    AND td.priority_Level IS NOT NULL
    AND sf.id_project = :idProject
""", nativeQuery = true)
    List<WorkVoteResponse> findAllVotedTodos(@Param("idProject") String idProject);


}
