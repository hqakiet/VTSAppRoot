package com.vts.vtsapproot.Tools;

import android.content.Context;
import android.content.Intent;

import com.vts.vtsapproot.API.Response.ResModDetail_DanhSachChucNang;

import java.util.List;

public interface FunctionConfigProvider {
    void onProcessDanhSachChucNang(
            List<ResModDetail_DanhSachChucNang> rawList,
            List<ResModDetail_DanhSachChucNang> filteredList,
            List<ResModDetail_DanhSachChucNang> fullList
    );

    Intent getMainActivityIntent(Context context, String remoteDb);
    Intent getMainActivityIntent(Context context);
    Intent getLoginActivityIntent(Context context);
}
