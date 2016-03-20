package local.wallet.analyzing;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Event;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/22/2016.
 */
public class FragmentReportExpenseAnalysis extends Fragment implements View.OnClickListener {
    private static final String Tag = "ReportExpenseAnalysis";

    private DatabaseHelper  mDbHelper;
    private Configurations  mConfigs;

    private Calendar        mFromDate   = Calendar.getInstance();
    private Calendar        mToDate     = Calendar.getInstance();

    private int[]           mCategoryId = new int[0]; // 0 is All Categories
    private int[]           mAccountId  = new int[0]; // 0 is All Accounts
    private int             mTime       = 0; // 0 is Current

    private LinearLayout    llCategories;
    private TextView        tvCategory;
    private LinearLayout    llAccounts;
    private TextView        tvAccount;
    private LinearLayout    llViewedBy;
    private TextView        tvViewedBy;

    private LineChart       mLineChart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        String myTag = getTag();
        ((ActivityMain)getActivity()).setFragmentReportExpenseAnalysis(myTag);

        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_report_expense_analysis, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mConfigs        = new Configurations(getContext());
        mDbHelper       = new DatabaseHelper(getActivity());

        // Update DateTime
        List<Transaction> arTransactions = mDbHelper.getAllTransactions();
        if(arTransactions.size() > 0) {
            mFromDate   = arTransactions.get(arTransactions.size() - 1).getTime();
            mToDate     = arTransactions.get(0).getTime();

            // Update list of selected Account at first time
            List<Account> arAccounts = mDbHelper.getAllAccounts();
            mAccountId  = new int[arAccounts.size()];
            for(int i = 0 ; i < arAccounts.size(); i++) {
                mAccountId[i] = arAccounts.get(i).getId();
            }

            llCategories    = (LinearLayout) getView().findViewById(R.id.llCategories);
            llCategories.setOnClickListener(this);
            tvCategory      = (TextView) getView().findViewById(R.id.tvCategory);
            llAccounts      = (LinearLayout) getView().findViewById(R.id.llAccounts);
            llAccounts.setOnClickListener(this);
            tvAccount       = (TextView) getView().findViewById(R.id.tvAccount);
            llViewedBy      = (LinearLayout) getView().findViewById(R.id.llViewedBy);
            llViewedBy.setOnClickListener(this);
            tvViewedBy      = (TextView) getView().findViewById(R.id.tvViewedBy);
            mLineChart      = (LineChart) getView().findViewById(R.id.lineChart);

        } else {
            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Error_Startup_No_Data));
            ((ActivityMain) getActivity()).setCurrentVisibleItem(ActivityMain.TAB_POSITION_TRANSACTIONS);
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(((ActivityMain) getActivity()).getCurrentVisibleItem() != ActivityMain.TAB_POSITION_REPORTS) {
            return;
        }
        LogUtils.logEnterFunction(Tag, null);

        LogUtils.logLeaveFunction(Tag, null, null);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llCategories: {
                showListAccounts();
                break;
            }
            case R.id.llAccounts: {
                showListAccounts();
                break;
            }
            case R.id.llViewedBy: {
                showListTime();
                break;
            }
            default:
                break;
        }
    }

    /**
     * Start Fragment ReportEvent
     */
    private void showListAccounts() {
        LogUtils.logEnterFunction(Tag, null);
        FragmentReportEVIAccount nextFrag = new FragmentReportEVIAccount();
        Bundle bundle = new Bundle();
        bundle.putString("Fragment", ((ActivityMain) getActivity()).getFragmentReportExpenseAnalysis());
        bundle.putIntArray("Accounts", mAccountId);
        nextFrag.setArguments(bundle);
        FragmentReportExpenseAnalysis.this.getFragmentManager().beginTransaction()
                .add(R.id.ll_report, nextFrag, "FragmentReportEVIAccount")
                .addToBackStack(null)
                .commit();
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Update TextView Account
     * @param accountId
     */
    public void updateAccount(int[] accountId) {
        LogUtils.logEnterFunction(Tag, "accountId = " + accountId);

        mAccountId = accountId;

        if(mAccountId.length == mDbHelper.getAllAccounts().size()) {
            tvAccount.setText(getResources().getString(R.string.report_evi_accounts_all_accounts));
        } else {
            String account = "";
            for(int i = 0 ; i < mAccountId.length; i++) {
                if(!account.equals("")) {
                    account += ", ";
                }
                account += mDbHelper.getAccount(mAccountId[i]).getName();
            }

            tvAccount.setText(account);
        }

        LogUtils.logLeaveFunction(Tag, "accountId = " + accountId, null);
    } // End updateAccount

    /**
     * Start Fragment ReportEVITimeSelect
     */
    private void showListTime() {
        LogUtils.logEnterFunction(Tag, null);
        FragmentReportEVITime nextFrag = new FragmentReportEVITime();
        Bundle bundle = new Bundle();
        bundle.putInt("Time", mTime);
        bundle.putLong("FromDate", mFromDate.getTimeInMillis());
        bundle.putLong("ToDate", mToDate.getTimeInMillis());
        nextFrag.setArguments(bundle);
       FragmentReportExpenseAnalysis.this.getFragmentManager().beginTransaction()
                .add(R.id.ll_report, nextFrag, "FragmentReportEVITime")
                .addToBackStack(null)
                .commit();
        LogUtils.logLeaveFunction(Tag, null, null);
    } // End showListTime

    /**
     * Update TextView ViewedBy
     * @param time
     */
    public void updateTime(int time) {
        LogUtils.logEnterFunction(Tag, "time = " + time);

        mTime = time;

        String[] arTimes = getResources().getStringArray(R.array.report_evi_ar_viewedby);

        tvViewedBy.setText(arTimes[mTime]);

        switch (time) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }

        LogUtils.logLeaveFunction(Tag, "time = " + time, null);
    } // End updateTime

    /**
     * Update TextView ViewedBy
     * @param fromDate
     * @param toDate
     */
    public void updateTime(Calendar fromDate, Calendar toDate) {
        String strFromDate = String.format(getResources().getString(R.string.format_budget_day_month_year_2),
                fromDate.get(Calendar.DATE),
                fromDate.get(Calendar.MONTH) + 1,
                fromDate.get(Calendar.YEAR));
        String strToDate = String.format(getResources().getString(R.string.format_budget_day_month_year_2),
                toDate.get(Calendar.DATE),
                toDate.get(Calendar.MONTH) + 1,
                toDate.get(Calendar.YEAR));

        LogUtils.logEnterFunction(Tag, "(" + strFromDate + " - " + strToDate + ")");

        mTime       = 4; // Period
        mFromDate   = fromDate;
        mToDate     = toDate;
        mTime       = getResources().getStringArray(R.array.report_evi_ar_viewedby).length - 1;

        tvViewedBy.setText(strFromDate + " - " + strToDate);

        LogUtils.logLeaveFunction(Tag, "(" + strFromDate + " - " +  strToDate + ")", null);
    } // End updateTime


}
