package local.wallet.analyzing;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.model.Transaction.TransactionEnum;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentTransactionCreateHost extends Fragment {

    public static final String Tag = "TransactionCreateHost";
    private String              mTag    = "";

    private DatabaseHelper      mDbHelper;
    private Configurations      mConfigs;

    private Spinner             spTransactionType;

    private Transaction         mTransaction;
    private int                 mContainerViewId    = 0;

    private int                 mCurrentTransactionType    = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle           = this.getArguments();
        if(bundle != null) {
            mTransaction            = (Transaction)bundle.get("Transaction");
            mContainerViewId        = bundle.getInt("ContainerViewId");

            LogUtils.trace(Tag, "mTransaction = " + mTransaction.toString() + ", ContainerViewId = " + mContainerViewId);
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        if(mTransaction.getId() == 0) {
            return inflater.inflate(R.layout.layout_fragment_transaction_create_host, container, false);
        } else {
            return inflater.inflate(R.layout.layout_fragment_transaction_ud_host, container, false);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mDbHelper       = new DatabaseHelper(getActivity());
        mConfigs        = new Configurations(getActivity());

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreateOptionsMenu(menu, inflater);

        /* Todo: Update ActionBar: Spinner ReportType */
        String[] arTransactionTypeName      = getResources().getStringArray(R.array.transaction_type);
        String[] arTransactionDescription   = getResources().getStringArray(R.array.transaction_type_description);

        ArrayList<TransactionType> arTransaction = new ArrayList<TransactionType>();
        for(int i = 0 ; i < arTransactionTypeName.length; i++) {
            arTransaction.add(new TransactionType(arTransactionTypeName[i], arTransactionDescription[i]));
        }

        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        View mCustomView            = mInflater.inflate(R.layout.action_bar_with_spinner, null);

        spTransactionType                = (Spinner) mCustomView.findViewById(R.id.spinner);
        spTransactionType.setAdapter(new TransactionTypeAdapter(getActivity().getApplicationContext(), arTransaction));

        spTransactionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.trace(Tag, "onItemSelected: " + position);
                if (position != mCurrentTransactionType) {
                    switch (position) {
                        case 0:
                            showExpense();
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        default:
                            break;
                    }
                }
                mCurrentTransactionType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        switch (TransactionEnum.getTransactionEnum(mTransaction.getTransactionType())) {
            case Expense:
                spTransactionType.setSelection(0);
                break;
            case Income:
                spTransactionType.setSelection(1);
                break;
            case Transfer:
                spTransactionType.setSelection(2);
                break;
            case Adjustment:
                spTransactionType.setSelection(3);
                break;
            default:
                break;
        }

        ((ActivityMain)getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Start Fragment Expense
     */
    private void showExpense() {
        LogUtils.logEnterFunction(Tag, null);

        Bundle bundle = new Bundle();
        bundle.putSerializable("Transaction", mTransaction);
        bundle.putInt("ContainerId", mContainerViewId);
        if(mTransaction.getId() != 0) {
            switch (mDbHelper.getCategory(mTransaction.getCategoryId()).getDebtType()) {
                case MORE:
                    // Replace Fragment
                    FragmentTransactionCreateExpenseLend nextFragExpenseLend = new FragmentTransactionCreateExpenseLend();
                    nextFragExpenseLend.setArguments(bundle);
                    FragmentTransactionCreateHost.this.getFragmentManager().beginTransaction()
                            .replace(R.id.ll_transaction_update, nextFragExpenseLend, FragmentTransactionCreateExpenseLend.Tag)
                            .commit();
                    break;
                case NONE:
                    // Replace Fragment
                    FragmentTransactionCreateExpense nextFragExpense = new FragmentTransactionCreateExpense();
                    nextFragExpense.setArguments(bundle);
                    FragmentTransactionCreateHost.this.getFragmentManager().beginTransaction()
                            .replace(R.id.ll_transaction_update, nextFragExpense, FragmentTransactionCreateExpense.Tag)
                            .commit();
                    break;
                case LESS:
                    // Replace Fragment
                    FragmentTransactionCreateExpenseRepayment nextFragRepayment = new FragmentTransactionCreateExpenseRepayment();
                    nextFragRepayment.setArguments(bundle);
                    FragmentTransactionCreateHost.this.getFragmentManager().beginTransaction()
                            .replace(R.id.ll_transaction_update, nextFragRepayment, FragmentTransactionCreateExpenseRepayment.Tag)
                            .commit();
                    break;
                default:
                    break;
            }
        } else {
            // Replace Fragment
            FragmentTransactionCreateExpense nextFrag = new FragmentTransactionCreateExpense();
            nextFrag.setArguments(bundle);
            FragmentTransactionCreateHost.this.getFragmentManager().beginTransaction()
                    .replace(R.id.ll_transaction_create, nextFrag, FragmentTransactionCreateExpense.Tag)
                    .commit();
        }


        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Private class for Spinner TransactionType
     */
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

    public interface OnSetupData extends Serializable {
        public void updateCategory(TransactionEnum type, int categoryId);
        public void updateDescription(TransactionEnum type, String description);
        public void updateAccount(TransactionEnum type, int accountId);
        public void updatePayee(TransactionEnum type, String payee);
        public void updateEvent(TransactionEnum type, String event);
    }
}
