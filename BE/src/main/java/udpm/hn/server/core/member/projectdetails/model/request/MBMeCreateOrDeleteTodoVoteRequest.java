package udpm.hn.server.core.member.projectdetails.model.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeCreateOrDeleteTodoVoteRequest extends MBMeTodoAndTodoListRequest{

    private String idMember;

    private String projectId;

    private String nameMember;

    private String email;

    private String idTodoCreateOrDelete;

    private String idUser;

}
