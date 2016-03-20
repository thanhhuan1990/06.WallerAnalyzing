package local.wallet.analyzing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

import local.wallet.analyzing.View.PasscodeText;

/**
 * Created by huynh.thanh.huan on 1/4/2016.
 */
public class ActivityEnter extends Activity implements IPasscodeEnter {

    protected PasscodeText mPasscode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_enter);

        Configurations config = new Configurations(getApplicationContext());
        String passCode = config.getString(Configurations.Key.Passcode);

        mPasscode   = new PasscodeText(this, this, passCode);
        // Temporary pass passcoed when developing
        Intent intent = new Intent(ActivityEnter.this, ActivityMain.class);
        startActivity(intent);
    }

    @Override
    public void onPasscodeResult(boolean result) {
        if(result) { // Login Successful
            Intent intent = new Intent(ActivityEnter.this, ActivityMain.class);
            startActivity(intent);
            finish();
        } else {    // Login fail
            mPasscode.clearPasscode();
            ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(200);
        }
    }

}
