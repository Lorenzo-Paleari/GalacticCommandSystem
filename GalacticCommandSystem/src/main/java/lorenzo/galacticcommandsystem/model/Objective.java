package lorenzo.galacticcommandsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.EnumSet;
import java.util.Set;

/**
 * An objective can be of multiple types (EXPLORATION, BUILDING) and contains
 * specific information based on its type. Each objective is associated with a mission
 * and can be performed when the mission is active.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Objective {
    /**
     * Unique identifier for the objective.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * The mission this objective belongs to.
     * Cannot be null and cannot be updated after creation.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "mission_id", nullable = false, updatable = false)
    private Mission mission;

    /**
     * The title of the objective.
     * Must be between 3 and 100 characters.
     */
    @NotBlank(message = "Objective title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Column(length = 100)
    private String title;
    
    /**
     * Detailed description of the objective.
     * Cannot exceed 1000 characters.
     */
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(length = 1000)
    private String description;

    /**
     * The place to explore for EXPLORATION type objectives.
     * Cannot exceed 200 characters.
     */
    @Size(max = 200, message = "Place name cannot exceed 200 characters")
    @Column(length = 200)
    private String place;

    /**
     * The structure to build for BUILDING type objectives.
     * Cannot exceed 200 characters.
     */
    @Size(max = 200, message = "Structure name cannot exceed 200 characters")
    @Column(length = 200)
    private String structure;

    /**
     * The types of this objective.
     * Must contain at least one type
     */
    @NotEmpty(message = "An Objective must have at least one type")
    @ElementCollection(targetClass = ObjectiveType.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "objective_types", joinColumns = @JoinColumn(name = "objective_id"))
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<ObjectiveType> types = EnumSet.noneOf(ObjectiveType.class);

    /**
     * Checks if this objective is of type EXPLORATION.
     *
     * @return true if the objective includes EXPLORATION type
     */
    public boolean isExploration() {
        return types.contains(ObjectiveType.EXPLORATION);
    }

    /**
     * Checks if this objective is of type BUILDING.
     *
     * @return true if the objective includes BUILDING type
     */
    public boolean isBuilding() {
        return types.contains(ObjectiveType.BUILDING);
    }

    /**
     * Performs the exploration objective.
     * This method validates the mission state, objective type, and required place information
     * before executing the exploration.
     *
     * @throws UnsupportedOperationException if the mission is inactive
     * @throws IllegalStateException if the objective is not an exploration type or place is not specified
     */
    public void performExploration() {
        validateMissionState();
        validateExplorationType();
        validatePlace();

        System.out.println("[" + title + "] Exploration performed at " + place);
        completeObjective();
    }

    /**
     * Performs the building objective.
     * This method validates the mission state, objective type, required structure information,
     * and planet association before executing the building operation.
     *
     * @throws UnsupportedOperationException if the mission is inactive
     * @throws IllegalStateException if the objective is not a building type, structure is not specified,
     *         or no planet is associated with the mission
     */
    public void performBuilding() {
        validateMissionState();
        validateBuildingType();
        validateStructure();
        validatePlanet();

        Planet planet = mission.getPlanet();
        planet.getBuildings().add(structure);
        completeObjective();
    }

    /**
     * Validates that the mission is in an active state.
     *
     * @throws UnsupportedOperationException if the mission is inactive
     */
    private void validateMissionState() {
        if (mission.getMissionState() != MissionState.ACTIVE) {
            throw new UnsupportedOperationException("Cannot perform objectives on an inactive mission");
        }
    }

    /**
     * Validates that the objective is of type EXPLORATION.
     *
     * @throws IllegalStateException if the objective is not an exploration type
     */
    private void validateExplorationType() {
        if (!isExploration()) {
            throw new IllegalStateException("Cannot perform exploration on a non-exploration objective");
        }
    }

    /**
     * Validates that the objective is of type BUILDING.
     *
     * @throws IllegalStateException if the objective is not a building type
     */
    private void validateBuildingType() {
        if (!isBuilding()) {
            throw new IllegalStateException("Cannot perform building on a non-building objective");
        }
    }

    /**
     * Validates that a place is specified for exploration objectives.
     *
     * @throws IllegalStateException if place is not specified
     */
    private void validatePlace() {
        if (place == null || place.isBlank()) {
            throw new IllegalStateException("Place must be specified for exploration objectives");
        }
    }

    /**
     * Validates that a structure is specified for building objectives.
     *
     * @throws IllegalStateException if structure is not specified
     */
    private void validateStructure() {
        if (structure == null || structure.isBlank()) {
            throw new IllegalStateException("Structure must be specified for building objectives");
        }
    }

    /**
     * Validates that a planet is associated with the mission for building objectives.
     *
     * @throws IllegalStateException if no planet is associated with the mission
     */
    private void validatePlanet() {
        if (mission.getPlanet() == null) {
            throw new IllegalStateException("Cannot build without an associated planet");
        }
    }

    /**
     * Completes the objective by removing it from the mission.
     * This method is called after successfully performing an objective.
     */
    private void completeObjective() {
        mission.getObjectives().remove(this);
        this.setMission(null);
    }

    /**
     * Validates that objectives cannot be removed from inactive missions.
     *
     * @throws PersistenceException if attempting to remove an objective from an inactive mission
     */
    @PreRemove
    public void preRemove() {
        if (mission.getMissionState() == MissionState.INACTIVE) {
            throw new PersistenceException("Cannot remove objectives from inactive missions");
        }
    }

    /**
     * Validates the objective's state before persisting or updating.
     * Ensures that the objective has at least one type and that required fields
     * are present based on the objective type.
     *
     * @throws IllegalArgumentException if validation fails
     */
    @PrePersist
    @PreUpdate
    public void validate() {
        if (types.isEmpty()) {
            throw new IllegalArgumentException("An Objective must have at least one type");
        }

        if (isExploration() && (place == null || place.isBlank())) {
            throw new IllegalArgumentException("Place is required for exploration objectives");
        }

        if (isBuilding() && (structure == null || structure.isBlank())) {
            throw new IllegalArgumentException("Structure is required for building objectives");
        }
    }
}