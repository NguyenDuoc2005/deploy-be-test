package udpm.hn.server.infrastructure.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Student;
import udpm.hn.server.repository.StaffProjectRepository;

public interface StaffProjectAuthRepository extends StaffProjectRepository {
    // Kiểm tra xem thành viên có dự án nào không
    boolean existsByStaff(Staff staff);

    boolean existsByStudent(Student student);
}
