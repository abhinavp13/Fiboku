package com.pabhinav.fiboku.db;

import android.content.ContentValues;
import android.text.TextUtils;

import com.pabhinav.fiboku.models.UploadedBook;
import com.pabhinav.fiboku.util.BookUtil;

import hugo.weaving.DebugLog;

/**
 * @author pabhinav
 */
public class UploadedBookTable {

    public static final String TABLE_NAME = "uploadedBookTable";

    public static final String KEY_ID = "id";
    public static final String KEY_BOOK_CONDITION = "book_condition";
    public static final String KEY_BOOK_CONDITION_DETAIL = "book_condition_detail";
    public static final String KEY_BOOK_UPLOAD_TIMESTAMP = "book_upload_timestamp";
    public static final String KEY_BOOK_TITLE = "book_title";
    public static final String KEY_BOOK_AUTHORS = "book_authors";
    public static final String KEY_BOOK_SUBTITLE = "book_subtitle";
    public static final String KEY_BOOK_PUBLISHERS = "book_publishers";
    public static final String KEY_BOOK_PUBLISHED_DATE = "book_published_date";
    public static final String KEY_BOOK_DESCRIPTION = "book_description";
    public static final String KEY_BOOK_INDUSTRY_IDENTIFIER = "book_industry_identifier";
    public static final String KEY_BOOK_THUMBNAIL = "book_thumbnail";
    public static final String KEY_BOOK_IMAGE = "book_image";


    public static String getCreateQuery(){

        return "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_BOOK_CONDITION + " TEXT,"
                + KEY_BOOK_CONDITION_DETAIL + " TEXT,"
                + KEY_BOOK_UPLOAD_TIMESTAMP + " TEXT,"
                + KEY_BOOK_TITLE + " TEXT,"
                + KEY_BOOK_AUTHORS + " TEXT,"
                + KEY_BOOK_SUBTITLE + " TEXT,"
                + KEY_BOOK_PUBLISHERS + " TEXT,"
                + KEY_BOOK_PUBLISHED_DATE + " TEXT,"
                + KEY_BOOK_DESCRIPTION + " TEXT,"
                + KEY_BOOK_INDUSTRY_IDENTIFIER + " TEXT,"
                + KEY_BOOK_THUMBNAIL + " TEXT,"
                + KEY_BOOK_IMAGE + " BLOB"
                + ")";
    }

    @DebugLog
    public static ContentValues getContentValues(UploadedBook uploadedBook){
        ContentValues values = new ContentValues();
        values.put(KEY_BOOK_CONDITION, BookUtil.getStringForBookConditionEnum(uploadedBook.getCondition()));
        values.put(KEY_BOOK_CONDITION_DETAIL, uploadedBook.getConditionDescription());
        values.put(KEY_BOOK_UPLOAD_TIMESTAMP , uploadedBook.getUploadTimestamp());
        values.put(KEY_BOOK_TITLE , uploadedBook.getSearchedBook().getTitle());
        values.put(KEY_BOOK_AUTHORS , TextUtils.join(",", uploadedBook.getSearchedBook().getAuthors()));
        values.put(KEY_BOOK_SUBTITLE ,uploadedBook.getSearchedBook().getSubTitle());
        values.put(KEY_BOOK_PUBLISHERS , uploadedBook.getSearchedBook().getPublisher());
        values.put(KEY_BOOK_PUBLISHED_DATE , uploadedBook.getSearchedBook().getPublishedDate());
        values.put(KEY_BOOK_DESCRIPTION, uploadedBook.getSearchedBook().getDescription());
        values.put(KEY_BOOK_INDUSTRY_IDENTIFIER, uploadedBook.getSearchedBook().getIndustryIdentifier());
        values.put(KEY_BOOK_THUMBNAIL, uploadedBook.getSearchedBook().getThumbnailLink());
        values.put(KEY_BOOK_IMAGE, uploadedBook.getBookImage());

        return values;
    }

}
