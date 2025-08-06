package lorenzo.galacticcommandsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

/**
 * This class tracks the relationship between a spaceship and a mission.
 * Each assignment has a start date and an optional end date.
 */
@Entity
@Data
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"spaceShip_id", "mission_id"}) //unique constraint to avoid duplicate assignments
})
public class MissionAssignment {

    /**
     * Unique identifier for the mission assignment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The spaceship assigned to this mission.
     * Cannot be null and is lazily loaded.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spaceShip_id", nullable = false)
    @NotNull(message = "Spaceship is required")
    private SpaceShip spaceShip;

    /**
     * The mission this assignment is for.
     * Cannot be null and is lazily loaded.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    @NotNull(message = "Mission is required")
    private Mission mission;

    /**
     * The date when the assignment started.
     * Cannot be null and defaults to the current date.
     */
    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate = LocalDate.now();

    /**
     * The date when the assignment ended or will end.
     * Can be null for open-ended assignments.
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Creates a new mission assignment with the specified spaceship, mission, and optional end date.
     *
     * @param spaceShip The spaceship to be assigned
     * @param mission The mission to assign the spaceship to
     * @param endDate The end date of the assignment (optional)
     * @throws IllegalArgumentException if spaceShip or mission is null, or if endDate is not after startDate
     */
    public MissionAssignment(@NotNull SpaceShip spaceShip,
                           @NotNull Mission mission,
                           LocalDate endDate) {
        if (spaceShip == null) {
            throw new IllegalArgumentException("Spaceship cannot be null");
        }
        if (mission == null) {
            throw new IllegalArgumentException("Mission cannot be null");
        }

        this.spaceShip = spaceShip;
        this.mission = mission;
        this.startDate = LocalDate.now();

        if (endDate != null && !endDate.isAfter(this.startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        this.endDate = endDate;
    }

    //default constructor
    public MissionAssignment() {
        this.startDate = LocalDate.now();
    }

    /**
     * Checks if the assignment is currently active.
     * An assignment is considered active if it has no end date or if the end date is in the future.
     *
     * @return true if the assignment is active, false otherwise
     */
    public boolean isActive() {
        return endDate == null || endDate.isAfter(LocalDate.now());
    }

    /**
     * Calculates the duration of the assignment in days.
     * Returns null if the assignment hasn't ended yet (no end date).
     *
     * @return The duration in days, or null if the assignment hasn't ended
     */
    public Long getDurationInDays() {
        if (endDate == null) {
            return null;
        }
        return endDate.toEpochDay() - startDate.toEpochDay();
    }

    /**
     * Extends the assignment by setting a new end date.
     * The new end date must be after the current end date.
     *
     * @param newEndDate The new end date for the assignment
     * @throws IllegalArgumentException if newEndDate is null or not after the current end date
     */
    public void extendAssignment(LocalDate newEndDate) {
        if (newEndDate == null) {
            throw new IllegalArgumentException("New end date cannot be null");
        }
        if (endDate != null && !newEndDate.isAfter(endDate)) {
            throw new IllegalArgumentException("New end date must be after current end date");
        }
        this.endDate = newEndDate;
    }

    /**
     * Gets a human-readable status of the assignment.
     * Returns "Active" for assignments without an end date,
     * "Active until [date]" for assignments with a future end date,
     * or "Completed on [date]" for assignments that have ended.
     *
     * @return A string describing the current status of the assignment
     */
    public String getStatus() {
        if (endDate == null) {
            return "Active";
        }
        if (endDate.isAfter(LocalDate.now())) {
            return "Active until " + endDate;
        }
        return "Completed on " + endDate;
    }
}

