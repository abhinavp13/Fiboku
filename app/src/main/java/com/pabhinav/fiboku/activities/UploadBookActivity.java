package com.pabhinav.fiboku.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pabhinav.fiboku.R;
import com.pabhinav.fiboku.util.BundleUtil;
import com.pabhinav.fiboku.util.Constants;
import com.pabhinav.fiboku.util.FLog;
import com.pabhinav.fiboku.util.HardwareUtil;
import com.pabhinav.fiboku.util.PermissionUtil;
import com.twitter.sdk.android.core.models.Search;

import java.util.List;
import java.util.Scanner;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;

/**
 * Activity asking for uploading a book.
 *
 * @author pabhinav
 */
public class UploadBookActivity extends AppCompatActivity {

    /**
     * Edit text for isbn
     */
    @Bind(R.id.isbn_edit_text)
    AppCompatEditText isbnEditText;

    /**
     * Error displaying text view
     */
    @Bind(R.id.error_text_view)
    TextView errorTextView;

    /**
     * main relative layout for this activity
     */
    @Bind(R.id.upload_book_main_relative_layout)
    RelativeLayout mainRelativeLayout;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_book);
        ButterKnife.bind(this);
    }

    /**
     * Tick Clicked by user
     *
     * @param v view for the tick icon
     */
    public void tickClicked(View v){
        if(validateIsbn()){

            /** call the find book activity with isbn **/
            callSearchBookWithISBN(isbnEditText.getText().toString());
        }
    }

    /**
     * Back icon pressed by user
     *
     * @param v view for the back icon
     */
    public void backClicked(View v){
        super.onBackPressed();
    }

    /**
     * Scan icon pressed by user
     *
     * @param v view for the scan icon
     */
    public void scanClicked(View v){

        /** Check camera available in device **/
        if(!HardwareUtil.isCameraAvailable(this)){

            /** No camera found **/
            Snackbar.make(mainRelativeLayout, R.string.no_camera_detected, Snackbar.LENGTH_LONG).show();
            return;
        }

        /** Need to ask for permissions for android M and above **/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            confirmCameraPermission();
        } else {
            Intent intent = new Intent(this, ScannerActivity.class);
            startActivityForResult(intent, Constants.ISBN_REQUEST);
        }
    }

    /**
     * Confirms whether app has camera permissions or not
     */
    public void confirmCameraPermission(){

        List<String> deniedPermissions = PermissionUtil.deniedPermissions(
                this,
                Manifest.permission.CAMERA
        );

        /** If you already have camera permissions, no need to ask for it. **/
        if(deniedPermissions.isEmpty()){
            Intent intent = new Intent(this, ScannerActivity.class);
            startActivityForResult(intent, Constants.ISBN_REQUEST);
            return;
        }

        /** Request for permissions **/
        ActivityCompat.requestPermissions(this, deniedPermissions.toArray(new String[deniedPermissions.size()]), Constants.REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        /** if request code matches the request code for asking permissions, confirm that all permissions are granted **/
        if(requestCode == Constants.REQUEST_CODE_SOME_FEATURES_PERMISSIONS){
            if(!PermissionUtil.confirmGrantedPermissions(grantResults)){
                /** Camera permissions not granted **/
                Snackbar.make(mainRelativeLayout, R.string.camera_permission_denied, Snackbar.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(this, ScannerActivity.class);
                startActivityForResult(intent, Constants.ISBN_REQUEST);
            }
        }
    }

    /**
     * Validate Isbn
     *
     * @return true, if it is a valid isbn, else false.
     */
    private boolean validateIsbn(){
        String isbn = isbnEditText.getText().toString();
        if(!(isbn.length() == 13)){
            errorTextView.setText(getResources().getString(R.string.enter_valid_isbn));
            requestFocus(isbnEditText);
            return false;
        } else {
            errorTextView.setText("");
        }
        return true;
    }

    /** Request focus for a view **/
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        /** if isbn scan was requested **/
        if(requestCode == Constants.ISBN_REQUEST){

            /** Got success **/
            if(resultCode == Constants.RESULT_OK){
                String isbn = BundleUtil.getStringFromBundle(null, data.getExtras() ,Constants.ISBN_KEY, "");
                FLog.d(this, isbn);

                /** call the find book activity with isbn **/
                callSearchBookWithISBN(isbn);

            } else if(resultCode == Constants.RESULT_FAILURE) {

                /** There was a failure, due to isbn not of a book scanned **/
                Snackbar.make(mainRelativeLayout, R.string.not_a_book_isbn_scanned, Snackbar.LENGTH_LONG).show();

            }
        }
    }

    /**
     * Calls the Search Book activity with the given isbn.
     *
     * @param isbn of the book
     */
    private void callSearchBookWithISBN(String isbn){
        Intent intent = new Intent(UploadBookActivity.this, SearchBookActivity.class);
        intent.putExtra(Constants.ISBN_KEY, isbn);
        intent.putExtra(Constants.IS_FROM_UPLOAD, true);
        startActivity(intent);
        finish();
    }
}
