package udpm.hn.server.infrastructure.job.staff.commonio;

import com.nimbusds.jose.util.StandardCharset;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import udpm.hn.server.entity.MajorFacility;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffMajorFacility;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.GlobalVariablesConstant;
import udpm.hn.server.infrastructure.constant.LogFileType;
import udpm.hn.server.infrastructure.job.staff.model.dto.TransferStaffRole;
import udpm.hn.server.infrastructure.job.staff.model.request.StaffExcelRequest;
import udpm.hn.server.infrastructure.job.staff.repository.ConfigMajorFacilityCustomRepository;
import udpm.hn.server.infrastructure.job.staff.repository.ConfigStaffCustomRepository;
import udpm.hn.server.repository.StaffMajorFacilityRepository;
import udpm.hn.server.utils.HistoryLogUtils;
import udpm.hn.server.infrastructure.config.global.GlobalVariables;
import udpm.hn.server.utils.UserContextHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
@Transactional
@EnableScheduling
public class StaffProcessor implements ItemProcessor<StaffExcelRequest, TransferStaffRole> {

    @Setter(onMethod_ = {@Autowired})
    private ConfigStaffCustomRepository staffCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private ConfigMajorFacilityCustomRepository majorFacilityCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private StaffMajorFacilityRepository staffMajorFacilityRepository;

    @Setter(onMethod_ = {@Autowired})
    private HistoryLogUtils historyLogUtils;

    @Setter(onMethod_ = {@Autowired})
    private GlobalVariables globalVariables;

    @Setter(onMethod_ = {@Autowired})
    private UserContextHelper userContextHelper;

    private boolean isSuccessLogged = false;

    @Override
    public TransferStaffRole process(StaffExcelRequest item) throws Exception {

        if (isHeaderRow(item)) {
            return null;
        }

        List<String> errors = new ArrayList<>();
        List<String> errorFields = new ArrayList<>();

        Optional<Staff> existingStaffOpt = staffCustomRepository.findByCodeAndStatus(item.getStaffCode(), EntityStatus.ACTIVE);
        if (existingStaffOpt.isPresent()) {
            errors.add("Đã tồn tại mã nhân viên: " + item.getStaffCode());
            errorFields.add(" - " + item.getStaffCode());
            logErrors(item, errors, errorFields);
            throw new RuntimeException("Dữ liệu không hợp lệ: " + errors.get(0));
        }

        if (!isValidStaffCode(item)) {
            errors.add("Email không hợp lệ.");
            errorFields.add(" - " + item.getEmailFpt() + " - " + item.getEmailFe());
            logErrors(item, errors, errorFields);
            throw new RuntimeException("Dữ liệu không hợp lệ: " + errors.get(0));
        }

        if (item.getDepartment() == null || item.getDepartment().trim().isEmpty()) {
            errors.add("Bộ môn không hợp lệ.");
            errorFields.add(" - " + item.getDepartment());
            logErrors(item, errors, errorFields);
            throw new RuntimeException("Dữ liệu không hợp lệ: " + errors.get(0));
        }

        if (item.getMajor() == null || item.getMajor().trim().isEmpty()) {
            errors.add("Chuyên ngành không hợp lệ.");
            errorFields.add(" - " + item.getMajor());
            logErrors(item, errors, errorFields);
            throw new RuntimeException("Dữ liệu không hợp lệ: " + errors.get(0));
        }

        if (item.getFacility() == null || item.getFacility().trim().isEmpty()) {
            errors.add("Cơ sở không hợp lệ.");
            errorFields.add(" - " + item.getFacility());
            logErrors(item, errors, errorFields);
            throw new RuntimeException("Dữ liệu không hợp lệ: " + errors.get(0));
        }

        Optional<MajorFacility> majorFacility = fetchMajorFacility(item.getDepartment(), item.getMajor(), item.getFacility());
        if (majorFacility.isEmpty()) {
            errors.add("Không tìm thấy chuyên ngành-cơ sở."+item.getMajor()+ " - " + item.getDepartment()+" - " + item.getFacility());
            errorFields.add(item.getMajor()+ " - " + item.getDepartment()+" - " + item.getFacility());
            logErrors(item, errors, errorFields);
            throw new RuntimeException("Dữ liệu không hợp lệ: " + errors.get(0));
        }

        if (!errors.isEmpty()) {
            logErrors(item, errors, errorFields);
            throw new RuntimeException("Dữ liệu không hợp lệ: " + String.join("; ", errors));
        }


        Staff staff = existingStaffOpt.orElseGet(() -> createNewStaff(item));
        StaffMajorFacility staffMajorFacility = new StaffMajorFacility();
        staffMajorFacility.setStatus(EntityStatus.ACTIVE);
        staffMajorFacility.setMajorFacility(majorFacility.orElse(null));
        staffMajorFacility.setStaff(staff);

        return new TransferStaffRole(staff, staffMajorFacility);
    }

    private boolean isHeaderRow(StaffExcelRequest item) {
        return "Mã Nhân Viên".equalsIgnoreCase(item.getStaffCode()) ||
                "Email FPT".equalsIgnoreCase(item.getEmailFpt()) ||
                "Họ và Tên".equalsIgnoreCase(item.getName()) ||
                "Email FE".equalsIgnoreCase(item.getEmailFe()) ||
                "Chuyên ngành".equalsIgnoreCase(item.getMajor()) ||
                "Bộ môn".equalsIgnoreCase(item.getDepartment()) ||
                "Cơ sở".equalsIgnoreCase(item.getFacility());
    }

    private Optional<MajorFacility> fetchMajorFacility(String department, String major, String facility) {
        List<MajorFacility> majorFacilities = majorFacilityCustomRepository.getMajorFacilities(department, major, facility);
        return majorFacilities.isEmpty() ? Optional.empty() : Optional.of(majorFacilities.get(0));
    }

    private boolean isValidStaffCode(StaffExcelRequest item) {
        return item.getEmailFpt() != null && item.getEmailFe() != null &&
                (item.getEmailFpt().toLowerCase().endsWith("@fpt.edu.vn") || item.getEmailFpt().toLowerCase().endsWith("@gmail.com")) &&
                (item.getEmailFe().toLowerCase().endsWith("@fe.edu.vn") || item.getEmailFe().toLowerCase().endsWith("@gmail.com"));
    }

    private Staff createNewStaff(StaffExcelRequest item) {
        Staff newStaff = new Staff();
        newStaff.setId(UUID.randomUUID().toString());
        newStaff.setCode(item.getStaffCode());
        newStaff.setName(item.getName());
        newStaff.setEmailFpt(item.getEmailFpt());
        newStaff.setEmailFe(item.getEmailFe());
        newStaff.setStatus(EntityStatus.ACTIVE);
        return newStaff;
    }

    private void logErrors(StaffExcelRequest item, List<String> errors, List<String> errorFields) {
        String fileName = errorFields.stream().collect(Collectors.joining("_"));
        String errorContent ;
        if (errors.stream().anyMatch(e -> e.contains("mã nhân viên"))) {
            errorContent = item.getName() + " - " + item.getEmailFpt() + ": " + String.join("; ", errors);
        } else if (errors.stream().anyMatch(e -> e.contains("Email không hợp lệ."))) {
            errorContent = item.getStaffCode() + " - " + item.getName()+": " + String.join("; ", errors);
        } else {
            errorContent = item.getEmailFpt() + " - " + item.getStaffCode() + ": " + String.join("; ", errors);
        }

        logErrorRecordToCSV(fileName, errorContent);
        logErrorRecordToHistory(fileName, errorContent);
    }

    private void logSuccess() {
        if (isSuccessLogged) {
            return;
        }

        String successMessage = "Danh sách nhân viên đã được lưu";
        logErrorRecordToHistory("", successMessage);
        isSuccessLogged = true;
    }

    private void logErrorRecordToCSV(String fileName, String content) {
        String csvFilePath = "src/main/resources/staff-excel/log.csv";

        File file = new File(csvFilePath);
        boolean isNewFile = !file.exists();

        try (FileWriter writer = new FileWriter(csvFilePath, true);
             OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(csvFilePath, true), StandardCharset.UTF_8)) {

            // Nếu là file mới, thêm BOM để Excel nhận diện UTF-8
            if (isNewFile) {
                out.write("\uFEFF");
            }

            out.write(content + "\n");
            out.flush(); // Đảm bảo dữ liệu được ghi vào file

        } catch (IOException e) {
            log.error("Failed to write error record to file: ", e);
        }
    }



    private void logErrorRecordToHistory(String fileName, String content) {
        historyLogUtils.logErrorRecord(
                content,
                fileName,
                (String) globalVariables.getGlobalVariable(GlobalVariablesConstant.CURRENT_USER_ID),
                LogFileType.STAFF
        );
    }
}