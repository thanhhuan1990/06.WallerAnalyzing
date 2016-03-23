package local.wallet.analyzing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ActivitySplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Intent i = new Intent(ActivitySplash.this, ActivityEnter.class);
//        startActivity(i);
        Intent intent = new Intent(ActivitySplash.this, ActivityMain.class);
        startActivity(intent);

        finish();
    }
}
