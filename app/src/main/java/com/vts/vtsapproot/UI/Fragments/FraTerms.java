package com.vts.vtsapproot.UI.Fragments;

import android.annotation.SuppressLint;
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
import com.vts.vtsapproot.Tools.gvSystem;

public class FraTerms
        extends Fragment {

    ScrollView FraTerm_ScrollView;

    WebView FraTerm_WebView_Content;

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
        View mView = inflater.inflate(R.layout.lay_base_fraterm, container, false);

        FraTerm_ScrollView = mView.findViewById(R.id.FraTerm_ScrollView);
        FraTerm_ScrollView.setOnScrollChangeListener((view, i, i1, i2, i3) -> {
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
            if (FraTerm_ScrollView == null) return;
            FraTerm_ScrollView.post(() -> {
                FraTerm_ScrollView.fullScroll(View.FOCUS_DOWN);
            });
            HideFloatingActionButtonsHandler.postDelayed(this::RefreshFloatingButton, 150);
        });

        FloatingActionButton_BackToTop = mView.findViewById(R.id.FloatingActionButton_BackToTop);
        FloatingActionButton_BackToTop.setOnClickListener(v -> {
            if (FraTerm_ScrollView == null) return;
            FraTerm_ScrollView.post(() -> FraTerm_ScrollView.scrollTo(0, 0));
            HideFloatingActionButtonsHandler.postDelayed(this::RefreshFloatingButton, 150);
        });

        FraTerm_WebView_Content = mView.findViewById(R.id.FraTerm_WebView_Content);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
//            FraTerm_WebView_Content.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            FraTerm_WebView_Content.getSettings().setJavaScriptEnabled(true);
//            FraTerm_WebView_Content.loadUrl(gvSystem.App_BaseUrl0 + "/terms/index.html");
//        } else {
            String finalHtml = gvSystem.loadHtmlWithAppName(requireContext(), "terms.html");
            FraTerm_WebView_Content.getSettings().setJavaScriptEnabled(true);
            FraTerm_WebView_Content.loadDataWithBaseURL(
                    "file:///android_asset/",
                    finalHtml,
                    "text/html",
                    "UTF-8",
                    null
            );
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
                FraTerm_ScrollView == null ||
                        FloatingActionButton_BackToTop == null ||
                        FloatingActionButton_GoToBot == null
        ) return;

        boolean showTop = FraTerm_ScrollView.canScrollVertically(-1);
        boolean showBot = FraTerm_ScrollView.canScrollVertically(1);

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

}
