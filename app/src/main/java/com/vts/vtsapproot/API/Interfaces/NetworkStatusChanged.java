package com.vts.vtsapproot.API.Interfaces;

import android.net.Network;

import androidx.annotation.NonNull;

public interface NetworkStatusChanged {
    public void onNetworkStatusAvailable(@NonNull Network network);
    public void onNetworkStatusLost(@NonNull Network network);
}
