package lorenzo.galacticcommandsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A spaceship can have crew members, be assigned to missions, and maintain a history
 * of crew assignments. Each spaceship has a unique name.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SpaceShip {
    /**
     * Unique identifier for the spaceship.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The name of the spaceship.
     * Must be unique and between 2 and 100 characters.
     */
    @NotBlank(message = "Ship name is mandatory")
    @Size(min = 2, max = 100, message = "Ship name must be between 2 and 100 characters")
    @Column(length = 100, unique = true)
    private String name;

    /**
     * The current crew members assigned to this spaceship.
     */
    @OneToMany(mappedBy = "spaceShip", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CrewMember> crew = new HashSet<>();

    /**
     * The mission assignments for this spaceship.
     * Assignments are persisted and removed with the spaceship.
     */
    @OneToMany(mappedBy = "spaceShip", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<MissionAssignment> assignments = new HashSet<>();

    /**
     * The history of crew assignments for this spaceship.
     * Ordered by assignment date in ascending order.
     * and are removed when the spaceship is deleted.
     */
    @OneToMany(mappedBy = "spaceShip", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("assignmentDate ASC")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<CrewHistory> crewHistoryList = new ArrayList<>();

    /**
     * The operational status of the spaceship.
     * Defaults to true (operational).
     */
    @NotNull(message = "Operational status is required")
    @Builder.Default
    private boolean isOperational = true;

    /**
     * Gets the current number of crew members assigned to this spaceship.
     *
     * @return the number of crew members currently assigned
     */
    public int crewCount() {
        return crew.size();
    }

    /**
     * Checks if the spaceship requires repairs.
     * A spaceship needs repairs when it is not operational.
     *
     * @return true if the spaceship is not operational and needs repairs
     */
    public boolean needsRepairs() {
        return !isOperational;
    }
}