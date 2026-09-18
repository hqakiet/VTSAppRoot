package com.vts.vtsapproot.Tools;

import static com.vts.vtsapproot.Tools.gvSystem.App_LocalDB;

public class AppDatas {

    public String getLIST_SortColumn(String pLoai) {
        return App_LocalDB.Get_GIATRITHONGSO_STRING(pLoai + "_SortColumn", "");
    }
    public void setLIST_SortColumn(String pLoai, String pValue) {
        App_LocalDB.Set_GIATRITHONGSO_STRING(pLoai + "_SortColumn", pValue);
    }
    public boolean getLIST_SortValue(String pLoai) {
        return (App_LocalDB.Get_GIATRITHONGSO_NUMBER(pLoai + "_SortValue") != 0);
    }
    public void setLIST_SortValue(String pLoai, boolean pValue) {
        if (pValue)
            App_LocalDB.Set_GIATRITHONGSO_NUMBER(pLoai + "_SortValue", 1);
        else
            App_LocalDB.Set_GIATRITHONGSO_NUMBER(pLoai + "_SortValue", 0);
    }
}
