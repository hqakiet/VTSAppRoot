package com.vts.vtsapproot.Tools;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;

import com.vts.vtsapproot.R;

public class ThemeUtils {

    @ColorInt
    public static int getColorFromAttr(Context context, @AttrRes int attrColor) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrColor, typedValue, true);
        return typedValue.data;
    }
    @ColorInt
    public static int getColorFromAttr(@AttrRes int attrColor) {
        TypedValue typedValue = new TypedValue();
        MyApplication.getAppContext().getTheme().resolveAttribute(attrColor, typedValue, true);
        return typedValue.data;
    }

    public static ColorStateList getZeroColorStateList() {
        return ColorStateList.valueOf(MyApplication.getAppContext().getColor(R.color.MyZeroColor));
    }

    public static ColorStateList getNoneZeroColorStateList() {
        return ColorStateList.valueOf(MyApplication.getAppContext().getColor(R.color.md_theme_onSurface));
    }

    public static ColorStateList getNegativeColorStateList() {
        return ColorStateList.valueOf(MyApplication.getAppContext().getColor(R.color.md_theme_error));
    }

    public static ColorStateList getHeaderZeroColorStateList() {
        return ColorStateList.valueOf(MyApplication.getAppContext().getColor(R.color.MyZeroColor));
    }

    public static ColorStateList getHeaderNoneZeroColorStateList() {
        return ColorStateList.valueOf(MyApplication.getAppContext().getColor(R.color.md_theme_onPrimary));
    }

    public static ColorStateList getHeaderNegativeColorStateList() {
        return ColorStateList.valueOf(MyApplication.getAppContext().getColor(R.color.md_theme_onPrimary));
    }

}
