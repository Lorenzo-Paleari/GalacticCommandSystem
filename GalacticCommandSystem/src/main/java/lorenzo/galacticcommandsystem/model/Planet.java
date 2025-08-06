package lorenzo.galacticcommandsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A planet is a celestial body that can have crew members, missions, buildings, and resources.
 * Each planet has a unique name, position in 3D space, and has properties such as atmosphere type.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Planet {

    /**
     * Unique identifier for the planet.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The name of the planet.
     * Must be unique and between 2 and 100 characters.
     */
    @NotBlank(message = "Planet name is required")
    @Size(min = 2, max = 100, message = "Planet name must be between 2 and 100 characters")
    @Column(length = 100, unique = true)
    private String name;

    /**
     * The type of atmosphere on the planet.
     * Cannot exceed 50 characters.
     */
    @Size(max = 50, message = "Atmosphere type cannot exceed 50 characters")
    @Column(length = 50)
    private String atmosphereType;

    /**
     * The crew members currently stationed on this planet.
     */
    @OneToMany(mappedBy = "planet", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CrewMember> crewMembers = new HashSet<>();

    /**
     * The missions associated with this planet.
     * Missions are removed when the planet is deleted.
     */
    @OneToMany(mappedBy = "planet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Mission> missions = new HashSet<>();

    /**
     * The buildings constructed on this planet.
     * Stored as a collection of building names.
     */
    @ElementCollection
    @CollectionTable(name = "planet_buildings", joinColumns = @JoinColumn(name = "planet_id"))
    @Column(name = "building_name", length = 100)
    @Builder.Default
    private List<String> buildings = new ArrayList<>();

    /**
     * The position of the planet in 3D space.
     * Must be in the format "x,y,z" where x, y, and z are valid numbers.
     */
    @NotBlank(message = "Position is required in format x,y,z")
    @Pattern(regexp = "-?\\d+(\\.\\d+)?,-?\\d+(\\.\\d+)?,-?\\d+(\\.\\d+)?", 
            message = "Position must be in format x,y,z with valid numbers")
    @Column(length = 50)
    private String position;

    /**
     * The resources available on this planet.
     * merge: update resources when a planet is updated
     * join table: create a table to store the relationship between planets and resources
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "planet_resource",
            joinColumns = @JoinColumn(name = "planet_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Resource> resources = new HashSet<>();

    /**
     * The primary resource of the planet.
     * Must be one of the planet's available resources.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_resource_id")
    private Resource primaryResource;

    /**
     * Static counter for the total number of planets in the system.
     */
    private static int planetCount = 0;

    /**
     * Gets the X coordinate of the planet's position.
     *
     * @return the X coordinate as a double
     */
    public double getX() {
        return Double.parseDouble(position.split(",")[0]);
    }

    /**
     * Gets the Y coordinate of the planet's position.
     *
     * @return the Y coordinate as a double
     */
    public double getY() {
        return Double.parseDouble(position.split(",")[1]);
    }

    /**
     * Gets the Z coordinate of the planet's position.
     *
     * @return the Z coordinate as a double
     */
    public double getZ() {
        return Double.parseDouble(position.split(",")[2]);
    }

    /**
     * Sets the position of the planet in 3D space.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     */
    public void setPosition(double x, double y, double z) {
        this.position = x + "," + y + "," + z;
    }

    /**
     * Gets the total number of planets in the system.
     *
     * @return the number of planets
     */
    public static int getPlanetCount() {
        return planetCount;
    }

    /**
     * Increments the planet count when a new planet is created.
     */
    @PostPersist
    public void onCreate() {
        planetCount++;
    }

    /**
     * Decrements the planet count when a planet is deleted.
     */
    @PostRemove
    public void onDelete() {
        planetCount--;
    }

    /**
     * Sets the primary resource of the planet.
     * The resource must be one of the planet's available resources.
     *
     * @param resource the resource to set as primary
     * @throws IllegalArgumentException if the resource is null or not part of the planet's resources
     */
    public void setPrimaryResource(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Primary resource cannot be null");
        }
        if (!resources.contains(resource)) {
            throw new IllegalArgumentException("Primary resource must be part of the planet's resources");
        }
        this.primaryResource = resource;
    }

    /**
     * Adds a building to the planet.
     * Duplicate buildings are not added.
     *
     * @param building the name of the building to add
     * @throws IllegalArgumentException if the building name is null or empty
     */
    public void addBuilding(String building) {
        if (building == null || building.isBlank()) {
            throw new IllegalArgumentException("Building name cannot be null or empty");
        }
        if (!buildings.contains(building)) {
            buildings.add(building);
        }
    }

    /**
     * Removes a building from the planet.
     *
     * @param building the name of the building to remove
     */
    public void removeBuilding(String building) {
        buildings.remove(building);
    }

    /**
     * Adds a resource to the planet.
     *
     * @param resource the resource to add
     * @throws IllegalArgumentException if the resource is null
     */
    public void addResource(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource cannot be null");
        }
        resources.add(resource);
    }

    /**
     * Removes a resource from the planet.
     * The primary resource cannot be removed.
     *
     * @param resource the resource to remove
     * @throws IllegalArgumentException if the resource is null
     * @throws IllegalStateException if trying to remove the primary resource
     */
    public void removeResource(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource cannot be null");
        }
        if (resource.equals(primaryResource)) {
            throw new IllegalStateException("Cannot remove the primary resource");
        }
        resources.remove(resource);
    }

    /**
     * Calculates the Euclidean distance between this planet and another planet.
     *
     * @param other the other planet to calculate distance to
     * @return the distance between the planets in the same units as the coordinates
     */
    public double getDistanceTo(Planet other) {
        double dx = this.getX() - other.getX();
        double dy = this.getY() - other.getY();
        double dz = this.getZ() - other.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}