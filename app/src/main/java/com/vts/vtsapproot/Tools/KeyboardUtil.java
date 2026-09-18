package com.vts.vtsapproot.Tools;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class KeyboardUtil {

    /**
     * Gắn bùa chống che bàn phím và sửa lỗi dư khoảng trống cho riêng màn hình cần thiết.
     * Chấp mọi loại thanh điều hướng (3 nút bấm truyền thống hoặc Cử chỉ vuốt 1 lằn).
     *
     * @param rootLayout Thẻ layout ngoài cùng trong XML của Activity (Ví dụ: LinearLayout_Root)
     * @param scrollView Thẻ ScrollView bọc nội dung nhập liệu
     */
    public static void assistActivity(final View rootLayout, final ScrollView scrollView) {
        if (rootLayout == null || scrollView == null) return;

        ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                WindowInsetsCompat insets = ViewCompat.getRootWindowInsets(rootLayout);
                if (insets == null) return;

                // Lấy độ cao tuyệt đối của bàn phím ảo (IME)
                int keyboardHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
                int bottomMargin = 0;
                if (keyboardHeight > 0) {
                    if (rootLayout.getLayoutParams() instanceof FrameLayout.LayoutParams lp) {
                        bottomMargin = lp.bottomMargin;
                    }

//                    // 2. PHÉP TÍNH PADDING KHÍT RỊT CHO CẢ 3 NÚT LẪN CỬ CHỈ:
                    int targetPadding;
                    if (insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom < 70) {
                        targetPadding = keyboardHeight - insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
                                - insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
                                - bottomMargin;
                    } else {
                        targetPadding = keyboardHeight - insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
                                - bottomMargin;
                    }

                    if (targetPadding < 0) targetPadding = 0;
                    scrollView.setPadding(0, 0, 0, targetPadding);

                    // 3. Cuộn mượt mà đưa ô đang gõ lên vị trí nhìn thấy tốt nhất
                    final View focusedView = rootLayout.findFocus();
                    if (focusedView != null) {
                        scrollView.post(() -> {
                            int[] loc = new int[2];
                            focusedView.getLocationOnScreen(loc);
                            int[] scrollLoc = new int[2];
                            scrollView.getLocationOnScreen(scrollLoc);

                            // Tính toán vị trí Y tương đối của View đang focus đối với ScrollView
                            int relativeTop = loc[1] - scrollLoc[1] + scrollView.getScrollY();
                            int scrollToY = relativeTop - 0; // Thay đổi số 0 này thành 100~150 nếu muốn ô gõ cách đỉnh màn hình một chút

                            if (scrollToY < 0) scrollToY = 0;
                            scrollView.smoothScrollTo(0, scrollToY);
                        });
                    }
                } else {
                    scrollView.setPadding(0, 0, 0, 0);
                }
            }
        };

        // Đăng ký lắng nghe
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

        // Tự động hủy lắng nghe khi View bị đóng (Chống rò rỉ bộ nhớ - Memory Leak)
        rootLayout.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
                rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
            }
        });
    }

    public static void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}