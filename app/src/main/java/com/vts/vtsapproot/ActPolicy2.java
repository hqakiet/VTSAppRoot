package com.vts.vtsapproot;

import android.os.Bundle;
import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vts.vtsapproot.Tools.ActBase;
import com.vts.vtsapproot.Tools.SingleClickListener;
import com.vts.vtsapproot.Tools.gvSystem;
import com.vts.vtsapproot.UI.Adapters.FragmentState_Policy;

public class ActPolicy2
        extends ActBase {

    MaterialToolbar ActPolicy2_MaterialToolbar;

    TabLayout ActPolicy2_TabLayout;

    ViewPager2 ActPolicy2_ViewPager2;

    String My_StartTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_base_actpolicy2);

        My_TransitionName = !gvSystem.getApp_TietKiemPin() ? getIntent().getStringExtra("TransitionName") : null;
        DoPostponeEnterTransition();

        My_StartTab = getIntent().getStringExtra("StartTab");

        ActPolicy2_MaterialToolbar = findViewById(R.id.ActPolicy2_MaterialToolbar);
        ActPolicy2_MaterialToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        ActPolicy2_MaterialToolbar.setNavigationOnClickListener(new SingleClickListener() {
            @Override
            public void safeSingleClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
        My_TransitionObject = ActPolicy2_MaterialToolbar;
        setupOnBackPressed();

        ActPolicy2_TabLayout = findViewById(R.id.ActPolicy2_TabLayout);

        ActPolicy2_ViewPager2 = findViewById(R.id.ActPolicy2_ViewPager2);
        ActPolicy2_ViewPager2.setAdapter(new FragmentState_Policy(this));

        new TabLayoutMediator(ActPolicy2_TabLayout, ActPolicy2_ViewPager2, (tab, position) -> {
            switch (position) {
                case 0: tab.setText(getString(R.string.Message_Caption_Term)); break;
                case 1: tab.setText(getString(R.string.Message_Caption_Privacy)); break;
            }
        }).attach();

        if ("Terms_Service".equals(My_StartTab)) {
            ActPolicy2_ViewPager2.setCurrentItem(0, false); // false để tắt smooth scroll
        } else {
            ActPolicy2_ViewPager2.setCurrentItem(1, false);
        }

        DoStartPostponedEnterTransition();
    }

}