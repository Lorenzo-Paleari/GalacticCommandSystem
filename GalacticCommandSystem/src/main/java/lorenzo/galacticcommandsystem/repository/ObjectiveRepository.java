package lorenzo.galacticcommandsystem.repository;

import lorenzo.galacticcommandsystem.model.Objective;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Repository interface for managing Objective entities.
 */
public interface ObjectiveRepository extends CrudRepository<Objective, Long> {

    /**
     * Finds objectives by their associated mission ID.
     * @param missionId the ID of the mission
     * @return list of objectives for the given mission
     */
    List<Objective> findByMission_Id(Long missionId);

    /**
     * Finds objectives by their associated mission ID, eagerly loading their types.
     * @param missionId the ID of the mission
     * @return list of objectives for the given mission
     */
    @Query("SELECT DISTINCT o FROM Objective o LEFT JOIN FETCH o.types WHERE o.mission.id = :missionId")
    List<Objective> findByMission_IdWithTypes(@Param("missionId") Long missionId);

    /**
     * Finds objectives whose title contains the given keyword (case-insensitive).
     * @param keyword the search term to look for in objective titles
     * @return list of matching objectives
     */
    List<Objective> findByTitleContainingIgnoreCase(String keyword);
}
