package local.wallet.analyzing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Budget;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/25/2016.
 */
public class FragmentBudgetDetail extends Fragment {

    private static final String Tag = "BudgetDetail";

    private DatabaseHelper  mDbHelper;
    private Configurations  mConfigs;
    private Budget          mBudget;

    private TextView        tvDescription;
    private TextView        tvName;
    private TextView        tvDate;
    private TextView        tvAmount;
    private TextView        tvIncremental;
    private TextView        tvExpensed;
    private TextView        tvBalance;
    private SeekBar         sbExpensed;
    private LinearLayout    llHistory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle           = this.getArguments();
        mBudget                 = (Budget)bundle.get("Budget");

        LogUtils.trace(Tag, mBudget.toString());

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_budget_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mDbHelper       = new DatabaseHelper(getActivity());
        mConfigs        = new Configurations(getActivity());

        tvDescription   = (TextView) getView().findViewById(R.id.tvDescription);
        tvName          = (TextView) getView().findViewById(R.id.tvName);
        tvAmount        = (TextView) getView().findViewById(R.id.tvAmount);
        tvIncremental   = (TextView) getView().findViewById(R.id.tvIncremental);
        tvDate          = (TextView) getView().findViewById(R.id.tvDate);
        tvExpensed      = (TextView) getView().findViewById(R.id.tvExpensed);
        tvBalance       = (TextView) getView().findViewById(R.id.tvBalance);
        sbExpensed      = (SeekBar) getView().findViewById(R.id.sbExpensed);
        sbExpensed.setEnabled(false);
        llHistory       = (LinearLayout) getView().findViewById(R.id.llHistory);
        if(mBudget.getRepeatType() == 0) {
            llHistory.setVisibility(View.GONE);
        }
        llHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBudget.getRepeatType() != 0) {
                    FragmentBudgetHistory nextFrag = new FragmentBudgetHistory();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Budget", mBudget);
                    nextFrag.setArguments(bundle);
                    FragmentBudgetDetail.this.getFragmentManager().beginTransaction()
                            .add(R.id.layout_budget, nextFrag, "FragmentBudgetHistory")
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(((ActivityMain) getActivity()).getCurrentVisibleItem() != ActivityMain.TAB_POSITION_LIST_BUDGET) {
            return;
        }
        LogUtils.logEnterFunction(Tag, null);
        super.onCreateOptionsMenu(menu, inflater);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView    = mInflater.inflate(R.layout.action_bar_with_button_update, null);
        TextView tvTitle    = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(mBudget.getName());

        ImageView ivUpdate  = (ImageView) mCustomView.findViewById(R.id.ivUpdate);
        ivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.trace(Tag, "Click Menu Action Update Budget.");
                FragmentBudgetCreateUpdateDelete nextFrag = new FragmentBudgetCreateUpdateDelete();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Budget", mBudget);
                nextFrag.setArguments(bundle);
                FragmentBudgetDetail.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_budget, nextFrag, "FragmentBudgetCreateUpdateDelete")
                        .addToBackStack(null)
                        .commit();
            }
        });

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        setViewData();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private void setViewData() {
        LogUtils.logEnterFunction(Tag, null);
        if(mBudget == null) {
            LogUtils.trace(Tag, "mBudget is NULL");
            return;
        }

        if(mDbHelper.getBudget(mBudget.getId()) == null) {
            getFragmentManager().popBackStackImmediate();
            return;
        }

        mBudget = mDbHelper.getBudget(mBudget.getId());

        String[] repeatTypes = getResources().getStringArray(R.array.budget_repeat_type);
        if(mBudget.getCategories().length == 1) {
            tvDescription.setText(String.format(getResources().getString(R.string.budget_detail_description),
                                                Currency.formatCurrency(getContext(), Currency.getCurrencyById(mBudget.getCurrency()), mBudget.getAmount()),
                                                repeatTypes[mBudget.getRepeatType()],
                                                mDbHelper.getCategory(mBudget.getCategories()[0]).getName()));
        } else if(mBudget.getCategories().length == mDbHelper.getAllCategories(true, false).size()){
            tvDescription.setText(String.format(getResources().getString(R.string.budget_detail_description_all),
                                    Currency.formatCurrency(getContext(), Currency.getCurrencyById(mBudget.getCurrency()), mBudget.getAmount()),
                                    repeatTypes[mBudget.getRepeatType()]));
        } else {
            String categories = mDbHelper.getCategory(mBudget.getCategories()[0]).getName();

            for(int i = 1; i < mBudget.getCategories().length; i++) {
                categories += ", " + mDbHelper.getCategory(mBudget.getCategories()[i]).getName();
            }

            tvDescription.setText(String.format(getResources().getString(R.string.budget_detail_description_many),
                    Currency.formatCurrency(getContext(), Currency.getCurrencyById(mBudget.getCurrency()), mBudget.getAmount()),
                    repeatTypes[mBudget.getRepeatType()],
                    categories));

        }
        tvName.setText(mBudget.getName());
        tvAmount.setText(String.format(getResources().getString(R.string.budget_item_total),
                Currency.formatCurrency(getContext(), Currency.getCurrencyById(mBudget.getCurrency()), mBudget.getAmount())));

        double incremental = 0.0;

        String date = "";
        Calendar today      = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, today.getActualMinimum(Calendar.HOUR_OF_DAY));
        today.set(Calendar.MINUTE,      today.getActualMinimum(Calendar.MINUTE));
        today.set(Calendar.SECOND,      today.getActualMinimum(Calendar.SECOND));
        today.set(Calendar.MILLISECOND, today.getActualMinimum(Calendar.MILLISECOND));

        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(mBudget.getStartDate().getTimeInMillis());
        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(mBudget.getStartDate().getTimeInMillis());

        int repeatType      = mBudget.getRepeatType();

        switch (repeatType) {
            case 0: {// No repeat
                endDate.setTimeInMillis(mBudget.getEndDate().getTimeInMillis());
                break;
            }
            case 1: {// daily
                while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                    endDate.add(Calendar.DAY_OF_YEAR, 1);

                    if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                        if(mBudget.isIncremental()) {
                            List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(mBudget.getCategories(), startDate, endDate, 0);

                            Double expensed = 0.0;
                            for(Transaction tran : arTransactions) {
                                expensed += tran.getAmount();
                            }

                            incremental += (mBudget.getAmount() - expensed);
                        }
                        startDate.add(Calendar.DAY_OF_YEAR, 1);
                    }
                }

                endDate.add(Calendar.DAY_OF_YEAR, -1);
                break;
            }
            case 2: { // weekly

                while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                    endDate.add(Calendar.WEEK_OF_MONTH, 1);

                    if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                        if(mBudget.isIncremental()) {
                            List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(mBudget.getCategories(), startDate, endDate, 0);

                            Double expensed = 0.0;
                            for(Transaction tran : arTransactions) {
                                expensed += tran.getAmount();
                            }

                            incremental += (mBudget.getAmount() - expensed);
                        }
                        startDate.add(Calendar.WEEK_OF_MONTH, 1);
                    }
                }

                endDate.add(Calendar.DAY_OF_YEAR, -1);
                break;
            }
            case 3: { // monthly
                while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                    endDate.add(Calendar.MONTH, 1);

                    if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                        if(mBudget.isIncremental()) {
                            List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(mBudget.getCategories(), startDate, endDate, 0);

                            Double expensed = 0.0;
                            for(Transaction tran : arTransactions) {
                                expensed += tran.getAmount();
                            }

                            incremental += (mBudget.getAmount() - expensed);
                        }
                        startDate.add(Calendar.MONTH, 1);
                    }
                }

                endDate.add(Calendar.DAY_OF_YEAR, -1);
                break;
            }
            case 4: { // quarterly
                while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                    endDate.add(Calendar.MONTH, 3);

                    if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                        if(mBudget.isIncremental()) {
                            List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(mBudget.getCategories(), startDate, endDate, 0);

                            Double expensed = 0.0;
                            for(Transaction tran : arTransactions) {
                                expensed += tran.getAmount();
                            }

                            incremental += (mBudget.getAmount() - expensed);
                        }
                        startDate.add(Calendar.MONTH, 3);
                    }
                }

                endDate.add(Calendar.DAY_OF_YEAR, -1);
                break;
            }
            case 5: { // yearly
                while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                    endDate.add(Calendar.YEAR, 1);

                    if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                        if(mBudget.isIncremental()) {
                            List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(mBudget.getCategories(), startDate, endDate, 0);

                            Double expensed = 0.0;
                            for(Transaction tran : arTransactions) {
                                expensed += tran.getAmount();
                            }

                            incremental += (mBudget.getAmount() - expensed);
                        }
                        startDate.add(Calendar.YEAR, 1);
                    }
                }

                endDate.add(Calendar.DAY_OF_YEAR, -1);
                break;
            }
            default:
                break;
        } // end switch

        if(startDate.getTimeInMillis() == endDate.getTimeInMillis()) {
            tvDate.setText(String.format(getResources().getString(R.string.format_budget_day_month_year),
                    startDate.get(Calendar.DAY_OF_MONTH),
                    startDate.get(Calendar.MONTH) + 1,
                    endDate.get(Calendar.YEAR)));
        } else {
            tvDate.setText(String.format(getResources().getString(R.string.format_budget_date),
                    startDate.get(Calendar.DAY_OF_MONTH),
                    startDate.get(Calendar.MONTH) + 1,
                    endDate.get(Calendar.DAY_OF_MONTH),
                    endDate.get(Calendar.MONTH) + 1));
        }

        if(incremental == 0) {
            tvIncremental.setVisibility(View.GONE);
        } else {
            tvIncremental.setVisibility(View.VISIBLE);
            tvIncremental.setText(String.format(getResources().getString(R.string.budget_item_incremental),
                    Currency.formatCurrency(getContext(), Currency.getCurrencyById(mBudget.getCurrency()), incremental)));

            if(incremental > 0) {
                tvIncremental.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                tvIncremental.setTextColor(getResources().getColor(R.color.budget_background_progress_over));
            }
        }

        Double amount = mBudget.getAmount() + incremental;

        List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(mBudget.getCategories(), startDate, endDate, 1);

        Double expensed = 0.0;
        for(Transaction tran : arTransactions) {
            expensed += tran.getAmount();
        }

        tvExpensed.setText(Currency.formatCurrency(getContext(), Currency.getCurrencyById(mBudget.getCurrency()), expensed));

        sbExpensed.setMax(amount.intValue());
        // Set date
        double numOfDaysOne = (double)getDays(startDate, today);
        double numOfDaysTwo = (double)getDays(startDate, endDate);
        int progress = (int)((numOfDaysOne / numOfDaysTwo) * sbExpensed.getMax());
        sbExpensed.setProgress(progress);

        // Set expensed
        sbExpensed.setSecondaryProgress(expensed.intValue());

        Double balance = amount - expensed;
        if(balance > 0) {
            tvBalance.setText(String.format(getResources().getString(R.string.budget_item_balance),
                    Currency.formatCurrency(getContext(), Currency.getCurrencyById(mBudget.getCurrency()), balance)));
            if(expensed <= progress) {
                tvBalance.setTextColor(getResources().getColor(R.color.colorPrimary));
                sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_ok));
            } else {
                tvBalance.setTextColor(getResources().getColor(R.color.budget_background_progress_warning));
                sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_warning));
            }
        } else if(balance == 0) {
            tvBalance.setText(String.format(getResources().getString(R.string.budget_item_balance),
                    Currency.formatCurrency(getContext(), Currency.getCurrencyById(mBudget.getCurrency()), balance)));
            tvBalance.setTextColor(getResources().getColor(R.color.budget_background_progress_over));
            sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_over));
        } else {
            tvBalance.setText(String.format(getResources().getString(R.string.budget_item_over),
                    Currency.formatCurrency(getContext(), Currency.getCurrencyById(mBudget.getCurrency()), Math.abs(balance))));
            tvBalance.setTextColor(getResources().getColor(R.color.budget_background_progress_over));
            sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_over));
        }
        LogUtils.logLeaveFunction(Tag, null, null);

    }

    public int getDays(Calendar start, Calendar end)
    {
        // Get the represented date in milliseconds
        long milis1 = start.getTimeInMillis();
        long milis2 = end.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = Math.abs(milis2 - milis1);

        return (int)(diff / (24 * 60 * 60 * 1000));
    }
}
