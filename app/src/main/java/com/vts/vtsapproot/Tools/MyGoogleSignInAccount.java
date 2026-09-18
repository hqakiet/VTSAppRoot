package com.vts.vtsapproot.Tools;

import android.net.Uri;

public class MyGoogleSignInAccount {
    String Email;
    String DisplayName;
    String FamilyName;
    String GivenName;
    Uri ProfilePictureUri;

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getFamilyName() {
        return FamilyName;
    }

    public void setFamilyName(String familyName) {
        FamilyName = familyName;
    }

    public String getGivenName() {
        return GivenName;
    }

    public void setGivenName(String givenName) {
        GivenName = givenName;
    }

    public Uri getProfilePictureUri() {
        return ProfilePictureUri;
    }

    public void setProfilePictureUri(Uri profilePictureUri) {
        ProfilePictureUri = profilePictureUri;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
