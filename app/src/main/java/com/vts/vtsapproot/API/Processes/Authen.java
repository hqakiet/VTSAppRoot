package com.vts.vtsapproot.API.Processes;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vts.vtsapproot.API.APIService;
import com.vts.vtsapproot.API.Request.ReqMod_DangNhap;
import com.vts.vtsapproot.API.Request.ReqMod_RefreshToken;
import com.vts.vtsapproot.API.Response.ResMod_Base;
import com.vts.vtsapproot.API.Response.ResMod_DangNhap;
import com.vts.vtsapproot.API.Response.ResMod_RefreshToken;
import com.vts.vtsapproot.Tools.gvSystem;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Authen {

    public static void DangNhap_Google(int pTimeOut, APIService.DoAPI_Authen_Result pResult) {
        APIService mAPIService = gvSystem.GetAPIServiceNoAuthen(pTimeOut);
        ReqMod_DangNhap mRequestModel = null;

        if (gvSystem.App_NhanThongBao)
            mRequestModel = new ReqMod_DangNhap.Builder().tokenID(gvSystem.App_GoogleSignInToken).clientToken(gvSystem.getApp_ClientToken()).build();
        else
            mRequestModel = new ReqMod_DangNhap.Builder().tokenID(gvSystem.App_GoogleSignInToken).build();

        Call<ResMod_DangNhap> call = mAPIService.DoAPI_DangNhap(gvSystem.App_BaseUrl + gvSystem.App_DangNhapFunction, mRequestModel);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResMod_DangNhap> call, @NonNull Response<ResMod_DangNhap> response) {
                if (response.isSuccessful()) {
                    ResMod_DangNhap mResponseModel = response.body();
                    if (mResponseModel != null) {
                        if (mResponseModel.getDataError() == 0) {
                            gvSystem.setApp_AccessToken(mResponseModel.getTKN() == null ? "" : mResponseModel.getTKN());
                            gvSystem.setApp_RefreshToken(mResponseModel.getRTKN() == null ? "" : mResponseModel.getRTKN());

                            gvSystem.setApp_GoogleAccount(gvSystem.App_GoogleSignInAccount.getEmail());

                            gvSystem.setApp_IsReleaseTest(mResponseModel.isReleaseTest());

                            gvSystem.setApp_BasicAuthenLogIn(false);
                            gvSystem.setApp_CurrentAccount(null);
                            gvSystem.setApp_CurrentPassword(null);

                            gvSystem.App_DanhSachChucNang(mResponseModel.getChucNangPhanQuyens());
                            gvSystem.App_sysval_SOLESOLUONG = mResponseModel.getSoLeHeThong().getSOLESOLUONG();
                            gvSystem.App_sysval_SOLEDONGIA = mResponseModel.getSoLeHeThong().getSOLEDONGIA();
                            gvSystem.App_sysval_SOLESOTIEN = mResponseModel.getSoLeHeThong().getSOLESOTIEN();
                            gvSystem.App_sysval_SOLETYLE = mResponseModel.getSoLeHeThong().getSOLETYLE();

                            pResult.GetDataOK();
                        } else {
                            gvSystem.setApp_AccessToken(null);
                            gvSystem.setApp_RefreshToken(null);
                            gvSystem.App_DanhSachChucNang(new ArrayList<>());
                            pResult.GetDataFailed(new Exception(mResponseModel.getDataErrorDescription()));
                        }
                    } else {
                        gvSystem.setApp_AccessToken(null);
                        gvSystem.setApp_RefreshToken(null);
                        gvSystem.App_DanhSachChucNang(new ArrayList<>());
                        pResult.GetDataFailed(new Exception("Không có dữ liệu trả về"));
                    }
                } else {
                    gvSystem.setApp_AccessToken(null);
                    gvSystem.setApp_RefreshToken(null);
                    gvSystem.App_DanhSachChucNang(new ArrayList<>());
                    Gson gson = new Gson();
                    Type type = new TypeToken<ResMod_Base>() {
                    }.getType();
                    assert response.errorBody() != null;
                    ResMod_Base mResponseModel = gson.fromJson(response.errorBody().charStream(), type);
                    if (mResponseModel != null)
                        pResult.GetDataFailed(new Exception(mResponseModel.getDataError() + ": " + mResponseModel.getDataErrorDescription()));
                    else
                        pResult.GetDataFailed(new Exception(response.toString()));
                }
                call.cancel();
            }

            @Override
            public void onFailure(@NonNull Call<ResMod_DangNhap> call, @NonNull Throwable t) {
                gvSystem.setApp_AccessToken(null);
                gvSystem.setApp_RefreshToken(null);
                gvSystem.App_DanhSachChucNang(new ArrayList<>());
                pResult.GetDataFailed(t);
                call.cancel();
            }
        });
    }

    public static void DangNhap_UserPwd(int pTimeOut, String pUser, String pPass, APIService.DoAPI_Authen_Result pResult) {
        APIService mAPIService = gvSystem.GetAPIServiceNoAuthen(pTimeOut);
        ReqMod_DangNhap mRequestModel = new ReqMod_DangNhap.Builder().user(pUser).pass(pPass).clientToken(gvSystem.getApp_ClientToken()).build();
        Call<ResMod_DangNhap> call = mAPIService.DoAPI_DangNhap(gvSystem.App_BaseUrl + gvSystem.App_DangNhapFunction, mRequestModel);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResMod_DangNhap> call, @NonNull Response<ResMod_DangNhap> response) {
                if (response.isSuccessful()) {
                    ResMod_DangNhap mResponseModel = response.body();
                    if (mResponseModel != null) {
                        if (mResponseModel.getDataError() == 0) {
                            gvSystem.setApp_AccessToken(mResponseModel.getTKN() == null ? "" : mResponseModel.getTKN());
                            gvSystem.setApp_RefreshToken(mResponseModel.getRTKN() == null ? "" : mResponseModel.getRTKN());

                            gvSystem.setApp_GoogleAccount(null);

                            gvSystem.setApp_IsReleaseTest(mResponseModel.isReleaseTest());

                            gvSystem.setApp_BasicAuthenLogIn(true);
                            gvSystem.setApp_CurrentAccount(pUser);
                            gvSystem.setApp_CurrentPassword(pPass);

                            gvSystem.App_DanhSachChucNang(mResponseModel.getChucNangPhanQuyens());

                            if (mResponseModel.getSoLeHeThong() != null) {
                                gvSystem.App_sysval_SOLESOLUONG = mResponseModel.getSoLeHeThong().getSOLESOLUONG();
                                gvSystem.App_sysval_SOLEDONGIA = mResponseModel.getSoLeHeThong().getSOLEDONGIA();
                                gvSystem.App_sysval_SOLESOTIEN = mResponseModel.getSoLeHeThong().getSOLESOTIEN();
                                gvSystem.App_sysval_SOLETYLE = mResponseModel.getSoLeHeThong().getSOLETYLE();
                            } else {
                                gvSystem.App_sysval_SOLESOLUONG = 0;
                                gvSystem.App_sysval_SOLEDONGIA = 0;
                                gvSystem.App_sysval_SOLESOTIEN = 0;
                                gvSystem.App_sysval_SOLETYLE = 0;
                            }

                            pResult.GetDataOK();
                        } else {
                            gvSystem.setApp_AccessToken(null);
                            gvSystem.setApp_RefreshToken(null);
                            gvSystem.App_DanhSachChucNang(new ArrayList<>());
                            pResult.GetDataFailed(new Exception(mResponseModel.getDataErrorDescription()));
                        }
                    } else {
                        gvSystem.setApp_AccessToken(null);
                        gvSystem.setApp_RefreshToken(null);
                        gvSystem.App_DanhSachChucNang(new ArrayList<>());
                        pResult.GetDataFailed(new Exception("Không có dữ liệu trả về"));
                    }
                } else {
                    gvSystem.setApp_AccessToken(null);
                    gvSystem.setApp_RefreshToken(null);
                    gvSystem.App_DanhSachChucNang(new ArrayList<>());
                    Gson gson = new Gson();
                    Type type = new TypeToken<ResMod_Base>() {
                    }.getType();
                    assert response.errorBody() != null;
                    ResMod_Base mResponseModel = gson.fromJson(response.errorBody().charStream(), type);
                    if (mResponseModel != null)
                        pResult.GetDataFailed(new Exception(mResponseModel.getDataError() + ": " + mResponseModel.getDataErrorDescription()));
                    else
                        pResult.GetDataFailed(new Exception(response.toString()));
                }
                call.cancel();
            }

            @Override
            public void onFailure(@NonNull Call<ResMod_DangNhap> call, @NonNull Throwable t) {
                gvSystem.setApp_AccessToken(null);
                gvSystem.setApp_RefreshToken(null);
                gvSystem.App_DanhSachChucNang(new ArrayList<>());
                pResult.GetDataFailed(t);
                call.cancel();
            }
        });
    }

    public static void RefreshToken(APIService.DoAPI_Authen_Result pResult) {
        RefreshToken(gvSystem.getApp_ConnectTimeOut(), pResult);
    }

    public static void RefreshToken(int pTimeOut, APIService.DoAPI_Authen_Result pResult) {
        ReqMod_RefreshToken mRequestModel = null;

        if (gvSystem.App_NhanThongBao)
            mRequestModel = new ReqMod_RefreshToken(gvSystem.getApp_RefreshToken(), gvSystem.getApp_ClientToken());
        else
            mRequestModel = new ReqMod_RefreshToken(gvSystem.getApp_RefreshToken());

        APIService mAPIService = gvSystem.GetAPIServiceNoAuthen(pTimeOut);
        Call<ResMod_RefreshToken> call = mAPIService.DoAPI_RefreshToken(gvSystem.App_BaseUrl + gvSystem.App_RefreshTokenFunction, mRequestModel);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResMod_RefreshToken> call, @NonNull Response<ResMod_RefreshToken> response) {
                if (response.isSuccessful()) {
                    ResMod_RefreshToken mResponseModel = response.body();
                    if (mResponseModel != null) {
                        if (mResponseModel.getDataError() == 0) {
                            gvSystem.setApp_AccessToken(mResponseModel.getTKN() == null ? "" : mResponseModel.getTKN());

                            gvSystem.setApp_IsReleaseTest(mResponseModel.isReleaseTest());

                            gvSystem.App_DanhSachChucNang(mResponseModel.getChucNangPhanQuyens());
                            if (mResponseModel.getSoLeHeThong() != null) {
                                gvSystem.App_sysval_SOLESOLUONG = mResponseModel.getSoLeHeThong().getSOLESOLUONG();
                                gvSystem.App_sysval_SOLEDONGIA = mResponseModel.getSoLeHeThong().getSOLEDONGIA();
                                gvSystem.App_sysval_SOLESOTIEN = mResponseModel.getSoLeHeThong().getSOLESOTIEN();
                                gvSystem.App_sysval_SOLETYLE = mResponseModel.getSoLeHeThong().getSOLETYLE();
                            } else {
                                gvSystem.App_sysval_SOLESOLUONG = 0;
                                gvSystem.App_sysval_SOLEDONGIA = 0;
                                gvSystem.App_sysval_SOLESOTIEN = 0;
                                gvSystem.App_sysval_SOLETYLE = 0;
                            }

                            pResult.GetDataOK();
                        } else {
                            gvSystem.setApp_AccessToken(null);
                            gvSystem.setApp_RefreshToken(null);
                            gvSystem.App_DanhSachChucNang(new ArrayList<>());
                            pResult.GetDataFailed(new Exception("Phiên đăng nhập đã hết hạn."));
                        }
                    } else {
                        gvSystem.setApp_AccessToken(null);
                        gvSystem.setApp_RefreshToken(null);
                        gvSystem.App_DanhSachChucNang(new ArrayList<>());
                        pResult.GetDataFailed(new Exception("Không có dữ liệu trả về"));
                    }
                } else {
                    gvSystem.setApp_AccessToken(null);
                    gvSystem.setApp_RefreshToken(null);
                    gvSystem.App_DanhSachChucNang(new ArrayList<>());
                    Gson gson = new Gson();
                    Type type = new TypeToken<ResMod_Base>() {
                    }.getType();
                    assert response.errorBody() != null;
                    ResMod_Base mResponseModel = gson.fromJson(response.errorBody().charStream(), type);
                    if (mResponseModel != null)
                        pResult.GetDataFailed(new Exception(mResponseModel.getDataError() + ": " + mResponseModel.getDataErrorDescription()));
                    else
                        pResult.GetDataFailed(new Exception(response.toString()));
                }
                call.cancel();
            }

            @Override
            public void onFailure(@NonNull Call<ResMod_RefreshToken> call, @NonNull Throwable t) {
                gvSystem.setApp_AccessToken(null);
                gvSystem.setApp_RefreshToken(null);
                gvSystem.App_DanhSachChucNang(new ArrayList<>());
                pResult.GetDataFailed(t);
                call.cancel();
            }
        });
    }

}
