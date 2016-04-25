package com.pabhinav.fiboku.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.pabhinav.fiboku.util.Constants;
import com.pabhinav.fiboku.util.FLog;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    /**
     * {@link ZXingScannerView} instance.
     */
    private ZXingScannerView mScannerView;

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
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or
     * {@link #onPause}, for your activity to start interacting with the user.</br>
     * This is a good place to start camera.
     */
    @Override
    public void onResume() {
        super.onResume();

        mScannerView.setResultHandler(this);

        /**
         * Starts Camera
         */
        mScannerView.startCamera();
    }

    /**
     * Called as part of the activity lifecycle when an activity is going into
     * the background, but has not (yet) been killed.  The counterpart to
     * {@link #onResume}. </br>
     * This is a good place to stop camera.
     */
    @Override
    public void onPause() {
        super.onPause();

        /**
         * Stops Camera
         */
        mScannerView.stopCamera();
    }

    /**
     * Back pressed by user, stop the camera and
     * set result as failure.
     */
    @Override
    public void onBackPressed(){

        setResult(Constants.RESULT_CANCELLED);

        /**
         * Stops Camera
         */
        mScannerView.stopCamera();
        super.onBackPressed();
    }

    @Override
    public void handleResult(Result rawResult) {

        FLog.d(ScannerActivity.this, rawResult.getText()); // Prints scan results
        FLog.d(ScannerActivity.this, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        /**
         * Stops Camera
         */
        mScannerView.stopCamera();

        /**
         * If result has type : EAN_13, return the value,
         * else set result as failure.
         */
        if(rawResult.getBarcodeFormat().equals(BarcodeFormat.EAN_13)) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Constants.ISBN_KEY, rawResult.getText());
            setResult(Constants.RESULT_OK, returnIntent);
        } else {
            setResult(Constants.RESULT_FAILURE);
        }

        finish();
    }
}
