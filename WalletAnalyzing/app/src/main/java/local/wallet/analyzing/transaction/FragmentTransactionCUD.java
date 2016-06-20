package local.wallet.analyzing.transaction;

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
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.main.RootFragment;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.model.Transaction.TransactionEnum;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentTransactionCUD extends Fragment {
    private int                 mTab = 1;
    private String              Tag = "---[" + mTab + "]---TransactionCUD";

    private ActivityMain        mActivity;

    // Current Tab position
    private View                mActionBar;
    private Spinner             spTransactionType;

    private Transaction         mTransaction;

    private int                 mCurrentTransactionType    = -1;

    private int                 mContainerViewId = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle           = this.getArguments();
        if(bundle != null) {
            mTab = bundle.getInt("Tab", mTab);
            Tag = "---[" + mTab + "]---" + "TransactionCUD";
            // Get Transaction from bundle
            mTransaction        = (Transaction)bundle.get("Transaction");
            LogUtils.trace(Tag, "mTransaction = " + mTransaction.toString());
            // Retry ContainerViewId from bundle
            if(bundle.getInt("ContainerViewId") != 0) {
                mContainerViewId = bundle.getInt("ContainerViewId");
            } else {
                mContainerViewId    = mTransaction.getId() != 0 ? R.id.ll_transaction_update : R.id.ll_transaction_create;
            }
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        View view;
        if(mTransaction.getId() == 0) {
            LogUtils.trace(Tag, "View: Create");
            view = inflater.inflate(R.layout.layout_fragment_transaction_create, container, false);
        } else {
            LogUtils.trace(Tag, "View: Update");
            view = inflater.inflate(R.layout.layout_fragment_transaction_update_delete, container, false);
        }
        LogUtils.trace(Tag, "MainView: " + view.toString());
        LogUtils.logLeaveFunction(Tag, null, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mActivity               = (ActivityMain) getActivity();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onResume() {
        LogUtils.logEnterFunction(Tag, null);
        super.onResume();

//        if(mTransaction.getId() == 0 && ((ActivityMain) getActivity()).getCurrentVisibleItem() != ActivityMain.TAB_POSITION_TRANSACTION_CREATE) {
//            LogUtils.error(Tag, "Wrong Tab. Return");
//            LogUtils.logLeaveFunction(Tag, null, null);
//            return;
//        }
        if(mTab  != mActivity.getCurrentVisibleItem()) {
            LogUtils.error(Tag, "Wrong Tab: " + mTab + " vs " + mActivity.getCurrentVisibleItem() + ". Return");
            LogUtils.logLeaveFunction(Tag, null, null);
            return;
        }

        if(mActionBar == null) {
            initActionBar();
        }

        ((ActivityMain)getActivity()).updateActionBar(mActionBar);

        if((getFragmentManager().findFragmentByTag("FragmentTransactionCUDExpense") != null &&
                getFragmentManager().findFragmentByTag("FragmentTransactionCUDExpense").isVisible()) ||
                (getFragmentManager().findFragmentByTag("FragmentTransactionCUDIncome") != null &&
                        getFragmentManager().findFragmentByTag("FragmentTransactionCUDIncome").isVisible()) ||
                (getFragmentManager().findFragmentByTag("FragmentTransactionCUDTransfer") != null &&
                        getFragmentManager().findFragmentByTag("FragmentTransactionCUDTransfer").isVisible()) ||
                (getFragmentManager().findFragmentByTag("FragmentTransactionCUDAdjustment") != null &&
                        getFragmentManager().findFragmentByTag("FragmentTransactionCUDAdjustment").isVisible())) {
        } else {
            switch (TransactionEnum.getTransactionEnum(mTransaction.getTransactionType())) {
                case Expense:
                    spTransactionType.setSelection(0);
                    showExpense();
                    break;
                case Income:
                    spTransactionType.setSelection(1);
                    showIncome();
                    break;
                case Transfer:
                    spTransactionType.setSelection(2);
                    showTransfer();
                    break;
                case Adjustment:
                    spTransactionType.setSelection(3);
                    showAdjustment();
                    break;
                default:
                    break;
            }
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreateOptionsMenu(menu, inflater);

        if(mTab != mActivity.getCurrentVisibleItem()) {
            LogUtils.error(Tag, "Wrong Tab: " + mTab + " vs " + mActivity.getCurrentVisibleItem() + ". Return");
            LogUtils.logLeaveFunction(Tag, null, null);
            return;
        }

        if(mActionBar == null) {
            initActionBar();
        }

        ((ActivityMain)getActivity()).updateActionBar(mActionBar);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Start Fragment Expense
     */
    private void showExpense() {
        LogUtils.logEnterFunction(Tag, null);

        Bundle bundle = new Bundle();
        bundle.putInt("Tab", mTab);
        bundle.putSerializable("Transaction", mTransaction);
        FragmentTransactionCUDExpense nextFrag = new FragmentTransactionCUDExpense();
        nextFrag.setArguments(bundle);
        mActivity.replaceFragment(mTab, mContainerViewId, nextFrag, "FragmentTransactionCUDExpense", false);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Start Fragment Income
     */
    private void showIncome() {
        LogUtils.logEnterFunction(Tag, null);

        Bundle bundle = new Bundle();
        bundle.putInt("Tab", mTab);
        bundle.putSerializable("Transaction", mTransaction);
        FragmentTransactionCUDIncome nextFrag = new FragmentTransactionCUDIncome();
        nextFrag.setArguments(bundle);
        mActivity.replaceFragment(mTab, mContainerViewId, nextFrag, "FragmentTransactionCUDIncome", false);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Start Fragment Transfer
     */
    private void showTransfer() {
        LogUtils.logEnterFunction(Tag, null);

        Bundle bundle = new Bundle();
        bundle.putInt("Tab", mTab);
        bundle.putSerializable("Transaction", mTransaction);
        FragmentTransactionCUDTransfer nextFrag = new FragmentTransactionCUDTransfer();
        nextFrag.setArguments(bundle);
        mActivity.replaceFragment(mTab, mContainerViewId, nextFrag, "FragmentTransactionCUDTransfer", false);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Start Fragment Adjustment
     */
    private void showAdjustment() {
        LogUtils.logEnterFunction(Tag, null);

        Bundle bundle = new Bundle();
        bundle.putInt("Tab", mTab);
        bundle.putSerializable("Transaction", mTransaction);
        FragmentTransactionCUDAdjustment nextFrag = new FragmentTransactionCUDAdjustment();
        nextFrag.setArguments(bundle);
        mActivity.replaceFragment(mTab, mContainerViewId, nextFrag, "FragmentTransactionCUDIncome", false);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Init ActionBar
     */
    private void initActionBar() {
        /* Todo: Update ActionBar: Spinner ReportType */
        String[] arTransactionTypeName      = getResources().getStringArray(R.array.transaction_type);
        String[] arTransactionDescription   = getResources().getStringArray(R.array.transaction_type_description);

        ArrayList<TransactionType> arTransaction = new ArrayList<TransactionType>();
        for(int i = 0 ; i < arTransactionTypeName.length; i++) {
            arTransaction.add(new TransactionType(arTransactionTypeName[i], arTransactionDescription[i]));
        }

        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        mActionBar                  = mInflater.inflate(R.layout.action_bar_with_spinner, null);

        spTransactionType           = (Spinner) mActionBar.findViewById(R.id.spinner);
        spTransactionType.setAdapter(new TransactionTypeAdapter(getActivity().getApplicationContext(), arTransaction));

        spTransactionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.trace(Tag, "onItemSelected: " + position + ", current Item = " + mCurrentTransactionType);
                if (position != mCurrentTransactionType) {
                    switch (position) {
                        case 0:
                            showExpense();
                            break;
                        case 1:
                            showIncome();
                            break;
                        case 2:
                            showTransfer();
                            break;
                        case 3:
                            showAdjustment();
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
}
