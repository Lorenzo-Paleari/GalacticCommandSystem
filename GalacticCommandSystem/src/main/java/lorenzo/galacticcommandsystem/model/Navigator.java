package lorenzo.galacticcommandsystem.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * A Navigator have specific skills and experience in navigation, tracked through various metrics
 * such as navigation bonus, experience years, and successful navigation count.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Navigator extends CrewMember {

    /**
     * The navigation bonus of the navigator.
     * Must be at least 100 and cannot be null.
     */
    @Min(100)
    @NotNull
    private Double navigationBonus;

    /**
     * The number of years of navigation experience.
     * Must be non-negative and cannot be null.
     */
    @Min(0)
    @NotNull
    private int navigationExperienceYears;

    /**
     * The number of successful navigation operations completed.
     * Must be non-negative and cannot be null.
     */
    @Min(0)
    @NotNull
    private double successfulNavigations;

    /**
     * Calculates the overall skill level of the navigator.
     * The calculation takes into account both experience years and successful navigations.
     *
     * @return the calculated skill level as a double
     */
    public double calculateSkillLevel() {
        return navigationExperienceYears * 1.5 + successfulNavigations * 2;
    }

    /**
     * Records a successful navigation operation.
     * Increments the successfulNavigations counter.
     */
    public void recordSuccessfulNavigation() {
        successfulNavigations++;
    }

    /**
     * Checks if the navigator is eligible for critical missions.
     * Eligibility is based on the calculated skill level.
     *
     * @return true if the navigator's skill level is above 50
     */
    public boolean isEligibleForCriticalMissions() {
        return calculateSkillLevel() > 50;
    }

    /**
     * Calculates the total compensation for the navigator.
     * Includes both base credits and navigation bonus.
     *
     * @return the total compensation as a double
     */
    public double calculateTotalCompensation() {
        return getCredits() + (navigationBonus != null ? navigationBonus : 0);
    }
}