package local.wallet.analyzing;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Budget;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentListBudget extends Fragment {

    private static final String TAG = "ListBudget";

    private DatabaseHelper  mDbHelper;
    private Configurations  mConfigs;
    private ListView        lvBudget;
    private BudgetAdapter   adapter;
    private List<Budget>    arBudgets = new ArrayList<>();

    private int width;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, null);
        return inflater.inflate(R.layout.layout_fragment_list_budget, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        super.onActivityCreated(savedInstanceState);

        mDbHelper = new DatabaseHelper(getActivity());
        mConfigs    = new Configurations(getActivity());

        lvBudget = (ListView) getView().findViewById(R.id.lvBudget);
        lvBudget.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                width = lvBudget.getWidth();
            }
        });
        arBudgets   = mDbHelper.getAllBudgets();
        adapter = new BudgetAdapter(getContext(), arBudgets);
        lvBudget.setAdapter(adapter);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(((ActivityMain) getActivity()).getCurrentVisibleItem() != ActivityMain.TAB_POSITION_LIST_BUDGET) {
            return;
        }
        LogUtils.logEnterFunction(TAG, null);
        super.onCreateOptionsMenu(menu, inflater);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_add, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_budget));

        ImageView ivAdd = (ImageView) mCustomView.findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.trace(TAG, "Click Menu Action Add Budget.");
                FragmentBudgetCreate nextFrag = new FragmentBudgetCreate();
                FragmentListBudget.this.getFragmentManager().beginTransaction()
                                                            .add(R.id.layout_budget, nextFrag, "FragmentBudgetCreate")
                                                            .addToBackStack(null)
                                                            .commit();
            }
        });

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        updateListBudget();

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    public void updateListBudget() {
        LogUtils.logEnterFunction(TAG, null);

        arBudgets.clear();

        List<Budget> temp = mDbHelper.getAllBudgets();

        for(int i = 0 ; i < temp.size(); i++) {
            arBudgets.add(temp.get(i));
        }

        if(arBudgets.size() == 0) {
            getView().findViewById(R.id.tvEmpty).setVisibility(View.VISIBLE);
            return;
        }

        getView().findViewById(R.id.tvEmpty).setVisibility(View.GONE);

        adapter.notifyDataSetChanged();

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    private class BudgetAdapter extends ArrayAdapter<Budget> {

        private class ViewHolder {
            private TextView    tvName;
            private TextView    tvDate;
            private TextView    tvBudgetAmount;
            private TextView    tvExpensed;
            private TextView    tvBalanceTitle;
            private TextView    tvBalance;
            private SeekBar     sbExpensed;
        }

        private int width = 0;
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
                viewHolder.tvBudgetAmount   = (TextView) convertView.findViewById(R.id.tvBudgetAmount);
                viewHolder.tvExpensed       = (TextView) convertView.findViewById(R.id.tvExpensed);
                viewHolder.tvBalanceTitle   = (TextView) convertView.findViewById(R.id.tvBalanceTitle);
                viewHolder.tvBalance        = (TextView) convertView.findViewById(R.id.tvBalance);
                viewHolder.sbExpensed       = (SeekBar) convertView.findViewById(R.id.sbExpensed);
                viewHolder.sbExpensed.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Budget budget = mBudgets.get(position);
            if(budget != null) {
                viewHolder.tvName.setText(budget.getName());
                viewHolder.tvBudgetAmount.setText(Currency.formatCurrency(getContext(), Currency.getCurrencyById(budget.getCurrency()), budget.getAmount()));

                double incremental = 0.0;

                String date = "";
                Calendar today      = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, today.getActualMinimum(Calendar.HOUR_OF_DAY));
                today.set(Calendar.MINUTE,      today.getActualMinimum(Calendar.MINUTE));
                today.set(Calendar.SECOND,      today.getActualMinimum(Calendar.SECOND));
                today.set(Calendar.MILLISECOND, today.getActualMinimum(Calendar.MILLISECOND));

                Calendar startDate = Calendar.getInstance();
                startDate.setTimeInMillis(budget.getFromDate().getTimeInMillis());
                Calendar endDate = Calendar.getInstance();
                endDate.setTimeInMillis(budget.getFromDate().getTimeInMillis());

                int repeatType      = budget.getRepeatType();

                switch (repeatType) {
                    case 0: {// No repeat
                        Calendar fromDate = budget.getFromDate();
                        date = String.format(getResources().getString(R.string.format_budget_day_month_year),
                                fromDate.get(Calendar.DAY_OF_MONTH),
                                fromDate.get(Calendar.MONTH) + 1,
                                fromDate.get(Calendar.YEAR));

                        viewHolder.tvDate.setText(date);
                        break;
                    }
                    case 1: {// daily
                        Calendar tomorow = Calendar.getInstance();
                        tomorow.add(Calendar.DAY_OF_MONTH, 1);
                        date = String.format(getResources().getString(R.string.format_budget_date),
                                today.get(Calendar.DAY_OF_MONTH),
                                today.get(Calendar.MONTH) + 1,
                                tomorow.get(Calendar.DAY_OF_MONTH),
                                tomorow.get(Calendar.MONTH) + 1);
                        viewHolder.tvDate.setText(date);

                        break;
                    }
                    case 2: { // weekly

                        while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                            endDate.add(Calendar.DAY_OF_YEAR, 6);

                            if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                                if(budget.isIncremental()) {
                                    List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(budget.getCategories(), startDate, endDate);

                                    Double expensed = 0.0;
                                    for(Transaction tran : arTransactions) {
                                        expensed += tran.getAmount();
                                    }

                                    incremental += (budget.getAmount() - expensed);
                                }
                                startDate.add(Calendar.DAY_OF_YEAR, 6);
                            }
                        }

                        date = String.format(getResources().getString(R.string.format_budget_date),
                                startDate.get(Calendar.DAY_OF_MONTH),
                                startDate.get(Calendar.MONTH) + 1,
                                endDate.get(Calendar.DAY_OF_MONTH),
                                endDate.get(Calendar.MONTH) + 1);
                        viewHolder.tvDate.setText(date);

                        break;
                    }
                    case 3: { // monthly
                        while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                            endDate.add(Calendar.MONTH, 1);

                            if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                                if(budget.isIncremental()) {
                                    List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(budget.getCategories(), startDate, endDate);

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
                        date = String.format(getResources().getString(R.string.format_budget_date),
                                startDate.get(Calendar.DAY_OF_MONTH),
                                startDate.get(Calendar.MONTH) + 1,
                                endDate.get(Calendar.DAY_OF_MONTH),
                                endDate.get(Calendar.MONTH) + 1);
                        viewHolder.tvDate.setText(date);
                        break;
                    }
                    case 4: { // quarterly
                        while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                            endDate.add(Calendar.MONTH, 3);

                            if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                                if(budget.isIncremental()) {
                                    List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(budget.getCategories(), startDate, endDate);

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
                        date = String.format(getResources().getString(R.string.format_budget_date),
                                startDate.get(Calendar.DAY_OF_MONTH),
                                startDate.get(Calendar.MONTH) + 1,
                                endDate.get(Calendar.DAY_OF_MONTH),
                                endDate.get(Calendar.MONTH) + 1);
                        viewHolder.tvDate.setText(date);
                        break;
                    }
                    case 5: { // yearly
                        while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                            endDate.add(Calendar.YEAR, 1);

                            if (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                                if(budget.isIncremental()) {
                                    List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(budget.getCategories(), startDate, endDate);

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
                        date = String.format(getResources().getString(R.string.format_budget_date),
                                startDate.get(Calendar.DAY_OF_MONTH),
                                startDate.get(Calendar.MONTH) + 1,
                                endDate.get(Calendar.DAY_OF_MONTH),
                                endDate.get(Calendar.MONTH) + 1);
                        viewHolder.tvDate.setText(date);
                        break;
                    }
                    default:
                        break;
                } // end switch

                Double amount = budget.getAmount() + incremental;

                List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(budget.getCategories(), startDate, endDate);

                Double expensed = 0.0;
                for(Transaction tran : arTransactions) {
                    expensed += tran.getAmount();
                }

                viewHolder.tvExpensed.setText(Currency.formatCurrency(getContext(), Currency.getCurrencyById(budget.getCurrency()), expensed));

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
                    if(expensed <= progress) {
                        viewHolder.tvBalanceTitle.setText(getResources().getString(R.string.budget_item_balance));
                        viewHolder.tvBalanceTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
                        viewHolder.tvBalance.setText(Currency.formatCurrency(getContext(), Currency.getCurrencyById(budget.getCurrency()), balance));
                        viewHolder.tvBalance.setTextColor(getResources().getColor(R.color.colorPrimary));
                    } else {
                        viewHolder.tvBalanceTitle.setText(getResources().getString(R.string.budget_item_balance));
                        viewHolder.tvBalanceTitle.setTextColor(getResources().getColor(R.color.budget_background_progress_warning));
                        viewHolder.tvBalance.setText(Currency.formatCurrency(getContext(), Currency.getCurrencyById(budget.getCurrency()), balance));
                        viewHolder.tvBalance.setTextColor(getResources().getColor(R.color.budget_background_progress_warning));
                        viewHolder.sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_warning));
                    }
                } else if(balance == 0) {
                    viewHolder.tvBalanceTitle.setText(getResources().getString(R.string.budget_item_balance));
                    viewHolder.tvBalanceTitle.setTextColor(getResources().getColor(R.color.budget_background_progress_over));
                    viewHolder.tvBalance.setText(Currency.formatCurrency(getContext(), Currency.getCurrencyById(budget.getCurrency()), balance));
                    viewHolder.tvBalance.setTextColor(getResources().getColor(R.color.budget_background_progress_over));
                    viewHolder.sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_over));
                } else {
                    viewHolder.tvBalanceTitle.setText(getResources().getString(R.string.budget_item_over));
                    viewHolder.tvBalanceTitle.setTextColor(getResources().getColor(R.color.budget_background_progress_over));
                    viewHolder.tvBalance.setText(Currency.formatCurrency(getContext(), Currency.getCurrencyById(budget.getCurrency()), Math.abs(balance)));
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
