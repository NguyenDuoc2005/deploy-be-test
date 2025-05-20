package udpm.hn.server.core.member.projectdetails.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.PhaseProject;

@Repository
public interface MBMePhaseProjectRepository extends JpaRepository<PhaseProject, String> {
}
