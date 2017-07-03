package local.wallet.analyzing.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import local.wallet.analyzing.utils.LogUtils;

public class ActivitySplash extends AppCompatActivity {
	private static final String	Tag = "ActivitySplash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		LogUtils.logEnterFunction(Tag);
        super.onCreate(savedInstanceState);

        Intent i = new Intent(ActivitySplash.this, ActivityEnter.class);
        startActivity(i);
//        Intent intent = new Intent(ActivitySplash.this, ActivityMain.class);
//        startActivity(intent);

        finish();
		LogUtils.logLeaveFunction(Tag);
    }
}
