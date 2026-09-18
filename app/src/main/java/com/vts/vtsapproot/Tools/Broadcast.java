package com.vts.vtsapproot.Tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Broadcast
        extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
       intent.getStringExtra("string_test");
    }
}
