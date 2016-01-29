package local.wallet.analyzing;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import local.wallet.analyzing.Utils.LogUtils;
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
public class FragmentTransactions extends Fragment {

    private static final String TAG = "FragmentTransactions";

    private DatabaseHelper          db;

    private List<TransactionGroup>  arGroupTrans;
    private ListView                lvTransaction;
    private TransactionAdapter      mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, null);
        return inflater.inflate(R.layout.layout_fragment_transactions, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        db = new DatabaseHelper(getActivity());

        // Get all transaction
        List<Transaction> arTrans = db.getAllTransactions();
        // Sort transaction
        Collections.sort(arTrans);

        // Add to Group Transaction
        arGroupTrans    = TransactionGroup.parseTransactions(arTrans);

        lvTransaction   = (ListView) getView().findViewById(R.id.lvTransaction);
        mAdapter = new TransactionAdapter(getActivity(), arGroupTrans);
        lvTransaction.setAdapter(mAdapter);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onResume() {
        LogUtils.logEnterFunction(TAG, null);
        super.onResume();
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);

        super.onCreateOptionsMenu(menu, inflater);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_transaction, null);
        ((ActivityMain)getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(TAG, null, null);
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
                    if(db.getCategory(tran.getCategoryId()).isExpense()) {
                        expense += tran.getAmount();
                    } else {
                        income += tran.getAmount();
                    }
                }

                if(expense != 0) {
                    viewHolder.tvExpense.setVisibility(View.VISIBLE);
                    viewHolder.tvExpense.setText(getResources().getString(R.string.content_expense) + ": " + Currency.formatCurrency(getContext(), Currency.CurrencyList.VND, (expense.longValue() == expense ? expense.longValue() : expense)));
                } else {
                    viewHolder.tvExpense.setVisibility(View.GONE);
                }

                if(income != 0) {
                    viewHolder.tvIncome.setVisibility(View.VISIBLE);
                    viewHolder.tvIncome.setText(getResources().getString(R.string.content_income) + ": " + Currency.formatCurrency(getContext(), Currency.CurrencyList.VND, (income.longValue() == income ? income.longValue() :  income)));
                } else {
                    viewHolder.tvIncome.setVisibility(View.GONE);
                }

                viewHolder.llTransactionDetail.removeAllViews();
                int pos = 0;
                for(Transaction tran : mTransactions.get(position).getArTrans()) {
                    pos++;

                    Account account = db.getAccount(tran.getAccountId());
                    Category cate = db.getCategory(tran.getCategoryId());

                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    View transactionDetailView = inflater.inflate(R.layout.listview_item_transaction_detail, parent, false);


                    TextView tvCategory         = (TextView) transactionDetailView.findViewById(R.id.tvCategory);
                    String strCategory          = (cate.isExpense() ? getResources().getString(R.string.content_expense) : getResources().getString(R.string.content_income)) + ": " + cate.getName();
                    tvCategory.setText(strCategory);
                    tvCategory.setTextColor(getResources().getColor(cate.isExpense() ? android.R.color.black : R.color.colorPrimary));

                    TextView tvAmount           = (TextView) transactionDetailView.findViewById(R.id.tvAmount);
                    tvAmount.setText(Currency.formatCurrency(getContext(), Currency.getCurrencyById(account.getCurrencyId()), tran.getAmount()));
                    tvAmount.setTextColor(getResources().getColor(cate.isExpense() ? android.R.color.black : R.color.colorPrimary));

                    TextView tvDescription      = (TextView) transactionDetailView.findViewById(R.id.tvDescription);
                    tvDescription.setText(tran.getDescription());
                    tvDescription.setTextColor(getResources().getColor(cate.isExpense() ? android.R.color.black : R.color.colorPrimary));

                    TextView tvAccount          = (TextView) transactionDetailView.findViewById(R.id.tvAccount);
                    tvAccount.setText(account.getName());

                    ImageView ivAccountIcon     = (ImageView) transactionDetailView.findViewById(R.id.ivAccountIcon);
                    ivAccountIcon.setImageResource(AccountType.getAccountTypeById(account.getTypeId()).getIcon());

                    if(pos == mTransactions.get(position).getArTrans().size()) {
                        transactionDetailView.findViewById(R.id.vDivider).setVisibility(View.GONE);
                    }

                    viewHolder.llTransactionDetail.addView(transactionDetailView);
                }

            }

            return convertView;
        }
    }

    public void updateListTransaction() {
        // Get all transaction
        List<Transaction> arTrans = db.getAllTransactions();
        // Sort transaction
        Collections.sort(arTrans);

        // Add to Group Transaction
        List<TransactionGroup> arTemp = TransactionGroup.parseTransactions(arTrans);
        arGroupTrans.clear();
        arGroupTrans.addAll(arTemp);
        mAdapter.notifyDataSetChanged();
    }
}
