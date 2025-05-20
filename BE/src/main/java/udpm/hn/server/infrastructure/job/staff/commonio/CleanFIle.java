package udpm.hn.server.infrastructure.job.staff.commonio;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class CleanFIle {
    @Scheduled(fixedRate = 30000) // Chạy mỗi 30 giây
    public void cleanUpXlsxFiles() {
        String directoryPath = "src/main/resources/staff-excel/";
        File directory = new File(directoryPath);

        File[] xlsxFiles = directory.listFiles((dir, name) -> name.endsWith(".xlsx"));

        boolean isDeleted = false;

        if (xlsxFiles != null && xlsxFiles.length > 0) {
            for (File file : xlsxFiles) {
                if (file.delete()) {
                    log.info("✅ Đã xóa file: " + file.getName());
                } else {
                    log.warn("❌ Không thể xóa file: " + file.getName());
                }
            }
        }
//        else {
//            log.info("⚠ Không có file .xlsx nào để xóa.");
//        }

    }

}
