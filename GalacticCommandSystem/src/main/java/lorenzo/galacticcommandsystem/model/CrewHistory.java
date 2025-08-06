package lorenzo.galacticcommandsystem.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * This class maintains a record of when crew members were assigned to specific spaceships,
 * allowing for tracking of crew movement and assignment history.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrewHistory {

    /**
     * Unique identifier for the crew history entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The crew member associated with this history entry.
     * Cannot be null.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "crew_member_id")
    private CrewMember crewMember;

    /**
     * The spaceship associated with this history entry.
     * Cannot be null.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "space_ship_id")
    private SpaceShip spaceShip;

    /**
     * The date when the crew member was assigned to the spaceship.
     * Cannot be null.
     */
    @Column(nullable = false)
    private LocalDate assignmentDate;
}