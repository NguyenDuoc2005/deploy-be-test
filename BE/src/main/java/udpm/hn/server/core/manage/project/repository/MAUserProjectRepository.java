package udpm.hn.server.core.manage.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.manage.project.model.response.MAUserProjectResponse;
import udpm.hn.server.repository.StaffProjectRepository;

public interface MAUserProjectRepository extends StaffProjectRepository {

    @Query("""
        select sp.role from StaffProject sp 
        left join Project p on sp.project.id = p.id
        left join Staff  s on sp.staff.id = s.id
        left join Student s2 on sp.student.id = s2.id 
        where p.id = :idProject
""")
    Page<MAUserProjectResponse> getAllUserByProject(Pageable pageable, @Param("idProject") String idProject);

}
