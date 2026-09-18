package com.vts.vtsapproot.Tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.widget.PopupMenu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vts.vtsapproot.API.APIService;
import com.vts.vtsapproot.API.Response.ResModDetail_DanhSachChucNang;
import com.vts.vtsapproot.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class gvSystem {
    public static String App_BaseUrl0 = "";
    public static String App_BaseUrl = "";
    public static String App_ClientID = "";
    public static String App_LocalDBName = "app_default.db";
    public static boolean App_IsFromNotification = false;

    public static boolean App_AutoProcessSystembars = true;
    public static boolean App_SystembarsIsDark = true;
    public static int App_AutoProcessSystembarsLightColor = R.color.md_theme_primaryContainer;
    public static int App_AutoProcessSystembarsDarkColor = R.color.md_theme_primaryContainer;

    public static boolean App_ChucNangAdmin = false;

    public static boolean App_NhanThongBao = false;

    public static int App_LocalDBVersion = 1;

    public static String App_GoogleSignInToken = "";

    public static String App_DangNhapFunction = "auth/DangNhap";
    public static String App_RefreshTokenFunction = "auth/RefreshToken";

    public static LocalDB App_LocalDB = null;
    public static int App_LimitListBigSize = 500;
    public static int App_sysval_SOLESOLUONG = 0;
    public static int App_sysval_SOLEDONGIA = 0;
    public static int App_sysval_SOLESOTIEN = 0;
    public static int App_sysval_SOLETYLE = 0;

    public static FunctionConfigProvider App_FunctionConfigProvider = null;

    public static void init(VtsConfig config) {
        App_BaseUrl0 = config.getBaseUrl0();
        App_BaseUrl = config.getBaseUrl();
        App_ClientID = config.getApp_ClientID();
        App_LocalDBName = config.getApp_LocalDBName();

        App_IsFromNotification = config.isApp_IsFromNotification();

        App_AutoProcessSystembars= config.isApp_AutoProcessSystembars();
        App_SystembarsIsDark = config.isApp_SystembarsIsDark();

        App_ChucNangAdmin= config.isApp_ChucNangAdmin();

        App_NhanThongBao = config.isApp_NhanThongBao();

        App_LocalDBVersion = config.getApp_LocalDBVersion();

        App_LimitListBigSize = config.getApp_LimitListBigSize();
        App_sysval_SOLESOLUONG = config.getApp_sysval_SOLESOLUONG();
        App_sysval_SOLEDONGIA = config.getApp_sysval_SOLEDONGIA();
        App_sysval_SOLESOTIEN = config.getApp_sysval_SOLESOTIEN();
        App_sysval_SOLETYLE = config.getApp_sysval_SOLETYLE();

        if (config.getFunctionConfigProvider() != null) {
            App_FunctionConfigProvider = config.getFunctionConfigProvider();
        }
    }

    public static AppDatas App_RuntimeVals = new AppDatas();
    public static MyGoogleSignInAccount App_GoogleSignInAccount = new MyGoogleSignInAccount();
    private static List<ResModDetail_DanhSachChucNang> _DanhSachChucNangs = new ArrayList<>();
    private static List<ResModDetail_DanhSachChucNang> _FullDanhSachChucNangs = new ArrayList<>();

    public static List<ResModDetail_DanhSachChucNang> App_DanhSachChucNang() {
        return _DanhSachChucNangs;
    }

    public static List<ResModDetail_DanhSachChucNang> App_FullDanhSachChucNang() {
        return _FullDanhSachChucNangs;
    }

    public static void App_DanhSachChucNang(List<ResModDetail_DanhSachChucNang> pResModDetails) {
        App_ChucNangAdmin = false;
        gvSystem._DanhSachChucNangs = new ArrayList<>();
        gvSystem._FullDanhSachChucNangs = new ArrayList<>(pResModDetails);

        // Nếu project con có truyền custom logic thì ưu tiên chạy logic của project con
        if (App_FunctionConfigProvider != null) {
            App_FunctionConfigProvider.onProcessDanhSachChucNang(
                    pResModDetails,
                    gvSystem._DanhSachChucNangs,
                    gvSystem._FullDanhSachChucNangs
            );
        } else {
            // Logic mặc định chung (nếu cần)
            for (ResModDetail_DanhSachChucNang item : pResModDetails) {
                item.setBGColorResId(R.color.md_theme_primaryContainer);
                item.setTextColorResId(R.color.md_theme_onPrimary);
                item.setIconResId(R.drawable.ic_home);
                gvSystem._DanhSachChucNangs.add(item);
            }
        }
    }

    public static void InitDataLocalDB(Context pContext) {
        App_LocalDB = new LocalDB(pContext, App_LocalDBName, null, App_LocalDBVersion);
    }

    public static String getApp_AccessToken() {
        return App_LocalDB.Get_GIATRITHONGSO_STRING("AccessToken", "");
    }

    public static void setApp_AccessToken(String pValue) {
        App_LocalDB.Set_GIATRITHONGSO_STRING("AccessToken", pValue);
    }

    public static String getApp_ClientToken() {
        return App_LocalDB.Get_GIATRITHONGSO_STRING("ClientToken", "");
    }

    public static void setApp_ClientToken(String pValue) {
        App_LocalDB.Set_GIATRITHONGSO_STRING("ClientToken", pValue);
    }

    public static String getApp_RefreshToken() {
        return App_LocalDB.Get_GIATRITHONGSO_STRING("RefreshToken", "");
    }

    public static void setApp_RefreshToken(String pValue) {
        App_LocalDB.Set_GIATRITHONGSO_STRING("RefreshToken", pValue);
    }

    public static boolean getApp_IsReleaseTest() {
        return (App_LocalDB.Get_GIATRITHONGSO_NUMBER("IsReleaseTest") != 0);
    }

    public static void setApp_IsReleaseTest(boolean pValue) {
        if (pValue)
            App_LocalDB.Set_GIATRITHONGSO_NUMBER("IsReleaseTest", 1);
        else
            App_LocalDB.Set_GIATRITHONGSO_NUMBER("IsReleaseTest", 0);
    }

    public static String getApp_GoogleAccount() {
        return App_LocalDB.Get_GIATRITHONGSO_STRING("GoogleAccount", "");
    }

    public static void setApp_GoogleAccount(String pValue) {
        App_LocalDB.Set_GIATRITHONGSO_STRING("GoogleAccount", pValue);
    }

    public static boolean getApp_UsingFingerPrint() {
        return (App_LocalDB.Get_GIATRITHONGSO_NUMBER("USING_FINGERPRINT") != 0);
    }

    public static void setApp_UsingFingerPrint(boolean pValue) {
        if (pValue)
            App_LocalDB.Set_GIATRITHONGSO_NUMBER("USING_FINGERPRINT", 1);
        else
            App_LocalDB.Set_GIATRITHONGSO_NUMBER("USING_FINGERPRINT", 0);
    }

    public static boolean getApp_UsingAppProtect() {
        return (App_LocalDB.Get_GIATRITHONGSO_NUMBER("USING_APPPROTECT") != 0);
    }

    public static void setApp_UsingAppProtect(boolean pValue) {
        if (pValue)
            App_LocalDB.Set_GIATRITHONGSO_NUMBER("USING_APPPROTECT", 1);
        else
            App_LocalDB.Set_GIATRITHONGSO_NUMBER("USING_APPPROTECT", 0);
    }

    public static boolean getApp_TietKiemPin() {
        return (App_LocalDB.Get_GIATRITHONGSO_NUMBER("USING_TietKiemPin") != 0);
    }

    public static void setApp_TietKiemPin(boolean pValue) {
        if (pValue)
            App_LocalDB.Set_GIATRITHONGSO_NUMBER("USING_TietKiemPin", 1);
        else
            App_LocalDB.Set_GIATRITHONGSO_NUMBER("USING_TietKiemPin", 0);
    }

    public static boolean getApp_BasicAuthenLogIn() {
        return (App_LocalDB.Get_GIATRITHONGSO_NUMBER("USING_APPBASICAUTHEN") != 0);
    }

    public static void setApp_BasicAuthenLogIn(boolean pValue) {
        if (pValue)
            App_LocalDB.Set_GIATRITHONGSO_NUMBER("USING_APPBASICAUTHEN", 1);
        else
            App_LocalDB.Set_GIATRITHONGSO_NUMBER("USING_APPBASICAUTHEN", 0);
    }

    public static String getApp_CurrentPasscode() {
        return (App_LocalDB.Get_GIATRITHONGSO_STRING("CURRENT_PASSCODE"));
    }

    public static void setApp_CurrentPasscode(String pValue) {
        App_LocalDB.Set_GIATRITHONGSO_STRING("CURRENT_PASSCODE", pValue);
    }

    public static String getApp_CurrentAccount() {
        return (App_LocalDB.Get_GIATRITHONGSO_STRING("CURRENT_ACCOUNT"));
    }

    public static void setApp_CurrentAccount(String pValue) {
        App_LocalDB.Set_GIATRITHONGSO_STRING("CURRENT_ACCOUNT", pValue);
    }

    public static String getApp_CurrentPassword() {
        return (App_LocalDB.Get_GIATRITHONGSO_STRING("CURRENT_PASSWORD"));
    }

    public static void setApp_CurrentPassword(String pValue) {
        App_LocalDB.Set_GIATRITHONGSO_STRING("CURRENT_PASSWORD", pValue);
    }

    public static int getApp_ConnectTimeOut() {
        return 60;
    }

    public static int getApp_ReadTimeOut() {
        return 60;
    }

    public static int getApp_WriteTimeOut() {
        return 60;
    }

    /// /////////////////////////////////////////////////////////////////////////////////////

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static String FormatNumber(double pValue) {
        DecimalFormat mDecimalFormat = new DecimalFormat("#,##0");
        return mDecimalFormat.format(pValue);
    }

    public static String FormatNumber(double pValue, int pLen) {
        if (pLen == 0) {
            return FormatNumber(pValue);
        } else {
            StringBuilder mPattern = new StringBuilder("#,##0");
            if (pLen > 0) {
                mPattern.append(".");
                for (int i = 0; i < pLen; i++) {
                    mPattern.append("0");
                }
            }
            DecimalFormat mDecimalFormat = new DecimalFormat(mPattern.toString());
            mDecimalFormat.setDecimalSeparatorAlwaysShown(true);
            return mDecimalFormat.format(pValue);
        }
    }

    // Làm tròn x số thập phân
    public static double RoundXDec(double pInput, int x) {
        double mCal = Math.pow(10, x);
        return (double) Math.round(pInput * mCal) / mCal;
    }

    // Làm tròn 1 số thập phân
    public static double Round1Dec(double pInput) {
        return (double) Math.round(pInput * 10) / 10;
    }

    // Làm tròn 2 số thập phân
    public static double Round2Dec(double pInput) {
        return (double) Math.round(pInput * 100) / 100;
    }

    // Làm tròn 3 số thập phân
    public static double Round3Dec(double pInput) {
        return (double) Math.round(pInput * 1000) / 1000;
    }

    // Làm tròn 4 số thập phân
    public static double Round4Dec(double pInput) {
        return (double) Math.round(pInput * 10000) / 10000;
    }

    // Làm tròn 5 số thập phân
    public static double Round5Dec(double pInput) {
        return (double) Math.round(pInput * 100000) / 100000;
    }

    // Làm tròn 6 số thập phân
    public static double Round6Dec(double pInput) {
        return (double) Math.round(pInput * 1000000) / 1000000;
    }

    public static String getApp_Path(Context pContext) {
        return Objects.requireNonNull(pContext.getExternalFilesDir(null)).getAbsolutePath();
    }

    public static String loadHtmlWithAppName(Context context, String assetFileName) {
        try {
            InputStream is = context.getAssets().open(assetFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String htmlContent = new String(buffer, StandardCharsets.UTF_8);
            String appName = context.getString(R.string.app_name);
            htmlContent = htmlContent.replace("{{APP_NAME}}", appName);

            return htmlContent;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Mã hóa
    @SuppressLint("NewApi")
    public static String StringToBase64(String pInput) {
        if (pInput == null || pInput.isEmpty())
            return "";
        else {
            byte[] data = (pInput).getBytes(StandardCharsets.UTF_8);
            return Base64.encodeToString(data, Base64.NO_WRAP);
        }
    }

    public static String Base64ToString(String pInput) {
        if (pInput == null || pInput.isEmpty())
            return "";
        else {
            byte[] data = Base64.decode(pInput, Base64.NO_WRAP);
            return new String(data, StandardCharsets.UTF_8);
        }
    }

    public static Date getCurrDate() {
        return Calendar.getInstance().getTime();
    }

    public static long getMinDateByLong() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.add(Calendar.YEAR, -100);
        return mCalendar.getTimeInMillis();
    }

    public static Date getMinDate() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mCalendar.add(Calendar.YEAR, -100);
        return mCalendar.getTime();
    }

    public static long getMaxDateByLong() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.add(Calendar.YEAR, 100);
        return mCalendar.getTimeInMillis();
    }

    public static Date getMaxDate() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.add(Calendar.YEAR, 100);
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        return mCalendar.getTime();
    }

    public static String getStrCurrDate_WithFormat(String pStringFormat) {
        return new SimpleDateFormat(pStringFormat, Locale.getDefault()).format(getCurrDate());
    }

    public static String getStrDate_WithFormat(Date pDateValue, String pStringFormat) {
        return new SimpleDateFormat(pStringFormat, Locale.getDefault()).format(pDateValue);
    }

    public static String getStrDate_WithFormat(LocalDate pDateValue, String pStringFormat) {
        return new SimpleDateFormat(pStringFormat, Locale.getDefault()).format(pDateValue);
    }

    public static Date getDateBeginMonth() {
        Calendar mCalendar = Calendar.getInstance();

        mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.HOUR, 0);

        mCalendar.set(Calendar.DATE, 1);

        return mCalendar.getTime();
    }

    public static Date getDateBeginMonth(Date pDateValue) {
        Calendar mCalendar = Calendar.getInstance();

        mCalendar.setTime(pDateValue);

        mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.HOUR, 0);

        mCalendar.set(Calendar.DATE, 1);

        return mCalendar.getTime();
    }

    public static Date getDateEndMonth(Date pDateValue) {
        Calendar mCalendar = Calendar.getInstance();

        mCalendar.setTime(pDateValue);

        mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.HOUR, 0);
        mCalendar.set(Calendar.DATE, 1);

        mCalendar.add(Calendar.MONTH, 1);
        mCalendar.add(Calendar.DATE, -1);

        return mCalendar.getTime();
    }

    public static Date getDateBeginOfWeek() {
        Calendar mCalendar = Calendar.getInstance();

        // Đưa về Thứ Hai
        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Reset thời gian về 00:00:00.000
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);

        return mCalendar.getTime();
    }

    public static Date getDateEndOfWeek() {
        Calendar mCalendar = Calendar.getInstance();

        // Đưa về Thứ Hai trước, sau đó cộng thêm 6 ngày để ra Chủ Nhật
        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        mCalendar.add(Calendar.DATE, 6);

        // Reset thời gian về cuối ngày 23:59:59.999
        mCalendar.set(Calendar.HOUR_OF_DAY, 23);
        mCalendar.set(Calendar.MINUTE, 59);
        mCalendar.set(Calendar.SECOND, 59);
        mCalendar.set(Calendar.MILLISECOND, 999);

        return mCalendar.getTime();
    }

    public static Date getDateBeginOfWeek(Date pDateValue) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(pDateValue);

        // Đưa về Thứ Hai
        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Reset thời gian về 00:00:00.000
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);

        return mCalendar.getTime();
    }

    public static Date getDateEndOfWeek(Date pDateValue) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(pDateValue);

        // Đưa về Thứ Hai trước, sau đó cộng thêm 6 ngày để ra Chủ Nhật
        mCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        mCalendar.add(Calendar.DATE, 6);

        // Reset thời gian về cuối ngày 23:59:59.999
        mCalendar.set(Calendar.HOUR_OF_DAY, 23);
        mCalendar.set(Calendar.MINUTE, 59);
        mCalendar.set(Calendar.SECOND, 59);
        mCalendar.set(Calendar.MILLISECOND, 999);

        return mCalendar.getTime();
    }

    public static Date getDateBeginMonth(int pYearValue) {
        Calendar mCalendar = Calendar.getInstance();

        mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.HOUR, 0);

        mCalendar.set(Calendar.DATE, 1);
        mCalendar.set(Calendar.YEAR, pYearValue);

        return mCalendar.getTime();
    }

    public static Date getDateBeginYear(int pYearValue) {
        Calendar mCalendar = Calendar.getInstance();

        mCalendar.set(Calendar.MILLISECOND, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.HOUR, 0);

        mCalendar.set(Calendar.DATE, 1);
        mCalendar.set(Calendar.MONTH, 1);
        mCalendar.set(Calendar.YEAR, pYearValue);

        return mCalendar.getTime();
    }

    public static Date getDateEndYear(int pValue) {
        Calendar mCalendar = Calendar.getInstance();

        mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.HOUR, 0);

        mCalendar.set(Calendar.DATE, 31);
        mCalendar.set(Calendar.MONTH, 11);
        mCalendar.set(Calendar.YEAR, pValue);

        return mCalendar.getTime();
    }

    public static Date getDateBeginMonth(int pYearValue, int pMonthValue) {
        Calendar mCalendar = Calendar.getInstance();

        mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.HOUR, 0);

        mCalendar.set(Calendar.DATE, 1);
        mCalendar.set(Calendar.MONTH, pMonthValue - 1);
        mCalendar.set(Calendar.YEAR, pYearValue);

        return mCalendar.getTime();
    }

    public static Date getDateEndMonth(int pYearValue, int pMonthValue) {
        Calendar mCalendar = Calendar.getInstance();

        mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.HOUR, 0);

        mCalendar.set(Calendar.DATE, 1);
        mCalendar.set(Calendar.MONTH, pMonthValue - 1);
        mCalendar.set(Calendar.YEAR, pYearValue);

        mCalendar.add(Calendar.MONTH, 1);
        mCalendar.add(Calendar.DATE, -1);

        return mCalendar.getTime();
    }

    public static int getDAYFromDate(Date pDateValue) {
        Calendar mCalendar = Calendar.getInstance();

        mCalendar.setTime(pDateValue);

        mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.HOUR, 0);

        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMONTHFromDate(Date pDateValue) {
        Calendar mCalendar = Calendar.getInstance();

        mCalendar.setTime(pDateValue);

        mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.HOUR, 0);

        return mCalendar.get(Calendar.MONTH) + 1;
    }

    public static int getYEARFromDate(Date pDateValue) {
        Calendar mCalendar = Calendar.getInstance();

        mCalendar.setTime(pDateValue);

        mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.HOUR, 0);

        return mCalendar.get(Calendar.YEAR);
    }

    public static Date getDate(int pYearValue, int mMonthValue, int pDayValue) {
        Calendar mCalendar = Calendar.getInstance();

        mCalendar.set(Calendar.HOUR_OF_DAY, Calendar.AM);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.HOUR, 0);

        mCalendar.set(Calendar.DATE, pDayValue);
        mCalendar.set(Calendar.MONTH, mMonthValue - 1);
        mCalendar.set(Calendar.YEAR, pYearValue);

        return mCalendar.getTime();
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getDate(String pStringDateValue, String mFormat) throws ParseException {
        return new SimpleDateFormat(mFormat).parse(pStringDateValue);
    }

    public static boolean isNumeric(String pValue) {
        if (pValue == null || pValue.isEmpty()) return false;
        try {
            Double.parseDouble(pValue);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) return capitalize(model);
        else return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) return s;
        else return Character.toUpperCase(first) + s.substring(1);
    }

    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] mFields = popupMenu.getClass().getDeclaredFields();
            for (Field field : mFields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    assert menuPopupHelper != null;
                    Class<?> popupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method mMethods = popupHelper.getMethod("setForceShowIcon", boolean.class);
                    mMethods.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable ignored) {
        }
    }

    public static void ResetLayout(Context pContext) {
        ((Activity) pContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        if ((pContext.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                < Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Configuration mConfiguration = pContext.getResources().getConfiguration();
            mConfiguration.fontScale = (float) 1.0;
            pContext.getResources().getDisplayMetrics();
            pContext.createConfigurationContext(mConfiguration);
        }
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) return null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            Log.d("gvSystem.bitmapToBase64", Objects.requireNonNull(e.getMessage()));
            return null;
        }
    }

    public static Bitmap base64ToBitmap(String base64) {
        if (base64 == null || base64.isEmpty()) return null;
        try {
            // Loại bỏ prefix nếu có (ví dụ: data:image/png;base64,)
            if (base64.contains(",")) {
                base64 = base64.substring(base64.indexOf(",") + 1);
            }
            byte[] decodedString = Base64.decode(base64.trim(), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            Log.d("gvSystem.base64ToBitmap", Objects.requireNonNull(e.getMessage()));
            return null;
        }
    }

    public static APIService GetAPIService(long pTimeOut) {
        OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder().addInterceptor(chain ->
                {
                    Request originalRequest = chain.request();
                    Request.Builder builder = originalRequest.newBuilder();
                    builder.header("Authorization", "Bearer " + gvSystem.getApp_AccessToken());
                    Request newRequest = builder.build();
                    return chain.proceed(newRequest);
                })
                .callTimeout(pTimeOut, TimeUnit.SECONDS)
                .connectTimeout(pTimeOut, TimeUnit.SECONDS)
                .readTimeout(pTimeOut, TimeUnit.SECONDS)
                .writeTimeout(pTimeOut, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new MultiDateFormatAdapter()) // ĐẶT Ở ĐÂY
                .serializeNulls()
                .create();

        return new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(gvSystem.App_BaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(APIService.class);
    }

    public static APIService GetAPIService() {
        OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder().addInterceptor(chain ->
                {
                    Request originalRequest = chain.request();
                    Request.Builder builder = originalRequest.newBuilder();
                    builder.header("Authorization", "Bearer " + gvSystem.getApp_AccessToken());
                    Request newRequest = builder.build();
                    return chain.proceed(newRequest);
                })
                .callTimeout(gvSystem.getApp_ConnectTimeOut(), TimeUnit.MILLISECONDS)
                .connectTimeout(gvSystem.getApp_ConnectTimeOut(), TimeUnit.MILLISECONDS)
                .readTimeout(gvSystem.getApp_ReadTimeOut(), TimeUnit.MILLISECONDS)
                .writeTimeout(gvSystem.getApp_WriteTimeOut(), TimeUnit.MILLISECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new MultiDateFormatAdapter()) // ĐẶT Ở ĐÂY
                .serializeNulls()
                .create();

        return new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(gvSystem.App_BaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(APIService.class);
    }

    public static APIService GetAPIServiceNoAuthen() {
        OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder()
                .callTimeout(gvSystem.getApp_ConnectTimeOut(), TimeUnit.MILLISECONDS)
                .connectTimeout(gvSystem.getApp_ConnectTimeOut(), TimeUnit.MILLISECONDS)
                .readTimeout(gvSystem.getApp_ReadTimeOut(), TimeUnit.MILLISECONDS)
                .writeTimeout(gvSystem.getApp_WriteTimeOut(), TimeUnit.MILLISECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new MultiDateFormatAdapter()) // ĐẶT Ở ĐÂY
                .serializeNulls()
                .create();

        return new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(gvSystem.App_BaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(APIService.class);
    }

    public static APIService GetAPIServiceNoAuthen(long pTimeOut) {
        OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder()
                .callTimeout(pTimeOut, TimeUnit.SECONDS)
                .connectTimeout(pTimeOut, TimeUnit.SECONDS)
                .readTimeout(pTimeOut, TimeUnit.SECONDS)
                .writeTimeout(pTimeOut, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new MultiDateFormatAdapter()) // ĐẶT Ở ĐÂY
                .serializeNulls()
                .create();

        return new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(gvSystem.App_BaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(APIService.class);
    }

    public static <T> T gsonToModel(String pValue, Class<T> classOfT) {
        if (pValue == null || pValue.isEmpty()) {
            return null;
        } else {
            try {
                return new GsonBuilder()
                        .registerTypeAdapter(Date.class, new MultiDateFormatAdapter())
                        .create()
                        .fromJson(pValue, classOfT);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static String modelToGson(Object pObject) {
        if (pObject == null) {
            return "";
        } else {
            try {
                return new Gson().toJson(pObject);
            } catch (Exception e) {
                return "";
            }
        }
    }

}