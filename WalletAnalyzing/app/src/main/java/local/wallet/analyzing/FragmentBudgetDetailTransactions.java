package local.wallet.analyzing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.AccountType;
import local.wallet.analyzing.model.Budget;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Transaction;
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
    private LinearLayout            llCategoryTransactions;
    private List<BudgetTransaction> arBudgetTransaction = new ArrayList<BudgetTransaction>();
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
        llCategoryTransactions      = (LinearLayout) getView().findViewById(R.id.llCategoryTransactions);
        tvAmount                    = (TextView) getView().findViewById(R.id.tvAmount);

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

        Calendar startDate;
        Calendar endDate;
        if(mBudget.getRepeatType() == 0) {
            startDate   = mBudget.getStartDate();
            endDate     = mBudget.getEndDate();
        } else {
            startDate   = getStartDate(mBudget);
            endDate     = getEndDate(mBudget);
        }

        tvDescription.setText(String.format(getResources().getString(R.string.budget_detail_transaction_description),
                                            startDate.get(Calendar.DAY_OF_MONTH),
                                            startDate.get(Calendar.MONTH),
                                            endDate.get(Calendar.DAY_OF_MONTH),
                                            endDate.get(Calendar.MONTH),
                                            Currency.formatCurrency(getContext(),
                                                                    Currency.getCurrencyById(mBudget.getCurrency()),
                                                                    mBudget.getAmount())));

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

    /**
     * Update data from Database
     */
    private void updateDataSource() {
        LogUtils.logEnterFunction(Tag, null);
        arBudgetTransaction.clear();
        for(int i = 0 ; i < mBudget.getCategories().length; i++) {
            Category category = mDbHelper.getCategory(mBudget.getCategories()[i]);

            List<Transaction> arTransactions = new ArrayList<Transaction>();
            if(mBudget.getRepeatType() == 0) {
                arTransactions = mDbHelper.getBudgetTransactions(category.getId(), mBudget.getStartDate(), mBudget.getEndDate(), 0);
            } else {
                arTransactions = mDbHelper.getBudgetTransactions(category.getId(), getStartDate(mBudget), getEndDate(mBudget), 0);
            }

            arBudgetTransaction.add(new BudgetTransaction(category, arTransactions, true));

        }
        LogUtils.logLeaveFunction(Tag, null, null);
    } // End updateDataSource

    /**
     * Update list Transactions
     */
    private void updateListTransactions() {
        LogUtils.logEnterFunction(Tag, null);
        llCategoryTransactions.removeAllViews();

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        for(BudgetTransaction category : arBudgetTransaction) {

            View                    categoryView    = mInflater.inflate(R.layout.listview_item_budget_transaction_category, null);
            LinearLayout            llCategory      = (LinearLayout) categoryView.findViewById(R.id.llCategory);
            final ImageView         ivExpand        = (ImageView) categoryView.findViewById(R.id.ivExpand);
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

            tvAmount.setText(Currency.formatCurrency(getContext(), Currency.getCurrencyById(mBudget.getCurrency()), expensed));

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
                tvTranAmount.setText(Currency.formatCurrency(getActivity(), Currency.getCurrencyById(mBudget.getCurrency()), transaction.getAmount()));
                if(!transaction.getDescription().equals("")) {
                    tvDescription.setText(transaction.getDescription());
                } else {
                    tvDescription.setVisibility(View.GONE);
                }

                tvDate.setText(String.format(getResources().getString(R.string.format_day_month_year),
                                                transaction.getTime().get(Calendar.DAY_OF_MONTH),
                                                transaction.getTime().get(Calendar.MONTH),
                                                transaction.getTime().get(Calendar.YEAR)));
                tvAccount.setText(mDbHelper.getAccount(transaction.getFromAccountId()).getName());
                ivAccountIcon.setImageResource(AccountType.getAccountTypeById(mDbHelper.getAccount(transaction.getFromAccountId()).getTypeId()).getIcon());

                transactionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentTransactionUpdate nextFrag = new FragmentTransactionUpdate();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Transaction", transaction);
                        bundle.putInt("ContainerViewId", R.id.layout_account);
                        nextFrag.setArguments(bundle);
                        FragmentBudgetDetailTransactions.this.getFragmentManager().beginTransaction()
                                .add(R.id.layout_budget, nextFrag, "FragmentTransactionUpdate")
                                .addToBackStack(null)
                                .commit();
                    }
                });

                llTransactions.addView(transactionView);
            }

            llCategoryTransactions.addView(categoryView);
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End updateListTransactions

    private Calendar getStartDate(Budget budget) {
        LogUtils.logEnterFunction(Tag, null);
        Calendar today      = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, today.getActualMinimum(Calendar.HOUR_OF_DAY));
        today.set(Calendar.MINUTE,      today.getActualMinimum(Calendar.MINUTE));
        today.set(Calendar.SECOND,      today.getActualMinimum(Calendar.SECOND));
        today.set(Calendar.MILLISECOND, today.getActualMinimum(Calendar.MILLISECOND));

        Calendar startDate  = Calendar.getInstance();
        startDate.setTimeInMillis(budget.getStartDate().getTimeInMillis());
        Calendar endDate    = Calendar.getInstance();
        endDate.setTimeInMillis(budget.getStartDate().getTimeInMillis());

        int repeatType      = budget.getRepeatType();

        while (endDate.getTimeInMillis() <= today.getTimeInMillis()) {
            LogUtils.trace(Tag, "End Date = " + endDate.getTimeInMillis());
            LogUtils.trace(Tag, "today = " + today.getTimeInMillis());
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

        LogUtils.logLeaveFunction(Tag, null, null);
        return startDate;
    }

    private Calendar getEndDate(Budget budget) {
        LogUtils.logEnterFunction(Tag, null);
        Calendar today      = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, today.getActualMinimum(Calendar.HOUR_OF_DAY));
        today.set(Calendar.MINUTE,      today.getActualMinimum(Calendar.MINUTE));
        today.set(Calendar.SECOND,      today.getActualMinimum(Calendar.SECOND));
        today.set(Calendar.MILLISECOND, today.getActualMinimum(Calendar.MILLISECOND));

        Calendar startDate  = Calendar.getInstance();
        startDate.setTimeInMillis(budget.getStartDate().getTimeInMillis());
        Calendar endDate    = Calendar.getInstance();
        endDate.setTimeInMillis(budget.getStartDate().getTimeInMillis());

        int repeatType      = budget.getRepeatType();

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

        LogUtils.logLeaveFunction(Tag, null, null);
        return endDate;
    }
}
