package com.artur.dualpair.android.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class User {

    private String name;
    private Date dateOfBirth;
    private Integer age;
    private Set<Sociotype> sociotypes = new HashSet<>();
    private SearchParameters searchParameters;

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
}
