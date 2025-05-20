package udpm.hn.server.infrastructure.job.staff.commonio;

import jakarta.transaction.Transactional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffMajorFacility;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.job.staff.model.dto.TransferStaffRole;
import udpm.hn.server.infrastructure.job.staff.repository.ConfigStaffCustomRepository;
import udpm.hn.server.infrastructure.job.staff.repository.ConfigStaffMajorFacilityRepository;

import java.util.UUID;

@Component
@Slf4j
public class StaffWriter implements ItemWriter<TransferStaffRole> {

    @Setter(onMethod_ = {@Autowired})
    private ConfigStaffCustomRepository staffCustomRepository;

//    @Setter(onMethod_ = {@Autowired})
//    private ConfigStaffRoleCustomRepository staffRoleCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private ConfigStaffMajorFacilityRepository staffMajorFacilityRepository;

    @Override
    public void write(Chunk<? extends TransferStaffRole> chunk) throws Exception {
        int recordNumber = 0;

        for (TransferStaffRole transferStaffRole : chunk) {
            recordNumber++;
            try {
                Staff savedStaff = saveOrUpdateStaff(transferStaffRole.getStaff());
//                StaffRole savedStaffRole = saveStaffRole(savedStaff, transferStaffRole.getRole());
                StaffMajorFacility savedStaffMajorFacility = saveStaffMajorFacility(savedStaff, transferStaffRole.getStaffMajorFacility());
                log.info(
                        "Successfully processed record number {}: Saved Staff Major Facility - {}",
                        recordNumber, savedStaffMajorFacility
                );
            } catch (Exception e) {
                log.error("Error processing record number {}: {}", recordNumber, transferStaffRole, e);
            }
        }
    }

    private Staff saveOrUpdateStaff(Staff staff) {
        return staffCustomRepository
                .findByCodeAndStatus(staff.getCode(), EntityStatus.ACTIVE)
                .orElseGet(() -> staffCustomRepository.save(staff));
    }

//    private StaffRole saveStaffRole(Staff staff, Role role) {
//        StaffRole staffRole = new StaffRole();
//        staffRole.setStaff(staff);
//        staffRole.setRole(role);
//        staffRole.setId(UUID.randomUUID().toString());
//        staffRole.setStatus(EntityStatus.ACTIVE);
//        return staffRoleCustomRepository.save(staffRole);
//    }

    private StaffMajorFacility saveStaffMajorFacility(Staff staff, StaffMajorFacility staffMajorFacility) {
        staffMajorFacility.setStaff(staff);
        return staffMajorFacilityRepository.save(staffMajorFacility);
    }
}
