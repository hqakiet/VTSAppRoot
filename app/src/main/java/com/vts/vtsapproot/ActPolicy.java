package com.vts.vtsapproot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vts.vtsapproot.Tools.ActBase;
import com.vts.vtsapproot.Tools.SingleClickListener;
import com.vts.vtsapproot.Tools.gvSystem;
import com.vts.vtsapproot.UI.Adapters.FragmentState_Policy;

public class ActPolicy
        extends ActBase {

    MaterialToolbar ActPolicy_MaterialToolbar;

    TabLayout ActPolicy_TabLayout;

    ViewPager2 ActPolicy_ViewPager2;

    MaterialButton ActPolicy_MaterialButton_Agreement;

    String My_StartTab;

    boolean My_LoginConfirmed = false;

    public ActPolicy() {
        super();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("My_LoginConfirmed", My_LoginConfirmed);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_base_actpolicy);

        My_TransitionName = !gvSystem.getApp_TietKiemPin() ? getIntent().getStringExtra("TransitionName") : null;
        DoPostponeEnterTransition();

        My_StartTab = getIntent().getStringExtra("StartTab");

        try {
            My_LoginConfirmed = getIntent().getBooleanExtra("LoginConfirmed", false);
        } catch (Exception ignored) {
        }

        ActPolicy_MaterialToolbar = findViewById(R.id.ActPolicy_MaterialToolbar);
        ActPolicy_MaterialToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        ActPolicy_MaterialToolbar.setNavigationOnClickListener(new SingleClickListener() {
            @Override
            public void safeSingleClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
        My_TransitionObject = ActPolicy_MaterialToolbar;
        setupOnBackPressed();

        ActPolicy_TabLayout = findViewById(R.id.ActPolicy_TabLayout);

        ActPolicy_ViewPager2 = findViewById(R.id.ActPolicy_ViewPager2);
        ActPolicy_ViewPager2.setAdapter(new FragmentState_Policy(this));

        new TabLayoutMediator(ActPolicy_TabLayout, ActPolicy_ViewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getString(R.string.Message_Caption_Term));
                    break;
                case 1:
                    tab.setText(getString(R.string.Message_Caption_Privacy));
                    break;
            }
        }).attach();

        if ("Terms_Service".equals(My_StartTab)) {
            ActPolicy_ViewPager2.setCurrentItem(0, false); // false để tắt smooth scroll
        } else {
            ActPolicy_ViewPager2.setCurrentItem(1, false);
        }

        ActPolicy_MaterialButton_Agreement = findViewById(R.id.ActPolicy_MaterialButton_Agreement);
        ActPolicy_MaterialButton_Agreement.setIconResource(
                My_LoginConfirmed
                        ? R.drawable.ic_check
                        : R.drawable.ic_uncheck);
        ActPolicy_MaterialButton_Agreement.setOnClickListener(new SingleClickListener() {
            @Override
            public void safeSingleClick(View v) {
                ActPolicy_MaterialButton_Agreement.setIconResource(R.drawable.ic_check);
                Intent intent = new Intent();
                intent.setAction("Policy.ConfirmOK");
                intent.setPackage(getPackageName());
                sendBroadcast(intent);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        if (savedInstanceState != null) {
            new android.os.Handler(android.os.Looper.getMainLooper()).post(
                    () -> {
                        boolean mValue = savedInstanceState.getBoolean("My_LoginConfirmed", false);
                        ActPolicy_MaterialButton_Agreement.setIconResource(
                                mValue
                                        ? R.drawable.ic_check
                                        : R.drawable.ic_uncheck
                        );
                    }
            );
        }

        DoStartPostponedEnterTransition();

    }

}