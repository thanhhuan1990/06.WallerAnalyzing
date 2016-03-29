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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Debt;
import local.wallet.analyzing.model.Event;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 3/28/2016.
 */
public class FragmentReportLentBorrowed extends Fragment implements View.OnClickListener {
    public static final String Tag = "ReportLentBorrowed";

    private DatabaseHelper  mDbHelper;
    private Configurations  mConfigs;

    private TextView        tvBorrowing;
    private ListView        lvBorrowing;
    private TextView        tvLending;
    private ListView        lvLending;

    private LentBorrowedAdapter mLendingAdapter;

    private Map<String, Double> arPeople = new HashMap<String, Double>();
    private List<Debt>      arDebtsLending      = new ArrayList<Debt>();
    private List<Debt>      arDebtsBorrowing    = new ArrayList<Debt>();

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

        lvLending       = (ListView) getView().findViewById(R.id.lvLending);
        List<Debt>      arAllDebts   = mDbHelper.getAllDebts();
        for(Debt debt : arAllDebts) {
            if(arPeople.get(debt.getPeople()) != null) {
                Category category = mDbHelper.getCategory(debt.getCategoryId());
                if(category.isExpense() && category.getDebtType() == Category.EnumDebt.LESS) { // Repayment
                    arPeople.put(debt.getPeople(), arPeople.get(debt.getPeople()) + debt.getAmount());
                } else if(category.isExpense() && category.getDebtType() == Category.EnumDebt.MORE) { // Lend
                    arPeople.put(debt.getPeople(), arPeople.get(debt.getPeople()) + debt.getAmount());
                } else if(!category.isExpense() && category.getDebtType() == Category.EnumDebt.LESS) { // Debt Collecting
                    arPeople.put(debt.getPeople(), arPeople.get(debt.getPeople()) - debt.getAmount());
                } else if(!category.isExpense() && category.getDebtType() == Category.EnumDebt.MORE) { // Borrow
                    arPeople.put(debt.getPeople(), arPeople.get(debt.getPeople()) - debt.getAmount());
                }
            } else {
                Category category = mDbHelper.getCategory(debt.getCategoryId());
                if(category.isExpense() && category.getDebtType() == Category.EnumDebt.LESS) { // Repayment
                    arPeople.put(debt.getPeople(), debt.getAmount());
                } else if(category.isExpense() && category.getDebtType() == Category.EnumDebt.MORE) { // Lend
                    arPeople.put(debt.getPeople(), debt.getAmount());
                } else if(!category.isExpense() && category.getDebtType() == Category.EnumDebt.LESS) { // Debt Collecting
                    arPeople.put(debt.getPeople(), debt.getAmount() * -1);
                } else if(!category.isExpense() && category.getDebtType() == Category.EnumDebt.MORE) { // Borrow
                    arPeople.put(debt.getPeople(), debt.getAmount() * -1);
                }
            }
        }

        mLendingAdapter = new LentBorrowedAdapter(getActivity(), new ArrayList(arPeople.entrySet()));
        lvLending.setAdapter(mLendingAdapter);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(((ActivityMain) getActivity()).getCurrentVisibleItem() != ActivityMain.TAB_POSITION_REPORTS) {
            return;
        }
        LogUtils.logEnterFunction(Tag, null);

        super.onCreateOptionsMenu(menu, inflater);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInProgress: {
                break;
            }
            case R.id.btnCompleted: {
                break;
            }
            default:
                break;
        }
    }

    private boolean isAdded(String people) {
        for(Debt debt : arDebtsLending) {
           if(debt.getPeople().equals(people)) {
               return true;
           }
        }

        return false;
    }

    private class DebtView {
        String name;
        Double amount;
    }
    /**
     * Event adapter
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

            Map.Entry<String, Double> entry = (Map.Entry<String, Double>) this.getItem(position);

            viewHolder.tvPeople.setText(entry.getKey());
            viewHolder.tvAmount.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), entry.getValue()));
            viewHolder.btnRepay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return convertView;
        }
    }
}
