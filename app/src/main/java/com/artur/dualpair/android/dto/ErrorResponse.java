package com.artur.dualpair.android.dto;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    @SerializedName("error_id")
    private String errorId;

    @SerializedName("error_description")
    private String errorDescription;

    public ErrorResponse(String error, String errorDescription) {
        this.errorId = error;
        this.errorDescription = errorDescription;
    }

    public String getError() {
        return errorId;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

}
