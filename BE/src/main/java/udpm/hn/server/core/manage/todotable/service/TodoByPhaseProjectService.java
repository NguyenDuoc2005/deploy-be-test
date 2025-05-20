package udpm.hn.server.core.manage.todotable.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaTodoListProjectRequest;

import java.io.FileNotFoundException;

public interface TodoByPhaseProjectService {
    ResponseObject<?> getAllTodoByPhaseProject(String idProject, String idPhase);

    ResponseObject<?> getAllTodoListByPhaseProject(String idProject, String idPhase);

    ResponseObject<?> getIndex(String id);

    ResponseObject<?> getLogsImportTodo(int page, int size) throws FileNotFoundException;

}
