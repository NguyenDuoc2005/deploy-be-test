package udpm.hn.server.core.manage.report.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.report.model.request.MAReportRequest;
import udpm.hn.server.core.manage.report.repository.MAReportRepository;
import udpm.hn.server.core.manage.report.service.MAReportService;
import udpm.hn.server.utils.Helper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;


@Service
@RequiredArgsConstructor
@Slf4j
public class MAReportServiceImpl implements MAReportService {

    private final MAReportRepository maReportRepository;

    @Override
    public ResponseObject<?> getAllReport(MAReportRequest request) {

        Long timestamp =Long.valueOf(request.getTime());
        LocalDate date = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        Long dateStart = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long dateEnd = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Pageable page = Helper.createPageable(request,"createDate");
        return new ResponseObject<>(maReportRepository.getAllReport(page,dateStart,dateEnd, request.getIdProject()), HttpStatus.OK,"Lấy dũ liệu báo cáo thành công");

    }
}
