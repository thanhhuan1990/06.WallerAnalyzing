package local.wallet.analyzing.report;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.main.Configurations;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.AccountType;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Debt;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/22/2016.
 */
public class FragmentReportFinancialStatement extends Fragment implements View.OnClickListener {
    public static final int         mTab = 4;
    public static final String      Tag = "---[" + mTab + ".3]---ReportExpenseAnalysisTime";

    private ActivityMain            mActivity;

    private DatabaseHelper          mDbHelper;
    private Configurations          mConfigs;

    private TextView                tvAsset;
    private ListView                lvAssets;
    private LinearLayout            llLent;
    private TextView                tvLent;
    private TextView                tvLiabilities;
    private LinearLayout            llBorrowed;
    private TextView                tvBorrowed;
    private TextView                tvNetWorth;

    private Double                  assets = 0.0, lent = 0.0, borrowed = 0.0;
    private AssetAdapter            mAdapter;
    private List<Map.Entry<String, Double>> arAssets = new ArrayList<Map.Entry<String, Double>>();

   @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        View view = inflater.inflate(R.layout.layout_fragment_report_financial_statement, container, false);

        mConfigs        = new Configurations(getContext());
        mDbHelper       = new DatabaseHelper(getActivity());

        initDataSource();

        tvAsset         = (TextView) view.findViewById(R.id.tvAsset);
        tvAsset.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), assets));

        lvAssets        = (ListView) view.findViewById(R.id.lvAssets);
        mAdapter        = new AssetAdapter(getActivity(), arAssets);
        lvAssets.setAdapter(mAdapter);

        llLent          = (LinearLayout) view.findViewById(R.id.llLent);
        llLent.setOnClickListener(this);
        tvLent          = (TextView) view.findViewById(R.id.tvLent);
        tvLent.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), lent));

        tvLiabilities   = (TextView) view.findViewById(R.id.tvLiabilities);
        tvLiabilities.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), borrowed));

        llBorrowed      = (LinearLayout) view.findViewById(R.id.llBorrowed);
        llBorrowed.setOnClickListener(this);
        tvBorrowed      = (TextView) view.findViewById(R.id.tvBorrowed);
        tvBorrowed.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), borrowed));

        tvNetWorth      = (TextView) view.findViewById(R.id.tvNetWorth);
        tvNetWorth.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), (assets - borrowed)));

        LogUtils.logLeaveFunction(Tag, null, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mActivity   = (ActivityMain) getActivity();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llLent: {
                FragmentReportFinancialStatementLentBorrowed nextFrag = new FragmentReportFinancialStatementLentBorrowed();
                Bundle bundle = new Bundle();
                bundle.putInt("Tab", mTab);
                bundle.putBoolean("Lent", true);
                nextFrag.setArguments(bundle);
                mActivity.addFragment(mTab, R.id.ll_report, nextFrag, FragmentReportFinancialStatementLentBorrowed.Tag, true);
                break;
            }
            case R.id.llBorrowed: {
                FragmentReportFinancialStatementLentBorrowed nextFrag = new FragmentReportFinancialStatementLentBorrowed();
                Bundle bundle = new Bundle();
                bundle.putInt("Tab", mTab);
                bundle.putBoolean("Lent", false);
                nextFrag.setArguments(bundle);
                mActivity.addFragment(mTab, R.id.ll_report, nextFrag, FragmentReportFinancialStatementLentBorrowed.Tag, true);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        LogUtils.logEnterFunction(Tag, null);
        super.onResume();

        initDataSource();

        tvAsset.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), assets));
        tvLent.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), lent));
        mAdapter.notifyDataSetChanged();
        tvLiabilities.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), borrowed));
        tvBorrowed.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), borrowed));
        tvNetWorth.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), (assets - borrowed))); // Borrowed < 0 => - Borrowed

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Update Data From Database
     */
    private void initDataSource() {
        LogUtils.logEnterFunction(Tag, null);

        // Re-initialize old data
        arAssets.clear();
        assets      = 0.0;
        lent        = 0.0;
        borrowed    = 0.0;

        // Update Data
        Map<String, Double> hmAsset = new HashMap<>();
        for(AccountType accType : AccountType.Accounts) {
            List<Account> arAccount = mDbHelper.getAllAccountsByTypeId(accType.getId());

            if(arAccount.size() > 0) {
                Double remain = 0.0;
                for(Account acc : arAccount) {
                    remain += mDbHelper.getAccountRemain(acc.getId());
                }

                assets += remain;

                hmAsset.put(getResources().getString(accType.getName()), remain);
            }
        }

        arAssets.addAll(new ArrayList(hmAsset.entrySet()));

        Map<String, Double> hmLent          = new HashMap<>();
        Map<String, Double> hmBorrowed      = new HashMap<>();
        lent = 0.0;
        borrowed = 0.0;

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

        for(Map.Entry<String, Double> entry : hmLent.entrySet()) {
            lent += entry.getValue();
        }
        for(Map.Entry<String, Double> entry : hmBorrowed.entrySet()) {
            borrowed += entry.getValue();
        }

        assets += lent;

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private class AssetAdapter extends ArrayAdapter {

        private class ViewHolder {
            LinearLayout    llMain;
            TextView        tvName;
            TextView        tvAmount;
        }
        public AssetAdapter(Context context, List<Map.Entry<String, Double>> objects) {
            super(context, R.layout.listview_item_assets, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder              = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView             = inflater.inflate(R.layout.listview_item_assets, parent, false);
                viewHolder.llMain       = (LinearLayout) convertView.findViewById(R.id.llMain);
                viewHolder.tvName       = (TextView) convertView.findViewById(R.id.tvName);
                viewHolder.tvAmount     = (TextView) convertView.findViewById(R.id.tvAmount);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Map.Entry<String, Double> entry = (Map.Entry<String, Double>) this.getItem(position);

            viewHolder.tvName.setText(entry.getKey());
            viewHolder.tvAmount.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), entry.getValue()));
            viewHolder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (AccountType accType : AccountType.Accounts) {
                        if (entry.getKey().equals(getResources().getString(accType.getName()))) {
                            FragmentReportFinancialStatementAccounts nextFrag = new FragmentReportFinancialStatementAccounts();
                            Bundle bundle = new Bundle();
                            bundle.putInt("Tab", mTab);
                            bundle.putInt("AccountType", accType.getId());
                            nextFrag.setArguments(bundle);
                            mActivity.addFragment(mTab, R.id.ll_report, nextFrag, FragmentReportFinancialStatementAccounts.Tag, true);
                        }
                    }
                }
            });

            return convertView;
        }
    }
}
