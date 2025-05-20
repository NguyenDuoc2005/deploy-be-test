package udpm.hn.server.core.member.report.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.report.model.request.MBReportRequest;
import udpm.hn.server.core.member.report.repository.MBReportRepository;
import udpm.hn.server.core.member.report.repository.MBTodoProjectRepository;
import udpm.hn.server.core.member.report.service.MBReportService;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.StatusReport;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.repository.StaffProjectRepository;
import udpm.hn.server.utils.Helper;
import udpm.hn.server.utils.UserContextHelper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MBReportServiceImpl implements MBReportService {

    private final MBReportRepository mbReportRepository;

    private final MBTodoProjectRepository todoProjectRepository;

    private final StaffProjectRepository staffProjectRepository;

    private final ProjectRepository projectRepository;

    private final UserContextHelper userContextHelper;


    @Override
    public ResponseObject<?> getAll(MBReportRequest request, String idProject) {
        if (request.getReportTime() != null) {
            request.setReportTimeEnd(request.getReportTime() + 86400000L);  // 86400000L là 1 ngày tính theo mili giây
        }

        Pageable pageable = Helper.createPageable(request, "createdDate");

        return new ResponseObject<>(
                PageableObject.of(mbReportRepository.getAll(pageable, idProject, userContextHelper, request)),
                HttpStatus.OK,
                "Lấy danh sách báo cáo thành công"
        );
    }


    @Override
    public ResponseObject<?> detail(String id, String idProject) {
        return new ResponseObject<>(
                mbReportRepository.detail(id, idProject, userContextHelper),
                HttpStatus.OK,
                "Lấy dữ liệu báo cáo thành công"
        );
    }

    @Override
    public ResponseObject<?> add(MBReportRequest request, String idProject) {
        Optional<StaffProject> optionalStaffProject = staffProjectRepository.getStaffProjectByStaffAndProject(userContextHelper, idProject);

        if (optionalStaffProject.isPresent()) {
            StaffProject staffProject = optionalStaffProject.get();
            Optional<Project> optionalProject = projectRepository.findProjectById(idProject);

            if (optionalProject.isPresent()) {
                Project project = optionalProject.get();
                Report report = new Report();
                report.setWorkDoneToday(request.getWorkDoneToday());
                report.setObstacles(request.getObstacles());
                report.setWorkPlanTomorrow(request.getWorkPlanTomorrow());
                report.setStatus(EntityStatus.ACTIVE);
                report.setReportTime(new Date().getTime());
                report.setStatusReport(StatusReport.DA_BAO_CAO);
                report.setStaffProject(staffProject);
                report.setProject(project);

                mbReportRepository.save(report);

                return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm báo cáo thành công");
            } else {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thông tin dự án");
            }
        } else {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thông tin dự án của nhân viên");
        }
    }

    @Override
    public ResponseObject<?> update(String id, MBReportRequest request) {
        Optional<Report> optionalReport = mbReportRepository.findById(id);
        if(optionalReport.isPresent()){
//            Optional<Todo> optionalTodo = todoProjectRepository.findById(request.getIdTodo());
//            if (optionalTodo.isPresent()){
                Report report = optionalReport.get();
                report.setWorkDoneToday(request.getWorkDoneToday());
                report.setObstacles(request.getObstacles());
                report.setWorkPlanTomorrow(request.getWorkPlanTomorrow());
                report.setStatus(EntityStatus.ACTIVE);
                report.setReportTime(new Date().getTime());
                report.setStatusReport(StatusReport.DA_BAO_CAO);
//                report.setTodo(optionalTodo.get());

                mbReportRepository.save(report);

                return new ResponseObject<>(null, HttpStatus.CREATED, "Sửa báo cáo thành công");
//            }
//            else {
//                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Task không tồn tại");
//            }
        } else {
            return new ResponseObject<>(null, HttpStatus.OK, "Không có báo cáo này");
        }
    }

    @Override
    public ResponseObject<?> detailByDate(Long reportTime, String idProject) {
        LocalDateTime startOfDay = Instant.ofEpochMilli(reportTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay();

        Long startTimestamp = startOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endTimestamp = startTimestamp + 86400000;

        String idUser = userContextHelper.getCurrentUserId();

        String reportId = mbReportRepository.findIdByReportTime(startTimestamp, endTimestamp,idUser, idProject);

        if (reportId != null) {
            return new ResponseObject<>(reportId, HttpStatus.OK, "Lấy ID báo cáo thành công");
        } else {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy báo cáo cho ngày này");
        }
    }

}
