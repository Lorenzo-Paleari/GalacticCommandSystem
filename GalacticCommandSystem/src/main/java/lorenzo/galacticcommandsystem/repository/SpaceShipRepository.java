package lorenzo.galacticcommandsystem.repository;

import lorenzo.galacticcommandsystem.model.SpaceShip;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing SpaceShip entities.
 */
public interface SpaceShipRepository extends CrudRepository<SpaceShip, Long> {

    /**
     * Finds spaceships by exact name match.
     * @param name the spaceship name to search for
     * @return list of matching spaceships
     */
    List<SpaceShip> findByName(String name);

    /**
     * Finds spaceships that have at least one assignment.
     * @return list of spaceships with assignments
     */
    @Query("select s from SpaceShip s where size(s.assignments) > 0")
    List<SpaceShip> findSpaceShipWithAssignments();

    /**
     * Finds a spaceship by ID and eagerly loads its crew.
     * @param id the spaceship ID
     * @return Optional containing the spaceship with crew if found
     */
    @Query("SELECT s FROM SpaceShip s LEFT JOIN FETCH s.crew WHERE s.id = :id")
    Optional<SpaceShip> findByIdWithCrew(@Param("id") Long id);

    /**
     * Finds a spaceship by ID and eagerly loads its crew and assignments.
     * @param id the spaceship ID
     * @return Optional containing the spaceship with crew and assignments if found
     */
    @Query("SELECT s FROM SpaceShip s LEFT JOIN FETCH s.crew LEFT JOIN FETCH s.assignments WHERE s.id = :id")
    Optional<SpaceShip> findByIdWithCrewAndAssignments(@Param("id") Long id);

    /**
     * Finds a spaceship by ID and eagerly loads all its related data.
     * @param id the spaceship ID
     * @return Optional containing the spaceship with all related data if found
     */
    @Query("SELECT DISTINCT s FROM SpaceShip s " +
           "LEFT JOIN FETCH s.crew " +
           "LEFT JOIN FETCH s.assignments a " +
           "LEFT JOIN FETCH a.mission m " +
           "LEFT JOIN FETCH m.objectives " +
           "WHERE s.id = :id")
    Optional<SpaceShip> findByIdWithAllData(@Param("id") Long id);
}