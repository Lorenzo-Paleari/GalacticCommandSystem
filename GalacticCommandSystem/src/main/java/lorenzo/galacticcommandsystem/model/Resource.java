package lorenzo.galacticcommandsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Resources are materials that can be found on planets
 * Each resource has a unique name and can be associated with multiple planets.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource {

    /**
     * Unique identifier for the resource.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The name of the resource.
     * Must be unique and between 2 and 100 characters.
     */
    @NotBlank(message = "Resource name is required")
    @Size(min = 2, max = 100, message = "Resource name must be between 2 and 100 characters")
    @Column(length = 100, unique = true)
    private String name;

    /**
     * The planets where this resource can be found.
     * Lazily loaded and managed through a many-to-many relationship.
     */
    @ManyToMany(mappedBy = "resources", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Planet> planets = new HashSet<>();
}