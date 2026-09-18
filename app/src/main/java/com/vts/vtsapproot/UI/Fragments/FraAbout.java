package com.vts.vtsapproot.UI.Fragments;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.textview.MaterialTextView;
import com.vts.vtsapproot.ActPolicy2;
import com.vts.vtsapproot.R;
import com.vts.vtsapproot.Tools.FragmentBase;
import com.vts.vtsapproot.Tools.SingleClickListener;
import com.vts.vtsapproot.Tools.gvSystem;

public class FraAbout
        extends FragmentBase {

    ImageView FraAbout_ImageView_AppIcon;

    MaterialTextView FraAbout_TextView_AppVersion;

    MaterialTextView FraAbout_MaterialTextView_Terms;
    MaterialTextView FraAbout_MaterialTextView_Privacy;

    public FraAbout() {
        super();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.lay_base_fraabout, container, false);

        AppBarLayout FraAbout_AppBarLayout = mView.findViewById(R.id.FraAbout_AppBarLayout);
        final LinearLayout FraAbout_LinearLayout_ToolbarContent = mView.findViewById(R.id.FraAbout_LinearLayout_ToolbarContent);
        final ImageView FraAbout_ImageView_BigLogo = mView.findViewById(R.id.FraAbout_ImageView_BigLogo);

        FraAbout_AppBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            // Tính toán tỷ lệ cuộn (từ 0.0 đến 1.0)
            float percentage = (float) Math.abs(verticalOffset) / appBarLayout1.getTotalScrollRange();

            // 1. Khi cuộn lên (percentage tiến về 1): Hiện cụm Toolbar nhỏ, ẩn Logo lớn
            FraAbout_LinearLayout_ToolbarContent.setAlpha(percentage);

            // 2. Khi cuộn xuống (percentage tiến về 0): Hiện Logo lớn, ẩn cụm Toolbar nhỏ
            FraAbout_ImageView_BigLogo.setAlpha(1 - percentage);

            // Hiệu ứng "Công nghệ": Bạn có thể scale nhỏ logo lớn khi cuộn lên
//                float scale = 1 - (percentage * 0.5f); // Scale từ 1.0 về 0.5
//                imgBigLogo.setScaleX(scale);
//                imgBigLogo.setScaleY(scale);
        });

        FraAbout_TextView_AppVersion = mView.findViewById(R.id.FraAbout_TextView_AppVersion);
//        FraAbout_TextView_AppVersion.setText(BuildConfig.VERSION_NAME);

        FraAbout_ImageView_AppIcon = mView.findViewById(R.id.FraAbout_ImageView_AppIcon);
        FraAbout_ImageView_AppIcon.setOnLongClickListener(view -> {
            Toast.makeText(requireContext(), gvSystem.App_BaseUrl, Toast.LENGTH_SHORT).show();
            return false;
        });

        FraAbout_MaterialTextView_Terms = mView.findViewById(R.id.FraAbout_MaterialTextView_Terms);
        FraAbout_MaterialTextView_Terms.setOnClickListener(
                new SingleClickListener() {
                    @Override
                    public void safeSingleClick(View v) {
                        Intent mIntent = new Intent(requireContext(), ActPolicy2.class);
                        mIntent.putExtra("StartTab", "Terms_Service");
                        if (!gvSystem.getApp_TietKiemPin()) {
                            String mTransitionName = "Terms_Service";
                            FraAbout_MaterialTextView_Terms.setTransitionName(null);
                            FraAbout_MaterialTextView_Terms.setTransitionName(mTransitionName);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(requireActivity(), FraAbout_MaterialTextView_Terms, mTransitionName);

                            mIntent.putExtra("TransitionName", mTransitionName);

                            FraAbout_MaterialTextView_Terms.postDelayed(() -> startActivity(mIntent, options.toBundle()), 150);
                        } else {
                            FraAbout_MaterialTextView_Terms.postDelayed(() -> startActivity(mIntent), 150);
                        }
                    }
                }
        );
        FraAbout_MaterialTextView_Privacy = mView.findViewById(R.id.FraAbout_MaterialTextView_Privacy);
        FraAbout_MaterialTextView_Privacy.setOnClickListener(
                new SingleClickListener() {
                    @Override
                    public void safeSingleClick(View v) {
                        Intent mIntent = new Intent(requireContext(), ActPolicy2.class);
                        mIntent.putExtra("StartTab", "Privacy_Policies");
                        if (!gvSystem.getApp_TietKiemPin()) {
                            String mTransitionName = "Privacy_Policies";
                            FraAbout_MaterialTextView_Privacy.setTransitionName(null);
                            FraAbout_MaterialTextView_Privacy.setTransitionName(mTransitionName);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(requireActivity(), FraAbout_MaterialTextView_Privacy, mTransitionName);

                            mIntent.putExtra("TransitionName", mTransitionName);

                            FraAbout_MaterialTextView_Privacy.postDelayed(() -> startActivity(mIntent, options.toBundle()), 150);
                        } else {
                            FraAbout_MaterialTextView_Privacy.postDelayed(() -> startActivity(mIntent), 150);
                        }
                    }
                }
        );

        return mView;
    }

}