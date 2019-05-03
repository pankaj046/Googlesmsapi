package sharma.pankaj.googlesms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import static sharma.pankaj.googlesms.MainActivity.TAG;

public class MySMSBroadcastReceiver extends BroadcastReceiver {

    String message;

    private static OTPSMSReceiveListner otpsmsReceiveListner = null;

    public static void injectListner(OTPSMSReceiveListner listner){
        otpsmsReceiveListner = listner;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG, "onReceive: is Started.............."+intent.getAction());

        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            Log.e(TAG, "onReceive:  "+status );

            assert status != null;
            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    Log.e(TAG, " code Is running............");
                    message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    otpsmsReceiveListner.onOTPReceived(message);
                    Log.e(TAG, "onReceive: sdsadsd "+message);

                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    otpsmsReceiveListner.onOTPTimeout();
                    Log.e(TAG, "onReceive: is time out......" );
                    break;
                case CommonStatusCodes.NETWORK_ERROR:

                    if (otpsmsReceiveListner != null) {
                        otpsmsReceiveListner.onOTPReceivedError("NETWORK ERROR");
                    }

                    break;
            }
        }else {
            Log.e(TAG, "onReceive: else part is ......" );
        }

    }

    public interface OTPSMSReceiveListner{
        void onOTPReceived(String otp);
        void onOTPTimeout();
        void onOTPReceivedError(String error);
    }

}