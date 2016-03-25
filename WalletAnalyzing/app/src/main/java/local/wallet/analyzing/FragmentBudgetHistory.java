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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
public class FragmentBudgetHistory extends Fragment {

    public static final String Tag = "BudgetHistory";

    private DatabaseHelper          mDbHelper;
    private Configurations          mConfigs;
    private Budget                  mBudget;
    private TextView                tvDescription;
    private ListView                lvHistory;
    private Adapter                 adapter;
    private List<BudgetHistory>     arHistories = new ArrayList<>();

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
        return inflater.inflate(R.layout.layout_fragment_budget_history, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mDbHelper = new DatabaseHelper(getActivity());
        mConfigs    = new Configurations(getActivity());

        tvDescription   = (TextView) getView().findViewById(R.id.tvDescription);
        tvDescription.setText(String.format(getResources().getString(R.string.budget_history_description),
                                            mBudget.getName()));

        lvHistory = (ListView) getView().findViewById(R.id.lvHistory);
        adapter = new Adapter(getContext(), arHistories);
        lvHistory.setAdapter(adapter);

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
        View mCustomView = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_budget_history));

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        updateListHistory();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private void updateListHistory() {
        LogUtils.logEnterFunction(Tag, null);

        arHistories.clear();

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

        int repeatType      = mBudget.getRepeatType();

        while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
            switch (repeatType) {
                case 1: {// daily
                    endDate.add(Calendar.DATE, 1);
                    break;
                }
                case 2: { // weekly
                    endDate.add(Calendar.WEEK_OF_YEAR, 1);
                    break;
                }
                case 3: { // monthly
                    endDate.add(Calendar.MONTH, 1);
                    break;
                }
                case 4: { // quarterly
                    endDate.add(Calendar.MONTH, 3);
                    break;
                }
                case 5: { // yearly
                    endDate.add(Calendar.YEAR, 1);
                    break;
                }
                default:
                    break;
            } // end switch

            if(endDate.getTimeInMillis() > today.getTimeInMillis()) {
                break;
            }

            List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeAndCategory(mBudget.getCategories(), startDate, endDate);

            Double expensed = 0.0;
            for(Transaction tran : arTransactions) {
                expensed += tran.getAmount();
            }

            Double amount = mBudget.getAmount() + incremental;

            if(mBudget.isIncremental()) {
                incremental += (mBudget.getAmount() - expensed);
            }

            final Calendar mStartDate = Calendar.getInstance();
            mStartDate.setTimeInMillis(startDate.getTimeInMillis());
            final Calendar mEndDate = Calendar.getInstance();
            mEndDate.setTimeInMillis(endDate.getTimeInMillis());
            mEndDate.add(Calendar.DATE, -1);

            arHistories.add(new BudgetHistory(mStartDate, mEndDate, amount, expensed));

            if(endDate.getTimeInMillis() <= today.getTimeInMillis()) {
                switch (repeatType) {
                    case 1: {// daily
                        startDate.add(Calendar.DATE, 1);
                        break;
                    }
                    case 2: { // weekly
                        startDate.add(Calendar.WEEK_OF_MONTH, 1);
                        break;
                    }
                    case 3: { // monthly
                        startDate.add(Calendar.MONTH, 1);
                        break;
                    }
                    case 4: { // quarterly
                        startDate.add(Calendar.MONTH, 3);
                        break;
                    }
                    case 5: { // yearly
                        startDate.add(Calendar.YEAR, 1);
                        break;
                    }
                    default:
                        break;
                } // end switch
            } // End IF endDate < today
        } // End While endDate < today

        adapter.notifyDataSetChanged();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private class BudgetHistory {
        Calendar    startDate;
        Calendar    endDate;
        Double      amount;
        Double      expensed;

        public BudgetHistory(Calendar fromDate, Calendar toDate, Double amount, Double expensed) {
            this.startDate = fromDate;
            this.endDate = toDate;
            this.amount = amount;
            this.expensed = expensed;
        }
    }
    private class Adapter extends ArrayAdapter<BudgetHistory> {

        private class ViewHolder {
            private TextView    tvTime;
            private TextView    tvAmount;
            private TextView    tvExpensed;
            private TextView    tvBalanceTitle;
            private TextView    tvBalance;
            private ImageView   ivDetail;
        }

        List<BudgetHistory> mBudgets;
        public Adapter(Context context, List<BudgetHistory> items) {
            super(context, R.layout.listview_item_budget_history, items);
            this.mBudgets  = items;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder                  = new ViewHolder();
                LayoutInflater inflater     = LayoutInflater.from(getContext());
                convertView                 = inflater.inflate(R.layout.listview_item_budget_history, parent, false);

                viewHolder.tvTime           = (TextView) convertView.findViewById(R.id.tvTime);
                viewHolder.tvAmount         = (TextView) convertView.findViewById(R.id.tvAmount);
                viewHolder.tvExpensed       = (TextView) convertView.findViewById(R.id.tvExpensed);
                viewHolder.tvBalanceTitle   = (TextView) convertView.findViewById(R.id.tvBalanceTitle);
                viewHolder.tvBalance        = (TextView) convertView.findViewById(R.id.tvBalance);
                viewHolder.ivDetail         = (ImageView) convertView.findViewById(R.id.ivDetail);
                viewHolder.ivDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            BudgetHistory budget = mBudgets.get(position);
            if(budget != null) {
                if(mBudget.getRepeatType() == 1) {
                    viewHolder.tvTime.setText(String.format(getResources().getString(R.string.format_budget_day_month_year_2),
                            budget.startDate.get(Calendar.DAY_OF_MONTH),
                            budget.startDate.get(Calendar.MONTH) + 1,
                            budget.startDate.get(Calendar.YEAR)));
                } else {
                    viewHolder.tvTime.setText(String.format(getResources().getString(R.string.format_budget_date_2),
                            budget.startDate.get(Calendar.DAY_OF_MONTH),
                            budget.startDate.get(Calendar.MONTH) + 1,
                            budget.endDate.get(Calendar.DAY_OF_MONTH),
                            budget.endDate.get(Calendar.MONTH) + 1));
                }

                viewHolder.tvAmount.setText(Currency.formatCurrency(getContext(), mBudget.getCurrency(), budget.amount));
                viewHolder.tvExpensed.setText(Currency.formatCurrency(getContext(), mBudget.getCurrency(), budget.expensed));

                Double balance = budget.amount - budget.expensed;
                if(balance > 0) {
                    viewHolder.tvBalanceTitle.setText(getResources().getString(R.string.budget_item_balance_no_format));
                    viewHolder.tvBalanceTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
                    viewHolder.tvBalance.setText(Currency.formatCurrency(getContext(), mBudget.getCurrency(), balance));
                    viewHolder.tvBalance.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    viewHolder.tvBalanceTitle.setText(getResources().getString(R.string.budget_item_over_no_format));
                    viewHolder.tvBalanceTitle.setTextColor(getResources().getColor(R.color.budget_background_progress_over));
                    viewHolder.tvBalance.setText(Currency.formatCurrency(getContext(), mBudget.getCurrency(), Math.abs(balance)));
                    viewHolder.tvBalance.setTextColor(getResources().getColor(R.color.budget_background_progress_over));
                } // End if

            } // End check null

            return convertView;
        } // End getView

    } // End Adapter

}
