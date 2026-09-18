package com.vts.vtsapproot.API.Request;

public class ReqMod_DangNhap {
    private String TokenID;
    private String User;
    private String Pass;
    private String ClientToken;

    private ReqMod_DangNhap(Builder builder) {
        this.TokenID = builder.tokenID;
        this.User = builder.user;
        this.Pass = builder.pass;
        this.ClientToken = builder.clientToken;
    }

    public static class Builder {
        private String tokenID;
        private String user;
        private String pass;
        private String clientToken;

        public Builder tokenID(String tokenID) { this.tokenID = tokenID; return this; }
        public Builder user(String user) { this.user = user; return this; }
        public Builder pass(String pass) { this.pass = pass; return this; }
        public Builder clientToken(String clientToken) { this.clientToken = clientToken; return this; }

        public ReqMod_DangNhap build() {
            return new ReqMod_DangNhap(this);
        }
    }
}
