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

import java.util.Arrays;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Currency.CurrencyList;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentCurrencySelect extends Fragment {
    public static int                   mTab = 2;
    public static final String          Tag = "---[" + mTab + "]---CurrencySelect";

    private ActivityMain                mActivity;

    public interface ISelectCurrency {
        void onCurrencySelected(CurrencyList currency);
    }

    private int             mUsingCurrencyId;
    private ISelectCurrency mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle       = this.getArguments();
        mTab                = bundle.getInt("Tab", mTab);
        mUsingCurrencyId    = bundle.getInt("Currency", 1);
        mCallback           = (ISelectCurrency) bundle.getSerializable("Callback");

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onActivityCreated(savedInstanceState);

        mActivity   = (ActivityMain) getActivity();

        ListView lvCurrency   = (ListView) getView().findViewById(R.id.listview);
        CurrencyAdapter accountTypeAdapter = new CurrencyAdapter(getActivity(), Arrays.asList(Currency.CurrencyList.values()));
        lvCurrency.setAdapter(accountTypeAdapter);

        lvCurrency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mCallback.onCurrencySelected(Currency.getCurrencyById(Arrays.asList(Currency.CurrencyList.values()).get(position).getValue()));
                getFragmentManager().popBackStackImmediate();
            }
        });

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_listview_only, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreateOptionsMenu(menu, inflater);

        if(mTab != mActivity.getCurrentVisibleItem()) {
            LogUtils.error(Tag, "Wrong Tab. Return");
            LogUtils.logLeaveFunction(Tag, null, null);
            return;
        }

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_account_select_currency));
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     *
     */
    private class CurrencyAdapter extends ArrayAdapter<Currency.CurrencyList> {

        private class ViewHolder {
            TextView tvType;
            ImageView ivUsing;
        }

        public CurrencyAdapter(Context context, List<Currency.CurrencyList> items) {
            super(context, R.layout.listview_item_title_select, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.listview_item_title_select, parent, false);
                viewHolder.tvType = (TextView) convertView.findViewById(R.id.tvType);
                viewHolder.ivUsing = (ImageView) convertView.findViewById(R.id.ivUsing);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tvType.setText(Currency.getCurrencyName(getItem(position)));
            if(mUsingCurrencyId == getItem(position).getValue()) {
                viewHolder.ivUsing.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivUsing.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }
}
