package local.wallet.analyzing.transactions;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import local.wallet.analyzing.transaction.FragmentTransactionCUD;
import local.wallet.analyzing.R;
import local.wallet.analyzing.utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.main.Configs;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.AccountType;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.model.TransactionGroup;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentListTransaction extends ListFragment {
    public static final int         mTab = 0;
    public static final String      Tag = "---[" + mTab + "]---ListTransaction";

    private ActivityMain            mActivity;

    private DatabaseHelper          mDbHelper;
    private Configs mConfigs;

    private List<TransactionGroup>  arGroupTrans;
    private TransactionAdapter      mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        LogUtils.logLeaveFunction(Tag);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        LogUtils.logLeaveFunction(Tag);
        return inflater.inflate(R.layout.layout_fragment_list_transaction, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        super.onActivityCreated(savedInstanceState);

        mActivity       = (ActivityMain) getActivity();

        mDbHelper       = new DatabaseHelper(getActivity());
        mDbHelper.insertDefaultCategories();
        mConfigs        = new Configs(getActivity());

        // Get all transaction
        List<Transaction> arTrans = mDbHelper.getAllTransactions();
        // Sort transaction
        Collections.sort(arTrans);

        // Add to Group Transaction
        arGroupTrans    = TransactionGroup.parseTransactions(arTrans);

        mAdapter = new TransactionAdapter(getActivity(), arGroupTrans);
        setListAdapter(mAdapter);

        updateActionBar();
        updateListTransaction();

        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onResume() {
        LogUtils.logEnterFunction(Tag);
        super.onResume();

        if(mTab != mActivity.getCurrentVisibleItem()) {
            LogUtils.error(Tag, "Wrong tab: " + mTab + " vs " + mActivity.getCurrentVisibleItem());
            LogUtils.logLeaveFunction(Tag);
            return;
        }

        updateActionBar();
        updateListTransaction();

        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag);
        super.onCreateOptionsMenu(menu, inflater);

        if(mTab != mActivity.getCurrentVisibleItem()) {
            LogUtils.error(Tag, "Wrong tab: " + mTab + " vs " + mActivity.getCurrentVisibleItem());
            LogUtils.logLeaveFunction(Tag);
            return;
        }

        updateActionBar();
        updateListTransaction();
        LogUtils.logLeaveFunction(Tag);
    }

    private void updateActionBar() {
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_transaction, null);
        ((ActivityMain)getActivity()).updateActionBar(mCustomView);
    }

    private void updateListTransaction() {
        // Get all transaction
        List<Transaction> arTrans = mDbHelper.getAllTransactions();
        // Sort transaction
        Collections.sort(arTrans);

        // Add to Group Transaction
        List<TransactionGroup> arTemp = TransactionGroup.parseTransactions(arTrans);
        arGroupTrans.clear();
        arGroupTrans.addAll(arTemp);
        mAdapter.notifyDataSetChanged();
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
                        && getResources().getConfiguration().locale.equals(new Locale("vn"))) {
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
                                                                        mConfigs.getInt(Configs.Key.Currency),
                                                                        (expense.longValue() == expense ? expense.longValue() : expense))));
                } else {
                    viewHolder.tvExpense.setVisibility(View.GONE);
                }

                if(income != 0) {
                    viewHolder.tvIncome.setVisibility(View.VISIBLE);
                    viewHolder.tvIncome.setText(String.format(getResources().getString(R.string.content_income),
                                                                Currency.formatCurrency(getContext(),
                                                                        mConfigs.getInt(Configs.Key.Currency),
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
                                                                    fromAccount.getCurrencyId(),
                                                                    tran.getAmount()));
                    } else if(toAccount != null) {
                        tvAmount.setText(Currency.formatCurrency(getContext(),
                                                                    toAccount.getCurrencyId(),
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
                                        fromAccount.getCurrencyId(),
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
                            FragmentTransactionCUD nextFrag = new FragmentTransactionCUD();
                            Bundle bundle = new Bundle();
                            bundle.putInt("Tab", mTab);
                            bundle.putSerializable("Transaction", tran);
                            nextFrag.setArguments(bundle);
                            mActivity.addFragment(mTab, R.id.ll_transactions, nextFrag, "FragmentTransactionCUD", true);
                        }
                    });

                    viewHolder.llTransactionDetail.addView(transactionDetailView);
                }

            }

            return convertView;
        }
    }
}
