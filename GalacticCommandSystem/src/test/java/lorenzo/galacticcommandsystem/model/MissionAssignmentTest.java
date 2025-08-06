package lorenzo.galacticcommandsystem.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
public class MissionAssignmentTest {

    @Test
    public void testStartDateInFuture_shouldFailLogic() {
        SpaceShip ship = new SpaceShip();
        Mission mission = new Mission("test", 100);

        MissionAssignment assignment = new MissionAssignment(ship, mission, LocalDate.now().plusDays(5));

        System.out.println("Assignment startDate: " + assignment.getStartDate());
    }

    @Test
    public void testEndDateBeforeStartDate_shouldFailLogic() {
        SpaceShip ship = new SpaceShip();
        Mission mission = new Mission("test", 100);

        LocalDate end = LocalDate.now().minusDays(10);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new MissionAssignment(ship, mission, end);
        });

        assertTrue(exception.getMessage().contains("endDate must be after startDate"));
    }
}