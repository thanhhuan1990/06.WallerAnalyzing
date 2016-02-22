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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Budget;
import local.wallet.analyzing.model.TransactionGroup;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentListBudget extends Fragment {

    private static final String TAG = "ListBudget";

    private DatabaseHelper db;
    private ListView lvBudget;
    private BudgetAdapter   adapter;
    private List<Budget> arBudgets = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, null);
        return inflater.inflate(R.layout.layout_fragment_list_budget, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        super.onActivityCreated(savedInstanceState);

        db = new DatabaseHelper(getActivity());

        lvBudget = (ListView) getView().findViewById(R.id.lvBudget);
        arBudgets   = db.getAllBudgets();
        adapter = new BudgetAdapter(getContext(), arBudgets);
        lvBudget.setAdapter(adapter);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);
        super.onCreateOptionsMenu(menu, inflater);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_add, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_budget));

        ImageView ivAdd = (ImageView) mCustomView.findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.trace(TAG, "Click Menu Action Add Budget.");
                FragmentBudgetCreate nextFrag = new FragmentBudgetCreate();
                FragmentListBudget.this.getFragmentManager().beginTransaction()
                                                            .add(R.id.layout_budget, nextFrag, "FragmentBudgetCreate")
                                                            .addToBackStack(null)
                                                            .commit();
            }
        });

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        updateListBudget();

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    public void updateListBudget() {
        LogUtils.logEnterFunction(TAG, null);

        arBudgets.clear();

        List<Budget> temp = db.getAllBudgets();

        for(int i = 0 ; i < temp.size(); i++) {
            arBudgets.add(temp.get(i));
        }

        if(arBudgets.size() == 0) {
            getView().findViewById(R.id.tvEmpty).setVisibility(View.VISIBLE);
            return;
        }

        getView().findViewById(R.id.tvEmpty).setVisibility(View.GONE);

        adapter.notifyDataSetChanged();

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    private class BudgetAdapter extends ArrayAdapter<Budget> {

        private class ViewHolder {
            private TextView    tvCategory;
            private TextView    tvDate;
            private TextView    tvBudgetAmount;
            private TextView    tvExpensed;
            private TextView    tvBalance;
            private SeekBar     sbExpensed;
        }

        List<Budget> mBudgets;
        public BudgetAdapter(Context context, List<Budget> items) {
            super(context, R.layout.listview_item_budget, items);
            this.mBudgets  = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder                  = new ViewHolder();
                LayoutInflater inflater     = LayoutInflater.from(getContext());
                convertView                 = inflater.inflate(R.layout.listview_item_budget, parent, false);

                viewHolder.tvCategory       = (TextView) convertView.findViewById(R.id.tvCategory);
                viewHolder.tvDate           = (TextView) convertView.findViewById(R.id.tvDate);
                viewHolder.tvBudgetAmount   = (TextView) convertView.findViewById(R.id.tvBudgetAmount);
                viewHolder.tvExpensed       = (TextView) convertView.findViewById(R.id.tvExpensed);
                viewHolder.tvBalance        = (TextView) convertView.findViewById(R.id.tvBalance);
                viewHolder.sbExpensed       = (SeekBar) convertView.findViewById(R.id.sbExpensed);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Budget budget = mBudgets.get(position);
            if(budget != null) {
                viewHolder.tvCategory.setText(budget.getName());
            }

            String date = "";
            Calendar fromDate   = budget.getFromDate();
            Calendar endDate    = budget.getFromDate();
            Calendar today      = Calendar.getInstance();

            int repeatType      = budget.getRepeatType();

            switch (repeatType) {
                case 0: // No repeat
                    date = String.format(getResources().getString(R.string.format_budget_day_month_year),
                                            fromDate.get(Calendar.DAY_OF_MONTH),
                                            fromDate.get(Calendar.MONTH) + 1,
                                            fromDate.get(Calendar.YEAR));

                    viewHolder.tvDate.setText(date);
                    break;
                case 1: // daily
                    Calendar tomorow = Calendar.getInstance();
                    tomorow.add(Calendar.DAY_OF_MONTH, 1);
                    date = String.format(getResources().getString(R.string.format_budget_date),
                            today.get(Calendar.DAY_OF_MONTH),
                            today.get(Calendar.MONTH) + 1,
                            tomorow.get(Calendar.DAY_OF_MONTH),
                            tomorow.get(Calendar.MONTH) + 1);
                    viewHolder.tvDate.setText(date);

                    break;
                case 2: // weekly
                    break;
                case 3: // monthly
                    break;
                case 4: //quarterly
                    break;
                case 5: // yearly
                    break;
                default:
                    break;
            }

            return convertView;
        }
    }
}
