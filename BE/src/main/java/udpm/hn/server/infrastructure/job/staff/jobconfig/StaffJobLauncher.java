package udpm.hn.server.infrastructure.job.staff.jobconfig;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import udpm.hn.server.infrastructure.job.staff.service.UploadStaffService;
import udpm.hn.server.utils.UserContextHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@NoArgsConstructor
public class StaffJobLauncher {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> scheduledTask;

    @Setter(onMethod_ = @Autowired, onParam_ = @Qualifier("saveStaffDataToDatabaseJob"))
    private Job job;

    @Setter(onMethod_ = @Autowired)
    private JobLauncher jobLauncher;

    @Setter(onMethod_ = @Autowired)
    private UserContextHelper userContextHelper;

    @Setter(onMethod_ = @Autowired)
    private UploadStaffService storageService;

    @Setter
    private String staffExcelFileName;

    private static final int DELAY = 0;

    private static final int PERIOD = 1;

    public void startTask() {
        if (scheduledTask == null || scheduledTask.isCancelled()) {
            scheduledTask = executorService.scheduleAtFixedRate(
                    this::launchExcelToDatabaseJob,
                    DELAY, PERIOD, TimeUnit.MINUTES
            );
        }
    }

    public void stopTask() {
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(false);
        }
    }

    @Async
    protected void launchExcelToDatabaseJob() {
        if (staffExcelFileName != null) {
            try {
                JobExecution jobExecution = jobLauncher.run(job, newExecution());
                ExitStatus exitStatus = jobExecution.getExitStatus();
                if (exitStatus.getExitCode().equals(ExitStatus.COMPLETED.getExitCode())) {
                    storageService.delete(staffExcelFileName);
                    stopTask();
                }
                if (exitStatus.getExitCode().equals(ExitStatus.FAILED.getExitCode())) {
                    log.error("Error launching excel to database job");
                }
            } catch (Exception e) {
                log.error("Error launching excel to database job", e);
            } finally {
                staffExcelFileName = null;
            }
        }
    }

    private JobParameters newExecution() {
        log.info("fullpath: ", staffExcelFileName);
        Map<String, JobParameter<?>> parameters = new HashMap<>();
        parameters.put("time", new JobParameter<>(new Date(), Date.class));
        parameters.put("staffExcelFileName", new JobParameter<>(staffExcelFileName, String.class));
        return new JobParameters(parameters);
    }



}
