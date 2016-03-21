package local.wallet.analyzing;

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

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;

/**
 * Created by huynh.thanh.huan on 3/21/2016.
 */
public class FragmentReportExpenseAnalysisTime extends Fragment {

    private static final String Tag = "ReportExpenseAnalysisTime";

    private int             mCurrentTime;
    private String[]        mTimes;

    private ListView        lvTime;
    TimeAdapter             mListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle       = this.getArguments();
        mCurrentTime        = bundle.getInt("Time", 1);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_report_expense_analysis_time, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onActivityCreated(savedInstanceState);

        mTimes          = getResources().getStringArray(R.array.report_expense_analysis_ar_viewedby);

        lvTime          = (ListView) getView().findViewById(R.id.lvTime);
        mListAdapter    = new TimeAdapter(getActivity(), Arrays.asList(mTimes));
        lvTime.setAdapter(mListAdapter);

        lvTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String tagOfFragment = ((ActivityMain) getActivity()).getFragmentReportExpenseAnalysis();
                FragmentReportExpenseAnalysis fragment = (FragmentReportExpenseAnalysis) getActivity().getSupportFragmentManager().findFragmentByTag(tagOfFragment);
                fragment.updateTime(position);

                getFragmentManager().popBackStackImmediate();
            }
        });

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_report_evi_time));
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        super.onCreateOptionsMenu(menu, inflater);
        LogUtils.logLeaveFunction(Tag, null, null);
    }

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
