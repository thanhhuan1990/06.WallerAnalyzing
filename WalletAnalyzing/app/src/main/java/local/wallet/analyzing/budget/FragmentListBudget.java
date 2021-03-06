package local.wallet.analyzing.budget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.main.Configs;
import local.wallet.analyzing.model.Budget;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentListBudget extends ListFragment {

    public static int               mTab = 3;
    public static final String      Tag = "---[" + mTab + "]---ListBudget";
    private ActivityMain            mActivity;

    private DatabaseHelper  mDbHelper;
    private Configs mConfigs;
    private BudgetAdapter   mAdapter;
    private List<Budget>    arBudgets = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle       = this.getArguments();
        if (bundle != null) {
            mTab                = bundle.getInt("Tab", mTab);
        }
        LogUtils.logLeaveFunction(Tag);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        LogUtils.logLeaveFunction(Tag);
        return inflater.inflate(R.layout.layout_fragment_list_budget, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        super.onActivityCreated(savedInstanceState);

        mActivity   = (ActivityMain) getActivity();

        mDbHelper   = new DatabaseHelper(getActivity());
        mConfigs    = new Configs(getActivity());

        arBudgets   = mDbHelper.getAllBudgets();
        mAdapter = new BudgetAdapter(getContext(), arBudgets);
        setListAdapter(mAdapter);

        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onResume() {
        LogUtils.logEnterFunction(Tag);
        super.onResume();

        if(mTab != mActivity.getCurrentVisibleItem()) {
            LogUtils.error(Tag, "Wrong Tab. Return");
            LogUtils.logLeaveFunction(Tag);
            return;
        }

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_add, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_budget));

        ImageView ivAdd = (ImageView) mCustomView.findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.trace(Tag, "Click Menu Action Add Budget.");
                FragmentBudgetCUD nextFrag = new FragmentBudgetCUD();
                Bundle bundle = new Bundle();
                bundle.putInt("Tab", mTab);
                nextFrag.setArguments(bundle);
                mActivity.addFragment(mTab, R.id.layout_budget, nextFrag, FragmentBudgetCUD.Tag, true);
            }
        });

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        updateListBudget();

        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag);
        super.onCreateOptionsMenu(menu, inflater);

        if(mTab != mActivity.getCurrentVisibleItem()) {
            LogUtils.error(Tag, "Wrong Tab. Return");
            LogUtils.logLeaveFunction(Tag);
            return;
        }

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_add, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_budget));

        ImageView ivAdd = (ImageView) mCustomView.findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.trace(Tag, "Click Menu Action Add Budget.");
                FragmentBudgetCUD nextFrag = new FragmentBudgetCUD();
                Bundle bundle = new Bundle();
                bundle.putInt("Tab", mTab);
                nextFrag.setArguments(bundle);
                mActivity.addFragment(mTab, R.id.layout_budget, nextFrag, FragmentBudgetCUD.Tag, true);
            }
        });

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        updateListBudget();

        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FragmentBudgetDetail nextFrag = new FragmentBudgetDetail();
        Bundle bundle = new Bundle();
        bundle.putInt("Tab", mTab);
        bundle.putSerializable("Budget", arBudgets.get(position));
        nextFrag.setArguments(bundle);
        mActivity.addFragment(mTab, R.id.layout_budget, nextFrag, FragmentBudgetDetail.Tag, true);
    }

    /**
     * Update Data Source
     */
    private void updateListBudget() {
        LogUtils.logEnterFunction(Tag);

        arBudgets.clear();

        List<Budget> temp = mDbHelper.getAllBudgets();

        arBudgets.addAll(temp);

        LogUtils.trace(Tag, "arBudgets = " + arBudgets.toString());

        mAdapter.notifyDataSetChanged();

        LogUtils.logLeaveFunction(Tag);
    }

    private class BudgetAdapter extends ArrayAdapter<Budget> {

        private class ViewHolder {
            private TextView    tvName;
            private TextView    tvDate;
            private TextView    tvAmount;
            private TextView    tvIncremental;
            private TextView    tvExpensed;
            private TextView    tvBalance;
            private SeekBar     sbExpensed;
        }

        List<Budget> mBudgets;
        public BudgetAdapter(Context context, List<Budget> items) {
            super(context, R.layout.listview_item_budget, items);
            this.mBudgets  = items;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder                  = new ViewHolder();
                LayoutInflater inflater     = LayoutInflater.from(getContext());
                convertView                 = inflater.inflate(R.layout.listview_item_budget, parent, false);

                viewHolder.tvName           = (TextView) convertView.findViewById(R.id.tvName);
                viewHolder.tvDate           = (TextView) convertView.findViewById(R.id.tvDate);
                viewHolder.tvAmount         = (TextView) convertView.findViewById(R.id.tvAmount);
                viewHolder.tvIncremental    = (TextView) convertView.findViewById(R.id.tvIncremental);
                viewHolder.tvExpensed       = (TextView) convertView.findViewById(R.id.tvExpensed);
                viewHolder.tvBalance        = (TextView) convertView.findViewById(R.id.tvBalance);
                viewHolder.sbExpensed       = (SeekBar) convertView.findViewById(R.id.sbExpensed);
                viewHolder.sbExpensed.setEnabled(false);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Budget budget = mBudgets.get(position);
            if(budget != null) {
                viewHolder.tvName.setText(budget.getName());
                viewHolder.tvAmount.setText(String.format(getResources().getString(R.string.budget_item_total),
                                                            Currency.formatCurrency(getContext(), budget.getCurrency(), budget.getAmount())));

                double incremental = 0.0;

                Calendar today      = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, today.getActualMinimum(Calendar.HOUR_OF_DAY));
                today.set(Calendar.MINUTE,      today.getActualMinimum(Calendar.MINUTE));
                today.set(Calendar.SECOND,      today.getActualMinimum(Calendar.SECOND));
                today.set(Calendar.MILLISECOND, today.getActualMinimum(Calendar.MILLISECOND));

                Calendar startDate = Calendar.getInstance();
                startDate.setTimeInMillis(budget.getStartDate().getTimeInMillis());
                Calendar endDate = Calendar.getInstance();
                endDate.setTimeInMillis(budget.getStartDate().getTimeInMillis());

                int repeatType      = budget.getRepeatType();
                switch (repeatType) {
                    case  0: // No repeat
                        endDate.setTimeInMillis(budget.getEndDate().getTimeInMillis());
                        break;
                    case 1: {// daily
                        while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                            endDate.add(Calendar.DAY_OF_YEAR, 1);

                            if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                                if(budget.isIncremental()) {
                                    List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeAndCategory(budget.getCategories(), startDate, endDate);

                                    Double expensed = 0.0;
                                    for(Transaction tran : arTransactions) {
                                        expensed += tran.getAmount();
                                    }

                                    incremental += (budget.getAmount() - expensed);
                                }
                                startDate.add(Calendar.DAY_OF_YEAR, 1);
                            }
                        }

                        endDate.add(Calendar.DAY_OF_YEAR, -1);
                        break;
                    }
                    case 2: { // weekly

                        while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                            endDate.add(Calendar.WEEK_OF_YEAR, 1);

                            if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                                if(budget.isIncremental()) {
                                    List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeAndCategory(budget.getCategories(), startDate, endDate);

                                    Double expensed = 0.0;
                                    for(Transaction tran : arTransactions) {
                                        expensed += tran.getAmount();
                                    }

                                    incremental += (budget.getAmount() - expensed);
                                }
                                startDate.add(Calendar.WEEK_OF_YEAR, 1);
                            }
                        }

                        endDate.add(Calendar.DAY_OF_YEAR, -1);
                        break;
                    }
                    case 3: { // monthly
                        while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                            endDate.add(Calendar.MONTH, 1);

                            if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                                if(budget.isIncremental()) {
                                    List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeAndCategory(budget.getCategories(), startDate, endDate);

                                    Double expensed = 0.0;
                                    for(Transaction tran : arTransactions) {
                                        expensed += tran.getAmount();
                                    }

                                    incremental += (budget.getAmount() - expensed);
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
                                if(budget.isIncremental()) {
                                    List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeAndCategory(budget.getCategories(), startDate, endDate);

                                    Double expensed = 0.0;
                                    for(Transaction tran : arTransactions) {
                                        expensed += tran.getAmount();
                                    }

                                    incremental += (budget.getAmount() - expensed);
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
                                if(budget.isIncremental()) {
                                    List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeAndCategory(budget.getCategories(), startDate, endDate);

                                    Double expensed = 0.0;
                                    for(Transaction tran : arTransactions) {
                                        expensed += tran.getAmount();
                                    }

                                    incremental += (budget.getAmount() - expensed);
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
                    viewHolder.tvDate.setText(String.format(getResources().getString(R.string.format_budget_day_month_year),
                            startDate.get(Calendar.DAY_OF_MONTH),
                            startDate.get(Calendar.MONTH) + 1,
                            endDate.get(Calendar.YEAR)));
                } else {
                    viewHolder.tvDate.setText(String.format(getResources().getString(R.string.format_budget_date),
                            startDate.get(Calendar.DAY_OF_MONTH),
                            startDate.get(Calendar.MONTH) + 1,
                            endDate.get(Calendar.DAY_OF_MONTH),
                            endDate.get(Calendar.MONTH) + 1));
                }

                if(incremental == 0) {
                    viewHolder.tvIncremental.setVisibility(View.GONE);
                } else {
                    viewHolder.tvIncremental.setVisibility(View.VISIBLE);
                    viewHolder.tvIncremental.setText(String.format(getResources().getString(R.string.budget_item_incremental),
                                                    Currency.formatCurrency(getContext(), budget.getCurrency(), incremental)));

                    if(incremental > 0) {
                        viewHolder.tvIncremental.setTextColor(getResources().getColor(R.color.colorPrimary));
                    } else {
                        viewHolder.tvIncremental.setTextColor(getResources().getColor(R.color.budget_background_progress_over));
                    }
                }

                Double amount = budget.getAmount() + incremental;

                List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeAndCategory(budget.getCategories(), startDate, endDate);

                Double expensed = 0.0;
                for(Transaction tran : arTransactions) {
                    expensed += tran.getAmount();
                }

                viewHolder.tvExpensed.setText(String.format(getResources().getString(R.string.budget_item_expensed),
                                            Currency.formatCurrency(getContext(), budget.getCurrency(), expensed)));

                viewHolder.sbExpensed.setMax(amount.intValue());
                // Set date
                double numOfDaysOne = (double)getDays(startDate, today);
                double numOfDaysTwo = (double)getDays(startDate, endDate);
                int progress = (int)((numOfDaysOne / numOfDaysTwo) * viewHolder.sbExpensed.getMax());
                viewHolder.sbExpensed.setProgress(progress);

                // Set expensed
                viewHolder.sbExpensed.setSecondaryProgress(expensed.intValue());

                Double balance = amount - expensed;
                if(balance > 0) {
                    viewHolder.tvBalance.setText(String.format(getResources().getString(R.string.budget_item_balance),
                                                                Currency.formatCurrency(getContext(), budget.getCurrency(), balance)));
                    if(expensed <= progress) {
                        viewHolder.tvBalance.setTextColor(getResources().getColor(R.color.colorPrimary));
                        viewHolder.sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_ok));
                    } else {
                        viewHolder.tvBalance.setTextColor(getResources().getColor(R.color.budget_background_progress_warning));
                        viewHolder.sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_warning));
                    }
                } else if(balance == 0) {
                    viewHolder.tvBalance.setText(String.format(getResources().getString(R.string.budget_item_balance),
                                                    Currency.formatCurrency(getContext(), budget.getCurrency(), balance)));
                    viewHolder.tvBalance.setTextColor(getResources().getColor(R.color.budget_background_progress_over));
                    viewHolder.sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_over));
                } else {
                    viewHolder.tvBalance.setText(String.format(getResources().getString(R.string.budget_item_over),
                                                                Currency.formatCurrency(getContext(), budget.getCurrency(), Math.abs(balance))));
                    viewHolder.tvBalance.setTextColor(getResources().getColor(R.color.budget_background_progress_over));
                    viewHolder.sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_over));
                }

            } // End check null

            return convertView;
        } // End getView

        public int getDays(Calendar start, Calendar end)
        {
            // Get the represented date in milliseconds
            long milis1 = start.getTimeInMillis();
            long milis2 = end.getTimeInMillis();

            // Calculate difference in milliseconds
            long diff = Math.abs(milis2 - milis1);

            return (int)(diff / (24 * 60 * 60 * 1000));
        }

    } // End Adapter

}
