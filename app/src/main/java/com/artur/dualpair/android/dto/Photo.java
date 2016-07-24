package com.artur.dualpair.android.dto;

import java.io.Serializable;

public class Photo implements Serializable {

    private Long id;
    private String sourceLink;

    public Long getId() {
        return id;
    }

    public String getSourceLink() {
        return sourceLink;
    }
}
