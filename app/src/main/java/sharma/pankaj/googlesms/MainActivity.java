package sharma.pankaj.googlesms;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.api.GoogleApiClient;


public class MainActivity extends AppCompatActivity {

    EditText ed_otp;

    public static final String TAG = "Connection_Sms :";
    private static final int RC_HINT = 3234;

    MySMSBroadcastReceiver mySMSBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ed_otp = findViewById(R.id.ed_otp);

        mySMSBroadcastReceiver = new MySMSBroadcastReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(mySMSBroadcastReceiver, filter);

        try {
            requestHint();
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
        findViewById(R.id.id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed_otp.getText().toString().length()!=0){

                    //send Sms.............
                    //allow permission first
                    String smsNumber = ed_otp.getText().toString();
                    String smsText = "<#> Your App_Name code is: 659654\n+uZ3/NqJ0eV";
                    SmsManager smsManager= SmsManager.getDefault();
                    smsManager.sendTextMessage(smsNumber,null,smsText,null,null);

                    //open Activity.....
                    Intent intent = new Intent(MainActivity.this, GetOtpActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            }
        });


    }


    private void requestHint() throws IntentSender.SendIntentException {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {
                                Log.e(TAG, "onConnected: ");
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                Log.e(TAG, "GoogleApiClient is suspended with cause code: " + i);
                            }
                        })
                        .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                Log.e(TAG, "GoogleApiClient failed to connect: " + connectionResult);
                            }
                        })
                        .addApi(Auth.CREDENTIALS_API)
                        .build(),
                hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                RC_HINT, null, 0, 0, 0);

    }


    // Obtain the phone number from the result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                ed_otp.setText(credential.getId());
                Toast.makeText(this, ""+credential.getId(), Toast.LENGTH_SHORT).show();

                Log.e(TAG, "onActivityResult: 1 ......");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(new MySMSBroadcastReceiver(), filter);
    }
}
