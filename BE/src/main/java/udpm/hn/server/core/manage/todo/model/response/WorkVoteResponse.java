package udpm.hn.server.core.manage.todo.model.response;

public interface WorkVoteResponse {
    String getName();         // Tên công việc
    String getMemberName();   // Tên thành viên
    Byte getLevel();        // Mức độ ưu tiên
}

