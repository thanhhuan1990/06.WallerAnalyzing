package local.wallet.analyzing.main;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import local.wallet.analyzing.R;
import local.wallet.analyzing.utils.LogUtils;
import local.wallet.analyzing.main.view.CustomViewPager;

public class ActivityMain extends BaseActivity {

    private static final String Tag = "ActivityMain";

    boolean doubleBackToExitPressedOnce = false;

    public static final int TAB_POSITION_TRANSACTIONS = 0;
    public static final int TAB_POSITION_TRANSACTION_CREATE = 1;
    public static final int TAB_POSITION_LIST_ACCOUNT = 2;
    public static final int TAB_POSITION_LIST_BUDGET = 3;
    public static final int TAB_POSITION_REPORTS = 4;
    public static final int TAB_POSITION_UTILITIES = 5;

    private TabLayout           tabLayout;
    private CustomViewPager     viewPager;
    private TabPagerAdapter     adapter;
    private int                 lastTabPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_main);

        /* Todo: Update Locale */
        Configs config   = new Configs(getApplicationContext());
        String languageToLoad   = config.getString(Configs.Key.Locale);
        Locale locale           = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration androidConfigs = new Configuration();
        androidConfigs.locale = locale;
        getBaseContext().getResources().updateConfiguration(androidConfigs, getBaseContext().getResources().getDisplayMetrics());

        /* Todo: Init Views */
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_transaction)).setIcon(R.drawable.icon_tab_transactions));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_transaction)).setIcon(R.drawable.icon_tab_transaction_new));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_account)).setIcon(R.drawable.icon_tab_account));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_budget)).setIcon(R.drawable.icon_tab_budget));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_report)).setIcon(R.drawable.icon_tab_report));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tab_utilities)).setIcon(R.drawable.icon_tab_utilities));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (CustomViewPager) findViewById(R.id.pager);
        adapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LogUtils.trace(TAG, "Selected Tab: " + tab.getPosition());

                hideKeyboard();

                viewPager.setCurrentItem(tab.getPosition());

                doubleBackToExitPressedOnce = false;

                // Todo: Update TabLayout Follow Current Position
                if (tab.getPosition() == TAB_POSITION_TRANSACTIONS) {                       //  TRANSACTION is showing, hide TRANSACTION, show NEW_TRANSACTION

                    updateTabs(TAB_POSITION_TRANSACTIONS, TAB_POSITION_TRANSACTION_CREATE);

                } else if (tab.getPosition() == TAB_POSITION_TRANSACTION_CREATE) {             // NEW_TRANSACTION is showing, hide tab NEW_TRANSACTION, show tab TRANSACTIONS

                    updateTabs(TAB_POSITION_TRANSACTION_CREATE, TAB_POSITION_TRANSACTIONS);

                } else if(lastTabPosition == 0 || lastTabPosition == 1){ // Other tabs

                    if (((ViewGroup) tabLayout.getChildAt(0)).getChildAt(TAB_POSITION_TRANSACTION_CREATE).getVisibility() == View.VISIBLE) { // NEW_TRANSACTION is VISIBLE

                        updateTabs(TAB_POSITION_TRANSACTION_CREATE, TAB_POSITION_TRANSACTIONS); // Gone tab NEW_TRANSACTION, tab Show TRANSACTIONS

                    } else if (((ViewGroup) tabLayout.getChildAt(0)).getChildAt(TAB_POSITION_TRANSACTIONS).getVisibility() == View.VISIBLE) { // TRANSACTION is VISIBLE

                        updateTabs(TAB_POSITION_TRANSACTIONS, TAB_POSITION_TRANSACTION_CREATE); // Gone tab TRANSACTION, Show tab NEW_TRANSACTION

                    }
                }

                adapter.resumeTopFragment(getCurrentVisibleItem());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                lastTabPosition = tab.getPosition();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(TAB_POSITION_TRANSACTIONS).setVisibility(View.GONE);

        LogUtils.logLeaveFunction(TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            finish();
        }

        int count = adapter.getBackStackCount(getCurrentVisibleItem());
        if (count == 1) {
            this.doubleBackToExitPressedOnce = true;
            showError(getResources().getString(R.string.click_back_to_exist));

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            adapter.popBackStack(getCurrentVisibleItem());
            adapter.removeTopFragment(getCurrentVisibleItem());
        }

    }

    @Override
    public void addFragment(int tab, int containViewId, Fragment fragment, String tag, boolean addToBackStack) {
        super.addFragment(tab, containViewId, fragment, tag, addToBackStack);
        adapter.addFragment(tab, fragment);

    }

    public void replaceFragment(int tab, int containViewId, Fragment fragment, String tag, boolean addToBackStack) {
        super.replaceFragment(tab, containViewId, fragment, tag, addToBackStack);
        adapter.replaceFragment(tab, fragment);
    }

    /**
     * Retry current visible tab
     * @return
     */
    public int getCurrentVisibleItem() {
        return viewPager.getCurrentItem();
    }

    /**
     * Update ActionBar by layout's ID
     * @param view
     */
    public void updateActionBar(View view) {
//        LogUtils.logEnterFunction(Tag);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mActionBar.setCustomView(view, lp);
//        LogUtils.logLeaveFunction(TAG);
    }

    /**
     * Update tabLayout
     * @param hide
     * @param show
     */
    public void updateTabs(int hide, int show) {
        LogUtils.logEnterFunction(TAG, "Hide " + hide + ", Show " + show);
        ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(hide).setVisibility(View.GONE);
        ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(show).setVisibility(View.VISIBLE);
        LogUtils.logLeaveFunction(TAG);
    }
    //endregion
}
