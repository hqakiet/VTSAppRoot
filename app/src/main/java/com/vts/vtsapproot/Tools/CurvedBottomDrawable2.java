package com.vts.vtsapproot.Tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.annotation.NonNull;

public class CurvedBottomDrawable2 extends Drawable {
    private final Path mPath = new Path();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float mOffset = 0;
    private final float radius;
    private final float holeDepth;

    private final float cornerRadius;
    private final int[] fillColors;
    private final float[] fillPositions;
    private final int[] strokeColors;
    private final float[] strokePositions;
    private final float shadowRadius;
    private final float shadowDy;
    private final int shadowColor;
    private final boolean drawbackground;
    private final boolean drawShadow;
    private final int bottomInset;

    protected CurvedBottomDrawable2(Builder builder, Context context) {
        this.cornerRadius = builder.cornerRadius;
        this.fillColors = builder.fillColors;
        this.fillPositions = builder.fillPositions;
        this.strokeColors = builder.strokeColors;
        this.strokePositions = builder.strokePositions;
        this.shadowRadius = builder.shadowRadius;
        this.shadowDy = builder.shadowDy;
        this.shadowColor = builder.shadowColor;
        this.drawbackground = builder.drawbackground;
        this.drawShadow = builder.drawShadow;
        this.bottomInset = builder.bottomInset;

        mPaint.setStyle(Paint.Style.FILL);

        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, builder.strokeWidthDp, context.getResources().getDisplayMetrics()));

        radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, builder.holeRadiusDp, context.getResources().getDisplayMetrics());
        holeDepth = radius * builder.holeDepthFactor;
    }

    public static class Builder {
        private final Context context;
        private float cornerRadius = 120f;
        private float holeRadiusDp = 45f;
        private float holeDepthFactor = 1.1f;
        private float strokeWidthDp = 1.5f;
        private boolean drawbackground = true; // Mặc định bật nền để hiển thị hiệu ứng kính
        private boolean drawShadow = false;     // Mặc định bật bóng nổi 3D
        private int bottomInset = 0;

        // Tối ưu màu sắc dải chuyển màu (Gradient) để tạo khối kính 3D nổi bật
        private int[] fillColors = new int[]{
                Color.parseColor("#E6FFFFFF"), // Đỉnh: Trắng sáng rực phản chiếu ánh sáng
                Color.parseColor("#B3FFFFFF"), // Thân trên: Trong suốt vừa phải
                Color.parseColor("#80F0F0F0"), // Thân dưới: Xám nhẹ tạo độ cong khối
                Color.parseColor("#33888888")  // Đáy: Tối dần tạo bóng chân khối 3D
        };
        private float[] fillPositions = new float[]{0f, 0.3f, 0.7f, 1f};

        // Viền 3D: Mép trên bắt sáng cực mạnh, mép dưới ngả tối tạo gờ kính dày dặn
        private int[] strokeColors = new int[]{
                Color.parseColor("#E6FFFFFF"), // Viền đỉnh cực sáng
                Color.parseColor("#B3FFFFFF"), // Lưng chừng mờ
                Color.parseColor("#33000000")  // Đáy tối nhẹ tạo cảm giác dày 3D cho mép kính
        };
        private float[] strokePositions = new float[]{0f, 0.75f, 1f};

        private float shadowRadius = 16f;
        private float shadowDy = 6f;
        private int shadowColor = Color.parseColor("#40000000");

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setFillColors(int[] fillColors) {
            this.fillColors = fillColors;
            return this;
        }

        public Builder setFillPositions(float[] fillPositions) {
            this.fillPositions = fillPositions;
            return this;
        }

        public Builder setStrokeColors(int[] strokeColors) {
            this.strokeColors = strokeColors;
            return this;
        }

        public Builder setStrokePositions(float[] strokePositions) {
            this.strokePositions = strokePositions;
            return this;
        }

        public Builder setCornerRadius(float radiusPx) {
            this.cornerRadius = radiusPx;
            return this;
        }

        public Builder setHoleRadiusDp(float dp) {
            this.holeRadiusDp = dp;
            return this;
        }

        public Builder setStrokeWidthDp(float dp) {
            this.strokeWidthDp = dp;
            return this;
        }

        public Builder setFillGradient(int[] colors, float[] positions) {
            this.fillColors = colors;
            this.fillPositions = positions;
            return this;
        }

        public Builder setStrokeGradient(int[] colors, float[] positions) {
            this.strokeColors = colors;
            this.strokePositions = positions;
            return this;
        }

        public Builder setShadow(float radius, float dy, int color) {
            this.shadowRadius = radius;
            this.shadowDy = dy;
            this.shadowColor = color;
            return this;
        }

        public Builder setDrawbackground(boolean drawbackground) {
            this.drawbackground = drawbackground;
            return this;
        }

        public Builder setDrawShadow(boolean drawShadow) {
            this.drawShadow = drawShadow;
            return this;
        }

        public Builder setBottomInset(int bottomInset) {
            this.bottomInset = bottomInset;
            return this;
        }

        public CurvedBottomDrawable2 build() {
            return new CurvedBottomDrawable2(this, context);
        }
    }

    public Path getPath() {
        return mPath;
    }

//    public void setColor(int color) {
//        mPaint.setColor(color);
//        invalidateSelf();
//    }

    public void setOffset(float offset) {
        this.mOffset = offset;
        invalidateSelf();
    }

    public float getOffset() {
        return mOffset;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height() - bottomInset;

        if (width <= 0 || height <= 0) return;

        mPath.reset();

        RectF rect = new RectF(0, 0, width, height);
        float[] radii = new float[]{
                cornerRadius, cornerRadius,
                cornerRadius, cornerRadius,
                cornerRadius, cornerRadius,
                cornerRadius, cornerRadius
        };
        mPath.addRoundRect(rect, radii, Path.Direction.CW);

        if (mOffset > 0) {
            float startLining = mOffset - (radius * 1.15f);
            float endLining = mOffset + (radius * 1.15f);
            float safeTop = 0f;

            Path holePath = new Path();
            holePath.moveTo(startLining, safeTop);
            holePath.cubicTo(
                    mOffset - (radius * 0.7f), safeTop,
                    mOffset - (radius * 0.85f), holeDepth,
                    mOffset, holeDepth
            );
            holePath.cubicTo(
                    mOffset + (radius * 0.85f), holeDepth,
                    mOffset + (radius * 0.7f), safeTop,
                    endLining, safeTop
            );
            holePath.lineTo(endLining, 0);
            holePath.lineTo(startLining, 0);
            holePath.close();

            mPath.op(holePath, Path.Op.DIFFERENCE);
        }

        // Cấu hình Gradient nền kính 3D
        if (drawbackground) {
            LinearGradient fillGradient = new LinearGradient(
                    0, 0, 0, height,
                    fillColors, fillPositions, Shader.TileMode.CLAMP
            );
            mPaint.setShader(fillGradient);
        } else {
            mPaint.setShader(null);
        }

        // Cấu hình Gradient viền để tạo hiệu ứng gờ kính 3D (Đỉnh sáng rực, đáy tối nhẹ)
        LinearGradient strokeGradient = new LinearGradient(
                0, 0, 0, height,
                strokeColors, strokePositions, Shader.TileMode.CLAMP
        );
        mStrokePaint.setShader(strokeGradient);

        // Đổ bóng nổi khối
        if (drawShadow) {
            mPaint.setShadowLayer(shadowRadius, 0f, shadowDy, shadowColor);
        } else {
            mPaint.clearShadowLayer();
        }

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