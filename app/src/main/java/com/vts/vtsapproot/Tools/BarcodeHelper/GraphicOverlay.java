package com.vts.vtsapproot.Tools.BarcodeHelper;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class GraphicOverlay extends View {
    private final Object mLock = new Object();
    private int mPreviewWidth;
    private int mPreviewHeight;
    private float mWidthScaleFactor = 1.0f;
    private float mHeightScaleFactor = 1.0f;
    private final List<Graphic> mGraphics = new ArrayList<>();

    public abstract static class Graphic {
        private final GraphicOverlay mOverlay;

        public Graphic(GraphicOverlay overlay) {
            mOverlay = overlay;
        }

        public abstract void draw(Canvas canvas);

        // Quy đổi tọa độ X từ Camera sang View
        public float translateX(float x) {
            return x * mOverlay.mWidthScaleFactor;
        }

        // Quy đổi tọa độ Y từ Camera sang View
        public float translateY(float y) {
            return y * mOverlay.mHeightScaleFactor;
        }

        public void postInvalidate() {
            mOverlay.postInvalidate();
        }
    }

    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void clear() {
        synchronized (mLock) {
            mGraphics.clear();
        }
        postInvalidate();
    }

    public void add(Graphic graphic) {
        synchronized (mLock) {
            mGraphics.add(graphic);
        }
        postInvalidate();
    }

    public void remove(Graphic graphic) {
        synchronized (mLock) {
            mGraphics.remove(graphic);
        }
        postInvalidate();
    }

    // Quan trọng: ML Kit gửi ảnh về, ta phải tính toán lại tỉ lệ Scale
    public void setCameraInfo(int previewWidth, int previewHeight) {
        synchronized (mLock) {
            // Với CameraX Portrait, ta đảo ngược thông số để tính Scale chuẩn
            this.mPreviewWidth = previewWidth;
            this.mPreviewHeight = previewHeight;
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        synchronized (mLock) {
            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
                // Tính toán tỉ lệ giữa kích thước View thực tế và kích thước ảnh Camera
                mWidthScaleFactor = (float) getWidth() / (float) mPreviewWidth;
                mHeightScaleFactor = (float) getHeight() / (float) mPreviewHeight;
            }

            for (Graphic graphic : mGraphics) {
                graphic.draw(canvas);
            }
        }
    }
}