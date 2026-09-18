package com.vts.vtsapproot.API;

import com.vts.vtsapproot.API.Request.ReqMod_DangNhap;
import com.vts.vtsapproot.API.Request.ReqMod_DoiMatKhau;
import com.vts.vtsapproot.API.Request.ReqMod_RefreshToken;
import com.vts.vtsapproot.API.Response.ResMod_Base;
import com.vts.vtsapproot.API.Response.ResMod_DangNhap;
import com.vts.vtsapproot.API.Response.ResMod_GiaTriHeThong;
import com.vts.vtsapproot.API.Response.ResMod_RefreshToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface APIService {
    @POST
    Call<ResMod_DangNhap> DoAPI_DangNhap(@Url String url, @Body ReqMod_DangNhap pRequestModel);

    @POST
    Call<ResMod_GiaTriHeThong> DoAPI_GetData_GiaTriHeThong(@Url String url);

    @POST
    Call<ResMod_RefreshToken> DoAPI_RefreshToken(@Url String url, @Body ReqMod_RefreshToken pRequestModel);

    @POST
    Call<ResMod_Base> DoAPI_PostData_DoiMatKhau(@Url String url, @Body ReqMod_DoiMatKhau requestModel);

    interface DoAPI_Authen_Result {
        void GetDataOK();

        void GetDataFailed(Throwable t);
    }

    interface APIResult<T> {
        void GetDataOK(T response);

        void GetDataFailed(Throwable t);

        void TokenExpired();
    }

    interface APIPostResult<T> {
        void ResultOK(T response);

        void ResultFailed(Throwable t);

        void TokenExpired();
    }

    interface DoAPI_PostData_Result_DoiMatKhau extends APIPostResult<ResMod_Base> {
    }

}