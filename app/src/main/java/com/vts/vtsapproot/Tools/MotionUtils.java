package com.vts.vtsapproot.Tools;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

public class MotionUtils {

    @SuppressLint("ClickableViewAccessibility")

    public static void applyBounceEffect(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Hiệu ứng lún xuống
                    v.animate()
                            .scaleX(0.92f)
                            .scaleY(0.92f)
                            .setDuration(100)
                            .start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Hiệu ứng bung lên lại
                    v.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(150)
                            .start();
                    break;
            }
            return true;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void applyBounceEffect(View view, Runnable onActionUp) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Hiệu ứng lún xuống
                    v.animate()
                            .scaleX(0.92f)
                            .scaleY(0.92f)
                            .setDuration(100)
                            .start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Hiệu ứng bung lên lại
                    v.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(150)
                            .withEndAction(() -> {
                                if (event.getAction() == MotionEvent.ACTION_UP && onActionUp != null) {
                                    onActionUp.run(); // Chạy chuyển cảnh ở đây
                                }
                            })
                            .start();
                    break;
            }
            return true;
        });
    }

}
