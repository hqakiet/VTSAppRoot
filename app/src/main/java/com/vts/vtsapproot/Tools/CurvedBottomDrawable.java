package com.vts.vtsapproot.Tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.annotation.NonNull;

public class CurvedBottomDrawable extends Drawable {
    private final Path mPath = new Path();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mOffset = 0;
    private final float radius;
    private final float holeDepth;

    public CurvedBottomDrawable(Context context) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, context.getResources().getDisplayMetrics());

        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1.5f, context.getResources().getDisplayMetrics()));

        // Đáy hố 1.0 = chạm đáy FAB
        holeDepth = radius * 1.02f;
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        invalidateSelf(); // Vẽ lại khi màu thay đổi
    }

    public void setOffset(float offset) {
        this.mOffset = offset;
        invalidateSelf();
    }

    public float getOffset() {
        return mOffset;
    }

    public Path getPath() {
        return mPath;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int width = getBounds().width();
        int height = getBounds().height();
        float safeTop = 1f;

        mPath.reset();
        mPath.moveTo(0, safeTop);

        // Miệng hố khép gọn
        float startLining = mOffset - (radius * 1.15f);
        float endLining = mOffset + (radius * 1.15f);

        mPath.lineTo(startLining, safeTop);

        if (mOffset > 0) {
            // Nhánh trái: Ép hệ số để bo tròn đáy mà không xòe mép
            mPath.cubicTo(
                    mOffset - (radius * 0.7f), safeTop,
                    mOffset - (radius * 0.85f), holeDepth,
                    mOffset, holeDepth
            );

            // Nhánh phải: Đối xứng
            mPath.cubicTo(
                    mOffset + (radius * 0.85f), holeDepth,
                    mOffset + (radius * 0.7f), safeTop,
                    endLining, safeTop
            );
        }

        mPath.lineTo(width, safeTop);
        mPath.lineTo(width, height);
        mPath.lineTo(0, height);
        mPath.close();

        // Cấu hình Gradient viền để tạo hiệu ứng gờ kính 3D (Đỉnh sáng rực, đáy tối nhẹ)
        LinearGradient strokeGradient = new LinearGradient(
                0, 0, 0, height,
                new int[]{
                        Color.parseColor("#FFFFFF"), // Viền đỉnh cực sáng 5000497C
                        Color.parseColor("#E600497C"), // Lưng chừng mờ 590168B4
                        Color.parseColor("#00497C")  // Đáy tối nhẹ tạo cảm giác dày 3D cho mép kính E600497C
                },
                new float[]{0f, 0.55f, 1f},
                Shader.TileMode.CLAMP
        );
        mStrokePaint.setShader(strokeGradient);

        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(mPath, mStrokePaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}