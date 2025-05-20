package udpm.hn.server.core.manage.report.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.report.model.request.MAReportRequest;

public interface MAReportService {

    ResponseObject<?> getAllReport(MAReportRequest request);

}
