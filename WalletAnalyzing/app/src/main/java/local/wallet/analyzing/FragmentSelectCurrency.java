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
import local.wallet.analyzing.model.Currency;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentSelectCurrency extends Fragment {

    private static final String TAG = "FragmentSelectCurrency";

    private String mTagOfSource = "";
    private int mUsingCurrencyId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        mTagOfSource        = bundle.getString("Tag");
        mUsingCurrencyId = bundle.getInt("Currency", 1);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_account_select_currency));
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        super.onCreateOptionsMenu(menu, inflater);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, null);
        return inflater.inflate(R.layout.layout_fragment_currency, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onActivityCreated(savedInstanceState);

        ListView lvCurrency   = (ListView) getView().findViewById(R.id.lvCurrency);
        CurrencyAdapter accountTypeAdapter = new CurrencyAdapter(getActivity(), Currency.Currencies);
        lvCurrency.setAdapter(accountTypeAdapter);

        lvCurrency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mTagOfSource.equals(((ActivityMain) getActivity()).getFragmentAccountAdd())) {
                    LogUtils.trace(TAG, "Setup for FragmentAccountAdd");
                    // Return Type's Id to FragmentAccountAdd
                    String TabOfFragmentAccountAdd = ((ActivityMain) getActivity()).getFragmentAccountAdd();
                    FragmentAccountAdd fragmentAccountAdd = (FragmentAccountAdd) getActivity()
                            .getSupportFragmentManager()
                            .findFragmentByTag(TabOfFragmentAccountAdd);
                    fragmentAccountAdd.updateCurrency(Currency.Currencies.get(position).getId());

                    getFragmentManager().popBackStackImmediate();
                } else if (mTagOfSource.equals(((ActivityMain) getActivity()).getFragmentAccountEdit())) {
                    LogUtils.trace(TAG, "Setup for FragmentAccountEdit");
                    // Return Type's Id to FragmentAccountAdd
                    String TabOfFragmentAccountEdit = ((ActivityMain) getActivity()).getFragmentAccountEdit();
                    FragmentAccountEdit fragmentAccountEdit = (FragmentAccountEdit) getActivity()
                            .getSupportFragmentManager()
                            .findFragmentByTag(TabOfFragmentAccountEdit);
                    fragmentAccountEdit.updateCurrency(Currency.Currencies.get(position).getId());

                }
            }
        });

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    /**
     *
     */
    private class CurrencyAdapter extends ArrayAdapter<Currency> {

        private class ViewHolder {
            ImageView ivIcon;
            TextView tvType;
            ImageView ivUsing;
        }

        public CurrencyAdapter(Context context, List<Currency> items) {
            super(context, R.layout.listview_item_currency, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.listview_item_currency, parent, false);
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
                viewHolder.tvType = (TextView) convertView.findViewById(R.id.tvType);
                viewHolder.ivUsing = (ImageView) convertView.findViewById(R.id.ivUsing);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Currency currency = getItem(position);

            if (currency != null) {
                viewHolder.ivIcon.setImageResource(currency.getIcon());
                viewHolder.tvType.setText(getResources().getString(currency.getName()));
                if(mUsingCurrencyId == currency.getId()) {
                    viewHolder.ivUsing.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.ivUsing.setVisibility(View.INVISIBLE);
                }
            }

            return convertView;
        }
    }
}
