package udpm.hn.server.core.manage.project.model.request;

import lombok.Getter;
import udpm.hn.server.core.common.base.PageableRequest;

@Getter
public class MaUserProjectRequest extends PageableRequest {

    private String idProject;
}
