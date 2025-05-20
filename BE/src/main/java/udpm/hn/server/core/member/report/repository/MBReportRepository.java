package udpm.hn.server.core.member.report.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.admin.department.department.model.request.ADMajorRequest;
import udpm.hn.server.core.member.report.model.request.MBReportRequest;
import udpm.hn.server.core.member.report.model.response.MBReportResponse;
import udpm.hn.server.repository.ReportRepository;
import udpm.hn.server.utils.UserContextHelper;

import java.util.List;

@Repository
public interface MBReportRepository extends ReportRepository {

//    @Query(value= """
//        SELECT
//        ROW_NUMBER() OVER(ORDER BY rp.id DESC) AS orderNUmber,
//        rp.id AS idReport,
//        rp.workDoneToday AS wordDoneTodayReport,
//        rp.workPlanTomorrow AS wordPlanTomorrowReport,
//        rp.obstacles AS obstaclesReport,
//        rp.statusReport AS statusReport,
//        rp.reportTime AS reportTime,
//        rp.createdDate AS createDate
//        FROM
//            Report rp
//        """,countQuery = """
//        SELECT
//            COUNT(rp.id)
//        FROM
//            Report rp
//        """
//    )
//    List<MBReportResponse> getAll();
    @Query(value = """
        SELECT 
        ROW_NUMBER() OVER(ORDER BY rp.id DESC) AS orderNUmber,
        rp.id AS idReport,
        rp.workDoneToday AS wordDoneTodayReport,
        rp.workPlanTomorrow AS wordPlanTomorrowReport,
        rp.obstacles AS obstaclesReport,
        rp.statusReport AS statusReport,
        rp.reportTime AS reportTime,
        rp.createdDate AS createDate
        FROM 
            Report rp
        JOIN Project p on rp.project.id = p.id
        join StaffProject sp on rp.staffProject.id = sp.id
        where rp.id = :id and p.id = :idProject
        AND (sp.staff.id = :#{#userContextHelper.currentUserId}
                 OR sp.student.id = :#{#userContextHelper.currentUserId})
""", countQuery = """
            SELECT
                COUNT(rp.id)
        FROM 
            Report rp
        JOIN Project p on rp.project.id = p.id
        join StaffProject sp on rp.staffProject.id = sp.id
        where rp.id = :id and p.id = :idProject
        AND (sp.staff.id = :#{#userContextHelper.currentUserId}
         OR sp.student.id = :#{#userContextHelper.currentUserId})
            """
    )
    MBReportResponse detail(String id, String idProject, UserContextHelper userContextHelper);

    @Query("""
            SELECT r.id FROM
            Report r JOIN StaffProject sp ON r.staffProject.id = sp.id
            LEFT JOIN Staff s ON sp.staff.id = s.id
            LEFT JOIN Student s2 ON sp.student.id = s2.id
            WHERE (r.reportTime >= :startOfDay AND r.reportTime < :endOfDay) 
            AND (s.id = :idUser OR s2.id = :idUser)
            and r.project.id = :idProject
            """)
    String findIdByReportTime(@Param("startOfDay") Long startOfDay, @Param("endOfDay") Long endOfDay, @Param("idUser") String idUser,@Param("idProject") String idProject);

    @Query(value = """
    SELECT 
        ROW_NUMBER() OVER(ORDER BY rp.id DESC) AS orderNUmber,
        rp.id AS idReport,
        rp.workDoneToday AS wordDoneTodayReport,
        rp.workPlanTomorrow AS wordPlanTomorrowReport,
        rp.obstacles AS obstaclesReport,
        rp.statusReport AS statusReport,
        rp.reportTime AS reportTime,
        rp.createdDate AS createDate,
        p.id AS idProject
    FROM 
        Report rp
    JOIN StaffProject sp on rp.staffProject.id = sp.id
    JOIN Project p ON p.id = sp.project.id
    WHERE 
        (sp.staff.id = :#{#userContextHelper.currentUserId}
         OR sp.student.id = :#{#userContextHelper.currentUserId})
        AND p.id = :idProject
        AND ( :#{#req.reportTime} IS NULL
            OR (rp.reportTime >= :#{#req.reportTime} AND rp.reportTime < :#{#req.reportTimeEnd})
        )
""", countQuery = """
    SELECT
        COUNT(rp.id)
    FROM
        Report rp
    JOIN StaffProject sp on rp.staffProject.id = sp.id
    JOIN Project p ON p.id = sp.project.id
    WHERE 
        (sp.staff.id = :#{#userContextHelper.currentUserId}
         OR sp.student.id = :#{#userContextHelper.currentUserId})
        AND p.id = :idProject
        AND ( :#{#req.reportTime} IS NULL
            OR (rp.reportTime >= :#{#req.reportTime} AND rp.reportTime < :#{#req.reportTimeEnd})
        )
"""
    )
    Page<MBReportResponse> getAll(Pageable pageable, String idProject, UserContextHelper userContextHelper, @Param("req") MBReportRequest req);

}
