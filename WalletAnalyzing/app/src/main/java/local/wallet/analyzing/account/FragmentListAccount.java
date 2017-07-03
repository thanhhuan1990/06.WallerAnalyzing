package local.wallet.analyzing.account;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.Account.IAccountCallback;
import local.wallet.analyzing.model.AccountType;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentListAccount extends ListFragment implements View.OnClickListener, IAccountCallback {

    public static final int         mTab = 2;
    public static final String      Tag = "---[" + mTab + "]---ListAccount";

    private         ActivityMain            mActivity;
    private static  FragmentListAccount     instance;
    public static   FragmentListAccount getInstance() {
        if(instance == null) {
            instance    = new FragmentListAccount();
        }
        return instance;
    }

    private static final int NORMAL_MODE    = 1;
    private static final int EDIT_MODE      = 2;

    private int             mCurrentMode    = NORMAL_MODE;

    private DatabaseHelper  mDbHelper;
    private AccountAdapter  accAdapter;
    private List<Account>   listAccount     = new ArrayList<Account>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mActivity   = (ActivityMain) getActivity();
        mDbHelper   = new DatabaseHelper(getActivity());

        listAccount = mDbHelper.getAllAccounts();
        accAdapter  = new AccountAdapter(getActivity(), listAccount);
        setListAdapter(accAdapter);

        LogUtils.logLeaveFunction(Tag);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        LogUtils.logLeaveFunction(Tag);
        return inflater.inflate(R.layout.layout_fragment_list_account, container, false);
    }

    @Override
    public void onResume() {
        LogUtils.logEnterFunction(Tag);
        super.onResume();

        if(mTab != mActivity.getCurrentVisibleItem()) {
            LogUtils.error(Tag, "Wrong Tab. Return");
            LogUtils.logLeaveFunction(Tag);
            return;
        }

        // Update actionbar
        updateActionBar();
        // Update list Accounts
        updateListAccounts();

        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAdd: {
                LogUtils.trace(Tag, "Click Menu Action Add Account.");
                FragmentAccountCreate nextFrag = new FragmentAccountCreate();
                Bundle bundle = new Bundle();
                bundle.putInt("Tab", mTab);
                bundle.putSerializable("Callback", FragmentListAccount.this);
                nextFrag.setArguments(bundle);
                mActivity.addFragment(mTab, R.id.layout_account, nextFrag, FragmentAccountCreate.Tag, true);
                break;
            }
            case R.id.ivEdit: {
                LogUtils.trace(Tag, "Click Menu Action Edit.");
                accAdapter.notifyDataSetChanged();
                mCurrentMode = EDIT_MODE;
                getActivity().invalidateOptionsMenu();
                break;
            }
            case R.id.ivDone: {
                LogUtils.trace(Tag, "Click Menu Action Done.");
                accAdapter.notifyDataSetChanged();
                mCurrentMode = NORMAL_MODE;
                getActivity().invalidateOptionsMenu();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        LogUtils.trace(Tag, "Click on Account number " + position + " -> ID = " + listAccount.get(position).getId());
        LogUtils.warn(Tag, "ListAccount ----------> AccountTransactions");
        // Go to list of transaction related with this Account
        FragmentAccountTransactions nextFrag = new FragmentAccountTransactions();
        Bundle bundle = new Bundle();
        bundle.putInt("Tab", mTab);
        bundle.putInt("AccountID", accAdapter.getItem(position).getId());
        bundle.putInt("ContainerViewId", R.id.layout_account);
        nextFrag.setArguments(bundle);
        mActivity.addFragment(mTab, R.id.layout_account, nextFrag, FragmentAccountTransactions.Tag, true);
    }

    @Override
    public void onListAccountUpdated() {
        LogUtils.logEnterFunction(Tag);
        List<Account> arTemp = mDbHelper.getAllAccounts();
        listAccount.clear();

        for(Account acc : arTemp) {
            listAccount.add(acc);
        }

        accAdapter.notifyDataSetChanged();

        LogUtils.logLeaveFunction(Tag);
    }

    private void updateActionBar() {
        /* Init ActionBar */
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_account, null);

        ImageView ivAdd     = (ImageView) mCustomView.findViewById(R.id.ivAdd);
        ImageView ivEdit    = (ImageView) mCustomView.findViewById(R.id.ivEdit);
        ImageView ivDone    = (ImageView) mCustomView.findViewById(R.id.ivDone);

        if (mCurrentMode == NORMAL_MODE) {
            ivEdit.setVisibility(listAccount.size() > 0 ? View.VISIBLE : View.INVISIBLE);
            ivAdd.setVisibility(View.VISIBLE);
            ivDone.setVisibility(View.GONE);
        } else if (mCurrentMode == EDIT_MODE) {
            ivAdd.setVisibility(View.GONE);
            ivEdit.setVisibility(View.GONE);
            ivDone.setVisibility(View.VISIBLE);
        }

        ivAdd.setOnClickListener(this);
        ivEdit.setOnClickListener(this);
        ivDone.setOnClickListener(this);

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);
    }

    private void updateListAccounts() {
        listAccount.clear();
        List<Account> arTemp = mDbHelper.getAllAccounts();
        for(int i = 0 ; i < arTemp.size(); i++) {
            listAccount.add(arTemp.get(i));
        }
        accAdapter.notifyDataSetChanged();
    }

    /**
     * Adapter for Accounts
     */
    private class AccountAdapter extends ArrayAdapter<Account> {
        private class ViewHolder {
            ImageView ivIcon;
            TextView tvAccountName;
            TextView tvRemain;
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

            if (mCurrentMode == NORMAL_MODE) {
                viewHolder.ivEdit.setImageResource(R.drawable.icon_list_edit);
                viewHolder.ivEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogUtils.trace(Tag, "Edit item number " + position + " -> AccountID = " + listAccount.get(position));
                        FragmentAccountUpdate nextFrag = new FragmentAccountUpdate();
                        Bundle bundle = new Bundle();
                        bundle.putInt("Tab", mTab);
                        bundle.putInt("AccountID", listAccount.get(position).getId());
                        bundle.putSerializable("Callback", FragmentListAccount.this);
                        bundle.putInt("ContainerViewId", R.id.layout_account);
                        nextFrag.setArguments(bundle);
                        mActivity.addFragment(mTab, R.id.layout_account, nextFrag, FragmentAccountUpdate.Tag, true);
                    }
                });
            } else if (mCurrentMode == EDIT_MODE) {
                viewHolder.ivEdit.setImageResource(R.drawable.icon_list_delete);
                viewHolder.ivEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogUtils.trace(Tag, "Delete item number " + position + " -> AccountID = " + listAccount.get(position));
                        mDbHelper.deleteAccount(listAccount.get(position).getId());
                        listAccount.remove(position);
                        accAdapter.notifyDataSetChanged();

                        if (listAccount.size() == 0) {
                            mCurrentMode = NORMAL_MODE;
                            getActivity().invalidateOptionsMenu();
                        }
                    }
                });
            }

            return convertView;
        }
    }
}
