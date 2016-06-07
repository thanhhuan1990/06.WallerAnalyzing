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
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentReport extends Fragment {

    public static final String Tag = "Report";

    private View                mActionBar;
    private Spinner             spReportType;
    private int                 mCurrentReportType = 0;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreateOptionsMenu(menu, inflater);

        if(((ActivityMain) getActivity()).getCurrentVisibleItem() != ActivityMain.TAB_POSITION_REPORTS) {
            LogUtils.trace(Tag, "CurrentVisibleItem is NOT TAB_POSITION_REPORTS");
            LogUtils.logLeaveFunction(Tag, null, null);
            return;
        }

        if(mActionBar == null) {
            initActionBar();
        }

        ((ActivityMain)getActivity()).updateActionBar(mActionBar);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_report, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onResume() {
        LogUtils.logEnterFunction(Tag, null);
        super.onResume();

        if((getFragmentManager().findFragmentByTag(FragmentReportEVI.Tag) != null &&
                getFragmentManager().findFragmentByTag(FragmentReportEVI.Tag).isVisible())) {
            LogUtils.warn(Tag, "FragmentReportEVI is visible ---> Resume");
            getFragmentManager().findFragmentByTag(FragmentReportEVI.Tag).onResume();
        } else if((getFragmentManager().findFragmentByTag(FragmentReportExpenseAnalysis.Tag) != null &&
                getFragmentManager().findFragmentByTag(FragmentReportExpenseAnalysis.Tag).isVisible())) {
            LogUtils.warn(Tag, "FragmentReportExpenseAnalysis is visible ---> Resume");
            getFragmentManager().findFragmentByTag(FragmentReportExpenseAnalysis.Tag).onResume();
        } else if((getFragmentManager().findFragmentByTag(FragmentReportFinancialStatement.Tag) != null &&
                getFragmentManager().findFragmentByTag(FragmentReportFinancialStatement.Tag).isVisible())) {
            LogUtils.warn(Tag, "FragmentReportFinancialStatement is visible ---> Resume");
            getFragmentManager().findFragmentByTag(FragmentReportFinancialStatement.Tag).onResume();
        } else if((getFragmentManager().findFragmentByTag(FragmentReportLentBorrowed.Tag) != null &&
                getFragmentManager().findFragmentByTag(FragmentReportLentBorrowed.Tag).isVisible())) {
            LogUtils.warn(Tag, "FragmentReportLentBorrowed is visible ---> Resume");
            getFragmentManager().findFragmentByTag(FragmentReportLentBorrowed.Tag).onResume();
        } else if((getFragmentManager().findFragmentByTag(FragmentReportEvent.Tag) != null &&
                getFragmentManager().findFragmentByTag(FragmentReportEvent.Tag).isVisible())) {
            LogUtils.warn(Tag, "FragmentReportEvent is visible ---> Resume");
            getFragmentManager().findFragmentByTag(FragmentReportEvent.Tag).onResume();
        } else {
            switch (mCurrentReportType) {
                case 0:
                    showExpenseVsIncome();
                    break;
                case 1:
                    showExpenseAnalysis();
                    break;
                case 2:
                    showFinancialStatement();
                    break;
                case 3:
                    showLentBorrowed();
                    break;
                case 4:
                    showListEvents();
                    break;
                default:
                    break;
            }
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Create action bar
     */
    private void initActionBar() {
        /* Todo: Update ActionBar: Spinner ReportType */
        String[] arReportType       = getResources().getStringArray(R.array.report_type);

        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        mActionBar                  = mInflater.inflate(R.layout.action_bar_with_spinner, null);

        spReportType                = (Spinner) mActionBar.findViewById(R.id.spinner);
        spReportType.setAdapter(new ReportTypeAdapter(getActivity().getApplicationContext(), Arrays.asList(arReportType)));
        spReportType.setSelection(mCurrentReportType);

        spReportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.trace(Tag, "onItemSelected: " + position);
                mCurrentReportType = position;
                switch (position) {
                    case 0:
                        showExpenseVsIncome();
                        break;
                    case 1:
                        showExpenseAnalysis();
                        break;
                    case 2:
                        showFinancialStatement();
                        break;
                    case 3:
                        showLentBorrowed();
                        break;
                    case 4:
                        showListEvents();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Start Fragment ExpenseVsIncome (EVI)
     */
    private void showExpenseVsIncome() {
        LogUtils.logEnterFunction(Tag, null);
        if((getFragmentManager().findFragmentByTag(FragmentReportEVI.Tag) != null &&
                getFragmentManager().findFragmentByTag(FragmentReportEVI.Tag).isVisible())) {
            LogUtils.warn(Tag, "FragmentReportEVI is visible ---> Resume");
            getFragmentManager().findFragmentByTag(FragmentReportEVI.Tag).onResume();
        }

        FragmentReportEVI nextFrag = new FragmentReportEVI();
        FragmentReport.this.getFragmentManager().beginTransaction()
                .replace(R.id.ll_report, nextFrag, FragmentReportEVI.Tag)
                .commit();
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Start Fragment ExpenseAnalysis
     */
    private void showExpenseAnalysis() {
        LogUtils.logEnterFunction(Tag, null);
        if((getFragmentManager().findFragmentByTag(FragmentReportExpenseAnalysis.Tag) != null &&
                getFragmentManager().findFragmentByTag(FragmentReportExpenseAnalysis.Tag).isVisible())) {
            LogUtils.warn(Tag, "FragmentReportExpenseAnalysis is visible ---> Resume");
            getFragmentManager().findFragmentByTag(FragmentReportExpenseAnalysis.Tag).onResume();
        }

        FragmentReportExpenseAnalysis nextFrag = new FragmentReportExpenseAnalysis();
        FragmentReport.this.getFragmentManager().beginTransaction()
                .replace(R.id.ll_report, nextFrag, FragmentReportExpenseAnalysis.Tag)
                .commit();
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Start Fragment FinancialStatement
     */
    private void showFinancialStatement() {
        LogUtils.logEnterFunction(Tag, null);
        if((getFragmentManager().findFragmentByTag(FragmentReportFinancialStatement.Tag) != null &&
                getFragmentManager().findFragmentByTag(FragmentReportFinancialStatement.Tag).isVisible())) {
            LogUtils.warn(Tag, "FragmentReportFinancialStatement is visible ---> Resume");
            getFragmentManager().findFragmentByTag(FragmentReportFinancialStatement.Tag).onResume();
        }

        FragmentReportFinancialStatement nextFrag = new FragmentReportFinancialStatement();
        FragmentReport.this.getFragmentManager().beginTransaction()
                .replace(R.id.ll_report, nextFrag, FragmentReportFinancialStatement.Tag)
                .commit();
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Start Fragment ReportLentBorrowed
     */
    private void showLentBorrowed() {
        LogUtils.logEnterFunction(Tag, null);
        if((getFragmentManager().findFragmentByTag(FragmentReportLentBorrowed.Tag) != null &&
                getFragmentManager().findFragmentByTag(FragmentReportLentBorrowed.Tag).isVisible())) {
            LogUtils.warn(Tag, "FragmentReportLentBorrowed is visible ---> Resume");
            getFragmentManager().findFragmentByTag(FragmentReportLentBorrowed.Tag).onResume();
        }

        FragmentReportLentBorrowed nextFrag = new FragmentReportLentBorrowed();
        FragmentReport.this.getFragmentManager().beginTransaction()
                .replace(R.id.ll_report, nextFrag, FragmentReportLentBorrowed.Tag)
                .commit();
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Start Fragment ReportEvent
     */
    private void showListEvents() {
        LogUtils.logEnterFunction(Tag, null);
        if((getFragmentManager().findFragmentByTag(FragmentReportEvent.Tag) != null &&
                getFragmentManager().findFragmentByTag(FragmentReportEvent.Tag).isVisible())) {
            LogUtils.warn(Tag, "FragmentReportEvent is visible ---> Resume");
            getFragmentManager().findFragmentByTag(FragmentReportEvent.Tag).onResume();
        }

        FragmentReportEvent nextFrag = new FragmentReportEvent();
        FragmentReport.this.getFragmentManager().beginTransaction()
                .replace(R.id.ll_report, nextFrag, FragmentReportEvent.Tag)
                .commit();
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Spinner Report Type's adapter
     */
    private class ReportTypeAdapter extends ArrayAdapter<String> {
        private class ViewHolder {
            TextView tvType;
        }

        private List<String> mList;

        public ReportTypeAdapter(Context context, List<String> items) {
            super(context, R.layout.spinner_report_type, items);
            this.mList = items;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public String getItem(int position) {
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
                viewHolder                  = new ViewHolder();
                LayoutInflater inflater     = LayoutInflater.from(getContext());
                convertView                 = inflater.inflate(R.layout.spinner_report_type, parent, false);
                viewHolder.tvType           = (TextView) convertView.findViewById(R.id.tvType);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tvType.setText(mList.get(position));

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater     = LayoutInflater.from(getContext());
                convertView                 = inflater.inflate(R.layout.spinner_report_type_dropdown_item, parent, false);
                viewHolder.tvType           = (TextView) convertView.findViewById(R.id.tvType);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tvType.setText(mList.get(position));

            return convertView;
        }
    }
}