package udpm.hn.server.core.manage.project.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaProjectCreateRequest;
import udpm.hn.server.core.manage.project.model.request.MaProjectSearchRequest;

public interface MaProjectService {

    ResponseObject<?> getAllProject(MaProjectSearchRequest request);

    ResponseObject<?> getAllDepartmentFacility(String idFacility);

    ResponseObject<?> createProject( MaProjectCreateRequest request);

    ResponseObject<?> updateProject( String idProject,MaProjectCreateRequest request);

    ResponseObject<?> detailProject( String idProject );

    ResponseObject<?> deleteProject( String idProject );

    ResponseObject<?> getAllFacility();

    ResponseObject<?> getDetailSummaryProject(String id);

    ResponseObject<?> finishEarlyProject(String id);
}
