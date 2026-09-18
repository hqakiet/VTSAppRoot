package com.vts.vtsapproot.UI.Adapters.Bases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.vts.vtsapproot.API.Interfaces.BaseClickInterface;
import com.vts.vtsapproot.API.Interfaces.BaseSingleProcessInterface;

import java.util.ArrayList;
import java.util.List;

public abstract class ListAdapter_ViewHolder<T, VH extends RecyclerView.ViewHolder>
        extends ListAdapter<T, VH> {

    protected Context My_Context;

//    protected MotionEvent My_MotionEvent;

    protected ListAdapter_ViewHolder(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        My_Context = parent.getContext();
        // Lớp con sẽ chịu trách nhiệm inflate layout và khởi tạo VH
        return createViewHolder(LayoutInflater.from(My_Context), parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        T item = getItem(position);
        if (item != null) {
            bindData(holder, item, position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            T item = getItem(position);
            if (item != null) {
                bindPayload(holder, item, position, payloads);
            }
        }
    }

    // Lấy item tại vị trí cụ thể (Hỗ trợ tốt hơn cho việc click)
    public T getItemAt(int position) {
        return getItem(position);
    }

    public void removeItem(int position) {
        List<T> currentList = new ArrayList<>(getCurrentList());
        currentList.remove(position);
        submitList(currentList);
    }

    public void restoreItem(T item, int position) {
        List<T> currentList = new ArrayList<>(getCurrentList());
        currentList.add(position, item);
        submitList(currentList);
    }

    public void submitListAndReset(List<T> data) {
        this.submitList(null);
        this.submitList(data);
    }

    public void submitListAndReset(List<T> data, BaseSingleProcessInterface complete) {
        this.submitList(null);
        this.submitList(data, complete::onCompleted);
    }

    public void setupForPopup(
            View pView,
            BaseClickInterface<MotionEvent> pItemPicker
    ) {
        if (pView == null) return;

        pView.setOnTouchListener(new View.OnTouchListener() {
            private static final long LONG_PRESS_DURATION = 500; // ms
            private final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
            private boolean isLongPressTriggered = false;
            private Runnable longPressRunnable;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:

                        isLongPressTriggered = false;

                        // Khởi tạo tác vụ chờ đủ 400ms
                        longPressRunnable = () -> {
                            isLongPressTriggered = true;
                            if (pItemPicker != null) {
                                pItemPicker.ItemClicked(event);
                            }
                        };

                        // Đặt lịch chạy sau 400ms
                        handler.postDelayed(longPressRunnable, LONG_PRESS_DURATION);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Hủy handler nếu người dùng nhấc tay trước 400ms
                        handler.removeCallbacks(longPressRunnable);

//                        // Nếu chưa kích hoạt long press (tức là < 400ms) thì coi là click thường
//                        if (!isLongPressTriggered) {
//                            if (pItemClick != null) {
//                                pItemClick.run();
//                            }
//                        }
                        isLongPressTriggered = false;
                        view.performClick();
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        // Nếu người dùng trượt tay ra ngoài view, hủy đếm giờ
                        handler.removeCallbacks(longPressRunnable);
                        isLongPressTriggered = false;
                        break;
                }
                return true;
            }
        });
    }

    public void setupForPopup(
            View pView,
            Runnable pItemClick,
            BaseClickInterface<MotionEvent> pItemPicker
    ) {
        if (pView == null) return;

        pView.setOnTouchListener(new View.OnTouchListener() {
            private static final long LONG_PRESS_DURATION = 500; // ms
            private final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
            private boolean isLongPressTriggered = false;
            private Runnable longPressRunnable;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:

                        isLongPressTriggered = false;

                        // Khởi tạo tác vụ chờ đủ 400ms
                        longPressRunnable = () -> {
//                            if (pItemClick != null) {
//                                pItemClick.run();
//                            }
                            isLongPressTriggered = true;
                            if (pItemPicker != null) {
                                pItemPicker.ItemClicked(event);
                            }
                        };

                        // Đặt lịch chạy sau 400ms
                        handler.postDelayed(longPressRunnable, LONG_PRESS_DURATION);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Hủy handler nếu người dùng nhấc tay trước 400ms
                        handler.removeCallbacks(longPressRunnable);

                        // Nếu chưa kích hoạt long press (tức là < 400ms) thì coi là click thường
                        if (!isLongPressTriggered) {
                            if (pItemClick != null) {
                                pItemClick.run();
                            }
                        }
                        view.performClick();
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        // Nếu người dùng trượt tay ra ngoài view, hủy đếm giờ
                        handler.removeCallbacks(longPressRunnable);
                        isLongPressTriggered = false;
                        break;
                }
                return true;
            }
        });
    }

    // Các hàm bắt buộc lớp con triển khai
    protected abstract VH createViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    protected abstract void bindData(VH holder, T item, int position);

    // Tùy chọn: Xử lý cập nhật từng phần (Partial update)
    protected void bindPayload(VH holder, T item, int position, List<Object> payloads) {
    }

}