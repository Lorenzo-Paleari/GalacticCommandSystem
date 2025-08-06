package lorenzo.galacticcommandsystem.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;

public class MissionTest {

    private SpaceShip spaceShip;
    private Mission mission;

    @BeforeEach
    public void setup() {
        spaceShip = new SpaceShip();
        spaceShip.setName("Millennium Falcon");
        spaceShip.setOperational(true);
        spaceShip.setAssignments(new HashSet<>());

        mission = new Mission("Rescue Operation", 1500);
        mission.setAssignments(new HashSet<>());
    }

    @Test
    public void testAssignSpaceShip() {
        System.out.println("=== BEFORE ASSIGNMENT ===");
        printAssignments(spaceShip.getAssignments());

        assertTrue(spaceShip.getAssignments().isEmpty(), "Assignments dello spaceship dovrebbero essere vuoti");
        assertTrue(mission.getAssignments().isEmpty(), "Assignments della missione dovrebbero essere vuoti");

        mission.assignSpaceShip(spaceShip, LocalDate.now().plusDays(5));

        System.out.println("\n=== AFTER ASSIGNMENT ===");
        printAssignments(spaceShip.getAssignments());

        assertEquals(1, spaceShip.getAssignments().size());
        assertEquals(1, mission.getAssignments().size());

        MissionAssignment assignment = spaceShip.getAssignments().iterator().next();
        assertEquals(spaceShip, assignment.getSpaceShip());
        assertEquals(mission, assignment.getMission());
    }

    private void printAssignments(Set<MissionAssignment> assignments) {
        if (assignments == null || assignments.isEmpty()) {
            System.out.println("No assignments.");
        } else {
            assignments.forEach(a ->
                    System.out.println("Mission: " + a.getMission().getName() +
                            " | Start: " + a.getStartDate() +
                            " | End: " + a.getEndDate())
            );
        }
    }
}
