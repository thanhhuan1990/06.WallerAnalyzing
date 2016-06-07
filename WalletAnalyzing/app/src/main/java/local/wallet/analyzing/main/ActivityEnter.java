package local.wallet.analyzing.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

import local.wallet.analyzing.main.listener.IPasscodeEnter;
import local.wallet.analyzing.R;
import local.wallet.analyzing.main.view.PasscodeText;

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
