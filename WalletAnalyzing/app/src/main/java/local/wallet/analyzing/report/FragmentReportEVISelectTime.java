package local.wallet.analyzing.report;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentReportEVISelectTime extends Fragment implements View.OnClickListener {
    public static int               mTab = 4;
    public static final String      Tag = "---[" + mTab + ".5]---ReportEVISelectTime";

    private ActivityMain            mActivity;

    public interface ISelectReportEVITime extends Serializable {
        void onReportEVITimeSelected(int time);
        void onReportEVITimeSelected(Calendar fromDate, Calendar toDate);
    }

    private int             mCurrentTime;
    private String[]        mTimes;
    private ISelectReportEVITime    mCallback;

    private ListView        lvTime;
    TimeAdapter             mListAdapter;

    private LinearLayout    llOtherSetting;
    private LinearLayout    llFromDate;
    private TextView        tvFromDate;
    private LinearLayout    llToDate;
    private TextView        tvToDate;

    private Calendar        mFromDate   = Calendar.getInstance();
    private Calendar        mToDate     = Calendar.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle       = this.getArguments();
        mTab                = bundle.getInt("Tab", mTab);
        mCurrentTime        = bundle.getInt("Time", 1);
        mFromDate.setTimeInMillis(bundle.getLong("FromDate"));
        mToDate.setTimeInMillis(bundle.getLong("ToDate"));
        mCallback           = (ISelectReportEVITime) bundle.getSerializable("Callback");

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_report_evi_select_time, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mActivity       = (ActivityMain) getActivity();

        mTimes          = getResources().getStringArray(R.array.report_evi_ar_viewedby);

        lvTime          = (ListView) getView().findViewById(R.id.lvTime);
        mListAdapter    = new TimeAdapter(getActivity(), Arrays.asList(mTimes));
        lvTime.setAdapter(mListAdapter);

        lvTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position != (mTimes.length - 1)) {
                    mCallback.onReportEVITimeSelected(position);

                    getFragmentManager().popBackStackImmediate();
                } else {
                    mCurrentTime = position;
                    mListAdapter.notifyDataSetChanged();

                    updateViewForPeriod();

                }
            }
        });

        llOtherSetting  = (LinearLayout) getView().findViewById(R.id.llOtherSetting);
        llFromDate      = (LinearLayout) getView().findViewById(R.id.llFromDate);
        llFromDate.setOnClickListener(this);
        tvFromDate      = (TextView) getView().findViewById(R.id.tvFromDate);
        tvFromDate.setText(String.format(getResources().getString(R.string.format_budget_day_month_year_2),
                mFromDate.get(Calendar.DATE),
                mFromDate.get(Calendar.MONTH) + 1,
                mFromDate.get(Calendar.YEAR)));
        llToDate        = (LinearLayout) getView().findViewById(R.id.llToDate);
        llToDate.setOnClickListener(this);
        tvToDate        = (TextView) getView().findViewById(R.id.tvToDate);
        tvToDate.setText(String.format(getResources().getString(R.string.format_budget_day_month_year_2),
                mToDate.get(Calendar.DATE),
                mToDate.get(Calendar.MONTH) + 1,
                mToDate.get(Calendar.YEAR)));

        LogUtils.logLeaveFunction(Tag, null, null);
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
        tvTitle.setText(getResources().getString(R.string.title_report_evi_time));
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        if(mCurrentTime == mTimes.length - 1) {
            updateViewForPeriod();
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llFromDate:
                showDialogFromDate();
                break;
            case R.id.llToDate:
                showDialogToDate();
                break;
            default:
                break;
        }
    }

    /**
     * Update View
     * 1. Show OtherSetting
     * 2. Update ActionBar
     */
    private void updateViewForPeriod() {
        LogUtils.logEnterFunction(Tag, null);

        llOtherSetting.setVisibility(View.VISIBLE);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_done, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_report_evi_time));
        ImageView ivDone = (ImageView) mCustomView.findViewById(R.id.ivDone);
        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onReportEVITimeSelected(mFromDate, mToDate);

                getFragmentManager().popBackStackImmediate();
            }
        });
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Show Dialog to select Time
     */
    private void showDialogFromDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        mFromDate.set(Calendar.YEAR, year);
                        mFromDate.set(Calendar.MONTH, monthOfYear);
                        mFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        tvFromDate.setText(String.format(getResources().getString(R.string.format_budget_day_month_year_2),
                                                        dayOfMonth,
                                                        monthOfYear + 1,
                                                        year));

                    }
                }, mFromDate.get(Calendar.YEAR), mFromDate.get(Calendar.MONTH), mFromDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    } // End showDialogFromDate

    /**
     * Show Dialog to select Time
     */
    private void showDialogToDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        Calendar tmp = Calendar.getInstance();
                        tmp.set(Calendar.YEAR, year);
                        tmp.set(Calendar.MONTH, monthOfYear);
                        tmp.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        if(tmp.getTimeInMillis() < mFromDate.getTimeInMillis()) {
                            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_todate_invalid));
                            return;
                        }

                        mToDate.set(Calendar.YEAR, year);
                        mToDate.set(Calendar.MONTH, monthOfYear);
                        mToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        tvToDate.setText(String.format(getResources().getString(R.string.format_budget_day_month_year_2),
                                                        dayOfMonth,
                                                        monthOfYear + 1,
                                                        year));

                    }
                }, mToDate.get(Calendar.YEAR), mToDate.get(Calendar.MONTH), mToDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    } // End showDialogFromDate

    /**
     *
     */
    private class TimeAdapter extends ArrayAdapter<String> {

        private class ViewHolder {
            TextView tvType;
            ImageView ivUsing;
        }

        public TimeAdapter(Context context, List<String> items) {
            super(context, R.layout.listview_item_title_select, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView             = inflater.inflate(R.layout.listview_item_title_select, parent, false);
                viewHolder.tvType       = (TextView) convertView.findViewById(R.id.tvType);
                viewHolder.ivUsing      = (ImageView) convertView.findViewById(R.id.ivUsing);

                convertView.setTag(viewHolder);
            } else {

                viewHolder = (ViewHolder) convertView.getTag();

            }

            viewHolder.tvType.setText(mTimes[position]);
            if(mCurrentTime == position) {
                viewHolder.ivUsing.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivUsing.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }
}
