package lorenzo.galacticcommandsystem.repository;

import lorenzo.galacticcommandsystem.model.Planet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Planet entities.
 */
public interface PlanetRepository extends CrudRepository<Planet, Long> {

    /**
     * Finds a planet by its exact name.
     * @param name the planet name to search for
     * @return Optional containing the planet if found
     */
    Optional<Planet> findByName(String name);

    /**
     * Finds a planet by ID and eagerly loads its missions.
     * @param id the planet ID
     * @return Optional containing the planet with missions if found
     */
    @Query("SELECT p FROM Planet p LEFT JOIN FETCH p.missions WHERE p.id = :id")
    Optional<Planet> findByIdWithMissions(@Param("id") Long id);

    /**
     * Retrieves all planets.
     * @return list of all planets
     */
    @Query("SELECT p FROM Planet p")
    List<Planet> findAllPlanets();
}
