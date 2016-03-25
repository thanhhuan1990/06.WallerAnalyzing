package local.wallet.analyzing;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.AccountType;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/22/2016.
 */
public class FragmentReportEVI extends Fragment implements View.OnClickListener {
    public static final String Tag = "ReportEVI";

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
        if(arTransactions.size() > 0) {
            mFromDate   = arTransactions.get(arTransactions.size() - 1).getTime();
            mToDate     = arTransactions.get(0).getTime();

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
        } else {
            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Error_Startup_No_Data));
            ((ActivityMain) getActivity()).setCurrentVisibleItem(ActivityMain.TAB_POSITION_TRANSACTIONS);
        }

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
        View            view                = mInflater.inflate(R.layout.layout_fragment_report_evi_detail_current, null);
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
        TextView        tvNoOfMonthDay      = (TextView)        view.findViewById(R.id.tvNoOfMonthDay);
        TextView        tvThisMonth         = (TextView)        view.findViewById(R.id.tvThisMonth);
        SeekBar         sbThisMonthIncome   = (SeekBar)         view.findViewById(R.id.sbThisMonthIncome);
        SeekBar         sbThisMonthExpense  = (SeekBar)         view.findViewById(R.id.sbThisMonthExpense);
        TextView        tvThisMonthIncome   = (TextView)        view.findViewById(R.id.tvThisMonthIncome);
        TextView        tvThisMonthExpense  = (TextView)        view.findViewById(R.id.tvThisMonthExpense);
        LinearLayout    llThisYear          = (LinearLayout)    view.findViewById(R.id.llThisYear);
        TextView        tvNoOfYearDay       = (TextView)        view.findViewById(R.id.tvNoOfYearDay);
        TextView        tvThisYear          = (TextView)        view.findViewById(R.id.tvThisYear);
        SeekBar         sbThisYearIncome    = (SeekBar)         view.findViewById(R.id.sbThisYearIncome);
        SeekBar         sbThisYearExpense   = (SeekBar)         view.findViewById(R.id.sbThisYearExpense);
        TextView        tvThisYearIncome    = (TextView)        view.findViewById(R.id.tvThisYearIncome);
        TextView        tvThisYearExpense   = (TextView)        view.findViewById(R.id.tvThisYearExpense);
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
        final Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        today.clear(Calendar.MINUTE);
        today.clear(Calendar.SECOND);
        today.clear(Calendar.MILLISECOND);

        final Calendar tomorrow = Calendar.getInstance();
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

        llToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentReportEVITransactions nextFrag = new FragmentReportEVITransactions();
                Bundle bundle = new Bundle();
                bundle.putIntArray("Accounts", mAccountId);
                bundle.putLong("FromDate", today.getTimeInMillis());
                bundle.putLong("ToDate", tomorrow.getTimeInMillis());
                nextFrag.setArguments(bundle);
                FragmentReportEVI.this.getFragmentManager().beginTransaction()
                        .add(R.id.ll_report, nextFrag, FragmentReportEVITransactions.Tag)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // endregion

        //region Todo: Update View This Week
        final Calendar calStartOfWeek = Calendar.getInstance();
        calStartOfWeek.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        calStartOfWeek.clear(Calendar.MINUTE);
        calStartOfWeek.clear(Calendar.SECOND);
        calStartOfWeek.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        calStartOfWeek.set(Calendar.DAY_OF_WEEK, calStartOfWeek.getFirstDayOfWeek());

        final Calendar calStartOfNextWeek = Calendar.getInstance();
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

        llThisWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentReportEVITransactions nextFrag = new FragmentReportEVITransactions();
                Bundle bundle = new Bundle();
                bundle.putIntArray("Accounts", mAccountId);
                bundle.putLong("FromDate", calStartOfWeek.getTimeInMillis());
                bundle.putLong("ToDate", calStartOfNextWeek.getTimeInMillis());
                nextFrag.setArguments(bundle);
                FragmentReportEVI.this.getFragmentManager().beginTransaction()
                        .add(R.id.ll_report, nextFrag, FragmentReportEVITransactions.Tag)
                        .addToBackStack(null)
                        .commit();
            }
        });
        //endregion

        //region Todo: Update View This Month
        final Calendar calStartOfMonth = Calendar.getInstance();
        calStartOfMonth.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        calStartOfMonth.clear(Calendar.MINUTE);
        calStartOfMonth.clear(Calendar.SECOND);
        calStartOfMonth.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        calStartOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        final Calendar calStartOfNextMonth = Calendar.getInstance();
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

        tvNoOfMonthDay.setText(Integer.toString(calStartOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)));
        tvThisMonth.setText(String.format(getResources().getString(R.string.report_evi_this_month),
                                            calStartOfMonth.get(Calendar.MONTH) + 1,
                                            calStartOfMonth.get(Calendar.YEAR)));

        sbThisMonthExpense.setMax(thisMonthIncome > thisMonthExpense ? (int) thisMonthIncome : (int) thisMonthExpense);
        sbThisMonthExpense.setProgress((int) thisMonthExpense);
        tvThisMonthExpense.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), thisMonthExpense));
        sbThisMonthIncome.setMax(thisMonthIncome > thisMonthExpense ? (int) thisMonthIncome : (int) thisMonthExpense);
        sbThisMonthIncome.setProgress((int) thisMonthIncome);
        tvThisMonthIncome.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), thisMonthIncome));

        llThisMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentReportEVITransactions nextFrag = new FragmentReportEVITransactions();
                Bundle bundle = new Bundle();
                bundle.putIntArray("Accounts", mAccountId);
                bundle.putLong("FromDate", calStartOfMonth.getTimeInMillis());
                bundle.putLong("ToDate", calStartOfNextMonth.getTimeInMillis());
                nextFrag.setArguments(bundle);
                FragmentReportEVI.this.getFragmentManager().beginTransaction()
                        .add(R.id.ll_report, nextFrag, FragmentReportEVITransactions.Tag)
                        .addToBackStack(null)
                        .commit();
            }
        });
        //endregion

        //region Todo: Update View This Year
        final Calendar calStartOfYear = Calendar.getInstance();
        calStartOfYear.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        calStartOfYear.clear(Calendar.MINUTE);
        calStartOfYear.clear(Calendar.SECOND);
        calStartOfYear.clear(Calendar.MILLISECOND);

        // get start of this year in milliseconds
        calStartOfYear.set(Calendar.MONTH, 0);
        calStartOfYear.set(Calendar.DAY_OF_MONTH, 1);

        final Calendar calStartOfNextYear = Calendar.getInstance();
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

        tvNoOfYearDay.setText(Integer.toString(calStartOfMonth.getActualMaximum(Calendar.DAY_OF_YEAR)));
        tvThisYear.setText(String.format(getResources().getString(R.string.report_evi_this_year), calStartOfMonth.get(Calendar.YEAR)));

        sbThisYearExpense.setMax(thisYearIncome > thisYearExpense ? (int) thisYearIncome : (int) thisYearExpense);
        sbThisYearExpense.setProgress((int) thisYearExpense);
        tvThisYearExpense.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), thisYearExpense));
        sbThisYearIncome.setMax(thisYearIncome > thisYearExpense ? (int) thisYearIncome : (int) thisYearExpense);
        sbThisYearIncome.setProgress((int) thisYearIncome);
        tvThisYearIncome.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), thisYearIncome));

        llThisYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentReportEVITransactions nextFrag = new FragmentReportEVITransactions();
                Bundle bundle = new Bundle();
                bundle.putIntArray("Accounts", mAccountId);
                bundle.putLong("FromDate", calStartOfYear.getTimeInMillis());
                bundle.putLong("ToDate", calStartOfNextYear.getTimeInMillis());
                nextFrag.setArguments(bundle);
                FragmentReportEVI.this.getFragmentManager().beginTransaction()
                        .add(R.id.ll_report, nextFrag, FragmentReportEVITransactions.Tag)
                        .addToBackStack(null)
                        .commit();
            }
        });
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
     * Show list of Expense vs Income viewed by Monthly
     */
    private void updateListEviMonthly() {
        LogUtils.logEnterFunction(Tag, null);
        llEVI.removeAllViews();

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View            view        = mInflater.inflate(R.layout.layout_fragment_report_evi_detail_list, null);
        LinearLayout    llMain      = (LinearLayout) view.findViewById(R.id.ll_main);

        List<Transaction> arAllTransactions = mDbHelper.getAllTransactions();

        // firstDayOfYear = 01/01 of year of last transaction
        Calendar firstDayOfYearTemp = Calendar.getInstance();
        firstDayOfYearTemp.set(Calendar.YEAR, arAllTransactions.get(0).getTime().get(Calendar.YEAR));
        firstDayOfYearTemp.set(Calendar.MONTH, 0);
        firstDayOfYearTemp.set(Calendar.DATE, 1);
        firstDayOfYearTemp.set(Calendar.HOUR_OF_DAY, 0);
        firstDayOfYearTemp.clear(Calendar.MINUTE);
        firstDayOfYearTemp.clear(Calendar.SECOND);
        firstDayOfYearTemp.clear(Calendar.MILLISECOND);

        Calendar firstDayOfYear = Calendar.getInstance();
        firstDayOfYear.setTimeInMillis(firstDayOfYearTemp.getTimeInMillis());

        //region get MAX
        double max = getMax(1);

        // Reset value of firstDayOfYear
        firstDayOfYear.setTimeInMillis(firstDayOfYearTemp.getTimeInMillis());
        while(firstDayOfYear.get(Calendar.YEAR) >= arAllTransactions.get(arAllTransactions.size() - 1).getTime().get(Calendar.YEAR)) {
            View            monthlyYear     = mInflater.inflate(R.layout.listview_item_evi_year_data, null);
            TextView        tvYear          = (TextView) monthlyYear.findViewById(R.id.tvYear);
            tvYear.setText(String.format(getResources().getString(R.string.report_evi_year), firstDayOfYear.get(Calendar.YEAR)));
            LinearLayout    ll_year_data    = (LinearLayout) monthlyYear.findViewById(R.id.ll_year_data);

            final Calendar startDate  = Calendar.getInstance();
            // StartDate == 01/12 of current year
            startDate.setTimeInMillis(firstDayOfYear.getTimeInMillis());
            startDate.set(Calendar.MONTH, 11);
            startDate.set(Calendar.DAY_OF_MONTH, 1);
            // EndDate == 01/01 of next year
            final Calendar endDate   = Calendar.getInstance();
            endDate.setTimeInMillis(firstDayOfYear.getTimeInMillis());
            endDate.add(Calendar.YEAR, 1);

            while(startDate.get(Calendar.YEAR) == firstDayOfYear.get(Calendar.YEAR)) {
                List<Transaction> arTransactionOfMonth = mDbHelper.getTransactionsByTimeAndAccount(mAccountId.length != mDbHelper.getAccountCount() ? mAccountId : null, startDate, endDate);

                if(arTransactionOfMonth.size() > 0) {

                    double income = 0.0, expense = 0.0;
                    for(Transaction tran : arTransactionOfMonth) {
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
                    View        month       = mInflater.inflate(R.layout.listview_item_evi_monthly, null);
                    TextView    tvMonth     = (TextView) month.findViewById(R.id.tvMonth);
                    tvMonth.setText("" + (startDate.get(Calendar.MONTH) + 1));
                    TextView    tvBalance   = (TextView) month.findViewById(R.id.tvBalance);
                    tvBalance.setText(String.format(getResources().getString(R.string.report_evi_monthly_balance),
                            Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), (income - expense))));
                    SeekBar     sbIncome    = (SeekBar) month.findViewById(R.id.sbIncome);
                    sbIncome.setEnabled(false);
                    sbIncome.setMax((int) max);
                    sbIncome.setProgress((int) income);
                    TextView    tvIncome    = (TextView) month.findViewById(R.id.tvIncome);
                    tvIncome.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), income));
                    SeekBar     sbExpense   = (SeekBar) month.findViewById(R.id.sbExpense);
                    sbExpense.setEnabled(false);
                    sbExpense.setMax((int) max);
                    sbExpense.setProgress((int) expense);
                    TextView    tvExpense   = (TextView) month.findViewById(R.id.tvExpense);
                    tvExpense.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), expense));

                    final long fStartDate = startDate.getTimeInMillis();
                    final long fEndDate = endDate.getTimeInMillis();
                    month.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentReportEVITransactions nextFrag = new FragmentReportEVITransactions();
                            Bundle bundle = new Bundle();
                            bundle.putIntArray("Accounts", mAccountId);
                            bundle.putLong("FromDate", fStartDate);
                            bundle.putLong("ToDate", fEndDate);
                            nextFrag.setArguments(bundle);
                            FragmentReportEVI.this.getFragmentManager().beginTransaction()
                                    .add(R.id.ll_report, nextFrag, FragmentReportEVITransactions.Tag)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });

                    ll_year_data.addView(month);
                }

                startDate.add(Calendar.MONTH, -1);
                endDate.add(Calendar.MONTH, -1);

            }

            llMain.addView(monthlyYear);
            firstDayOfYear.add(Calendar.YEAR, -1);
        }

        llEVI.addView(view);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Show list of Expense vs Income viewed by Quarterly
     */
    private void updateListEviQuarterly() {
        LogUtils.logEnterFunction(Tag, null);
        llEVI.removeAllViews();

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View            view        = mInflater.inflate(R.layout.layout_fragment_report_evi_detail_list, null);
        LinearLayout    llMain      = (LinearLayout) view.findViewById(R.id.ll_main);

        List<Transaction> arAllTransactions = mDbHelper.getAllTransactions();

        // firstDayOfYear = 01/01 of year of last transaction
        Calendar firstDayOfYear = Calendar.getInstance();
        firstDayOfYear.set(Calendar.YEAR, arAllTransactions.get(0).getTime().get(Calendar.YEAR));
        firstDayOfYear.set(Calendar.MONTH, 0);
        firstDayOfYear.set(Calendar.DATE, 1);
        firstDayOfYear.set(Calendar.HOUR_OF_DAY, 0);
        firstDayOfYear.clear(Calendar.MINUTE);
        firstDayOfYear.clear(Calendar.SECOND);
        firstDayOfYear.clear(Calendar.MILLISECOND);

        //region get MAX
        double max = getMax(3);

        //endregion
        while(firstDayOfYear.get(Calendar.YEAR) >= arAllTransactions.get(arAllTransactions.size() - 1).getTime().get(Calendar.YEAR)) {
            LogUtils.trace(Tag, "firstDayOfYear = " + firstDayOfYear.toString());
            View            monthlyYear     = mInflater.inflate(R.layout.listview_item_evi_year_data, null);
            TextView        tvYear          = (TextView) monthlyYear.findViewById(R.id.tvYear);
            tvYear.setText(String.format(getResources().getString(R.string.report_evi_year), firstDayOfYear.get(Calendar.YEAR)));
            LinearLayout    ll_year_data    = (LinearLayout) monthlyYear.findViewById(R.id.ll_year_data);

            final Calendar startDate  = Calendar.getInstance();
            // StartDate == 01/10 of year of last transaction
            startDate.setTimeInMillis(firstDayOfYear.getTimeInMillis());
            startDate.set(Calendar.MONTH, 9);
            startDate.set(Calendar.DAY_OF_MONTH, 1);
            // EndDate == 01/01 of next year of last transaction
            final Calendar endDate   = Calendar.getInstance();
            endDate.setTimeInMillis(firstDayOfYear.getTimeInMillis());
            endDate.add(Calendar.YEAR, 1);

            while(startDate.get(Calendar.YEAR) == firstDayOfYear.get(Calendar.YEAR)) {
                List<Transaction> arTransactionOfQuarter = mDbHelper.getTransactionsByTimeAndAccount(mAccountId.length != mDbHelper.getAccountCount() ? mAccountId : null, startDate, endDate);

                if(arTransactionOfQuarter.size() > 0) {

                    double income = 0.0, expense = 0.0;
                    for(Transaction tran : arTransactionOfQuarter) {
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
                    View        quarter         = mInflater.inflate(R.layout.listview_item_evi_quarterly_yearly, null);
                    TextView    tvQuarter       = (TextView) quarter.findViewById(R.id.tvQuarter);
                    tvQuarter.setText(String.format(getResources().getString(R.string.report_evi_quarterly),
                            RomanNumerals(((startDate.get(Calendar.MONTH) / 3) + 1)),
                            Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), (income - expense))));
                    SeekBar     sbIncome        = (SeekBar) quarter.findViewById(R.id.sbIncome);
                    sbIncome.setEnabled(false);
                    sbIncome.setMax((int) max);
                    sbIncome.setProgress((int)income);
                    TextView    tvIncome        = (TextView) quarter.findViewById(R.id.tvIncome);
                    tvIncome.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), income));
                    SeekBar     sbExpense       = (SeekBar) quarter.findViewById(R.id.sbExpense);
                    sbExpense.setEnabled(false);
                    sbExpense.setMax((int) max);
                    sbExpense.setProgress((int) expense);
                    TextView    tvExpense       = (TextView) quarter.findViewById(R.id.tvExpense);
                    tvExpense.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), expense));

                    final long fStartDate = startDate.getTimeInMillis();
                    final long fEndDate = endDate.getTimeInMillis();
                    quarter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentReportEVITransactions nextFrag = new FragmentReportEVITransactions();
                            Bundle bundle = new Bundle();
                            bundle.putIntArray("Accounts", mAccountId);
                            bundle.putLong("FromDate", fStartDate);
                            bundle.putLong("ToDate", fEndDate);
                            nextFrag.setArguments(bundle);
                            FragmentReportEVI.this.getFragmentManager().beginTransaction()
                                    .add(R.id.ll_report, nextFrag, FragmentReportEVITransactions.Tag)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });

                    ll_year_data.addView(quarter);
                }

                startDate.add(Calendar.MONTH, -3);
                endDate.add(Calendar.MONTH, -3);

            }

            llMain.addView(monthlyYear);
            firstDayOfYear.add(Calendar.YEAR, -1);
        }

        llEVI.addView(view);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Show list of Expense vs Income viewed by Yearly
     */
    private void updateListEviYearly() {
        LogUtils.logEnterFunction(Tag, null);
        llEVI.removeAllViews();

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View            view        = mInflater.inflate(R.layout.layout_fragment_report_evi_detail_list, null);
        LinearLayout    llMain      = (LinearLayout) view.findViewById(R.id.ll_main);

        List<Transaction> arAllTransactions = mDbHelper.getAllTransactions();

        // firstDayOfYear = 01/01 of year of last transaction
        Calendar firstDayOfYear = Calendar.getInstance();
        firstDayOfYear.set(Calendar.YEAR, arAllTransactions.get(0).getTime().get(Calendar.YEAR));
        firstDayOfYear.set(Calendar.MONTH, 0);
        firstDayOfYear.set(Calendar.DATE, 1);
        firstDayOfYear.set(Calendar.HOUR_OF_DAY, 0);
        firstDayOfYear.clear(Calendar.MINUTE);
        firstDayOfYear.clear(Calendar.SECOND);
        firstDayOfYear.clear(Calendar.MILLISECOND);

        //region Check MAX
        double max = getMax(12);
        final Calendar startDate  = Calendar.getInstance();
        // StartDate == 01/01 of year of last transaction
        startDate.setTimeInMillis(firstDayOfYear.getTimeInMillis());
        // EndDate == 01/01 of next year of last transaction
        final Calendar endDate   = Calendar.getInstance();
        endDate.setTimeInMillis(firstDayOfYear.getTimeInMillis());
        endDate.add(Calendar.YEAR, 1);

        while(startDate.get(Calendar.YEAR) >= arAllTransactions.get(arAllTransactions.size() - 1).getTime().get(Calendar.YEAR)) {
            List<Transaction> arTransactionOfYear = mDbHelper.getTransactionsByTimeAndAccount(mAccountId.length != mDbHelper.getAccountCount() ? mAccountId : null, startDate, endDate);

            if(arTransactionOfYear.size() > 0) {

                double income = 0.0, expense = 0.0;
                for(Transaction tran : arTransactionOfYear) {
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
                View        year       = mInflater.inflate(R.layout.listview_item_evi_quarterly_yearly, null);
                TextView    tvQuarter     = (TextView) year.findViewById(R.id.tvQuarter);
                tvQuarter.setText(String.format(getResources().getString(R.string.report_evi_yearly),
                        startDate.get(Calendar.YEAR),
                        Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), (income - expense))));
                SeekBar     sbIncome    = (SeekBar) year.findViewById(R.id.sbIncome);
                sbIncome.setEnabled(false);
                sbIncome.setMax((int) max);
                sbIncome.setProgress((int) income);
                TextView    tvIncome    = (TextView) year.findViewById(R.id.tvIncome);
                tvIncome.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), income));
                SeekBar     sbExpense   = (SeekBar) year.findViewById(R.id.sbExpense);
                sbExpense.setEnabled(false);
                sbExpense.setMax((int) max);
                sbExpense.setProgress((int) expense);
                TextView    tvExpense   = (TextView) year.findViewById(R.id.tvExpense);
                tvExpense.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), expense));

                final long fStartDate = startDate.getTimeInMillis();
                final long fEndDate = endDate.getTimeInMillis();
                year.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentReportEVITransactions nextFrag = new FragmentReportEVITransactions();
                        Bundle bundle = new Bundle();
                        bundle.putIntArray("Accounts", mAccountId);
                        bundle.putLong("FromDate", fStartDate);
                        bundle.putLong("ToDate", fEndDate);
                        nextFrag.setArguments(bundle);
                        FragmentReportEVI.this.getFragmentManager().beginTransaction()
                                .add(R.id.ll_report, nextFrag, FragmentReportEVITransactions.Tag)
                                .addToBackStack(null)
                                .commit();
                    }
                });

                llMain.addView(year);
            }

            startDate.add(Calendar.YEAR, -1);
            endDate.add(Calendar.YEAR, -1);

        }

        llEVI.addView(view);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Show Expense vs Income as PieChart of Period
     */
    private class CategoryEVI {
        Category    category;
        double      amount;

        public CategoryEVI(Category category, double amount) {
            this.category = category;
            this.amount = amount;
        }
    }

    private class CategoryTransaction {
        Category category;
        List<Transaction> arTransactions;
        boolean isShow;

        public CategoryTransaction(Category category, List<Transaction> arTransactions, boolean isShow) {
            this.category = category;
            this.arTransactions = arTransactions;
            this.isShow = isShow;
        }
    }

    /**
     * Update view to show data of Period
     */
    private void updateEviPeriod() {
        LogUtils.logEnterFunction(Tag, null);
        llEVI.removeAllViews();

        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        View            view        = mInflater.inflate(R.layout.layout_fragment_report_evi_detail_period, null);

        //region CHART EXPENSE
        //region get expense data from Database
        final ArrayList<CategoryEVI> arData = new ArrayList<CategoryEVI>();

        List<Category> arParentExpenseCategory = mDbHelper.getAllParentCategories(true);
        for(Category parentCate : arParentExpenseCategory) {
            List<Category> arExpenseCategory = mDbHelper.getCategoriesByParent(parentCate.getId());
            double amount = 0.0;
            for(Category cate : arExpenseCategory) {
                List<Transaction> arTransaction = mDbHelper.getTransactionsByTimeCategoryAccount(new int[]{cate.getId()}, mAccountId, mFromDate, mToDate);

                for(Transaction tran : arTransaction) {
                    amount += tran.getAmount();
                }

            }

            if(amount != 0) {
                arData.add(new CategoryEVI(parentCate, amount));
            }
        }

//        List<Category> arParentExpenseCategoryLoan = mDbHelper.getAllParentCategories(true, true);
//        for(Category parentCate : arParentExpenseCategoryLoan) {
//            List<Category> arExpenseCategory = mDbHelper.getCategoriesByParent(parentCate.getId());
//            double amount = 0.0;
//            for(Category cate : arExpenseCategory) {
//                List<Transaction> arTransaction = mDbHelper.getTransactionsByTimeCategoryAccount(new int[]{cate.getId()}, mAccountId, mFromDate, mToDate);
//
//                for(Transaction tran : arTransaction) {
//                    amount += tran.getAmount();
//                }
//
//            }
//
//            if(amount != 0) {
//                arData.add(new CategoryEVI(parentCate, amount));
//            }
//        }

        Collections.sort(arData, new Comparator<CategoryEVI>() {
            @Override
            public int compare(CategoryEVI c1, CategoryEVI c2) {
                return Double.compare(c2.amount, c1.amount);
            }
        });

        ArrayList<String> xVals = new ArrayList<String>();
        for(CategoryEVI cate : arData) {
            xVals.add(cate.category.getName());
        }

        double expenseAmount = 0.0;
        final ArrayList<Entry> yVals = new ArrayList<Entry>();
        for(int i = 0 ; i < arData.size(); i++) {
            yVals.add(new Entry((float) arData.get(i).amount, i));
            expenseAmount += arData.get(i).amount;
        }
        //endregion

        //region Setup Chart Expense
        final LinearLayout llExpense                = (LinearLayout) view.findViewById(R.id.llExpense);
        final LinearLayout  llExpenseTransactions   = (LinearLayout) view.findViewById(R.id.llExpenseTransactions);
        if(expenseAmount == 0) {
            llExpense.setVisibility(View.GONE);
        } else {
            llExpense.setVisibility(View.VISIBLE);
            final PieChart mChart = (PieChart) view.findViewById(R.id.chartExpense);
            mChart.setUsePercentValues(true);
            mChart.setDrawSliceText(false);
            mChart.setDescription("");
            mChart.setExtraOffsets(5, 10, 5, 5);

            mChart.setDragDecelerationFrictionCoef(0.95f);

            mChart.setCenterTextTypeface(Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf"));
            mChart.setCenterText(generateCenterSpannableText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), expenseAmount)));
            mChart.setDrawCenterText(true);

            mChart.setDrawHoleEnabled(true);
            mChart.setHoleColor(Color.WHITE);

            mChart.setTransparentCircleColor(Color.WHITE);
            mChart.setTransparentCircleAlpha(110);

            mChart.setHoleRadius(58f);
            mChart.setTransparentCircleRadius(61f);

            mChart.setRotationAngle(0);
            // enable rotation of the chart by touch
            mChart.setRotationEnabled(false);
            mChart.setHighlightPerTapEnabled(true);

            final double fExpenseAmount = expenseAmount;
            // add a selection listener
            mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                    mChart.setCenterText(generateCenterSpannableText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), Double.parseDouble(Float.toString(e.getVal())))));
                    mChart.invalidate();

                    List<CategoryTransaction> arCategoryTransaction = new ArrayList<CategoryTransaction>();
                    List<Transaction> arParentTransactions = mDbHelper.getTransactionsByTimeCategoryAccount(new int[]{arData.get(e.getXIndex()).category.getId()}, mAccountId, mFromDate, mToDate);
                    if(arParentTransactions.size() > 0) {
                        arCategoryTransaction.add(new CategoryTransaction(arData.get(e.getXIndex()).category, arParentTransactions, true));
                    }

                    List<Category> arChildCategory = mDbHelper.getCategoriesByParent(arData.get(e.getXIndex()).category.getId());
                    for(Category cate : arChildCategory) {

                        List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeCategoryAccount(new int[]{cate.getId()}, mAccountId, mFromDate, mToDate);

                        if(arTransactions.size() > 0) {
                            arCategoryTransaction.add(new CategoryTransaction(cate, arTransactions, true));
                        }

                    }

                    updateListTransactions(llExpenseTransactions, arCategoryTransaction);
                }

                @Override
                public void onNothingSelected() {
                    mChart.setCenterText(generateCenterSpannableText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), fExpenseAmount)));
                    mChart.invalidate();
                    llExpenseTransactions.removeAllViews();
                }
            });

            PieDataSet dataSet = new PieDataSet(yVals, "");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            // add a lot of colors
            ArrayList<Integer> colorsExpense = new ArrayList<Integer>();

            for (int c : ColorTemplate.VORDIPLOM_COLORS) {
                colorsExpense.add(c);
            }

            for (int c : ColorTemplate.JOYFUL_COLORS) {
                colorsExpense.add(c);
            }

            for (int c : ColorTemplate.COLORFUL_COLORS) {
                colorsExpense.add(c);
            }

            for (int c : ColorTemplate.LIBERTY_COLORS) {
                colorsExpense.add(c);
            }

            for (int c : ColorTemplate.PASTEL_COLORS) {
                colorsExpense.add(c);
            }

            colorsExpense.add(ColorTemplate.getHoloBlue());

            dataSet.setColors(colorsExpense);

            PieData data = new PieData(xVals, dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.GRAY);
            data.setValueTypeface(Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf"));
            mChart.setData(data);

            // undo all highlights
            mChart.highlightValues(null);

            mChart.invalidate();

            mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
            // mChart.spin(2000, 0, 360);

            Legend l = mChart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
            l.setXEntrySpace(17f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);
            //endregion region Setup Chart Expense
        }
        //endregion Setup Chart Expense
        //endregion CHART EXPENSE

        //region CHART INCOME
        //region get income data from Database
        final ArrayList<CategoryEVI> arDataIncome = new ArrayList<CategoryEVI>();

        List<Category> arIncomeCategory = mDbHelper.getAllCategories(false);
        for(Category cate : arIncomeCategory) {
            List<Transaction> arTransaction = mDbHelper.getTransactionsByTimeCategoryAccount(new int[]{cate.getId()}, mAccountId, mFromDate, mToDate);
            double cateAmount = 0.0;
            for(Transaction tran : arTransaction) {
                cateAmount += tran.getAmount();
            }

            if(cateAmount != 0) {
                arDataIncome.add(new CategoryEVI(cate, cateAmount));
            }
        }

//        List<Category> arIncomeCategoryLoan = mDbHelper.getAllCategories(false, true);
//        for(Category cate : arIncomeCategoryLoan) {
//            List<Transaction> arTransaction = mDbHelper.getTransactionsByTimeCategoryAccount(new int[]{cate.getId()}, mAccountId, mFromDate, mToDate);
//            double cateAmount = 0.0;
//            for(Transaction tran : arTransaction) {
//                cateAmount += tran.getAmount();
//            }
//
//            if(cateAmount != 0) {
//                arDataIncome.add(new CategoryEVI(cate, cateAmount));
//            }
//        }

        Collections.sort(arDataIncome, new Comparator<CategoryEVI>() {
            @Override
            public int compare(CategoryEVI c1, CategoryEVI c2) {
                return Double.compare(c2.amount, c1.amount);
            }
        });

        ArrayList<String> xValsIncome = new ArrayList<String>();
        for(CategoryEVI cate : arDataIncome) {
            xValsIncome.add(cate.category.getName());
        }

        double incomeAmount = 0.0;
        ArrayList<Entry> yValsIncome = new ArrayList<Entry>();
        for(int i = 0 ; i < arDataIncome.size(); i++) {
            yValsIncome.add(new Entry((float) arDataIncome.get(i).amount, i));
            incomeAmount += arDataIncome.get(i).amount;
        }
        //endregion

        //region Setup View Chart Income
        LinearLayout            llIncome            = (LinearLayout) view.findViewById(R.id.llIncome);
        final LinearLayout      llIncomeTransaction = (LinearLayout) view.findViewById(R.id.llIncomeTransactions);

        if(incomeAmount == 0) {
            llIncome.setVisibility(View.GONE);
        } else {
            llIncome.setVisibility(View.VISIBLE);
            final PieChart mChartIncome = (PieChart) view.findViewById(R.id.chartIncome);
            mChartIncome.setUsePercentValues(true);
            mChartIncome.setDrawSliceText(false);
            mChartIncome.setDescription("");
            mChartIncome.setExtraOffsets(5, 10, 5, 5);

            mChartIncome.setDragDecelerationFrictionCoef(0.95f);

            mChartIncome.setCenterTextTypeface(Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf"));
            mChartIncome.setCenterText(generateCenterSpannableText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), incomeAmount)));
            mChartIncome.setDrawCenterText(true);

            mChartIncome.setDrawHoleEnabled(true);
            mChartIncome.setHoleColor(Color.WHITE);

            mChartIncome.setTransparentCircleColor(Color.WHITE);
            mChartIncome.setTransparentCircleAlpha(110);

            mChartIncome.setHoleRadius(58f);
            mChartIncome.setTransparentCircleRadius(61f);

            mChartIncome.setRotationAngle(0);
            // enable rotation of the chart by touch
            mChartIncome.setRotationEnabled(false);
            mChartIncome.setHighlightPerTapEnabled(true);

            final double fIncomeAmount = incomeAmount;
            // add a selection listener
            mChartIncome.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                    mChartIncome.setCenterText(generateCenterSpannableText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), Double.parseDouble(Float.toString(e.getVal())))));
                    mChartIncome.invalidate();

                    List<CategoryTransaction> arCategoryTransaction = new ArrayList<CategoryTransaction>();
                    List<Transaction> arParentTransactions = mDbHelper.getTransactionsByTimeCategoryAccount(new int[]{arDataIncome.get(e.getXIndex()).category.getId()}, mAccountId, mFromDate, mToDate);
                    if(arParentTransactions.size() > 0) {
                        arCategoryTransaction.add(new CategoryTransaction(arDataIncome.get(e.getXIndex()).category, arParentTransactions, true));
                    }

                    List<Category> arChildCategory = mDbHelper.getCategoriesByParent(arDataIncome.get(e.getXIndex()).category.getId());
                    for(Category cate : arChildCategory) {

                        List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeCategoryAccount(new int[]{cate.getId()}, mAccountId, mFromDate, mToDate);

                        if(arTransactions.size() > 0) {
                            arCategoryTransaction.add(new CategoryTransaction(cate, arTransactions, true));
                        }

                    }

                    updateListTransactions(llIncomeTransaction, arCategoryTransaction);
                }

                @Override
                public void onNothingSelected() {
                    mChartIncome.setCenterText(generateCenterSpannableText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), fIncomeAmount)));
                    mChartIncome.invalidate();
                    llIncomeTransaction.removeAllViews();
                }
            });

            PieDataSet dataSetIncome = new PieDataSet(yValsIncome, "");
            dataSetIncome.setSliceSpace(3f);
            dataSetIncome.setSelectionShift(5f);

            // add a lot of colors
            ArrayList<Integer> colorsIncome = new ArrayList<Integer>();

            for (int c : ColorTemplate.JOYFUL_COLORS) {
                colorsIncome.add(c);
            }

            for (int c : ColorTemplate.COLORFUL_COLORS) {
                colorsIncome.add(c);
            }

            for (int c : ColorTemplate.LIBERTY_COLORS) {
                colorsIncome.add(c);
            }

            for (int c : ColorTemplate.PASTEL_COLORS) {
                colorsIncome.add(c);
            }

            for (int c : ColorTemplate.VORDIPLOM_COLORS) {
                colorsIncome.add(c);
            }

            colorsIncome.add(ColorTemplate.getHoloBlue());

            dataSetIncome.setColors(colorsIncome);

            PieData dataIncome = new PieData(xValsIncome, dataSetIncome);
            dataIncome.setValueFormatter(new PercentFormatter());
            dataIncome.setValueTextSize(11f);
            dataIncome.setValueTextColor(Color.GRAY);
            dataIncome.setValueTypeface(Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf"));
            mChartIncome.setData(dataIncome);

            // undo all highlights
            mChartIncome.highlightValues(null);

            mChartIncome.invalidate();

            mChartIncome.animateY(1400, Easing.EasingOption.EaseInOutQuad);

            Legend legendIncome = mChartIncome.getLegend();
            legendIncome.setPosition(Legend.LegendPosition.LEFT_OF_CHART_CENTER);
            legendIncome.setXEntrySpace(17f);
            legendIncome.setYEntrySpace(0f);
            legendIncome.setYOffset(0f);
            //endregion Set Data
        }
        //endregion Setup View Chart Income
        //endregion CHART INCOME

        llEVI.addView(view);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Update list Transactions
     */
    private void updateListTransactions(LinearLayout layout, List<CategoryTransaction> arTransactions) {
        LogUtils.logEnterFunction(Tag, null);
        layout.removeAllViews();

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        for(CategoryTransaction category : arTransactions) {

            View                    categoryView    = mInflater.inflate(R.layout.listview_item_transaction_follow_category, null);
            LinearLayout            llCategory      = (LinearLayout) categoryView.findViewById(R.id.llCategory);
            final ImageView ivExpand                = (ImageView) categoryView.findViewById(R.id.ivExpand);
            TextView                tvCategory      = (TextView) categoryView.findViewById(R.id.tvCategory);
            TextView                tvAmount        = (TextView) categoryView.findViewById(R.id.tvAmount);
            final LinearLayout      llTransactions  = (LinearLayout) categoryView.findViewById(R.id.llTransactions);

            final Animation expand = AnimationUtils.loadAnimation(getActivity(), R.anim.expand_no_refresh);
            expand.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    LogUtils.trace(Tag, "expand Animation");
                    llTransactions.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            final Animation shrink = AnimationUtils.loadAnimation(getActivity(), R.anim.shrink_no_refresh);
            shrink.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    LogUtils.trace(Tag, "shrink Animation");
                    llTransactions.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            llCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (llTransactions.getVisibility() == View.GONE) {
                        ivExpand.startAnimation(expand);
                    } else {
                        ivExpand.startAnimation(shrink);
                    }
                }
            });

            tvCategory.setText(String.format(getResources().getString(R.string.budget_detail_transaction_category_sum),
                    category.category.getName()));
            Double expensed = 0.0;
            for(Transaction tran : category.arTransactions) {
                expensed += tran.getAmount();
            }

            tvAmount.setText(Currency.formatCurrency(getContext(), mConfigs.getInt(Configurations.Key.Currency), expensed));

            /* Todo: Add list of transaction for category */
            for(final Transaction transaction : category.arTransactions) {
                View        transactionView     = mInflater.inflate(R.layout.listview_item_budget_transaction_detail, null);
                TextView    tvTranCategory      = (TextView) transactionView.findViewById(R.id.tvCategory);
                TextView    tvTranAmount        = (TextView) transactionView.findViewById(R.id.tvAmount);
                TextView    tvDescription       = (TextView) transactionView.findViewById(R.id.tvDescription);
                TextView    tvDate              = (TextView) transactionView.findViewById(R.id.tvDate);
                TextView    tvAccount           = (TextView) transactionView.findViewById(R.id.tvAccount);
                ImageView   ivAccountIcon       = (ImageView) transactionView.findViewById(R.id.ivAccountIcon);

                tvTranCategory.setText(String.format(getResources().getString(R.string.content_expense),
                        mDbHelper.getCategory(transaction.getCategoryId()).getName()));
                tvTranAmount.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), transaction.getAmount()));
                if(!transaction.getDescription().equals("")) {
                    tvDescription.setText(transaction.getDescription());
                } else {
                    tvDescription.setVisibility(View.GONE);
                }

                tvDate.setText(String.format(getResources().getString(R.string.format_day_month_year),
                        transaction.getTime().get(Calendar.DAY_OF_MONTH),
                        transaction.getTime().get(Calendar.MONTH) + 1,
                        transaction.getTime().get(Calendar.YEAR)));
                if(transaction.getFromAccountId() != 0) {
                    tvAccount.setText(mDbHelper.getAccount(transaction.getFromAccountId()).getName());
                    ivAccountIcon.setImageResource(AccountType.getAccountTypeById(mDbHelper.getAccount(transaction.getFromAccountId()).getTypeId()).getIcon());
                } else if(transaction.getToAccountId() != 0) {
                    tvAccount.setText(mDbHelper.getAccount(transaction.getToAccountId()).getName());
                    ivAccountIcon.setImageResource(AccountType.getAccountTypeById(mDbHelper.getAccount(transaction.getToAccountId()).getTypeId()).getIcon());
                }

                transactionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*FragmentTransactionUpdate nextFrag = new FragmentTransactionUpdate();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Transaction", transaction);
                        bundle.putInt("ContainerViewId", R.id.layout_account);
                        nextFrag.setArguments(bundle);
                        FragmentReportEVI.this.getFragmentManager().beginTransaction()
                                .add(R.id.ll_report, nextFrag, "FragmentTransactionUpdate")
                                .addToBackStack(null)
                                .commit();*/

                        FragmentTransactionCUD nextFrag = new FragmentTransactionCUD();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Transaction", transaction);
                        nextFrag.setArguments(bundle);
                        FragmentReportEVI.this.getFragmentManager().beginTransaction()
                                .add(R.id.ll_report, nextFrag, FragmentTransactionCUD.Tag)
                                .addToBackStack(null)
                                .commit();
                    }
                });

                llTransactions.addView(transactionView);
            }

            layout.addView(categoryView);
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End updateListTransactions

    private SpannableString generateCenterSpannableText(String amount) {

        SpannableString str = new SpannableString(amount);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new RelativeSizeSpan(1.7f), 0, str.length(), 0);
        str.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 0, str.length(), 0);

        return str;
    }

    /**
     * Start Fragment ReportEvent
     */
    private void showListAccounts() {
        LogUtils.logEnterFunction(Tag, null);
        FragmentReportSelectAccount nextFrag = new FragmentReportSelectAccount();
        Bundle bundle = new Bundle();
        bundle.putString("Fragment", Tag);
        bundle.putIntArray("Accounts", mAccountId);
        nextFrag.setArguments(bundle);
        FragmentReportEVI.this.getFragmentManager().beginTransaction()
                .add(R.id.ll_report, nextFrag, FragmentReportSelectAccount.Tag)
                .addToBackStack(null)
                .commit();
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Update TextView Account
     * @param accountId
     */
    public void updateAccount(int[] accountId) {
        LogUtils.logEnterFunction(Tag, "accountId = " + Arrays.toString(accountId));

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

        switch (mTime) {
            case 0:
                updateListEviCurrent();
                break;
            case 1:
                updateListEviMonthly();
                break;
            case 2:
                updateListEviQuarterly();
                break;
            case 3:
                updateListEviYearly();
                break;
            case 4:
                updateEviPeriod();
                break;
            default:
                break;
        }

        LogUtils.logLeaveFunction(Tag, "accountId = " + accountId, null);
    } // End updateAccount

    /**
     * Start Fragment ReportEVITimeSelect
     */
    private void showListTime() {
        LogUtils.logEnterFunction(Tag, null);
        FragmentReportEVISelectTime nextFrag = new FragmentReportEVISelectTime();
        Bundle bundle = new Bundle();
        bundle.putInt("Time", mTime);
        bundle.putLong("FromDate", mFromDate.getTimeInMillis());
        bundle.putLong("ToDate", mToDate.getTimeInMillis());
        nextFrag.setArguments(bundle);
        FragmentReportEVI.this.getFragmentManager().beginTransaction()
                .add(R.id.ll_report, nextFrag, FragmentReportEVISelectTime.Tag)
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
                updateListEviCurrent();
                break;
            case 1:
                updateListEviMonthly();
                break;
            case 2:
                updateListEviQuarterly();
                break;
            case 3:
                updateListEviYearly();
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

        updateEviPeriod();

        LogUtils.logLeaveFunction(Tag, "(" + strFromDate + " - " +  strToDate + ")", null);
    } // End updateTime

    /**
     * get MAX of Amount in exactly Month
     * @param noOfMonth: 1(Month) || 3(Quarter) || 12(Year)
     * @return double MAX
     */
    private double getMax(int noOfMonth) {

        List<Transaction> arAllTransactions = mDbHelper.getAllTransactions();

        // firstDayOfYear = 01/01 of year of last transaction
        Calendar firstDayOfYear = Calendar.getInstance();
        firstDayOfYear.set(Calendar.YEAR, arAllTransactions.get(0).getTime().get(Calendar.YEAR));
        firstDayOfYear.set(Calendar.MONTH, 0);
        firstDayOfYear.set(Calendar.DATE, 1);
        firstDayOfYear.set(Calendar.HOUR_OF_DAY, 0);
        firstDayOfYear.clear(Calendar.MINUTE);
        firstDayOfYear.clear(Calendar.SECOND);
        firstDayOfYear.clear(Calendar.MILLISECOND);

        double max = 0.0;
        while(firstDayOfYear.get(Calendar.YEAR) >= arAllTransactions.get(arAllTransactions.size() - 1).getTime().get(Calendar.YEAR)) {
            Calendar startDate  = Calendar.getInstance();
            startDate.setTimeInMillis(firstDayOfYear.getTimeInMillis());
            startDate.set(Calendar.MONTH, 12 - noOfMonth);
            startDate.set(Calendar.DAY_OF_MONTH, 1);
            Calendar endDate   = Calendar.getInstance();
            endDate.setTimeInMillis(firstDayOfYear.getTimeInMillis());
            endDate.add(Calendar.YEAR, 1);

            while(startDate.get(Calendar.YEAR) == firstDayOfYear.get(Calendar.YEAR)) {
                List<Transaction> arTransaction = mDbHelper.getTransactionsByTimeAndAccount(mAccountId.length != mDbHelper.getAccountCount() ? mAccountId : null, startDate, endDate);

                if(arTransaction.size() > 0) {

                    double income = 0.0, expense = 0.0;
                    for(Transaction tran : arTransaction) {
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

                    if(income > max) {
                        max = income;
                    }

                    if(expense > max) {
                        max = expense;
                    }
                }

                startDate.add(Calendar.MONTH, noOfMonth * -1);
                endDate.add(Calendar.MONTH, noOfMonth * -1);

            }

            firstDayOfYear.add(Calendar.YEAR, -1);
        }

        return max;
    }

    public static String RomanNumerals(int Int) {
        LinkedHashMap<String, Integer> roman_numerals = new LinkedHashMap<String, Integer>();
        roman_numerals.put("M", 1000);
        roman_numerals.put("CM", 900);
        roman_numerals.put("D", 500);
        roman_numerals.put("CD", 400);
        roman_numerals.put("C", 100);
        roman_numerals.put("XC", 90);
        roman_numerals.put("L", 50);
        roman_numerals.put("XL", 40);
        roman_numerals.put("X", 10);
        roman_numerals.put("IX", 9);
        roman_numerals.put("V", 5);
        roman_numerals.put("IV", 4);
        roman_numerals.put("I", 1);
        String res = "";
        for(Map.Entry<String, Integer> entry : roman_numerals.entrySet()){
            int matches = Int/entry.getValue();
            res += repeat(entry.getKey(), matches);
            Int = Int % entry.getValue();
        }
        return res;
    }
    public static String repeat(String s, int n) {
        if(s == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for(int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

} // End class ReportEVI
