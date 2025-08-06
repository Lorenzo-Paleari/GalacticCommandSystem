package lorenzo.galacticcommandsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Engineer have a specific technical specialty and a set of skills
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Engineer extends CrewMember {

    /**
     * The primary technical specialty of the engineer.
     * Cannot be blank.
     */
    @NotBlank
    private String techSpecialty;

    /**
     * The set of skills possessed by the engineer.
     * Stored as a collection of skill names.
     */
    @ElementCollection
    @CollectionTable(name = "engineer_skill", joinColumns = @JoinColumn(name = "engineer_id"))
    @Builder.Default
    private Set<String> skills = new HashSet<>();

    /**
     * Checks if the engineer possesses a specific skill.
     *
     * @param skill the skill to check for
     * @return true if the engineer has the specified skill
     */
    public boolean hasSkill(String skill) {
        return skills.contains(skill);
    }

    /**
     * Adds a new skill to the engineer's skill set.
     *
     * @param skill the skill to add
     */
    public void addSkill(String skill) {
        skills.add(skill);
    }

    /**
     * Removes a skill from the engineer's skill set.
     *
     * @param skill the skill to remove
     */
    public void removeSkill(String skill) {
        skills.remove(skill);
    }

    /**
     * Gets the full name of the engineer with the "Dr." title.
     *
     * @return the full name with title
     */
    @Override
    public String getFullName() {
        return "Dr. "+super.getFullName();
    }
}

