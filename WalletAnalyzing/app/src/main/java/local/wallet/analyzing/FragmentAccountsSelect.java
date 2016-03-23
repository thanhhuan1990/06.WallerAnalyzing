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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.AccountType;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;
import local.wallet.analyzing.model.Transaction.TransactionEnum;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentAccountsSelect extends Fragment {

    public static final String Tag = "FragmentAccountsSelect";

    private String          mTagOfSource = "";
    private int             mUsingAccountId;
    private TransactionEnum mTransactionType;

    private DatabaseHelper  mDbHelper;
    private Configurations  mConfigs;

    private List<Account>   arAccounts = new ArrayList<Account>();
    private AccountAdapter  accountAdapter;

    private ListView        lvAccount;
    private TextView        tvEmpty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        /* Get data from Bundle */
        Bundle bundle                   = this.getArguments();
        mTagOfSource                    = bundle.getString("Tag");
        mUsingAccountId                 = bundle.getInt("AccountID", 0);
        mTransactionType               = (TransactionEnum) bundle.get("TransactionType");

        LogUtils.trace(Tag, "mTagOfSource = " + mTagOfSource);
        LogUtils.trace(Tag, "mUsingAccountId = " + mUsingAccountId);
        LogUtils.trace(Tag, "mTransactionType = " + mTransactionType.name());

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_account_select, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onActivityCreated(savedInstanceState);

        /* Initialize Database, insert default category */
        mDbHelper       = new DatabaseHelper(getActivity());
        mConfigs        = new Configurations(getActivity());

        tvEmpty         = (TextView) getView().findViewById(R.id.tvEmpty);
        lvAccount       = (ListView) getView().findViewById(R.id.lvAccount);

        arAccounts      = mDbHelper.getAllAccounts();
        accountAdapter  = new AccountAdapter(getActivity(), arAccounts);
        lvAccount.setAdapter(accountAdapter);

        /* Click on listview item to select category*/
        lvAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(mTagOfSource);

                if(mTagOfSource.equals(FragmentTransactionCreateExpense.Tag)) {

                    ((FragmentTransactionCreateExpense) fragment).updateAccount(mTransactionType, arAccounts.get(position).getId());

                } else if(mTagOfSource.equals(FragmentTransactionCreateExpenseLend.Tag)) {

                    ((FragmentTransactionCreateExpenseLend) fragment).updateAccount(mTransactionType, arAccounts.get(position).getId());

                } else if(mTagOfSource.equals(FragmentTransactionCreateExpenseRepayment.Tag)) {

                    ((FragmentTransactionCreateExpenseRepayment) fragment).updateAccount(mTransactionType, arAccounts.get(position).getId());

                } else if(mTagOfSource.equals(((ActivityMain) getActivity()).getFragmentTransactionUpdate())) {

                    LogUtils.trace(Tag, "Setup for FragmentTransactionUpdate");
                    String tagOfFragment = ((ActivityMain) getActivity()).getFragmentTransactionUpdate();
                    FragmentTransactionUpdate fragment1 = (FragmentTransactionUpdate) getActivity().getSupportFragmentManager().findFragmentByTag(tagOfFragment);
                    fragment1.updateAccount(mTransactionType, arAccounts.get(position).getId());

                }

                // Back to FragmentTransactionNew
                getFragmentManager().popBackStackImmediate();
            }
        });

        /* Show/Hide TextView Empty Category */
        if(arAccounts.size() > 0) {
            tvEmpty.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
        }
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(((ActivityMain) getActivity()).getCurrentVisibleItem() != ActivityMain.TAB_POSITION_TRANSACTION_CREATE) {
            return;
        }
        LogUtils.logEnterFunction(Tag, null);
        super.onCreateOptionsMenu(menu, inflater);

        /* Init ActionBar */
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_account));
        ((ActivityMain)getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * AccountAdapter: Adapter of account's listview
     */
    private class AccountAdapter extends ArrayAdapter<Account> {

        private class ViewHolder {
            LinearLayout    llAccount;
            ImageView       ivIcon;
            TextView        tvAccount;
            TextView        tvRemain;
            ImageView       ivUsing;
        }

        private List<Account> arAccounts;

        public AccountAdapter(Context context, List<Account> items) {
            super(context, R.layout.listview_item_account_select,items);
            this.arAccounts = items;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(getContext());

            final ViewHolder viewHolder;

            if (convertView == null || convertView.getTag() == null) {
                viewHolder = new ViewHolder();

                convertView = inflater.inflate(R.layout.listview_item_account_select, parent, false);
                viewHolder.llAccount    = (LinearLayout) convertView.findViewById(R.id.llAccount);
                viewHolder.ivIcon       = (ImageView) convertView.findViewById(R.id.ivIcon);
                viewHolder.tvAccount    = (TextView) convertView.findViewById(R.id.tvAccount);
                viewHolder.tvRemain     = (TextView) convertView.findViewById(R.id.tvRemain);
                viewHolder.ivUsing      = (ImageView) convertView.findViewById(R.id.ivUsing);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.ivIcon.setImageResource(AccountType.getAccountTypeById(arAccounts.get(position).getTypeId()).getIcon());
            viewHolder.tvAccount.setText(arAccounts.get(position).getName());

            Double remain = mDbHelper.getAccountRemain(arAccounts.get(position).getId());
            viewHolder.tvRemain.setText(Currency.formatCurrency(getContext(), mConfigs.getInt(Configurations.Key.Currency), remain));

            if(mUsingAccountId == arAccounts.get(position).getId()) {
                viewHolder.ivUsing.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivUsing.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

    }

}
