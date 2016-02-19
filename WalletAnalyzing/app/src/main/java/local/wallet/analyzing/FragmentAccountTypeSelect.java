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
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.AccountType;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentAccountTypeSelect extends Fragment {

    private static final String TAG = "FragmentAccountTypeSelect";

    private String mTagOfSource = "";
    private int mUsingAccountTypeId;
    private int myExpenseUsingMode = 0;
    private int myIncomeUsingMode = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        mTagOfSource        = bundle.getString("Tag");
        mUsingAccountTypeId = bundle.getInt("AccountType", 1);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_account_select_type));
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        super.onCreateOptionsMenu(menu, inflater);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, null);
        return inflater.inflate(R.layout.layout_fragment_accounttype, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onActivityCreated(savedInstanceState);

        ListView lvAccountType   = (ListView) getView().findViewById(R.id.lvAccountType);
        AccountTypeAdapter accountTypeAdapter = new AccountTypeAdapter(getActivity(), AccountType.Accounts);
        lvAccountType.setAdapter(accountTypeAdapter);

        lvAccountType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mTagOfSource.equals(((ActivityMain) getActivity()).getFragmentAccountCreate())) {

                    LogUtils.trace(TAG, "Setup for FragmentAccountCreate");
                    // Return Type's Id to FragmentAccountCreate
                    String tagOfFragment = ((ActivityMain) getActivity()).getFragmentAccountCreate();
                    FragmentAccountCreate fragment = (FragmentAccountCreate) getActivity().getSupportFragmentManager().findFragmentByTag(tagOfFragment);
                    fragment.updateAccountType(AccountType.Accounts.get(position).getId());

                } else if (mTagOfSource.equals(((ActivityMain) getActivity()).getFragmentAccountUpdate())) {

                    LogUtils.trace(TAG, "Setup for FragmentAccountUpdate");
                    // Return Type's Id to FragmentAccountUpdate
                    String tagOfFragment = ((ActivityMain) getActivity()).getFragmentAccountUpdate();
                    FragmentAccountUpdate fragment = (FragmentAccountUpdate) getActivity().getSupportFragmentManager().findFragmentByTag(tagOfFragment);
                    fragment.updateAccountType(AccountType.Accounts.get(position).getId());

                }

                getFragmentManager().popBackStackImmediate();
            }
        });

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    /**
     *
     */
    private class AccountTypeAdapter extends ArrayAdapter<AccountType> {

        private class ViewHolder {
            ImageView ivIcon;
            TextView tvType;
            ImageView ivUsing;
        }

        public AccountTypeAdapter(Context context, List<AccountType> items) {
            super(context, R.layout.listview_item_account_type, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.listview_item_account_type, parent, false);
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
                viewHolder.tvType = (TextView) convertView.findViewById(R.id.tvType);
                viewHolder.ivUsing  = (ImageView) convertView.findViewById(R.id.ivUsing);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            AccountType accountType = getItem(position);

            if (accountType != null) {
                viewHolder.ivIcon.setImageResource(accountType.getIcon());
                viewHolder.tvType.setText(getResources().getString(accountType.getName()));
                if(mUsingAccountTypeId == accountType.getId()) {
                    viewHolder.ivUsing.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.ivUsing.setVisibility(View.INVISIBLE);
                }
            }

            return convertView;
        }
    }
}
