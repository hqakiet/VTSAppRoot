package com.vts.vtsapproot.Tools;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.SystemBarStyle;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.vts.vtsapproot.API.Interfaces.BaseSingleProcessInterface;
import com.vts.vtsapproot.API.Interfaces.DateFromToPickerInterface;
import com.vts.vtsapproot.API.Interfaces.DatePickerInterface;
import com.vts.vtsapproot.ActAbout;
import com.vts.vtsapproot.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public abstract class ActBase
        extends FragmentActivity {

    protected final MutableLiveData<CustomListEvents<?>> _CustomListEvents = new MutableLiveData<>();
    public boolean isLandscape;
    public boolean isLargeScreen;
    public boolean isXLargeScreen;
    public int MySpanCount = 1;
    protected String My_TransitionName;
    protected View My_TransitionObject;
    protected boolean My_AllowLayoutAnimation = true;
    protected LinearLayout LinearLayout_StatusBar;
    protected CompositeDisposable My_CompositeDisposable = new CompositeDisposable();

    public LiveData<CustomListEvents<?>> My_CustomListEvents() {
        return _CustomListEvents;
    }

    protected Insets MySystemBarInsets = Insets.of(0, 0, 0, 0);
    protected int MyBottomMenuHeight;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // chặn xoay
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Bật EdgeToEdge nhưng để màu trong suốt để lộ màu nền Activity bên dưới
        if (gvSystem.App_SystembarsIsDark) {
            EdgeToEdge.enable(
                    this,
                    SystemBarStyle.dark(Color.TRANSPARENT),
                    SystemBarStyle.dark(Color.TRANSPARENT)
            );
        } else {
            int mColor = ContextCompat.getColor(this, gvSystem.App_AutoProcessSystembarsLightColor);
            EdgeToEdge.enable(
                    this,
                    SystemBarStyle.light(mColor, mColor),
                    SystemBarStyle.light(mColor, mColor)
            );
        }

        super.onCreate(savedInstanceState);

        MySpanCount = 1;
        if (isXLargeScreen)
            MySpanCount = 2;
        gvSystem.ResetLayout(this);

    }

    @Override
    public void setContentView(int layoutResID) {
        // 2. Ruột app
        View contentView = getLayoutInflater().inflate(layoutResID, null, false);
        setContentView(contentView);
    }

    @Override
    public void setContentView(View view) {
        if (gvSystem.App_AutoProcessSystembars) {
            // 1. Vỏ app
            FrameLayout baseWrapper = new FrameLayout(this);
            baseWrapper.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            int mColor;
            if (gvSystem.App_SystembarsIsDark) {
                mColor = ContextCompat.getColor(this, gvSystem.App_AutoProcessSystembarsDarkColor);
            } else {
                mColor = ContextCompat.getColor(this, gvSystem.App_AutoProcessSystembarsLightColor);
            }
            baseWrapper.setBackgroundColor(mColor);

            if (view != null && view.getBackground() == null) {
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.md_theme_background));
            }

            // Đảm bảo view có LayoutParams của FrameLayout để margin có tác dụng
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, -1);
            if (view != null) {
                view.setLayoutParams(layoutParams);
            }

            baseWrapper.addView(view);
            super.setContentView(baseWrapper);

            // 3. Xử lý Insets
            applySafeInsets(baseWrapper, view);
        } else {
            super.setContentView(view);
            initSystemBarInsets(view);
        }
    }

    protected void initSystemBarInsets(View pView) {
        WindowInsetsCompat rootInsets = ViewCompat.getRootWindowInsets(pView);
        if (rootInsets != null) {
            MySystemBarInsets = rootInsets.getInsets(WindowInsetsCompat.Type.systemBars());
        } else {
            // Fallback qua listener nếu lúc đó chưa có
            ViewCompat.setOnApplyWindowInsetsListener(pView, (v, insets) -> {
                MySystemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());

                applyLayoutInsets();

                return WindowInsetsCompat.CONSUMED;
            });
            ViewCompat.requestApplyInsets(pView);
        }
    }

    private void applySafeInsets(View wrapper, View content) {
        ViewCompat.setOnApplyWindowInsetsListener(wrapper, (v, windowInsets) -> {
            Insets mInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            // TOP: Đẩy padding cho vỏ
            v.setPadding(0, mInsets.top, 0, 0);

            // BOTTOM: Đẩy Margin cho ruột
            // Ép kiểu chắc chắn về FrameLayout.LayoutParams
            if (content.getLayoutParams() instanceof FrameLayout.LayoutParams lp) {
                lp.bottomMargin = mInsets.bottom; // NHẤC BỔNG NỘI DUNG
                content.setLayoutParams(lp);
            }

            applyLayoutInsets();

            return WindowInsetsCompat.CONSUMED;
        });

        // Ép hệ thống phải gửi Insets ngay lập tức
        ViewCompat.requestApplyInsets(wrapper);
    }

    protected void applyLayoutInsets() {
    }

    @Override
    protected void onResume() {
        setupEnterSharedElementCallback();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        My_CompositeDisposable.clear();
        super.onDestroy();
    }


    @Override
    protected void attachBaseContext(Context newBase) {

        Configuration config = new Configuration(newBase.getResources().getConfiguration());

        if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                < Configuration.SCREENLAYOUT_SIZE_LARGE) {

            config.fontScale = 1f;
        }

        Context context = newBase.createConfigurationContext(config);

        isLandscape = (config.orientation == Configuration.ORIENTATION_LANDSCAPE);
        if (config.smallestScreenWidthDp >= 720) {
            isXLargeScreen = true;
            isLargeScreen = false;
        } else if (config.smallestScreenWidthDp >= 600) {
            isXLargeScreen = false;
            isLargeScreen = true;
        } else {
            isXLargeScreen = false;
            isLargeScreen = false;
        }

        super.attachBaseContext(context);
    }

    protected void setupDateOnlyPicker(View anchorView, Date pInitDate, DatePickerInterface pPickedDate) {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.inclay_base_singledateandtimepicker, null);
        PopupWindow popupWindow = new PopupWindow(
                view,
                ((isLandscape || isLargeScreen || isXLargeScreen)
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : ViewGroup.LayoutParams.MATCH_PARENT), // Rộng
                ViewGroup.LayoutParams.WRAP_CONTENT, // Cao
                true // Cho phép bấm ra ngoài để đóng
        );

        SingleDateAndTimePicker singleDateAndTimePicker = view.findViewById(R.id.SingleDateAndTimePicker);
        MaterialButton singleDateAndTimePicker_MaterialButton_NgayHienHanh = view.findViewById(R.id.SingleDateAndTimePicker_MaterialButton_NgayHienHanh);
        MaterialButton singleDateAndTimePicker_MaterialButton_OK = view.findViewById(R.id.SingleDateAndTimePicker_MaterialButton_OK);

        if (isLandscape || isLargeScreen || isXLargeScreen) {
            MaterialTextView SingleDateAndTimePicker_MaterialTextView_Caption = view.findViewById(R.id.SingleDateAndTimePicker_MaterialTextView_Caption);
            SingleDateAndTimePicker_MaterialTextView_Caption.setText(getText(R.string.Com_Title_ChonNgay));
            SingleDateAndTimePicker_MaterialTextView_Caption.setVisibility(View.VISIBLE);
        }

        singleDateAndTimePicker.setMinDate(gvSystem.getMinDate());
        singleDateAndTimePicker.setMaxDate(gvSystem.getMaxDate());

        singleDateAndTimePicker.setDisplayHours(false);
        singleDateAndTimePicker.setDisplayMinutes(false);
        singleDateAndTimePicker.setDisplayDays(false);
        singleDateAndTimePicker.setDisplayMonths(true);
        singleDateAndTimePicker.setDisplayYears(true);
        singleDateAndTimePicker.setDisplayDaysOfMonth(true);
        singleDateAndTimePicker.setCurved(true);
        singleDateAndTimePicker.setCyclic(true);
        singleDateAndTimePicker.setTypeface(Typeface.DEFAULT_BOLD);

        singleDateAndTimePicker.setDefaultDate(pInitDate);

        singleDateAndTimePicker_MaterialButton_NgayHienHanh.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            singleDateAndTimePicker.selectDate(calendar);
        });
        singleDateAndTimePicker_MaterialButton_OK.setOnClickListener(v -> {
            pPickedDate.DatePicked(singleDateAndTimePicker.getDate());
            popupWindow.dismiss();
        });

        if (!gvSystem.getApp_TietKiemPin())
            popupWindow.setAnimationStyle(android.R.style.Animation);
        popupWindow.setElevation(10);

        if (isLandscape || isLargeScreen || isXLargeScreen) {
            popupWindow.showAtLocation(anchorView, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        } else {
            popupWindow.showAsDropDown(anchorView, 0, 0);
        }
    }

    protected void setupDateOnlyDialogPicker(View anchorView, Date pInitDate, DatePickerInterface pPickedDate) {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.inclay_base_singledateandtimepicker, null);
        PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.WRAP_CONTENT, // Rộng
                ViewGroup.LayoutParams.WRAP_CONTENT, // Cao
                true // Cho phép bấm ra ngoài để đóng
        );

        SingleDateAndTimePicker singleDateAndTimePicker = view.findViewById(R.id.SingleDateAndTimePicker);
        LinearLayout LinearLayout_SingleDateAndTimePicker = view.findViewById(R.id.LinearLayout_SingleDateAndTimePicker);
        MaterialTextView SingleDateAndTimePicker_MaterialTextView_Caption = view.findViewById(R.id.SingleDateAndTimePicker_MaterialTextView_Caption);
        MaterialButton singleDateAndTimePicker_MaterialButton_NgayHienHanh = view.findViewById(R.id.SingleDateAndTimePicker_MaterialButton_NgayHienHanh);
        MaterialButton singleDateAndTimePicker_MaterialButton_OK = view.findViewById(R.id.SingleDateAndTimePicker_MaterialButton_OK);

        LinearLayout_SingleDateAndTimePicker.setBackgroundResource(R.drawable.bg_popup_menu2);

        SingleDateAndTimePicker_MaterialTextView_Caption.setText(getText(R.string.Com_Title_ChonNgay));
        SingleDateAndTimePicker_MaterialTextView_Caption.setVisibility(View.VISIBLE);

        singleDateAndTimePicker.setMinDate(gvSystem.getMinDate());
        singleDateAndTimePicker.setMaxDate(gvSystem.getMaxDate());

        singleDateAndTimePicker.setDisplayHours(false);
        singleDateAndTimePicker.setDisplayMinutes(false);
        singleDateAndTimePicker.setDisplayDays(false);
        singleDateAndTimePicker.setDisplayMonths(true);
        singleDateAndTimePicker.setDisplayYears(true);
        singleDateAndTimePicker.setDisplayDaysOfMonth(true);
        singleDateAndTimePicker.setCurved(true);
        singleDateAndTimePicker.setCyclic(true);
        singleDateAndTimePicker.setTypeface(Typeface.DEFAULT_BOLD);

        singleDateAndTimePicker.setDefaultDate(pInitDate);

        singleDateAndTimePicker_MaterialButton_NgayHienHanh.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            singleDateAndTimePicker.selectDate(calendar);
        });
        singleDateAndTimePicker_MaterialButton_OK.setOnClickListener(v -> {
            pPickedDate.DatePicked(singleDateAndTimePicker.getDate());
            popupWindow.dismiss();
        });

        if (!gvSystem.getApp_TietKiemPin())
            popupWindow.setAnimationStyle(android.R.style.Animation);
        popupWindow.setElevation(10);

        popupWindow.showAtLocation(anchorView, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
    }

    protected void setupDateTimePicker(View anchorView, Date pInitDate, DatePickerInterface pPickedDate) {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.inclay_base_singledateandtimepicker, null);
        PopupWindow popupWindow = new PopupWindow(
                view,
                ((isLandscape || isLargeScreen || isXLargeScreen)
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : ViewGroup.LayoutParams.MATCH_PARENT), // Rộng
                ViewGroup.LayoutParams.WRAP_CONTENT, // Cao
                true // Cho phép bấm ra ngoài để đóng
        );

        SingleDateAndTimePicker singleDateAndTimePicker = view.findViewById(R.id.SingleDateAndTimePicker);
        MaterialButton singleDateAndTimePicker_MaterialButton_NgayHienHanh = view.findViewById(R.id.SingleDateAndTimePicker_MaterialButton_NgayHienHanh);
        MaterialButton singleDateAndTimePicker_MaterialButton_OK = view.findViewById(R.id.SingleDateAndTimePicker_MaterialButton_OK);

        if (isLandscape || isLargeScreen || isXLargeScreen) {
            MaterialTextView SingleDateAndTimePicker_MaterialTextView_Caption = view.findViewById(R.id.SingleDateAndTimePicker_MaterialTextView_Caption);
            SingleDateAndTimePicker_MaterialTextView_Caption.setText(getText(R.string.Com_Title_ChonNgay));
            SingleDateAndTimePicker_MaterialTextView_Caption.setVisibility(View.VISIBLE);
        }

        singleDateAndTimePicker.setMinDate(gvSystem.getMinDate());
        singleDateAndTimePicker.setMaxDate(gvSystem.getMaxDate());

        singleDateAndTimePicker.setDisplayHours(true);
        singleDateAndTimePicker.setDisplayMinutes(true);
        singleDateAndTimePicker.setDisplayDays(false);
        singleDateAndTimePicker.setDisplayMonths(true);
        singleDateAndTimePicker.setDisplayYears(true);
        singleDateAndTimePicker.setDisplayDaysOfMonth(true);
        singleDateAndTimePicker.setCurved(true);
        singleDateAndTimePicker.setCyclic(true);
        singleDateAndTimePicker.setTypeface(Typeface.DEFAULT_BOLD);

        singleDateAndTimePicker.setDefaultDate(pInitDate);

        singleDateAndTimePicker_MaterialButton_NgayHienHanh.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            singleDateAndTimePicker.selectDate(calendar);
        });
        singleDateAndTimePicker_MaterialButton_OK.setOnClickListener(v -> {
            pPickedDate.DatePicked(singleDateAndTimePicker.getDate());
            popupWindow.dismiss();
        });

        if (!gvSystem.getApp_TietKiemPin())
            popupWindow.setAnimationStyle(android.R.style.Animation);
        popupWindow.setElevation(10);

        if (isLandscape || isLargeScreen || isXLargeScreen) {
            popupWindow.showAtLocation(anchorView, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        } else {
            popupWindow.showAsDropDown(anchorView, 0, 0);
        }
    }

    protected void setupDateTimeDialogPicker(View anchorView, Date pInitDate, DatePickerInterface pPickedDate) {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.inclay_base_singledateandtimepicker, null);
        PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.WRAP_CONTENT, // Rộng
                ViewGroup.LayoutParams.WRAP_CONTENT, // Cao
                true // Cho phép bấm ra ngoài để đóng
        );

        SingleDateAndTimePicker singleDateAndTimePicker = view.findViewById(R.id.SingleDateAndTimePicker);
        MaterialTextView SingleDateAndTimePicker_MaterialTextView_Caption = view.findViewById(R.id.SingleDateAndTimePicker_MaterialTextView_Caption);
        MaterialButton singleDateAndTimePicker_MaterialButton_NgayHienHanh = view.findViewById(R.id.SingleDateAndTimePicker_MaterialButton_NgayHienHanh);
        MaterialButton singleDateAndTimePicker_MaterialButton_OK = view.findViewById(R.id.SingleDateAndTimePicker_MaterialButton_OK);

        SingleDateAndTimePicker_MaterialTextView_Caption.setText(getText(R.string.Com_Title_ChonNgayGio));
        SingleDateAndTimePicker_MaterialTextView_Caption.setVisibility(View.VISIBLE);

        singleDateAndTimePicker.setMinDate(gvSystem.getMinDate());
        singleDateAndTimePicker.setMaxDate(gvSystem.getMaxDate());

        singleDateAndTimePicker.setDisplayHours(true);
        singleDateAndTimePicker.setDisplayMinutes(true);
        singleDateAndTimePicker.setDisplayDays(false);
        singleDateAndTimePicker.setDisplayMonths(true);
        singleDateAndTimePicker.setDisplayYears(true);
        singleDateAndTimePicker.setDisplayDaysOfMonth(true);
        singleDateAndTimePicker.setCurved(true);
        singleDateAndTimePicker.setCyclic(true);
        singleDateAndTimePicker.setTypeface(Typeface.DEFAULT_BOLD);

        singleDateAndTimePicker.setDefaultDate(pInitDate);

        singleDateAndTimePicker_MaterialButton_NgayHienHanh.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            singleDateAndTimePicker.selectDate(calendar);
        });
        singleDateAndTimePicker_MaterialButton_OK.setOnClickListener(v -> {
            pPickedDate.DatePicked(singleDateAndTimePicker.getDate());
            popupWindow.dismiss();
        });

        if (!gvSystem.getApp_TietKiemPin())
            popupWindow.setAnimationStyle(android.R.style.Animation);
        popupWindow.setElevation(10);

        popupWindow.showAtLocation(anchorView, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
    }

    protected void setupNgayTuanThangPicker(View anchorView, DateFromToPickerInterface pPickedDateFromTo) {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.inclay_base_picker_ngaytuanthang, null);
        PopupWindow popupWindow = new PopupWindow(
                view,
                ViewGroup.LayoutParams.WRAP_CONTENT, // Rộng
                ViewGroup.LayoutParams.WRAP_CONTENT, // Cao
                true // Cho phép bấm ra ngoài để đóng
        );

        MaterialTextView Picker_MaterialTextView_HomQua = view.findViewById(R.id.Picker_MaterialTextView_HomQua);
        MaterialTextView Picker_MaterialTextView_HomNay = view.findViewById(R.id.Picker_MaterialTextView_HomNay);
        MaterialTextView Picker_MaterialTextView_TuanTruoc = view.findViewById(R.id.Picker_MaterialTextView_TuanTruoc);
        MaterialTextView Picker_MaterialTextView_TuanNay = view.findViewById(R.id.Picker_MaterialTextView_TuanNay);
        MaterialTextView Picker_MaterialTextView_ThangTruoc = view.findViewById(R.id.Picker_MaterialTextView_ThangTruoc);
        MaterialTextView Picker_MaterialTextView_ThangNay = view.findViewById(R.id.Picker_MaterialTextView_ThangNay);
        MaterialTextView Picker_MaterialTextView_NamTruoc = view.findViewById(R.id.Picker_MaterialTextView_NamTruoc);
        MaterialTextView Picker_MaterialTextView_NamNay = view.findViewById(R.id.Picker_MaterialTextView_NamNay);

        Picker_MaterialTextView_HomQua.setOnClickListener(
                v -> {
                    Calendar mCalendar = Calendar.getInstance();
                    mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
                    mCalendar.set(Calendar.MILLISECOND, 0);
                    mCalendar.set(Calendar.SECOND, 0);
                    mCalendar.set(Calendar.MINUTE, 0);
                    mCalendar.set(Calendar.HOUR, 0);
                    mCalendar.add(Calendar.DATE, -1);
                    pPickedDateFromTo.DateFromToPicked(mCalendar.getTime(), mCalendar.getTime());
                    popupWindow.dismiss();
                }
        );
        Picker_MaterialTextView_HomNay.setOnClickListener(
                v -> {
                    Calendar mCalendar = Calendar.getInstance();
                    mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
                    mCalendar.set(Calendar.MILLISECOND, 0);
                    mCalendar.set(Calendar.SECOND, 0);
                    mCalendar.set(Calendar.MINUTE, 0);
                    mCalendar.set(Calendar.HOUR, 0);
                    pPickedDateFromTo.DateFromToPicked(mCalendar.getTime(), mCalendar.getTime());
                    popupWindow.dismiss();
                }
        );

        Picker_MaterialTextView_TuanTruoc.setOnClickListener(
                v -> {
                    Date mDateFrom, mDateTo;
                    Calendar mCalendar = Calendar.getInstance();
                    mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
                    mCalendar.set(Calendar.MILLISECOND, 0);
                    mCalendar.set(Calendar.SECOND, 0);
                    mCalendar.set(Calendar.MINUTE, 0);
                    mCalendar.set(Calendar.HOUR, 0);
                    mCalendar.add(Calendar.DATE, -7);
                    mDateFrom = mCalendar.getTime();
                    mCalendar.add(Calendar.DATE, 6);
                    mDateTo = mCalendar.getTime();
                    pPickedDateFromTo.DateFromToPicked(mDateFrom, mDateTo);
                    popupWindow.dismiss();
                }
        );
        Picker_MaterialTextView_TuanNay.setOnClickListener(
                v -> {
                    Date mDateFrom, mDateTo;
                    Calendar mCalendar = Calendar.getInstance();
                    mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
                    mCalendar.set(Calendar.MILLISECOND, 0);
                    mCalendar.set(Calendar.SECOND, 0);
                    mCalendar.set(Calendar.MINUTE, 0);
                    mCalendar.set(Calendar.HOUR, 0);
                    mDateFrom = mCalendar.getTime();
                    mCalendar.add(Calendar.DATE, 6);
                    mDateTo = mCalendar.getTime();
                    pPickedDateFromTo.DateFromToPicked(mDateFrom, mDateTo);
                    popupWindow.dismiss();
                }
        );

        Picker_MaterialTextView_ThangTruoc.setOnClickListener(
                v -> {
                    Date mDateFrom, mDateTo;
                    Calendar mCalendar = Calendar.getInstance();
                    mCalendar.set(Calendar.DAY_OF_MONTH, 1);
                    mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
                    mCalendar.set(Calendar.MILLISECOND, 0);
                    mCalendar.set(Calendar.SECOND, 0);
                    mCalendar.set(Calendar.MINUTE, 0);
                    mCalendar.set(Calendar.HOUR, 0);
                    mCalendar.add(Calendar.MONTH, -1);
                    mDateFrom = mCalendar.getTime();
                    mCalendar.add(Calendar.MONTH, 1);
                    mCalendar.add(Calendar.DATE, -1);
                    mDateTo = mCalendar.getTime();
                    pPickedDateFromTo.DateFromToPicked(mDateFrom, mDateTo);
                    popupWindow.dismiss();
                }
        );
        Picker_MaterialTextView_ThangNay.setOnClickListener(
                v -> {
                    Date mDateFrom, mDateTo;
                    Calendar mCalendar = Calendar.getInstance();
                    mCalendar.set(Calendar.DAY_OF_MONTH, 1);
                    mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
                    mCalendar.set(Calendar.MILLISECOND, 0);
                    mCalendar.set(Calendar.SECOND, 0);
                    mCalendar.set(Calendar.MINUTE, 0);
                    mCalendar.set(Calendar.HOUR, 0);
                    mDateFrom = mCalendar.getTime();
                    mCalendar.add(Calendar.MONTH, 1);
                    mCalendar.add(Calendar.DATE, -1);
                    mDateTo = mCalendar.getTime();
                    pPickedDateFromTo.DateFromToPicked(mDateFrom, mDateTo);
                    popupWindow.dismiss();
                }
        );

        Picker_MaterialTextView_NamTruoc.setOnClickListener(
                v -> {
                    Date mDateFrom, mDateTo;
                    Calendar mCalendar = Calendar.getInstance();
                    mCalendar.set(Calendar.DAY_OF_YEAR, 1);
                    mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
                    mCalendar.set(Calendar.MILLISECOND, 0);
                    mCalendar.set(Calendar.SECOND, 0);
                    mCalendar.set(Calendar.MINUTE, 0);
                    mCalendar.set(Calendar.HOUR, 0);
                    mCalendar.add(Calendar.YEAR, -1);
                    mDateFrom = mCalendar.getTime();
                    mCalendar.add(Calendar.YEAR, 1);
                    mCalendar.add(Calendar.DATE, -1);
                    mDateTo = mCalendar.getTime();
                    pPickedDateFromTo.DateFromToPicked(mDateFrom, mDateTo);
                    popupWindow.dismiss();
                }
        );
        Picker_MaterialTextView_NamNay.setOnClickListener(
                v -> {
                    Date mDateFrom, mDateTo;
                    Calendar mCalendar = Calendar.getInstance();
                    mCalendar.set(Calendar.DAY_OF_YEAR, 1);
                    mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
                    mCalendar.set(Calendar.MILLISECOND, 0);
                    mCalendar.set(Calendar.SECOND, 0);
                    mCalendar.set(Calendar.MINUTE, 0);
                    mCalendar.set(Calendar.HOUR, 0);
                    mDateFrom = mCalendar.getTime();
                    mCalendar.add(Calendar.YEAR, 1);
                    mCalendar.add(Calendar.DATE, -1);
                    mDateTo = mCalendar.getTime();
                    pPickedDateFromTo.DateFromToPicked(mDateFrom, mDateTo);
                    popupWindow.dismiss();
                }
        );

        if (!gvSystem.getApp_TietKiemPin())
            popupWindow.setAnimationStyle(android.R.style.Animation);
        popupWindow.setElevation(10);

        popupWindow.showAsDropDown(anchorView, 0, 0);
//        popupWindow.showAtLocation(anchorView, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
    }


    private void setupEnterSharedElementCallback() {
        if (!gvSystem.getApp_TietKiemPin() && My_TransitionObject != null && My_TransitionName != null && !My_TransitionName.isEmpty()) {
            My_TransitionObject.setTransitionName(My_TransitionName);
            My_TransitionObject.requestLayout();
            My_TransitionObject.invalidate();
            setEnterSharedElementCallback(new androidx.core.app.SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    names.clear();
                    names.add(My_TransitionName);
                    sharedElements.clear();
                    sharedElements.put(My_TransitionName, My_TransitionObject);
                }
            });
        }
    }

    protected void DoPostponeEnterTransition() {
        if (!gvSystem.getApp_TietKiemPin() && My_TransitionName != null && !My_TransitionName.isEmpty()) {
            supportPostponeEnterTransition();
        }
    }

    protected void DoStartPostponedEnterTransition() {
        if (!gvSystem.getApp_TietKiemPin() && My_TransitionName != null && !My_TransitionName.isEmpty()) {
            supportStartPostponedEnterTransition();
        }
    }

    protected void DoStartPostponedEnterTransition(BaseSingleProcessInterface pProcessed) {
        if (!gvSystem.getApp_TietKiemPin() && My_TransitionName != null && !My_TransitionName.isEmpty()) {
            android.transition.Transition sharedElementTransition = getWindow().getSharedElementEnterTransition();
            if (sharedElementTransition != null) {
                sharedElementTransition.addListener(new android.transition.Transition.TransitionListener() {
                    @Override
                    public void onTransitionEnd(android.transition.Transition transition) {
                        // Hủy lắng nghe để tránh kích hoạt lại không mong muốn
                        transition.removeListener(this);

                        // 🔥 ĐÚNG THỜI ĐIỂM: Màn hình đã phóng to xong, View ổn định vị trí, bắt đầu nạp dữ liệu!
                        pProcessed.onCompleted();
                    }

                    @Override
                    public void onTransitionStart(android.transition.Transition transition) {
                    }

                    @Override
                    public void onTransitionCancel(android.transition.Transition transition) {
                    }

                    @Override
                    public void onTransitionPause(android.transition.Transition transition) {
                    }

                    @Override
                    public void onTransitionResume(android.transition.Transition transition) {
                    }
                });
            } else {
                // Phòng hờ thiết bị cũ hoặc hiệu ứng bị null thì gọi dữ liệu luôn không để kẹt màn hình
                pProcessed.onCompleted();
            }

            supportStartPostponedEnterTransition();
        } else {
            pProcessed.onCompleted();
        }
    }

    protected void PerformBackAction() {
        if (!gvSystem.getApp_TietKiemPin()) {
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(this::supportFinishAfterTransition, 70);
        } else {
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(this::finish, 70);
        }
    }

    protected void setupOnBackPressed() {
        if (!gvSystem.getApp_TietKiemPin()) {
            setupEnterSharedElementCallback();
        }
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                PerformBackAction();
            }
        });
    }


    protected void setupStatusBar() {
        if (LinearLayout_StatusBar != null) {
            LinearLayout_StatusBar.setOnClickListener(new SingleClickListener() {
                @Override
                public void safeSingleClick(View v) {
                    if (!gvSystem.getApp_TietKiemPin()) {
                        LinearLayout_StatusBar.setTransitionName(null);
                        LinearLayout_StatusBar.setTransitionName("ActAbout");
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ActBase.this, LinearLayout_StatusBar, "ActAbout");

                        Intent intent = new Intent(ActBase.this, ActAbout.class);
                        intent.putExtra("TransitionName", "ActAbout");
                        startActivity(intent, options.toBundle());
                    } else {
                        Intent intent = new Intent(ActBase.this, ActAbout.class);
                        startActivity(intent);
                    }
                }
            });
        }
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
                        + (MySystemBarInsets == null ? 0 : MySystemBarInsets.top);
                float leftLimit = padding;
                float rightLimit = (parentWidth - (float) mView.getWidth() - padding);
                float bottomLimit = (parentHeight - (float) mView.getHeight() - padding)
                        - (MySystemBarInsets == null ? 0 : MySystemBarInsets.bottom)
                        - MyBottomMenuHeight;

                SharedPreferences prefs = getSharedPreferences(this.getClass().getName() + "." + mView.getId(), MODE_PRIVATE);
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
        if (mView != null) {
            SharedPreferences prefs = getSharedPreferences(this.getClass().getName() + "." + mView.getId(), MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat("x", x);
            editor.putFloat("y", y);
            editor.apply();
        }
    }

    protected void restoreSharedPreferences(View mView) {
        if (mView != null) {
            View parent = (View) mView.getParent();
            if (parent == null) return;

            SharedPreferences prefs = getSharedPreferences(this.getClass().getName() + "." + mView.getId(), MODE_PRIVATE);
            float x = prefs.getFloat("x", -1f); // Giá trị mặc định là -1
            float y = prefs.getFloat("y", parent.getHeight() + 1f);

            mView.post(() -> {
                mView.setX(x);
                mView.setY(y);
            });
        }
    }


    protected Intent getIntentBroadcast(String pValue) {
        Intent mIntent = new Intent(pValue);
        mIntent.setPackage(getPackageName());
        return mIntent;
    }
    protected void DoRaiseBroadcast(String pValue) {
        Intent mIntent = getIntentBroadcast(pValue);
        sendBroadcast(mIntent);
    }
    protected void DoRaiseAccessTokenExpired() {
        DoRaiseBroadcast("AccessToken_Expired");
    }

}
