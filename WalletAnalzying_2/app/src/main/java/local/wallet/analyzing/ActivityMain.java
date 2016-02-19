package local.wallet.analyzing;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import java.util.Locale;

import local.wallet.analyzing.Utils.LogUtils;

/**
 * Created by huynh.thanh.huan on 2/18/2016.
 */
public class ActivityMain extends FragmentActivity {
    TabHost tHost;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_main);

        /* Update Locale */
        Configurations config = new Configurations(getApplicationContext());
        String languageToLoad  = config.getString(Configurations.Key.Locale);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration androidConfigs = new Configuration();
        androidConfigs.locale = locale;
        getBaseContext().getResources().updateConfiguration(androidConfigs, getBaseContext().getResources().getDisplayMetrics());

        tHost = (TabHost) findViewById(android.R.id.tabhost);
        tHost.setup();

        /** Defining Tab Change Listener event. This is invoked when tab is changed */
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();
                FragmentListTransaction androidFragment = (FragmentListTransaction) fm.findFragmentByTag("ListTransaction");
                FragmentListAccount appleFragment = (FragmentListAccount) fm.findFragmentByTag("ListAccount");
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

                /** Detaches the androidfragment if exists */
                if(androidFragment!=null)
                    ft.detach(androidFragment);

                /** Detaches the applefragment if exists */
                if(appleFragment!=null)
                    ft.detach(appleFragment);

                /** If current tab is android */
                if(tabId.equalsIgnoreCase("android")){

                    if(androidFragment==null){
                        /** Create AndroidFragment and adding to fragmenttransaction */
                        ft.add(R.id.realtabcontent,new FragmentListTransaction(), "ListTransaction");
                    }else{
                        /** Bring to the front, if already exists in the fragmenttransaction */
                        ft.attach(androidFragment);
                    }

                }else{    /** If current tab is apple */
                    if(appleFragment==null){
                        /** Create AppleFragment and adding to fragmenttransaction */
                        ft.add(R.id.realtabcontent,new FragmentListAccount(), "ListAccount");
                    }else{
                        /** Bring to the front, if already exists in the fragmenttransaction */
                        ft.attach(appleFragment);
                    }
                }
                ft.commit();
            }
        };

        /** Setting tabchangelistener for the tab */
        tHost.setOnTabChangedListener(tabChangeListener);

        /** Defining tab builder for Andriod tab */
        TabHost.TabSpec tSpecAndroid = tHost.newTabSpec("ListTransaction");
        tSpecAndroid.setIndicator("Transaction", getResources().getDrawable(R.drawable.icon_tab_transactions));
        tSpecAndroid.setContent(new DummyTabContent(getBaseContext()));
        tHost.addTab(tSpecAndroid);

        /** Defining tab builder for Apple tab */
        TabHost.TabSpec tSpecApple = tHost.newTabSpec("ListAccount");
        tSpecApple.setIndicator("Account", getResources().getDrawable(R.drawable.icon_tab_account));
        tSpecApple.setContent(new DummyTabContent(getBaseContext()));
        tHost.addTab(tSpecApple);

    }



    /**
     * Update ActionBar by layout's ID
     *
     * @param view
     */
    public void updateActionBar(View view) {
        LogUtils.logEnterFunction(TAG, null);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mActionBar.setCustomView(view, lp);
        LogUtils.logLeaveFunction(TAG, null, null);
    }
}
