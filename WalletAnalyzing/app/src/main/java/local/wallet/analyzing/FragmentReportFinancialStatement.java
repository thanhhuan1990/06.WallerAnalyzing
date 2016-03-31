package local.wallet.analyzing;

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

import local.wallet.analyzing.Utils.LogUtils;
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
    public static final String Tag = "ReportFinancialStatement";

    private DatabaseHelper  mDbHelper;
    private Configurations  mConfigs;

    private TextView        tvAsset;
    private ListView        lvAssets;
    private TextView        tvLiabilities;
    private LinearLayout    llBorrowed;
    private TextView        tvBorrowed;
    private TextView        tvNetWorth;

    private Double          assets = 0.0, borrowed = 0.0;
    private AssetAdapter    mAdapter;
    private List<Map.Entry<String, Double>> arAssets = new ArrayList<Map.Entry<String, Double>>();

   @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_report_financial_statement, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mConfigs        = new Configurations(getContext());
        mDbHelper       = new DatabaseHelper(getActivity());

        initDataSource();

        tvAsset         = (TextView) getView().findViewById(R.id.tvAsset);
        tvAsset.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), assets));

        lvAssets        = (ListView) getView().findViewById(R.id.lvAssets);
        mAdapter        = new AssetAdapter(getActivity(), arAssets);
        lvAssets.setAdapter(mAdapter);

        tvLiabilities   = (TextView) getView().findViewById(R.id.tvLiabilities);
        tvLiabilities.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), borrowed < 0 ? borrowed * -1 : borrowed));
        tvBorrowed      = (TextView) getView().findViewById(R.id.tvBorrowed);
        tvBorrowed.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), borrowed < 0 ? borrowed * -1 : borrowed));

        tvNetWorth      = (TextView) getView().findViewById(R.id.tvNetWorth);
        tvNetWorth.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), (assets + borrowed))); // Borrowed < 0 => - Borrowed

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    private void initDataSource() {
        LogUtils.logEnterFunction(Tag, null);

        assets = 0.0;

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

        Map<String, Double> hmAllDebt       = new HashMap<String, Double>();
        Double lending = 0.0;
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
                lending += entry.getValue();
            } else if(entry.getValue() < 0) {
                borrowed += entry.getValue();
            }
        }

        if(lending > 0) {
            hmAsset.put(getResources().getString(R.string.report_financial_statement_money_lent), lending);
        }

        arAssets.clear();
        arAssets.addAll(new ArrayList(hmAsset.entrySet()));


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

            return convertView;
        }
    }
}
