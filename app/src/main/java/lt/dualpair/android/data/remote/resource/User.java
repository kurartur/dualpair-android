package lt.dualpair.android.data.remote.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User extends BaseResource implements Serializable {

    private Long id;
    private String name;
    private Date dateOfBirth;
    private Integer age;
    private String gender;
    private Set<Sociotype> sociotypes = new HashSet<>();
    private Set<Location> locations;
    private String description;
    private List<PhotoResource> photos;
    private List<UserAccount> accounts = new ArrayList<>();
    private String relationshipStatus;
    private Set<String> purposesOfBeing = new HashSet<>();

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

    public String getGender() {
        return gender;
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

    public List<PhotoResource> getPhotos() {
        return photos;
    }

    public List<UserAccount> getAccounts() {
        return accounts;
    }

    public Location getFirstLocation() {
        if (!locations.isEmpty()) {
            return locations.iterator().next();
        }
        return null;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setSociotypes(Set<Sociotype> sociotypes) {
        this.sociotypes = sociotypes;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhotos(List<PhotoResource> photos) {
        this.photos = photos;
    }

    public void setAccounts(List<UserAccount> accounts) {
        this.accounts = accounts;
    }

    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public Set<String> getPurposesOfBeing() {
        return purposesOfBeing;
    }

    public void setPurposesOfBeing(Set<String> purposesOfBeing) {
        this.purposesOfBeing = purposesOfBeing;
    }
}
