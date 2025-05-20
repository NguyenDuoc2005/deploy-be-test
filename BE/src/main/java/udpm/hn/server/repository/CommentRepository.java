package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
}
