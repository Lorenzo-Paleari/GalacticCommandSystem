package lorenzo.galacticcommandsystem.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CrewHistoryTest {

    //testa che alla creazione di una history aggiorna anche le associazioni
    //non c'è bisogno di controlli che stia lavorando o che la data si maggiore ?
    @Test
    public void testEngineerAssignmentAndHistoryCreation() {
        SpaceShip ship = SpaceShip.builder()
                .name("USS Voyager")
                .isOperational(true)
                .build();

        Engineer engineer = Engineer.builder()
                .firstName("Montgomery")
                .lastName("Scott")
                .credits(150)
                .techSpecialty("Warp Drive")
                .build();

        engineer.assignToSpaceShip(ship);

        assertEquals(1, engineer.getCrewHistoryList().size(), "La lista CrewHistory dell'Engineer deve contenere 1 elemento.");
        assertEquals(1, ship.getCrewHistoryList().size(), "La lista CrewHistory della SpaceShip deve contenere 1 elemento.");

        CrewHistory historyFromEngineer = engineer.getCrewHistoryList().get(0);
        CrewHistory historyFromShip = ship.getCrewHistoryList().get(0);

        assertSame(historyFromEngineer, historyFromShip, "Entrambe le CrewHistory devono riferirsi allo stesso oggetto.");

        System.out.println("Engineer History: " + engineer.getCrewHistoryList());
        System.out.println("SpaceShip History: " + ship.getCrewHistoryList());

        assertEquals(ship, historyFromEngineer.getSpaceShip());
        assertEquals(engineer, historyFromEngineer.getCrewMember());
        assertEquals(LocalDate.now(), historyFromEngineer.getAssignmentDate());
    }
}