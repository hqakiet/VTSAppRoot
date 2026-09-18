package com.vts.vtsapproot.UI.Activities;

import static com.vts.vtsapproot.Tools.gvSystem.App_FunctionConfigProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vts.vtsapproot.API.APIService;
import com.vts.vtsapproot.API.Response.ResMod_Base;
import com.vts.vtsapproot.API.Processes.DoiMatKhau;
import com.vts.vtsapproot.R;
import com.vts.vtsapproot.Tools.ActBase;
import com.vts.vtsapproot.Tools.Message;
import com.vts.vtsapproot.Tools.SingleClickListener;
import com.vts.vtsapproot.Tools.gvSystem;

import java.util.ArrayList;
import java.util.Objects;

public class ActDoiMatKhau
        extends ActBase {

    MaterialToolbar ActDoiMatKhau_MaterialToolbar;

    LinearLayout ActDoiMatKhau_LinearLayout_Data;

    TextInputLayout ActDoiMatKhau_TextInputLayout_MatKhau_Old;
    TextInputEditText ActDoiMatKhau_TextInputEditText_MatKhau_Old;

    TextInputLayout ActDoiMatKhau_TextInputLayout_MatKhau_New;
    TextInputEditText ActDoiMatKhau_TextInputEditText_MatKhau_New;

    TextInputLayout ActDoiMatKhau_TextInputLayout_MatKhau_New_Confirm;
    TextInputEditText ActDoiMatKhau_TextInputEditText_MatKhau_New_Confirm;

    MaterialButton ActDoiMatKhau_MaterialButton_Confirm;

    ShimmerFrameLayout ShimmerFrameLayout_Processing;

    public ActDoiMatKhau() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_base_actdoimatkhau);

        My_TransitionName = !gvSystem.getApp_TietKiemPin() ? getIntent().getStringExtra("TransitionName") : null;
        DoPostponeEnterTransition();

        ActDoiMatKhau_MaterialToolbar = findViewById(R.id.ActDoiMatKhau_MaterialToolbar);
        ActDoiMatKhau_MaterialToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        ActDoiMatKhau_MaterialToolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        My_TransitionObject = ActDoiMatKhau_MaterialToolbar;

        setupOnBackPressed();

        ShimmerFrameLayout_Processing = findViewById(R.id.ShimmerFrameLayout_Processing);
        ShimmerFrameLayout_Processing.stopShimmer();
        ShimmerFrameLayout_Processing.setVisibility(View.GONE);

        initViews();

        ActDoiMatKhau_LinearLayout_Data = findViewById(R.id.ActDoiMatKhau_LinearLayout_Data);
        DoStartPostponedEnterTransition(
                () -> {
                    if (!gvSystem.getApp_TietKiemPin()) {
                        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(ActDoiMatKhau.this, R.anim.layout_showup);
                        ActDoiMatKhau_LinearLayout_Data.setLayoutAnimation(controller);
                        ActDoiMatKhau_LinearLayout_Data.setVisibility(View.VISIBLE);
                    } else {
                        ActDoiMatKhau_LinearLayout_Data.setLayoutAnimation(null);
                        ActDoiMatKhau_LinearLayout_Data.setVisibility(View.VISIBLE);
                    }
                    Do_UnlockedUI();
                }
        );
    }

    private void initViews() {
        ActDoiMatKhau_TextInputLayout_MatKhau_Old = findViewById(R.id.ActDoiMatKhau_TextInputLayout_MatKhau_Old);
        ActDoiMatKhau_TextInputEditText_MatKhau_Old = findViewById(R.id.ActDoiMatKhau_TextInputEditText_MatKhau_Old);
        ActDoiMatKhau_TextInputEditText_MatKhau_Old.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ActDoiMatKhau_TextInputLayout_MatKhau_Old.setError(null);
                ActDoiMatKhau_TextInputLayout_MatKhau_Old.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ActDoiMatKhau_TextInputLayout_MatKhau_New = findViewById(R.id.ActDoiMatKhau_TextInputLayout_MatKhau_New);
        ActDoiMatKhau_TextInputEditText_MatKhau_New = findViewById(R.id.ActDoiMatKhau_TextInputEditText_MatKhau_New);
        ActDoiMatKhau_TextInputEditText_MatKhau_New.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ActDoiMatKhau_TextInputLayout_MatKhau_New.setError(null);
                ActDoiMatKhau_TextInputLayout_MatKhau_New.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ActDoiMatKhau_TextInputLayout_MatKhau_New_Confirm = findViewById(R.id.ActDoiMatKhau_TextInputLayout_MatKhau_New_Confirm);
        ActDoiMatKhau_TextInputEditText_MatKhau_New_Confirm = findViewById(R.id.ActDoiMatKhau_TextInputEditText_MatKhau_New_Confirm);
        ActDoiMatKhau_TextInputEditText_MatKhau_New_Confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ActDoiMatKhau_TextInputLayout_MatKhau_New_Confirm.setError(null);
                ActDoiMatKhau_TextInputLayout_MatKhau_New_Confirm.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ActDoiMatKhau_MaterialButton_Confirm = findViewById(R.id.ActDoiMatKhau_MaterialButton_Confirm);
        ActDoiMatKhau_MaterialButton_Confirm.setOnClickListener(new SingleClickListener() {
            @Override
            public void safeSingleClick(View v) {
                Do_DoiMatKhau();
            }
        });

        LinearLayout_StatusBar = findViewById(R.id.LinearLayout_StatusBar);
        setupStatusBar();
    }

    private void Do_LockedUI() {
        ActDoiMatKhau_TextInputEditText_MatKhau_Old.setEnabled(false);
        ActDoiMatKhau_TextInputEditText_MatKhau_New.setEnabled(false);
        ActDoiMatKhau_TextInputEditText_MatKhau_New_Confirm.setEnabled(false);

        ShimmerFrameLayout_Processing.setVisibility(View.VISIBLE);
        ShimmerFrameLayout_Processing.startShimmer();

        ActDoiMatKhau_MaterialButton_Confirm.setEnabled(false);
    }

    private void Do_UnlockedUI() {
        ActDoiMatKhau_TextInputEditText_MatKhau_Old.setEnabled(true);
        ActDoiMatKhau_TextInputEditText_MatKhau_New.setEnabled(true);
        ActDoiMatKhau_TextInputEditText_MatKhau_New_Confirm.setEnabled(true);

        ShimmerFrameLayout_Processing.stopShimmer();
        ShimmerFrameLayout_Processing.setVisibility(View.GONE);

        ActDoiMatKhau_MaterialButton_Confirm.setEnabled(true);
    }

    private void Do_DoiMatKhau() {
        Do_LockedUI();

        if (!DataIsOK()) {
            Do_UnlockedUI();
            Toast.makeText(ActDoiMatKhau.this, getString(R.string.Com_Message_DataHasError), Toast.LENGTH_SHORT).show();
        } else {
            String mMatKhau_Old = Objects.requireNonNull(ActDoiMatKhau_TextInputEditText_MatKhau_Old.getText()).toString();
            String mMatKhau_New = Objects.requireNonNull(ActDoiMatKhau_TextInputEditText_MatKhau_New.getText()).toString();

            DoiMatKhau.PostData(mMatKhau_Old, mMatKhau_New, new APIService.DoAPI_PostData_Result_DoiMatKhau() {
                @Override
                public void ResultOK(ResMod_Base response) {
                    Message.ShowInfor(ActDoiMatKhau.this, getString(R.string.Act_DoiMatKhau_ProcessOK), (dialogInterface, i) -> {
                        dialogInterface.dismiss();

                        gvSystem.setApp_AccessToken(null);
                        gvSystem.setApp_RefreshToken(null);
                        gvSystem.App_DanhSachChucNang(new ArrayList<>());

                        gvSystem.setApp_CurrentPassword(null);

//                        Intent mIntent = new Intent(ActDoiMatKhau.this, ActLogin.class);
//                        startActivity(mIntent);
                        Intent intent = null;
                        if (App_FunctionConfigProvider != null) {
                            intent = App_FunctionConfigProvider.getLoginActivityIntent(ActDoiMatKhau.this);
                        }
                        if (intent != null) {
                            startActivity(intent);
                        }
                        getOnBackPressedDispatcher().onBackPressed();
                    });
                }

                @Override
                public void ResultFailed(Throwable t) {
                    Do_UnlockedUI();
                    Message.ShowInfor(ActDoiMatKhau.this, getString(R.string.Com_Message_Loi) + t.getMessage());
                }

                @Override
                public void TokenExpired() {
                    DoRaiseAccessTokenExpired();
                    getOnBackPressedDispatcher().onBackPressed();
                }
            });
        }
    }

    private boolean DataIsOK() {
        resetErrors();

        boolean mReturn = true;
        if (ActDoiMatKhau_TextInputEditText_MatKhau_Old.getText() == null ||
                ActDoiMatKhau_TextInputEditText_MatKhau_Old.getText().toString().isEmpty()) {
            ActDoiMatKhau_TextInputLayout_MatKhau_Old.setErrorEnabled(true);
            ActDoiMatKhau_TextInputLayout_MatKhau_Old.setError(getText(R.string.Com_Message_CheckDataNotEmpty));
            mReturn = false;
        }
        if (ActDoiMatKhau_TextInputEditText_MatKhau_New.getText() == null ||
                ActDoiMatKhau_TextInputEditText_MatKhau_New.getText().toString().isEmpty()) {
            ActDoiMatKhau_TextInputLayout_MatKhau_New.setErrorEnabled(true);
            ActDoiMatKhau_TextInputLayout_MatKhau_New.setError(getText(R.string.Com_Message_CheckDataNotEmpty));
            mReturn = false;
        }
        if (ActDoiMatKhau_TextInputEditText_MatKhau_New_Confirm.getText() == null ||
                ActDoiMatKhau_TextInputEditText_MatKhau_New_Confirm.getText().toString().isEmpty()) {
            ActDoiMatKhau_TextInputLayout_MatKhau_New_Confirm.setErrorEnabled(true);
            ActDoiMatKhau_TextInputLayout_MatKhau_New_Confirm.setError(getText(R.string.Com_Message_CheckDataNotEmpty));
            mReturn = false;
        }
        if (
                ActDoiMatKhau_TextInputEditText_MatKhau_New.getText() != null &&
                        !ActDoiMatKhau_TextInputEditText_MatKhau_New.getText().toString().isEmpty() &&
                        ActDoiMatKhau_TextInputEditText_MatKhau_New_Confirm.getText() != null &&
                        !ActDoiMatKhau_TextInputEditText_MatKhau_New_Confirm.getText().toString().isEmpty() &&
                        !ActDoiMatKhau_TextInputEditText_MatKhau_New.getText().toString().equals(ActDoiMatKhau_TextInputEditText_MatKhau_New_Confirm.getText().toString())
        ) {
            ActDoiMatKhau_TextInputLayout_MatKhau_New_Confirm.setErrorEnabled(true);
            ActDoiMatKhau_TextInputLayout_MatKhau_New_Confirm.setError(getText(R.string.Com_Message_CheckDataNotOK));
            mReturn = false;
        }
        return mReturn;
    }

    private void resetErrors() {
        ActDoiMatKhau_TextInputLayout_MatKhau_Old.setError(null);
        ActDoiMatKhau_TextInputLayout_MatKhau_Old.setErrorEnabled(false);

        ActDoiMatKhau_TextInputLayout_MatKhau_New.setError(null);
        ActDoiMatKhau_TextInputLayout_MatKhau_New.setErrorEnabled(false);

        ActDoiMatKhau_TextInputLayout_MatKhau_New_Confirm.setError(null);
        ActDoiMatKhau_TextInputLayout_MatKhau_New_Confirm.setErrorEnabled(false);
    }

}