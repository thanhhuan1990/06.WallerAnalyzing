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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.account.FragmentAccountTransactions;
import local.wallet.analyzing.account.FragmentAccountUpdate;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.main.Configurations;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.Account.IAccountCallback;
import local.wallet.analyzing.model.AccountType;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 3/31/2016.
 */
public class FragmentReportFinancialStatementAccounts extends Fragment implements View.OnClickListener, IAccountCallback {
    public static final String Tag = "ReportFinancialStatementAccounts";

    private DatabaseHelper  mDbHelper;
    private Configurations mConfigs;

    private AccountType     mAccount;
    private ListView        lvAccount;
    private AccountAdapter  accAdapter;
    private List<Account>   listAccount     = new ArrayList<Account>();

    private TextView        tvEmpty;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            int accountTypeId = bundle.getInt("AccountType", 0);
            for (AccountType accType : AccountType.Accounts) {
                if(accountTypeId == accType.getId()) {
                    mAccount = accType;
                }
            }
        }
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        mConfigs    = new Configurations(getContext());
        mDbHelper   = new DatabaseHelper(getActivity());

        View view = inflater.inflate(R.layout.layout_fragment_list_account, container, false);
        lvAccount   = (ListView) view.findViewById(R.id.lvAccount);
        tvEmpty     = (TextView) view.findViewById(R.id.tvEmpty);

        listAccount = mDbHelper.getAllAccountsByTypeId(mAccount.getId());
        accAdapter  = new AccountAdapter(getActivity(), listAccount);
        lvAccount.setAdapter(accAdapter);

        lvAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LogUtils.trace(Tag, "Click on Account number " + position + " -> ID = " + listAccount.get(position).getId());
                LogUtils.info(Tag, "ReportFinancialStatementAccounts ----------> AccountTransactions");

                FragmentAccountTransactions nextFrag = new FragmentAccountTransactions();
                Bundle bundle = new Bundle();
                bundle.putInt("AccountID", accAdapter.getItem(position).getId());
                bundle.putInt("ContainerViewId", R.id.ll_report);
                nextFrag.setArguments(bundle);
                FragmentReportFinancialStatementAccounts.this.getFragmentManager().beginTransaction()
                        .add(R.id.ll_report, nextFrag, FragmentAccountTransactions.Tag)
                        .addToBackStack(null)
                        .commit();

            }
        });

        if (listAccount.size() > 0) {
            tvEmpty = (TextView) view.findViewById(R.id.tvEmpty);
            tvEmpty.setVisibility(View.GONE);
        }

        LogUtils.logLeaveFunction(Tag, null, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreateOptionsMenu(menu, inflater);

        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        View actionBar              = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView    tvTitle         = (TextView) actionBar.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(mAccount.getName()));

        ((ActivityMain) getActivity()).updateActionBar(actionBar);

        // Update List Account
        this.onListAccountUpdated();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    public void onListAccountUpdated() {
        LogUtils.logEnterFunction(Tag, null);
        List<Account> arTemp = mDbHelper.getAllAccountsByTypeId(mAccount.getId());
        listAccount.clear();

        for(Account acc : arTemp) {
            listAccount.add(acc);
        }

        accAdapter.notifyDataSetChanged();

        if (listAccount.size() > 0) {
            tvEmpty.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Adapter for Accounts
     */
    private class AccountAdapter extends ArrayAdapter<Account> {
        private class ViewHolder {
            ImageView ivIcon;
            TextView tvAccountName;
            TextView tvRemain;
            ImageView ivCurrency;
            ImageView ivEdit;
        }

        private List<Account> mList;

        public AccountAdapter(Context context, List<Account> items) {
            super(context, R.layout.listview_item_account, items);
            this.mList = items;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Account getItem(int position) {
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
                LayoutInflater inflater     = LayoutInflater.from(getContext());
                convertView                 = inflater.inflate(R.layout.listview_item_account, parent, false);
                viewHolder.ivIcon           = (ImageView) convertView.findViewById(R.id.ivIcon);
                viewHolder.tvAccountName    = (TextView) convertView.findViewById(R.id.tvAccount);
                viewHolder.tvRemain         = (TextView) convertView.findViewById(R.id.tvRemain);
                viewHolder.ivEdit           = (ImageView) convertView.findViewById(R.id.ivEdit);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.ivIcon.setImageResource(AccountType.getAccountTypeById(mList.get(position).getTypeId()).getIcon());
            viewHolder.tvAccountName.setText(mList.get(position).getName());

            Double remain = mDbHelper.getAccountRemain(mList.get(position).getId());
            viewHolder.tvRemain.setText(Currency.formatCurrency(getContext(), mList.get(position).getCurrencyId(), remain));

            viewHolder.ivEdit.setImageResource(R.drawable.icon_list_edit);
            viewHolder.ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.trace(Tag, "Edit item number " + position + " -> AccountID = " + listAccount.get(position));
                    FragmentAccountUpdate nextFrag = new FragmentAccountUpdate();
                    Bundle bundle = new Bundle();
                    bundle.putInt("AccountID", listAccount.get(position).getId());
                    bundle.putSerializable("Callback", FragmentReportFinancialStatementAccounts.this);
                    bundle.putInt("ContainerViewId", R.id.ll_report);
                    nextFrag.setArguments(bundle);
                    FragmentReportFinancialStatementAccounts.this.getFragmentManager().beginTransaction()
                            .add(R.id.ll_report, nextFrag, FragmentAccountUpdate.Tag)
                            .addToBackStack(null)
                            .commit();
                }
            });

            return convertView;
        }
    }

}