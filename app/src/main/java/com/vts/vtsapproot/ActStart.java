package com.vts.vtsapproot;

import static com.vts.vtsapproot.Tools.gvSystem.App_FunctionConfigProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.vts.vtsapproot.API.APIService;
import com.vts.vtsapproot.API.Processes.Authen;
import com.vts.vtsapproot.Tools.ActBase;
import com.vts.vtsapproot.Tools.gvSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ActStart
        extends ActBase {

    boolean _IsExitApp = false;

    MaterialTextView ActStart_MaterialTextView_ProcessInfor;
    private String _NotiRemoteDB = null;
    private String _NotiMsgType = null;

    // Launcher mới hỗ trợ xin NHIỀU quyền cùng lúc
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                ProceedAfterPermissions();
            });

    public ActStart() {
        super();
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent == null) return;
        _NotiRemoteDB = intent.getStringExtra("EXTRA_REMOTE_DB");
        _NotiMsgType = intent.getStringExtra("EXTRA_MSG_TYPE");
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleIntent(getIntent());
        FirebaseApp.initializeApp(this);
        _IsExitApp = getIntent().getBooleanExtra("IsExitApp", false);
        if (!_IsExitApp) {
            if (gvSystem.App_NhanThongBao){
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Log.e("FCM_Service", "Lấy token thất bại", task.getException());
                                gvSystem.setApp_ClientToken(null);
                            } else {
                                String token = task.getResult();
                                Log.d("FCM_Service", "Token hiện tại: " + token);
                                gvSystem.setApp_ClientToken(token);
                            }
                        });
            }


            setContentView(R.layout.lay_base_actstart);

            ActStart_MaterialTextView_ProcessInfor = findViewById(R.id.ActStart_MaterialTextView_ProcessInfor);

            gvSystem.InitDataLocalDB(getApplicationContext());

            ImageView ActStart_ImageView_Logo = findViewById(R.id.ActStart_ImageView_Logo);
            ShimmerFrameLayout ActStart_ShimmerFrameLayout = findViewById(R.id.ActStart_ShimmerFrameLayout);
            ActStart_ImageView_Logo.post(() -> {
                Drawable drawable = ActStart_ImageView_Logo.getDrawable();
                if (drawable instanceof AnimatedVectorDrawable avd) {
                    avd.registerAnimationCallback(new Animatable2.AnimationCallback() {
                        @Override
                        public void onAnimationEnd(Drawable drawable) {
                            super.onAnimationEnd(drawable);
                            ActStart_ShimmerFrameLayout.startShimmer();
                            new Handler(Looper.getMainLooper()).postDelayed(() -> StartApp(), 1000);
                        }
                    });
                    avd.start();
                }
            });
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                finish();
                System.exit(0);
            }, 500);
        }
    }

    private void StartApp() {
        List<String> requiredPermissions = new ArrayList<>();

        // 1. Luôn yêu cầu quyền Camera
        if (gvSystem.App_Camera)
            requiredPermissions.add(Manifest.permission.CAMERA);

        if (gvSystem.App_NhanThongBao)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requiredPermissions.add(Manifest.permission.POST_NOTIFICATIONS);
            }

        // Kiểm tra xem có quyền nào chưa được cấp không
        List<String> missingPermissions = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }

        if (missingPermissions.isEmpty()) {
            // Đã đủ quyền -> Đi tiếp
            ProceedAfterPermissions();
        } else {
            // Còn thiếu quyền -> Bật hộp thoại yêu cầu
            requestPermissionsLauncher.launch(missingPermissions.toArray(new String[0]));
        }
    }

    // Tách logic chạy tiếp app sau khi đã đủ quyền ra một hàm riêng cho gọn
    private void ProceedAfterPermissions() {
        if (gvSystem.getApp_UsingAppProtect()
                && (gvSystem.getApp_CurrentPasscode() != null && !Objects.equals(gvSystem.getApp_CurrentPasscode(), ""))) {
            startActivity(new Intent(this, ActPasscode.class));
            finish();
        } else {
            Do_RefreshToken();
        }
    }

    private void Do_RefreshToken() {
        ActStart_MaterialTextView_ProcessInfor.setText(String.format(getString(R.string.Com_Message_AskForAccessPermission), ""));
        if (gvSystem.getApp_RefreshToken() != null && !Objects.equals(gvSystem.getApp_RefreshToken(), "")) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> Authen.RefreshToken(10, new APIService.DoAPI_Authen_Result() {
                @Override
                public void GetDataOK() {
                    Do_ShowMain();
                }

                @Override
                public void GetDataFailed(Throwable t) {
                    ActStart_MaterialTextView_ProcessInfor.setText(String.format(getString(R.string.Com_Message_AskForAccessPermission), " (2)"));
                    new Handler(Looper.getMainLooper()).postDelayed(() -> Authen.RefreshToken(20, new APIService.DoAPI_Authen_Result() {
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
        ActStart_MaterialTextView_ProcessInfor.setText(String.format(getString(R.string.Com_Message_Logging), ""));
        new Handler(Looper.getMainLooper()).postDelayed(() -> Authen.DangNhap_UserPwd(20, gvSystem.getApp_CurrentAccount(), gvSystem.getApp_CurrentPassword(), new APIService.DoAPI_Authen_Result() {
            @Override
            public void GetDataOK() {
                Do_ShowMain();
            }

            @Override
            public void GetDataFailed(Throwable t) {
                ActStart_MaterialTextView_ProcessInfor.setText(String.format(getString(R.string.Com_Message_Logging), " (2)"));
                new Handler(Looper.getMainLooper()).postDelayed(() -> Authen.DangNhap_UserPwd(40, gvSystem.getApp_CurrentAccount(), gvSystem.getApp_CurrentPassword(), new APIService.DoAPI_Authen_Result() {
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
        }), 100);
    }

    private void Do_Start_GoogleAuthen() {
        ActStart_MaterialTextView_ProcessInfor.setText(String.format(getString(R.string.Com_Message_GoogleLogging), ""));

        Executor mExecutor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mExecutor = ActStart.this.getMainExecutor();
        } else {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }

        CredentialManager credentialManager = CredentialManager.create(ActStart.this);
        GetGoogleIdOption getGoogleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(gvSystem.App_ClientID)
                .setAutoSelectEnabled(true)
                .build();
        GetCredentialRequest getCredentialRequest = new GetCredentialRequest.Builder()
                .addCredentialOption(getGoogleIdOption)
                .build();
        credentialManager.getCredentialAsync(
                ActStart.this,
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
                            CredentialManager credentialManager = CredentialManager.create(ActStart.this);
                            GetGoogleIdOption getGoogleIdOptionNotAuthorized = new GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId(gvSystem.App_ClientID)
                                    .setAutoSelectEnabled(false)
                                    .build();
                            GetCredentialRequest getCredentialRequest = new GetCredentialRequest.Builder()
                                    .addCredentialOption(getGoogleIdOptionNotAuthorized)
                                    .build();
                            credentialManager.getCredentialAsync(
                                    ActStart.this,
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
        ActStart_MaterialTextView_ProcessInfor.setText(String.format(getString(R.string.Com_Message_Logging), ""));
        new Handler(Looper.getMainLooper()).postDelayed(() -> Authen.DangNhap_Google(20, new APIService.DoAPI_Authen_Result() {
            @Override
            public void GetDataOK() {
                Do_ShowMain();
            }

            @Override
            public void GetDataFailed(Throwable t) {
                ActStart_MaterialTextView_ProcessInfor.setText(String.format(getString(R.string.Com_Message_Logging), " (2)"));
                new Handler(Looper.getMainLooper()).postDelayed(() -> Authen.DangNhap_Google(40, new APIService.DoAPI_Authen_Result() {
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
        }), 100);
    }

    private void Do_ShowMain() {
        if (_NotiRemoteDB != null && !_NotiRemoteDB.isEmpty()) {
            // Logic xử lý notification nếu cần chuyển hướng ở đây
            gvSystem.App_IsFromNotification = true;
        }
        Intent intent = null;
        if (App_FunctionConfigProvider != null) {
            if (_NotiRemoteDB != null && !_NotiRemoteDB.isEmpty())
                intent = App_FunctionConfigProvider.getMainActivityIntent(this, _NotiRemoteDB);
            else
                intent = App_FunctionConfigProvider.getMainActivityIntent(this);
        }
        if (intent != null) {
            startActivity(intent);
        }
        finish();
    }

    private void Do_ShowLogin() {
        Intent intent = null;
        if (gvSystem.App_FunctionConfigProvider != null) {
            intent = gvSystem.App_FunctionConfigProvider.getLoginActivityIntent(this);
        }

        if (intent != null) {
            startActivity(intent);
        }
        finish();
    }
}