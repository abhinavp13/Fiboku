package com.pabhinav.fiboku.alertDialogs;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.pabhinav.fiboku.R;

/**
 * @author pabhinav
 */
public class FibokuAlertInputDialog {

    private AlertDialog alertDialog;
    public OnAlertButtonClicked onAlertButtonClicked;

    public FibokuAlertInputDialog(Context context, String title, String body, String hintText, String leftButtonTitle, String rightButtonTitle){
        this(context, title, Html.fromHtml(body), hintText, leftButtonTitle, rightButtonTitle);
    }

    public FibokuAlertInputDialog(Context context, String title, Spanned body, String hintText, String leftButtonTitle, String rightButtonTitle){

        if(context == null || !(context instanceof  Activity)){
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        final View rootView = ((Activity)context).getLayoutInflater().inflate(R.layout.alert_dialog_input_layout, null);
        if(rootView == null){
            return;
        }
        alertDialogBuilder.setView(rootView);
        alertDialog = alertDialogBuilder.create();

        /** Set Dialog Values **/
        ((TextView)rootView.findViewById(R.id.title)).setText(title);
        ((TextView)rootView.findViewById(R.id.body)).setText(body);
        ((TextView)rootView.findViewById(R.id.left_button)).setText(leftButtonTitle);
        ((TextView)rootView.findViewById(R.id.right_button)).setText(rightButtonTitle);
        ((TextInputLayout)rootView.findViewById(R.id.input_dialog_text_input_layout)).setHint(hintText);
        ((AppCompatEditText)rootView.findViewById(R.id.in_dialog_edit_text)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() >0 && rootView != null){
                    ((TextView)rootView.findViewById(R.id.empty_input_view)).setVisibility(View.GONE);
                }
            }
        });

        /** On Click Listeners **/
        ((TextView)rootView.findViewById(R.id.left_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAlertButtonClicked != null) {
                    onAlertButtonClicked.onLeftButtonClicked(v);
                }
                cancel();
            }
        });
        ((TextView)rootView.findViewById(R.id.right_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rootView != null && ((AppCompatEditText)rootView.findViewById(R.id.in_dialog_edit_text)).getText().toString().length() == 0){
                    ((TextView)rootView.findViewById(R.id.empty_input_view)).setVisibility(View.VISIBLE);
                    return;
                }
                if(onAlertButtonClicked != null && rootView != null) {
                    onAlertButtonClicked.onRightButtonClicked(v,((AppCompatEditText)rootView.findViewById(R.id.in_dialog_edit_text)).getText().toString());
                }
                cancel();
            }
        });
    }

    /** Shows the alert dialog **/
    public void show(){
        if(alertDialog != null){
            alertDialog.show();
        }
    }

    /** Cancels the alert dialog **/
    public void cancel(){
        if(alertDialog != null){
            alertDialog.cancel();
        }
    }

    /** Set alert button clicked **/
    public void setOnAlertButtonClicked(OnAlertButtonClicked onAlertButtonClicked){
        this.onAlertButtonClicked = onAlertButtonClicked;
    }

    /** Callback for alert buttons clicked **/
    public interface OnAlertButtonClicked{
        void onLeftButtonClicked(View v);
        void onRightButtonClicked(View v, String editTextString);
    }
}
