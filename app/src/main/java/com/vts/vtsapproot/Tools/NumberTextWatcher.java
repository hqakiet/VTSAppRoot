package com.vts.vtsapproot.Tools;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;

import com.vts.vtsapproot.API.Interfaces.NumberTextWatcherValueChanged;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.regex.Pattern;

public class NumberTextWatcher implements TextWatcher, View.OnFocusChangeListener {
    private final EditText myEditText;
    private final NumberTextWatcherValueChanged myNumberTextWatcherValueChanged;

    private final int decimalLen;
    private final boolean allowNegative;
    private final double minValue;
    private final double maxValue;

    private DecimalFormat decimalFormat;
    private DecimalFormat decimalFormatWithDecimal;
    private boolean hasFractionalPart;
    private String current = "";

    // Lưu trữ dấu phân cách theo ngôn ngữ của máy
    private String decSepStr;
    private String grpSepStr;

    private NumberTextWatcher(Builder builder) {
        this.myEditText = builder.editText;
        this.myNumberTextWatcherValueChanged = builder.valueChangedListener;
        this.decimalLen = builder.decimalLen;
        this.allowNegative = builder.allowNegative;
        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;

        initConfiguration();
    }

    private void initConfiguration() {
        // 1. Lấy format theo hệ thống của máy thay vì fix cứng Locale.US
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        decSepStr = String.valueOf(symbols.getDecimalSeparator());
        grpSepStr = String.valueOf(symbols.getGroupingSeparator());

        // Chuỗi Pattern nội bộ của DecimalFormat luôn xài #,### bất kể hệ thống
        decimalFormat = new DecimalFormat("#,###", symbols);

        int inputType = InputType.TYPE_CLASS_NUMBER;
        String keys = "0123456789";

        if (allowNegative) {
            inputType |= InputType.TYPE_NUMBER_FLAG_SIGNED;
            keys += "-";
        }

        if (decimalLen > 0) {
            inputType |= InputType.TYPE_NUMBER_FLAG_DECIMAL;
            hasFractionalPart = true;

            StringBuilder mPattern = new StringBuilder("#,###.");
            for (int i = 0; i < decimalLen; i++) {
                mPattern.append("0");
            }
            decimalFormatWithDecimal = new DecimalFormat(mPattern.toString(), symbols);
            decimalFormatWithDecimal.setDecimalSeparatorAlwaysShown(true);

            // 2. Add phím bấm là dấu phân cách thập phân của hệ thống
            keys += decSepStr;
        } else {
            decimalFormatWithDecimal = new DecimalFormat("#,###", symbols);
            hasFractionalPart = false;
        }

        myEditText.setInputType(inputType);
        myEditText.setKeyListener(DigitsKeyListener.getInstance(keys));
        myEditText.setOnFocusChangeListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable pEditable) {
        try {
            myEditText.removeTextChangedListener(this);

            String input = pEditable.toString();
            if (input.equals(current)) {
                return;
            }

            boolean isNegative = false;
            if (input.startsWith("-")) {
                if (allowNegative) {
                    isNegative = true;
                    input = input.substring(1);
                } else {
                    input = input.replace("-", "");
                }
            }

            // Xóa dấu phân cách hàng nghìn của hệ thống
            String cleanString = input.replace(grpSepStr, "");

            if (cleanString.isEmpty()) {
                current = isNegative ? "-" : "";
                myEditText.setText(current);
                myEditText.setSelection(current.length());
                myEditText.setTag("0");
                if (myNumberTextWatcherValueChanged != null) {
                    myNumberTextWatcherValueChanged.ValueChanged(0.0);
                }
                return;
            }

            // Chặn gõ nhiều dấu thập phân
            int dotCount = cleanString.length() - cleanString.replace(decSepStr, "").length();
            if (dotCount > 1) {
                cleanString = cleanString.substring(0, cleanString.lastIndexOf(decSepStr));
            }

            // Giới hạn số lẻ
            if (cleanString.contains(decSepStr) && decimalLen > 0) {
                int dotIndex = cleanString.indexOf(decSepStr);
                String decimalPart = cleanString.substring(dotIndex + 1);
                if (decimalPart.length() > decimalLen) {
                    cleanString = cleanString.substring(0, dotIndex + 1 + decimalLen);
                }
            }

            // QUAN TRỌNG: Đổi dấu thập phân của hệ thống về dấu chấm (.) chuẩn để Double.parseDouble hiểu được
            String standardDoubleString = cleanString.replace(decSepStr, ".");
            double parsedValue = Double.parseDouble(standardDoubleString.isEmpty() || standardDoubleString.equals(".") ? "0" : standardDoubleString);

            if (isNegative) parsedValue = -parsedValue;

            if (parsedValue > maxValue) {
                myEditText.setText(current);
                myEditText.setSelection(current.length());
                return;
            }

            String formatted = formatDouble(cleanString);
            if (isNegative) formatted = "-" + formatted;

            int selectionStart = myEditText.getSelectionStart();
            int oldLength = myEditText.getText().length();

            current = formatted;
            myEditText.setText(formatted);

            int newLength = formatted.length();
            int targetSelection = selectionStart + (newLength - oldLength);

            if (targetSelection >= 0 && targetSelection <= newLength) {
                myEditText.setSelection(targetSelection);
            } else {
                myEditText.setSelection(newLength);
            }

            // Ní chú ý: Hàm Tag đang lưu trữ số với dấu thập phân của hệ thống hiện tại
            myEditText.setTag(cleanString);
            if (myNumberTextWatcherValueChanged != null) {
                myNumberTextWatcherValueChanged.ValueChanged(parsedValue);
            }

        } catch (Exception e) {
            myEditText.setText(current);
            myEditText.setSelection(current.length());
        } finally {
            myEditText.addTextChangedListener(this);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            try {
                String inputStr = myEditText.getText().toString().replace(grpSepStr, "");

                if (!inputStr.isEmpty() && !inputStr.equals("-") && !inputStr.equals(decSepStr)) {
                    // Chuyển dấu về chuẩn US để hàm Java đọc
                    String standardDoubleString = inputStr.replace(decSepStr, ".");
                    double currentValue = Double.parseDouble(standardDoubleString);

                    if (minValue != Double.NEGATIVE_INFINITY && currentValue < minValue) {
                        currentValue = minValue;
                    }

                    myEditText.removeTextChangedListener(this);

                    String formattedValue;
                    if (hasFractionalPart) {
                        formattedValue = decimalFormatWithDecimal.format(currentValue);
                    } else {
                        formattedValue = decimalFormat.format(currentValue);
                    }

                    current = formattedValue;
                    myEditText.setText(formattedValue);
                    myEditText.setTag(formattedValue.replace(grpSepStr, ""));

                    if (myNumberTextWatcherValueChanged != null) {
                        myNumberTextWatcherValueChanged.ValueChanged(currentValue);
                    }

                    myEditText.addTextChangedListener(this);
                }
            } catch (Exception ignored) {
            }
        }
    }

    private String formatDouble(String cleanString) {
        if (cleanString.isEmpty()) return "";

        if (cleanString.startsWith(decSepStr)) {
            cleanString = "0" + cleanString;
        }

        if (cleanString.endsWith(decSepStr)) {
            String wholePart = cleanString.substring(0, cleanString.length() - 1);
            String stdWholePart = wholePart.replace(decSepStr, ".");
            return decimalFormat.format(Double.parseDouble(stdWholePart)) + decSepStr;

        } else if (cleanString.contains(decSepStr)) {
            // Dùng Pattern.quote để bảo vệ ký tự (phòng hờ dấu chấm bị hiểu lầm là Regex any char)
            String[] parts = cleanString.split(Pattern.quote(decSepStr));
            String stdWholePart = parts[0].replace(decSepStr, ".");
            double parsedWhole = Double.parseDouble(stdWholePart);
            return decimalFormat.format(parsedWhole) + decSepStr + (parts.length > 1 ? parts[1] : "");

        } else {
            String stdWholePart = cleanString.replace(decSepStr, ".");
            return decimalFormat.format(Double.parseDouble(stdWholePart));
        }
    }

    // --- BUILDER PATTERN ---
    public static class Builder {
        private final EditText editText;
        private final NumberTextWatcherValueChanged valueChangedListener;

        private int decimalLen = 0;
        private boolean allowNegative = false;
        private double minValue = Double.NEGATIVE_INFINITY;
        private double maxValue = 999999999999999.0;

        public Builder(EditText editText, NumberTextWatcherValueChanged valueChangedListener) {
            this.editText = editText;
            this.valueChangedListener = valueChangedListener;
        }

        public Builder setDecimalLen(int decimalLen) {
            this.decimalLen = decimalLen;
            return this;
        }

        public Builder setAllowNegative(boolean allowNegative) {
            this.allowNegative = allowNegative;
            return this;
        }

        public Builder setMinValue(double minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder setMaxValue(double maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public NumberTextWatcher build() {
            NumberTextWatcher watcher = new NumberTextWatcher(this);
            this.editText.addTextChangedListener(watcher);
            return watcher;
        }
    }
}
