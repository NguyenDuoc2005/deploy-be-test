package udpm.hn.server.core.member.report.model.response;

import udpm.hn.server.core.common.base.IsIdentify;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.infrastructure.constant.StatusReport;

public interface MBReportResponse extends IsIdentify {

    String getIdReport();

    String getWordDoneTodayReport();

    String getObstaclesReport();

    String getWordPlanTomorrowReport();

    Long getReportTime();

    Long getCreateDate();

    StatusReport getStatusReport();

    String getIdProject();

}