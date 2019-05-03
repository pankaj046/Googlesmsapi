package sharma.pankaj.googlesms;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class GetOtpActivity extends AppCompatActivity {

    EditText one, two, three, four, five, six;
    public SmsRetrieverClient client;
    TextView textView;
    public static final String TAG = "Connection_Sms :";
    //  private MySMSBroadcastReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_otp);

        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(this);
        Log.e(TAG, "Apps Hash Key: " + appSignatureHashHelper.getAppSignatures().get(0));
        textView = findViewById(R.id.status);
        // smsReceiver = new MySMSBroadcastReceiver();

        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);
        five = findViewById(R.id.five);
        six = findViewById(R.id.six);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(new MySMSBroadcastReceiver(), filter);



        client = SmsRetriever.getClient(this);
        final Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                textView.setText("Waiting for the OTP");
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                textView.setText("Fail : "+e);
            }
        });


        MySMSBroadcastReceiver.injectListner(new MySMSBroadcastReceiver.OTPSMSReceiveListner() {

            @Override
            public void onOTPReceived(String otp) {
                getOtp(otp);

                Toast.makeText(GetOtpActivity.this, ""+otp, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOTPTimeout() {
                Toast.makeText(GetOtpActivity.this, "Time out Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOTPReceivedError(String error) {
                Toast.makeText(GetOtpActivity.this, "Error : "+error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void getOtp(String opt) {
        String opt1 = opt.substring(opt.indexOf(":")+1, opt.indexOf("+")).trim();
        one.setText(""+opt1.charAt(0));
        two.setText(""+opt1.charAt(1));
        three.setText(""+opt1.charAt(2));
        four.setText(""+opt1.charAt(3));
        five.setText(""+opt1.charAt(4));
        six.setText(""+opt1.charAt(5));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // getApplicationContext().unregisterReceiver(new MySMSBroadcastReceiver());
    }


}
