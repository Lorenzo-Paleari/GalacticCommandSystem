package lorenzo.galacticcommandsystem.repository;

import lorenzo.galacticcommandsystem.model.Mission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Mission entities.
 * Provides operations and custom queries for space missions.
 */
public interface MissionRepository extends CrudRepository<Mission, Long> {

    /**
     * Finds missions by exact name match.
     * @param name the mission name to search for
     * @return list of missions with the specified name
     */
    List<Mission> findByName(String name);

    /**
     * Finds missions with funding greater than the specified amount.
     * @param minFunding the minimum funding threshold
     * @return list of missions exceeding the funding threshold
     */
    List<Mission> findByFundingGreaterThan(double minFunding);

    /**
     * Finds a mission by ID and eagerly loads its assignments.
     * @param id the mission ID
     * @return Optional containing the mission with assignments if found
     */
    @Query("select m from Mission m join fetch m.assignments where m.id = :id")
    Optional<Mission> findByIdWithAssignments(@Param("id") Long id);

    /**
     * Finds missions that have at least one objective defined.
     * @return list of missions with objectives
     */
    @Query("select m from Mission m where size(m.objectives) > 0")
    List<Mission> findMissionsWithObjectives();

    /**
     * Retrieves all missions.
     * @return list of all missions
     */
    @Query("SELECT m FROM Mission m")
    List<Mission> findAllMissions();

    /**
     * Finds missions not currently assigned to the specified spaceship.
     * @param shipId the spaceship ID to check assignments against
     * @return list of unassigned missions for the spaceship
     */
    @Query("SELECT m FROM Mission m WHERE m.id NOT IN (" +
            "SELECT ma.mission.id FROM MissionAssignment ma WHERE ma.spaceShip.id = :shipId)")
    List<Mission> findUnassignedMissionsForShip(@Param("shipId") Long shipId);

    @Query("SELECT DISTINCT m FROM Mission m " +
           "LEFT JOIN FETCH m.objectives " +
           "LEFT JOIN FETCH m.planet " +
           "WHERE m.id IN :ids")
    List<Mission> findAllByIdsWithData(@Param("ids") Collection<Long> ids);
}
