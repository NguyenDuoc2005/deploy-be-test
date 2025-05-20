package udpm.hn.server.core.manage.phase.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.phase.model.request.MACreatePhaseRequest;
import udpm.hn.server.core.manage.phase.model.request.MAPhaseRequest;
import udpm.hn.server.core.manage.phase.model.request.MATodoByPhase;
import udpm.hn.server.core.manage.phase.repository.MAPhaseRepository;
import udpm.hn.server.core.manage.phase.service.MAPhaseService;
import udpm.hn.server.core.manage.todo.model.response.MATodoResponse;
import udpm.hn.server.core.manage.todo.repository.MAListTodoProjectRepository;
import udpm.hn.server.core.manage.todo.repository.MATodoRepository;
import udpm.hn.server.entity.PhaseProject;
import udpm.hn.server.entity.PhaseTodoProject;
import udpm.hn.server.entity.Project;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.entity.TodoList;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.StatusListTodo;
import udpm.hn.server.repository.PhaseTodoProjectRepository;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.repository.ToDoRepository;
import udpm.hn.server.utils.Helper;
import udpm.hn.server.utils.StatusPhase;
import udpm.hn.server.utils.TodoListHelper;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class MAPhaseServiceImpl implements MAPhaseService {

    private final MAPhaseRepository maPhaseRepository;

    private final ToDoRepository toDoRepository;

    private final PhaseTodoProjectRepository phaseTodoProjectRepository;

    private final MAListTodoProjectRepository maListTodoProjectRepository;

    private final TodoListHelper todoListHelper;

    private final ProjectRepository projectRepository;

    private final MATodoRepository maTodoRepository;

    @Override
    public ResponseObject<?> getAllPhase(MAPhaseRequest request) {
        Pageable pageable = Helper.createPageable(request, "statusPhase");
        return new ResponseObject<>(maPhaseRepository.getAllPhaseProject(pageable, request.getIdProject()), HttpStatus.OK, "Lấy dự liệu giai đoạn thành công");
    }

    @Override
    public ResponseObject<?> findByPhaseStatus(String idProject) {

        Optional<PhaseProject> optionalPhaseProject = maPhaseRepository.findAll()
                .stream()
                .filter(ps -> ps.getStatusPhase() == StatusPhase.DANG_LAM)
                .filter(ps -> ps.getProject().getId().equals(idProject)) // lọc thêm theo idProject
                .findFirst();

        if (optionalPhaseProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy");
        }

        log.info(optionalPhaseProject.get() + " DỮ LIỆU");
        return new ResponseObject<>(optionalPhaseProject.get(), HttpStatus.OK, "Tìm thấy");
    }

    @Override
    public ResponseObject<?> getAllPhase(String idProject) {
        return new ResponseObject<>(maPhaseRepository.getAllPhaseProjectStatistics(idProject), HttpStatus.OK, "Lấy dữ liệu thành công");
    }

    @Override
    public ResponseObject<?> getAllSprint(MAPhaseRequest request) {
        StatusPhase statusPhase = null;
        if (request.getStatus() != null) {
            statusPhase = StatusPhase.values()[Integer.valueOf(request.getStatus())];
        }

        Pageable pageable = Helper.createPageable(request, "statusPhase");
        return new ResponseObject<>(maPhaseRepository.getAllSprintProject(pageable, request.getIdProject(), statusPhase, request.getTime(), request.getSearch()), HttpStatus.OK, "Lấy dự liệu giai đoạn thành công");

    }

    @Override
    public ResponseObject<?> getAllPhaseByProjectId(String id) {
        return new ResponseObject<>(maPhaseRepository.getAllPhaseByProjectId(id));
    }

    @Override
    public ResponseObject<?> createPhase(MACreatePhaseRequest request) {

        Optional<Project> optionalProject = projectRepository.findById(request.getIdProject());
        if (optionalProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy project");
        }

        long currentTime = System.currentTimeMillis();

        long oneWeekAgo = currentTime + (7L * 24 * 60 * 60 * 1000);

        PhaseProject phaseProject = new PhaseProject();
        phaseProject.setCode(request.getCode());
        phaseProject.setName(request.getName());
        phaseProject.setStartTime(currentTime);
        phaseProject.setEndTime(oneWeekAgo);
        phaseProject.setProject(optionalProject.get());
        phaseProject.setStatusPhase(StatusPhase.CHUA_HOAN_THANH);
        maPhaseRepository.save(phaseProject);

        for (int i = 0; i < 3; i++) {
            TodoList newTodoList = new TodoList();
            newTodoList.setCode(todoListHelper.genCodeTodoList(request.getIdProject()) + "_" + phaseProject.getId());
            newTodoList.setName(i == 0 ? "Chưa làm" : i == 1 ? "Đang làm" : "Đã xong");
            Byte index = todoListHelper.genIndexTodoList(request.getIdProject());
            newTodoList.setIndexTodoList(index != null ? index : 0);
            newTodoList.setProject(optionalProject.get());
            newTodoList.setPhaseProject(phaseProject);
            newTodoList.setStatusListTodo(i == 0 ? StatusListTodo.CHUA_BAT_DAU : i == 1 ? StatusListTodo.DANG_DIEN_RA : StatusListTodo.DA_XONG);
            maListTodoProjectRepository.save(newTodoList);
        }


        int countPhaseCompleted = maPhaseRepository.countPhaseProjectByIdProject(phaseProject.getProject().getId(), StatusPhase.DA_HOAN_THANH);
        int countAllPhase = maPhaseRepository.getAllPhaseByProjectId(optionalProject.get().getId()).size();

        float progress = ((float) countPhaseCompleted / countAllPhase) * 100;

        log.info(" update progess project : {} {} {}", countPhaseCompleted, countAllPhase, progress);

        optionalProject.get().setProgress(progress);

        projectRepository.save(optionalProject.get());

        return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm giai đoạn thành công");

    }

    @Override
    public ResponseObject<?> createPhaseTOdoList(MACreatePhaseRequest request) {
        return null;
    }

    //        TodoList todoList = maListTodoProjectRepository.findByProjectAndPhase(request.getIdProject(), request.getIdPhase()).stream().findFirst().orElseGet(() -> {
//            TodoList newTodoList = new TodoList();
//            newTodoList.setCode(phaseProject.getName());
//            newTodoList.setName(phaseProject.getCode());
//            Byte index = todoListHelper.genIndexTodoList(request.getIdProject());
//            newTodoList.setIndexTodoList(index != null ? index : 0);
//            newTodoList.setProject(project);
//            newTodoList.setPhaseProject(phaseProject);
//            return maListTodoProjectRepository.save(newTodoList);
//
//
//        });
    @Override
    public ResponseObject<?> createTodoByPhase(MATodoByPhase request) {

        log.info("Dữ liệu request khi kéo todo và phase: {}", request.toString());

        if (request.getIdTodo() == null || request.getIdTodo().isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Danh sách công việc không được để trống");
        }

        Pageable page = Helper.createPageable(request, "createDate");

        Page<MATodoResponse> todoByPhase = maTodoRepository.getAllTodoByPhaseProject(page, request.getIdPhase(), "");

        Project project = projectRepository.findById(request.getIdProject()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy project"));

        PhaseProject phaseProject = maPhaseRepository.findById(request.getIdPhase()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giai đoạn"));


        TodoList todoList = maListTodoProjectRepository.findTodoListNotStratedByProjectAndPhase(request.getIdProject(), request.getIdPhase(), StatusListTodo.CHUA_BAT_DAU).stream().findFirst().orElseGet(() -> {
            TodoList newTodoList = new TodoList();
            newTodoList.setCode(todoListHelper.genCodeTodoList(request.getIdProject()) + request.getIdPhase());
            newTodoList.setName("Chưa làm");
            Byte index = todoListHelper.genIndexTodoList(request.getIdProject());
            newTodoList.setIndexTodoList(index != null ? index : 0);
            newTodoList.setProject(project);
            newTodoList.setPhaseProject(phaseProject);
            newTodoList.setStatusListTodo(StatusListTodo.CHUA_BAT_DAU);
            return maListTodoProjectRepository.save(newTodoList);
        });

        // nếu chưa có thì bắt đầu từ 1


        List<Todo> todos = toDoRepository.findAllById(request.getIdTodo());
        if (todos.size() != request.getIdTodo().size()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Một hoặc nhiều công việc không tồn tại");
        }


        todos.forEach(todo -> {
            if (!todoList.equals(todo.getTodoList())) {
                todo.setTodoList(todoList);
                todo.setIndexTodo(Short.valueOf(request.getIndex()));
                toDoRepository.save(todo);
            }

            if (!phaseTodoProjectRepository.existsDistinctByTodoAndPhaseProject(todo, phaseProject)) {
                PhaseTodoProject phaseTodoProject1 = new PhaseTodoProject();
                phaseTodoProject1.setPhaseProject(phaseProject);
                phaseTodoProject1.setTodo(todo);
                phaseTodoProjectRepository.save(phaseTodoProject1);
            }
        });

        return new ResponseObject<>(null, HttpStatus.OK, "Thêm công việc vào giai đoạn thành công");
    }


    @Override
    public ResponseObject<?> updatePhase(String id, MACreatePhaseRequest request) {

        Optional<PhaseProject> optionalPhaseProject = maPhaseRepository.findById(id);
        if (optionalPhaseProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "không tìm thấy giai đoạn công việc này");
        }
        Long startTime = Long.valueOf(request.getStartTime());
        Long endTime = Long.valueOf(request.getEndTime());

        PhaseProject phaseProject = optionalPhaseProject.get();
        phaseProject.setName(request.getName());
        phaseProject.setCode(request.getCode());
        phaseProject.setStartTime(startTime);
        phaseProject.setEndTime(endTime);

        maPhaseRepository.save(phaseProject);

        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật thành công giai đoạn");
    }

    @Override
    public ResponseObject<?> updateStatusPhase(String id, Integer indexStatus) {

        StatusPhase status = null;
        if (indexStatus != null) {
            status = StatusPhase.values()[indexStatus];
        }

        Optional<PhaseProject> optionalPhaseProject = maPhaseRepository.findById(id);
        if (optionalPhaseProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "không tìm thấy giai đoạn công việc này");
        }

        PhaseProject phaseProject = optionalPhaseProject.get();
        phaseProject.setStatusPhase(status);

        maPhaseRepository.save(phaseProject);

        // cập nhật progress
        Optional<Project> existProject = projectRepository.findProjectById(phaseProject.getProject().getId());

        if (existProject.isPresent()) {
            int countPhaseCompleted = maPhaseRepository.countPhaseProjectByIdProject(phaseProject.getProject().getId(), StatusPhase.DA_HOAN_THANH);
            int countAllPhase = maPhaseRepository.getAllPhaseByProjectId(existProject.get().getId()).size();

            float progress = ((float) countPhaseCompleted / countAllPhase) * 100;

            log.info(" update progess project : {} {} {}", countPhaseCompleted, countAllPhase, progress);

            existProject.get().setProgress(progress);

            projectRepository.save(existProject.get());
        }

        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật thành công giai đoạn");

    }

    @Override
    public ResponseObject<?> deletePhase(String id) {

        Optional<PhaseProject> optionalPhaseProject = maPhaseRepository.findById(id);
        if (optionalPhaseProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy giai đoạn");
        }

        PhaseProject phaseProject = optionalPhaseProject.get();
        phaseProject.setStatus(EntityStatus.INACTIVE);

        maPhaseRepository.save(phaseProject);
        return new ResponseObject<>(null, HttpStatus.OK, "Xóa thành công giai đoạn");

    }

    @Override
    public ResponseObject<?> deleteTodoByPhase(String id) {
        Optional<Todo> optionalTodo = toDoRepository.findById(id);
        if (optionalTodo.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy công việc");
        }

//        Optional<Project> optionalProject = projectRepository.findById(request.getIdProject());
//        if(optionalProject.isEmpty()){
//            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"không tìm thấy project");
//        }
//
//        TodoList todoList = maListTodoProjectRepository.findByProject(request.getIdProject())
//                .stream().findFirst().orElse(null);
//
//        // Nếu chưa có TodoList, tạo mới
//        if (todoList == null) {
//            todoList = new TodoList();
//            todoList.setCode(optionalTodo.get().getCode());
//            todoList.setName(optionalTodo.get().getName());
//            todoList.setIndexTodoList(todoListHelper.genIndexTodoList(request.getIdProject()));
//
//            todoList.setProject(optionalProject.get());
//            maListTodoProjectRepository.save(todoList);
//        }
//

        Optional<PhaseTodoProject> optionalPhaseTodoProject = phaseTodoProjectRepository.findByTodo(optionalTodo.get());
        if (optionalPhaseTodoProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy giai đoạn ");
        }
        phaseTodoProjectRepository.delete(optionalPhaseTodoProject.get());

        return new ResponseObject<>(null, HttpStatus.OK, "Xoá thành công");

    }
}
