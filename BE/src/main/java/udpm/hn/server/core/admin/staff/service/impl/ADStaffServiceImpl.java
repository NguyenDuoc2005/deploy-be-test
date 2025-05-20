package udpm.hn.server.core.admin.staff.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.admin.staff.model.request.ADCreateStaffFDM;
import udpm.hn.server.core.admin.staff.model.request.ADCreateStaffRequest;
import udpm.hn.server.core.admin.staff.model.request.ADStaffProjection;
import udpm.hn.server.core.admin.staff.model.request.ADStaffRequest;
import udpm.hn.server.core.admin.staff.model.request.ADUpdateStaffFDMRequest;
import udpm.hn.server.core.admin.staff.model.request.RoleStaffRequest;
import udpm.hn.server.core.admin.staff.model.response.ADStaffByDepartmentFacility;
import udpm.hn.server.core.admin.staff.model.response.StaffResponse;
import udpm.hn.server.core.admin.staff.model.response.StaffRoleResponse;
import udpm.hn.server.core.admin.staff.repository.ADStaffByDeparmentFacilityRepository;
import udpm.hn.server.core.admin.staff.repository.ADStaffDepartmentFacilityRepository;
import udpm.hn.server.core.admin.staff.repository.ADStaffDepartmentRepository;
import udpm.hn.server.core.admin.staff.repository.ADStaffMajorFacilityRepository;
import udpm.hn.server.core.admin.staff.repository.ADStaffMajorRepository;
import udpm.hn.server.core.admin.staff.repository.ADStaffRepository;
import udpm.hn.server.core.admin.staff.repository.ADStaffRoleRepository;
import udpm.hn.server.core.admin.staff.service.ADStaffService;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.LogFileType;
import udpm.hn.server.infrastructure.job.staff.jobconfig.StaffJobLauncher;
import udpm.hn.server.infrastructure.job.staff.model.dto.HistoryImportResponse;
import udpm.hn.server.infrastructure.log.LogService;
import udpm.hn.server.repository.RoleRepository;
import udpm.hn.server.repository.StaffMajorFacilityRepository;
import udpm.hn.server.repository.StaffRoleRepository;
import udpm.hn.server.utils.Helper;
import udpm.hn.server.utils.HistoryLogUtils;
import udpm.hn.server.utils.UserContextHelper;

import javax.swing.text.html.Option;
import java.io.*;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
@RequiredArgsConstructor
@Slf4j
public class ADStaffServiceImpl implements ADStaffService {

    private final ADStaffRepository adStaffRepository;

    private final ADStaffByDeparmentFacilityRepository adStaffByDeparmentFacilityRepository;

    private final ADStaffDepartmentRepository adStaffDepartmentRepository;

    private final ADStaffMajorRepository adStaffMajorRepository;

    private final ADStaffDepartmentFacilityRepository departmentFacilityRepository;

    private final ADStaffMajorFacilityRepository adStaffMajorFacilityRepository;

    private final RoleRepository roleRepository;

    private final ADStaffRoleRepository adStaffRoleRepository;

    private final StaffRoleRepository staffRoleRepository;

    private final StaffMajorFacilityRepository staffMajorFacilityRepository;

    private final HistoryLogUtils historyLogUtils;

    private final UserContextHelper userContextHelper;

    private static final String DIRECTORY = "src/main/resources/staff-excel/";

    @Override
    public ResponseObject<?> getAllStaff(ADStaffRequest request) {
        Pageable page = Helper.createPageable(request, "createDate");
        EntityStatus status = null;
        if (request.getSearchStatus() != null) {
            status = EntityStatus.values()[Integer.valueOf(request.getSearchStatus())];
        }
        Page<StaffResponse> projections = adStaffRepository.getAllStaff(page, request, status);

        Map<String, ADStaffProjection> staffMap = new LinkedHashMap<>();

        projections.forEach(projection -> {
            staffMap.computeIfAbsent(projection.getId(), id -> new ADStaffProjection(projection))
                    .addRole(projection.getNameRole());
        });

//        return new ResponseObject<>(new ArrayList<>(staffMap.values()), HttpStatus.OK, "Lấy dữ liệu thành công");
        return new ResponseObject<>(
                PageableObject.of(
                    new PageImpl<ADStaffProjection>(
                            new ArrayList(staffMap.values()),
                            projections.getPageable(),
                            projections.getTotalElements()
                    )
        ), HttpStatus.OK, "Lấy dữ liệu thành công");
    }


    @Override
    public ResponseObject<?> getAllRole() {
        return new ResponseObject<>(adStaffRoleRepository.getAllRole(), HttpStatus.OK, "Lâý dữ liệu thành công");
    }


    @Override
    public ResponseObject<?> getMajorByDepartment(String idDepartment) {
        return new ResponseObject<>(adStaffMajorRepository.getMajorByDepartment(idDepartment), HttpStatus.OK, "Lấy dữ liệu thành công");

    }

    @Override
    public ResponseObject<?> getAllDepartmentByFacility(String idFacility) {
        return new ResponseObject<>(adStaffDepartmentRepository.getAllDepartmentByFacility(idFacility), HttpStatus.OK, "Lấy dữ liệu bộ môn theo cơ sở thành công");

    }

    @Override
    public ResponseObject<?> createStaffByFDM(ADCreateStaffFDM request) {

        Optional<Facility> optionalFacility = adStaffByDeparmentFacilityRepository.findById(request.getIdFacility());
        if (optionalFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy cơ sở này");
        }

        Optional<Department> optionalDepartment = adStaffDepartmentRepository.findById(request.getIdDepartment());
        if (optionalDepartment.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy bộ môn này");
        }

        Optional<Major> optionalMajor = adStaffMajorRepository.findById(request.getIdMajor());
        if (optionalMajor.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên ngành này");
        }

        Optional<DepartmentFacility> optionalDepartmentFacility = departmentFacilityRepository.findByDepartmentAndFacility(optionalDepartment.get(), optionalFacility.get());
        if (optionalDepartmentFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy bộ môn cơ sở này ");
        }

        Optional<MajorFacility> optionalMajorFacility = adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(optionalMajor.get(), optionalDepartmentFacility.get());
        if (optionalMajorFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên nghành cơ sở này ");
        }

        Optional<Staff> optionalStaff = adStaffRepository.findById(request.getIdStaffDetail());
        if (optionalStaff.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên này ");
        }
        // update role
        Set<String> setCodeRole = new HashSet<>();
        List<StaffRole> staffRoles = adStaffRoleRepository.getStaffRoleByIdStaff(request.getIdStaffDetail());
        Map<String, Role> roles = adStaffRoleRepository.getRoleByFacility(request.getIdFacility())
                .stream().collect(Collectors.toMap(
                        key -> key.getCode(),
                        value -> value
                ));
        for (StaffRole staffRole : staffRoles) {
            if (!setCodeRole.contains(staffRole.getRole().getCode())) {
                staffRole.setRole(roles.get(staffRole.getRole().getCode()));
                staffRoleRepository.save(staffRole);
                setCodeRole.add(staffRole.getRole().getCode());
            }
        }

        StaffMajorFacility staffMajorFacility = StaffMajorFacility.builder().majorFacility(optionalMajorFacility.get()).staff(optionalStaff.get()).build();

        staffMajorFacilityRepository.save(staffMajorFacility);

        return new ResponseObject<>(null, HttpStatus.OK, "Thêm thành công chuyên ngành bộ môn cơ sở");
    }

    @Override
    public ResponseObject<?> updateStaffByFDM(ADUpdateStaffFDMRequest request) {

        Optional<DepartmentFacility> optionalDepartmentFacility = departmentFacilityRepository.findByDepartmentAndFacilityAndStaff(request.getIdDepartment(), request.getIdFacility());
        if (optionalDepartmentFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy bộ môn cơ sở  này  ");
        }

        Optional<Major> optionalFindMajor = adStaffMajorRepository.findById(request.getIdMajor());
        if (optionalFindMajor.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên ngành này");
        }

        Optional<MajorFacility> optionalMajorFacility = adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(optionalFindMajor.get(), optionalDepartmentFacility.get());
        if (optionalMajorFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên nghành cơ sở này ");
        }

        Optional<Staff> optionalStaff = adStaffRepository.findById(request.getIdStaffDetail());
        if (optionalStaff.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thành viên này");
        }

        Optional<StaffMajorFacility> optionalStaffMajorFacility = staffMajorFacilityRepository.findByStaffAndMajorFacility(optionalStaff.get(), optionalMajorFacility.get());

        Optional<DepartmentFacility> optionalDepartmentFacilityUpdate = departmentFacilityRepository.findByDepartmentAndFacilityAndStaff(request.getIdUpdateDepartment(), request.getIdUpdateFacility());
        if (optionalDepartmentFacilityUpdate.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy bộ môn cơ sở này update ");
        }

        Optional<Major> optionalMajorUpdate = adStaffMajorRepository.findById(request.getIdUpdateMajor());
        if (optionalMajorUpdate.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên ngành này update");
        }

        Optional<MajorFacility> optionalMajorFacilityUpdate = adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(optionalMajorUpdate.get(), optionalDepartmentFacilityUpdate.get());
        if (optionalMajorFacilityUpdate.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên ngành cơ sở này update");
        }

        // update role
        Set<String> setCodeRole = new HashSet<>();
        List<StaffRole> staffRoles = adStaffRoleRepository.getStaffRoleByIdStaff(request.getIdStaffDetail());
        Map<String, Role> roles = adStaffRoleRepository.getRoleByFacility(request.getIdUpdateFacility())
                .stream().collect(Collectors.toMap(
                        key -> key.getCode(),
                        value -> value
                ));

        for (StaffRole staffRole : staffRoles) {
            if (!setCodeRole.contains(staffRole.getRole().getCode())) {
                staffRole.setRole(roles.get(staffRole.getRole().getCode()));
                staffRoleRepository.save(staffRole);
                setCodeRole.add(staffRole.getRole().getCode());
            }
        }

        StaffMajorFacility staffMajorFacility = optionalStaffMajorFacility.get();
        staffMajorFacility.setMajorFacility(optionalMajorFacilityUpdate.get());

        staffMajorFacilityRepository.save(staffMajorFacility);

        return new ResponseObject<>(null, HttpStatus.OK, "Update thành công");

    }

    @Override
    public ResponseObject<?> deleteStaffByFDM(String id) {

        Optional<StaffMajorFacility> optionalStaffMajorFacility = staffMajorFacilityRepository.findById(id);
        if (optionalStaffMajorFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thành viên cơ sở này");
        }

        Staff exsitStaff = optionalStaffMajorFacility.get().getStaff();

        Set<String> setCodeRole = new HashSet<>();
        List<StaffRole> staffRoles = adStaffRoleRepository.getStaffRoleByIdStaff(exsitStaff.getId());
        Map<String, Role> roles = adStaffRoleRepository.getRoleDefault()
                .stream().collect(Collectors.toMap(
                        key -> key.getCode(),
                        value -> value
                ));
        for (StaffRole staffRole : staffRoles) {
            if (!setCodeRole.contains(staffRole.getRole().getCode())) {
                staffRole.setRole(roles.get(staffRole.getRole().getCode()));
                staffRoleRepository.save(staffRole);
                setCodeRole.add(staffRole.getRole().getCode());
            }
        }

        staffMajorFacilityRepository.delete(optionalStaffMajorFacility.get());

        return new ResponseObject<>(null, HttpStatus.OK, "Xóa thành viên khỏi chuyên ngành cơ sở thành cồng");
    }

    @Override
    public ResponseObject<?> getRoleByStaff(String idStaff) {
        List<StaffRoleResponse> staffRoleResponses = adStaffRoleRepository.getRoleByStaff(idStaff);
        Optional<StaffRole> theLast = adStaffRoleRepository.getLastStaffRoleAdmin().stream().findFirst();

        if (theLast.isPresent()) {
            StaffRole theLastAdmin = theLast.get();
            for (StaffRoleResponse staffRoleResponse : staffRoleResponses) {
                if (theLastAdmin.getId().equals(staffRoleResponse.getId())) {
                    staffRoleResponses.remove(staffRoleResponse);
                    break;
                }
            }
        }

        return new ResponseObject<>(staffRoleResponses, HttpStatus.OK, "Lấy dữ liệu thành công");
    }

    @Override
    public ResponseObject<?> getStaffRoleByStaff(String idStaff) {
        return new ResponseObject<>(adStaffRoleRepository.getStaffRoleByStaff(idStaff), HttpStatus.OK, "Lấy dữ liệu thành công");
    }

    @Override
    public ResponseObject<?> createUpdateRoleByStaff(RoleStaffRequest request) {

        Optional<Staff> optionalStaff = adStaffRepository.findById(request.getIdStaff());
        if (optionalStaff.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên này");
        }

        StaffRole newStaffRole = new StaffRole();
        newStaffRole.setStaff(optionalStaff.get());
        Optional<ADStaffByDepartmentFacility> optionalAdStaffByDepartmentFacility = adStaffByDeparmentFacilityRepository.staffByDepartmentFacility(request.getIdStaff());
        if (optionalAdStaffByDepartmentFacility.isPresent()) {

            List<Role> roles = adStaffRoleRepository.getRoleByFacility(optionalAdStaffByDepartmentFacility.get().getIdFacility());

            for (Role role : roles) {
                if (role.getCode().equals(request.getCodeRole())) {
                    newStaffRole.setRole(role);
                    break;
                }
            }
        } else {
            Optional<Role> optionalRole = adStaffRoleRepository.findRoleDefaultByCode(request.getCodeRole());

            newStaffRole.setStaff(optionalStaff.get());
            newStaffRole.setRole(optionalRole.get());
        }

        staffRoleRepository.save(newStaffRole);

        return new ResponseObject<>(null, HttpStatus.OK, "Thêm quyền cho nhân viên thành công");

    }

    @Override
    public ResponseObject<?> deleteRoleByStaff(RoleStaffRequest request) {

        Optional<StaffRole> optionalStaffRole = adStaffRoleRepository.findTheLastByRoleIdAndStaffId(request.getIdRole(), request.getIdStaff())
                .stream().findFirst();
        if (optionalStaffRole.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy role");
        }

        staffRoleRepository.delete(optionalStaffRole.get());

        return new ResponseObject<>(null, HttpStatus.OK, "Xóa thành công");
    }

    @Override
    public ResponseObject<?> getAllStaffNoProject() {
        return new ResponseObject<>(adStaffRepository.getAllStaffNoProject(), HttpStatus.OK, "Lấy dữ liệu thành viên không trong dự án thành công");
    }

    @Override
    public ResponseObject<?> createStaff(ADCreateStaffRequest request) {

        Staff staff = new Staff();
        staff.setCode(request.getCode());
        staff.setName(request.getName());
        staff.setEmailFe(request.getEmailFe());
        staff.setEmailFpt(request.getEmailFpt());
        staff.setStatus(EntityStatus.ACTIVE);
        adStaffRepository.save(staff);

        return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm nhân viên thành công");
    }

    @Override
    public ResponseObject<?> updateStaff(String id, ADCreateStaffRequest request) {

        Optional<Staff> optionalStaff = adStaffRepository.findById(id);
        if (optionalStaff.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thành nhân viên này");
        }

        Staff staff = optionalStaff.get();
        staff.setCode(request.getCode());
        staff.setName(request.getName());
        staff.setEmailFe(request.getEmailFe());
//        staff.setRole(optionalRole.get());
        staff.setEmailFpt(request.getEmailFpt());

        adStaffRepository.save(staff);

        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật thành công");

    }

    @Override
    public ResponseObject<?> deleteStaff(String id) {

        Optional<Staff> optionalStaff = adStaffRepository.findById(id);

        if (optionalStaff.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thành nhân viên này");
        }

        Staff staff = optionalStaff.get();
        staff.setStatus(EntityStatus.INACTIVE);
        adStaffRepository.save(staff);

        return new ResponseObject<>(null, HttpStatus.OK, "Xóa nhân viên thành công");
    }

    @Override
    public ResponseObject<?> detailStaff(String id) {
        return adStaffRepository.findByIdStaff(id).map(staff -> new ResponseObject<>(staff, HttpStatus.OK, "Lấy thông nhân viên thành công")).orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên có Id này"));
    }

    @Override
    public ResponseObject<?> staffByDepartmentFacility(String id) {

        return adStaffByDeparmentFacilityRepository.staffByDepartmentFacility(id).map(staff -> new ResponseObject<>(staff, HttpStatus.OK, "Lấy thông nhân viên thành công")).orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên có Id này"));
    }

    @Override
    public ResponseObject<?> getLogsImportStaff(int page, int size) throws FileNotFoundException {
        if (page < 1) {
            page = 1;
        }

        String staffId = userContextHelper.getCurrentUserId();

        List<HistoryImport> listLogRaw = historyLogUtils.getHistoryImportByFacilityIdAndStaffIdAndFileType(
                staffId, LogFileType.STAFF
        );

        if (listLogRaw.isEmpty()) {
            return ResponseObject.successForward(
                    PageableObject.of(new PageImpl<>(
                            Collections.emptyList(),
                            PageRequest.of(0, size),
                            0
                    )),
                    "Không có dữ liệu lịch sử thay đổi"
            );
        }

        List<HistoryImportResponse> responseList = listLogRaw.stream()
                .skip((long) (page - 1) * size)
                .limit(size)
                .map(history -> {
                    List<Role> roles = roleRepository.findRoleByStaff(staffId);
                    return new HistoryImportResponse(
                            history.getId(),
                            history.getFileName(),
                            history.getMessage(),
                            history.getType(),
                            history.getLogFileType(),
                            roles,
                            history.getStaff(),
                            history.getCreatedDate()
                    );
                })
                .toList();

        return ResponseObject.successForward(
                PageableObject.of(new PageImpl<>(
                        responseList,
                        PageRequest.of(page - 1, size),
                        listLogRaw.size()
                )),
                "Lấy lịch sử thay đổi thành công"
        );
    }


    @Override
    public Resource getAllCsv() throws IOException {
        File directory = new File(DIRECTORY);
        File[] csvFiles = directory.listFiles((dir, name) -> name.endsWith(".csv"));

        if (csvFiles == null || csvFiles.length == 0) {
            throw new FileNotFoundException("Không có file CSV nào để tải xuống.");
        }

        // Trả về file CSV đầu tiên (hoặc bạn có thể sửa logic để chọn file cụ thể)
        return new InputStreamResource(new FileInputStream(csvFiles[0]));
    }


}




