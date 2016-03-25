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
import local.wallet.analyzing.model.Category.EnumDebt;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/25/2016.
 */
public class FragmentBudgetDetail extends Fragment {

    public static final String Tag = "BudgetDetail";

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
    private LinearLayout    llBudgetDetailTransactions;
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
    } // End onCreate

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_budget_detail, container, false);
    } // End onCreateView

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mDbHelper                   = new DatabaseHelper(getActivity());
        mConfigs                    = new Configurations(getActivity());

        tvDescription               = (TextView) getView().findViewById(R.id.tvDescription);
        tvName                      = (TextView) getView().findViewById(R.id.tvName);
        tvAmount                    = (TextView) getView().findViewById(R.id.tvAmount);
        tvIncremental               = (TextView) getView().findViewById(R.id.tvIncremental);
        tvDate                      = (TextView) getView().findViewById(R.id.tvDate);
        tvExpensed                  = (TextView) getView().findViewById(R.id.tvExpensed);
        tvBalance                   = (TextView) getView().findViewById(R.id.tvBalance);
        sbExpensed                  = (SeekBar) getView().findViewById(R.id.sbExpensed);
        sbExpensed.setEnabled(false);
        llBudgetDetailTransactions  = (LinearLayout) getView().findViewById(R.id.llBudgetDetailTransactions);
        llHistory                   = (LinearLayout) getView().findViewById(R.id.llHistory);

        if(mBudget.getRepeatType() == 0) {
            llHistory.setVisibility(View.GONE);
        }

        llBudgetDetailTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentBudgetDetailTransactions nextFrag = new FragmentBudgetDetailTransactions();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Budget", mBudget);
                nextFrag.setArguments(bundle);
                FragmentBudgetDetail.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_budget, nextFrag, FragmentBudgetDetailTransactions.Tag)
                        .addToBackStack(null)
                        .commit();
            }
        });
        llHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBudget.getRepeatType() != 0) {
                    FragmentBudgetHistory nextFrag = new FragmentBudgetHistory();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Budget", mBudget);
                    nextFrag.setArguments(bundle);
                    FragmentBudgetDetail.this.getFragmentManager().beginTransaction()
                            .add(R.id.layout_budget, nextFrag, FragmentBudgetHistory.Tag)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End onActivityCreated

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
                FragmentBudgetCUD nextFrag = new FragmentBudgetCUD();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Budget", mBudget);
                nextFrag.setArguments(bundle);
                FragmentBudgetDetail.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_budget, nextFrag, FragmentBudgetCUD.Tag)
                        .addToBackStack(null)
                        .commit();
            }
        });

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        setViewData();

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End onCreateOptionsMenu

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
        LogUtils.trace(Tag, "mBudget.getCategories().length = " + mBudget.getCategories().length + ", mDbHelper.getAllCategories(true, EnumDebt.NONE).size() = " + mDbHelper.getAllCategories(true, EnumDebt.NONE).size());
        /* Show budget's description */
        String[] repeatTypes = getResources().getStringArray(R.array.budget_repeat_type);
        if(mBudget.getCategories().length == 1) {
            tvDescription.setText(String.format(getResources().getString(R.string.budget_detail_description),
                    Currency.formatCurrency(getContext(), mBudget.getCurrency(), mBudget.getAmount()),
                    repeatTypes[mBudget.getRepeatType()],
                    mDbHelper.getCategory(mBudget.getCategories()[0]).getName()));
        } else if(mBudget.getCategories().length == mDbHelper.getAllCategories(true, EnumDebt.NONE).size()){
            tvDescription.setText(String.format(getResources().getString(R.string.budget_detail_description_all),
                                                Currency.formatCurrency(getContext(), mBudget.getCurrency(), mBudget.getAmount()),
                                                repeatTypes[mBudget.getRepeatType()]));
        } else {
            String categories = "";

            for(int i = 0; i < mBudget.getCategories().length; i++) {
                if(checkContain(mDbHelper.getCategory(mBudget.getCategories()[i]).getParentId())) {
                    continue;
                }
                if(!categories.equals("")) {
                    categories += ", ";
                }
                categories += mDbHelper.getCategory(mBudget.getCategories()[i]).getName();
            }

            tvDescription.setText(String.format(getResources().getString(R.string.budget_detail_description_many),
                                                Currency.formatCurrency(getContext(), mBudget.getCurrency(), mBudget.getAmount()),
                                                repeatTypes[mBudget.getRepeatType()],
                                                categories));

        }

        tvName.setText(mBudget.getName());
        tvAmount.setText(String.format(getResources().getString(R.string.budget_item_total),
                                        Currency.formatCurrency(getContext(), mBudget.getCurrency(), mBudget.getAmount())));

        double incremental = 0.0;

        Calendar today      = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, today.getActualMinimum(Calendar.HOUR_OF_DAY));
        today.set(Calendar.MINUTE,      today.getActualMinimum(Calendar.MINUTE));
        today.set(Calendar.SECOND,      today.getActualMinimum(Calendar.SECOND));
        today.set(Calendar.MILLISECOND, today.getActualMinimum(Calendar.MILLISECOND));

        Calendar startDate  = Calendar.getInstance();
        startDate.setTimeInMillis(mBudget.getStartDate().getTimeInMillis());
        Calendar endDate    = Calendar.getInstance();
        endDate.setTimeInMillis(mBudget.getStartDate().getTimeInMillis());

        switch (mBudget.getRepeatType()) {
            case 0: {// No repeat
                endDate.setTimeInMillis(mBudget.getEndDate().getTimeInMillis());
                break;
            } // End No-repeat
            case 1: {// daily
                while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                    endDate.add(Calendar.DAY_OF_YEAR, 1);

                    if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                        if(mBudget.isIncremental()) {
                            List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeAndCategory(mBudget.getCategories(), startDate, endDate);

                            Double expensed = 0.0;
                            for(Transaction tran : arTransactions) {
                                expensed += tran.getAmount();
                            }

                            incremental += (mBudget.getAmount() - expensed);
                        }
                        startDate.add(Calendar.DAY_OF_YEAR, 1);
                    }
                }

                break;
            } // End Daily
            case 2: { // weekly

                while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                    endDate.add(Calendar.WEEK_OF_MONTH, 1);

                    if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                        if(mBudget.isIncremental()) {
                            List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeAndCategory(mBudget.getCategories(), startDate, endDate);

                            Double expensed = 0.0;
                            for(Transaction tran : arTransactions) {
                                expensed += tran.getAmount();
                            }

                            incremental += (mBudget.getAmount() - expensed);
                        }
                    }
                }

                endDate.add(Calendar.DAY_OF_YEAR, -1);
                break;
            } // End Weekly
            case 3: { // monthly
                while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                    endDate.add(Calendar.MONTH, 1);

                    if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                        if(mBudget.isIncremental()) {
                            List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeAndCategory(mBudget.getCategories(), startDate, endDate);

                            Double expensed = 0.0;
                            for(Transaction tran : arTransactions) {
                                expensed += tran.getAmount();
                            }

                            incremental += (mBudget.getAmount() - expensed);
                        }
                        startDate.add(Calendar.MONTH, 1);
                    }
                }

                break;
            } // End Monthly
            case 4: { // quarterly
                while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                    endDate.add(Calendar.MONTH, 3);

                    if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                        if(mBudget.isIncremental()) {
                            List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeAndCategory(mBudget.getCategories(), startDate, endDate);

                            Double expensed = 0.0;
                            for(Transaction tran : arTransactions) {
                                expensed += tran.getAmount();
                            }

                            incremental += (mBudget.getAmount() - expensed);
                        }
                        startDate.add(Calendar.MONTH, 3);
                    }
                }

                break;
            } // End Quarterly
            case 5: { // yearly
                while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                    endDate.add(Calendar.YEAR, 1);

                    if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                        if(mBudget.isIncremental()) {
                            List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeAndCategory(mBudget.getCategories(), startDate, endDate);

                            Double expensed = 0.0;
                            for(Transaction tran : arTransactions) {
                                expensed += tran.getAmount();
                            }

                            incremental += (mBudget.getAmount() - expensed);
                        }
                        startDate.add(Calendar.YEAR, 1);
                    }
                }

                break;
            } // End Yearly
            default:
                break;
        } // end switch

        Calendar textEndDate = Calendar.getInstance();
        textEndDate.setTimeInMillis(endDate.getTimeInMillis());
        textEndDate.add(Calendar.DATE, -1);
        if(startDate.getTimeInMillis() == endDate.getTimeInMillis()) {
            tvDate.setText(String.format(getResources().getString(R.string.format_budget_day_month_year),
                                        startDate.get(Calendar.DAY_OF_MONTH),
                                        startDate.get(Calendar.MONTH) + 1,
                                        textEndDate.get(Calendar.YEAR)));
        } else {
            tvDate.setText(String.format(getResources().getString(R.string.format_budget_date),
                                        startDate.get(Calendar.DAY_OF_MONTH),
                                        startDate.get(Calendar.MONTH) + 1,
                                        textEndDate.get(Calendar.DAY_OF_MONTH),
                                        textEndDate.get(Calendar.MONTH) + 1));
        }

        if(incremental == 0) {
            tvIncremental.setVisibility(View.GONE);
        } else {
            tvIncremental.setVisibility(View.VISIBLE);
            tvIncremental.setText(String.format(getResources().getString(R.string.budget_item_incremental),
                    Currency.formatCurrency(getContext(), mBudget.getCurrency(), incremental)));

            if(incremental > 0) {
                tvIncremental.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                tvIncremental.setTextColor(getResources().getColor(R.color.budget_background_progress_over));
            }
        }

        Double amount = mBudget.getAmount() + incremental;

        List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeAndCategory(mBudget.getCategories(), startDate, endDate);

        Double expensed = 0.0;
        for(Transaction tran : arTransactions) {
            expensed += tran.getAmount();
        }

        tvExpensed.setText(String.format(getResources().getString(R.string.budget_item_expensed),
                                        Currency.formatCurrency(getContext(), mBudget.getCurrency(), expensed)));

        // Hide/Show layout Transactions
        if(expensed == 0) {
            llBudgetDetailTransactions.setVisibility(View.GONE);
        }

        // Hide/Show layout History
        if(startDate.getTimeInMillis() == mBudget.getStartDate().getTimeInMillis()) {
            llHistory.setVisibility(View.GONE);
        }

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
                    Currency.formatCurrency(getContext(), mBudget.getCurrency(), balance)));
            if(expensed <= progress) {
                tvBalance.setTextColor(getResources().getColor(R.color.colorPrimary));
                sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_ok));
            } else {
                tvBalance.setTextColor(getResources().getColor(R.color.budget_background_progress_warning));
                sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_warning));
            }
        } else if(balance == 0) {
            tvBalance.setText(String.format(getResources().getString(R.string.budget_item_balance),
                    Currency.formatCurrency(getContext(), mBudget.getCurrency(), balance)));
            tvBalance.setTextColor(getResources().getColor(R.color.budget_background_progress_over));
            sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_over));
        } else {
            tvBalance.setText(String.format(getResources().getString(R.string.budget_item_over),
                    Currency.formatCurrency(getContext(), mBudget.getCurrency(), Math.abs(balance))));
            tvBalance.setTextColor(getResources().getColor(R.color.budget_background_progress_over));
            sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_over));
        }
        LogUtils.logLeaveFunction(Tag, null, null);

    } // End setViewData

    private int getDays(Calendar start, Calendar end) {
        // Get the represented date in milliseconds
        long milis1 = start.getTimeInMillis();
        long milis2 = end.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = Math.abs(milis2 - milis1);

        return (int)(diff / (24 * 60 * 60 * 1000));
    }

    /**
     * Check ID is contain in list from BudgetCreateUpdateDelete
     * @param id
     * @return
     */
    private boolean checkContain(int id) {
        for(int i = 0 ; i < mBudget.getCategories().length; i++) {
            if(mBudget.getCategories()[i] == id) {
                return true;
            }
        }

        return false;
    } // End checkContain
}
