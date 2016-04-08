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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Debt;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 04/04/2016.
 */
public class FragmentReportFinancialStatementLentBorrowed extends Fragment {
    public static final String Tag = "ReportFinancialStatementLentBorrowed";

    private DatabaseHelper      mDbHelper;
    private Configurations      mConfigs;

    private boolean             isLent  = true;
    private TextView            tvBorrowing;
    private ListView            lvBorrowing;
    private TextView            tvLending;
    private ListView            lvLending;

    private LentBorrowedAdapter mLendingAdapter;
    private LentBorrowedAdapter mBorrowingAdapter;

    private Double lending = 0.0, borrowing = 0.0;
    private List<Map.Entry<String, Double>> arLent = new ArrayList<Map.Entry<String, Double>>();
    private List<Map.Entry<String, Double>> arBorrowed = new ArrayList<Map.Entry<String, Double>>();

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreateOptionsMenu(menu, inflater);

        if(((ActivityMain) getActivity()).getCurrentVisibleItem() != ActivityMain.TAB_POSITION_REPORTS) {
            LogUtils.warn(Tag, "CurrentVisibleItem is NOT TAB_POSITION_REPORTS");
            LogUtils.logLeaveFunction(Tag, null, null);
            return;
        }

        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        View mActionBar             = mInflater.inflate(R.layout.action_bar_only_title, null);
        if(isLent) {
            ((TextView) mActionBar.findViewById(R.id.tvTitle)).setText(getResources().getString(R.string.title_report_financial_statement_lent));
        } else {
            ((TextView) mActionBar.findViewById(R.id.tvTitle)).setText(getResources().getString(R.string.title_report_financial_statement_Borrow));
        }

        ((ActivityMain) getActivity()).updateActionBar(mActionBar);

        initDataSource();

        if(isLent) {
            tvLending.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), lending));
            mLendingAdapter.notifyDataSetChanged();
        } else {
            tvBorrowing.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), borrowing));
            mBorrowingAdapter.notifyDataSetChanged();
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle   = this.getArguments();
        isLent          = bundle.getBoolean("Lent");

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        View view = inflater.inflate(R.layout.layout_fragment_report_financial_statement_lent_borrow, container, false);

        mConfigs        = new Configurations(getContext());
        mDbHelper       = new DatabaseHelper(getActivity());

        if(isLent) {
            tvLending           = (TextView) view.findViewById(R.id.tvLending);
            tvLending.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), lending));
            lvLending           = (ListView) view.findViewById(R.id.lvLending);
            mLendingAdapter     = new LentBorrowedAdapter(getActivity(), arLent);
            lvLending.setAdapter(mLendingAdapter);

            view.findViewById(R.id.llBorrowed).setVisibility(View.GONE);

        } else {
            tvBorrowing         = (TextView) view.findViewById(R.id.tvBorrowing);
            tvBorrowing.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), borrowing));
            lvBorrowing         = (ListView) view.findViewById(R.id.lvBorrowing);
            mBorrowingAdapter   = new LentBorrowedAdapter(getActivity(), arBorrowed);
            lvBorrowing.setAdapter(mBorrowingAdapter);

            view.findViewById(R.id.llLent).setVisibility(View.GONE);
        }

        LogUtils.logLeaveFunction(Tag, null, null);
        return view;
    }

    @Override
    public void onResume() {
        LogUtils.logEnterFunction(Tag, null);
        super.onResume();

        initDataSource();

        if(isLent) {
            tvLending.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), lending));
            mLendingAdapter.notifyDataSetChanged();
        } else {
            tvBorrowing.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), borrowing));
            mBorrowingAdapter.notifyDataSetChanged();
        }
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Update data source from Database
     */
    private void initDataSource() {
        LogUtils.logEnterFunction(Tag, null);

        // Reset data
        Map<String, Double> hmLent          = new HashMap<>();
        Map<String, Double> hmBorrowed      = new HashMap<>();
        lending = 0.0;
        borrowing = 0.0;

        List<Debt>      arAllDebts   = mDbHelper.getAllDebts();
        for(Debt debt : arAllDebts) {
            Category category = mDbHelper.getCategory(debt.getCategoryId());
            if(category.isExpense() && category.getDebtType() == Category.EnumDebt.MORE) { // Lend
                if(hmLent.get(debt.getPeople()) != null) {
                    hmLent.put(debt.getPeople(), hmLent.get(debt.getPeople()) + debt.getAmount());
                } else {
                    hmLent.put(debt.getPeople(), debt.getAmount());
                }
            } else if(!category.isExpense() && category.getDebtType() == Category.EnumDebt.LESS) { // Debt Collecting
                if(hmLent.get(debt.getPeople()) != null) {
                    hmLent.put(debt.getPeople(), hmLent.get(debt.getPeople()) - debt.getAmount());
                } else {
                    hmLent.put(debt.getPeople(), debt.getAmount());
                }
            } else if(!category.isExpense() && category.getDebtType() == Category.EnumDebt.MORE) { // Borrow
                if(hmBorrowed.get(debt.getPeople()) != null) {
                    hmBorrowed.put(debt.getPeople(), hmBorrowed.get(debt.getPeople()) + debt.getAmount());
                } else {
                    hmBorrowed.put(debt.getPeople(), debt.getAmount());
                }
            } else if(category.isExpense() && category.getDebtType() == Category.EnumDebt.LESS) { // Repayment
                if(hmBorrowed.get(debt.getPeople()) != null) {
                    hmBorrowed.put(debt.getPeople(), hmBorrowed.get(debt.getPeople()) - debt.getAmount());
                } else {
                    hmBorrowed.put(debt.getPeople(), debt.getAmount());
                }
            }
        }

        Iterator<Map.Entry<String,Double>> iter = hmLent.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String,Double> entry = iter.next();
            if(entry.getValue() == 0) {
                iter.remove();
            }
        }

        iter = hmBorrowed.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String,Double> entry = iter.next();
            if(entry.getValue() == 0) {
                iter.remove();
            }
        }

        for(Map.Entry<String, Double> entry : hmLent.entrySet()) {
            lending += entry.getValue();
        }
        for(Map.Entry<String, Double> entry : hmBorrowed.entrySet()) {
            borrowing += entry.getValue();
        }

        arLent.clear();
        arLent.addAll(hmLent.entrySet());
        arBorrowed.clear();
        arBorrowed.addAll(hmBorrowed.entrySet());
        LogUtils.logLeaveFunction(Tag, null, null);
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

            viewHolder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.info(Tag, Tag + " ---> ReportLentBorrowedDetail" );
                    FragmentReportLentBorrowedDetail nextFrag = new FragmentReportLentBorrowedDetail();
                    Bundle bundle = new Bundle();
                    bundle.putString("People", entry.getKey());
                    bundle.putBoolean("Lent", isLent);
                    nextFrag.setArguments(bundle);
                    FragmentReportFinancialStatementLentBorrowed.this.getFragmentManager().beginTransaction()
                            .add(R.id.ll_report, nextFrag, FragmentReportLentBorrowedDetail.Tag)
                            .addToBackStack(null)
                            .commit();
                }
            });

            if (entry.getValue() < 0) {
                viewHolder.tvAmount.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), entry.getValue() * -1));
                viewHolder.btnRepay.setText(getResources().getString(R.string.report_Lent_borrow_repay));
                viewHolder.btnRepay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Transaction transaction = new Transaction();
                        transaction.setAmount(entry.getValue() * -1);
                        transaction.setCategoryId(mDbHelper.getAllCategories(true, Category.EnumDebt.LESS).get(0).getId());
                        transaction.setPayee(entry.getKey());
                        transaction.setTransactionType(Transaction.TransactionEnum.Expense.getValue());

                        FragmentTransactionCUD nextFrag = new FragmentTransactionCUD();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Transaction", transaction);
                        nextFrag.setArguments(bundle);
                        FragmentReportFinancialStatementLentBorrowed.this.getFragmentManager().beginTransaction()
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
                        FragmentReportFinancialStatementLentBorrowed.this.getFragmentManager().beginTransaction()
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
