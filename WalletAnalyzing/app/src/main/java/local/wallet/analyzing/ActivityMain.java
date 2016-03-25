package local.wallet.analyzing;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.View.CustomViewPager;

public class ActivityMain extends AppCompatActivity {

    private static final String TAG = "ActivityMain";

    boolean doubleBackToExitPressedOnce = false;

    public static final int TAB_POSITION_TRANSACTIONS = 0;
    public static final int TAB_POSITION_TRANSACTION_CREATE = 1;
    public static final int TAB_POSITION_LIST_ACCOUNT = 2;
    public static final int TAB_POSITION_LIST_BUDGET = 3;
    public static final int TAB_POSITION_REPORTS = 4;
    public static final int TAB_POSITION_UTILITIES = 5;

    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private TabPagerAdapter adapter;
    private int lastTabPosition = 0;

    private String fragmentTransactionUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_activity_main);

        getSupportFragmentManager().addOnBackStackChangedListener(getListener());

        /* Todo: Update Locale */
        Configurations config   = new Configurations(getApplicationContext());
        String languageToLoad   = config.getString(Configurations.Key.Locale);
        Locale locale           = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration androidConfigs = new Configuration();
        androidConfigs.locale = locale;
        getBaseContext().getResources().updateConfiguration(androidConfigs, getBaseContext().getResources().getDisplayMetrics());

        /* Todo: Init Views */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LogUtils.trace(TAG, "Selected Tab: " + tab.getPosition());

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tabLayout.getApplicationWindowToken(), 0);

                viewPager.setCurrentItem(tab.getPosition());

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

                android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                if (manager != null) {
                    int backStackEntryCount = manager.getBackStackEntryCount();

                    int index = getCurrentVisibleItem();
                    switch (index) {
                        case TAB_POSITION_TRANSACTIONS:
                            if(backStackEntryCount == 0) {
                                FragmentListTransaction listTransaction = (FragmentListTransaction)adapter.getRegisteredFragment(index);
                                listTransaction.onResume();
                            } else {
                                Fragment fragment = manager.getFragments().get(backStackEntryCount - 1);
                                fragment.onResume();
                            }
                            break;
                        case TAB_POSITION_TRANSACTION_CREATE:
                            if(backStackEntryCount == 0) {
                                FragmentTransactionCUD transactionCreate = (FragmentTransactionCUD)adapter.getRegisteredFragment(index);
                                transactionCreate.onResume();
                            } else {
                                Fragment fragment = manager.getFragments().get(backStackEntryCount - 1);
                                fragment.onResume();
                            }
                            break;
                        case TAB_POSITION_LIST_ACCOUNT:
                            break;
                        case TAB_POSITION_LIST_BUDGET:
                            break;
                        case TAB_POSITION_REPORTS:
                            break;
                        case TAB_POSITION_UTILITIES:
                            break;
                        default:
                            break;
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                lastTabPosition = tab.getPosition();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(TAB_POSITION_TRANSACTIONS).setVisibility(View.GONE);

        LogUtils.logLeaveFunction(TAG, null, null);
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

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            this.doubleBackToExitPressedOnce = true;
            showError(getResources().getString(R.string.click_back_to_exist));

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    private android.support.v4.app.FragmentManager.OnBackStackChangedListener getListener() {
        android.support.v4.app.FragmentManager.OnBackStackChangedListener result = new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                if (manager != null)
                {
                    int backStackEntryCount = manager.getBackStackEntryCount();

                    int index = getCurrentVisibleItem();
                    switch (index) {
                        case TAB_POSITION_TRANSACTIONS:
                            if(backStackEntryCount == 0) {
                                FragmentListTransaction listTransaction = (FragmentListTransaction)adapter.getRegisteredFragment(index);
                                listTransaction.onResume();
                            } else {
                                Fragment fragment = manager.getFragments().get(backStackEntryCount - 1);
                                fragment.onResume();
                            }
                            break;
                        case TAB_POSITION_TRANSACTION_CREATE:
                            if(backStackEntryCount == 0) {
                                FragmentTransactionCUD transactionCreate = (FragmentTransactionCUD)adapter.getRegisteredFragment(index);
                                transactionCreate.onResume();
                            } else {
                                Fragment fragment = manager.getFragments().get(backStackEntryCount - 1);
                                fragment.onResume();
                            }
                            break;
                        case TAB_POSITION_LIST_ACCOUNT:
                            break;
                        case TAB_POSITION_LIST_BUDGET:
                            break;
                        case TAB_POSITION_REPORTS:
                            break;
                        case TAB_POSITION_UTILITIES:
                            break;
                        default:
                            break;
                    }

                }
            }
        };
        return result;
    }

    /**
     * Retry current visible tab
     * @return
     */
    public int getCurrentVisibleItem() {
        return viewPager.getCurrentItem();
    }

    /**
     * Update current visible tab
     * @param page
     */
    public void setCurrentVisibleItem(int page) {
        viewPager.setCurrentItem(page);
    }

    /**
     * Update ActionBar by layout's ID
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

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * Show customize Toast
     * @param error
     */
    public void showError(String error) {
        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.layout_toast_error, (ViewGroup) findViewById(R.id.llCustomToast));

        // set a message
        TextView text = (TextView) layout.findViewById(R.id.tvError);
        text.setText(error);

        // Toast...
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    /**
     * Show customize Toast
     * @param message
     */
    public void showToastSuccessful(String message) {
        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.layout_toast_successful, (ViewGroup) findViewById(R.id.llCustomToast));

        // set a message
        TextView text = (TextView) layout.findViewById(R.id.tvMessage);
        text.setText(message);

        // Toast...
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
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
        LogUtils.logLeaveFunction(TAG, "Hide " + hide + ", Show " + show, null);
    }

    //region RETRY FRAGMENT from FragmentManager
    /**
     * Retrieve fragment from FragmentPagerAdapter
     *
     * @param position
     * @return
     */
    public Fragment getFragment(int position) {
        LogUtils.logEnterFunction(TAG, "position = " + position);
        Fragment fragment = adapter.getRegisteredFragment(position);
        if (fragment != null) {
            LogUtils.logLeaveFunction(TAG, "position = " + position, "OK");
        } else {
            LogUtils.logLeaveFunction(TAG, "position = " + position, "NULL");
        }
        return fragment;
    }

    public void setFragmentTransactionUpdate(String tag) {
        LogUtils.logEnterFunction(TAG, "tag = " + tag);
        fragmentTransactionUpdate = tag;
        LogUtils.logLeaveFunction(TAG, "tag = " + tag, null);
    }

    public String getFragmentTransactionUpdate() {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, fragmentTransactionUpdate);
        return fragmentTransactionUpdate;
    }

    //endregion
}
