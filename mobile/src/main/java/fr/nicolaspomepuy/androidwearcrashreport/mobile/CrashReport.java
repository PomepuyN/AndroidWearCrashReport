package fr.nicolaspomepuy.androidwearcrashreport.mobile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ApplicationErrorReport;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by nicolas on 16/08/14.
 */
public class CrashReport implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context context;
    private GoogleApiClient mGoogleApiClient = null;
    private IOnCrashListener onCrashListener;

    private static CrashReport INSTANCE;
    private boolean autoReport;
    private Throwable currentException;
    private CrashInfo currentCrashInfo;

    public static CrashReport getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new CrashReport(context);
        }
        return INSTANCE;
    }

    private CrashReport(Context context) {
        this.context = context;
        //Init the Google API client ot listen to crashes
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    private CrashReport() {

    }

    public CrashReport(Context context, IOnCrashListener listener) {

        onCrashListener = listener;
    }

    @Override
    public void onConnected(Bundle bundle) {

        //Add the listener to listen to data
        Wearable.DataApi.addListener(mGoogleApiClient, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().contains("/EXCEPTION")) {

                //A new Exception has been received
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                //Get the exception
                byte[] serializedException = dataMapItem.getDataMap().getByteArray("ex");
                Throwable throwable = (Throwable) Utils.deserializeObject(serializedException);

                //Send it with the listener
                if (throwable != null) {

                    if (onCrashListener != null) {
                        currentCrashInfo = new CrashInfo.Builder(throwable)
                                .fingerprint(dataMapItem.getDataMap().getString("fingerprint"))
                                .manufacturer(dataMapItem.getDataMap().getString("manufacturer"))
                                .model(dataMapItem.getDataMap().getString("model"))
                                .product(dataMapItem.getDataMap().getString("product"))
                                .versionCode(dataMapItem.getDataMap().getInt("versionCode"))
                                .versionName(dataMapItem.getDataMap().getString("versionName"))
                                .build();
                        onCrashListener.onCrashReceived(currentCrashInfo);
                    }
                    this.currentException = throwable;
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void reportToPlayStore(Context c) {
        if (currentCrashInfo == null || currentException == null) {
            return;
        }
        ApplicationErrorReport applicationErrorReport = new ApplicationErrorReport();

        applicationErrorReport.packageName = this.context.getPackageName();
        applicationErrorReport.processName = this.context.getPackageName();
        applicationErrorReport.time = System.currentTimeMillis();
        applicationErrorReport.systemApp = false;

        ///////////
        // CRASH //
        ///////////

        applicationErrorReport.type = ApplicationErrorReport.TYPE_CRASH;

        ApplicationErrorReport.CrashInfo crashInfo = new ApplicationErrorReport.CrashInfo();
        crashInfo.exceptionClassName = currentException.getClass().getSimpleName();
        crashInfo.exceptionMessage = currentException.getMessage();
        crashInfo.stackTrace = currentCrashInfo.toString() + " - " +Utils.getStackTrace(currentException);

        StackTraceElement stackTraceElement = currentException.getStackTrace()[0];
        crashInfo.throwClassName = stackTraceElement.getClassName();
        crashInfo.throwFileName = stackTraceElement.getFileName();
        crashInfo.throwMethodName = stackTraceElement.getMethodName();
        crashInfo.throwLineNumber = stackTraceElement.getLineNumber();

        applicationErrorReport.crashInfo = crashInfo;

        Intent i = new Intent(Intent.ACTION_APP_ERROR);
        i.putExtra(Intent.EXTRA_BUG_REPORT, applicationErrorReport);
        if (!(c instanceof Activity)) {
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        // Force "Send feedback choice", but still needs user acknowledgement
        i.setClassName("com.google.android.feedback", "com.google.android.feedback.FeedbackActivity");

        c.startActivity(i);
        currentCrashInfo = null;
        currentException = null;
    }

    public void setOnCrashListener(IOnCrashListener onCrashListener) {
        this.onCrashListener = onCrashListener;
    }

    //Listener interface
    public interface IOnCrashListener {
        void onCrashReceived(CrashInfo crashInfo);
    }

}
