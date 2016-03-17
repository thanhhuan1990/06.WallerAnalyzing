package local.wallet.analyzing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/22/2016.
 */
public class FragmentReportEVI extends Fragment implements View.OnClickListener {
    private static final String Tag = "ReportEVI";

    private DatabaseHelper  mDbHelper;
    private Configurations  mConfigs;
    private Calendar        mFromDate   = Calendar.getInstance();
    private Calendar        mToDate     = Calendar.getInstance();

    private int[]           mAccountId  = new int[0]; // 0 is All Accounts
    private int             mTime       = 0; // 0 is Current

    private LinearLayout    llAccount;
    private TextView        tvAccount;
    private LinearLayout    llViewedBy;
    private TextView        tvViewedBy;
    private LinearLayout    llEVI;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        String myTag = getTag();
        ((ActivityMain)getActivity()).setFragmentReportEVI(myTag);

        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_report_evi, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mConfigs        = new Configurations(getContext());
        mDbHelper       = new DatabaseHelper(getActivity());

        // Update DateTime
        List<Transaction> arTransactions = mDbHelper.getAllTransactions();
        mFromDate   = arTransactions.get(arTransactions.size() - 1).getTime();
        mToDate     = arTransactions.get(0).getTime();
        String strFromDate = String.format(getResources().getString(R.string.format_budget_day_month_year_2),
                mFromDate.get(Calendar.DATE),
                mFromDate.get(Calendar.MONTH) + 1,
                mFromDate.get(Calendar.YEAR));
        String strToDate = String.format(getResources().getString(R.string.format_budget_day_month_year_2),
                mToDate.get(Calendar.DATE),
                mToDate.get(Calendar.MONTH) + 1,
                mToDate.get(Calendar.YEAR));

        LogUtils.trace(Tag, "From: " + strFromDate + " - To: " + strToDate);

        // Update list of selected Account at first time
        List<Account> arAccounts = mDbHelper.getAllAccounts();
        mAccountId  = new int[arAccounts.size()];
        for(int i = 0 ; i < arAccounts.size(); i++) {
            mAccountId[i] = arAccounts.get(i).getId();
        }

        llAccount       = (LinearLayout) getView().findViewById(R.id.llAccount);
        llAccount.setOnClickListener(this);
        tvAccount       = (TextView) getView().findViewById(R.id.tvAccount);
        llViewedBy      = (LinearLayout) getView().findViewById(R.id.llViewedBy);
        llViewedBy.setOnClickListener(this);
        tvViewedBy      = (TextView) getView().findViewById(R.id.tvViewedBy);
        llEVI           = (LinearLayout) getView().findViewById(R.id.llEVI);

        updateListEviCurrent();

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End onActivityCreated

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(((ActivityMain) getActivity()).getCurrentVisibleItem() != ActivityMain.TAB_POSITION_REPORTS) {
            return;
        }
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        super.onCreateOptionsMenu(menu, inflater);
    } // End onCreateOptionsMenu

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llAccount: {
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
     * Update list EvI follow FromDate, ToDate
     */
    private void updateListEviCurrent() {
        LogUtils.logEnterFunction(Tag, null);
        llEVI.removeAllViews();

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View            view                = mInflater.inflate(R.layout.layout_fragment_report_evi_current, null);
        LinearLayout    llToday             = (LinearLayout)    view.findViewById(R.id.llToday);
        TextView        tvToday             = (TextView)        view.findViewById(R.id.tvToday);
        SeekBar         sbTodayIncome       = (SeekBar)         view.findViewById(R.id.sbTodayIncome);
        SeekBar         sbTodayExpense      = (SeekBar)         view.findViewById(R.id.sbTodayExpense);
        TextView        tvTodayIncome       = (TextView)        view.findViewById(R.id.tvTodayIncome);
        TextView        tvTodayExpense      = (TextView)        view.findViewById(R.id.tvTodayExpense);
        LinearLayout    llThisWeek          = (LinearLayout)    view.findViewById(R.id.llThisWeek);
        TextView        tvThisWeek          = (TextView)        view.findViewById(R.id.tvThisWeek);
        SeekBar         sbThisWeekIncome    = (SeekBar)         view.findViewById(R.id.sbThisWeekIncome);
        SeekBar         sbThisWeekExpense   = (SeekBar)         view.findViewById(R.id.sbThisWeekExpense);
        TextView        tvThisWeekIncome    = (TextView)        view.findViewById(R.id.tvThisWeekIncome);
        TextView        tvThisWeekExpense   = (TextView)        view.findViewById(R.id.tvThisWeekExpense);
        LinearLayout    llThisMonth         = (LinearLayout)    view.findViewById(R.id.llThisMonth);
        TextView        tvThisMonth         = (TextView)        view.findViewById(R.id.tvThisMonth);
        SeekBar         sbThisMonthIncome   = (SeekBar)         view.findViewById(R.id.sbThisMonthIncome);
        SeekBar         sbThisMonthExpense  = (SeekBar)         view.findViewById(R.id.sbThisMonthExpense);
        TextView        tvThisMonthIncome   = (TextView)        view.findViewById(R.id.tvThisMonthIncome);
        TextView        tvThisMonthExpense  = (TextView)        view.findViewById(R.id.tvThisMonthExpense);
        LinearLayout    llThisYear          = (LinearLayout)    view.findViewById(R.id.llThisYear);
        TextView        tvThisYear          = (TextView)        view.findViewById(R.id.tvThisYear);
        SeekBar         sbThisYearIncome    = (SeekBar)         view.findViewById(R.id.sbThisYearIncome);
        SeekBar         sbThisYearExpense   = (SeekBar)         view.findViewById(R.id.sbThisYearExpense);
        TextView        tvThisYearIncome    = (TextView)        view.findViewById(R.id.tvThisYearIncome);
        TextView        tvThisYearExpense   = (TextView)        view.findViewById(R.id.tvThisYearExpense);
        LinearLayout    llAll               = (LinearLayout)    view.findViewById(R.id.llAll);
        SeekBar         sbIncome            = (SeekBar)         view.findViewById(R.id.sbIncome);
        SeekBar         sbExpense           = (SeekBar)         view.findViewById(R.id.sbExpense);
        TextView        tvIncome            = (TextView)        view.findViewById(R.id.tvIncome);
        TextView        tvExpense           = (TextView)        view.findViewById(R.id.tvExpense);
        TextView        tvNetIncome         = (TextView)        view.findViewById(R.id.tvNetIncome);

        sbTodayExpense.setEnabled(false);
        sbTodayIncome.setEnabled(false);
        sbThisWeekExpense.setEnabled(false);
        sbThisWeekIncome.setEnabled(false);
        sbThisMonthIncome.setEnabled(false);
        sbThisMonthExpense.setEnabled(false);
        sbThisYearIncome.setEnabled(false);
        sbThisYearExpense.setEnabled(false);
        sbIncome.setEnabled(false);
        sbExpense.setEnabled(false);

        List<Account> arAccounts = mDbHelper.getAllAccounts();
        // region Todo: Update View Today
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        today.clear(Calendar.MINUTE);
        today.clear(Calendar.SECOND);
        today.clear(Calendar.MILLISECOND);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTimeInMillis(today.getTimeInMillis());
        tomorrow.add(Calendar.DATE, 1);

        List<Transaction> aTransactionToday = mDbHelper.getTransactionsByTimeAndAccount(mAccountId.length == arAccounts.size() ? null : mAccountId, today, tomorrow);

        double todayIncome = 0.0, todayExpense = 0.0;

        for (Transaction tran : aTransactionToday) {
            if (tran.getTransactionType() == Transaction.TransactionEnum.Expense.getValue()) {
                todayExpense += tran.getAmount();
            } else if (tran.getTransactionType() == Transaction.TransactionEnum.Income.getValue()) {
                todayIncome += tran.getAmount();
            } else if (tran.getTransactionType() == Transaction.TransactionEnum.Adjustment.getValue()) {
                if(tran.getFromAccountId() != 0) {
                    todayExpense += tran.getAmount();
                } else if(tran.getToAccountId() != 0) {
                    todayIncome += tran.getAmount();
                }
            }
        }

        tvToday.setText(String.format(getResources().getString(R.string.report_evi_today),
                today.get(Calendar.DATE),
                today.get(Calendar.MONTH) + 1,
                today.get(Calendar.YEAR)));

        sbTodayExpense.setMax(todayIncome > todayExpense ? (int) todayIncome : (int) todayExpense);
        sbTodayExpense.setProgress((int) todayExpense);
        tvTodayIncome.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), todayIncome));
        sbTodayIncome.setMax(todayIncome > todayExpense ? (int) todayIncome : (int) todayExpense);
        sbTodayIncome.setProgress((int) todayIncome);
        tvTodayExpense.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), todayExpense));
        // endregion

        //region Todo: Update View This Week
        Calendar calStartOfWeek = Calendar.getInstance();
        calStartOfWeek.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        calStartOfWeek.clear(Calendar.MINUTE);
        calStartOfWeek.clear(Calendar.SECOND);
        calStartOfWeek.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        calStartOfWeek.set(Calendar.DAY_OF_WEEK, calStartOfWeek.getFirstDayOfWeek());

        Calendar calStartOfNextWeek = Calendar.getInstance();
        calStartOfNextWeek.setTimeInMillis(calStartOfWeek.getTimeInMillis());
        calStartOfNextWeek.add(Calendar.WEEK_OF_YEAR, 1);

        List<Transaction> aTransactionThisWeek = mDbHelper.getTransactionsByTimeAndAccount(mAccountId.length == arAccounts.size() ? null : mAccountId, calStartOfWeek, calStartOfNextWeek);

        double thisWeekIncome = 0.0, thisWeekExpense = 0.0;

        for (Transaction tran : aTransactionThisWeek) {
            if (tran.getTransactionType() == Transaction.TransactionEnum.Expense.getValue()) {
                thisWeekExpense += tran.getAmount();
            } else if (tran.getTransactionType() == Transaction.TransactionEnum.Income.getValue()) {
                thisWeekIncome += tran.getAmount();
            } else if (tran.getTransactionType() == Transaction.TransactionEnum.Adjustment.getValue()) {
                if(tran.getFromAccountId() != 0) {
                    thisWeekExpense += tran.getAmount();
                } else if(tran.getToAccountId() != 0) {
                    thisWeekIncome += tran.getAmount();
                }
            }
        }

        tvThisWeek.setText(String.format(getResources().getString(R.string.report_evi_this_week),
                calStartOfWeek.get(Calendar.DATE),
                calStartOfWeek.get(Calendar.MONTH) + 1,
                calStartOfNextWeek.get(Calendar.DATE),
                calStartOfNextWeek.get(Calendar.MONTH) + 1));

        sbThisWeekExpense.setMax(thisWeekIncome > thisWeekExpense ? (int) thisWeekIncome : (int) thisWeekExpense);
        sbThisWeekExpense.setProgress((int) thisWeekExpense);
        tvThisWeekExpense.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), thisWeekExpense));
        sbThisWeekIncome.setMax(thisWeekIncome > thisWeekExpense ? (int) thisWeekIncome : (int) thisWeekExpense);
        sbThisWeekIncome.setProgress((int) thisWeekIncome);
        tvThisWeekIncome.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), thisWeekIncome));
        //endregion

        //region Todo: Update View This Month
        Calendar calStartOfMonth = Calendar.getInstance();
        calStartOfMonth.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        calStartOfMonth.clear(Calendar.MINUTE);
        calStartOfMonth.clear(Calendar.SECOND);
        calStartOfMonth.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        calStartOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        Calendar calStartOfNextMonth = Calendar.getInstance();
        calStartOfNextMonth.setTimeInMillis(calStartOfMonth.getTimeInMillis());
        calStartOfNextMonth.add(Calendar.MONTH, 1);

        List<Transaction> aTransactionThisMonth = mDbHelper.getTransactionsByTimeAndAccount(mAccountId.length == arAccounts.size() ? null : mAccountId, calStartOfMonth, calStartOfNextMonth);

        double thisMonthIncome = 0.0, thisMonthExpense = 0.0;

        for (Transaction tran : aTransactionThisMonth) {
            if (tran.getTransactionType() == Transaction.TransactionEnum.Expense.getValue()) {
                thisMonthExpense += tran.getAmount();
            } else if (tran.getTransactionType() == Transaction.TransactionEnum.Income.getValue()) {
                thisMonthIncome += tran.getAmount();
            } else if (tran.getTransactionType() == Transaction.TransactionEnum.Adjustment.getValue()) {
                if(tran.getFromAccountId() != 0) {
                    thisMonthExpense += tran.getAmount();
                } else if(tran.getToAccountId() != 0) {
                    thisMonthIncome += tran.getAmount();
                }
            }
        }

        tvThisMonth.setText(String.format(getResources().getString(R.string.report_evi_this_month),
                                            calStartOfMonth.get(Calendar.MONTH) + 1,
                                            calStartOfMonth.get(Calendar.YEAR)));

        sbThisMonthExpense.setMax(thisMonthIncome > thisMonthExpense ? (int) thisMonthIncome : (int) thisMonthExpense);
        sbThisMonthExpense.setProgress((int) thisMonthExpense);
        tvThisMonthExpense.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), thisMonthExpense));
        sbThisMonthIncome.setMax(thisMonthIncome > thisMonthExpense ? (int) thisMonthIncome : (int) thisMonthExpense);
        sbThisMonthIncome.setProgress((int) thisMonthIncome);
        tvThisMonthIncome.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), thisMonthIncome));
        //endregion

        //region Todo: Update View This Year
        Calendar calStartOfYear = Calendar.getInstance();
        calStartOfYear.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        calStartOfYear.clear(Calendar.MINUTE);
        calStartOfYear.clear(Calendar.SECOND);
        calStartOfYear.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        calStartOfYear.set(Calendar.MONTH, 1);
        calStartOfYear.set(Calendar.DAY_OF_MONTH, 1);

        Calendar calStartOfNextYear = Calendar.getInstance();
        calStartOfNextYear.setTimeInMillis(calStartOfYear.getTimeInMillis());
        calStartOfNextYear.add(Calendar.YEAR, 1);

        List<Transaction> aTransactionThisYear = mDbHelper.getTransactionsByTimeAndAccount(mAccountId.length == arAccounts.size() ? null : mAccountId, calStartOfYear, calStartOfNextYear);

        double thisYearIncome = 0.0, thisYearExpense = 0.0;

        for (Transaction tran : aTransactionThisYear) {
            if (tran.getTransactionType() == Transaction.TransactionEnum.Expense.getValue()) {
                thisYearExpense += tran.getAmount();
            } else if (tran.getTransactionType() == Transaction.TransactionEnum.Income.getValue()) {
                thisYearIncome += tran.getAmount();
            } else if (tran.getTransactionType() == Transaction.TransactionEnum.Adjustment.getValue()) {
                if(tran.getFromAccountId() != 0) {
                    thisYearExpense += tran.getAmount();
                } else if(tran.getToAccountId() != 0) {
                    thisYearIncome += tran.getAmount();
                }
            }
        }

        tvThisYear.setText(String.format(getResources().getString(R.string.report_evi_this_year), calStartOfMonth.get(Calendar.YEAR)));

        sbThisYearExpense.setMax(thisYearIncome > thisYearExpense ? (int) thisYearIncome : (int) thisYearExpense);
        sbThisYearExpense.setProgress((int) thisYearExpense);
        tvThisYearExpense.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), thisYearExpense));
        sbThisYearIncome.setMax(thisYearIncome > thisYearExpense ? (int) thisYearIncome : (int) thisYearExpense);
        sbThisYearIncome.setProgress((int) thisYearIncome);
        tvThisYearIncome.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), thisYearIncome));
        //endregion

        //region Todo: Update View All
        List<Transaction> aTransactions = mDbHelper.getTransactionsByTimeAndAccount(mAccountId.length == arAccounts.size() ? null : mAccountId, null, null);

        double income = 0.0, expense = 0.0;

        for (Transaction tran : aTransactions) {
            if (tran.getTransactionType() == Transaction.TransactionEnum.Expense.getValue()) {
                expense += tran.getAmount();
            } else if (tran.getTransactionType() == Transaction.TransactionEnum.Income.getValue()) {
                income += tran.getAmount();
            } else if (tran.getTransactionType() == Transaction.TransactionEnum.Adjustment.getValue()) {
                if(tran.getFromAccountId() != 0) {
                    expense += tran.getAmount();
                } else if(tran.getToAccountId() != 0) {
                    income += tran.getAmount();
                }
            }
        }

        sbExpense.setMax(income > expense ? (int) income : (int) expense);
        sbExpense.setProgress((int) expense);
        tvExpense.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), expense));
        sbIncome.setMax(income > expense ? (int) income : (int) expense);
        sbIncome.setProgress((int) income);
        tvIncome.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), income));
        tvNetIncome.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), income - expense));
        //endregion

        llEVI.addView(view);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Start Fragment ReportEvent
     */
    private void showListAccounts() {
        LogUtils.logEnterFunction(Tag, null);
        FragmentReportEVIAccount nextFrag = new FragmentReportEVIAccount();
        Bundle bundle = new Bundle();
        bundle.putIntArray("Accounts", mAccountId);
        nextFrag.setArguments(bundle);
        FragmentReportEVI.this.getFragmentManager().beginTransaction()
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

        updateListEviCurrent();

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
        FragmentReportEVI.this.getFragmentManager().beginTransaction()
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

        updateListEviCurrent();

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

        LogUtils.logEnterFunction(Tag, "(" + strFromDate + " - " +  strToDate + ")");

        mFromDate   = fromDate;
        mToDate     = toDate;
        mTime       = getResources().getStringArray(R.array.report_evi_ar_viewedby).length - 1;

        tvViewedBy.setText(strFromDate + " - " + strToDate);

        LogUtils.logLeaveFunction(Tag, "(" + strFromDate + " - " +  strToDate + ")", null);
    } // End updateTime

} // End class ReportEVI
