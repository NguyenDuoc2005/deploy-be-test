package udpm.hn.server.core.admin.staff.repository;

import org.springframework.data.jpa.repository.Query;
import udpm.hn.server.entity.MajorFacility;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffMajorFacility;
import udpm.hn.server.repository.StaffMajorFacilityRepository;

import java.util.Optional;

public interface ADStaffByMajorFacility extends StaffMajorFacilityRepository {


}