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
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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

    private DatabaseHelper db;

    private List<Transaction> arTrans;
    private List<TransactionGroup> arGroupTrans;

    private ListView    lvTransaction;
    private TransactionAdapter  mAdapter;

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
        arTrans = db.getAllTransactions();
        // Sort transaction
        Collections.sort(arTrans);

        // Add to Group Transaction
        arGroupTrans    = TransactionGroup.parseTransactions(db.getAllTransactions());

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
            ListView    lvTransactionDetail;
        }

        private List<TransactionGroup> mTransactions;
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
                viewHolder.lvTransactionDetail  = (ListView) convertView.findViewById(R.id.lvTransactionDetail);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if(mTransactions.get(position) != null) {
                Calendar car = Calendar.getInstance();
                Calendar time = mTransactions.get(position).getTime();

                viewHolder.tvDate.setText(time.get(Calendar.DATE) + "");

                if(car.get(Calendar.DAY_OF_YEAR) == time.get(Calendar.DAY_OF_YEAR)) {
                    viewHolder.tvDate1.setText("Today");
                } else if((car.get(Calendar.DAY_OF_YEAR) - 1) == time.get(Calendar.DAY_OF_YEAR)) {
                    viewHolder.tvDate1.setText("Yesterday");
                } else {
                    viewHolder.tvDate1.setText(new SimpleDateFormat("dd-MM-yyyy").format(time));
                }
                viewHolder.tvDate2.setText(mTransactions.get(position).getTime().get(Calendar.MONTH) + "/" + mTransactions.get(position).getTime().get(Calendar.YEAR));

                Double expense = 0.1, income = 0.0;
                for(Transaction tran : mTransactions.get(position).getArTrans()) {
                    if(db.getCategory(tran.getCategoryId()).isExpense()) {
                        expense += tran.getAmount();
                    } else {
                        income += tran.getAmount();
                    }
                }

                if(expense != 0) {
                    viewHolder.tvExpense.setVisibility(View.VISIBLE);
                    viewHolder.tvExpense.setText(getResources().getString(R.string.content_expense) + ": " + expense);
                } else {
                    viewHolder.tvExpense.setVisibility(View.GONE);
                }

                if(income != 0) {
                    viewHolder.tvIncome.setVisibility(View.VISIBLE);
                    viewHolder.tvIncome.setText(getResources().getString(R.string.content_income) + ": " + income);
                } else {
                    viewHolder.tvIncome.setVisibility(View.GONE);
                }
            }

            return convertView;
        }
    }

    private class TransactionDetailAdapter extends ArrayAdapter<Transaction> {
        private class ViewHolder {
            TextView    tvCategory;
            TextView    tvAmount;
            ImageView   ivCurrencyIcon;
            TextView    tvDescription;
            TextView    tvAccount;
            ImageView   ivAccountIcon;
        }

        private List<Transaction> mTransactions;
        public TransactionDetailAdapter(Context context, List<Transaction> items) {
            super(context, R.layout.listview_item_transaction_detail, items);
            this.mTransactions  = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.listview_item_transaction_detail, parent, false);
                viewHolder.tvCategory       = (TextView) convertView.findViewById(R.id.tvCategory);
                viewHolder.tvAmount         = (TextView) convertView.findViewById(R.id.tvAmount);
                viewHolder.ivCurrencyIcon   = (ImageView) convertView.findViewById(R.id.ivCurrencyIcon);
                viewHolder.tvDescription    = (TextView) convertView.findViewById(R.id.tvDescription);
                viewHolder.tvAccount        = (TextView) convertView.findViewById(R.id.tvAccount);
                viewHolder.ivAccountIcon    = (ImageView) convertView.findViewById(R.id.ivAccountIcon);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if(mTransactions.get(position) != null) {
                // Set category value
                Category cate = db.getCategory(mTransactions.get(position).getCategoryId());
                String strCategory = cate.isExpense() ? getResources().getString(R.string.content_expense) : getResources().getString(R.string.content_income) + ": " + cate.getName();
                viewHolder.tvCategory.setText(strCategory);
                // Set amount value
                viewHolder.tvAmount.setText(mTransactions.get(position).getAmount() + "");
                // Set amount currency
                Account account = db.getAccount(mTransactions.get(position).getAccountId());
                viewHolder.ivCurrencyIcon.setImageResource(Currency.getCurrencyById(account.getCurrencyId()).getIcon());
                //Set description value
                viewHolder.tvDescription.setText(mTransactions.get(position).getDescription());
                // Set account value
                viewHolder.tvAccount.setText(account.getName());
                viewHolder.ivAccountIcon.setImageResource(AccountType.getAccountTypeById(account.getTypeId()).getIcon());
            }

            return convertView;
        }
    }

    public void updateListTransaction() {
        arGroupTrans    = TransactionGroup.parseTransactions(db.getAllTransactions());
        mAdapter.notifyDataSetChanged();
    }
}
