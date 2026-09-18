package com.vts.vtsapproot.API.Request;

public class ReqMod_RefreshToken {
    String TokenID;
    String ClientToken;

    public ReqMod_RefreshToken(String pTokenID, String pClientToken) {
        TokenID = pTokenID;
        ClientToken = pClientToken;
    }

    public ReqMod_RefreshToken(String pTokenID) {
        TokenID = pTokenID;
    }
}
