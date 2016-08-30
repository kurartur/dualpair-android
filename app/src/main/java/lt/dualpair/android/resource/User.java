package lt.dualpair.android.resource;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User extends BaseResource implements Serializable {

    private Long id;
    private String name;
    private Date dateOfBirth;
    private Integer age;
    private Set<Sociotype> sociotypes = new HashSet<>();
    private Set<Location> locations;
    private String description;
    private List<Photo> photos;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public Integer getAge() {
        return age;
    }

    public Set<Sociotype> getSociotypes() {
        return sociotypes;
    }

    public Set<Location> getLocations() {
        return locations;
    }

    public String getDescription() {
        return description;
    }

    public List<Photo> getPhotos() {
        return photos;
    }
}
