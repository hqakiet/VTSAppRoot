package com.vts.vtsapproot.Tools;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LocalDB
        extends SQLiteOpenHelper {

    public LocalDB
            (
                    @Nullable Context context
                    , @Nullable String name
                    , @Nullable SQLiteDatabase.CursorFactory factory
                    , int version
            ) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String strSQL;

        strSQL = "CREATE TABLE THONGSOHETHONG (MATHONGSO TEXT PRIMARY KEY, GIATRITHONGSO_STRING TEXT, GIATRITHONGSO_NUMBER INTEGER)";
        sqLiteDatabase.execSQL(strSQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        String strSQL;

//        strSQL = "CREATE TABLE DATA_PROCESS (MAHANG TEXT PRIMARY KEY, MAVACH TEXT, TENHANG TEXT, DVT TEXT, QUYCACH TEXT, NHOM TEXT, LOAI TEXT, HASDATA INTEGER, SOLUONGTON DOUBLE)";
//        try {
//            sqLiteDatabase.execSQL(strSQL);
//        } catch (Exception ignored) {
//        }

//        if (i < 5) {
//            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS DATA_PROCESS");
//            strSQL = "CREATE TABLE DATA_PROCESS (LOGTYPE TEXT, MATIMKIEM TEXT, THOIGIAN LONG DEFAULT (CAST((julianday('now') - 2440587.5) * 86400000 AS INTEGER)), PRIMARY KEY (LOGTYPE, MATIMKIEM))";
//            sqLiteDatabase.execSQL(strSQL);
//        }

    }

    public boolean DoInsert_THONGSOHETHONG_StringData(String pMa, String pGiaTri) {
        ContentValues mContentValues = new ContentValues();
        mContentValues.put("MATHONGSO", pMa);
        mContentValues.put("GIATRITHONGSO_STRING", pGiaTri);
        mContentValues.put("GIATRITHONGSO_NUMBER", 0);
        long kiemtra = this.getWritableDatabase().insert("THONGSOHETHONG", null, mContentValues);
        return kiemtra != 0;
    }

    public boolean DoInsert_THONGSOHETHONG_IntegerData(String pMa, int pGiaTri) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("MATHONGSO", pMa);
        contentValues.put("GIATRITHONGSO_STRING", "");
        contentValues.put("GIATRITHONGSO_NUMBER", pGiaTri);
        long kiemtra = this.getWritableDatabase().insert("THONGSOHETHONG", null, contentValues);
        return kiemtra != 0;
    }

    public boolean DoDelete_THONGSOHETHONG(String pMa) {
        long kiemtra = this.getWritableDatabase().delete("THONGSOHETHONG", "MATHONGSO = '" + pMa + "'", null);
        return kiemtra != 0;
    }

    @SuppressLint("Range")
    public String Get_GIATRITHONGSO_STRING(String pMa) {
        String result = null;
        try {
            String strSQL = "SELECT * FROM THONGSOHETHONG WHERE MATHONGSO='" + pMa + "'";
            Cursor cursor = this.getWritableDatabase().rawQuery(strSQL, null);
            if (cursor.getCount() == 0) {
                cursor.close();
                DoInsert_THONGSOHETHONG_StringData(pMa, "");
            } else {
                cursor.moveToFirst();
                result = cursor.getString(cursor.getColumnIndex("GIATRITHONGSO_STRING"));
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw (e);
        }
        return result;
    }

    @SuppressLint("Range")
    public String Get_GIATRITHONGSO_STRING(String pMa, String pDefaultValue) {
        String result = pDefaultValue;
        try {
            String strSQL = "SELECT * FROM THONGSOHETHONG WHERE MATHONGSO='" + pMa + "'";
            Cursor cursor = this.getWritableDatabase().rawQuery(strSQL, null);
            if (cursor.getCount() == 0) {
                cursor.close();
                DoInsert_THONGSOHETHONG_StringData(pMa, pDefaultValue);
            } else {
                cursor.moveToFirst();
                result = cursor.getString(cursor.getColumnIndex("GIATRITHONGSO_STRING"));
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw (e);
        }
        return result;
    }

    public void Set_GIATRITHONGSO_STRING(String pMa, String pGiaTri) {
        try {
            String strSQL = "SELECT * FROM THONGSOHETHONG WHERE MATHONGSO='" + pMa + "'";
            Cursor cursor = this.getWritableDatabase().rawQuery(strSQL, null);
            if (cursor.getCount() == 0) {
                cursor.close();
                DoInsert_THONGSOHETHONG_StringData(pMa, pGiaTri);
            } else {
                cursor.close();
                ContentValues contentValues = new ContentValues();
                contentValues.put("GIATRITHONGSO_STRING", pGiaTri);
                long kiemtra = this.getWritableDatabase().update("THONGSOHETHONG", contentValues, "MATHONGSO = '" + pMa + "'", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw (e);
        }
    }

    @SuppressLint("Range")
    public int Get_GIATRITHONGSO_NUMBER(String pMa) {
        int result = 0;
        try {
            String strSQL = "SELECT * FROM THONGSOHETHONG WHERE MATHONGSO='" + pMa + "'";
            Cursor cursor = this.getWritableDatabase().rawQuery(strSQL, null);
            if (cursor.getCount() == 0) {
                cursor.close();
                DoInsert_THONGSOHETHONG_IntegerData(pMa, 0);
            } else {
                cursor.moveToFirst();
                result = cursor.getInt(cursor.getColumnIndex("GIATRITHONGSO_NUMBER"));
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw (e);
        }
        return result;
    }

    @SuppressLint("Range")
    public int Get_GIATRITHONGSO_NUMBER(String pMa, int pDefaultValue) {
        int result = pDefaultValue;
        try {
            String strSQL = "SELECT * FROM THONGSOHETHONG WHERE MATHONGSO='" + pMa + "'";
            Cursor cursor = this.getWritableDatabase().rawQuery(strSQL, null);
            if (cursor.getCount() == 0) {
                cursor.close();
                DoInsert_THONGSOHETHONG_IntegerData(pMa, pDefaultValue);
            } else {
                cursor.moveToFirst();
                result = cursor.getInt(cursor.getColumnIndex("GIATRITHONGSO_NUMBER"));
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw (e);
        }
        return result;
    }

    public boolean Set_GIATRITHONGSO_NUMBER(String pMa, int pGiaTri) {
        try {
            String strSQL = "SELECT * FROM THONGSOHETHONG WHERE MATHONGSO='" + pMa + "'";
            Cursor cursor = this.getWritableDatabase().rawQuery(strSQL, null);
            if (cursor.getCount() == 0) {
                cursor.close();
                DoInsert_THONGSOHETHONG_IntegerData(pMa, pGiaTri);
                return true;
            } else {
                cursor.close();
                ContentValues contentValues = new ContentValues();
                contentValues.put("GIATRITHONGSO_NUMBER", pGiaTri);
                long kiemtra = this.getWritableDatabase().update("THONGSOHETHONG", contentValues, "MATHONGSO = '" + pMa + "'", null);
                return kiemtra != 0;
            }
        } catch (Exception e) {
            throw (e);
        }
    }

}
