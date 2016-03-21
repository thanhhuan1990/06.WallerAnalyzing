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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Event;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/22/2016.
 */
public class FragmentReportFinancialStatement extends Fragment implements View.OnClickListener {
    private static final String Tag = "ReportFinancialStatement";

    private DatabaseHelper  mDbHelper;
    private Configurations  mConfigs;

    private TextView        tvAsset;
    private ListView        lvAssets;
    private LinearLayout    llLent;
    private TextView        tvLent;
    private TextView        tvLiabilities;
    private LinearLayout    llBorrowed;
    private TextView        tvBorrowed;
    private TextView        tvNetWorth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_report_financial_statement, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mConfigs        = new Configurations(getContext());
        mDbHelper       = new DatabaseHelper(getActivity());

        tvAsset         = (TextView) getView().findViewById(R.id.tvAsset);
        lvAssets        = (ListView) getView().findViewById(R.id.lvAssets);
        tvLent          = (TextView) getView().findViewById(R.id.tvLent);
        tvLiabilities   = (TextView) getView().findViewById(R.id.tvLiabilities);
        tvBorrowed      = (TextView) getView().findViewById(R.id.tvBorrowed);
        tvNetWorth      = (TextView) getView().findViewById(R.id.tvNetWorth);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(((ActivityMain) getActivity()).getCurrentVisibleItem() != ActivityMain.TAB_POSITION_REPORTS) {
            return;
        }
        LogUtils.logEnterFunction(Tag, null);

        LogUtils.logLeaveFunction(Tag, null, null);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

}
