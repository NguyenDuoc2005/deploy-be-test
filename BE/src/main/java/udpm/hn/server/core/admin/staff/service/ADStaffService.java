package udpm.hn.server.core.admin.staff.service;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import udpm.hn.server.core.admin.staff.model.request.ADCreateStaffFDM;
import udpm.hn.server.core.admin.staff.model.request.ADCreateStaffRequest;
import udpm.hn.server.core.admin.staff.model.request.ADStaffRequest;
import udpm.hn.server.core.admin.staff.model.request.ADUpdateStaffFDMRequest;
import udpm.hn.server.core.admin.staff.model.request.RoleStaffRequest;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.Staff;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ADStaffService {

    ResponseObject<?> getAllStaff(ADStaffRequest request);

    ResponseObject<?> getAllRole( );

    ResponseObject<?> createStaff(ADCreateStaffRequest request);

    ResponseObject<?> updateStaff(String id,ADCreateStaffRequest request);

    ResponseObject<?> deleteStaff(String id);

    ResponseObject<?> detailStaff(String id);

    ResponseObject<?> staffByDepartmentFacility(String id);

    ResponseObject<?> getMajorByDepartment(String idDepartment);

    ResponseObject<?> getAllDepartmentByFacility(String idFacility);

    ResponseObject<?> createStaffByFDM(ADCreateStaffFDM request);

    ResponseObject<?> updateStaffByFDM(ADUpdateStaffFDMRequest request);

    ResponseObject<?> deleteStaffByFDM(String id);

    ResponseObject<?> getRoleByStaff(String idStaff);

    ResponseObject<?> createUpdateRoleByStaff(RoleStaffRequest request);

    ResponseObject<?> deleteRoleByStaff(RoleStaffRequest request );

    ResponseObject<?> getAllStaffNoProject();

    ResponseObject<?> getStaffRoleByStaff(String idStaff);

    ResponseObject<?> getLogsImportStaff(int page, int size) throws FileNotFoundException;

    Resource getAllCsv() throws IOException;
}