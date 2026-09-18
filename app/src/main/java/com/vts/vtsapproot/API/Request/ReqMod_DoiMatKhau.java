package com.vts.vtsapproot.API.Request;

public class ReqMod_DoiMatKhau {
    String MatKhauOld;
    String MatKhauNew;

    public ReqMod_DoiMatKhau(String pMatKhauOld, String pMatKhauNew) {
        MatKhauOld = pMatKhauOld;
        MatKhauNew = pMatKhauNew;
    }

    public String getMatKhauOld() {
        return MatKhauOld;
    }

    public void setMatKhauOld(String matKhauOld) {
        MatKhauOld = matKhauOld;
    }

    public String getMatKhauNew() {
        return MatKhauNew;
    }

    public void setMatKhauNew(String matKhauNew) {
        MatKhauNew = matKhauNew;
    }
}
