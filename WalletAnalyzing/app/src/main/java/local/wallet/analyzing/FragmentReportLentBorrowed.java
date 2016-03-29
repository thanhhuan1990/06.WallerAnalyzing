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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Debt;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 3/28/2016.
 */
public class FragmentReportLentBorrowed extends Fragment {
    public static final String Tag = "ReportLentBorrowed";

    private DatabaseHelper      mDbHelper;
    private Configurations      mConfigs;

    private TextView            tvBorrowing;
    private ListView            lvBorrowing;
    private TextView            tvLending;
    private ListView            lvLending;

    private LentBorrowedAdapter mLendingAdapter;
    private LentBorrowedAdapter mBorrowingAdapter;

    Double lending = 0.0, borrowing = 0.0;
    private Map<String, Double> hmAllDebt   = new HashMap<String, Double>();
    private Map<String, Double> hmLent      = new HashMap<String, Double>();
    private Map<String, Double> hmBorrowed  = new HashMap<String, Double>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_report_lent_borrow, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mConfigs        = new Configurations(getContext());
        mDbHelper       = new DatabaseHelper(getActivity());

        initDataSource();

        tvLending           = (TextView) getView().findViewById(R.id.tvLending);
        tvLending.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), lending));
        lvLending           = (ListView) getView().findViewById(R.id.lvLending);
        mLendingAdapter     = new LentBorrowedAdapter(getActivity(), new ArrayList(hmLent.entrySet()));
        lvLending.setAdapter(mLendingAdapter);

        tvBorrowing          = (TextView) getView().findViewById(R.id.tvBorrowing);
        tvBorrowing.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), borrowing < 0 ? borrowing * -1 : 0));
        lvBorrowing         = (ListView) getView().findViewById(R.id.lvBorrowing);
        mBorrowingAdapter   = new LentBorrowedAdapter(getActivity(), new ArrayList(hmBorrowed.entrySet()));
        lvBorrowing.setAdapter(mBorrowingAdapter);

        LogUtils.logLeaveFunction(Tag, null, null);
    }


    @Override
    public void onResume() {
        LogUtils.logEnterFunction(Tag, null);
        super.onResume();

        initDataSource();

        tvLending.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), lending));
        tvBorrowing.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), borrowing < 0 ? borrowing * -1 : 0));
        mLendingAdapter.notifyDataSetChanged();
        mBorrowingAdapter.notifyDataSetChanged();
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(((ActivityMain) getActivity()).getCurrentVisibleItem() != ActivityMain.TAB_POSITION_REPORTS) {
            return;
        }
        LogUtils.logEnterFunction(Tag, null);

        super.onCreateOptionsMenu(menu, inflater);

        initDataSource();

        tvLending.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), lending));
        tvBorrowing.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), borrowing < 0 ? borrowing * -1 : 0));
        mLendingAdapter.notifyDataSetChanged();
        mBorrowingAdapter.notifyDataSetChanged();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private void initDataSource() {
        hmAllDebt.clear();
        hmLent.clear();
        hmBorrowed.clear();
        lending = 0.0;
        borrowing = 0.0;
        List<Debt>      arAllDebts   = mDbHelper.getAllDebts();
        for(Debt debt : arAllDebts) {
            if(hmAllDebt.get(debt.getPeople()) != null) {
                Category category = mDbHelper.getCategory(debt.getCategoryId());
                if(category.isExpense() && category.getDebtType() == Category.EnumDebt.LESS) { // Repayment
                    hmAllDebt.put(debt.getPeople(), hmAllDebt.get(debt.getPeople()) + debt.getAmount());
                } else if(category.isExpense() && category.getDebtType() == Category.EnumDebt.MORE) { // Lend
                    hmAllDebt.put(debt.getPeople(), hmAllDebt.get(debt.getPeople()) + debt.getAmount());
                } else if(!category.isExpense() && category.getDebtType() == Category.EnumDebt.LESS) { // Debt Collecting
                    hmAllDebt.put(debt.getPeople(), hmAllDebt.get(debt.getPeople()) - debt.getAmount());
                } else if(!category.isExpense() && category.getDebtType() == Category.EnumDebt.MORE) { // Borrow
                    hmAllDebt.put(debt.getPeople(), hmAllDebt.get(debt.getPeople()) - debt.getAmount());
                }
            } else {
                Category category = mDbHelper.getCategory(debt.getCategoryId());
                if(category.isExpense() && category.getDebtType() == Category.EnumDebt.LESS) { // Repayment
                    hmAllDebt.put(debt.getPeople(), debt.getAmount());
                } else if(category.isExpense() && category.getDebtType() == Category.EnumDebt.MORE) { // Lend
                    hmAllDebt.put(debt.getPeople(), debt.getAmount());
                } else if(!category.isExpense() && category.getDebtType() == Category.EnumDebt.LESS) { // Debt Collecting
                    hmAllDebt.put(debt.getPeople(), debt.getAmount() * -1);
                } else if(!category.isExpense() && category.getDebtType() == Category.EnumDebt.MORE) { // Borrow
                    hmAllDebt.put(debt.getPeople(), debt.getAmount() * -1);
                }
            }
        }

        for(Map.Entry<String, Double> entry : hmAllDebt.entrySet()) {
            if(entry.getValue() > 0) {
                hmLent.put(entry.getKey(), entry.getValue());
                lending += entry.getValue();
            } else if(entry.getValue() < 0) {
                hmBorrowed.put(entry.getKey(), entry.getValue());
                borrowing += entry.getValue();
            }
        }

    }

    /**
     * Lent/Borrowed adapter
     */
    private class LentBorrowedAdapter extends ArrayAdapter {
        private class ViewHolder {
            LinearLayout    llMain;
            TextView        tvPeople;
            TextView        tvAmount;
            Button          btnRepay;
        }

        public LentBorrowedAdapter(Context context, List<Map.Entry<String, Double>> objects) {
            super(context, R.layout.listview_item_lent_borrowed, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder              = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView             = inflater.inflate(R.layout.listview_item_lent_borrowed, parent, false);
                viewHolder.llMain       = (LinearLayout) convertView.findViewById(R.id.llMain);
                viewHolder.tvPeople     = (TextView) convertView.findViewById(R.id.tvPeople);
                viewHolder.tvAmount     = (TextView) convertView.findViewById(R.id.tvAmount);
                viewHolder.btnRepay     = (Button) convertView.findViewById(R.id.btnRepay);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Map.Entry<String, Double> entry = (Map.Entry<String, Double>) this.getItem(position);

            viewHolder.tvPeople.setText(entry.getKey());

            if (entry.getValue() < 0) {
                viewHolder.tvAmount.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), entry.getValue() * -1));
                viewHolder.btnRepay.setText(getResources().getString(R.string.report_Lent_borrow_repay));
                viewHolder.btnRepay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Transaction transaction = new Transaction();
                        transaction.setAmount(entry.getValue());
                        transaction.setCategoryId(mDbHelper.getAllCategories(true, Category.EnumDebt.LESS).get(0).getId());
                        transaction.setPayee(entry.getKey());
                        transaction.setTransactionType(Transaction.TransactionEnum.Expense.getValue());

                        FragmentTransactionCUD nextFrag = new FragmentTransactionCUD();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Transaction", transaction);
                        nextFrag.setArguments(bundle);
                        FragmentReportLentBorrowed.this.getFragmentManager().beginTransaction()
                                .add(R.id.ll_report, nextFrag, FragmentTransactionCUD.Tag)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            } else if(entry.getValue() > 0) {
                viewHolder.tvAmount.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), entry.getValue()));
                viewHolder.btnRepay.setText(getResources().getString(R.string.report_Lent_borrow_collect));
                viewHolder.btnRepay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Transaction transaction = new Transaction();
                        transaction.setAmount(entry.getValue());
                        transaction.setCategoryId(mDbHelper.getAllCategories(false, Category.EnumDebt.LESS).get(0).getId());
                        transaction.setPayee(entry.getKey());
                        transaction.setTransactionType(Transaction.TransactionEnum.Income.getValue());

                        FragmentTransactionCUD nextFrag = new FragmentTransactionCUD();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Transaction", transaction);
                        nextFrag.setArguments(bundle);
                        FragmentReportLentBorrowed.this.getFragmentManager().beginTransaction()
                                .add(R.id.ll_report, nextFrag, FragmentTransactionCUD.Tag)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }

            return convertView;
        }
    }
}
