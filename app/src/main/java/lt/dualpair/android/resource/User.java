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
    private Set<UserAccount> accounts;

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

    public Set<UserAccount> getAccounts() {
        return accounts;
    }

    public UserAccount getFacebookAccount() {
        for (UserAccount account : getAccounts()) {
            if (account.getAccountType().equals("FACEBOOK")) {
                return account;
            }
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

    public void setSociotypes(Set<Sociotype> sociotypes) {
        this.sociotypes = sociotypes;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public void setAccounts(Set<UserAccount> accounts) {
        this.accounts = accounts;
    }
}
