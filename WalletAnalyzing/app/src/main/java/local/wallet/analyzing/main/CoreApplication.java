package local.wallet.analyzing.main;

import android.app.Application;
import android.content.res.Configuration;

import java.util.Locale;

import local.wallet.analyzing.main.Configs.Key;
import local.wallet.analyzing.utils.LogUtils;

/**
 * Created by huynh.thanh.huan on 07/03/2017.
 */
public class CoreApplication extends Application {
    protected String Tag = CoreApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        LogUtils.logEnterFunction(Tag);
        super.onCreate();

		// Load debug config from SDCard
        Configs config = new Configs(getApplicationContext());
		config.loadDebugConfig(getApplicationContext());

        // to load app language from debug_config file. Default is Japanese.
        String languageToLoad  = config.getString(Key.Locale);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
		configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());

		LogUtils.logLeaveFunction(Tag);
    }

}
