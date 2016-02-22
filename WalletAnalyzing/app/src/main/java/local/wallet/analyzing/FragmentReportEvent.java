package local.wallet.analyzing;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
import local.wallet.analyzing.model.Event;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/22/2016.
 */
public class FragmentReportEvent extends Fragment implements View.OnClickListener {
    private static final String TAG = "ReportEvent";

    private DatabaseHelper  mDbHelper;

    private Button          btnInProgress;
    private Button          btnCompleted;
    private ListView        lvEvents;
    private List<Event>     arEvents    = new ArrayList<>();
    private EventAdapter    adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, null);
        return inflater.inflate(R.layout.layout_fragment_report_event_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        super.onActivityCreated(savedInstanceState);

        mDbHelper       = new DatabaseHelper(getActivity());

        btnInProgress   = (Button) getView().findViewById(R.id.btnInProgress);
        btnInProgress.setOnClickListener(this);
        btnCompleted    = (Button) getView().findViewById(R.id.btnCompleted);
        btnCompleted.setOnClickListener(this);
        lvEvents        = (ListView) getView().findViewById(R.id.lvEvents);
        adapter         = new EventAdapter(getContext(), arEvents);
        lvEvents.setAdapter(adapter);
        lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInProgress:
                btnInProgress.setBackgroundResource(R.drawable.background_button_left_case_selected);
                btnInProgress.setTextColor(getResources().getColorStateList(R.color.button_textcolor_2));
                btnCompleted.setBackgroundResource(R.drawable.background_button_right_case);
                btnCompleted.setTextColor(getResources().getColorStateList(R.color.button_textcolor));
                break;
            case R.id.btnCompleted:
                btnCompleted.setBackgroundResource(R.drawable.background_button_left_case_selected);
                btnCompleted.setTextColor(getResources().getColorStateList(R.color.button_textcolor_2));
                btnInProgress.setBackgroundResource(R.drawable.background_button_right_case);
                btnInProgress.setTextColor(getResources().getColorStateList(R.color.button_textcolor));
                break;
            default:
                break;
        }
    }

    /**
     * Event adapter
     */
    private class EventAdapter extends ArrayAdapter<Event> {
        private class ViewHolder {
            LinearLayout    llEvent;
            TextView        tvEventName;
            TextView        tvDate;
            TextView        tvIncome;
            TextView        tvExpense;
        }

        List<Event> mEvents;
        public EventAdapter(Context context, List<Event> items) {
            super(context, R.layout.listview_item_event, items);
            this.mEvents  = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder              = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView             = inflater.inflate(R.layout.listview_item_event, parent, false);
                viewHolder.llEvent      = (LinearLayout) convertView.findViewById(R.id.llEvent);
                viewHolder.tvEventName  = (TextView) convertView.findViewById(R.id.tvEventName);
                viewHolder.tvDate       = (TextView) convertView.findViewById(R.id.tvDate);
                viewHolder.tvIncome     = (TextView) convertView.findViewById(R.id.tvIncome);
                viewHolder.tvExpense    = (TextView) convertView.findViewById(R.id.tvExpense);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Event event = mEvents.get(position);
            if(event != null) {

                viewHolder.llEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                viewHolder.tvEventName.setText(event.getName());
                viewHolder.tvDate.setText(String.format(getResources().getString(R.string.event_item_date),
                                                        String.format(getResources().getString(R.string.format_day_month_year),
                                                                        event.getStartDate().get(Calendar.DAY_OF_MONTH),
                                                                        event.getStartDate().get(Calendar.MONTH) + 1,
                                                                        event.getStartDate().get(Calendar.YEAR))));
                viewHolder.tvExpense.setText("");
                viewHolder.tvIncome.setText("");
            }

            return convertView;
        }
    }
}
