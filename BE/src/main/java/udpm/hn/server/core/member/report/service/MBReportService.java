package udpm.hn.server.core.member.report.service;

import jakarta.validation.Valid;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.report.model.request.MBReportRequest;

public interface MBReportService {

    ResponseObject<?> getAll(MBReportRequest request, String idProject);

    ResponseObject<?> detail (String id, String idProject);

    ResponseObject<?> add(@Valid MBReportRequest request, String idProject);

    ResponseObject<?> update(String id, @Valid MBReportRequest request);

    ResponseObject<?> detailByDate(Long report, String idProject);
}
