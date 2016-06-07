package local.wallet.analyzing.report;

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
import java.util.Collections;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.main.Configurations;
import local.wallet.analyzing.model.AccountType;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Debt;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;
import local.wallet.analyzing.transaction.FragmentTransactionCUD;

/**
 * Created by huynh.thanh.huan on 04/04/2016.
 */
public class FragmentReportLentBorrowedDetail extends Fragment {
    public static final String Tag = "ReportLentBorrowedDetail";

    private DatabaseHelper          mDbHelper;
    private Configurations mConfigs;

    private boolean                 isLent      = false;
    private String                  strPeople   = "";
    private ListView                lvTransaction;
    private ArrayList<Transaction>  arTransactions = new ArrayList<>();
    private TransactionAdapter      mAdapter;

    private Double starting_balance = 0.0, finish = 0.0;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreateOptionsMenu(menu, inflater);

        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        View mActionBar             = mInflater.inflate(R.layout.action_bar_only_title, null);
        ((TextView) mActionBar.findViewById(R.id.tvTitle)).setText(strPeople);

        ((ActivityMain) getActivity()).updateActionBar(mActionBar);

        updateDataSource();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle       = this.getArguments();
        isLent              = bundle.getBoolean("Lent");
        strPeople           = bundle.getString("People");

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        mConfigs            = new Configurations(getContext());
        mDbHelper           = new DatabaseHelper(getActivity());

        View view = inflater.inflate(R.layout.layout_fragment_report_lent_borrowed_detail, container, false);

        lvTransaction       = (ListView) view.findViewById(R.id.lvTransaction);
        mAdapter            = new TransactionAdapter(getActivity(), arTransactions);
        lvTransaction.setAdapter(mAdapter);

        LogUtils.logLeaveFunction(Tag, null, null);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        if(getView() != null) {
            updateDataSource();
        }
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onResume() {
        LogUtils.logEnterFunction(Tag, null);
        super.onResume();
        if(getView() != null) {
            updateDataSource();
        }
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private void updateDataSource() {
        LogUtils.logEnterFunction(Tag, null);

        List<Debt> arDebt = mDbHelper.getAllDebtByPeople(strPeople);

        starting_balance    = 0.0;
        finish              = 0.0;
        arTransactions.clear();

        for(Debt debt : arDebt) {
            Category category = mDbHelper.getCategory(debt.getCategoryId());
            if(isLent) {
                if(category.isExpense() && category.getDebtType() == Category.EnumDebt.MORE) { // Lend
                    starting_balance += debt.getAmount();
                    arTransactions.add(mDbHelper.getTransaction(debt.getTransactionId()));
                } else if(!category.isExpense() && category.getDebtType() == Category.EnumDebt.LESS) { // Debt Collecting
                    finish += debt.getAmount();
                    arTransactions.add(mDbHelper.getTransaction(debt.getTransactionId()));
                }
            } else {
                if(!category.isExpense() && category.getDebtType() == Category.EnumDebt.MORE) { // Borrow
                    starting_balance += debt.getAmount();
                    arTransactions.add(mDbHelper.getTransaction(debt.getTransactionId()));
                } else if(category.isExpense() && category.getDebtType() == Category.EnumDebt.LESS) { // Repayment
                    finish += debt.getAmount();
                    arTransactions.add(mDbHelper.getTransaction(debt.getTransactionId()));
                }
            }
        }

        Collections.sort(arTransactions);

        TextView tvTitleStartingBalance = (TextView) getView().findViewById(R.id.tvTitleStartingBalance);
        TextView tvTitleFinished        = (TextView) getView().findViewById(R.id.tvTitleFinished);
        TextView tvTitleRemaining       = (TextView) getView().findViewById(R.id.tvTitleRemaining);
        if(isLent) { // Lending
            tvTitleStartingBalance.setText(String.format(getResources().getString(R.string.report_lent_borrowed_detail_lent_starting_balance), starting_balance));
            tvTitleFinished.setText(String.format(getResources().getString(R.string.report_lent_borrowed_detail_lent_collected), (int)((finish * 100) / starting_balance)));
            tvTitleRemaining.setText(String.format(getResources().getString(R.string.report_lent_borrowed_detail_lent_remaining), 100 - (int)((finish * 100) / starting_balance)));
        } else { // Borrowing
            tvTitleStartingBalance.setText(String.format(getResources().getString(R.string.report_lent_borrowed_detail_borrowed_starting_balance), starting_balance));
            tvTitleFinished.setText(String.format(getResources().getString(R.string.report_lent_borrowed_detail_borrowed_repaid), (int)((finish * 100) / starting_balance)));
            tvTitleRemaining.setText(String.format(getResources().getString(R.string.report_lent_borrowed_detail_borrowed_remaining), 100 - (int)((finish * 100) / starting_balance)));
        }

        TextView tvStartingBalance      = (TextView) getView().findViewById(R.id.tvStartingBalance);
        tvStartingBalance.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), starting_balance));

        TextView tvFinish               = (TextView) getView().findViewById(R.id.tvFinish);
        tvFinish.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), finish));

        TextView tvRemaining            = (TextView) getView().findViewById(R.id.tvRemaining);
        tvRemaining.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), (starting_balance - finish)));

        mAdapter.notifyDataSetChanged();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private class TransactionAdapter extends ArrayAdapter<Transaction> {
        private class ViewHolder {
            TextView    tvTranCategory;
            TextView    tvTranAmount;
            TextView    tvDescription;
            TextView    tvDate;
            TextView    tvAccount;
            ImageView   ivAccountIcon ;
        }

        private List<Transaction> mList;

        public TransactionAdapter(Context context, List<Transaction> items) {
            super(context, R.layout.listview_item_budget_transaction_detail, items);
            this.mList = items;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Transaction getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater         = LayoutInflater.from(getContext());
                convertView                     = inflater.inflate(R.layout.listview_item_budget_transaction_detail, parent, false);
                viewHolder.tvTranCategory       = (TextView) convertView.findViewById(R.id.tvCategory);
                viewHolder.tvTranAmount         = (TextView) convertView.findViewById(R.id.tvAmount);
                viewHolder.tvDescription        = (TextView) convertView.findViewById(R.id.tvDescription);
                viewHolder.tvDate               = (TextView) convertView.findViewById(R.id.tvDate);
                viewHolder.tvAccount            = (TextView) convertView.findViewById(R.id.tvAccount);
                viewHolder.ivAccountIcon        = (ImageView) convertView.findViewById(R.id.ivAccountIcon);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Transaction transaction = getItem(position);
            Category category = mDbHelper.getCategory(transaction.getCategoryId());

            if(category.isExpense()) {
                viewHolder.tvTranCategory.setText(String.format(getResources().getString(R.string.content_expense), category.getName()));
            } else {
                viewHolder.tvTranCategory.setText(String.format(getResources().getString(R.string.content_income), category.getName()));
            }

            viewHolder.tvTranAmount.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), transaction.getAmount()));

            if(!transaction.getDescription().equals("")) {
                viewHolder.tvDescription.setText(transaction.getDescription());
            } else {
                viewHolder.tvDescription.setVisibility(View.GONE);
            }

            if(!category.isExpense()) {
                viewHolder.tvTranCategory.setTextColor(getResources().getColor(R.color.colorPrimary));
                viewHolder.tvTranAmount.setTextColor(getResources().getColor(R.color.colorPrimary));
                viewHolder.tvDescription.setTextColor(getResources().getColor(R.color.colorPrimary));
            }

            viewHolder.tvDate.setText(String.format(getResources().getString(R.string.format_day_month_year),
                    transaction.getTime().get(Calendar.DAY_OF_MONTH),
                    transaction.getTime().get(Calendar.MONTH) + 1,
                    transaction.getTime().get(Calendar.YEAR)));
            viewHolder.tvAccount.setText(mDbHelper.getAccount(transaction.getFromAccountId() != 0 ? transaction.getFromAccountId() : transaction.getToAccountId()).getName());
            viewHolder.ivAccountIcon.setImageResource(AccountType.getAccountTypeById(mDbHelper.getAccount(transaction.getFromAccountId() != 0 ? transaction.getFromAccountId() : transaction.getToAccountId()).getTypeId()).getIcon());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransactionCUD nextFrag = new FragmentTransactionCUD();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Transaction", transaction);
                    nextFrag.setArguments(bundle);
                    FragmentReportLentBorrowedDetail.this.getFragmentManager().beginTransaction()
                            .add(R.id.ll_report, nextFrag, FragmentTransactionCUD.Tag)
                            .addToBackStack(null)
                            .commit();
                }
            });

            return convertView;
        }
    }
}
