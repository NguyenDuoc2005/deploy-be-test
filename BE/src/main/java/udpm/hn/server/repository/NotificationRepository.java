package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,String> {
}
