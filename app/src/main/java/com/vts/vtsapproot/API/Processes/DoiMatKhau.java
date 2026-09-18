package com.vts.vtsapproot.API.Processes;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vts.vtsapproot.API.APIService;
import com.vts.vtsapproot.API.Request.ReqMod_DoiMatKhau;
import com.vts.vtsapproot.API.Response.ResMod_Base;
import com.vts.vtsapproot.Tools.gvSystem;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoiMatKhau {

    public static void PostData(String pMatKhau_Old, String pMatKhau_New, APIService.DoAPI_PostData_Result_DoiMatKhau pResult) {
        PostData(gvSystem.getApp_ConnectTimeOut(), pMatKhau_Old, pMatKhau_New, pResult);
    }

    public static void PostData(int pTimeOut, String pMatKhau_Old, String pMatKhau_New, APIService.DoAPI_PostData_Result_DoiMatKhau pResult) {
        APIService mAPIService = gvSystem.GetAPIService(pTimeOut);
        ReqMod_DoiMatKhau pRequest = new ReqMod_DoiMatKhau(pMatKhau_Old, pMatKhau_New);
        Call<ResMod_Base> call = mAPIService.DoAPI_PostData_DoiMatKhau(gvSystem.App_BaseUrl + "settings/DoiMatKhau", pRequest);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResMod_Base> call, @NonNull Response<ResMod_Base> response) {
                if (response.isSuccessful()) {
                    ResMod_Base mResponseModel = response.body();
                    if (mResponseModel != null) {
                        if (mResponseModel.getDataError() == 0)
                            pResult.ResultOK(mResponseModel);
                        else if (mResponseModel.getDataError() == -107)
                            pResult.TokenExpired();
                        else if (mResponseModel.getDataError() == -108) {
                            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(
                                    () -> Authen.RefreshToken(new APIService.DoAPI_Authen_Result() {
                                        @Override
                                        public void GetDataOK() {
                                            PostData(pTimeOut, pMatKhau_Old, pMatKhau_New, pResult);
                                        }

                                        @Override
                                        public void GetDataFailed(Throwable t) {
                                            pResult.TokenExpired();
                                        }
                                    }), 1500);
                        } else
                            pResult.ResultFailed(new Exception(mResponseModel.getDataErrorDescription()));
                    } else
                        pResult.ResultFailed(new Exception("Không có dữ liệu"));
                } else {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ResMod_Base>() {
                    }.getType();
                    assert response.errorBody() != null;
                    ResMod_Base mResponseModel = gson.fromJson(response.errorBody().charStream(), type);
                    if (mResponseModel != null)
                        pResult.ResultFailed(new Exception(mResponseModel.getDataError() + ": " + mResponseModel.getDataErrorDescription()));
                    else
                        pResult.ResultFailed(new Exception(response.toString()));
                }
                call.cancel();
            }

            @Override
            public void onFailure(@NonNull Call<ResMod_Base> call, @NonNull Throwable t) {
                pResult.ResultFailed(t);
                call.cancel();
            }
        });
    }

}
