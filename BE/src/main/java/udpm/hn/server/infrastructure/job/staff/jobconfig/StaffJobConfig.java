package udpm.hn.server.infrastructure.job.staff.jobconfig;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import udpm.hn.server.infrastructure.job.staff.commonio.StaffProcessor;
import udpm.hn.server.infrastructure.job.staff.commonio.StaffRowMapper;
import udpm.hn.server.infrastructure.job.staff.commonio.StaffWriter;
import udpm.hn.server.infrastructure.job.staff.model.dto.TransferStaffRole;
import udpm.hn.server.infrastructure.job.staff.model.request.StaffExcelRequest;

import java.io.File;

@Configuration
@Slf4j
@EnableTransactionManagement
public class StaffJobConfig {

    @Setter(onMethod_ = {@Autowired}, onParam_ = {@Qualifier("transactionManager")})
    private PlatformTransactionManager transactionManager;

    @Value("${file.upload.staff.path}")
    private String fullPath;

    @Bean
    @StepScope
    public ItemReader<StaffExcelRequest> excelStaffReader(@Value("#{jobParameters['staffExcelFileName']}") String path) {
        try {
            PoiItemReader<StaffExcelRequest> reader = new PoiItemReader<>();
            Resource resource = new FileSystemResource(new File(fullPath + "/" + path));

            if (!resource.exists()) {
                throw new RuntimeException("Could not read the file!");
            }

            reader.setResource(resource);
            reader.open(new ExecutionContext());
            reader.setLinesToSkip(1);
            reader.setRowMapper(rowMapper());
            return reader;
        } catch (Exception e) {
            log.error("Error reading excel file: {}", e.getMessage());
            return null;
        }
    }

    RowMapper<StaffExcelRequest> rowMapper() {
        return new StaffRowMapper();
    }

    @Bean
    @StepScope
    @Qualifier("staffExcelProcessor") // Đổi tên
    public ItemProcessor<StaffExcelRequest, TransferStaffRole> staffExcelProcessor() {
        StaffProcessor processor = new StaffProcessor();
//        processor.setFileName(fullPath);
        return processor;
    }


    @Bean
    @StepScope
    public ItemWriter<TransferStaffRole> excelStaffWriter() {
        return new StaffWriter();
    }


    @Bean
    Step firstStepStaff(
            @Qualifier("excelStaffReader") ItemReader<StaffExcelRequest> excelStaffReader,
            JobRepository jobRepository
    ) {
        return new StepBuilder("Import Staff To Database - Step", jobRepository)
                .<StaffExcelRequest, TransferStaffRole>chunk(10, transactionManager)
                .reader(excelStaffReader)
                .processor(staffExcelProcessor())
                .writer(excelStaffWriter())
                .transactionManager(transactionManager)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        System.out.println("Step bắt đầu: " + stepExecution.getStepName());
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        System.out.println("Step kết thúc với trạng thái: " + stepExecution.getExitStatus());
                        return stepExecution.getExitStatus();
                    }
                })
                .build();
    }



    @Bean
    public Job saveStaffDataToDatabaseJob(
            @Qualifier("firstStepStaff") Step step,
            JobRepository jobRepository
    ) {
        return new JobBuilder("Save-Data-Staff-To-Database-Job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

}
