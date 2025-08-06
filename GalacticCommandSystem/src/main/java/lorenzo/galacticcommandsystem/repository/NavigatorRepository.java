package lorenzo.galacticcommandsystem.repository;

import lorenzo.galacticcommandsystem.model.Navigator;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for managing Navigator entities.
 */
public interface NavigatorRepository extends CrudRepository<Navigator, Long> {

    /**
     * Finds navigators by last name.
     * @param lastName the last name to search for
     * @return list of matching navigators
     */
    List<Navigator> findByLastName(String lastName);

    /**
     * Finds navigators with more experience than specified years.
     * @param years minimum years of experience
     * @return list of qualified navigators
     */
    List<Navigator> findByNavigationExperienceYearsGreaterThan(int years);

    /**
     * Finds navigators by their assigned spaceship name.
     * @param spaceShipName name of the spaceship
     * @return list of navigators assigned to the spaceship
     */
    List<Navigator> findBySpaceShip_Name(String spaceShipName);

    /**
     * Finds navigators with at least the specified number of successful navigations.
     * @param minSuccesses minimum successful navigations required
     * @return list of qualified navigators
     */
    @Query("select n from Navigator n where n.successfulNavigations >= :minSuccesses")
    List<Navigator> findBySuccessfulNavigationsGreaterThanEqual(@Param("minSuccesses") double minSuccesses);

    /**
     * Finds navigators with navigation bonus greater than specified value.
     * @param minBonus minimum bonus required
     * @return list of qualified navigators
     */
    List<Navigator> findByNavigationBonusGreaterThan(Double minBonus);

    /**
     * Finds navigators not assigned to any spaceship.
     * @return list of unassigned navigators
     */
    @Query("select n from Navigator n where n.spaceShip is null")
    List<Navigator> findUnassignedNavigators();

    /**
     * Finds navigators eligible for critical missions based on experience and success score.
     * @return list of eligible navigators
     */
    @Query("select n from Navigator n where (n.navigationExperienceYears * 1.5 + n.successfulNavigations * 2) > 50")
    List<Navigator> findEligibleForCriticalMissions();
}

