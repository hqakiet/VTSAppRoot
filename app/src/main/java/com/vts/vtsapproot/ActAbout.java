package com.vts.vtsapproot;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.shimmer.BuildConfig;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;
import com.vts.vtsapproot.Tools.ActBase;
import com.vts.vtsapproot.Tools.SingleClickListener;
import com.vts.vtsapproot.Tools.gvSystem;

public class ActAbout
        extends ActBase {

    MaterialToolbar ActAbout_MaterialToolbar;

    ImageView ActAbout_ImageView_AppIcon;

    MaterialTextView ActAbout_MaterialTextView_AppVersion;

    MaterialTextView ActAbout_MaterialTextView_Terms;
    MaterialTextView ActAbout_MaterialTextView_Privacy;

    public ActAbout() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_base_actabout);

        My_TransitionName = !gvSystem.getApp_TietKiemPin() ? getIntent().getStringExtra("TransitionName") : null;
        DoPostponeEnterTransition();

        AppBarLayout ActAbout_AppBarLayout = findViewById(R.id.ActAbout_AppBarLayout);
        final LinearLayout ActAbout_LinearLayout_ToolbarContent = findViewById(R.id.ActAbout_LinearLayout_ToolbarContent);
        final ImageView ActAbout_ImageView_BigLogo = findViewById(R.id.ActAbout_ImageView_BigLogo);

        ActAbout_AppBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            // Tính toán tỷ lệ cuộn (từ 0.0 đến 1.0)
            float percentage = (float) Math.abs(verticalOffset) / appBarLayout1.getTotalScrollRange();

            // 1. Khi cuộn lên (percentage tiến về 1): Hiện cụm Toolbar nhỏ, ẩn Logo lớn
            ActAbout_LinearLayout_ToolbarContent.setAlpha(percentage);

            // 2. Khi cuộn xuống (percentage tiến về 0): Hiện Logo lớn, ẩn cụm Toolbar nhỏ
            ActAbout_ImageView_BigLogo.setAlpha(1 - percentage);

            // Hiệu ứng "Công nghệ": Bạn có thể scale nhỏ logo lớn khi cuộn lên
//                float scale = 1 - (percentage * 0.5f); // Scale từ 1.0 về 0.5
//                imgBigLogo.setScaleX(scale);
//                imgBigLogo.setScaleY(scale);
        });

        ActAbout_MaterialToolbar = findViewById(R.id.ActAbout_MaterialToolbar);
        ActAbout_MaterialToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        ActAbout_MaterialToolbar.setNavigationOnClickListener(new SingleClickListener() {
            @Override
            public void safeSingleClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
        My_TransitionObject = ActAbout_ImageView_BigLogo;
        setupOnBackPressed();

        ActAbout_MaterialTextView_AppVersion = findViewById(R.id.ActAbout_MaterialTextView_AppVersion);
        ActAbout_MaterialTextView_AppVersion.setText(BuildConfig.VERSION_NAME);
        ActAbout_ImageView_AppIcon = findViewById(R.id.ActAbout_ImageView_AppIcon);
        ActAbout_ImageView_AppIcon.setOnLongClickListener(view -> {
            Toast.makeText(ActAbout.this, gvSystem.App_BaseUrl, Toast.LENGTH_SHORT).show();
            return false;
        });

        ActAbout_MaterialTextView_Terms = findViewById(R.id.ActAbout_MaterialTextView_Terms);
        ActAbout_MaterialTextView_Terms.setOnClickListener(
                new SingleClickListener() {
                    @Override
                    public void safeSingleClick(View v) {
                        Intent mIntent = new Intent(ActAbout.this, ActPolicy2.class);
                        mIntent.putExtra("StartTab", "Terms_Service");
                        if (!gvSystem.getApp_TietKiemPin()) {
                            String mTransitionName = "Terms_Service";
                            ActAbout_MaterialTextView_Terms.setTransitionName(null);
                            ActAbout_MaterialTextView_Terms.setTransitionName(mTransitionName);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ActAbout.this, ActAbout_MaterialTextView_Terms, mTransitionName);

                            mIntent.putExtra("TransitionName", mTransitionName);

                            ActAbout_MaterialTextView_Terms.postDelayed(() -> startActivity(mIntent, options.toBundle()), 150);
                        } else {
                            ActAbout_MaterialTextView_Terms.postDelayed(() -> startActivity(mIntent), 150);
                        }
                    }
                }
        );

        ActAbout_MaterialTextView_Privacy = findViewById(R.id.ActAbout_MaterialTextView_Privacy);
        ActAbout_MaterialTextView_Privacy.setOnClickListener(new SingleClickListener() {
            @Override
            public void safeSingleClick(View v) {
                Intent mIntent = new Intent(ActAbout.this, ActPolicy2.class);
                mIntent.putExtra("StartTab", "Privacy_Policies");
                if (!gvSystem.getApp_TietKiemPin()) {
                    String mTransitionName = "Privacy_Policies";
                    ActAbout_MaterialTextView_Privacy.setTransitionName(null);
                    ActAbout_MaterialTextView_Privacy.setTransitionName(mTransitionName);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ActAbout.this, ActAbout_MaterialTextView_Privacy, mTransitionName);

                    mIntent.putExtra("TransitionName", mTransitionName);

                    ActAbout_MaterialTextView_Privacy.postDelayed(() -> startActivity(mIntent, options.toBundle()), 150);
                } else {
                    ActAbout_MaterialTextView_Privacy.postDelayed(() -> startActivity(mIntent), 150);
                }
            }
        });

        DoStartPostponedEnterTransition();
    }

}