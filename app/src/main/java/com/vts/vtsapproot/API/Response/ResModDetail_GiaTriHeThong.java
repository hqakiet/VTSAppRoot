package com.vts.vtsapproot.API.Response;

import java.io.Serializable;

public class ResModDetail_GiaTriHeThong
        implements Serializable {

    int SOLESOLUONG;
    int SOLEDONGIA;
    int SOLESOTIEN;
    int SOLETYLE;

    public int getSOLESOLUONG() {
        return SOLESOLUONG;
    }

    public int getSOLEDONGIA() {
        return SOLEDONGIA;
    }

    public int getSOLESOTIEN() {
        return SOLESOTIEN;
    }

    public int getSOLETYLE() {
        return SOLETYLE;
    }
}