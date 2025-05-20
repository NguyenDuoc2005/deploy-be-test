package udpm.hn.server.core.member.projectdetails.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.response.MBMemberInfoTodoResponse;
import udpm.hn.server.core.member.projectdetails.repository.MBMeAssignRepository;
import udpm.hn.server.core.member.projectdetails.service.MBMeAssignService;

import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MBMeTodoVoteServiceImpl implements MBMeAssignService {

    private final MBMeAssignRepository mbMeTodoVoteRepository;

    @Override
    public ResponseObject<?> getAllMemberByIdTodo(String idTodo) {
         List<MBMemberInfoTodoResponse> mb = mbMeTodoVoteRepository.getAllMemberByIdTodo(idTodo);
        return new ResponseObject<>(mb, HttpStatus.OK,"lấy id thành viên dự án thành công");
    }

    @Override
    public ResponseObject<?> getTodoByIdStaffProject(String idStaff,String idProject) {
        List<String> idTodos = mbMeTodoVoteRepository.getIDTodoByIdStaffProject(idStaff, idProject);

        return new ResponseObject<>(idTodos, HttpStatus.OK, "Lấy id todo nhân viên dự án thành công");
    }
}
