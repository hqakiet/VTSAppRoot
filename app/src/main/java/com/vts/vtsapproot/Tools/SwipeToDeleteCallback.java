package com.vts.vtsapproot.Tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.vts.vtsapproot.R;

abstract public class SwipeToDeleteCallback
        extends ItemTouchHelper.Callback {

    private final Paint mClearPaint;
    private final GradientDrawable mBackground;
    private final int backgroundColor;
    private final Drawable deleteDrawable;
    private final int intrinsicWidth;
    private final int intrinsicHeight;
    private final int cornerRadius = 24; // Bạn có thể bỏ comment nếu muốn bo góc nền

    public SwipeToDeleteCallback(Context context) {
        mBackground = new GradientDrawable();

        // Lấy màu từ Theme (colorPrimaryVariant hoặc colorError)
        backgroundColor = context.getColor(R.color.MySwipeToDeleteColor);

        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        // Sử dụng icon Outline
        deleteDrawable = ContextCompat.getDrawable(context, R.drawable.ic_delete_sweep);

        if (deleteDrawable != null) {
            deleteDrawable.setTint(Color.WHITE);
            intrinsicWidth = deleteDrawable.getIntrinsicWidth();
            intrinsicHeight = deleteDrawable.getIntrinsicHeight();
        } else {
            intrinsicWidth = 0;
            intrinsicHeight = 0;
        }
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();
        boolean isCancelled = dX == 0 && !isCurrentlyActive;

        if (isCancelled) {
            clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        // 1. VẼ NỀN
        mBackground.setColor(backgroundColor);
        mBackground.setCornerRadius(cornerRadius);
        mBackground.setBounds(
                itemView.getRight() + (int) dX - 50,
                itemView.getTop(),
                itemView.getRight(),
                itemView.getBottom()
        );
        mBackground.draw(c);

        // 2. TÍNH TOÁN KÍCH THƯỚC ICON DỰA TRÊN CHIỀU CAO ITEM
        // Ví dụ: Chiều cao gốc của icon khi hiển thị tối đa = 40% chiều cao item
        int targetIconHeight = (int) (itemHeight * 0.8f);
        // Tính chiều rộng tương ứng để giữ nguyên tỷ lệ (aspect ratio) của icon gốc
        int targetIconWidth = (int) (targetIconHeight * ((float) intrinsicWidth / intrinsicHeight));

        // 3. TÍNH TOÁN HIỆU ỨNG ICON (ALPHA & SCALE)
        float threshold = (float) itemView.getWidth() / 2;
        float alphaRatio = Math.abs(dX) / threshold;
        if (alphaRatio > 1f) alphaRatio = 1f;

        deleteDrawable.setAlpha((int) (alphaRatio * 255));

        int deleteIconMargin = (itemHeight - targetIconHeight) / 2;
        int iconCenterX = itemView.getRight() - deleteIconMargin - (targetIconWidth / 2);
        int iconCenterY = itemView.getTop() + (itemHeight / 2);

        // Hiệu ứng Scale dựa trên kích thước mới đã tính theo itemHeight
        float scaleFactor = 0.1f + (0.9f * alphaRatio);
        int finalWidth = (int) (targetIconWidth * scaleFactor);
        int finalHeight = (int) (targetIconHeight * scaleFactor);

        deleteDrawable.setBounds(
                iconCenterX - (finalWidth / 2),
                iconCenterY - (finalHeight / 2),
                iconCenterX + (finalWidth / 2),
                iconCenterY + (finalHeight / 2)
        );

        deleteDrawable.draw(c);

        // 4. ĐỘ MỜ CỦA ITEM KHI VUỐT
        float maxSwipeDistance = itemView.getWidth();
        float alpha = 1.0f - (Math.abs(dX) / maxSwipeDistance);
        if (alpha < 0.2f) {
            alpha = 0.2f;
        }
        itemView.setAlpha(alpha);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

//        View itemView = viewHolder.itemView;
//        int itemHeight = itemView.getHeight();
//        boolean isCancelled = dX == 0 && !isCurrentlyActive;
//
//        if (isCancelled) {
//            clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            return;
//        }
//
//        // 1. VẼ NỀN (Vẽ dôi ra 50px để che phủ tốt hơn khi vuốt nhanh)
//        mBackground.setColor(backgroundColor);
//        mBackground.setCornerRadius(cornerRadius);
//        mBackground.setBounds(
//                itemView.getRight() + (int) dX - 50,
//                itemView.getTop(),
//                itemView.getRight(),
//                itemView.getBottom()
//        );
//        mBackground.draw(c);
//
//        // 2. TÍNH TOÁN HIỆU ỨNG ICON (ALPHA & SCALE)
//        int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
//
//        // Ngưỡng để icon hiện rõ 100% (ví dụ khi vuốt được 1/2 item)
//        float threshold = (float) itemView.getWidth() / 2;
//        float alphaRatio = Math.abs(dX) / threshold;
//        if (alphaRatio > 1f) alphaRatio = 1f;
//
//        // Thiết lập Alpha (0 - 255)
//        deleteDrawable.setAlpha((int) (alphaRatio * 255));
//
//        // Tính toán tọa độ tâm của Icon
//        int iconCenterX = itemView.getRight() - deleteIconMargin - (intrinsicWidth / 2);
//        int iconCenterY = itemView.getTop() + (itemHeight / 2);
//
//        // Hiệu ứng Scale: Icon to dần từ 0.6 -> 1.0
//        float scaleFactor = 0.1f + (0.9f * alphaRatio);
//        int finalWidth = (int) (intrinsicWidth * scaleFactor);
//        int finalHeight = (int) (intrinsicHeight * scaleFactor);
//
//        deleteDrawable.setBounds(
//                iconCenterX - (finalWidth / 2),
//                iconCenterY - (finalHeight / 2),
//                iconCenterX + (finalWidth / 2),
//                iconCenterY + (finalHeight / 2)
//        );
//
//        deleteDrawable.draw(c);
//
//        // Tính toán độ mờ (alpha) dựa trên quãng đường vuốt (dX)
//        // Càng vuốt ra xa, item càng mờ đi (từ 1.0 giảm dần về 0.2)
//        float maxSwipeDistance = itemView.getWidth();
//        float alpha = 1.0f - (Math.abs(dX) / maxSwipeDistance);
//        if (alpha < 0.2f) {
//            alpha = 0.2f; // Giữ độ mờ tối thiểu để không biến mất hoàn toàn
//        }
//
//        // Áp dụng trực tiếp alpha lên item đang vuốt
//        itemView.setAlpha(alpha);
//
//        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
        c.drawRect(left, top, right, bottom, mClearPaint);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        // Ngưỡng 0.5f là hợp lý nhất cho trải nghiệm người dùng
        return 0.5f;
    }
}