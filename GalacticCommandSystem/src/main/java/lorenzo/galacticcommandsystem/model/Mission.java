package lorenzo.galacticcommandsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

/**
 * A mission can be assigned to spaceships and contains multiple objectives.
 * Each mission has a name, funding, state, and can be associated with a specific planet.
 */
@Entity
@Data
public class Mission {

    /**
     * Unique identifier for the mission.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The name of the mission. Must be unique and cannot be blank.
     */
    @NotBlank(message = "Mission name is required")
    @Column(unique = true)
    private String name;

    /**
     * The funding allocated to the mission. Must be at least 100 units.
     */
    @Min(value = 100, message = "Mission funding must be at least 100")
    private double funding;

    /**
     * Set of mission assignments associated with this mission.
     * Each assignment represents a spaceship assigned to this mission.
     * persist: salva anche gli assignments nel database
     * remove: se rimuovi la missione rimuove anche gli assignments dal database
     * lazy: carica solo i mission assignments quando sono richiesti
     */
    @OneToMany(mappedBy = "mission", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<MissionAssignment> assignments = new HashSet<>();

    /**
     * Set of objectives that need to be completed for this mission.
     */
    @OneToMany(mappedBy = "mission", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Objective> objectives = new HashSet<>();

    /**
     * Current state of the mission (ACTIVE or INACTIVE).
     */
    @NotNull(message = "Mission state is required")
    @Enumerated(EnumType.STRING)
    private MissionState missionState;

    /**
     * Information about why the mission was activated.
     */
    @Column(length = 1000)
    private String activationInfo;

    /**
     * Information about why the mission was deactivated.
     */
    @Column(length = 1000)
    private String deactivationInfo;

    /**
     * The planet associated with this mission, if any.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "planet_id", nullable = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Planet planet;

    /**
     * Creates a new mission with the specified name and funding.
     *
     * @param name The name of the mission
     * @param funding The initial funding for the mission
     * @throws IllegalArgumentException if name is null/blank or funding is less than 100
     */
    public Mission(String name, double funding) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Mission name cannot be null or blank");
        }
        if (funding < 100) {
            throw new IllegalArgumentException("Mission funding must be at least 100");
        }
        this.name = name;
        this.funding = funding;
        this.missionState = MissionState.INACTIVE;
        this.deactivationInfo = "never assigned";
    }

    /**
     * Default constructor that initializes a mission in INACTIVE state.
     * serve al framework per il recupero di dati da database
     */
    public Mission() {
        this.missionState = MissionState.INACTIVE;
        this.deactivationInfo = "never assigned";
    }

    /**
     * Creates a new mission with specified name, funding, and initial state.
     *
     * @param name The name of the mission
     * @param funding The initial funding for the mission
     * @param initialState The initial state of the mission
     * @param stateInfo Information about the initial state
     */
    public Mission(String name, double funding, MissionState initialState, String stateInfo) {
        this(name, funding);
        if (initialState == MissionState.ACTIVE) {
            setActiveStatus(stateInfo);
        } else {
            setInactiveStatus(stateInfo);
        }
    }

    /**
     * Ensures mission has a valid state before persisting to database.
     */
    @PrePersist
    public void prePersist() {
        if (missionState == null) {
            missionState = MissionState.INACTIVE;
            deactivationInfo = "never assigned";
        }
    }

    /**
     * Removes a specified amount of funding from the mission.
     *
     * @param amount The amount of funding to remove
     * @throws IllegalArgumentException if amount is not positive or exceeds available funding
     */
    public void removeFunding(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to remove must be positive");
        }
        if (amount > this.funding) {
            throw new IllegalArgumentException("Cannot remove more funding than available");
        }
        this.funding -= amount;
    }

    /**
     * Adds additional funding to the mission.
     *
     * @param amount The amount of funding to add
     * @throws IllegalArgumentException if amount is not positive or exceeds 50% of current funding
     */
    public void addFunding(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to add must be positive");
        }
        double maxIncrease = this.funding * 0.5;
        if (amount > maxIncrease) {
            throw new IllegalArgumentException("Cannot add more than 50% of current funding");
        }
        this.funding += amount;
    }

    /**
     * Sets the mission to ACTIVE state with the provided information.
     *
     * @param info Information about why and when the mission was activated
     * @throws IllegalArgumentException if info is null or blank
     */
    public void setActiveStatus(String info) {
        if (info == null || info.isBlank()) {
            throw new IllegalArgumentException("activation info cannot be null or blank");
        }
        this.missionState = MissionState.ACTIVE;
        this.activationInfo = info;
        this.deactivationInfo = null;
    }

    /**
     * Sets the mission to INACTIVE state with the provided information.
     *
     * @param info Information about why and when the mission was deactivated
     * @throws IllegalArgumentException if info is null or blank
     */
    public void setInactiveStatus(String info) {
        if (info == null || info.isBlank()) {
            throw new IllegalArgumentException("deactivation info cannot be null or blank");
        }
        this.missionState = MissionState.INACTIVE;
        this.deactivationInfo = info;
        this.activationInfo = null;
    }

    /**
     * Associates a planet with this mission.
     *
     * @param planet The planet to associate with the mission
     */
    public void setPlanet(Planet planet) {
        if (planet != null) {
            this.planet = planet;
            planet.getMissions().add(this);
        }
    }

    /**
     * Removes the association with the current planet.
     */
    public void removePlanet() {
        if (this.planet != null) {
            this.planet.getMissions().remove(this);
            this.planet = null;
        }
    }

    /**
     * Removes a spaceship's assignment from this mission.
     *
     * @param spaceShip The spaceship to remove from the mission
     * @throws IllegalArgumentException if spaceShip is null
     */
    public void removeAssignment(SpaceShip spaceShip) {
        if (spaceShip == null) {
            throw new IllegalArgumentException("SpaceShip cannot be null");
        }
        
        MissionAssignment toRemove = assignments.stream()
            .filter(assignment -> assignment.getSpaceShip().equals(spaceShip))
            .findFirst()
            .orElse(null);

        if (toRemove != null) {
            assignments.remove(toRemove);
            spaceShip.getAssignments().remove(toRemove);
        }
    }

    /**
     * Assigns a spaceship to this mission with an optional end date.
     *
     * @param spaceShip The spaceship to assign
     * @param endDate The date when the assignment should end (must be in the future)
     * @throws IllegalArgumentException if spaceShip is null or endDate is not in the future
     */
    public void assignSpaceShip(SpaceShip spaceShip, LocalDate endDate) {
        if (spaceShip == null) {
            throw new IllegalArgumentException("SpaceShip cannot be null");
        }
        if (endDate != null && !endDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("End date must be in the future");
        }

        MissionAssignment assignment = new MissionAssignment(spaceShip, this, endDate);
        this.assignments.add(assignment);
        spaceShip.getAssignments().add(assignment);
    }

    /**
     * Creates and adds a new objective to this mission.
     *
     * @param title The title of the objective
     * @param description The description of the objective
     * @param types The types of the objective
     * @param place The place where the objective should be completed
     * @param structure The structure associated with the objective
     * @return The newly created objective
     * @throws IllegalArgumentException if title is null/blank or types is empty
     */
    public Objective createAndAddObjective(String title, String description, Set<ObjectiveType> types, String place, String structure) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Objective title cannot be null or blank");
        }
        if (types == null || types.isEmpty()) {
            throw new IllegalArgumentException("Objective must have at least one type");
        }

        Objective objective = new Objective();
        objective.setTitle(title);
        objective.setDescription(description);
        objective.setTypes(types);
        objective.setPlace(place);
        objective.setStructure(structure);
        objective.setMission(this);

        this.objectives.add(objective);
        return objective;
    }
}