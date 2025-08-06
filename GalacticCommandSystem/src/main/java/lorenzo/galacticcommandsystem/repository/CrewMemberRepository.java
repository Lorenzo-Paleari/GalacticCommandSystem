package lorenzo.galacticcommandsystem.repository;

import lorenzo.galacticcommandsystem.model.CrewMember;
import lorenzo.galacticcommandsystem.model.SpaceShip;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for managing CrewMember entities.
 * Provides operations and custom queries for crew members.
 */
public interface CrewMemberRepository extends CrudRepository<CrewMember, Long> {

    /**
     * Finds crew members by their last name.
     * @param lastName the last name to search for
     * @return list of matching crew members
     */
    List<CrewMember> findByLastName(String lastName);

    /**
     * Finds crew members assigned to a specific spaceship.
     * @param spaceShip the spaceship to search by
     * @return list of crew members on the specified spaceship
     */
    List<CrewMember> findBySpaceShip(SpaceShip spaceShip);

    /**
     * Retrieves all crew members and their spaceship or planet
     * @return list of all crew members
     */
    @Query("SELECT DISTINCT c FROM CrewMember c LEFT JOIN FETCH c.spaceShip LEFT JOIN FETCH c.planet")
    List<CrewMember> findAllCrewMembers();

    /**
     * Finds crew members with at least the specified minimum credits.
     * @param minCredits the minimum credit threshold
     * @return list of crew members meeting the credit requirement
     */
    @Query("select c from CrewMember c where c.credits >= :minCredits")
    List<CrewMember> findCrewWithMinCredits(@Param("minCredits") double minCredits);

    /**
     * Finds crew members by their spaceship's name.
     * @param shipName the spaceship name to search by
     * @return list of crew members on the specified spaceship
     */
    @Query("select c from CrewMember c where c.spaceShip.name = :shipName")
    List<CrewMember> findBySpaceShipName(@Param("shipName") String shipName);

    /**
     * Finds crew members by their spaceship's ID.
     * @param shipId the spaceship ID to search by
     * @return list of crew members on the specified spaceship
     */
    @Query("SELECT c FROM CrewMember c WHERE c.spaceShip.id = :shipId")
    List<CrewMember> findBySpaceShipId(@Param("shipId") Long shipId);

    @Query("SELECT DISTINCT s FROM SpaceShip s LEFT JOIN FETCH s.crew WHERE s.id = :shipId  ")
    List<CrewMember> findCrewMemberOnaSpaceship(@Param("shipId") Long shipId);
}

