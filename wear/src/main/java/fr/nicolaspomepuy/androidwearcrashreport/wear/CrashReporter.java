package fr.nicolaspomepuy.androidwearcrashreport.wear;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CrashReporter {
    private static final String CRASH = "crash";
    private static String TAG = "androidwearcrashreport";

    private GoogleApiClient googleApiClient;
    private Thread.UncaughtExceptionHandler androidDefaultUEH;
    private Context context;
    private static CrashReporter INSTANCE;

    private CrashReporter(Context context) {
        this.context = context;

        //Init the handler
        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);

        //Init the google api client
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();

        //See if there is a crash to send
        File file = new File(context.getFilesDir() + "/crash.data");
        if (file.exists()) {
            byte[] fileData = new byte[(int) file.length()];
            DataInputStream dis = null;
            try {
                dis = new DataInputStream(new FileInputStream(file));
                dis.readFully(fileData);
                dis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                file.delete();
            } catch (IOException e) {
                e.printStackTrace();
                file.delete();
            }

            sendException((Throwable) Utils.deserializeObject(fileData));
        }
    }

    public static CrashReporter getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new CrashReporter(context);
        }
        return INSTANCE;
    }

    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            final byte[] data = Utils.serializeObject(ex);
            final String serializedThrowableString = new String(data);
            Log.d(TAG, "uncaughtException: " + serializedThrowableString);

            //Write crash to a file in case of the handler is not able to send the crash now
            DataOutputStream out = null;
            File file = new File(context.getFilesDir() + "/crash.data");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                out = new DataOutputStream(new FileOutputStream(file.getPath()));
                out.write(data); //data is String variable
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            androidDefaultUEH.uncaughtException(thread, ex);

            sendException(ex);
        }
    };

    public GoogleApiClient getGoogleApiClient(Context context) {
        return googleApiClient;
    }


    /**
     * Sends the exception to the handheld device
     * @param throwable the {@code Throwable} to send
     */
    public void sendException(Throwable throwable) {
        //Connect the Google API Client
        googleApiClient.connect();

        // Send the Throwable
        PutDataMapRequest dataMap = PutDataMapRequest.create(MessagingPathes.EXCEPTION + System.currentTimeMillis());
        dataMap.getDataMap().putByteArray("ex", Utils.serializeObject(throwable));
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(getGoogleApiClient(context), request);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                if (dataItemResult.getStatus().isSuccess()) {
                    //When done, remove the file
                    File file = new File(context.getFilesDir() + "/crash.data");
                    file.delete();

                    //And disconnect the client
                    googleApiClient.disconnect();
                }
            }
        });

    }

}
