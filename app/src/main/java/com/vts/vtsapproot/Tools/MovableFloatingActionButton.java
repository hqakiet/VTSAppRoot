package com.vts.vtsapproot.Tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MovableFloatingActionButton extends FloatingActionButton {

    private float downRawX, downRawY;
    private float dX, dY;
    private int touchSlop;
    private boolean isDragging = false;

    // Biến để lưu tham chiếu đến SwipeRefreshLayout tìm thấy
    private SwipeRefreshLayout parentSwipeRefreshLayout = null;

    public MovableFloatingActionButton(Context context) {
        super(context);
        init(context);
    }

    public MovableFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MovableFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        int action = motionEvent.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 1. Tìm SwipeRefreshLayout cha và TẮT nó đi
                toggleSwipeRefreshLayout(false);

                // 2. Vẫn giữ cái này để chặn các ScrollView/RecyclerView khác
                ViewParent parent = getParent();
                while (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                    parent = parent.getParent();
                }

                downRawX = motionEvent.getRawX();
                downRawY = motionEvent.getRawY();
                dX = getX() - downRawX;
                dY = getY() - downRawY;
                isDragging = false;
                return true;

            case MotionEvent.ACTION_MOVE:
                int viewWidth = getWidth();
                int viewHeight = getHeight();

                View viewParent = (View) getParent();
                int parentWidth = viewParent.getWidth();
                int parentHeight = viewParent.getHeight();

                float newX = motionEvent.getRawX() + dX;
                newX = Math.max(layoutParams.leftMargin, newX);
                newX = Math.min(parentWidth - viewWidth - layoutParams.rightMargin, newX);

                float newY = motionEvent.getRawY() + dY;
                newY = Math.max(layoutParams.topMargin, newY);
                newY = Math.min(parentHeight - viewHeight - layoutParams.bottomMargin, newY);

                setX(newX);
                setY(newY);

                if (Math.abs(motionEvent.getRawX() - downRawX) > touchSlop ||
                        Math.abs(motionEvent.getRawY() - downRawY) > touchSlop) {
                    isDragging = true;
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 3. MỞ LẠI SwipeRefreshLayout khi thả tay
                toggleSwipeRefreshLayout(true);

                if (action == MotionEvent.ACTION_UP) {
                    if (!isDragging) {
                        performClick();
                    } else {
                        snapToEdge();
                    }
                }
                return true;

            default:
                return super.onTouchEvent(motionEvent);
        }
    }

    // Hàm đệ quy tìm SwipeRefreshLayout cha
    private void toggleSwipeRefreshLayout(boolean enable) {
        // Nếu chưa tìm thấy thì đi tìm
        if (parentSwipeRefreshLayout == null) {
            ViewParent parent = getParent();
            while (parent != null) {
                if (parent instanceof SwipeRefreshLayout) {
                    parentSwipeRefreshLayout = (SwipeRefreshLayout) parent;
                    break;
                }
                parent = parent.getParent();
            }
        }

        // Nếu tìm thấy rồi thì setEnabled
        if (parentSwipeRefreshLayout != null) {
            parentSwipeRefreshLayout.setEnabled(enable);
        }
    }

    // ... Giữ nguyên hàm snapToEdge() và performClick() như cũ ...
    private void snapToEdge() {
        View viewParent = (View) getParent();
        int parentWidth = viewParent.getWidth();
        int viewWidth = getWidth();
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();

        float currentX = getX();
        float targetX;

        if (currentX + viewWidth / 2f < parentWidth / 2f) {
            targetX = layoutParams.leftMargin;
        } else {
            targetX = parentWidth - viewWidth - layoutParams.rightMargin;
        }

        this.animate()
                .x(targetX)
                .setDuration(200)
                .start();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
