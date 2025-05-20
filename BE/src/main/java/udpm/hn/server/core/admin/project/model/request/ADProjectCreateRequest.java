package udpm.hn.server.core.admin.project.model.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import udpm.hn.server.entity.Staff;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class ADProjectCreateRequest {

    @NotBlank(message = "Tên không được để trống")
    private String nameProject;

    @NotBlank(message = "Mã không được để trống")
    private String codeProject;

    private String descriptions;

    @NotNull
    private Long startTime;

    @NotNull
    private Long endTime;

    private String idCategory;

    private String idMajorFacility;

    private List<ADProjectSTRequest> listMembers;

}
