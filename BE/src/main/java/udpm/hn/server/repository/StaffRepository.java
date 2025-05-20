package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffRole;
import udpm.hn.server.utils.UserContextHelper;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, String> {

    Optional<Staff> findByEmailFe(String email);

    Optional<Staff> findByEmailFpt(String emailFpt);

    @Query(
            value = """
                            select s.emailFpt from Staff s 
                            where s.id = :idStaff
                    """
    )
    String getEmailFpt(@Param("idStaff") String idStaff);
}
