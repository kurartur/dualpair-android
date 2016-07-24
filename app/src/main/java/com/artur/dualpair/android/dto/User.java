package com.artur.dualpair.android.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User implements Serializable {

    private String name;
    private Date dateOfBirth;
    private Integer age;
    private Set<Sociotype> sociotypes = new HashSet<>();
    private SearchParameters searchParameters;
    private Location location;
    private String description;
    private List<Photo> photos;

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

    public SearchParameters getSearchParameters() {
        return searchParameters;
    }

    public Location getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public List<Photo> getPhotos() {
        return photos;
    }
}
