package com.vts.vtsapproot.API.Response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResMod_Base
        implements Serializable {

    @SerializedName("DataError")
    int DataError;
    @SerializedName("DataErrorDescription")
    String DataErrorDescription;

    public int getDataError() {
        return DataError;
    }

    public String getDataErrorDescription() {
        return DataErrorDescription;
    }
}