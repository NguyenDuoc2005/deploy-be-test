package udpm.hn.server.core.manage.phase.model.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MACreatePhaseRequest {

    private String name;

    private String code;

    private Long startTime;

    private Long endTime;

    private String idProject;

}
