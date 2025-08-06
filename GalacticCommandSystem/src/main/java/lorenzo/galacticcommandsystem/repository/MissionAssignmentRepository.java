package lorenzo.galacticcommandsystem.repository;

import lorenzo.galacticcommandsystem.model.MissionAssignment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing MissionAssignment entities.
 * Provides operations and custom queries for mission assignments.
 */
public interface MissionAssignmentRepository extends CrudRepository<MissionAssignment, Long> {

    /**
     * Finds mission assignments by spaceship ID.
     * @param spaceShipId the spaceship ID to search by
     * @return list of assignments for the specified spaceship
     */
    List<MissionAssignment> findBySpaceShip_Id(Long spaceShipId);

    /**
     * Finds mission assignments by mission ID.
     * @param missionId the mission ID to search by
     * @return list of assignments for the specified mission
     */
    List<MissionAssignment> findByMission_Id(Long missionId);

    /**
     * Finds ongoing mission assignments (those without an end date).
     * @return list of active assignments
     */
    List<MissionAssignment> findByEndDateIsNull();

    /**
     * Finds mission assignments that started before the specified date.
     * @param date the cutoff date
     * @return list of assignments started before the given date
     */
    List<MissionAssignment> findByStartDateBefore(LocalDate date);

    /**
     * Finds currently active mission assignments (ongoing at the specified date).
     * @param now the reference date to check activity against
     * @return list of active assignments
     */
    @Query("select ma from MissionAssignment ma where ma.startDate <= :now and (ma.endDate is null or ma.endDate >= :now)")
    List<MissionAssignment> findActiveAssignments(@Param("now") LocalDate now);

    /**
     * Finds distinct spaceship names assigned to a specific mission.
     * @param missionId the mission ID to search by
     * @return list of spaceship names assigned to the mission
     */
    @Query("SELECT DISTINCT ma.spaceShip.name FROM MissionAssignment ma WHERE ma.mission.id = :missionId")
    List<String> findSpaceShipNamesByMissionId(@Param("missionId") Long missionId);
}

