package lorenzo.galacticcommandsystem.controller;

import lombok.RequiredArgsConstructor;
import lorenzo.galacticcommandsystem.model.*;
import lorenzo.galacticcommandsystem.repository.*;
import lorenzo.galacticcommandsystem.view.MainView;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class MainController {

    private final SpaceShipRepository spaceShipRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MissionAssignmentRepository missionAssignmentRepository;
    private final MissionRepository missionRepository;
    private final ObjectiveRepository objectiveRepository;
    private final PlanetRepository planetRepository;
    private final CrewHistoryRepository crewHistoryRepository;

    /**
     * Initializes the main UI view.
     */
    public void init() {
        SwingUtilities.invokeLater(() -> {
            MainView view = new MainView(this);
            view.setVisible(true);
        });
    }

    /**
     * Retrieves all spaceships.
     * @return list of all spaceships
     */
    public List<SpaceShip> getAllSpaceShips() {
        return (List<SpaceShip>) spaceShipRepository.findAll();
    }

    /**
     * Creates a new spaceship with given name.
     * @param name the name of the spaceship
     */
    public void createNewSpaceShip(String name) {
        SpaceShip newShip = SpaceShip.builder()
                .name(name)
                .isOperational(true)
                .build();
        spaceShipRepository.save(newShip);
    }

    /**
     * Gets spaceship with its crew by ID.
     * @param id the spaceship ID
     * @return optional spaceship with crew
     */
    public Optional<SpaceShip> getSpaceShipWithCrew(Long id) {
        return spaceShipRepository.findByIdWithCrew(id);
    }

    /**
     * Gets crew members for a specific spaceship.
     * @param spaceShipId the spaceship ID
     * @return list of crew members
     */
    public List<CrewMember> getCrewForSpaceShip(Long spaceShipId) {
        return spaceShipRepository.findByIdWithCrew(spaceShipId)
                .map(s -> s.getCrew().stream().collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    /**
     * Gets missions assigned to a spaceship.
     * @param spaceShipId the spaceship ID
     * @return list of missions
     */
    public List<Mission> getMissionsForSpaceShip(Long spaceShipId) {
        List<MissionAssignment> assignments = missionAssignmentRepository.findBySpaceShip_Id(spaceShipId);
        return assignments.stream()
                .map(MissionAssignment::getMission)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Gets spaceship by ID with crew and assignments.
     * @param id the spaceship ID
     * @return spaceship or null if not found
     */
    public SpaceShip getSpaceShipById(Long id) {
        return spaceShipRepository.findByIdWithAllData(id)
                .orElseThrow(() -> new RuntimeException("Spaceship not found with id: " + id));
    }

    /**
     * Gets crew members by spaceship ID.
     * @param shipId the spaceship ID
     * @return list of crew members
     */
    public List<CrewMember> getCrewMembersByShipId(Long shipId) {
        return crewMemberRepository.findBySpaceShipId(shipId);
    }

    /**
     * Gets missions by spaceship ID.
     * @param shipId the spaceship ID
     * @return list of missions
     */
    public List<Mission> getMissionsByShipId(Long shipId) {
        SpaceShip ship = spaceShipRepository.findByIdWithCrewAndAssignments(shipId)
                .orElseThrow(() -> new RuntimeException("Spaceship not found with id: " + shipId));
        
        List<Long> missionIds = ship.getAssignments().stream()
                .map(assignment -> assignment.getMission().getId())
                .collect(Collectors.toList());
        
        return missionRepository.findAllByIdsWithData(missionIds);
    }

    /**
     * Assigns a mission to a spaceship.
     * @param missionId the mission ID
     * @param shipId the spaceship ID
     * @param activationInfo activation status information
     */
    @Transactional //salva, se qualcosa va male, rollback
    public void assignMissionToShip(Long missionId, Long shipId, String activationInfo) {
        Mission mission = missionRepository.findById(missionId).orElseThrow(() ->
                new IllegalArgumentException("Mission not found"));
        SpaceShip ship = spaceShipRepository.findById(shipId).orElseThrow(() ->
                new IllegalArgumentException("Ship not found"));

        MissionAssignment assignment = new MissionAssignment(ship, mission, null);
        mission.setActiveStatus(activationInfo);
        missionRepository.save(mission);
        missionAssignmentRepository.save(assignment);
    }

    /**
     * Retrieves all missions.
     * @return list of all missions
     */
    public List<Mission> getAllMissions() {
        return missionRepository.findAllMissions();
    }

    /**
     * Gets unassigned missions for a specific spaceship.
     * @param shipId the spaceship ID
     * @return list of unassigned missions
     */
    public List<Mission> getUnassignedMissionsForShip(Long shipId) {
        return missionRepository.findUnassignedMissionsForShip(shipId);
    }

    /**
     * Gets spaceship names assigned to a mission.
     * @param missionId the mission ID
     * @return list of spaceship names
     */
    public List<String> getSpaceShipNamesByMissionId(Long missionId) {
        return missionAssignmentRepository.findSpaceShipNamesByMissionId(missionId);
    }

    /**
     * Gets objectives for a specific mission.
     * @param missionId the mission ID
     * @return list of objectives
     */
    public List<Objective> getObjectivesByMissionId(Long missionId) {
        return objectiveRepository.findByMission_IdWithTypes(missionId);
    }

    /**
     * Gets mission by ID.
     * @param id the mission ID
     * @return mission or null if not found
     */
    public Mission getMissionById(Long id) {
        return missionRepository.findById(id).orElse(null);
    }

    /**
     * Retrieves all planets.
     * @return list of all planets
     */
    public List<Planet> getAllPlanets() {
        return planetRepository.findAllPlanets();
    }

    /**
     * Gets planet by ID.
     * @param id the planet ID
     * @return optional planet
     */
    public Optional<Planet> getPlanetById(Long id) {
        return planetRepository.findById(id);
    }

    /**
     * Creates a new planet.
     * @param planet the planet to create
     */
    public void createPlanet(Planet planet) {
        planetRepository.save(planet);
    }

    /**
     * Deletes a planet by ID.
     * @param id the planet ID
     */
    public void deletePlanet(Long id) {
        planetRepository.deleteById(id);
    }

    /**
     * Creates a new mission.
     * @param mission the mission to create
     */
    public void createMission(Mission mission) {
        missionRepository.save(mission);
    }

    /**
     * Retrieves all crew members.
     * @return list of all crew members
     */
    public List<CrewMember> getAllCrewMembers() {
        return crewMemberRepository.findAllCrewMembers();
    }

    /**
     * Get spaceship history by spaceship ID
     * @param spaceShipId the ID of the spaceship
     * @return List of CrewHistory records for the specified spaceship
     */
    public List<CrewHistory> getSpaceShipHistory(Long spaceShipId) {
        return crewHistoryRepository.findBySpaceShipIdOrderByAssignmentDateDesc(spaceShipId);
    }
}