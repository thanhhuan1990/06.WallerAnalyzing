package local.wallet.analyzing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

import local.wallet.analyzing.View.PasscodeText;

/**
 * Created by huynh.thanh.huan on 1/4/2016.
 */
public class ActivityEnter extends Activity implements OnInputPasscode{

    protected PasscodeText mPasscode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_enter);

        Configurations config = new Configurations(getApplicationContext());
        String passCode = config.getString(Configurations.Key.Passcode);

        mPasscode   = new PasscodeText(this, this, passCode);
    }

    @Override
    public void onPasscodeOK() {
        Intent intent = new Intent(ActivityEnter.this, ActivityMain.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPasscodeWrong() {
        mPasscode.clearPasscode();
        ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(200);
    }

}
