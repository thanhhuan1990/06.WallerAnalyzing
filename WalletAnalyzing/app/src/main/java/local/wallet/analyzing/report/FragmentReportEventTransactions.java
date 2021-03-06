package local.wallet.analyzing.report;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.main.Configs;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.AccountType;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Event;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;
import local.wallet.analyzing.transaction.FragmentTransactionCUD;

/**
 * Created by huynh.thanh.huan on 2/22/2016.
 */
public class FragmentReportEventTransactions extends Fragment implements View.OnClickListener {
    public static int               mTab = 4;
    public static final String      Tag = "---[" + mTab + ".5]---ReportEventTransactions";

    private ActivityMain            mActivity;

    private DatabaseHelper  mDbHelper;
    private Configs mConfigs;

    private Event           mEvent;

    private ImageView       ivExpandExpense;
    private TextView        tvTotalExpense;
    private LinearLayout    llExpenses;
    private ImageView       ivExpandIncome;
    private TextView        tvTotalIncome;
    private LinearLayout    llIncomes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mConfigs        = new Configs(getContext());
        mDbHelper       = new DatabaseHelper(getActivity());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            mTab    = bundle.getInt("Tab", mTab);
            mEvent  = mDbHelper.getEvent(bundle.getInt("EventID", 0));

            if(mEvent == null) {
                LogUtils.warn(Tag, "Event is null, RETURN");
                LogUtils.logLeaveFunction(Tag);
                getFragmentManager().popBackStackImmediate();
                return;
            }

            LogUtils.trace(Tag, "Event: " + mEvent.toString());
        }

        LogUtils.logLeaveFunction(Tag);
    } // End onCreate

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);

        View view   = inflater.inflate(R.layout.layout_fragment_report_event_transactions, container, false);

        ivExpandExpense = (ImageView) view.findViewById(R.id.ivExpandExpense);
        ivExpandExpense.setOnClickListener(this);
        tvTotalExpense  = (TextView) view.findViewById(R.id.tvTotalExpense);
        llExpenses      = (LinearLayout) view.findViewById(R.id.llExpenses);
        ivExpandIncome  = (ImageView) view.findViewById(R.id.ivExpandIncome);
        ivExpandIncome.setOnClickListener(this);
        tvTotalIncome   = (TextView) view.findViewById(R.id.tvTotalIncome);
        llIncomes       = (LinearLayout) view.findViewById(R.id.llIncomes);

        // Todo: Update view by data from mDbHelper
        updateListTransactions();

        LogUtils.logLeaveFunction(Tag);
        return view;
    } // End onCreateView

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        super.onActivityCreated(savedInstanceState);

        mActivity   = (ActivityMain) getActivity();

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

        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        View mCustomView            = mInflater.inflate(R.layout.action_bar_with_button_update_export, null);
        TextView  tvTitle           = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(mEvent.getName());
        ImageView ivUpdate          = (ImageView) mCustomView.findViewById(R.id.ivUpdate);
        ivUpdate.setOnClickListener(this);
        ImageView ivExport          = (ImageView) mCustomView.findViewById(R.id.ivExport);
        ivExport.setOnClickListener(this);

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        // Todo: Update view by data from mDbHelper
        updateListTransactions();

        LogUtils.logLeaveFunction(Tag);
    } // End onCreateOptionsMenu

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivUpdate:
                FragmentReportEventUpdate nextFrag = new FragmentReportEventUpdate();
                Bundle bundle = new Bundle();
                bundle.putInt("Tab", mTab);
                bundle.putSerializable("EventID", mEvent.getId());
                nextFrag.setArguments(bundle);
                mActivity.replaceFragment(mTab, R.id.ll_report, nextFrag, FragmentReportEventUpdate.Tag, true);
            break;
            case R.id.ivExport:
                break;
            case R.id.ivExpandExpense:
                final Animation expand = AnimationUtils.loadAnimation(getActivity(), R.anim.expand_no_refresh);
                expand.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        LogUtils.trace(Tag, "expand Animation");
                        llExpenses.setVisibility(View.VISIBLE);
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
                        llExpenses.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                if (llExpenses.getVisibility() == View.VISIBLE) {
                    v.startAnimation(shrink);
                } else {
                    v.startAnimation(expand);
                }

                break;
            case R.id.ivExpandIncome:
                final Animation expandIncome = AnimationUtils.loadAnimation(getActivity(), R.anim.expand_no_refresh);
                expandIncome.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        LogUtils.trace(Tag, "expand Animation");
                        llIncomes.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                final Animation shrinkIncome = AnimationUtils.loadAnimation(getActivity(), R.anim.shrink_no_refresh);
                shrinkIncome.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        LogUtils.trace(Tag, "shrink Animation");
                        llIncomes.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                if (llIncomes.getVisibility() == View.VISIBLE) {
                    v.startAnimation(shrinkIncome);
                } else {
                    v.startAnimation(expandIncome);
                }

                break;
            default:
                break;
        }
    }

    /**
     * Update list Transactions
     */
    private void updateListTransactions() {
        LogUtils.logEnterFunction(Tag);
        llExpenses.removeAllViews();
        llIncomes.removeAllViews();

        Double expense = 0.0, income = 0.0;

        List<Transaction> arTransactions = mDbHelper.getTransactionsByEvent(mEvent.getId());

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        for(final Transaction transaction : arTransactions) {

            View        transactionView     = mInflater.inflate(R.layout.listview_item_budget_transaction_detail, null);
            TextView    tvTranCategory      = (TextView) transactionView.findViewById(R.id.tvCategory);
            TextView    tvTranAmount        = (TextView) transactionView.findViewById(R.id.tvAmount);
            TextView    tvDescription       = (TextView) transactionView.findViewById(R.id.tvDescription);
            TextView    tvDate              = (TextView) transactionView.findViewById(R.id.tvDate);
            TextView    tvAccount           = (TextView) transactionView.findViewById(R.id.tvAccount);
            ImageView   ivAccountIcon       = (ImageView) transactionView.findViewById(R.id.ivAccountIcon);

            // CATEGORY
            tvTranCategory.setText(String.format(getResources().getString(R.string.content_expense),
                                                    mDbHelper.getCategory(transaction.getCategoryId()).getName()));

            // AMOUNT
            Account fromAccount     = mDbHelper.getAccount(transaction.getFromAccountId());
            Account toAccount       = mDbHelper.getAccount(transaction.getToAccountId());
            tvTranAmount.setText(Currency.formatCurrency(getActivity(),
                                                        fromAccount != null ? fromAccount.getCurrencyId() : toAccount.getCurrencyId(),
                                                        transaction.getAmount()));

            // DESCRIPTION
            if(!transaction.getDescription().equals("")) {
                tvDescription.setText(transaction.getDescription());
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            // DATE
            tvDate.setText(String.format(getResources().getString(R.string.format_day_month_year),
                                        transaction.getTime().get(Calendar.DAY_OF_MONTH),
                                        transaction.getTime().get(Calendar.MONTH),
                                        transaction.getTime().get(Calendar.YEAR)));

            transactionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FragmentTransactionCUD nextFrag = new FragmentTransactionCUD();
                    Bundle bundle = new Bundle();
                    bundle.putInt("Tab", mTab);
                    bundle.putSerializable("Transaction", transaction);
                    nextFrag.setArguments(bundle);
                    mActivity.addFragment(mTab, R.id.ll_report, nextFrag, "FragmentTransactionCUD", true);
                }
            });

            if (transaction.getTransactionType() == Transaction.TransactionEnum.Expense.getValue()) {

                // ACCOUNT
                tvAccount.setText(fromAccount.getName());
                ivAccountIcon.setImageResource(AccountType.getAccountTypeById(fromAccount.getTypeId()).getIcon());

                expense += transaction.getAmount();
                llExpenses.addView(transactionView);
            } else if (transaction.getTransactionType() == Transaction.TransactionEnum.Income.getValue()) {

                // ACCOUNT
                tvAccount.setText(toAccount.getName());
                ivAccountIcon.setImageResource(AccountType.getAccountTypeById(toAccount.getTypeId()).getIcon());

                income += transaction.getAmount();
                llIncomes.addView(transactionView);
            } else if (transaction.getTransactionType() == Transaction.TransactionEnum.Adjustment.getValue()) {
                if(transaction.getFromAccountId() != 0) {

                    // ACCOUNT
                    tvAccount.setText(fromAccount.getName());
                    ivAccountIcon.setImageResource(AccountType.getAccountTypeById(fromAccount.getTypeId()).getIcon());

                    expense += transaction.getAmount();
                    llExpenses.addView(transactionView);
                } else if(transaction.getToAccountId() != 0) {

                    // ACCOUNT
                    tvAccount.setText(toAccount.getName());
                    ivAccountIcon.setImageResource(AccountType.getAccountTypeById(toAccount.getTypeId()).getIcon());

                    income += transaction.getAmount();
                    llIncomes.addView(transactionView);
                }
            }

        } // End for(final Transaction transaction : arTransactions)

        tvTotalExpense.setText(String.format(getResources().getString(R.string.content_expense,
                Currency.formatCurrency(getContext(), mConfigs.getInt(Configs.Key.Currency), expense))));
        tvTotalIncome.setText(String.format(getResources().getString(R.string.content_income,
                Currency.formatCurrency(getContext(), mConfigs.getInt(Configs.Key.Currency), income))));

        LogUtils.logLeaveFunction(Tag);
    } // End updateListTransactions
} // End class FragmentReportEventTransactions
