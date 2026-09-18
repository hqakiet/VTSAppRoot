package com.vts.vtsapproot.API.Response;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ResMod_RefreshToken extends ResMod_Base implements Serializable {
    String TKN;
    boolean UserTest;
    boolean ReleaseTest;
    boolean NewVersion;
    Date NgayHetHan;
    List<ResModDetail_DanhSachChucNang> ChucNangPhanQuyens;
    ResModDetail_GiaTriHeThong SoLeHeThong;

    public String getTKN() {
        return TKN;
    }

    public boolean isUserTest() {
        return UserTest;
    }

    public boolean isReleaseTest() {
        return ReleaseTest;
    }

    public Date getNgayHetHan() {
        return NgayHetHan;
    }

    public List<ResModDetail_DanhSachChucNang> getChucNangPhanQuyens() {
        return ChucNangPhanQuyens;
    }

    public ResModDetail_GiaTriHeThong getSoLeHeThong() {
        return SoLeHeThong;
    }

    public boolean getNewVersion() {
        return NewVersion;
    }
}