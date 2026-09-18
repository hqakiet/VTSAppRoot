package com.vts.vtsapproot;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import static com.vts.vtsapproot.Tools.gvSystem.App_FunctionConfigProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vts.vtsapproot.API.APIService;
import com.vts.vtsapproot.API.Processes.Authen;
import com.vts.vtsapproot.Tools.ActBase;
import com.vts.vtsapproot.Tools.Message;
import com.vts.vtsapproot.Tools.SingleClickListener;
import com.vts.vtsapproot.Tools.gvSystem;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ActPasscode
        extends ActBase {

    LinearLayout ActPassCode_LinearLayout_Data;

    TextInputLayout ActPassCode_TextInputLayout_Password;
    TextInputEditText ActPassCode_TextInputEditText_Password;

    MaterialButton ActPassCode_MaterialButton_Confirm;

    LinearLayout ActPassCode_LinearLayout_UnlockFinger;

    ShimmerFrameLayout ShimmerFrameLayout_Processing;

    boolean IsBiometricAvaliable;

    boolean _IsExitApp = false;

    public ActPasscode() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_base_actpasscode);

        BiometricManager mBiometricManager = BiometricManager.from(this);
        IsBiometricAvaliable = ((mBiometricManager.canAuthenticate(BIOMETRIC_STRONG | BIOMETRIC_WEAK | DEVICE_CREDENTIAL)) == BiometricManager.BIOMETRIC_SUCCESS);

        _IsExitApp = getIntent().getBooleanExtra("IsExitApp", false);

        ShimmerFrameLayout_Processing = findViewById(R.id.ShimmerFrameLayout_Processing);
        ShimmerFrameLayout_Processing.stopShimmer();
        ShimmerFrameLayout_Processing.setVisibility(View.GONE);

        initViews();

        if (_IsExitApp) {
            Toast.makeText(this, getString(R.string.Com_Message_ClickBackAgainToExit), Toast.LENGTH_SHORT).show();
        }

        ActPassCode_LinearLayout_Data = findViewById(R.id.ActPassCode_LinearLayout_Data);
        if (!gvSystem.getApp_TietKiemPin()) {
            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(ActPasscode.this, R.anim.layout_showup);
            ActPassCode_LinearLayout_Data.setLayoutAnimation(controller);
            ActPassCode_LinearLayout_Data.setVisibility(View.VISIBLE);
        } else {
            ActPassCode_LinearLayout_Data.setLayoutAnimation(null);
            ActPassCode_LinearLayout_Data.setVisibility(View.VISIBLE);
        }
        Do_UnlockedUI();
    }

    private void initViews() {
        ActPassCode_TextInputLayout_Password = findViewById(R.id.ActPassCode_TextInputLayout_Password);
        ActPassCode_TextInputEditText_Password = findViewById(R.id.ActPassCode_TextInputEditText_Password);
        ActPassCode_TextInputEditText_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ActPassCode_TextInputLayout_Password.setErrorEnabled(false);
                ActPassCode_TextInputLayout_Password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ActPassCode_MaterialButton_Confirm = findViewById(R.id.ActPassCode_MaterialButton_Confirm);
        ActPassCode_MaterialButton_Confirm.setOnClickListener(new SingleClickListener() {
            @Override
            public void safeSingleClick(View v) {
                if (DataIsOK()) {
                    Process_PassCode_Login();
                }
            }
        });

        ActPassCode_LinearLayout_UnlockFinger = findViewById(R.id.ActPassCode_LinearLayout_UnlockFinger);
        if (IsBiometricAvaliable && gvSystem.getApp_UsingFingerPrint()) {
            ActPassCode_LinearLayout_UnlockFinger.setVisibility(View.VISIBLE);
            ActPassCode_LinearLayout_UnlockFinger.setOnClickListener(new SingleClickListener() {
                @Override
                public void safeSingleClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        Process_Biometric_Login();
                    }
                }
            });
        } else
            ActPassCode_LinearLayout_UnlockFinger.setVisibility(View.GONE);

        LinearLayout_StatusBar = findViewById(R.id.LinearLayout_StatusBar);
        setupStatusBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (IsBiometricAvaliable && gvSystem.getApp_UsingFingerPrint() && !_IsExitApp) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Process_Biometric_Login();
            }
        }
    }

    private void Do_LockedUI() {
        ActPassCode_TextInputEditText_Password.setEnabled(false);

        ShimmerFrameLayout_Processing.setVisibility(View.VISIBLE);
        ShimmerFrameLayout_Processing.startShimmer();

        ActPassCode_MaterialButton_Confirm.setEnabled(false);
    }

    private void Do_UnlockedUI() {
        ActPassCode_TextInputEditText_Password.setEnabled(true);

        ShimmerFrameLayout_Processing.stopShimmer();
        ShimmerFrameLayout_Processing.setVisibility(View.GONE);

        ActPassCode_MaterialButton_Confirm.setEnabled(true);
    }

    private void Process_PassCode_Login() {
        Do_LockedUI();

        if (gvSystem.getApp_CurrentPasscode().equals(Objects.requireNonNull(ActPassCode_TextInputEditText_Password.getText()).toString().trim()))
            Do_RefreshToken();
        else {
            Do_UnlockedUI();
            Message.ShowInfor(this, getString(R.string.ActPasscode_Message_SaiMatKhauBaoVe));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void Process_Biometric_Login() {
        Do_LockedUI();

        BiometricPrompt my_BiometricPrompt = new BiometricPrompt(ActPasscode.this, getMainExecutor(), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Do_UnlockedUI();
                Toast.makeText(ActPasscode.this, errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Do_RefreshToken();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Do_UnlockedUI();
                Toast.makeText(ActPasscode.this, getString(R.string.Biometric_Message_Failed), Toast.LENGTH_SHORT).show();
            }
        });
        BiometricPrompt.PromptInfo my_PromptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(String.format(getString(R.string.Biometric_Title), getString(R.string.app_name)))
                .setNegativeButtonText(getString(R.string.Biometric_Caption_Cancel))
                .setConfirmationRequired(false)
                .setAllowedAuthenticators(BIOMETRIC_WEAK)
                .build();
        my_BiometricPrompt.authenticate(my_PromptInfo);
    }

    private void Do_RefreshToken() {
        if (gvSystem.getApp_RefreshToken() != null && !Objects.equals(gvSystem.getApp_RefreshToken(), "")) {
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> Authen.RefreshToken(10, new APIService.DoAPI_Authen_Result() {
                @Override
                public void GetDataOK() {
                    Do_ShowMain();
                }

                @Override
                public void GetDataFailed(Throwable t) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> Authen.RefreshToken(15, new APIService.DoAPI_Authen_Result() {
                        @Override
                        public void GetDataOK() {
                            Do_ShowMain();
                        }

                        @Override
                        public void GetDataFailed(Throwable t) {
                            Do_DangNhap();
                        }
                    }), 2000);
                }
            }), 100);

        } else {
            Do_DangNhap();
        }
    }

    private void Do_DangNhap() {
        if (gvSystem.getApp_BasicAuthenLogIn()) {
            Do_DangNhap_UserPwd();
        } else if (gvSystem.getApp_GoogleAccount() != null && !gvSystem.getApp_GoogleAccount().isEmpty()) {
            Do_Start_GoogleAuthen();
        } else {
            Do_ShowLogin();
        }
    }

    private void Do_DangNhap_UserPwd() {
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> Authen.DangNhap_UserPwd(20, gvSystem.getApp_CurrentAccount(), gvSystem.getApp_CurrentPassword(), new APIService.DoAPI_Authen_Result() {
            @Override
            public void GetDataOK() {
                Do_ShowMain();
            }

            @Override
            public void GetDataFailed(Throwable t) {
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> Authen.DangNhap_UserPwd(40, gvSystem.getApp_CurrentAccount(), gvSystem.getApp_CurrentPassword(), new APIService.DoAPI_Authen_Result() {
                    @Override
                    public void GetDataOK() {
                        Do_ShowMain();
                    }

                    @Override
                    public void GetDataFailed(Throwable t) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> Authen.DangNhap_UserPwd(60, gvSystem.getApp_CurrentAccount(), gvSystem.getApp_CurrentPassword(), new APIService.DoAPI_Authen_Result() {
                            @Override
                            public void GetDataOK() {
                                Do_ShowMain();
                            }

                            @Override
                            public void GetDataFailed(Throwable t) {
                                Do_ShowLogin();
                            }
                        }), 2000);
                    }
                }), 2000);
            }
        }), 100);
    }

    private void Do_Start_GoogleAuthen() {
        Executor mExecutor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mExecutor = ActPasscode.this.getMainExecutor();
        } else {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }

        CredentialManager credentialManager = CredentialManager.create(ActPasscode.this);
        GetGoogleIdOption getGoogleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(gvSystem.App_ClientID)
                .setAutoSelectEnabled(true)
                .build();
        GetCredentialRequest getCredentialRequest = new GetCredentialRequest.Builder()
                .addCredentialOption(getGoogleIdOption)
                .build();
        credentialManager.getCredentialAsync(
                ActPasscode.this,
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
                            Do_ShowLogin();
                        }
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e1) {
                        if (e1 instanceof GetCredentialCancellationException) {
                            Do_ShowLogin();
                        } else {
                            CredentialManager credentialManager = CredentialManager.create(ActPasscode.this);
                            GetGoogleIdOption getGoogleIdOptionNotAuthorized = new GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId(gvSystem.App_ClientID)
                                    .setAutoSelectEnabled(false)
                                    .build();
                            GetCredentialRequest getCredentialRequest = new GetCredentialRequest.Builder()
                                    .addCredentialOption(getGoogleIdOptionNotAuthorized)
                                    .build();
                            credentialManager.getCredentialAsync(
                                    ActPasscode.this,
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
                                                Do_ShowLogin();
                                            }
                                        }

                                        @Override
                                        public void onError(@NonNull GetCredentialException e2) {
                                            Do_ShowLogin();
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
                        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> Authen.DangNhap_Google(60, new APIService.DoAPI_Authen_Result() {
                            @Override
                            public void GetDataOK() {
                                Do_ShowMain();
                            }

                            @Override
                            public void GetDataFailed(Throwable t) {
                                Do_ShowLogin();
                            }
                        }), 2000);
                    }
                }), 2000);
            }
        }), 100);
    }

    private void Do_ShowMain() {
//        startActivity(new Intent(ActPasscode.this, ActMain.class));
//        finish();

        Intent intent = null;
        if (App_FunctionConfigProvider != null) {
            intent = App_FunctionConfigProvider.getMainActivityIntent(this);
        }
        if (intent != null) {
            startActivity(intent);
        }
        finish();
    }

    private void Do_ShowLogin() {
//        startActivity(new Intent(ActPasscode.this, ActLogin.class));
//        finish();

        Intent intent = null;
        if (App_FunctionConfigProvider != null) {
            intent = App_FunctionConfigProvider.getLoginActivityIntent(this);
        }
        if (intent != null) {
            startActivity(intent);
        }
        finish();
    }

    private boolean DataIsOK() {
        boolean mReturn = true;

        resetErrors();

        if (ActPassCode_TextInputEditText_Password.getText() == null ||
                ActPassCode_TextInputEditText_Password.getText().toString().trim().isEmpty()) {
            ActPassCode_TextInputLayout_Password.setErrorEnabled(true);
            ActPassCode_TextInputLayout_Password.setError(getText(R.string.Com_Message_CheckDataNotEmpty));
            mReturn = false;
        } else {
            if (Objects.requireNonNull(ActPassCode_TextInputEditText_Password.getText()).toString().trim().length() != 6) {
                ActPassCode_TextInputLayout_Password.setErrorEnabled(true);
                ActPassCode_TextInputLayout_Password.setError(getText(R.string.Com_Message_CheckDataNotOK));
                mReturn = false;
            }
        }

        return mReturn;
    }

    private void resetErrors() {
        ActPassCode_TextInputLayout_Password.setError(null);
        ActPassCode_TextInputLayout_Password.setErrorEnabled(false);
    }

}