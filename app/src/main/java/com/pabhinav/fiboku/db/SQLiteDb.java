package com.pabhinav.fiboku.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pabhinav.fiboku.BuildConfig;
import com.pabhinav.fiboku.models.SearchedBook;
import com.pabhinav.fiboku.models.UploadedBook;
import com.pabhinav.fiboku.util.BookUtil;

import java.util.ArrayList;
import java.util.Arrays;

import hugo.weaving.DebugLog;

/**
 * @author pabhinav
 */
public class SQLiteDb extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = Integer.parseInt(BuildConfig.DATABASE_VERSION);
    public static final String DATABASE_NAME = BuildConfig.DATABASE_NAME;

    public SQLiteDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLiteDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLiteDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UploadedBookTable.getCreateQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UploadedBookTable.TABLE_NAME);

        onCreate(db);
    }

}
