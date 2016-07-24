package com.artur.dualpair.android.dto;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    @SerializedName("message")
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
