package lorenzo.galacticcommandsystem.repository;

import lorenzo.galacticcommandsystem.model.CrewHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CrewHistoryRepository extends CrudRepository<CrewHistory, Long> {

    /**
     * Find all crew history records for a specific spaceship
     * @param spaceShipId the ID of the spaceship
     * @return List of CrewHistory records for the specified spaceship
     */
    List<CrewHistory> findBySpaceShipId(Long spaceShipId);

    /**
     * Find all crew history records for a specific spaceship, ordered by assignment date
     * @param spaceShipId the ID of the spaceship
     * @return List of CrewHistory records ordered by assignment date (most recent first)
     */
    List<CrewHistory> findBySpaceShipIdOrderByAssignmentDateDesc(Long spaceShipId);
}