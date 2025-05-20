package udpm.hn.server.core.manage.todo.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.Student;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class MATodoProjection {

    private String id;
    private String name;
    private String code;
    private String createdDate;
    private String priorityLevel;
    private String progress;


    private List<ParticipantDTO> participants = new ArrayList<>();

    public MATodoProjection(MATodoResponse response) {
        this.id = response.getId();
        this.name = response.getName();
        this.code = response.getCode();
        this.createdDate = response.getDate();
        this.priorityLevel = response.getPriorityLevel();
        this.progress = response.getProgress();
    }

    public void addParticipant(String name, String image) {
        if (name != null) {
            participants.add(new ParticipantDTO(name, image));
        }
    }
}
