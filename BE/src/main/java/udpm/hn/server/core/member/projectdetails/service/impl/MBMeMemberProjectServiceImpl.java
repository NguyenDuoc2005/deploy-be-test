package udpm.hn.server.core.member.projectdetails.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateOrDeleteTodoVoteRequest;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeMemberProjectResponse;
import udpm.hn.server.core.member.projectdetails.repository.MBMeMemberProjectRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeTodoRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeAssignRepository;
import udpm.hn.server.core.member.projectdetails.service.MBMeMemberProjectService;
import udpm.hn.server.entity.Assign;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.entity.TodoVote;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MBMeMemberProjectServiceImpl implements MBMeMemberProjectService {

    private final SimpMessagingTemplate messagingTemplate;

    private final MBMeMemberProjectRepository mbMeMemberProjectRepository;

    private final MBMeTodoRepository mbMeTodoRepository;

    private final MBMeAssignRepository mbMeAssignRepository;



    @Override
    public ResponseObject<?> getAllMemberProject(String idProject,String idTodo) {
        List<MBMeMemberProjectResponse> lstMemberProject = mbMeMemberProjectRepository.findAllMembersWithAddedFlag(idProject,idTodo);
        return new ResponseObject<>(lstMemberProject,HttpStatus.OK,"getAllMemberProject");
    }

    @Override
    public ResponseObject<?> joinTodoVoteMemberProject(MBMeCreateOrDeleteTodoVoteRequest request) {
        String idStaffProject = mbMeMemberProjectRepository
                .findIdByMemberIdAndProjectId(request.getIdMember(), request.getProjectId());

        Optional<Todo> todo = mbMeTodoRepository.findById(request.getIdTodo());

        Optional<StaffProject> staffProject = mbMeMemberProjectRepository.findById(idStaffProject);

        // Kiểm tra tồn tại Todo và StaffProject
        if (todo.isEmpty() || staffProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Todo hoặc StaffProject không tồn tại");
        }

        //  Kiểm tra đã join chưa
        boolean alreadyJoined = mbMeAssignRepository.existsByTodoAndStaffProject(todo.get(), staffProject.get());
        if (alreadyJoined) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT, "Đã vote Todo này rồi");
        }

        Assign todoVote = new Assign();
        todoVote.setTodo(todo.get());
        todoVote.setStaffProject(staffProject.get());
        mbMeAssignRepository.save(todoVote);

        return new ResponseObject<>(todoVote,HttpStatus.OK,"joinTodoVoteMemberProject");
    }

    @Override
    public ResponseObject<?> outTodoVoteMemberProject(MBMeCreateOrDeleteTodoVoteRequest request) {
        String idStaffProject = mbMeMemberProjectRepository
                .findIdByMemberIdAndProjectId(request.getIdMember(), request.getProjectId());

        Optional<Todo> todo = mbMeTodoRepository.findById(request.getIdTodo());

        Optional<StaffProject> staffProject = mbMeMemberProjectRepository.findById(idStaffProject);

        // Kiểm tra tồn tại Todo và StaffProject
        if (todo.isEmpty() || staffProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Todo hoặc StaffProject không tồn tại");
        }

        // Kiểm tra xem đã vote chưa (đã tồn tại TodoVote chưa)
        Optional<String> idTodoVote = mbMeAssignRepository
                .findIdAssignByIdTodoAndIdStaffProject(request.getIdTodo(), idStaffProject);

        if (idTodoVote.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Người dùng chưa vote nên không thể out");
        }

        mbMeAssignRepository.deleteById(idTodoVote.get());
//        messagingTemplate.convertAndSend("/topic/out-todoVote", "Out thành công");
        return new ResponseObject<>(null,HttpStatus.OK,"outTodoVoteMemberProject");

    }

    @Override
    public ResponseObject<?> getAllFilterMemberProject(String idProject) {
        List<MBMeMemberProjectResponse> lstMemberProject = mbMeMemberProjectRepository.getAllFilterMemberProject(idProject);
        return new ResponseObject<>(lstMemberProject,HttpStatus.OK,"getAllFilterMemberProject");
    }

}
