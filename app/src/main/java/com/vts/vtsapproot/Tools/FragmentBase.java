package com.vts.vtsapproot.Tools;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.vts.vtsapproot.API.Interfaces.DateFromToPickerInterface;
import com.vts.vtsapproot.API.Interfaces.DatePickerInterface;

import java.util.Date;

public class FragmentBase
        extends Fragment {

    protected final MutableLiveData<CustomListEvents<?>> _CustomListEvents = new MutableLiveData<>();
    protected boolean My_AllowLayoutAnimation = true;

    public LiveData<CustomListEvents<?>> My_CustomListEvents() {
        return _CustomListEvents;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        applyLayoutInsets(view);
    }

    protected Insets MySystemBarInsets() {
        requireContext();
        return  ((ActBase) requireContext()).MySystemBarInsets;
    }
    protected int MyBottomMenuHeight() {
        requireContext();
        return  ((ActBase) requireContext()).MyBottomMenuHeight;
    }

    protected void applyLayoutInsets(@NonNull View view) {

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


    protected void adjustFloatingButtonPositionWithAnimation(View mView) {
        if (mView != null) {
            mView.post(() -> {
                View parent = (View) mView.getParent();
                if (parent == null) return;

                float parentWidth = (float) parent.getWidth();
                float parentHeight = (float) parent.getHeight();
                float padding = 55f;
                float topLimit = padding
                        + (MySystemBarInsets() == null ? 0 : MySystemBarInsets().top);
                float leftLimit = padding;
                float rightLimit = (parentWidth - (float) mView.getWidth() - padding);
                float bottomLimit = (parentHeight - (float) mView.getHeight() - padding)
                        - (MySystemBarInsets() == null ? 0 : MySystemBarInsets().bottom)
                        - MyBottomMenuHeight();

                SharedPreferences prefs = requireContext().getSharedPreferences(this.getClass().getName() + "." + mView.getId(), MODE_PRIVATE);
                float savedX = prefs.getFloat("x", -1f);
                float savedY = prefs.getFloat("y", parentHeight + 1f);

                if (savedX < leftLimit || savedX > rightLimit || savedY < topLimit || savedY > bottomLimit) {
                    float finalX;
                    if (savedX <= (parentWidth / 2f)) {
                        finalX = leftLimit;
                    } else {
                        finalX = rightLimit;
                    }

                    float finalY = Math.max(
                            topLimit,
                            Math.min(savedY, bottomLimit)
                    );

                    mView.animate()
                            .x(finalX)
                            .y(finalY)
                            .setDuration(400)
                            .setInterpolator(new DecelerateInterpolator())
                            .withEndAction(() -> saveSharedPreferences(mView, finalX, finalY))
                            .start();
                }
            });
        }
    }

    protected void saveSharedPreferences(View mView, float x, float y) {
        requireContext();
        if (mView == null) return;
        SharedPreferences prefs = requireContext().getSharedPreferences(this.getClass().getName() + "." + mView.getId(), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("x", x);
        editor.putFloat("y", y);
        editor.apply();
    }

    protected void restoreSharedPreferences(View mView) {
        requireContext();
        if (mView == null) return;
        View parent = (View) mView.getParent();
        if (parent == null) return;

        SharedPreferences prefs = requireContext().getSharedPreferences(this.getClass().getName() + "." + mView.getId(), MODE_PRIVATE);
        float x = prefs.getFloat("x", -1f); // Giá trị mặc định là -1
        float y = prefs.getFloat("y", parent.getHeight() + 1f);

        mView.post(() -> {
            mView.setX(x);
            mView.setY(y);
        });
    }

    protected Intent getIntentBroadcast(String pValue) {
        Intent mIntent = new Intent(pValue);
        mIntent.setPackage(requireContext().getPackageName());
        return mIntent;
    }
    protected void DoRaiseBroadcast(String pValue) {
        Intent mIntent = getIntentBroadcast("AccessToken_Expired");
        requireContext().sendBroadcast(mIntent);
    }
    protected void DoRaiseAccessTokenExpired() {
        DoRaiseBroadcast("AccessToken_Expired");
    }


}
