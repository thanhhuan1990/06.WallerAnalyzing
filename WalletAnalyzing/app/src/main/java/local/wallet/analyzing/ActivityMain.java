package local.wallet.analyzing;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import local.wallet.analyzing.Utils.LogUtils;

public class ActivityMain extends AppCompatActivity {

    private static final String TAG = "ActivityMain";

    public static final int TAB_POSITION_TRANSACTIONS = 0;
    public static final int TAB_POSITION_NEW_TRANSACTION = 1;
    public static final int TAB_POSITION_ACCOUNTS = 2;
    public static final int TAB_POSITION_BUDGET = 3;
    public static final int TAB_POSITION_REPORTS = 4;
    public static final int TAB_POSITION_UTILITIES = 5;

    private TabLayout tabLayout;
    private TabPagerAdapter adapter;
    private int lastTabPosition = 0;

    private String fragmentAccountAdd;
    private String fragmentAccountEdit;
    private String fragmentNewTransactionSelectCategory;
    private String fragmentCategoryAdd;
    private String fragmentNewTransactionSelectAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

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

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LogUtils.trace(TAG, "onTabSelected: " + tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());

                /* Todo: Update TabLayout Follow Current Position */
                if (tab.getPosition() == TAB_POSITION_TRANSACTIONS) { //  TRANSACTION is showing, hide TRANSACTION, show NEW_TRANSACTION
                    updateTabs(TAB_POSITION_TRANSACTIONS, TAB_POSITION_NEW_TRANSACTION);
                } else if (tab.getPosition() == TAB_POSITION_NEW_TRANSACTION) { // NEW_TRANSACTION is showing, hide tab NEW_TRANSACTION, show tab TRANSACTIONS
                    updateTabs(TAB_POSITION_NEW_TRANSACTION, TAB_POSITION_TRANSACTIONS);
                } else if(lastTabPosition == 0 || lastTabPosition == 1){ // Other tabs
                    if (((ViewGroup) tabLayout.getChildAt(0)).getChildAt(TAB_POSITION_NEW_TRANSACTION).getVisibility() == View.VISIBLE) { // NEW_TRANSACTION is VISIBLE
                        updateTabs(TAB_POSITION_NEW_TRANSACTION, TAB_POSITION_TRANSACTIONS); // Gone tab NEW_TRANSACTION, tab Show TRANSACTIONS
                    } else if (((ViewGroup) tabLayout.getChildAt(0)).getChildAt(TAB_POSITION_TRANSACTIONS).getVisibility() == View.VISIBLE) { // TRANSACTION is VISIBLE
                        updateTabs(TAB_POSITION_TRANSACTIONS, TAB_POSITION_NEW_TRANSACTION); // Gone tab TRANSACTION, Show tab NEW_TRANSACTION
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                LogUtils.trace(TAG, "onTabUnselected: " + tab.getPosition());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Update ActionBar by layout's ID
     *
     * @param view
     */
    public void updateActionBar(View view) {
        LogUtils.logEnterFunction(TAG, null);
        /* Todo: Update ActionBar: Spinner TransactionType */
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mActionBar.setCustomView(view, lp);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    /**
     * Retrieve fragment from TabPagerAdapter
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

    public void setFragmentAccountAdd(String tag) {
        LogUtils.logEnterFunction(TAG, "tag = " + tag);
        fragmentAccountAdd = tag;
        LogUtils.logLeaveFunction(TAG, "tag = " + tag, null);
    }

    public String getFragmentAccountAdd() {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, fragmentAccountAdd);
        return fragmentAccountAdd;
    }

    public void setFragmentAccountEdit(String tag) {
        LogUtils.logEnterFunction(TAG, "tag = " + tag);
        fragmentAccountEdit = tag;
        LogUtils.logLeaveFunction(TAG, "tag = " + tag, null);
    }

    public String getFragmentAccountEdit() {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, fragmentAccountEdit);
        return fragmentAccountEdit;
    }

    public void setFragmentCategoryAdd(String tag) {
        LogUtils.logEnterFunction(TAG, "tag = " + tag);
        fragmentCategoryAdd = tag;
        LogUtils.logLeaveFunction(TAG, "tag = " + tag, null);
    }

    public String getFragmentCategoryAdd() {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, fragmentCategoryAdd);
        return fragmentCategoryAdd;
    }

    public void setFragmentNewTransactionSelectCategory(String tag) {
        LogUtils.logEnterFunction(TAG, "tag = " + tag);
        fragmentNewTransactionSelectCategory = tag;
        LogUtils.logLeaveFunction(TAG, "tag = " + tag, null);
    }

    public String getFragmentNewTransactionSelectCategory() {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, fragmentNewTransactionSelectCategory);
        return fragmentNewTransactionSelectCategory;
    }

    public void setFragmentNewTransactionSelectAccount(String tag) {
        LogUtils.logEnterFunction(TAG, "tag = " + tag);
        fragmentNewTransactionSelectAccount = tag;
        LogUtils.logLeaveFunction(TAG, "tag = " + tag, null);
    }

    public String getFragmentNewTransactionSelectAccount() {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, fragmentNewTransactionSelectAccount);
        return fragmentNewTransactionSelectAccount;
    }

    public void updateTabs(int hide, int show) {
        LogUtils.logEnterFunction(TAG, "Hide " + hide + ", Show " + show);
        ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(hide).setVisibility(View.GONE);
        ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(show).setVisibility(View.VISIBLE);
        LogUtils.logLeaveFunction(TAG, "Hide " + hide + ", Show " + show, null);
    }
}
