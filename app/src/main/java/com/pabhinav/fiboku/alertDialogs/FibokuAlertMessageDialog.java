package com.pabhinav.fiboku.alertDialogs;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.pabhinav.fiboku.R;

/**
 * @author pabhinav
 */
public class FibokuAlertMessageDialog {

    private AlertDialog alertDialog;
    public OnAlertButtonClicked onAlertButtonClicked;

    public FibokuAlertMessageDialog(Context context, String title, String body, String leftButtonTitle, String rightButtonTitle){
        this(context, title, Html.fromHtml(body), leftButtonTitle, rightButtonTitle);
    }

    public FibokuAlertMessageDialog(Context context, String title, Spanned body, String leftButtonTitle, String rightButtonTitle){

        if(context == null || !(context instanceof Activity)){
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        View rootView = ((Activity) context).getLayoutInflater().inflate(R.layout.alert_dialog_layout, null);
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

        /** On Click Listeners **/
        ((TextView)rootView.findViewById(R.id.left_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onAlertButtonClicked != null) {
                    onAlertButtonClicked.onLeftButtonClicked(v);
                }
                cancel();
            }
        });
        ((TextView)rootView.findViewById(R.id.right_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onAlertButtonClicked != null) {
                    onAlertButtonClicked.onRightButtonClicked(v);
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
        void onRightButtonClicked(View v);
    }
}
