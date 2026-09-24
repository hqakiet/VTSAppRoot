package com.vts.vtsapproot.Tools;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.vts.vtsapproot.API.Interfaces.DateFromToPickerInterface;
import com.vts.vtsapproot.API.Interfaces.DatePickerInterface;

import java.util.Calendar;
import java.util.Date;

public class FragmentBase
        extends Fragment {


    protected final MutableLiveData<CustomListEvents<?>> _CustomListEvents = new MutableLiveData<>();
    protected boolean My_AllowLayoutAnimation = true;

    public LiveData<CustomListEvents<?>> My_CustomListEvents() {
        return _CustomListEvents;
    }

    protected int MyBottomMenuHeight;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (gvSystem.App_AutoProcessSystembars) {
            applyLayoutInsets(Insets.of(0, 0, 0, 0));
        } else {
            ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
                applyLayoutInsets(windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()));
                return windowInsets;
            });
            ViewCompat.requestApplyInsets(view);
        }

    }

    protected void applyLayoutInsets(Insets insets) {

    }


    protected void setupDateOnlyPicker(View anchorView, Date pInitDate, DatePickerInterface pPickedDate) {
        if (getContext() == null) return;
        ((ActBase) getContext()).setupDateOnlyPicker(anchorView, pInitDate, pPickedDate);
    }

    protected void setupDateOnlyDialogPicker(View anchorView, Date pInitDate, DatePickerInterface pPickedDate) {
        if (getContext() == null) return;
        ((ActBase) getContext()).setupDateOnlyDialogPicker(anchorView, pInitDate, pPickedDate);
    }

    protected void setupDateTimePicker(View anchorView, Date pInitDate, DatePickerInterface pPickedDate) {
        if (getContext() == null) return;
        ((ActBase) getContext()).setupDateTimePicker(anchorView, pInitDate, pPickedDate);
    }

    protected void setupDateTimeDialogPicker(View anchorView, Date pInitDate, DatePickerInterface pPickedDate) {
        if (getContext() == null) return;
        ((ActBase) getContext()).setupDateTimeDialogPicker(anchorView, pInitDate, pPickedDate);
    }

    protected void setupNgayTuanThangPicker(View anchorView, DateFromToPickerInterface pPickedDateFromTo) {
        if (getContext() == null) return;
        ((ActBase) getContext()).setupNgayTuanThangPicker(anchorView, pPickedDateFromTo);
    }


    protected void setupSearchEvents(
            MaterialButton pMaterialButton_Search,
            LinearLayout pSearch_LinearLayout,
            TextInputEditText pSearch_TextInputEditText_SearchContent,
            MaterialButton pSearch_MaterialButton_ClearContent,
            MaterialButton pSearch_MaterialButton_DoSearchContent,
            Runnable runnable
    ) {
        requireContext();
        if (getContext() == null) return;
        if (
                pMaterialButton_Search != null
                        && pSearch_LinearLayout != null
                        && pSearch_TextInputEditText_SearchContent != null
                        && pSearch_MaterialButton_ClearContent != null
                        && pSearch_MaterialButton_DoSearchContent != null
        ) {
            pMaterialButton_Search.setVisibility(View.GONE);
            pMaterialButton_Search.addOnCheckedChangeListener(
                    (materialButton, b) -> {
                        if (b) {
                            pSearch_TextInputEditText_SearchContent.setEnabled(true);

                            if (!gvSystem.getApp_TietKiemPin()) {
                                pSearch_LinearLayout.setPivotY(0f);
                                pSearch_LinearLayout.setAlpha(0f);

                                pSearch_LinearLayout.setTranslationY(0f);
                                pSearch_LinearLayout.setScaleY(0f);

                                pSearch_LinearLayout.setVisibility(View.VISIBLE);
                                pSearch_LinearLayout.post(
                                        () -> pSearch_LinearLayout.animate()
                                                .scaleY(1f)
                                                .alpha(1f)
                                                .setDuration(300)
                                                .setInterpolator(new DecelerateInterpolator())
                                                .withEndAction(
                                                        () -> {
                                                            if (runnable != null) {
                                                                runnable.run();
                                                            }
                                                        }
                                                )
                                                .start()
                                );
                            } else {
                                pSearch_LinearLayout.setVisibility(View.VISIBLE);
                                if (runnable != null) {
                                    runnable.run();
                                }
                            }
                        } else {
                            String oldValue = pSearch_TextInputEditText_SearchContent.getText() != null ? pSearch_TextInputEditText_SearchContent.getText().toString().trim() : "";
                            pSearch_TextInputEditText_SearchContent.setText("");
                            if (!oldValue.isEmpty()) {
                                _CustomListEvents.setValue(new CustomListEvents<>(CustomListEvents.Type.NEEDSTOPFILTER, ""));
                            }
                            pSearch_TextInputEditText_SearchContent.setEnabled(false);

                            if (!gvSystem.getApp_TietKiemPin()) {
                                pSearch_LinearLayout.setPivotY(0f);

                                pSearch_LinearLayout.animate()
                                        .scaleY(0f)
                                        .alpha(0f)
                                        .setDuration(200)
                                        .setInterpolator(new AccelerateInterpolator())
                                        .withEndAction(() -> {
                                            pSearch_LinearLayout.setVisibility(View.GONE);
                                            pSearch_LinearLayout.setScaleY(1f);
                                            if (runnable != null) {
                                                runnable.run();
                                            }
                                        })
                                        .start();
                            } else {
                                pSearch_LinearLayout.setVisibility(View.GONE);
                                if (runnable != null) {
                                    runnable.run();
                                }
                            }

                            InputMethodManager imm = (InputMethodManager) requireContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(pSearch_TextInputEditText_SearchContent.getWindowToken(), 0);
                        }
                    }
            );
            pSearch_MaterialButton_ClearContent.setOnClickListener(new SingleClickListener() {
                @Override
                public void safeSingleClick(View v) {
                    String oldValue = pSearch_TextInputEditText_SearchContent.getText() != null ? pSearch_TextInputEditText_SearchContent.getText().toString().trim() : "";
                    pSearch_TextInputEditText_SearchContent.setText("");
                    if (!oldValue.isEmpty()) {
                        _CustomListEvents.setValue(new CustomListEvents<>(CustomListEvents.Type.NEEDSTOPFILTER, ""));
                    }
                }
            });
            pSearch_MaterialButton_DoSearchContent.setOnClickListener(new SingleClickListener() {
                @Override
                public void safeSingleClick(View v) {
                    InputMethodManager imm = (InputMethodManager) requireContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(pSearch_TextInputEditText_SearchContent.getWindowToken(), 0);
                    String searchContent = pSearch_TextInputEditText_SearchContent.getText() != null ? pSearch_TextInputEditText_SearchContent.getText().toString().trim() : "";
                    _CustomListEvents.setValue(new CustomListEvents<>(CustomListEvents.Type.NEEDSTARTFILTER, searchContent));
                }
            });
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void setupFloatingActionButton(
            FloatingActionButton pFloatingActionButton_Movable,
            SwipeRefreshLayout pSwipeRefreshLayout,
            RecyclerView pRecyclerView,
            View pPaddingView
    ) {
        requireContext();
        if (getContext() == null) return;
        if (pFloatingActionButton_Movable != null) {
            pFloatingActionButton_Movable.post(() -> restoreSharedPreferences(pFloatingActionButton_Movable, pRecyclerView));
            pFloatingActionButton_Movable.setOnTouchListener(new View.OnTouchListener() {
                private static final int MAX_CLICK_DURATION = 200; // ms
                private long startClickTime;
                private float dX, dY;

                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    View parent = (View) view.getParent();
                    float paddingTop, paddingStart, paddingEnd, paddingBottom;
                    if (pPaddingView != null) {
                        paddingTop = Math.max(55f, (float) pPaddingView.getPaddingTop());
                        paddingStart = Math.max(55f, (float) pPaddingView.getPaddingStart());
                        paddingEnd = Math.max(55f, (float) pPaddingView.getPaddingEnd());
                        paddingBottom = Math.max(55f, (float) pPaddingView.getPaddingBottom());
                    } else {
                        paddingTop = 55f;
                        paddingStart = 55f;
                        paddingEnd = 55f;
                        paddingBottom = 55f;
                    }
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            if (pSwipeRefreshLayout != null) {
                                pSwipeRefreshLayout.setEnabled(false);
                            }

                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            dX = view.getX() - event.getRawX();
                            dY = view.getY() - event.getRawY();
                            break;

                        case MotionEvent.ACTION_MOVE:
                            if (pSwipeRefreshLayout != null) {
                                pSwipeRefreshLayout.setEnabled(false);
                            }

                            float newX = event.getRawX() + dX;
                            float newY = event.getRawY() + dY;

                            // Giới hạn không cho nút bay ra khỏi màn hình (tùy chọn)
                            newX = Math.max(paddingStart, Math.min(newX, (float) parent.getWidth() - (float) view.getWidth() - paddingEnd));
                            newY = Math.max(paddingTop, Math.min(newY, (float) parent.getHeight() - (float) view.getHeight() - paddingBottom));

                            view.animate().x(newX).y(newY).setDuration(0).start();

                            break;

                        case MotionEvent.ACTION_UP:
                            if (pSwipeRefreshLayout != null) {
                                if (pRecyclerView != null) {
                                    pSwipeRefreshLayout.setEnabled(!pRecyclerView.canScrollVertically(-1));
                                }
                            }

                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;

                            if (clickDuration < MAX_CLICK_DURATION) {
                                view.performClick();
                            } else {
                                float finalX;
                                float finalY = view.getY();

                                // Kiểm tra xem nút đang ở nửa bên trái hay nửa bên phải màn hình
                                if (view.getX() + (view.getWidth() / 2f) < parent.getWidth() / 2f) {
                                    finalX = paddingStart; // Hút về cạnh trái
                                } else {
                                    finalX = parent.getWidth() - view.getWidth() - paddingEnd; // Hút về cạnh phải
                                }

                                view.animate()
                                        .x(finalX)
                                        .setDuration(400) // Thời gian trượt 0.4 giây cho mượt
                                        .setInterpolator(new OvershootInterpolator(0.8f))
                                        .withEndAction(() -> {
                                            // Lưu lại vị trí chuẩn sau khi đã neo
                                            saveSharedPreferences(pFloatingActionButton_Movable, finalX, finalY);
                                        })
                                        .start();
                            }
                            break;
                    }
                    return true;
                }
            });
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void setupFloatingActionButton(
            FloatingActionButton pFloatingActionButton_Movable,
            SwipeRefreshLayout pSwipeRefreshLayout,
            NestedScrollView pNestedScrollView,
            View pPaddingView
    ) {
        if (pFloatingActionButton_Movable != null) {
            pFloatingActionButton_Movable.post(() -> restoreSharedPreferences(pFloatingActionButton_Movable, pNestedScrollView));
            pFloatingActionButton_Movable.setOnTouchListener(new View.OnTouchListener() {
                private static final int MAX_CLICK_DURATION = 200; // ms
                private long startClickTime;
                private float dX, dY;

                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    View parent = (View) view.getParent();
                    float paddingTop, paddingStart, paddingEnd, paddingBottom;
                    if (pPaddingView != null) {
                        paddingTop = Math.max(55f, (float) pPaddingView.getPaddingTop());
                        paddingStart = Math.max(55f, (float) pPaddingView.getPaddingStart());
                        paddingEnd = Math.max(55f, (float) pPaddingView.getPaddingEnd());
                        paddingBottom = Math.max(55f, (float) pPaddingView.getPaddingBottom());
                    } else {
                        paddingTop = 55f;
                        paddingStart = 55f;
                        paddingEnd = 55f;
                        paddingBottom = 55f;
                    }
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            if (pSwipeRefreshLayout != null) {
                                pSwipeRefreshLayout.setEnabled(false);
                            }

                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            dX = view.getX() - event.getRawX();
                            dY = view.getY() - event.getRawY();
                            break;

                        case MotionEvent.ACTION_MOVE:
                            if (pSwipeRefreshLayout != null) {
                                pSwipeRefreshLayout.setEnabled(false);
                            }

                            float newX = event.getRawX() + dX;
                            float newY = event.getRawY() + dY;

                            // Giới hạn không cho nút bay ra khỏi màn hình (tùy chọn)
                            newX = Math.max(paddingStart, Math.min(newX, (float) parent.getWidth() - (float) view.getWidth() - paddingEnd));
                            newY = Math.max(paddingTop, Math.min(newY, (float) parent.getHeight() - (float) view.getHeight() - paddingBottom));

                            view.animate().x(newX).y(newY).setDuration(0).start();

                            break;

                        case MotionEvent.ACTION_UP:
                            if (pSwipeRefreshLayout != null) {
                                if (pNestedScrollView != null) {
                                    pSwipeRefreshLayout.setEnabled(!pNestedScrollView.canScrollVertically(-1));
                                }
                            }

                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;

                            if (clickDuration < MAX_CLICK_DURATION) {
                                view.performClick();
                            } else {
                                float finalX;
                                float finalY = view.getY();

                                // Kiểm tra xem nút đang ở nửa bên trái hay nửa bên phải màn hình
                                if (view.getX() + (view.getWidth() / 2f) < parent.getWidth() / 2f) {
                                    finalX = paddingStart; // Hút về cạnh trái
                                } else {
                                    finalX = parent.getWidth() - view.getWidth() - paddingEnd; // Hút về cạnh phải
                                }

                                view.animate()
                                        .x(finalX)
                                        .setDuration(400) // Thời gian trượt 0.4 giây cho mượt
                                        .setInterpolator(new OvershootInterpolator(0.8f))
                                        .withEndAction(() -> {
                                            // Lưu lại vị trí chuẩn sau khi đã neo
                                            saveSharedPreferences(pFloatingActionButton_Movable, finalX, finalY);
                                        })
                                        .start();
                            }
                            break;
                    }
                    return true;
                }
            });
        }
    }


    protected void adjustFloatingButtonPositionWithAnimation(View mView, View paddingView) {
        requireContext();
        if (getContext() == null) return;
        if (mView != null) {
            mView.post(() -> {
                View parent = (View) mView.getParent();
                if (parent == null) return;
                float paddingTop, paddingStart, paddingEnd, paddingBottom;
                if (paddingView != null) {
                    paddingTop = Math.max(55f, (float) paddingView.getPaddingTop());
                    paddingStart = Math.max(55f, (float) paddingView.getPaddingStart());
                    paddingEnd = Math.max(55f, (float) paddingView.getPaddingEnd());
                    paddingBottom = Math.max(55f, (float) paddingView.getPaddingBottom());
                } else {
                    paddingTop = 55f;
                    paddingStart = 55f;
                    paddingEnd = 55f;
                    paddingBottom = 55f;
                }

                float parentWidth = (float) parent.getWidth();
                float parentHeight = (float) parent.getHeight();
                float rightLimit = (parentWidth - (float) mView.getWidth() - paddingEnd);
                float bottomLimit = (parentHeight - (float) mView.getHeight() - paddingBottom);

                float savedX = mView.getX();
                float savedY = mView.getY();

                if (savedX < paddingStart || savedX > rightLimit || savedY < paddingTop || savedY > bottomLimit) {
                    float finalX;
                    if (savedX <= (parentWidth / 2f)) {
                        finalX = paddingStart;
                    } else {
                        finalX = rightLimit;
                    }

                    float finalY = Math.max(
                            paddingTop,
                            Math.min(savedY, bottomLimit)
                    );

                    mView.animate()
                            .x(finalX)
                            .y(finalY)
                            .setDuration(400)
                            .setInterpolator(new DecelerateInterpolator())
                            .withEndAction(() -> saveSharedPreferences(mView, finalX, finalY))
                            .start();
                } else {
                    saveSharedPreferences(mView, mView.getX(), mView.getY());
                }
            });
        }
    }

    protected void saveSharedPreferences(View mView, float x, float y) {
        requireContext();
        if (getContext() == null) return;
        if (mView == null) return;
        SharedPreferences prefs = requireContext().getSharedPreferences(this.getClass().getName() + "." + mView.getId(), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("x", x);
        editor.putFloat("y", y);
        editor.apply();
    }

    protected void restoreSharedPreferences(View mView, View paddingView) {
        requireContext();
        if (getContext() == null) return;
        if (mView == null) return;
        View parent = (View) mView.getParent();
        if (parent == null) return;

        SharedPreferences prefs = requireContext().getSharedPreferences(this.getClass().getName() + "." + mView.getId(), MODE_PRIVATE);
        float savedX = prefs.getFloat("x", -1f); // Giá trị mặc định là -1
        float savedY = prefs.getFloat("y", parent.getHeight() + 1f);

        mView.post(
                () -> {
                    float paddingTop, paddingStart, paddingEnd, paddingBottom;
                    if (paddingView != null) {
                        paddingTop = Math.max(55f, (float) paddingView.getPaddingTop());
                        paddingStart = Math.max(55f, (float) paddingView.getPaddingStart());
                        paddingEnd = Math.max(55f, (float) paddingView.getPaddingEnd());
                        paddingBottom = Math.max(55f, (float) paddingView.getPaddingBottom());
                    } else {
                        paddingTop = 55f;
                        paddingStart = 55f;
                        paddingEnd = 55f;
                        paddingBottom = 55f;
                    }

                    float parentWidth = (float) parent.getWidth();
                    float parentHeight = (float) parent.getHeight();
                    float rightLimit = (parentWidth - (float) mView.getWidth() - paddingEnd);
                    float bottomLimit = (parentHeight - (float) mView.getHeight() - paddingBottom);

                    if (savedX < paddingStart || savedX > rightLimit || savedY < paddingTop || savedY > bottomLimit) {
                        float finalX;
                        if (savedX <= (parentWidth / 2f)) {
                            finalX = paddingStart;
                        } else {
                            finalX = rightLimit;
                        }

                        float finalY = Math.max(
                                paddingTop,
                                Math.min(savedY, bottomLimit)
                        );

                        mView.setX(finalX);
                        mView.setY(finalY);
                    } else {
                        mView.setX(savedX);
                        mView.setY(savedY);
                    }
                }
        );
    }


    protected Intent getIntentBroadcast(String pValue) {
        Intent mIntent = new Intent(pValue);
        mIntent.setPackage(requireContext().getPackageName());
        return mIntent;
    }

    protected void DoRaiseBroadcast(String pValue) {
        Intent mIntent = getIntentBroadcast(pValue);
        requireContext().sendBroadcast(mIntent);
    }

    protected void DoRaiseAccessTokenExpired() {
        DoRaiseBroadcast("AccessToken_Expired");
    }

}
