package local.wallet.analyzing.account;

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

import java.io.Serializable;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.model.AccountType;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentAccountTypeSelect extends Fragment {

    public static final String Tag = "FragmentAccountTypeSelect";

    public interface ISelectAccountType extends Serializable {
        void onAccountTypeSelected(int accountTypeId);
    }

    private int                 mUsingAccountTypeId;

    private ISelectAccountType  mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        mUsingAccountTypeId = bundle.getInt("AccountType", 1);
        mCallback           = (ISelectAccountType) bundle.getSerializable("Callback");

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_account_select_type));
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        super.onCreateOptionsMenu(menu, inflater);
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_accounttype, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onActivityCreated(savedInstanceState);

        ListView lvAccountType   = (ListView) getView().findViewById(R.id.lvAccountType);
        AccountTypeAdapter accountTypeAdapter = new AccountTypeAdapter(getActivity(), AccountType.Accounts);
        lvAccountType.setAdapter(accountTypeAdapter);

        lvAccountType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mCallback.onAccountTypeSelected(AccountType.Accounts.get(position).getId());

                getFragmentManager().popBackStackImmediate();
            }
        });

        LogUtils.logLeaveFunction(Tag, null, null);
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
