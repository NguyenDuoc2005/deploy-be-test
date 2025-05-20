package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeUpdateProgressTodoRequest  extends MBMeTodoAndTodoListRequest{

    @NotNull
    @NotBlank
    @NotEmpty
    private String id;

    private Float progress;

    @NotNull
    @NotBlank
    @NotEmpty
    private String periodId;

    @NotNull
    @NotBlank
    @NotEmpty
    private String projectId;

    @NotNull
    @NotBlank
    @NotEmpty
    private String idUser;

}
