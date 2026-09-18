package com.vts.vtsapproot.API.Response;

import java.util.Date;
import java.util.List;

public class ResMod_DangNhap
        extends ResMod_Base {

    String TKN;
    String RTKN;
    boolean UserTest;
    boolean ReleaseTest;
    Date NgayHetHan;
    List<ResModDetail_DanhSachChucNang> ChucNangPhanQuyens;
    ResModDetail_GiaTriHeThong SoLeHeThong;
    List<ResModDetail_RemoteDB> DataResults;

    public String getTKN() {
        return TKN;
    }

    public String getRTKN() {
        return RTKN;
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

    public List<ResModDetail_RemoteDB> getDataResults() {
        return DataResults;
    }

    public ResModDetail_GiaTriHeThong getSoLeHeThong() {
        return SoLeHeThong;
    }
}