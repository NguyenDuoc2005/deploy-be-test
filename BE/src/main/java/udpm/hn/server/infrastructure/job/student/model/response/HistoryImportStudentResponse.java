package udpm.hn.server.infrastructure.job.student.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.entity.Role;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.infrastructure.constant.LogFileType;
import udpm.hn.server.infrastructure.constant.LogType;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryImportStudentResponse {

    private String id;
    private String email;
    private String message;
    private List<Role> roles;
    private Long createdDate;

}
