package udpm.hn.server.core.manage.project.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.manage.project.model.response.MaPhaseProjectResponse;
import udpm.hn.server.repository.PhaseRepository;

import java.util.List;

@Repository
public interface MaPhaseProjectRepository extends PhaseRepository {
    @Query(value = """
        SELECT
         pp.id AS idPhase ,
         pp.name AS namePhaseProject
         FROM PhaseProject pp
         join Project p on pp.project.id = p.id
         where p.id = :id and pp.status=0
""")
    List<MaPhaseProjectResponse> getALl(String id);
}
