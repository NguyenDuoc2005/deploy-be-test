package udpm.hn.server.infrastructure.schedule.project;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import udpm.hn.server.entity.Project;
import udpm.hn.server.infrastructure.constant.StatusProject;
import udpm.hn.server.infrastructure.schedule.project.repository.ScheduleProjectRepository;
import udpm.hn.server.utils.Helper;

import java.util.Calendar;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectSchedule {

    private final ScheduleProjectRepository projectRepository;

    @Value("")
    private String cronTime;

//    @Scheduled(cron = "${schedule.project.cron}", zone = "UTC")
//    public void updateProjectStatus() {
//
//        log.info(getCurrentTime()+"");
//
//        List<Project> projects = projectRepository.findAll();
//
//        projects.forEach(project -> {
//            if(getCurrentTime() > project.getEndTime()) {
//                project.setStatusProject(StatusProject.DA_DIEN_RA);
//            }
//
//            projectRepository.save(project);
//            log.info("Cập nhật dự án {} sang trạng thái: Đã diễn ra", project.getId());
//        });
//    }

    private long getCurrentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

}
