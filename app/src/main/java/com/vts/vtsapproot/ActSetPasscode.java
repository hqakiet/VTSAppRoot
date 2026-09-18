package com.vts.vtsapproot;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vts.vtsapproot.Tools.ActBase;
import com.vts.vtsapproot.Tools.SingleClickListener;
import com.vts.vtsapproot.Tools.gvSystem;

import java.util.Objects;

public class ActSetPasscode
        extends ActBase {

    MaterialToolbar ActSetPassCode_MaterialToolbar;

    LinearLayout ActSetPassCode_LinearLayout_Data;

    TextInputLayout ActSetPassCode_TextInputLayout_OldPassword;
    TextInputEditText ActSetPassCode_TextInputEditText_OldPassword;

    TextInputLayout ActSetPassCode_TextInputLayout_Password;
    TextInputEditText ActSetPassCode_TextInputEditText_Password;

    TextInputLayout ActSetPassCode_TextInputLayout_RePassword;
    TextInputEditText ActSetPassCode_TextInputEditText_RePassword;

    MaterialButton ActSetPassCode_MaterialButton_Confirm;

    ShimmerFrameLayout ShimmerFrameLayout_Processing;

    String My_OpenAction = "NEWPASSCODE";
    String My_ReturnAction = "";

    public ActSetPasscode() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_base_actsetpasscode);

        My_TransitionName = !gvSystem.getApp_TietKiemPin() ? getIntent().getStringExtra("TransitionName") : null;
        DoPostponeEnterTransition();

        if (getIntent().getStringExtra("OpenAction") != null)
            My_OpenAction = getIntent().getStringExtra("OpenAction");

        ActSetPassCode_MaterialToolbar = findViewById(R.id.ActSetPassCode_MaterialToolbar);
        ActSetPassCode_MaterialToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        ActSetPassCode_MaterialToolbar.setNavigationOnClickListener(new SingleClickListener() {
            @Override
            public void safeSingleClick(View v) {
                My_ReturnAction = My_OpenAction + ".CANCELED";
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        My_TransitionObject = ActSetPassCode_MaterialToolbar;

        setupOnBackPressed();

        ShimmerFrameLayout_Processing = findViewById(R.id.ShimmerFrameLayout_Processing);
        ShimmerFrameLayout_Processing.stopShimmer();
        ShimmerFrameLayout_Processing.setVisibility(View.GONE);

        initViews();

        ActSetPassCode_LinearLayout_Data = findViewById(R.id.ActSetPassCode_LinearLayout_Data);
        DoStartPostponedEnterTransition(
                () -> {
                    if (!gvSystem.getApp_TietKiemPin()) {
                        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(ActSetPasscode.this, R.anim.layout_showup);
                        ActSetPassCode_LinearLayout_Data.setLayoutAnimation(controller);
                        ActSetPassCode_LinearLayout_Data.setVisibility(View.VISIBLE);
                    } else {
                        ActSetPassCode_LinearLayout_Data.setLayoutAnimation(null);
                        ActSetPassCode_LinearLayout_Data.setVisibility(View.VISIBLE);
                    }
                    Do_UnlockedUI();
                }
        );
    }

    private void initViews() {
        ActSetPassCode_TextInputLayout_OldPassword = findViewById(R.id.ActSetPassCode_TextInputLayout_OldPassword);
        ActSetPassCode_TextInputEditText_OldPassword = findViewById(R.id.ActSetPassCode_TextInputEditText_OldPassword);
        if (Objects.equals(My_OpenAction, "MODIFYPASSCODE")) {
            ActSetPassCode_TextInputEditText_OldPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    ActSetPassCode_TextInputLayout_OldPassword.setError(null);
                    ActSetPassCode_TextInputLayout_OldPassword.setErrorEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        } else
            ActSetPassCode_TextInputLayout_OldPassword.setVisibility(View.GONE);

        ActSetPassCode_TextInputLayout_Password = findViewById(R.id.ActSetPassCode_TextInputLayout_Password);
        ActSetPassCode_TextInputEditText_Password = findViewById(R.id.ActSetPassCode_TextInputEditText_Password);
        ActSetPassCode_TextInputEditText_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ActSetPassCode_TextInputLayout_Password.setError(null);
                ActSetPassCode_TextInputLayout_Password.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ActSetPassCode_TextInputLayout_RePassword = findViewById(R.id.ActSetPassCode_TextInputLayout_RePassword);
        ActSetPassCode_TextInputEditText_RePassword = findViewById(R.id.ActSetPassCode_TextInputEditText_RePassword);
        ActSetPassCode_TextInputEditText_RePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ActSetPassCode_TextInputLayout_RePassword.setError(null);
                ActSetPassCode_TextInputLayout_RePassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ActSetPassCode_MaterialButton_Confirm = findViewById(R.id.ActSetPassCode_MaterialButton_Confirm);
        ActSetPassCode_MaterialButton_Confirm.setOnClickListener(new SingleClickListener() {
            @Override
            public void safeSingleClick(View v) {
                if (DataIsOK()) {
                    Do_SetPasscode();
                }
            }
        });

        LinearLayout_StatusBar = findViewById(R.id.LinearLayout_StatusBar);
        setupStatusBar();
    }

    private void Do_LockedUI() {
        ActSetPassCode_TextInputEditText_OldPassword.setEnabled(false);
        ActSetPassCode_TextInputEditText_Password.setEnabled(false);
        ActSetPassCode_TextInputEditText_RePassword.setEnabled(false);

        ShimmerFrameLayout_Processing.setVisibility(View.VISIBLE);
        ShimmerFrameLayout_Processing.startShimmer();

        ActSetPassCode_MaterialButton_Confirm.setEnabled(false);
    }

    private void Do_UnlockedUI() {
        ActSetPassCode_TextInputEditText_OldPassword.setEnabled(true);
        ActSetPassCode_TextInputEditText_Password.setEnabled(true);
        ActSetPassCode_TextInputEditText_RePassword.setEnabled(true);

        ShimmerFrameLayout_Processing.stopShimmer();
        ShimmerFrameLayout_Processing.setVisibility(View.GONE);

        ActSetPassCode_MaterialButton_Confirm.setEnabled(true);
    }

    @Override
    protected void PerformBackAction() {
        if (My_ReturnAction.isEmpty()) {
            My_ReturnAction = My_OpenAction + ".CANCELED";
        }

        // 2. Gửi Broadcast ngay
        Intent intent = new Intent();
        intent.setAction(My_ReturnAction);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);

        super.PerformBackAction();
    }

    private void Do_SetPasscode() {
        Do_LockedUI();

        String password = Objects.requireNonNull(ActSetPassCode_TextInputEditText_Password.getText()).toString();
        gvSystem.setApp_CurrentPasscode(password);
        My_ReturnAction = My_OpenAction + ".OK";
        getOnBackPressedDispatcher().onBackPressed();
    }


    private boolean DataIsOK() {
        boolean mReturn = true;

        resetErrors();

        if (Objects.equals(My_OpenAction, "MODIFYPASSCODE")) {
            if (ActSetPassCode_TextInputEditText_OldPassword.getText() == null || ActSetPassCode_TextInputEditText_OldPassword.getText().toString().isEmpty()) {
                ActSetPassCode_TextInputLayout_OldPassword.setErrorEnabled(true);
                ActSetPassCode_TextInputLayout_OldPassword.setError(getText(R.string.Com_Message_CheckDataNotEmpty));
                mReturn = false;
            } else {
                if (!Objects.equals(ActSetPassCode_TextInputEditText_OldPassword.getText().toString(), gvSystem.getApp_CurrentPasscode())) {
                    ActSetPassCode_TextInputLayout_OldPassword.setErrorEnabled(true);
                    ActSetPassCode_TextInputLayout_OldPassword.setError(getText(R.string.Com_Message_CheckDataNotOK));
                    mReturn = false;
                }
            }
        }

        if (ActSetPassCode_TextInputEditText_Password.getText() == null || ActSetPassCode_TextInputEditText_Password.getText().toString().isEmpty()) {
            ActSetPassCode_TextInputLayout_Password.setErrorEnabled(true);
            ActSetPassCode_TextInputLayout_Password.setError(getText(R.string.Com_Message_CheckDataNotEmpty));
            mReturn = false;
        } else {
            if (ActSetPassCode_TextInputEditText_Password.getText().toString().length() != 6) {
                ActSetPassCode_TextInputLayout_Password.setErrorEnabled(true);
                ActSetPassCode_TextInputLayout_Password.setError(getText(R.string.Com_Message_CheckDataNotOK));
                mReturn = false;
            }
        }
        if (ActSetPassCode_TextInputEditText_RePassword.getText() == null || ActSetPassCode_TextInputEditText_RePassword.getText().toString().isEmpty()) {
            ActSetPassCode_TextInputLayout_RePassword.setErrorEnabled(true);
            ActSetPassCode_TextInputLayout_RePassword.setError(getText(R.string.Com_Message_CheckDataNotEmpty));
            mReturn = false;
        } else {
            if (ActSetPassCode_TextInputEditText_RePassword.getText().toString().length() != 6) {
                ActSetPassCode_TextInputLayout_RePassword.setErrorEnabled(true);
                ActSetPassCode_TextInputLayout_RePassword.setError(getText(R.string.Com_Message_CheckDataNotOK));
                mReturn = false;
            }
        }
        if (
                ActSetPassCode_TextInputEditText_Password.getText() != null && !ActSetPassCode_TextInputEditText_Password.getText().toString().isEmpty() && ActSetPassCode_TextInputEditText_Password.getText().toString().length() == 6
                        && ActSetPassCode_TextInputEditText_RePassword.getText() != null && !ActSetPassCode_TextInputEditText_RePassword.getText().toString().isEmpty() && ActSetPassCode_TextInputEditText_RePassword.getText().toString().length() == 6
                        && !Objects.equals(ActSetPassCode_TextInputEditText_Password.getText().toString(), ActSetPassCode_TextInputEditText_RePassword.getText().toString())
        ) {
            ActSetPassCode_TextInputLayout_RePassword.setErrorEnabled(true);
            ActSetPassCode_TextInputLayout_RePassword.setError(getText(R.string.Com_Message_CheckDataNotOK));
            mReturn = false;
        }

        return mReturn;
    }

    private void resetErrors() {
        ActSetPassCode_TextInputLayout_OldPassword.setErrorEnabled(false);
        ActSetPassCode_TextInputLayout_OldPassword.setError(null);

        ActSetPassCode_TextInputLayout_Password.setErrorEnabled(false);
        ActSetPassCode_TextInputLayout_Password.setError(null);

        ActSetPassCode_TextInputLayout_RePassword.setErrorEnabled(false);
        ActSetPassCode_TextInputLayout_RePassword.setError(null);
    }

}