package com.vts.vtsapproot.Tools;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.vts.vtsapproot.R;

public class PopupMenuBase  extends PopupWindow {
    private final View contentView;
    private final Context context;

    public interface OnMenuClickListener {
        void onClick(int viewId);
    }

    private OnMenuClickListener listener;

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        this.listener = listener;
    }

    public PopupMenuBase(Context context, int layoutId) {
        super(context);
        this.context = context;
        this.contentView = LayoutInflater.from(context).inflate(layoutId, null);

        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        bindClickEvents((ViewGroup) contentView);
    }

    private void bindClickEvents(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof ViewGroup) {
                bindClickEvents((ViewGroup) v);
            }

            // Nếu View có đặt ID thì mới gán sự kiện
            if (v.getId() != View.NO_ID) {
                v.setOnClickListener(view -> {
                    if (listener != null) {
                        listener.onClick(view.getId());
                    }
                    dismiss(); // Click xong tự đóng cho khỏe
                });
            }
        }
    }

    public View findViewById(int id) {
        return contentView.findViewById(id);
    }

    public void showPopup(View viewAnchor, OnMenuClickListener listener) {
        this.listener = listener;
        showPopup(viewAnchor);
    }

    private void showPopup(View viewAnchor) {
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = contentView.getMeasuredWidth();
        int popupHeight = contentView.getMeasuredHeight();

        int[] location = new int[2];
        viewAnchor.getLocationOnScreen(location);
        int anchorX = location[0];
        int anchorY = location[1];
        int anchorWidth = viewAnchor.getWidth();
        int anchorHeight = viewAnchor.getHeight();

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

//        int marginEdge = (int) (24 * dm.density); // Cách mép màn hình
//        int marginFab = (int) (10 * dm.density);   // Khoảng cách hở giữa Popup và FAB

        // --- TÍNH TOÁN TRỤC X (Né FAB) ---
        int xOffset=0;
//        // Nếu nút FAB nằm ở nửa bên phải màn hình -> Hiện popup sang bên TRÁI nút
//        if (anchorX + (anchorWidth / 2) > screenWidth / 2) {
//            xOffset = -(popupWidth);
//        } else {
//            // Ngược lại hiện sang bên PHẢI nút
//            xOffset = anchorWidth;
//        }

        // --- TÍNH TOÁN TRỤC Y (Gióng hàng theo tâm) ---
        int yOffset;
        int anchorCenterY = anchorY + (anchorHeight / 2);

        // Kiểm tra không gian bên dưới tính từ tâm FAB
        boolean canShowBelow = (anchorCenterY + popupHeight) < screenHeight;

        if (canShowBelow) {
            setAnimationStyle(R.style.AppPopupAnimation_SlideDown);
            // Xổ xuống: Cạnh trên Popup ngang tâm FAB
            // (Gốc là cạnh dưới FAB nên phải trừ đi nửa nút)
            yOffset = -(anchorHeight);
            showAsDropDown(viewAnchor, xOffset, yOffset);
        } else {
            setAnimationStyle(R.style.AppPopupAnimation_SlideUp);
            // Xổ lên: Cạnh dưới Popup ngang tâm FAB
            // (Kéo lên hết popup và thêm nửa nút)
            yOffset = -(popupHeight);
            showAsDropDown(viewAnchor, xOffset, yOffset);
        }
    }

    public void showSmart(View viewAnchor, OnMenuClickListener listener) {
        this.listener = listener;
        showSmart(viewAnchor);
    }

    private void showSmart(View viewAnchor) {
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = contentView.getMeasuredWidth();
        int popupHeight = contentView.getMeasuredHeight();

        int[] location = new int[2];
        viewAnchor.getLocationOnScreen(location);
        int anchorX = location[0];
        int anchorY = location[1];
        int anchorWidth = viewAnchor.getWidth();
        int anchorHeight = viewAnchor.getHeight();

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        int marginEdge = (int) (24 * dm.density); // Cách mép màn hình
        int marginFab = (int) (10 * dm.density);   // Khoảng cách hở giữa Popup và FAB

        // --- TÍNH TOÁN TRỤC X (Né FAB) ---
        int xOffset;
        // Nếu nút FAB nằm ở nửa bên phải màn hình -> Hiện popup sang bên TRÁI nút
        if (anchorX + (anchorWidth / 2) > screenWidth / 2) {
            xOffset = -(popupWidth + marginFab + (int) (10 * dm.density));
        } else {
            // Ngược lại hiện sang bên PHẢI nút
            xOffset = anchorWidth + marginFab;
        }

        // --- TÍNH TOÁN TRỤC Y (Gióng hàng theo tâm) ---
        int yOffset;
        int anchorCenterY = anchorY + (anchorHeight / 2);

        // Kiểm tra không gian bên dưới tính từ tâm FAB
        boolean canShowBelow = (anchorCenterY + popupHeight + marginEdge) < screenHeight;

        if (canShowBelow) {
            setAnimationStyle(R.style.AppPopupAnimation_SlideDown);
            // Xổ xuống: Cạnh trên Popup ngang tâm FAB
            // (Gốc là cạnh dưới FAB nên phải trừ đi nửa nút)
            yOffset = -(anchorHeight / 2);
            showAsDropDown(viewAnchor, xOffset, yOffset);
        } else {
            setAnimationStyle(R.style.AppPopupAnimation_SlideUp);
            // Xổ lên: Cạnh dưới Popup ngang tâm FAB
            // (Kéo lên hết popup và thêm nửa nút)
            yOffset = -(popupHeight + (anchorHeight / 2));
            showAsDropDown(viewAnchor, xOffset, yOffset);
        }
    }

    // Overload để tiện truyền kèm cả Listener nếu cần giống như showSmart
    public void showAtTouch(View viewAnchor, MotionEvent event, OnMenuClickListener listener) {
        this.listener = listener;
        showAtTouch(viewAnchor, event);
    }

    private void showAtTouch(View viewAnchor, MotionEvent event) {
        if (event == null) return;

        // 1. Đo kích thước thực tế của popup trước khi vẽ
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = contentView.getMeasuredWidth();
        int popupHeight = contentView.getMeasuredHeight();

        // 2. Lấy tọa độ điểm chạm trên màn hình (RawX, RawY bao gồm cả thanh status bar)
        int touchX = (int) event.getRawX();
        int touchY = (int) event.getRawY();

        // 3. Lấy kích thước màn hình để phòng trường hợp popup bị tràn viền
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        int marginEdge = (int) (12 * dm.density); // Khoảng cách an toàn tránh dính sát mép màn hình

        // 4. Tính toán X (Tránh tràn sang phải)
        int xPosition = touchX;
        if (xPosition + popupWidth > screenWidth - marginEdge) {
            xPosition = screenWidth - popupWidth - marginEdge; // Đẩy ngược về bên trái
        }
        if (xPosition < marginEdge) {
            xPosition = marginEdge; // Phòng trường hợp màn hình quá nhỏ thì giữ lề trái
        }

        // 5. Tính toán Y và chọn Animation (Tránh tràn xuống dưới)
        int yPosition = touchY;
        boolean canShowBelow = (yPosition + popupHeight + marginEdge) < screenHeight;

        if (canShowBelow) {
            // Nếu đủ chỗ phía dưới: Hiện từ điểm chạm đi xuống
            setAnimationStyle(R.style.AppPopupAnimation_SlideDown);
        } else {
            // Nếu cấn cạnh dưới: Đẩy popup ngược lên trên điểm chạm
            setAnimationStyle(R.style.AppPopupAnimation_SlideUp);
            yPosition = touchY - popupHeight;
        }

        // 6. Hiển thị dựa trên hệ tọa độ gốc của màn hình (TOP - LEFT)
        showAtLocation(viewAnchor.getRootView(), Gravity.NO_GRAVITY, xPosition, yPosition);
    }

//    public void showSmart(View viewAnchor) {
//
//        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        int popupWidth = contentView.getMeasuredWidth();
//        int popupHeight = contentView.getMeasuredHeight();
//
//        int[] location = new int[2];
//        viewAnchor.getLocationOnScreen(location);
//        int anchorX = location[0];
//        int anchorY = location[1];
//        int anchorWidth = viewAnchor.getWidth();
//        int anchorHeight = viewAnchor.getHeight();
//
//        DisplayMetrics dm = context.getResources().getDisplayMetrics();
//        int screenWidth = dm.widthPixels;
//        int screenHeight = dm.heightPixels;
//
//        // --- CÀI ĐẶT KHOẢNG CÁCH (MARGIN) ---
//        // Ní có thể đổi mấy con số này tùy theo mắt thẩm mỹ của ní nhé
//        int marginEdge = (int) (15 * dm.density); // Cách mép màn hình 8dp
//        int marginAnchor = (int) (10 * dm.density); // Cách cái nút 4dp
//
//        // --- TÍNH TOÁN TRỤC X (TRÁI/PHẢI) ---
//        int xOffset;
//        if (anchorX + popupWidth > screenWidth - marginEdge) {
//            // Nếu tràn phải, đẩy sang trái và chừa ra một khoảng cách mép màn hình
//            xOffset = -(popupWidth - anchorWidth + marginEdge);
//        } else {
//            xOffset = 0; // Mặc định dính lề trái của nút
//        }
//
//        // --- TÍNH TOÁN TRỤC Y (TRÊN/DƯỚI) ---
//        int yOffset;
//        // Kiểm tra xem phía dưới có đủ chỗ (chừa thêm marginAnchor và marginEdge) không
//        boolean canShowBelow = (anchorY + anchorHeight + popupHeight + marginAnchor + marginEdge) < screenHeight;
//
//        if (canShowBelow) {
//            setAnimationStyle(R.style.AppPopupAnimation_SlideDown);
//            // HIỆN PHÍA DƯỚI: Cách cái nút một đoạn marginAnchor
//            yOffset = marginAnchor;
//            showAsDropDown(viewAnchor, xOffset, yOffset);
//        } else {
//            setAnimationStyle(R.style.AppPopupAnimation_SlideUp);
//            // HIỆN PHÍA TRÊN: Nhảy lên và cách mép trên của nút một đoạn marginAnchor
//            // Công thức: -(Chiều cao menu + Chiều cao nút + Khoảng cách muốn chừa)
//            yOffset = -(popupHeight + anchorHeight );
//            showAsDropDown(viewAnchor, xOffset, yOffset);
//        }
//    }
}
