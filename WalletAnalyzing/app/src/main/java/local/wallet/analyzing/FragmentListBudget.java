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

    private static final String Tag = "ListBudget";

    private DatabaseHelper  mDbHelper;
    private Configurations  mConfigs;
    private ListView        lvBudget;
    private BudgetAdapter   adapter;
    private List<Budget>    arBudgets = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_list_budget, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mDbHelper   = new DatabaseHelper(getActivity());
        mConfigs    = new Configurations(getActivity());

        lvBudget    = (ListView) getView().findViewById(R.id.lvBudget);
        arBudgets   = mDbHelper.getAllBudgets();
        adapter     = new BudgetAdapter(getContext(), arBudgets);
        lvBudget.setAdapter(adapter);
        lvBudget.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentBudgetDetail nextFrag = new FragmentBudgetDetail();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Budget", arBudgets.get(position));
                nextFrag.setArguments(bundle);
                FragmentListBudget.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_budget, nextFrag, "FragmentBudgetDetail")
                        .addToBackStack(null)
                        .commit();
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
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_add, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_budget));

        ImageView ivAdd = (ImageView) mCustomView.findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.trace(Tag, "Click Menu Action Add Budget.");
                FragmentBudgetCreateUpdateDelete nextFrag = new FragmentBudgetCreateUpdateDelete();
                FragmentListBudget.this.getFragmentManager().beginTransaction()
                                                            .add(R.id.layout_budget, nextFrag, "FragmentBudgetCreateUpdateDelete")
                                                            .addToBackStack(null)
                                                            .commit();
            }
        });

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        updateListBudget();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    public void updateListBudget() {
        LogUtils.logEnterFunction(Tag, null);

        arBudgets.clear();

        List<Budget> temp = mDbHelper.getAllBudgets();

        for(int i = 0 ; i < temp.size(); i++) {
            arBudgets.add(temp.get(i));
        }

        LogUtils.trace(Tag, "arBudgets = " + arBudgets.toString());

        getView().findViewById(R.id.tvEmpty).setVisibility(View.GONE);

        if(arBudgets.size() == 0) {
            getView().findViewById(R.id.tvEmpty).setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();

        LogUtils.logLeaveFunction(Tag, null, null);
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
                                                            Currency.formatCurrency(getContext(), Currency.getCurrencyById(budget.getCurrency()), budget.getAmount())));

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
                                    List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(budget.getCategories(), startDate, endDate, 0);

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
                                    List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(budget.getCategories(), startDate, endDate, 0);

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
                                    List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(budget.getCategories(), startDate, endDate, 0);

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
                                    List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(budget.getCategories(), startDate, endDate, 0);

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
                                    List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(budget.getCategories(), startDate, endDate, 0);

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
                                                    Currency.formatCurrency(getContext(), Currency.getCurrencyById(budget.getCurrency()), incremental)));

                    if(incremental > 0) {
                        viewHolder.tvIncremental.setTextColor(getResources().getColor(R.color.colorPrimary));
                    } else {
                        viewHolder.tvIncremental.setTextColor(getResources().getColor(R.color.budget_background_progress_over));
                    }
                }

                Double amount = budget.getAmount() + incremental;

                List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(budget.getCategories(), startDate, endDate, 1);

                Double expensed = 0.0;
                for(Transaction tran : arTransactions) {
                    expensed += tran.getAmount();
                }

                viewHolder.tvExpensed.setText(String.format(getResources().getString(R.string.budget_item_expensed),
                                            Currency.formatCurrency(getContext(), Currency.getCurrencyById(budget.getCurrency()), expensed)));

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
                                                                Currency.formatCurrency(getContext(), Currency.getCurrencyById(budget.getCurrency()), balance)));
                    if(expensed <= progress) {
                        viewHolder.tvBalance.setTextColor(getResources().getColor(R.color.colorPrimary));
                        viewHolder.sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_ok));
                    } else {
                        viewHolder.tvBalance.setTextColor(getResources().getColor(R.color.budget_background_progress_warning));
                        viewHolder.sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_warning));
                    }
                } else if(balance == 0) {
                    viewHolder.tvBalance.setText(String.format(getResources().getString(R.string.budget_item_balance),
                                                    Currency.formatCurrency(getContext(), Currency.getCurrencyById(budget.getCurrency()), balance)));
                    viewHolder.tvBalance.setTextColor(getResources().getColor(R.color.budget_background_progress_over));
                    viewHolder.sbExpensed.setProgressDrawable(getResources().getDrawable(R.drawable.budget_progress_over));
                } else {
                    viewHolder.tvBalance.setText(String.format(getResources().getString(R.string.budget_item_over),
                                                                Currency.formatCurrency(getContext(), Currency.getCurrencyById(budget.getCurrency()), Math.abs(balance))));
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
