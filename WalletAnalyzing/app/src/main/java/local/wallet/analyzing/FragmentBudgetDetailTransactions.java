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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.AccountType;
import local.wallet.analyzing.model.Budget;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.model.TransactionGroup;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 3/4/2016.
 */
public class FragmentBudgetDetailTransactions extends Fragment {

    private static final String Tag = "BudgetDetailTransactions";

    private DatabaseHelper          mDbHelper;
    private Configurations          mConfigs;
    private Budget                  mBudget;

    private TextView                tvDescription;
    private LinearLayout            llTransactions;
    private List<BudgetTransaction> arBudgetTransaction;
    private TextView                tvAmount;

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
        return inflater.inflate(R.layout.layout_fragment_budget_detail_transactions, container, false);
    } // End onCreateView

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mDbHelper                   = new DatabaseHelper(getActivity());
        mConfigs                    = new Configurations(getActivity());

        tvDescription               = (TextView) getView().findViewById(R.id.tvDescription);
        llTransactions              = (LinearLayout) getView().findViewById(R.id.llTransactions);

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
        View mCustomView    = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle    = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(mBudget.getName());

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        updateDataSource();

        Calendar startDate  = getStartDate(mBudget);
        Calendar endDate    = getEndDate(mBudget);
        tvDescription.setText(String.format(getResources().getString(R.string.budget_detail_transaction_description),
                                            startDate.get(Calendar.DAY_OF_MONTH),
                                            startDate.get(Calendar.MONTH),
                                            endDate.get(Calendar.DAY_OF_MONTH),
                                            endDate.get(Calendar.MONTH),
                                            mBudget.getAmount() + ""));

        updateListTransactions();

        Double expensed = 0.0;

        for(BudgetTransaction item : arBudgetTransaction) {
            for(Transaction transaction : item.arTransactions) {
                expensed += transaction.getAmount();
            }
        }

        tvAmount.setText(String.format(getResources().getString(R.string.budget_detail_transaction_total),
                                        Currency.formatCurrency(getContext(), Currency.getCurrencyById(mBudget.getCurrency()), expensed)));

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End onCreateOptionsMenu

    private class BudgetTransaction {
        Category category;
        List<Transaction> arTransactions;
        boolean isShow;

        public BudgetTransaction(Category category, List<Transaction> arTransactions, boolean isShow) {
            this.category = category;
            this.arTransactions = arTransactions;
            this.isShow = isShow;
        }
    }

    private void updateDataSource() {
        LogUtils.logEnterFunction(Tag, null);
        arBudgetTransaction.clear();
        for(int i = 0 ; i < mBudget.getCategories().length; i++) {
            Category category = mDbHelper.getCategory(mBudget.getCategories()[i]);

            List<Transaction> arTransactions = mDbHelper.getBudgetTransactions(category.getId(), getStartDate(mBudget), getEndDate(mBudget), 0);

            arBudgetTransaction.add(new BudgetTransaction(category, arTransactions, true));

        }
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private void updateListTransactions() {
        LogUtils.logEnterFunction(Tag, null);
        llTransactions.removeAllViews();

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        for(BudgetTransaction item : arBudgetTransaction) {

            View            categoryItem    = mInflater.inflate(R.layout.listview_item_budget_transaction_category, null);
            LinearLayout    llCategory      = (LinearLayout) categoryItem.findViewById(R.id.llCategory);
            final ImageView ivExpand        = (ImageView) categoryItem.findViewById(R.id.ivExpand);
            TextView        tvCategory      = (TextView) categoryItem.findViewById(R.id.tvCategory);
            TextView        tvAmount        = (TextView) categoryItem.findViewById(R.id.tvAmount);
            final ListView  lvTransaction   = (ListView) categoryItem.findViewById(R.id.lvTransactions);

            final Animation expand = AnimationUtils.loadAnimation(getActivity(), R.anim.expand);
            expand.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    LogUtils.trace(Tag, "expand Animation");
                    lvTransaction.setVisibility(View.VISIBLE);
                    ivExpand.setImageResource(R.drawable.icon_expanding);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            final Animation shrink = AnimationUtils.loadAnimation(getActivity(), R.anim.shrink);
            shrink.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    LogUtils.trace(Tag, "shrink Animation");
                    lvTransaction.setVisibility(View.GONE);
                    ivExpand.setImageResource(R.drawable.icon_shrinking);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            llCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lvTransaction.getVisibility() == View.GONE) {
                        ivExpand.startAnimation(expand);
                    } else {
                        ivExpand.startAnimation(shrink);
                    }
                }
            });

            tvCategory.setText(String.format(getResources().getString(R.string.budget_detail_transaction_category_sum),
                    item.category.getName()));
            Double expensed = 0.0;
            for(Transaction tran : item.arTransactions) {
                expensed += tran.getAmount();
            }

            tvAmount.setText(Currency.formatCurrency(getContext(), Currency.getCurrencyById(mBudget.getCurrency()), expensed));

            TransactionAdapter adapter = new TransactionAdapter(getActivity(), item.arTransactions);
            lvTransaction.setAdapter(adapter);

            llTransactions.addView(categoryItem);
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private class TransactionAdapter extends ArrayAdapter<TransactionGroup> {
        private class ViewHolder {
            TextView    tvDate;
            TextView    tvDate1;
            TextView    tvDate2;
            TextView    tvIncome;
            TextView    tvExpense;
            LinearLayout llTransactionDetail;
        }

        List<TransactionGroup> mTransactions;
        public TransactionAdapter(Context context, List<TransactionGroup> items) {
            super(context, R.layout.listview_item_transaction_date, items);
            this.mTransactions  = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.listview_item_transaction_date, parent, false);
                viewHolder.tvDate               = (TextView) convertView.findViewById(R.id.tvDate);
                viewHolder.tvDate1              = (TextView) convertView.findViewById(R.id.tvDate1);
                viewHolder.tvDate2              = (TextView) convertView.findViewById(R.id.tvDate2);
                viewHolder.tvIncome             = (TextView) convertView.findViewById(R.id.tvIncome);
                viewHolder.tvExpense            = (TextView) convertView.findViewById(R.id.tvExpense);
                viewHolder.llTransactionDetail  = (LinearLayout) convertView.findViewById(R.id.llTransactionDetail);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if(mTransactions.get(position) != null) {
                Calendar car = Calendar.getInstance();
                Calendar time = mTransactions.get(position).getTime();

                viewHolder.tvDate.setText(time.get(Calendar.DATE) + "");

                if(car.get(Calendar.DAY_OF_YEAR) == time.get(Calendar.DAY_OF_YEAR)) {
                    viewHolder.tvDate1.setText(getResources().getString(R.string.content_today));
                } else if((car.get(Calendar.DAY_OF_YEAR) - 1) == time.get(Calendar.DAY_OF_YEAR)) {
                    viewHolder.tvDate1.setText(getResources().getString(R.string.content_yesterday));
                } else if((car.get(Calendar.DAY_OF_YEAR) - 2) == time.get(Calendar.DAY_OF_YEAR)
                        && getResources().getConfiguration().locale.equals(Locale.forLanguageTag("vi_VN"))) {
                    viewHolder.tvDate1.setText(getResources().getString(R.string.content_before_yesterday));
                } else {
                    viewHolder.tvDate1.setText(mTransactions.get(position).getTime().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()));
                }

                viewHolder.tvDate2.setText(String.format(getResources().getString(R.string.format_month_year),
                        mTransactions.get(position).getTime().get(Calendar.MONTH) + 1,
                        mTransactions.get(position).getTime().get(Calendar.YEAR)));

                Double expense = 0.0, income = 0.0;
                for(Transaction tran : mTransactions.get(position).getArTrans()) {
                    Account fromAccount = mDbHelper.getAccount(tran.getFromAccountId());
                    Account toAccount   = mDbHelper.getAccount(tran.getToAccountId());
                    if(fromAccount != null && toAccount == null) {
                        expense += tran.getAmount();
                    } else if(fromAccount == null && toAccount != null) {
                        income += tran.getAmount();
                    }
                }

                if(expense != 0) {
                    viewHolder.tvExpense.setVisibility(View.VISIBLE);
                    viewHolder.tvExpense.setText(String.format(getResources().getString(R.string.content_expense),
                            Currency.formatCurrency(getContext(),
                                    Currency.CurrencyList.VND,
                                    (expense.longValue() == expense ? expense.longValue() : expense))));
                } else {
                    viewHolder.tvExpense.setVisibility(View.GONE);
                }

                if(income != 0) {
                    viewHolder.tvIncome.setVisibility(View.VISIBLE);
                    viewHolder.tvIncome.setText(String.format(getResources().getString(R.string.content_income),
                            Currency.formatCurrency(getContext(),
                                    Currency.CurrencyList.VND,
                                    (income.longValue() == income ? income.longValue() :  income))));
                } else {
                    viewHolder.tvIncome.setVisibility(View.GONE);
                }

                viewHolder.llTransactionDetail.removeAllViews();
                List<Transaction> arTrans = mTransactions.get(position).getArTrans();
                Collections.sort(arTrans);
                int pos = 0;
                for(final Transaction tran : arTrans) {
                    pos++;

                    Account fromAccount     = mDbHelper.getAccount(tran.getFromAccountId());
                    Account toAccount       = mDbHelper.getAccount(tran.getToAccountId());
                    Category cate           = mDbHelper.getCategory(tran.getCategoryId());

                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    View transactionDetailView = inflater.inflate(R.layout.listview_item_transaction_detail, parent, false);

                    TextView tvCategory         = (TextView) transactionDetailView.findViewById(R.id.tvCategory);
                    String strCategory          = "";
                    if(tran.getTransactionType() == Transaction.TransactionEnum.Expense.getValue()) {
                        strCategory += getResources().getString(R.string.content_expense);
                    } else if(tran.getTransactionType() == Transaction.TransactionEnum.Income.getValue()) {
                        strCategory += getResources().getString(R.string.content_income);
                    } else if(tran.getTransactionType() == Transaction.TransactionEnum.Transfer.getValue()) {
                        strCategory += String.format(getResources().getString(R.string.content_transfer_to), toAccount.getName());
                    } else if(tran.getTransactionType() == Transaction.TransactionEnum.Adjustment.getValue()) {
                        if(fromAccount != null) {
                            strCategory += getResources().getString(R.string.content_expense);
                        } else {
                            strCategory += getResources().getString(R.string.content_income);
                        }
                    }

                    tvCategory.setText(String.format(strCategory, cate != null ? cate.getName() : ""));

                    TextView tvAmount           = (TextView) transactionDetailView.findViewById(R.id.tvAmount);
                    if(fromAccount != null) {
                        tvAmount.setText(Currency.formatCurrency(getContext(),
                                Currency.getCurrencyById(fromAccount.getCurrencyId()),
                                tran.getAmount()));
                    } else if(toAccount != null) {
                        tvAmount.setText(Currency.formatCurrency(getContext(),
                                Currency.getCurrencyById(toAccount.getCurrencyId()),
                                tran.getAmount()));
                    }

                    TextView tvDescription      = (TextView) transactionDetailView.findViewById(R.id.tvDescription);
                    String description = tran.getDescription();

                    if(tran.getFee() != 0) {
                        if(!description.equals("")) {
                            description += "\n";
                        }
                        description += String.format(getResources().getString(R.string.content_transfer_fee),
                                Currency.formatCurrency(getContext(),
                                        Currency.getCurrencyById(fromAccount.getCurrencyId()),
                                        tran.getFee()));
                    }

                    if(!description.equals("")) {
                        tvDescription.setText(description);
                    } else {
                        tvDescription.setVisibility(View.GONE);
                    }


                    TextView tvAccount          = (TextView) transactionDetailView.findViewById(R.id.tvAccount);
                    ImageView ivAccountIcon     = (ImageView) transactionDetailView.findViewById(R.id.ivAccountIcon);

                    if(fromAccount != null) {
                        tvAccount.setText(fromAccount.getName());
                        ivAccountIcon.setImageResource(AccountType.getAccountTypeById(fromAccount.getTypeId()).getIcon());
                    } else if(toAccount != null) {
                        tvAccount.setText(toAccount.getName());
                        ivAccountIcon.setImageResource(AccountType.getAccountTypeById(toAccount.getTypeId()).getIcon());
                    }

                    if(tran.getTransactionType() == Transaction.TransactionEnum.Income.getValue() ||
                            (tran.getTransactionType() == Transaction.TransactionEnum.Adjustment.getValue() && tran.getToAccountId() > 0)) {
                        tvCategory.setTextColor(getResources().getColor(R.color.colorPrimary));
                        tvDescription.setTextColor(getResources().getColor(R.color.colorPrimary));
                        tvAccount.setTextColor(getResources().getColor(R.color.colorPrimary));
                        tvAmount.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }

                    if(pos == mTransactions.get(position).getArTrans().size()) {
                        transactionDetailView.findViewById(R.id.vDivider).setVisibility(View.GONE);
                    }

                    transactionDetailView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentTransactionUpdate nextFrag = new FragmentTransactionUpdate();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Transaction", tran);
                            bundle.putInt("ContainerViewId", R.id.ll_transactions);
                            nextFrag.setArguments(bundle);
                            FragmentListTransaction.this.getFragmentManager().beginTransaction()
                                    .add(R.id.ll_transactions, nextFrag, "FragmentTransactionUpdate")
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });

                    viewHolder.llTransactionDetail.addView(transactionDetailView);
                }

            }

            return convertView;
        }
    }

    private Calendar getStartDate(Budget budget) {
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

        return startDate;
    }

    private Calendar getEndDate(Budget budget) {
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

        return endDate;
    }
}
