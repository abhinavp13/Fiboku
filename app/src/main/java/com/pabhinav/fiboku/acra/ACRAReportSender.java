package com.pabhinav.fiboku.acra;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pabhinav.fiboku.firebase.ACRAFirebaseHelper;

import org.acra.ACRAConstants;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pabhinav
 */
public class ACRAReportSender implements ReportSender {

    public ACRAReportSender(){
        /** Empty constructor required **/
    }

    /**
     * Send crash report data. You don't have to take care of managing Threads,
     * just implement what is necessary to handle the data. ACRA will use a
     * specific Thread (not the UI Thread) to run your sender.
     *
     *
     *
     * @param context       Android Context in which to send the crash report.
     * @param errorContent
     *            Stores key/value pairs for each report field. A report field
     *            is identified by a {@link org.acra.ReportField} enum value.
     * @throws ReportSenderException
     *             If anything goes fatally wrong during the handling of crash
     *             data, you can (should) throw a {@link ReportSenderException}
     *             with a custom message.
     */
    @Override
    public void send(final Context context, CrashReportData errorContent) throws ReportSenderException {

        final Map<String, String> finalReport = remap(errorContent);

        /** send data for firebase to handle **/
        ACRAFirebaseHelper firebaseHelper = new ACRAFirebaseHelper(context);
        firebaseHelper.sendAcraMap(finalReport);
    }

    /**
     * Report has many fields, most of them are filled, but some are null or empty,
     * these can be filtered here in remap method. Since, report is send to firebase,
     * if not filtered correctly, null entries can kill nodes, which can lead to loss
     * in error report details.
     *
     *
     *
     * @param report
     *          This is a map of {@link ReportField} and String, which maps report fields
     *          with corresponding data.
     *
     * @return This method returns a map of String to String, which maps field name in
     *          String to corresponding String value.
     */
    @NonNull
    private Map<String, String> remap(@NonNull Map<ReportField, String> report) {

        ReportField[] fields = ACRAConstants.DEFAULT_REPORT_FIELDS;

        final Map<String, String> finalReport = new HashMap<String, String>(report.size());
        for (ReportField field : fields) {
            if(field !=null && report.get(field) != null) {
                finalReport.put(field.toString(), report.get(field));
            }
        }
        return finalReport;
    }
}
