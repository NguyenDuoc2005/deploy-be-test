package udpm.hn.server.core.manage.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.manage.report.model.request.MAReportRequest;
import udpm.hn.server.core.manage.report.model.response.MAReportResponse;
import udpm.hn.server.repository.ReportRepository;

import java.time.LocalDate;

public interface MAReportRepository extends ReportRepository {

    @Query("""
    SELECT s.name as nameStaff,
           s.picture as imageStaff,
           s2.name as nameStudent,
           s2.image as imageStudent,
           s2.code as codeStudent,
           s.code as codeStaff,
           r.workDoneToday as workDoneToday,
           r.workPlanTomorrow as workPlanTomorrow,
           r.obstacles as obstacles,
           r.reportTime as reportTime
    from StaffProject sp
    left join Staff s on sp.staff.id = s.id
    left join Student s2 on sp.student.id = s2.id
    left join Report r ON r.staffProject.id = sp.id 
    AND (r.reportTime between :dateStart and :dateEnd  or r.reportTime IS NULL)
    WHERE sp.project.id = :idProject 
""")
    Page<MAReportResponse>  getAllReport(Pageable pageable, @Param("dateStart") Long dateStart , @Param("dateEnd") Long dateEnd, @Param("idProject") String idProject);
}


//and s2 is null