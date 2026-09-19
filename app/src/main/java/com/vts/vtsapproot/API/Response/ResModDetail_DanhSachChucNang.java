package com.vts.vtsapproot.API.Response;

import java.io.Serializable;
import java.util.Objects;

public class ResModDetail_DanhSachChucNang implements Serializable {
    private int CaiLumMia;

    public int getCaiLumMia() {
        return CaiLumMia;
    }

    public void setCaiLumMia(int caiLumMia) {
        CaiLumMia = caiLumMia;
    }

    private String SapXep;
    private String NhomChucNang;
    private String TenNhomChucNang;
    private String MaChucNang;
    private String TenChucNang;
    private String Caption;
    private String Title;
    private String GhiChu;
    private boolean ALLOW_VISIBLE;
    private boolean ALLOW_VIEW;
    private boolean ALLOW_ADD;
    private boolean ALLOW_DEL;
    private boolean ALLOW_EDIT;
    private boolean ALLOW_RUN;
    private boolean ALLOW_EXCEL;
    private boolean ALLOW_TOOLBAR;
    private boolean VISIBLE;
    private boolean VIEW;
    private boolean ADD;
    private boolean DEL;
    private boolean EDIT;
    private boolean RUN;
    private boolean EXCEL;
    private boolean TOOLBAR;
    private int IconResId;
    private int TextColorResId;
    private int BGColorResId;
    private int IconColorResId;

    public int getBGColorResId() {
        return BGColorResId;
    }

    public void setBGColorResId(int BGColorResId) {
        this.BGColorResId = BGColorResId;
    }

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        Caption = caption;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getTextColorResId() {
        return TextColorResId;
    }

    public void setTextColorResId(int textColorResId) {
        TextColorResId = textColorResId;
    }

    public int getIconResId() {
        return IconResId;
    }

    public void setIconResId(int iconResId) {
        IconResId = iconResId;
    }

    public String getSapXep() {
        return SapXep;
    }

    public void setSapXep(String sapXep) {
        SapXep = sapXep;
    }

    public String getNhomChucNang() {
        return NhomChucNang;
    }

    public String getTenNhomChucNang() {
        return TenNhomChucNang;
    }

    public String getMaChucNang() {
        return MaChucNang;
    }

    public String getTenChucNang() {
        return TenChucNang;
    }

    public boolean isALLOW_VISIBLE() {
        return ALLOW_VISIBLE;
    }

    public boolean isALLOW_VIEW() {
        return ALLOW_VIEW;
    }

    public boolean isALLOW_ADD() {
        return ALLOW_ADD;
    }

    public boolean isALLOW_DEL() {
        return ALLOW_DEL;
    }

    public boolean isALLOW_EDIT() {
        return ALLOW_EDIT;
    }

    public boolean isALLOW_RUN() {
        return ALLOW_RUN;
    }

    public boolean isALLOW_EXCEL() {
        return ALLOW_EXCEL;
    }

    public boolean isALLOW_TOOLBAR() {
        return ALLOW_TOOLBAR;
    }

    public boolean isVISIBLE() {
        return VISIBLE;
    }

    public boolean isVIEW() {
        return VIEW;
    }

    public boolean isADD() {
        return ADD;
    }

    public boolean isDEL() {
        return DEL;
    }

    public boolean isEDIT() {
        return EDIT;
    }

    public boolean isRUN() {
        return RUN;
    }

    public boolean isEXCEL() {
        return EXCEL;
    }

    public boolean isTOOLBAR() {
        return TOOLBAR;
    }

    public String getGhiChu() {
        return GhiChu;
    }

    public int getIconColorResId() {
        return IconColorResId;
    }

    public void setIconColorResId(int iconColorResId) {
        IconColorResId = iconColorResId;
    }

    public ResModDetail_DanhSachChucNang() {
    }
    public ResModDetail_DanhSachChucNang(String pMaChucNang) {
        this.MaChucNang = pMaChucNang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResModDetail_DanhSachChucNang item = (ResModDetail_DanhSachChucNang) o;
        return Objects.equals(MaChucNang, item.MaChucNang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(MaChucNang);
    }
}