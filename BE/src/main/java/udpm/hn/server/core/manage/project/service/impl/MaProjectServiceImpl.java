package udpm.hn.server.core.manage.project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.admin.project.model.request.ADProjectSTRequest;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaProjectCreateRequest;
import udpm.hn.server.core.manage.project.model.request.MaProjectSearchRequest;
import udpm.hn.server.core.manage.project.model.response.MAProjectDetailSummaryResponse;
import udpm.hn.server.core.manage.project.repository.MaProjectDFRepository;
import udpm.hn.server.core.manage.project.repository.MaProjectRepository;
import udpm.hn.server.core.manage.project.repository.MaProjectStaffRepository;
import udpm.hn.server.core.manage.project.repository.MaProjectStudentRepository;
import udpm.hn.server.core.manage.project.repository.MaTodoProjectRepository;
import udpm.hn.server.core.manage.project.service.MaProjectService;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.EntityStudentStatus;
import udpm.hn.server.infrastructure.constant.StatusProject;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.Helper;
import udpm.hn.server.utils.UserContextHelper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaProjectServiceImpl implements MaProjectService {

    private final MaProjectRepository maProjectRepository;

    private final MaProjectDFRepository projectDFRepository;

    private final MajorFacilityRepository majorFacilityRepository;

    private final CategoryRepository categoryRepository;

    private final MaProjectStudentRepository maProjectStudentRepository;

    private final MaProjectStaffRepository maProjectStaffRepository;

    private final StaffProjectRepository staffProjectRepository;

    private final ProjectRepository projectRepository;

    private final FacilityRepository facilityRepository;

    private final UserContextHelper userContextHelper;

    private final MaTodoProjectRepository maTodoProjectRepository;

    @Override
    public ResponseObject<?> getAllProject(MaProjectSearchRequest request) {
        StatusProject status = null;

        if (request.getStatus() != null) {
            status = StatusProject.values()[request.getStatus()];
        }

        Pageable page = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(maProjectRepository.getAllProject(page, request, status, userContextHelper), HttpStatus.OK, "Lấy thông tin project thành công");
    }

    @Override
    public ResponseObject<?> getAllDepartmentFacility(String idFacility) {
        return new ResponseObject<>(projectDFRepository.getAllDepartmentFacility(idFacility), HttpStatus.OK, "Lấy danh sách bộ môn cơ sở thành công");
    }

    @Override
    public ResponseObject<?> createProject(MaProjectCreateRequest request) {
        return null;
    }

    @Override
    public ResponseObject<?> updateProject(String idProject, MaProjectCreateRequest request) {
        log.info("updateProjetc: ", request.toString());
        Optional<Project> optionalProject = projectRepository.findById(idProject);
        if(optionalProject.isEmpty()){
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND,"Không tìm thấy dự án này");
        }

        Project project = optionalProject.get();
        Optional<MajorFacility> optionalMajorFacility = majorFacilityRepository.findById(request.getIdMajorFacility());

        if (optionalMajorFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên ngành cơ sở");
        }

        Optional<Category> optionalCategory = categoryRepository.findById(request.getIdCategory());
        if (optionalCategory.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thể loại");
        }

        Long startTime = Long.valueOf(request.getStartTime());
        Long endTime = Long.valueOf(request.getEndTime());

        project.setStartTime(startTime);
        project.setEndTime(endTime);
        Long currentTime = new Date().getTime();
        if (currentTime < startTime) {
            project.setStatusProject(StatusProject.CHUA_DIEN_RA);
        }
        if (currentTime >= startTime && currentTime <= endTime) {
            project.setStatusProject(StatusProject.DANG_DIEN_RA);
        }
        if (endTime < currentTime) {
            project.setStatusProject(StatusProject.DA_DIEN_RA);
        }

        project.setProgress(Float.parseFloat("0"));
        project.setCategory(optionalCategory.get());
        project.setBackgroundColor("#59a1e3");
        project.setName(request.getNameProject());
        project.setCode(request.getCodeProject());
        project.setDescriptions(request.getDescriptions());
        project.setMajorFacility(optionalMajorFacility.get());

        Project newProject = projectRepository.save(project);

        List<StaffProject> existingStaffProjects = staffProjectRepository.findByProject(project);

        Set<String> requestEmails = request.getListMembers().stream()
                .map(ADProjectSTRequest::getEmail)
                .collect(Collectors.toSet());

        List<StaffProject> toDelete = existingStaffProjects.stream()
                .filter(sp -> !requestEmails.contains(
                        sp.getStaff() != null ? sp.getStaff().getEmailFe() : sp.getStudent().getEmail()))
                .toList();

        if (!toDelete.isEmpty()) {
            toDelete.forEach(a-> a.setStatus(EntityStatus.INACTIVE));
        }

        List<ADProjectSTRequest> listMembers = request.getListMembers();

        List<StaffProject> newStaffProjects = new ArrayList<>();

        listMembers.forEach(item -> {
            if (item.getEmail().isEmpty() || item.getEmail() == null ) {
                return;
            }

            if (item.getEmail() != null && (item.getEmail().endsWith("@fe.edu.vn") || item.getEmail().endsWith("@fpt.edu.vn"))) {

                Optional<Staff> staff = maProjectStaffRepository.findByEmailFpt(item.getEmail());
                Optional<StaffProject> optionalStaffProject = staffProjectRepository.findByStaffAndProject(staff.get(),project);

                if(optionalStaffProject.isPresent()){
                    StaffProject staffProject = optionalStaffProject.get();

                    if (item.getRole().equals("QUAN_Li")) {
                        staffProject.setRole(EntityStudentStatus.QUAN_Li);
                    } else if (item.getRole().equals("DEV")) {
                        staffProject.setRole(EntityStudentStatus.DEV);
                    } else {
                        staffProject.setRole(EntityStudentStatus.TESTER);
                    }

                    staffProject.setStatus(EntityStatus.ACTIVE);
                    staffProjectRepository.save(staffProject);
                }else {

                    StaffProject staffProject1 = new StaffProject();

                    staffProject1.setStaff(staff.get());
                    staffProject1.setProject(newProject);
                    staffProject1.setStatus(EntityStatus.ACTIVE);
                    if (item.getRole().equals("QUAN_Li")) {
                        staffProject1.setRole(EntityStudentStatus.QUAN_Li);
                    } else if (item.getRole().equals("DEV")) {
                        staffProject1.setRole(EntityStudentStatus.DEV);
                    } else {
                        staffProject1.setRole(EntityStudentStatus.TESTER);
                    }
                    newStaffProjects.add(staffProject1);
                }

            } else {

                Optional<Student> optionalStudent = maProjectStudentRepository.findByEmail(item.getEmail());

                if(optionalStudent.isEmpty()){
                    Optional<Staff> staff = maProjectStaffRepository.findByEmailFpt(item.getEmail());
                    if (staff.isPresent()) {
                        Optional<StaffProject> optionalStaffProject = staffProjectRepository.findByStaffAndProject(staff.get(), project);
                        if(optionalStaffProject.isPresent()){
                            StaffProject studentProject = optionalStaffProject.get();
                            if (item.getRole().equals("QUAN_Li")) {
                                studentProject.setRole(EntityStudentStatus.QUAN_Li);
                            } else if (item.getRole().equals("DEV")) {
                                studentProject.setRole(EntityStudentStatus.DEV);
                            } else {
                                studentProject.setRole(EntityStudentStatus.TESTER);
                            }
                            studentProject.setStatus(EntityStatus.ACTIVE);
                            staffProjectRepository.save(studentProject);
                        }else {

                            StaffProject studentProject1 = new StaffProject();
                            studentProject1.setStaff(staff.get());
                            studentProject1.setProject(newProject);
                            if (item.getRole().equals("QUAN_Li")) {
                                studentProject1.setRole(EntityStudentStatus.QUAN_Li);
                            } else if (item.getRole().equals("DEV")) {
                                studentProject1.setRole(EntityStudentStatus.DEV);
                            } else {
                                studentProject1.setRole(EntityStudentStatus.TESTER);
                            }
                            studentProject1.setStatus(EntityStatus.ACTIVE);
                            newStaffProjects.add(studentProject1);
                        }
                    } else {

                    }

                }else{
                    Optional<StaffProject> optionalStaffProject = staffProjectRepository.findByStudentAndProject(optionalStudent.get(),project);
                    if(optionalStaffProject.isPresent()){
                        StaffProject studentProject = optionalStaffProject.get();
                        if (item.getRole().equals("QUAN_Li")) {
                            studentProject.setRole(EntityStudentStatus.QUAN_Li);
                        } else if (item.getRole().equals("DEV")) {
                            studentProject.setRole(EntityStudentStatus.DEV);
                        } else {
                            studentProject.setRole(EntityStudentStatus.TESTER);
                        }
                        studentProject.setStatus(EntityStatus.ACTIVE);
                        staffProjectRepository.save(studentProject);
                    }else {

                        StaffProject studentProject1 = new StaffProject();
                        studentProject1.setStudent(optionalStudent.get());
                        studentProject1.setProject(newProject);
                        if (item.getRole().equals("QUAN_Li")) {
                            studentProject1.setRole(EntityStudentStatus.QUAN_Li);
                        } else if (item.getRole().equals("DEV")) {
                            studentProject1.setRole(EntityStudentStatus.DEV);
                        } else {
                            studentProject1.setRole(EntityStudentStatus.TESTER);
                        }
                        studentProject1.setStatus(EntityStatus.ACTIVE);
                        newStaffProjects.add(studentProject1);
                    }
                }


            }
        });

        staffProjectRepository.saveAll(newStaffProjects);

        return new ResponseObject<>(null, HttpStatus.OK, "Update thành công dự án");

    }

    @Override
    public ResponseObject<?> detailProject(String idProject) {
        return maProjectRepository.findById(idProject).map(project -> new ResponseObject<>(project, HttpStatus.OK, "Lấy thông tin dự án thành công!")).orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy dự án!"));
    }

    @Override
    public ResponseObject<?> deleteProject(String idProject) {
        return null;
    }

    @Override
    public ResponseObject<?> getAllFacility() {
        return new ResponseObject<>(facilityRepository.findAll(), HttpStatus.OK, "Lấy dư liệu cơ sở thành công");
    }

    @Override
    public ResponseObject<?> getDetailSummaryProject(String id) {
        Optional<MAProjectDetailSummaryResponse> optionalProject = maProjectRepository.getDetailSummaryProject(id);

        if(optionalProject.isEmpty()) return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy dự án");

        MAProjectDetailSummaryResponse projectDetailSummary = optionalProject.get();

        return new ResponseObject<>(projectDetailSummary, HttpStatus.OK, "Lấy dữ liệu tổng kết dự án thành công");

    }

    @Override
    public ResponseObject<?> finishEarlyProject(String id) {
        Optional<Project> optionalProject = maProjectRepository.findProjectById(id);

        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();

            project.setStatusProject(StatusProject.DA_DIEN_RA);

            maProjectRepository.save(project);

            return new ResponseObject<>(project, HttpStatus.OK, "Kết thúc sớm dự án thành công");
        }
        return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy dự án");
    }
}