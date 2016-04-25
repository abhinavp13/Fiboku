package com.pabhinav.fiboku.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.pabhinav.fiboku.db.SQLiteDb;
import com.pabhinav.fiboku.db.UploadedBookTable;

import java.util.Arrays;
import java.util.HashSet;

import hugo.weaving.DebugLog;

/**
 * @author pabhinav
 */
public class UploadedBookProvider extends ContentProvider {

    private SQLiteDb sqLiteDb;

    private static final int UPLOADED_BOOKS = 10;
    private static final int UPLOADED_BOOKS_ID = 20;

    private static final String AUTHORITY = "com.pabhinav.fiboku.contentprovider";

    private static final String BASE_PATH = "uploaded_books";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, UPLOADED_BOOKS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", UPLOADED_BOOKS_ID);
    }


    @Override
    public boolean onCreate() {
        sqLiteDb = new SQLiteDb(getContext());
        return false;
    }

    @Nullable
    @Override
    @DebugLog
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(UploadedBookTable.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case UPLOADED_BOOKS:
                break;
            case UPLOADED_BOOKS_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(UploadedBookTable.KEY_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = sqLiteDb.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    @DebugLog
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = sqLiteDb.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case UPLOADED_BOOKS:
                id = sqlDB.insert(UploadedBookTable.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    @DebugLog
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = sqLiteDb.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case UPLOADED_BOOKS:
                rowsDeleted = sqlDB.delete(UploadedBookTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case UPLOADED_BOOKS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(UploadedBookTable.TABLE_NAME,
                            UploadedBookTable.KEY_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(UploadedBookTable.TABLE_NAME,
                            UploadedBookTable.KEY_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    @DebugLog
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = sqLiteDb.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case UPLOADED_BOOKS:
                rowsUpdated = sqlDB.update(UploadedBookTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case UPLOADED_BOOKS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(UploadedBookTable.TABLE_NAME,
                            values,
                            UploadedBookTable.KEY_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(UploadedBookTable.TABLE_NAME,
                            values,
                            UploadedBookTable.KEY_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {

        String[] available = {
                UploadedBookTable.KEY_ID,
                UploadedBookTable.KEY_BOOK_CONDITION,
                UploadedBookTable.KEY_BOOK_CONDITION_DETAIL,
                UploadedBookTable.KEY_BOOK_UPLOAD_TIMESTAMP,
                UploadedBookTable.KEY_BOOK_TITLE,
                UploadedBookTable.KEY_BOOK_AUTHORS,
                UploadedBookTable.KEY_BOOK_SUBTITLE,
                UploadedBookTable.KEY_BOOK_PUBLISHERS,
                UploadedBookTable.KEY_BOOK_PUBLISHED_DATE,
                UploadedBookTable.KEY_BOOK_DESCRIPTION,
                UploadedBookTable.KEY_BOOK_INDUSTRY_IDENTIFIER,
                UploadedBookTable.KEY_BOOK_THUMBNAIL,
                UploadedBookTable.KEY_BOOK_IMAGE
        };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
