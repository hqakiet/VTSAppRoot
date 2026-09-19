package com.vts.vtsapproot.UI.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vts.vtsapproot.R;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FraPrivacy
        extends Fragment {

    ScrollView FraPrivacy_ScrollView;

    WebView FraPrivacy_WebView_Content;

    FloatingActionButton FloatingActionButton_GoToBot;
    FloatingActionButton FloatingActionButton_BackToTop;

    final Handler HideFloatingActionButtonsHandler = new Handler(Looper.getMainLooper());
    final Runnable HideFloatingActionButtons = () -> {
        if (FloatingActionButton_BackToTop != null) FloatingActionButton_BackToTop.hide();
        if (FloatingActionButton_GoToBot != null) FloatingActionButton_GoToBot.hide();
    };

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.lay_base_fraprivacy, container, false);

        FraPrivacy_ScrollView = mView.findViewById(R.id.FraPrivacy_ScrollView);
        FraPrivacy_ScrollView.setOnScrollChangeListener((view, i, i1, i2, i3) -> {
            boolean isAtTop = !view.canScrollVertically(-1);
            if (FloatingActionButton_BackToTop != null) {
                if (isAtTop) FloatingActionButton_BackToTop.hide();
            }
            boolean isAtBot = !view.canScrollVertically(1);
            if (FloatingActionButton_GoToBot != null) {
                if (isAtBot) FloatingActionButton_GoToBot.hide();
            }
            if (Math.abs(i3 - i1) > 5) {
                RefreshFloatingButton();
            }
        });

        FloatingActionButton_GoToBot = mView.findViewById(R.id.FloatingActionButton_GoToBot);
        FloatingActionButton_GoToBot.setOnClickListener(v -> {
            if (FraPrivacy_ScrollView == null) return;
            FraPrivacy_ScrollView.post(() -> {
                FraPrivacy_ScrollView.fullScroll(View.FOCUS_DOWN);
            });
            HideFloatingActionButtonsHandler.postDelayed(this::RefreshFloatingButton, 150);
        });

        FloatingActionButton_BackToTop = mView.findViewById(R.id.FloatingActionButton_BackToTop);
        FloatingActionButton_BackToTop.setOnClickListener(v -> {
            if (FraPrivacy_ScrollView == null) return;
            FraPrivacy_ScrollView.post(() -> FraPrivacy_ScrollView.scrollTo(0, 0));
            HideFloatingActionButtonsHandler.postDelayed(this::RefreshFloatingButton, 150);
        });

        FraPrivacy_WebView_Content = mView.findViewById(R.id.FraPrivacy_WebView_Content);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
//            FraPrivacy_WebView_Content.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            FraPrivacy_WebView_Content.getSettings().setJavaScriptEnabled(true);
//            FraPrivacy_WebView_Content.loadUrl(gvSystem.App_BaseUrl0 + "/privacy/index.html");
//        } else {
            String finalHtml = loadHtmlWithAppName(requireContext(), "privacy.html");
            FraPrivacy_WebView_Content.getSettings().setJavaScriptEnabled(true);
            FraPrivacy_WebView_Content.getSettings().setAllowFileAccess(true);
            FraPrivacy_WebView_Content.getSettings().setDomStorageEnabled(true);

            FraPrivacy_WebView_Content.loadDataWithBaseURL(
                "file:///android_asset/", // Đảm bảo WebView vẫn load được CSS/hình ảnh trong assets
                finalHtml,
                "text/html",
                "UTF-8",
                null
        );

//            FraPrivacy_WebView_Content.loadUrl("file:///android_asset/privacy.html");
//        }

        HideFloatingActionButtonsHandler.postDelayed(this::RefreshFloatingButton, 150);

        return mView;
    }

    @Override
    public void onPause() {
        HideFloatingActionButtonsHandler.removeCallbacks(HideFloatingActionButtons);
        super.onPause();
    }

    @Override
    public void onStop() {
        HideFloatingActionButtonsHandler.removeCallbacks(HideFloatingActionButtons);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        HideFloatingActionButtonsHandler.removeCallbacks(HideFloatingActionButtons);
        super.onDestroy();
    }

    protected void RefreshFloatingButton() {
        if (
                FraPrivacy_ScrollView == null ||
                        FloatingActionButton_BackToTop == null ||
                        FloatingActionButton_GoToBot == null
        ) return;

        boolean showTop = FraPrivacy_ScrollView.canScrollVertically(-1);
        boolean showBot = FraPrivacy_ScrollView.canScrollVertically(1);

        if (showTop) {
            FloatingActionButton_BackToTop.show(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onShown(FloatingActionButton fab) {
                    super.onShown(fab);
                    fab.setAlpha(0.75f);
                }
            });
        } else {
            FloatingActionButton_BackToTop.hide();
        }

        if (showBot) {
            FloatingActionButton_GoToBot.show(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onShown(FloatingActionButton fab) {
                    super.onShown(fab);
                    fab.setAlpha(0.75f);
                }
            });
        } else {
            FloatingActionButton_GoToBot.hide();
        }

        HideFloatingActionButtonsHandler.removeCallbacks(HideFloatingActionButtons);
        if (showTop || showBot) {
            HideFloatingActionButtonsHandler.postDelayed(HideFloatingActionButtons, 2000);
        }
    }

    public static String loadHtmlWithAppName(Context context, String assetFileName) {
        try {
            InputStream is = context.getAssets().open(assetFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String htmlContent = new String(buffer, StandardCharsets.UTF_8);

            // 1. Lấy giá trị app_name từ string.xml (hoặc từ gvSystem)
            String appName = context.getString(R.string.app_name);

            // 2. Thay thế placeholder {{APP_NAME}} bằng tên app thực tế
            htmlContent = htmlContent.replace("{{APP_NAME}}", appName);

            // Hoặc nếu HTML của bạn đang để chữ "VTS - Staff", bạn có thể replace trực tiếp:
            // htmlContent = htmlContent.replace("VTS - Staff", appName);

            return htmlContent;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
