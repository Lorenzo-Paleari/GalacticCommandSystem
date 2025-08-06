package lorenzo.galacticcommandsystem.repository;

import lorenzo.galacticcommandsystem.model.Engineer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for managing Engineer entities.
 * Provides operations and custom queries for engineers.
 */
public interface EngineerRepository extends CrudRepository<Engineer, Long> {

    /**
     * Finds engineers by their technical specialty.
     * @param techSpecialty the technical specialty to search for
     * @return list of engineers with the specified specialty
     */
    List<Engineer> findByTechSpecialty(String techSpecialty);

    /**
     * Finds engineers who possess a specific skill.
     * @param skill the skill to search for
     * @return list of engineers with the specified skill
     */
    @Query("select e from Engineer e join e.skills s where s = :skill")
    List<Engineer> findBySkill(@Param("skill") String skill);

    /**
     * Finds engineers assigned to a spaceship with the given name.
     * @param shipName the spaceship name to search by
     * @return list of engineers on the specified spaceship
     */
    @Query("select e from Engineer e where e.spaceShip.name = :shipName")
    List<Engineer> findBySpaceShipName(@Param("spaceShipName") String shipName);
}
