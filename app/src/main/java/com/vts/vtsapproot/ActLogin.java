package com.vts.vtsapproot;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialCancellationException;
import androidx.credentials.exceptions.GetCredentialException;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.vts.vtsapproot.API.APIService;
import com.vts.vtsapproot.API.Processes.Authen;
import com.vts.vtsapproot.Tools.ActBase;
import com.vts.vtsapproot.Tools.Broadcast;
import com.vts.vtsapproot.Tools.Message;
import com.vts.vtsapproot.Tools.SingleClickListener;
import com.vts.vtsapproot.Tools.gvSystem;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class ActLogin
        extends ActBase {

    MaterialToolbar ActLogin_MaterialToolbar;

    LinearLayout ActLogin_LinearLayout_Data;

    TextInputLayout ActLogin_TextInputLayout_Account;
    TextInputEditText ActLogin_TextInputEditText_Account;

    TextInputLayout ActLogin_TextInputLayout_Password;
    TextInputEditText ActLogin_TextInputEditText_Password;

    MaterialCheckBox ActLogin_MaterialCheckBox_Agreement;
    MaterialTextView ActLogin_MaterialTextView_Agreement;

    MaterialButton ActLogin_MaterialButton_Confirm;
    MaterialButton ActLogin_MaterialButton_Google;

    ShimmerFrameLayout ShimmerFrameLayout_Processing;

    Broadcast My_Broadcast;

    public ActLogin() {
        super();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("ActLogin_MaterialCheckBox_Agreement", ActLogin_MaterialCheckBox_Agreement.isChecked());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_base_actlogin);

        ActLogin_MaterialToolbar = findViewById(R.id.ActLogin_MaterialToolbar);
        ActLogin_MaterialToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        ActLogin_MaterialToolbar.setNavigationOnClickListener(
                new SingleClickListener() {
                    @Override
                    public void safeSingleClick(View v) {
                        finish();
                        System.exit(0);
                    }
                }
        );

        ShimmerFrameLayout_Processing = findViewById(R.id.ShimmerFrameLayout_Processing);
        ShimmerFrameLayout_Processing.stopShimmer();
        ShimmerFrameLayout_Processing.setVisibility(View.GONE);

        initViews();
        setupBroadcast();

        if (savedInstanceState != null) {
            boolean mValue = savedInstanceState.getBoolean("ActLogin_MaterialCheckBox_Agreement", false);
            if (mValue) ActLogin_MaterialCheckBox_Agreement.setChecked(true);
        }

        ActLogin_LinearLayout_Data = findViewById(R.id.ActLogin_LinearLayout_Data);
        if (!gvSystem.getApp_TietKiemPin()) {
            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(ActLogin.this, R.anim.layout_showup);
            ActLogin_LinearLayout_Data.setLayoutAnimation(controller);
            ActLogin_LinearLayout_Data.setVisibility(View.VISIBLE);
        } else {
            ActLogin_LinearLayout_Data.setLayoutAnimation(null);
            ActLogin_LinearLayout_Data.setVisibility(View.VISIBLE);
        }
        Do_UnlockedUI();
    }

    @Override
    protected void onDestroy() {
        if (My_Broadcast != null) unregisterReceiver(My_Broadcast);
        super.onDestroy();
    }

    private void initViews() {
        ActLogin_TextInputLayout_Account = findViewById(R.id.ActLogin_TextInputLayout_Account);
        ActLogin_TextInputEditText_Account = findViewById(R.id.ActLogin_TextInputEditText_Account);
        ActLogin_TextInputEditText_Account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ActLogin_TextInputLayout_Account.setErrorEnabled(false);
                ActLogin_TextInputLayout_Account.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ActLogin_TextInputLayout_Password = findViewById(R.id.ActLogin_TextInputLayout_Password);
        ActLogin_TextInputEditText_Password = findViewById(R.id.ActLogin_TextInputEditText_Password);
        ActLogin_TextInputEditText_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ActLogin_TextInputLayout_Password.setErrorEnabled(false);
                ActLogin_TextInputLayout_Password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ActLogin_MaterialCheckBox_Agreement = findViewById(R.id.ActLogin_MaterialCheckBox_Agreement);
        ActLogin_MaterialCheckBox_Agreement.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    ActLogin_MaterialButton_Confirm.setEnabled(isChecked);
                    ActLogin_MaterialButton_Google.setEnabled(isChecked);
                }
        );

        ActLogin_MaterialTextView_Agreement = findViewById(R.id.ActLogin_MaterialTextView_Agreement);
        setupAgreementText();

        ActLogin_MaterialButton_Confirm = findViewById(R.id.ActLogin_MaterialButton_Confirm);
        ActLogin_MaterialButton_Confirm.setEnabled(false);
        ActLogin_MaterialButton_Confirm.setOnClickListener(new SingleClickListener() {
            @Override
            public void safeSingleClick(View v) {
                if (DataIsOK()) {
                    Do_Start_BasicAuthen();
                }
            }
        });

        ActLogin_MaterialButton_Google = findViewById(R.id.ActLogin_MaterialButton_Google);
        ActLogin_MaterialButton_Google.setEnabled(false);
        ActLogin_MaterialButton_Google.setOnClickListener(new SingleClickListener() {
            @Override
            public void safeSingleClick(View v) {
                resetErrors();
                Do_Start_GoogleAuthen();
            }
        });

        LinearLayout_StatusBar = findViewById(R.id.LinearLayout_StatusBar);
        setupStatusBar();
    }

    private void setupAgreementText() {
        String mDieuKhoan = getString(R.string.Message_Caption_Term);
        String mBaoMat = getString(R.string.Message_Caption_Privacy);

        String mNoiDung = String.format(getString(R.string.ActLogin_TextView_Agreement), mDieuKhoan, mBaoMat);

        SpannableString spannableString = new SpannableString(mNoiDung);

        BiConsumer<String, Runnable> applyLink = (text, action) -> {
            int mStart = mNoiDung.indexOf(text);
            int mEnd = mStart + text.length();

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    action.run();
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };

            spannableString.setSpan(clickableSpan, mStart, mEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), mStart, mEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        };

        applyLink.accept(
                mDieuKhoan,
                () -> {
                    if (ActLogin_MaterialCheckBox_Agreement.isEnabled()) {
                        Intent mIntent = new Intent(ActLogin.this, ActPolicy.class);
                        mIntent.putExtra("StartTab", "Terms_Service");
                        mIntent.putExtra("LoginConfirmed", ActLogin_MaterialCheckBox_Agreement.isChecked());
                        if (!gvSystem.getApp_TietKiemPin()) {
                            String mTransitionName = "ActPolicy";
                            ActLogin_MaterialTextView_Agreement.setTransitionName(null);
                            ActLogin_MaterialTextView_Agreement.setTransitionName(mTransitionName);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, ActLogin_MaterialTextView_Agreement, mTransitionName);

                            mIntent.putExtra("TransitionName", mTransitionName);
                            startActivity(mIntent, options.toBundle());
                        } else {
                            startActivity(mIntent);
                        }
                    }
                }
        );

        applyLink.accept(
                mBaoMat,
                () -> {
                    if (ActLogin_MaterialCheckBox_Agreement.isEnabled()) {
                        Intent mIntent = new Intent(ActLogin.this, ActPolicy.class);
                        mIntent.putExtra("StartTab", "Privacy_Policies");
                        mIntent.putExtra("LoginConfirmed", ActLogin_MaterialCheckBox_Agreement.isChecked());

                        if (!gvSystem.getApp_TietKiemPin()) {
                            String mTransitionName = "ActPolicy";
                            ActLogin_MaterialTextView_Agreement.setTransitionName(null);
                            ActLogin_MaterialTextView_Agreement.setTransitionName(mTransitionName);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, ActLogin_MaterialTextView_Agreement, mTransitionName);

                            mIntent.putExtra("TransitionName", mTransitionName);
                            startActivity(mIntent, options.toBundle());
                        } else {
                            startActivity(mIntent);
                        }
                    }
                }
        );

        ActLogin_MaterialTextView_Agreement.setText(spannableString);
        ActLogin_MaterialTextView_Agreement.setMovementMethod(LinkMovementMethod.getInstance());
        ActLogin_MaterialTextView_Agreement.setHighlightColor(Color.TRANSPARENT);
    }

    private void setupBroadcast() {
        My_Broadcast = new Broadcast() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Objects.equals(intent.getAction(), "Policy.ConfirmOK")) {
                    ActLogin_MaterialCheckBox_Agreement.setChecked(true);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("Policy.ConfirmOK");
        ContextCompat.registerReceiver(this, My_Broadcast, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    private void Do_LockedUI() {
        ActLogin_TextInputEditText_Account.setEnabled(false);
        ActLogin_TextInputLayout_Password.setEnabled(false);

        ShimmerFrameLayout_Processing.setVisibility(View.VISIBLE);
        ShimmerFrameLayout_Processing.startShimmer();

        ActLogin_MaterialCheckBox_Agreement.setEnabled(false);

        ActLogin_MaterialButton_Confirm.setEnabled(false);
        ActLogin_MaterialButton_Google.setEnabled(false);
    }

    private void Do_UnlockedUI() {
        ActLogin_TextInputEditText_Account.setEnabled(true);
        ActLogin_TextInputLayout_Password.setEnabled(true);

        ShimmerFrameLayout_Processing.stopShimmer();
        ShimmerFrameLayout_Processing.setVisibility(View.GONE);

        ActLogin_MaterialCheckBox_Agreement.setEnabled(true);

        ActLogin_MaterialButton_Confirm.setEnabled(ActLogin_MaterialCheckBox_Agreement.isChecked());
        ActLogin_MaterialButton_Google.setEnabled(ActLogin_MaterialCheckBox_Agreement.isChecked());
    }

    private void Do_Start_BasicAuthen() {
        Do_LockedUI();

        String mAccount = Objects.requireNonNull(ActLogin_TextInputEditText_Account.getText()).toString().trim();
        String mPassword = Objects.requireNonNull(ActLogin_TextInputEditText_Password.getText()).toString().trim();

        Do_DangNhap_UserPwd(mAccount, mPassword);
    }

    private void Do_DangNhap_UserPwd(String pAccount, String pPassword) {
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> Authen.DangNhap_UserPwd(20, pAccount, pPassword, new APIService.DoAPI_Authen_Result() {
            @Override
            public void GetDataOK() {
                Do_ShowMain();
            }

            @Override
            public void GetDataFailed(Throwable t) {
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> Authen.DangNhap_UserPwd(40, pAccount, pPassword, new APIService.DoAPI_Authen_Result() {
                    @Override
                    public void GetDataOK() {
                        Do_ShowMain();
                    }

                    @Override
                    public void GetDataFailed(Throwable t) {
                        Do_UnlockedUI();
                        Message.ShowInfor(ActLogin.this, getString(R.string.Com_Message_Loi) + t.getMessage());
                    }
                }), 2000);
            }
        }), 100);
    }

    private void Do_Start_GoogleAuthen() {
        Do_LockedUI();

        Executor mExecutor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mExecutor = ActLogin.this.getMainExecutor();
        } else {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }

        CredentialManager credentialManager = CredentialManager.create(ActLogin.this);
        GetGoogleIdOption getGoogleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(gvSystem.App_ClientID)
                .setAutoSelectEnabled(true)
                .build();
        GetCredentialRequest getCredentialRequest = new GetCredentialRequest.Builder()
                .addCredentialOption(getGoogleIdOption)
                .build();
        credentialManager.getCredentialAsync(
                ActLogin.this,
                getCredentialRequest,
                new CancellationSignal(),
                mExecutor,
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        Credential credential = result.getCredential();
                        if (credential instanceof CustomCredential) {
                            GoogleIdTokenCredential mGoogleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
                            gvSystem.App_GoogleSignInToken = mGoogleIdTokenCredential.getIdToken();
                            gvSystem.App_GoogleSignInAccount.setEmail(mGoogleIdTokenCredential.getEmail());
                            gvSystem.App_GoogleSignInAccount.setDisplayName(mGoogleIdTokenCredential.getDisplayName());
                            gvSystem.App_GoogleSignInAccount.setFamilyName(mGoogleIdTokenCredential.getFamilyName());
                            gvSystem.App_GoogleSignInAccount.setGivenName(mGoogleIdTokenCredential.getGivenName());
                            gvSystem.App_GoogleSignInAccount.setProfilePictureUri(mGoogleIdTokenCredential.getProfilePictureUri());
                            gvSystem.setApp_GoogleAccount(mGoogleIdTokenCredential.getEmail());
                            Do_DangNhap_Google();
                        } else {
                            Do_UnlockedUI();
                            Message.ShowInfor(ActLogin.this, getString(R.string.Com_Message_LoiTuChoiDangNhapGoogle), (dialogInterface, i) -> dialogInterface.dismiss());
                        }
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e1) {
                        if (e1 instanceof GetCredentialCancellationException) {
                            Do_UnlockedUI();
                            Message.ShowInfor(ActLogin.this, getString(R.string.Com_Message_LoiTuChoiDangNhapGoogle), (dialogInterface, i) -> dialogInterface.dismiss());
                        } else {
                            CredentialManager credentialManager = CredentialManager.create(ActLogin.this);
                            GetGoogleIdOption getGoogleIdOptionNotAuthorized = new GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId(gvSystem.App_ClientID)
                                    .setAutoSelectEnabled(false)
                                    .build();
                            GetCredentialRequest getCredentialRequest = new GetCredentialRequest.Builder()
                                    .addCredentialOption(getGoogleIdOptionNotAuthorized)
                                    .build();
                            credentialManager.getCredentialAsync(
                                    ActLogin.this,
                                    getCredentialRequest,
                                    new CancellationSignal(),
                                    mExecutor,
                                    new CredentialManagerCallback<>() {
                                        @Override
                                        public void onResult(GetCredentialResponse result) {
                                            Credential credential = result.getCredential();
                                            if (credential instanceof CustomCredential) {
                                                GoogleIdTokenCredential mGoogleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
                                                gvSystem.App_GoogleSignInToken = mGoogleIdTokenCredential.getIdToken();
                                                gvSystem.App_GoogleSignInAccount.setEmail(mGoogleIdTokenCredential.getEmail());
                                                gvSystem.App_GoogleSignInAccount.setDisplayName(mGoogleIdTokenCredential.getDisplayName());
                                                gvSystem.App_GoogleSignInAccount.setFamilyName(mGoogleIdTokenCredential.getFamilyName());
                                                gvSystem.App_GoogleSignInAccount.setGivenName(mGoogleIdTokenCredential.getGivenName());
                                                gvSystem.App_GoogleSignInAccount.setProfilePictureUri(mGoogleIdTokenCredential.getProfilePictureUri());
                                                gvSystem.setApp_GoogleAccount(mGoogleIdTokenCredential.getEmail());
                                                Do_DangNhap_Google();
                                            } else {
                                                Do_UnlockedUI();
                                                Message.ShowInfor(ActLogin.this, getString(R.string.Com_Message_LoiTuChoiDangNhapGoogle), (dialogInterface, i) -> dialogInterface.dismiss());
                                            }
                                        }

                                        @Override
                                        public void onError(@NonNull GetCredentialException e2) {
                                            if (e2 instanceof GetCredentialCancellationException) {
                                                Do_UnlockedUI();
                                                Message.ShowInfor(ActLogin.this, getString(R.string.Com_Message_LoiTuChoiDangNhapGoogle), (dialogInterface, i) -> dialogInterface.dismiss());
                                            } else {
                                                Do_UnlockedUI();
                                                Message.ShowInfor(ActLogin.this, getString(R.string.Com_Message_LoiTuChoiDangNhapGoogle), (dialogInterface, i) -> dialogInterface.dismiss());
                                            }
                                        }
                                    }
                            );
                        }
                    }
                }
        );
    }

    private void Do_DangNhap_Google() {
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> Authen.DangNhap_Google(20, new APIService.DoAPI_Authen_Result() {
            @Override
            public void GetDataOK() {
                Do_ShowMain();
            }

            @Override
            public void GetDataFailed(Throwable t) {
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> Authen.DangNhap_Google(40, new APIService.DoAPI_Authen_Result() {
                    @Override
                    public void GetDataOK() {
                        Do_ShowMain();
                    }

                    @Override
                    public void GetDataFailed(Throwable t) {
                        Do_UnlockedUI();
                        Message.ShowInfor(ActLogin.this, t.getMessage(), (dialogInterface, i) -> dialogInterface.dismiss());
                    }
                }), 2000);
            }
        }), 100);
    }

    private void Do_ShowMain() {
        Intent intent = null;
        if (gvSystem.App_FunctionConfigProvider != null) {
            intent = gvSystem.App_FunctionConfigProvider.getMainActivityIntent(this);
        }

        if (intent != null) {
            startActivity(intent);
        }

        finish();
    }

    private boolean DataIsOK() {
        boolean mReturn = true;

        resetErrors();

        if (ActLogin_TextInputEditText_Account.getText() == null ||
                ActLogin_TextInputEditText_Account.getText().toString().trim().isEmpty()) {
            ActLogin_TextInputLayout_Account.setErrorEnabled(true);
            ActLogin_TextInputLayout_Account.setError(getText(R.string.Com_Message_CheckDataNotEmpty));
            mReturn = false;
        }

        if (ActLogin_TextInputEditText_Password.getText() == null ||
                ActLogin_TextInputEditText_Password.getText().toString().trim().isEmpty()) {
            ActLogin_TextInputLayout_Password.setErrorEnabled(true);
            ActLogin_TextInputLayout_Password.setError(getText(R.string.Com_Message_CheckDataNotEmpty));
            mReturn = false;
        }

        return mReturn;
    }

    private void resetErrors() {
        ActLogin_TextInputLayout_Account.setError(null);
        ActLogin_TextInputLayout_Account.setErrorEnabled(false);

        ActLogin_TextInputLayout_Password.setError(null);
        ActLogin_TextInputLayout_Password.setErrorEnabled(false);
    }

}