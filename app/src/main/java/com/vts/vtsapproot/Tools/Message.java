package com.vts.vtsapproot.Tools;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.vts.vtsapproot.R;

public class Message {

    // Hàm dùng chung để áp dụng style bo góc cho tất cả AlertDialog
    private static AlertDialog.Builder createBuilder(Context pContext, String pTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(pContext, R.style.DialogCustom);
        builder.setTitle(pTitle);
        return builder;
    }

    public static void ShowInfor(Context pContext, String pMessage) {
        AlertDialog.Builder mAlertDialogBuilder = createBuilder(pContext, pContext.getString(R.string.Message_Com_Title));
        mAlertDialogBuilder.setCancelable(true);
        mAlertDialogBuilder.setMessage(pMessage);
        mAlertDialogBuilder.setPositiveButton("     " + pContext.getString(R.string.Message_Caption_ButtonOK) + "     ", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();

        android.widget.Button btnPositive = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (btnPositive != null) {
            btnPositive.setTextColor(pContext.getColor(R.color.md_theme_onPrimary));
            btnPositive.setBackgroundResource(R.drawable.bg_dialog_button);
            btnPositive.setBackgroundTintList(android.content.res.ColorStateList.valueOf(pContext.getColor(R.color.md_theme_primaryContainer))); // Màu đỏ cảnh báo
        }
    }

    public static void ShowInfor(Context pContext, String pMessage, String pTitle) {
        AlertDialog.Builder mAlertDialogBuilder = createBuilder(pContext, pTitle);
        mAlertDialogBuilder.setCancelable(true);
        mAlertDialogBuilder.setMessage(pMessage);
        mAlertDialogBuilder.setPositiveButton("     " + pContext.getString(R.string.Message_Caption_ButtonOK) + "     ", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();

        android.widget.Button btnPositive = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (btnPositive != null) {
            btnPositive.setTextColor(pContext.getColor(R.color.md_theme_onPrimary));
            btnPositive.setBackgroundResource(R.drawable.bg_dialog_button);
            btnPositive.setBackgroundTintList(android.content.res.ColorStateList.valueOf(pContext.getColor(R.color.md_theme_primaryContainer))); // Màu đỏ cảnh báo
        }
    }

    public static void ShowInfor(Context pContext, String pMessage, MessageOKButton pMessageOKButton) {
        AlertDialog.Builder mAlertDialogBuilder = createBuilder(pContext, pContext.getString(R.string.Message_Com_Title));
        mAlertDialogBuilder.setCancelable(false);
        mAlertDialogBuilder.setMessage(pMessage);
        mAlertDialogBuilder.setPositiveButton("     " + pContext.getString(R.string.Message_Caption_ButtonOK) + "     ", pMessageOKButton::OnOKButtonClick);

        AlertDialog mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();

        android.widget.Button btnPositive = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (btnPositive != null) {
            btnPositive.setTextColor(pContext.getColor(R.color.md_theme_onPrimary));
            btnPositive.setBackgroundResource(R.drawable.bg_dialog_button);
            btnPositive.setBackgroundTintList(android.content.res.ColorStateList.valueOf(pContext.getColor(R.color.md_theme_primaryContainer))); // Màu đỏ cảnh báo
        }
    }

    public static void ShowInfor(Context pContext, String pMessage, String pTitle, MessageOKButton pMessageOKButton) {
        AlertDialog.Builder mAlertDialogBuilder = createBuilder(pContext, pTitle);
        mAlertDialogBuilder.setCancelable(false);
        mAlertDialogBuilder.setMessage(pMessage);
        mAlertDialogBuilder.setPositiveButton("     " + pContext.getString(R.string.Message_Caption_ButtonOK) + "     ", pMessageOKButton::OnOKButtonClick);

        AlertDialog mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();

        android.widget.Button btnPositive = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (btnPositive != null) {
            btnPositive.setTextColor(pContext.getColor(R.color.md_theme_onPrimary));
            btnPositive.setBackgroundResource(R.drawable.bg_dialog_button);
            btnPositive.setBackgroundTintList(android.content.res.ColorStateList.valueOf(pContext.getColor(R.color.md_theme_primaryContainer))); // Màu đỏ cảnh báo
        }
    }

    public static void ShowConfirm(Context pContext, String pMessage, MessageYesButton pMessageYesNoButton) {
        AlertDialog.Builder mAlertDialogBuilder = createBuilder(pContext, pContext.getString(R.string.Message_Com_Title_Confirm));
        mAlertDialogBuilder.setCancelable(false);
        mAlertDialogBuilder.setMessage(pMessage);
        mAlertDialogBuilder.setPositiveButton("     " + pContext.getString(R.string.Message_Caption_ButtonYes) + "     ", pMessageYesNoButton::OnYesButtonClick);
        mAlertDialogBuilder.setNegativeButton("     " + pContext.getString(R.string.Message_Caption_ButtonNo) + "     ", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();

        android.widget.Button btnPositive = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (btnPositive != null) {
            btnPositive.setTextColor(pContext.getColor(R.color.md_theme_onPrimary));
            btnPositive.setBackgroundResource(R.drawable.bg_dialog_button);
            btnPositive.setBackgroundTintList(android.content.res.ColorStateList.valueOf(pContext.getColor(R.color.md_theme_primaryContainer))); // Màu đỏ cảnh báo
        }

        android.widget.Button btnNegative = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (btnNegative != null) {
            btnNegative.setTextColor(pContext.getColor(R.color.md_theme_onPrimary));
            btnNegative.setBackgroundResource(R.drawable.bg_dialog_button);
            btnNegative.setBackgroundTintList(android.content.res.ColorStateList.valueOf(pContext.getColor(R.color.md_theme_primaryContainer))); // Màu nền xám nhạt
        }
    }

    public static void ShowConfirm(Context pContext, String pMessage, MessageYesNoButton pMessageYesNoButton) {
        AlertDialog.Builder mAlertDialogBuilder = createBuilder(pContext, pContext.getString(R.string.Message_Com_Title_Confirm));
        mAlertDialogBuilder.setCancelable(false);
        mAlertDialogBuilder.setMessage(pMessage);
        mAlertDialogBuilder.setPositiveButton("     " + pContext.getString(R.string.Message_Caption_ButtonYes) + "     ", pMessageYesNoButton::OnYesButtonClick);
        mAlertDialogBuilder.setNegativeButton("     " + pContext.getString(R.string.Message_Caption_ButtonNo) + "     ", pMessageYesNoButton::OnNoButtonClick);

        AlertDialog mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();

        android.widget.Button btnPositive = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (btnPositive != null) {
            btnPositive.setTextColor(pContext.getColor(R.color.md_theme_onPrimary));
            btnPositive.setBackgroundResource(R.drawable.bg_dialog_button);
            btnPositive.setBackgroundTintList(android.content.res.ColorStateList.valueOf(pContext.getColor(R.color.md_theme_primaryContainer))); // Màu đỏ cảnh báo
        }

        android.widget.Button btnNegative = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (btnNegative != null) {
            btnNegative.setTextColor(pContext.getColor(R.color.md_theme_onPrimary));
            btnNegative.setBackgroundResource(R.drawable.bg_dialog_button);
            btnNegative.setBackgroundTintList(android.content.res.ColorStateList.valueOf(pContext.getColor(R.color.md_theme_primaryContainer))); // Màu nền xám nhạt
        }
    }

    public static void ShowConfirm(Context pContext, String pMessage, String pTitle, MessageYesButton pMessageYesNoButton) {
        AlertDialog.Builder mAlertDialogBuilder = createBuilder(pContext, pTitle);
        mAlertDialogBuilder.setCancelable(false);
        mAlertDialogBuilder.setMessage(pMessage);
        mAlertDialogBuilder.setPositiveButton("     " + pContext.getString(R.string.Message_Caption_ButtonYes) + "     ", pMessageYesNoButton::OnYesButtonClick);
        mAlertDialogBuilder.setNegativeButton("     " + pContext.getString(R.string.Message_Caption_ButtonNo) + "     ", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();

        android.widget.Button btnPositive = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (btnPositive != null) {
            btnPositive.setTextColor(pContext.getColor(R.color.md_theme_onPrimary));
            btnPositive.setBackgroundResource(R.drawable.bg_dialog_button);
            btnPositive.setBackgroundTintList(android.content.res.ColorStateList.valueOf(pContext.getColor(R.color.md_theme_primaryContainer))); // Màu đỏ cảnh báo
        }

        android.widget.Button btnNegative = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (btnNegative != null) {
            btnNegative.setTextColor(pContext.getColor(R.color.md_theme_onPrimary));
            btnNegative.setBackgroundResource(R.drawable.bg_dialog_button);
            btnNegative.setBackgroundTintList(android.content.res.ColorStateList.valueOf(pContext.getColor(R.color.md_theme_primaryContainer))); // Màu nền xám nhạt
        }
    }

    public static void ShowConfirm(Context pContext, String pMessage, String pTitle, MessageYesNoButton pMessageYesNoButton) {
        AlertDialog.Builder mAlertDialogBuilder = createBuilder(pContext, pTitle);
        mAlertDialogBuilder.setCancelable(false);
        mAlertDialogBuilder.setMessage(pMessage);
        mAlertDialogBuilder.setPositiveButton("     " + pContext.getString(R.string.Message_Caption_ButtonYes) + "     ", pMessageYesNoButton::OnYesButtonClick);
        mAlertDialogBuilder.setNegativeButton("     " + pContext.getString(R.string.Message_Caption_ButtonNo) + "     ", pMessageYesNoButton::OnNoButtonClick);

        AlertDialog mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();

        android.widget.Button btnPositive = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (btnPositive != null) {
            btnPositive.setTextColor(pContext.getColor(R.color.md_theme_onPrimary));
            btnPositive.setBackgroundResource(R.drawable.bg_dialog_button);
            btnPositive.setBackgroundTintList(android.content.res.ColorStateList.valueOf(pContext.getColor(R.color.md_theme_primaryContainer))); // Màu đỏ cảnh báo
        }

        android.widget.Button btnNegative = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (btnNegative != null) {
            btnNegative.setTextColor(pContext.getColor(R.color.md_theme_onPrimary));
            btnNegative.setBackgroundResource(R.drawable.bg_dialog_button);
            btnNegative.setBackgroundTintList(android.content.res.ColorStateList.valueOf(pContext.getColor(R.color.md_theme_primaryContainer))); // Màu nền xám nhạt
        }
    }
}