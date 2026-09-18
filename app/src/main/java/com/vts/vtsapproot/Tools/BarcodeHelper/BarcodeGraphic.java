package com.vts.vtsapproot.Tools.BarcodeHelper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.google.mlkit.vision.barcode.common.Barcode;

public class BarcodeGraphic extends GraphicOverlay.Graphic {
    private final Paint mRectPaint;
    private final Barcode mBarcode;

    public BarcodeGraphic(GraphicOverlay overlay, Barcode barcode) {
        super(overlay);
        this.mBarcode = barcode;
        mRectPaint = new Paint();
        mRectPaint.setColor(Color.GREEN);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(12.0f); // Dày cho máy DPI cao
    }

    @Override
    public void draw(Canvas canvas) {
        if (mBarcode == null) return;

        Rect rectTho = mBarcode.getBoundingBox();
        if (rectTho == null) return;

        RectF rect = new RectF(rectTho);
        // Quy đổi tọa độ từ Camera sang màn hình
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);

        float cornerRadius = 15.0f; // Độ bo góc

        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, mRectPaint);
    }
}