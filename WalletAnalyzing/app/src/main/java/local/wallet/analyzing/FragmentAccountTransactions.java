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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.AccountType;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/3/2016.
 */
public class FragmentAccountTransactions extends Fragment {

    private static final String TAG = "FragmentAccountTransactions";

    private String              mTagOfSource = "";
    private int                 mAccountId;

    private DatabaseHelper      mDbHelper;
    private List<Transaction>   arTransactions = new ArrayList<Transaction>();
//    private TransactionAdapter  transactionAdapter;

    private TextView            tvInitBalance;
    private TextView            tvBalance;
    private LinearLayout        llTransactions;
//    private ListView            lvTransactions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        /* Get data from Bundle */
        Bundle bundle                   = this.getArguments();
        mTagOfSource                    = bundle.getString("Tag");
        mAccountId                      = bundle.getInt("AccountID", 0);

        LogUtils.trace(TAG, "mTagOfSource = " + mTagOfSource);
        LogUtils.trace(TAG, "mAccountId = " + mAccountId);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        // Set this fragment tag to ActivityMain
        String myTag = getTag();
        ((ActivityMain)getActivity()).setFragmentAccountTransactions(myTag);

        LogUtils.logLeaveFunction(TAG, null, null);
        return inflater.inflate(R.layout.layout_fragment_account, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onActivityCreated(savedInstanceState);

        /* Initialize Database, insert default category */
        mDbHelper = new DatabaseHelper(getActivity());

        Account account = mDbHelper.getAccount(mAccountId);
        tvInitBalance       = (TextView) getView().findViewById(R.id.tvAccountInitBalance);
        tvInitBalance.setText(Currency.formatCurrency(getContext(), Currency.getCurrencyById(account.getCurrencyId()), mDbHelper.getAccount(mAccountId).getInitBalance()));

        tvBalance           = (TextView) getView().findViewById(R.id.tvAccountRemain);
        tvBalance.setText(Currency.formatCurrency(getContext(), Currency.getCurrencyById(account.getCurrencyId()), mDbHelper.getAccountRemain(mAccountId)));

        updateListTransactions();

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);
        super.onCreateOptionsMenu(menu, inflater);

        /* Init ActionBar */
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_add, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(mDbHelper.getAccount(mAccountId).getName());
        ((ActivityMain)getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    private void updateListTransactions() {

        arTransactions = mDbHelper.getAllTransactions(mAccountId);
        Collections.sort(arTransactions);

        LinearLayout    llTransactions = (LinearLayout) getView().findViewById(R.id.llTransactions);

        int position = 0;
        for(Transaction tran : arTransactions) {

            LayoutInflater mInflater = LayoutInflater.from(getActivity());
            View mTransactionView = mInflater.inflate(R.layout.listview_item_account_transaction, null);
            TextView tvCategory     = (TextView) mTransactionView.findViewById(R.id.tvCategory);
            TextView tvDescription  = (TextView) mTransactionView.findViewById(R.id.tvDescription);
            TextView tvDate         = (TextView) mTransactionView.findViewById(R.id.tvDate);
            TextView tvAmount       = (TextView) mTransactionView.findViewById(R.id.tvAmount);
            TextView tvBalance      = (TextView) mTransactionView.findViewById(R.id.tvBalance);

            if(position % 2 == 0) {
                mTransactionView.setBackgroundColor(getResources().getColor(R.color.listview_account_transactions_event_background));
            } else {
                mTransactionView.setBackgroundColor(getResources().getColor(R.color.listview_account_transactions_odd_background));
            }

            String category = "";
            Category cate = mDbHelper.getCategory(tran.getCategoryId());
            if(cate != null) {
                category += String.format(cate.isExpense() ? getResources().getString(R.string.content_expense) : getResources().getString(R.string.content_income),
                                            cate.getName() );
            }

            if(!category.equals("")) {
                tvCategory.setText(category);
            } else {
                tvCategory.setVisibility(View.GONE);
            }

            if(!tran.getDescription().equals("")) {
                tvDescription.setText(tran.getDescription());
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            tvDate.setText(String.format(getResources().getString(R.string.format_day_month_year),
                    tran.getTime().get(Calendar.DAY_OF_MONTH),
                    tran.getTime().get(Calendar.MONTH) + 1,
                    tran.getTime().get(Calendar.YEAR)));

            Account fromAcc = mDbHelper.getAccount(tran.getFromAccountId());
            Account toAcc   = mDbHelper.getAccount(tran.getToAccountId());

            if(fromAcc != null) {
                tvAmount.setText(Currency.formatCurrency(getContext(), Currency.getCurrencyById(fromAcc.getCurrencyId()), tran.getAmount()));
                tvBalance.setText(String.format(getResources().getString(R.string.account_list_balance),
                                                Currency.formatCurrency(getContext(), Currency.getCurrencyById(fromAcc.getCurrencyId()), mDbHelper.getAccountRemainAfter(fromAcc.getId(), tran.getTime()))));
            } else if(toAcc != null) {
                tvAmount.setText(Currency.formatCurrency(getContext(), Currency.getCurrencyById(toAcc.getCurrencyId()), tran.getAmount()));
                tvBalance.setText(String.format(getResources().getString(R.string.account_list_balance),
                        Currency.formatCurrency(getContext(), Currency.getCurrencyById(toAcc.getCurrencyId()), mDbHelper.getAccountRemainAfter(toAcc.getId(), tran.getTime()))));

                tvCategory.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvDescription.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvAmount.setTextColor(getResources().getColor(R.color.colorPrimary));
            }

            llTransactions.addView(mTransactionView);
            position++;
        }
    }
}
