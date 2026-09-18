package com.vts.vtsapproot.Tools;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.EditText;

public class TextInputHelper {

    public static void applyCleanCodeTextLogic(EditText editText) {
        // 1. Gắn Filter chặn Xuống dòng, Space
        InputFilter cleanFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                StringBuilder builder = new StringBuilder();
                boolean modified = false;

                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);

                    if (c == '\n') {
                        modified = true;
                        continue;
                    }
                    if (c == ' ') {
                        modified = true;
                        continue;
                    }
//                    if (c == ' ') {
//                        boolean isPrevCharSpace = false;
//                        if (builder.length() > 0) {
//                            isPrevCharSpace = (builder.charAt(builder.length() - 1) == ' ');
//                        } else if (dstart > 0 && dest.length() >= dstart) {
//                            isPrevCharSpace = (dest.charAt(dstart - 1) == ' ');
//                        }
//                        if (isPrevCharSpace) {
//                            modified = true;
//                            continue;
//                        }
//                    }
                    builder.append(c);
                }
                return modified ? builder.toString() : null;
            }
        };

        // Giữ lại các filter cũ (ví dụ: maxLength khai báo trong XML)
        InputFilter[] currentFilters = editText.getFilters();
        InputFilter[] newFilters = new InputFilter[currentFilters.length + 1];
        System.arraycopy(currentFilters, 0, newFilters, 0, currentFilters.length);
        newFilters[currentFilters.length] = cleanFilter;
        editText.setFilters(newFilters);

        // 1.5 Tự động xóa lỗi khi có text
//        autoClearError(editText);

        // 2. Gắn sự kiện dọn dẹp khoảng trắng cuối khi rời ô nhập liệu
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String currentText = editText.getText().toString();
                    if (currentText.endsWith(" ")) {
                        // Xóa khoảng trắng cuối cùng
                        editText.setText(currentText.replaceAll("\\s+$", ""));
                    }
                }
            }
        });
    }

    public static void applyCleanCodeTextLogic(EditText editText, Runnable runable) {
        applyCleanCodeTextLogic(editText);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                runable.run();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });
    }

    public static void applyCleanTextLogic(EditText editText) {
        // 1. Gắn Filter chặn Xuống dòng, Space đầu, và Space kép
        InputFilter cleanFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                StringBuilder builder = new StringBuilder();
                boolean modified = false;

                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);

                    if (c == '\n') {
                        modified = true;
                        continue;
                    }
                    if (c == ' ' && dstart == 0 && builder.length() == 0) {
                        modified = true;
                        continue;
                    }
                    if (c == ' ') {
                        boolean isPrevCharSpace = false;
                        if (builder.length() > 0) {
                            isPrevCharSpace = (builder.charAt(builder.length() - 1) == ' ');
                        } else if (dstart > 0 && dest.length() >= dstart) {
                            isPrevCharSpace = (dest.charAt(dstart - 1) == ' ');
                        }
                        if (isPrevCharSpace) {
                            modified = true;
                            continue;
                        }
                    }
                    builder.append(c);
                }
                return modified ? builder.toString() : null;
            }
        };

        // Giữ lại các filter cũ (ví dụ: maxLength khai báo trong XML)
        InputFilter[] currentFilters = editText.getFilters();
        InputFilter[] newFilters = new InputFilter[currentFilters.length + 1];
        System.arraycopy(currentFilters, 0, newFilters, 0, currentFilters.length);
        newFilters[currentFilters.length] = cleanFilter;
        editText.setFilters(newFilters);

        // 2. Gắn sự kiện dọn dẹp khoảng trắng cuối khi rời ô nhập liệu
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String currentText = editText.getText().toString();
                    if (currentText.endsWith(" ")) {
                        // Xóa khoảng trắng cuối cùng
                        editText.setText(currentText.replaceAll("\\s+$", ""));
                    }
                }
            }
        });
    }

    public static void applyCleanTextLogic(EditText editText, Runnable runable) {
        applyCleanTextLogic(editText);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                runable.run();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });
    }

    public static void applyReadOnly(EditText editText) {
        if (editText == null) return;
        editText.setKeyListener(null);
        editText.setCursorVisible(false);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setTextIsSelectable(true);
        editText.setLongClickable(true);
    }

    public static void applyEditMode(EditText editText, KeyListener originalListener, boolean isEditable) {
        if (editText == null) return;
        if (isEditable) {
            editText.setKeyListener(originalListener);
        } else {
            editText.setKeyListener(null);
        }
        editText.setCursorVisible(isEditable);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setTextIsSelectable(true);
        editText.setLongClickable(true);
    }

    public static void applyEditMode(EditText editText, boolean isEditable) {
        if (editText == null) return;
        editText.setKeyListener(null);
        editText.setCursorVisible(isEditable);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setTextIsSelectable(true);
        editText.setLongClickable(true);
    }

//    public static void applyNumberTextLogic(EditText editText, NumberTextWatcherValueChanged listener) {
//        editText.addTextChangedListener(new NumberTextWatcher.Builder(editText, listener)
//                .setAllowNegative(false)
//                .setMinValue(0.0)
//                .setMaxValue(999999999999.0)
//                .setDecimalLen(gvSystem.App_sysval_SOLESOLUONG)
//                .build());
//
//
//    }

}
