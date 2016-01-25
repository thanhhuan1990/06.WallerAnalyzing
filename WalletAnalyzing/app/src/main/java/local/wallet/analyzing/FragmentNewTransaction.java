package local.wallet.analyzing;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentNewTransaction extends Fragment {
    private static final String TAG = "FragmentNewTransaction";

    private DatabaseHelper db;

    private Category    mCategory;
    private Spinner spTransactionType;

    /* Layout Expense */
    private LinearLayout    llExpense;
    private EditText        etExpenseAmount;
    private ImageView       ivExpenseCurrencyIcon;
    private LinearLayout    llExpenseCategory;
    private TextView        tvExpenseCategory;
    private LinearLayout    llExpenseDescription;
    private TextView        tvExpenseDescription;
    private LinearLayout    llExpenseAccount;
    private TextView        tvExpenseAccount;
    private LinearLayout    llExpenseDate;
    private TextView        tvExpenseDate;
    private LinearLayout    llExpensePayee;
    private TextView        tvExpensePayee;
    private LinearLayout    llExpenseEvent;
    private TextView        tvExpenseEvent;

    /* Layout Income */
    private LinearLayout    llIncome;
    private EditText        etIncomeAmount;
    private ImageView       ivIncomeCurrencyIcon;
    private LinearLayout    llIncomeCategory;
    private TextView        tvIncomeCategory;
    private LinearLayout    llIncomeDescription;
    private TextView        tvIncomeDescription;
    private LinearLayout    llToAccount;
    private TextView        tvToAccount;
    private LinearLayout    llIncomeDate;
    private TextView        tvIncomeDate;
    private LinearLayout    llIncomeEvent;
    private TextView        tvIncomeEvent;

    /* Layout Transfer */
    private LinearLayout    llTransfer;
    private EditText        etTransferAmount;
    private ImageView       ivTransferCurrencyIcon;
    private LinearLayout    llTransferFromAccount;
    private TextView        tvTransferFromAccount;
    private LinearLayout    llTransferToAccount;
    private TextView        tvTransferToAccount;
    private LinearLayout    llTransferDescription;
    private TextView        tvTransferDescription;
    private LinearLayout    llTransferDate;
    private TextView        tvTransferDate;
    private EditText        etTransferFee;
    private ImageView       ivTransferFeeCurrencyIcon;
    private LinearLayout    llTransferCategory;
    private TextView        tvTransferCategory;

    /* Adjustment */
    private LinearLayout    llAdjustment;
    private LinearLayout    llAdjustmentAccount;
    private TextView        tvAdjustmentAccount;
    private EditText        etAdjustmentBalance;
    private ImageView       ivAdjustmentCurrencyIcon;
    private TextView        tvAdjustmentSpent;
    private LinearLayout    llAdjustmentCategory;
    private TextView        tvAdjustmentCategory;
    private LinearLayout    llAdjustmentDescription;
    private TextView        tvAdjustmentDescription;
    private LinearLayout    llAdjustmentDate;
    private TextView        tvAdjustmentDate;
    private LinearLayout    llAdjustmentPayee;
    private TextView        tvAdjustmentPayee;
    private LinearLayout    llTransferEvent;
    private TextView        tvTransferEvent;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        super.onCreate(savedInstanceState);
        LogUtils.logLeaveFunction(TAG, null, null);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        String myTag = getTag();
        ((ActivityMain)getActivity()).setFragmentNewTransaction(myTag);

        LogUtils.logLeaveFunction(TAG, null, null);

        return inflater.inflate(R.layout.layout_fragment_new_transaction, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();

        db = new DatabaseHelper(getActivity());

        llExpense           = (LinearLayout) getView().findViewById(R.id.llExpense);
        llIncome            = (LinearLayout) getView().findViewById(R.id.llIncome);
        llTransfer          = (LinearLayout) getView().findViewById(R.id.llTransfer);
        llAdjustment        = (LinearLayout) getView().findViewById(R.id.llAdjustment);

        llExpenseCategory   = (LinearLayout) getView().findViewById(R.id.llExpenseCategory);
        llExpenseCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentNewTransactionSelectCategory nextFrag = new FragmentNewTransactionSelectCategory();
                Bundle bundle = new Bundle();
                bundle.putString("Tag", ((ActivityMain)getActivity()).getFragmentNewTransaction());
                bundle.putInt("TransactionType", spTransactionType.getSelectedItemPosition());
                bundle.putInt("CategoryID", mCategory != null ? mCategory.getId() : 0);
                nextFrag.setArguments(bundle);
                FragmentNewTransaction.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_new_transaction, nextFrag, "FragmentNewTransactionSelectCategory")
                        .addToBackStack(null)
                        .commit();
            }
        });

        tvExpenseCategory   = (TextView) getView().findViewById(R.id.tvExpenseCategory);

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

        /* Todo: Init Data */
        String[] arTransactionTypeName      = getResources().getStringArray(R.array.transaction_type);
        String[] arTransactionDescription   = getResources().getStringArray(R.array.transaction_type_description);

        ArrayList<TransactionType> arTransaction = new ArrayList<TransactionType>();
        for(int i = 0 ; i < arTransactionTypeName.length; i++) {
            arTransaction.add(new TransactionType(arTransactionTypeName[i], arTransactionDescription[i]));
        }

        /* Todo: Update ActionBar: Spinner TransactionType */
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_new_transaction, null);

        spTransactionType   = (Spinner) mCustomView.findViewById(R.id.spinnerTransactionType);
        spTransactionType.setAdapter(new TransactionTypeAdapter(getActivity().getApplicationContext(), arTransaction));

        spTransactionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.trace(TAG, "onItemSelected: " + position);

                llExpense.setVisibility(View.GONE);
                llIncome.setVisibility(View.GONE);
                llTransfer.setVisibility(View.GONE);
                llAdjustment.setVisibility(View.GONE);

                switch (position) {
                    case 0:
                        llExpense.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        llIncome.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        llTransfer.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        llAdjustment.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ((ActivityMain)getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private class TransactionType {
        private String name;
        private String description;

        public TransactionType(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Spinner Transaction Type's adapter
     */
    private class TransactionTypeAdapter extends ArrayAdapter<TransactionType> {
        private class ViewHolder {
            TextView tvType;
        }
        private class DropdownViewHolder {
            TextView tvType;
            TextView tvDescription;
        }

        private List<TransactionType> mList;

        public TransactionTypeAdapter(Context context, List<TransactionType> items) {
            super(context, R.layout.spinner_transaction_type_dropdown_item, items);
            this.mList = items;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public TransactionType getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.spinner_transaction_type, parent, false);
                viewHolder.tvType           = (TextView) convertView.findViewById(R.id.tvType);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tvType.setText(mList.get(position).getName());

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            DropdownViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new DropdownViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.spinner_transaction_type_dropdown_item, parent, false);
                viewHolder.tvType           = (TextView) convertView.findViewById(R.id.tvType);
                viewHolder.tvDescription    = (TextView) convertView.findViewById(R.id.tvDescription);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (DropdownViewHolder) convertView.getTag();
            }

            viewHolder.tvType.setText(mList.get(position).getName());
            viewHolder.tvDescription.setText(mList.get(position).getDescription());

            return convertView;
        }
    }

    /**
     * Update ParentCategory, call from ActivityMain
     * @param categoryId
     */
    public void updateCategory(int categoryId) {
        LogUtils.logEnterFunction(TAG, "categoryId = " + categoryId);

        mCategory = db.getCategory(categoryId);
        if(mCategory != null) {
            tvExpenseCategory.setText(mCategory.getName());
        } else {
            tvExpenseCategory.setText("");
        }

        LogUtils.logLeaveFunction(TAG, "categoryId = " + categoryId, null);
    }
}
