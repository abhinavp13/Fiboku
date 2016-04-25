package com.pabhinav.fiboku.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.contentprovider.UploadedBookProvider;
import com.pabhinav.fiboku.db.UploadedBookTable;
import com.pabhinav.fiboku.models.SearchedBook;
import com.pabhinav.fiboku.models.UploadedBook;
import com.pabhinav.fiboku.util.BookUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private int mCount;
    private List<UploadedBook> mWidgetItems = new ArrayList<UploadedBook>();
    private Context mContext;
    private int mAppWidgetId;
    private Cursor cursor;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        cursor = mContext.getContentResolver().query(UploadedBookProvider.CONTENT_URI, null, null, null, UploadedBookTable.KEY_BOOK_UPLOAD_TIMESTAMP + " DESC");
        updateCount();
    }

    public void onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
        fetchDataAndUpdateList();
    }

    public void onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        mWidgetItems.clear();
    }

    public int getCount() {
        return mCount;
    }

    public RemoteViews getViewAt(int position) {
        // position will always range from 0 to getCount() - 1.

        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        if(position < mWidgetItems.size() && mWidgetItems.size() > 0) {

            Bitmap image = BookUtil.convertByteArrayToBitmap(mWidgetItems.get(position).getBookImage());
            if (image != null) {
                rv.setImageViewBitmap(R.id.uploaded_book_book_image_view, image);
            }
            rv.setTextViewText(R.id.uploaded_book_book_title, mWidgetItems.get(position).getSearchedBook().getTitle());
            rv.setTextViewText(R.id.uploaded_book_book_authors, TextUtils.join(",", mWidgetItems.get(position).getSearchedBook().getAuthors()));
            rv.setTextViewText(R.id.uploaded_book_book_isbn, mWidgetItems.get(position).getSearchedBook().getIndustryIdentifier());
            rv.setTextViewText(R.id.book_condition_, BookUtil.getStringForBookConditionEnum(mWidgetItems.get(position).getCondition()));
            rv.setTextViewText(R.id.upload_timestamp_, mWidgetItems.get(position).getUploadTimestamp());


            // Next, we set a fill-intent which will be used to fill-in the pending intent template
            // which is set on the collection view in StackWidgetProvider.
            Bundle extras = new Bundle();
            extras.putInt(StackWidgetProvider.EXTRA_ITEM, position);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            rv.setOnClickFillInIntent(R.id.main_linear_layout, fillInIntent);

            // Return the remote views object.
        }
        return rv;
    }

    public RemoteViews getLoadingView() {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.
        if(cursor != null){
            cursor.close();
        }
        cursor = mContext.getContentResolver().query(UploadedBookProvider.CONTENT_URI, null, null, null, UploadedBookTable.KEY_BOOK_UPLOAD_TIMESTAMP + " DESC");

        updateCount();
        fetchDataAndUpdateList();

    }

    private void updateCount(){
        if(cursor == null || cursor.getCount() <1){
            mCount = 0;
        } else {
            mCount = cursor.getCount();
        }
    }

    private void fetchDataAndUpdateList(){

        if(mCount == 0){
            return;
        }

        /** If data previously available, clear it **/
        mWidgetItems.clear();

        while(cursor.moveToNext()){

            /** Prepare uploaded book **/
            UploadedBook uploadedBook = new UploadedBook.UploadedBookBuilder()
                    .addCondition(BookUtil.getBookConditionEnumFromString(
                                    cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_CONDITION)))
                    )
                    .addBookImage(cursor.getBlob(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_IMAGE)))
                    .addConditionDescription(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_CONDITION_DETAIL)))
                    .addUploadTimestamp(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_UPLOAD_TIMESTAMP)))
                    .addSearchedBook(new SearchedBook.SearchedBookBuilder()
                                    .addTitle(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_TITLE)))
                                    .addAuthors(new ArrayList<String>(Arrays.asList((
                                                    cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_AUTHORS))).split(" , ")))
                                    )
                                    .addSubtitle(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_SUBTITLE)))
                                    .addPublisher(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_PUBLISHERS)))
                                    .addPublishedDate(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_PUBLISHED_DATE)))
                                    .addDescription(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_DESCRIPTION)))
                                    .addIndustryIdentifier(cursor.getString(
                                                    cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_INDUSTRY_IDENTIFIER))
                                    )
                                    .addThumbnailLink(cursor.getString(cursor.getColumnIndex(UploadedBookTable.KEY_BOOK_THUMBNAIL)))
                                    .build()
                    ).build();

            /** Add to list **/
            mWidgetItems.add(uploadedBook);

        }
        cursor.close();
    }
}