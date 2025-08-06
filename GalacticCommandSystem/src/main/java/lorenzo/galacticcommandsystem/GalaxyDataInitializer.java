package lorenzo.galacticcommandsystem;

import lorenzo.galacticcommandsystem.model.*;
import lorenzo.galacticcommandsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.time.LocalDate;


/**
 * Data initializer component responsible for bootstrapping the galaxy system with initial data.
 * This class creates sample space ships, crew members, planets, missions, and their relationships
 * when the application context is refreshed and no existing data is found.
 */
@Component
@RequiredArgsConstructor
public class GalaxyDataInitializer {

    private final SpaceShipRepository spaceShipRepository;
    private final EngineerRepository engineerRepository;
    private final NavigatorRepository navigatorRepository;
    private final MissionRepository missionRepository;
    private final ObjectiveRepository objectiveRepository;
    private final PlanetRepository planetRepository;
    private final MissionAssignmentRepository missionAssignmentRepository;
    private final ResourcesRepository resourcesRepository;
    private final CrewHistoryRepository crewHistoryRepository;

    /**
     * Event listener method that initializes the galaxy with sample data when the application context is refreshed.
     * This method only runs if no spaceships exist in the database, preventing duplicate data creation.
     */
    @EventListener
    public void onGalaxyBoot(ContextRefreshedEvent event) {
        // Only initialize if no spaceships exist
        if (spaceShipRepository.count() > 0) {
            return;
        }
        // Create planets
        Planet earth = Planet.builder()
                .name("Earth")
                .atmosphereType("Nitrogen-Oxygen")
                .position("0,0,0")
                .build();
        earth.addBuilding("Starfleet Command");
        earth.addBuilding("Space Dock");

        Planet vulcan = Planet.builder()
                .name("Vulcan")
                .atmosphereType("Nitrogen-Methane")
                .position("10,5,0")
                .build();

        Planet klingon = Planet.builder()
                .name("Qo'noS")
                .atmosphereType("Nitrogen-Methane")
                .position("-15,8,3")
                .build();

        Resource iron = Resource.builder().name("iron").build();

        earth.addResource(iron);
        // Save planets with their resources
        planetRepository.saveAll(List.of(earth, vulcan, klingon));

        // Create spaceships
        SpaceShip eagle = SpaceShip.builder()
                .name("Eagle")
                .isOperational(true)
                .build();

        SpaceShip defiant = SpaceShip.builder()
                .name("USS Defiant")
                .isOperational(true)
                .build();

        SpaceShip voyager = SpaceShip.builder()
                .name("USS Voyager")
                .isOperational(true)
                .build();

        spaceShipRepository.saveAll(List.of(eagle, defiant, voyager));

        // Create crew members
        Engineer scotty = Engineer.builder()
                .firstName("Riso")
                .lastName("Scotty")
                .techSpecialty("jumping on tables")
                .spaceShip(eagle)
                .build();
        scotty.addSkill("infinite rizz");
        scotty.addSkill("positive karma");

        Engineer torres = Engineer.builder()
                .firstName("Giorgia")
                .lastName("Torres")
                .techSpecialty("Java")
                .spaceShip(voyager)
                .build();
        torres.addSkill("overthinking");

        Navigator sulu = Navigator.builder()
                .firstName("young")
                .lastName("Sulu")
                .navigationBonus(150.0)
                .navigationExperienceYears(10)
                .successfulNavigations(50.0)
                .spaceShip(eagle)
                .build();

        Navigator paris = Navigator.builder()
                .firstName("baguette")
                .lastName("Paris")
                .navigationBonus(120.0)
                .navigationExperienceYears(8)
                .successfulNavigations(40.0)
                .spaceShip(voyager)
                .build();

        engineerRepository.saveAll(List.of(scotty, torres));
        navigatorRepository.saveAll(List.of(sulu, paris));

        // Create crew history entries
        CrewHistory scottyHistory = CrewHistory.builder()
                .crewMember(scotty)
                .spaceShip(eagle)
                .assignmentDate(LocalDate.now().minusYears(5))
                .build();
        CrewHistory torresHistory = CrewHistory.builder()
                .crewMember(torres)
                .spaceShip(voyager)
                .assignmentDate(LocalDate.now().minusYears(3))
                .build();
        CrewHistory suluHistory = CrewHistory.builder()
                .crewMember(sulu)
                .spaceShip(eagle)
                .assignmentDate(LocalDate.now().minusYears(4))
                .build();
        CrewHistory parisHistory = CrewHistory.builder()
                .crewMember(paris)
                .spaceShip(voyager)
                .assignmentDate(LocalDate.now().minusYears(2))
                .build();

        crewHistoryRepository.saveAll(List.of(scottyHistory, torresHistory, suluHistory, parisHistory));

        // Create missions
        Mission exploration = new Mission("Deep Space Exploration", 1000.0);
        exploration.setPlanet(vulcan);
        Objective explorationObjective = exploration.createAndAddObjective(
            "Survey Nebula",
            "Map and analyze the composition of the nearby nebula",
            EnumSet.of(ObjectiveType.EXPLORATION),
            "Nebula Sector",
            "Science Lab"
        );

        Mission diplomatic = new Mission("Peace Treaty Negotiation", 2000.0);
        diplomatic.setPlanet(klingon);
        Objective diplomaticObjective = diplomatic.createAndAddObjective(
            "Treaty Signing",
            "Facilitate the signing of a new peace treaty",
            EnumSet.of(ObjectiveType.BUILDING),
            "Great Hall",
            "Conference Room"
        );

        Mission rescue = new Mission("Rescue Operation", 1500.0);
        rescue.setPlanet(earth);
        Objective rescueObjective = rescue.createAndAddObjective(
            "Evacuate Colony",
            "Rescue colonists from a failing life support system",
            EnumSet.of(ObjectiveType.EXPLORATION),
            "Alpha Colony",
            "Habitat Dome"
        );

        // Save missions first
        missionRepository.saveAll(List.of(exploration, diplomatic, rescue));

        // Save objectives
        objectiveRepository.saveAll(List.of(explorationObjective, diplomaticObjective, rescueObjective));

        // Create and save mission assignments
        MissionAssignment enterpriseAssignment = new MissionAssignment(eagle, exploration, LocalDate.now().plusMonths(3));
        MissionAssignment defiantAssignment = new MissionAssignment(defiant, diplomatic, LocalDate.now().plusMonths(2));
        MissionAssignment voyagerAssignment = new MissionAssignment(voyager, rescue, LocalDate.now().plusMonths(1));

        missionAssignmentRepository.saveAll(List.of(enterpriseAssignment, defiantAssignment, voyagerAssignment));

        // Update missions with their assignments
        exploration.getAssignments().add(enterpriseAssignment);
        diplomatic.getAssignments().add(defiantAssignment);
        rescue.getAssignments().add(voyagerAssignment);

        // Update spaceships with their assignments
        eagle.getAssignments().add(enterpriseAssignment);
        defiant.getAssignments().add(defiantAssignment);
        voyager.getAssignments().add(voyagerAssignment);

        // Save the updated missions and spaceships
        missionRepository.saveAll(List.of(exploration, diplomatic, rescue));
        spaceShipRepository.saveAll(List.of(eagle, defiant, voyager));

        //TRY TO REMOVE FROM SPACESHIP AND ASSIGN TO ANOTHER
        scotty.removeFromSpaceShip();
        scotty.assignToSpaceShip(defiant);
        engineerRepository.save(scotty);
    }
}