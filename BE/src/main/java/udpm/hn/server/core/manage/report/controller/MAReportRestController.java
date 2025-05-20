package udpm.hn.server.core.manage.report.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.core.manage.report.model.request.MAReportRequest;
import udpm.hn.server.core.manage.report.service.MAReportService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_MANAGE_REPORT)
public class MAReportRestController {

    private final MAReportService maReportService;

    @GetMapping
    public ResponseEntity<?> getAllReport( MAReportRequest request){
        return Helper.createResponseEntity(maReportService.getAllReport(request));
    }

}
