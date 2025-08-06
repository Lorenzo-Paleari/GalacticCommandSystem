package lorenzo.galacticcommandsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This is an abstract base class for different types of crew members (e.g., Engineers, Navigators).
 * Each crew member has personal information, credits, and can be assigned to different locations.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
public abstract class CrewMember {

    /**
     * Unique identifier for the crew member.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The first name of the crew member.
     * Must be between 2 and 50 characters.
     */
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(length = 50)
    private String firstName;

    /**
     * The last name of the crew member.
     * Must be between 2 and 50 characters.
     */
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(length = 50)
    private String lastName;

    /**
     * The amount of credits owned by the crew member.
     * Cannot be negative and defaults to 0.0.
     */
    @Min(value = 0, message = "Credits cannot be negative")
    @NotNull(message = "Credits are required")
    @Builder.Default
    private double credits = 0.0;

    /**
     * The spaceship this crew member is currently assigned to.
     * Can be null if not assigned to any spaceship.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spaceShip_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private SpaceShip spaceShip;

    /**
     * The planet this crew member is currently assigned to.
     * Can be null if not assigned to any planet.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planet_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Planet planet;

    /**
     * The history of assignments for this crew member.
     * Maintains a chronological record of all assignments.
     */
    @OneToMany(mappedBy = "crewMember", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<CrewHistory> crewHistoryList = new ArrayList<>();

    /**
     * Gets the full name of the crew member by combining first and last name.
     *
     * @return the full name as a string
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Adds credits to the crew member's balance.
     *
     * @param amount the amount of credits to receive
     * @throws IllegalArgumentException if amount is negative
     */
    public void receiveCredits(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.credits += amount;
    }

    /**
     * Adds credits to the crew member's balance (integer version).
     *
     * @param amount the amount of credits to receive
     */
    public void receiveCredits(int amount) {
        receiveCredits((double) amount);
    }

    /**
     * Deducts credits from the crew member's balance.
     *
     * @param amount the amount of credits to spend
     * @throws IllegalArgumentException if amount is negative or insufficient credits
     */
    public void spendCredits(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (amount > credits) {
            throw new IllegalArgumentException("Insufficient credits");
        }
        this.credits -= amount;
    }

    /**
     * Assigns the crew member to a spaceship.
     * Updates both the crew member's assignment and the spaceship's crew list.
     * Also creates a new entry in the crew history.
     *
     * @param ship the spaceship to assign to
     * @throws IllegalArgumentException if ship is null
     * @throws IllegalStateException if already assigned to a planet or spaceship
     */
    public void assignToSpaceShip(SpaceShip ship) {
        if (ship == null) {
            throw new IllegalArgumentException("Spaceship cannot be null");
        }
        if (this.planet != null) {
            throw new IllegalStateException("CrewMember is already assigned to a planet");
        }
        if (this.spaceShip != null) {
            throw new IllegalStateException("CrewMember is already assigned to a spaceship");
        }

        this.spaceShip = ship;
        ship.getCrew().add(this);

        // Add to crew history
        CrewHistory history = CrewHistory.builder()
                .crewMember(this)
                .spaceShip(ship)
                .assignmentDate(LocalDate.now())
                .build();

        this.crewHistoryList.add(history);
        ship.getCrewHistoryList().add(history);
    }

    /**
     * Assigns the crew member to a planet.
     * Updates both the crew member's assignment and the planet's crew list.
     *
     * @param planet the planet to assign to
     * @throws IllegalArgumentException if planet is null
     * @throws IllegalStateException if already assigned to a spaceship or planet
     */
    public void assignToPlanet(Planet planet) {
        if (planet == null) {
            throw new IllegalArgumentException("Planet cannot be null");
        }
        if (this.spaceShip != null) {
            throw new IllegalStateException("CrewMember is already assigned to a spaceship");
        }
        if (this.planet != null) {
            throw new IllegalStateException("CrewMember is already assigned to a planet");
        }

        this.planet = planet;
        planet.getCrewMembers().add(this);
    }

    /**
     * Removes the crew member from their current spaceship assignment.
     * Updates both the crew member's assignment and the spaceship's crew list.
     */
    public void removeFromSpaceShip() {
        if (this.spaceShip != null) {
            this.spaceShip.getCrew().remove(this);  // rimuove da crew della nave
            this.spaceShip = null;
        }
    }

    /**
     * Removes the crew member from their current planet assignment.
     * Updates both the crew member's assignment and the planet's crew list.
     */
    public void removeFromPlanet() {
        if (this.planet != null) {
            this.planet.getCrewMembers().remove(this);
            this.planet = null;
        }
    }

    /**
     * Checks if the crew member is currently assigned to a spaceship.
     *
     * @return true if assigned to a spaceship, false otherwise
     */
    public boolean isAssignedToSpaceShip() {
        return spaceShip != null;
    }

    /**
     * Checks if the crew member is currently assigned to a planet.
     *
     * @return true if assigned to a planet, false otherwise
     */
    public boolean isAssignedToPlanet() {
        return planet != null;
    }

    /**
     * Gets a string representation of the crew member's current assignment.
     *
     * @return a string describing the current assignment location
     */
    public String getCurrentAssignment() {
        if (isAssignedToSpaceShip()) {
            return "Spaceship: " + spaceShip.getName();
        } else if (isAssignedToPlanet()) {
            return "Planet: " + planet.getName();
        }
        return "Unassigned";
    }
}