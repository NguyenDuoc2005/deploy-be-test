package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import udpm.hn.server.entity.DepartmentFacility;

import java.util.Optional;

public interface DepartmentFacilityRepository extends JpaRepository<DepartmentFacility,String> {


}
