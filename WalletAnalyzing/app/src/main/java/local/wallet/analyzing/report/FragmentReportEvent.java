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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.main.Configs;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Event;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/22/2016.
 */
public class FragmentReportEvent extends Fragment implements View.OnClickListener {
    public static final int         mTab = 4;
    public static final String      Tag = "---[" + mTab + ".5]---ReportEvent";

    private ActivityMain            mActivity;

    private DatabaseHelper  mDbHelper;
    private Configs mConfigs;

    private boolean         isRunning   = true;
    private Button          btnInProgress;
    private Button          btnCompleted;
    private ListView        lvEvents;
    private List<Event>     arEvents    = new ArrayList<>();
    private EventAdapter    adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        LogUtils.logLeaveFunction(Tag);
        return inflater.inflate(R.layout.layout_fragment_report_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        super.onActivityCreated(savedInstanceState);

        mActivity       = (ActivityMain) getActivity();

        mConfigs        = new Configs(getContext());
        mDbHelper       = new DatabaseHelper(getActivity());

        arEvents        = mDbHelper.getRunningEvents();

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
                FragmentReportEventTransactions nextFrag = new FragmentReportEventTransactions();
                Bundle bundle = new Bundle();
                bundle.putInt("Tab", mTab);
                bundle.putInt("EventID", arEvents.get(position).getId());
                nextFrag.setArguments(bundle);
                mActivity.replaceFragment(mTab, R.id.ll_report, nextFrag, FragmentReportEventTransactions.Tag, true);
            }
        });

        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag);
        super.onCreateOptionsMenu(menu, inflater);

        if(mTab != mActivity.getCurrentVisibleItem()) {
            LogUtils.error(Tag, "Wrong Tab. Return");
            LogUtils.logLeaveFunction(Tag);
            return;
        }

        arEvents.clear();
        List<Event> arTmp = isRunning ? mDbHelper.getRunningEvents() : mDbHelper.getFinishedEvents();
        for (Event event : arTmp) {
            arEvents.add(event);
        }
        adapter.notifyDataSetChanged();

        LogUtils.logLeaveFunction(Tag);

        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInProgress: {
                isRunning = true;
                btnInProgress.setBackgroundResource(R.drawable.background_button_left_case_selected);
                btnInProgress.setTextColor(getResources().getColorStateList(R.color.button_textcolor_2));
                btnCompleted.setBackgroundResource(R.drawable.background_button_right_case);
                btnCompleted.setTextColor(getResources().getColorStateList(R.color.button_textcolor));

                arEvents.clear();
                List<Event> arTmp = mDbHelper.getRunningEvents();
                for (Event event : arTmp) {
                    arEvents.add(event);
                }
                adapter.notifyDataSetChanged();
                break;
            }
            case R.id.btnCompleted: {
                isRunning = false;
                btnInProgress.setBackgroundResource(R.drawable.background_button_left_case);
                btnInProgress.setTextColor(getResources().getColorStateList(R.color.button_textcolor));
                btnCompleted.setBackgroundResource(R.drawable.background_button_right_case_selected);
                btnCompleted.setTextColor(getResources().getColorStateList(R.color.button_textcolor_2));

                arEvents.clear();
                List<Event> arTmp = mDbHelper.getFinishedEvents();
                for (Event event : arTmp) {
                    arEvents.add(event);
                }
                adapter.notifyDataSetChanged();
                break;
            }
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

                viewHolder.tvEventName.setText(event.getName());
                viewHolder.tvDate.setText(String.format(getResources().getString(R.string.event_item_date),
                                                        String.format(getResources().getString(R.string.format_day_month_year),
                                                                        event.getStartDate().get(Calendar.DAY_OF_MONTH),
                                                                        event.getStartDate().get(Calendar.MONTH) + 1,
                                                                        event.getStartDate().get(Calendar.YEAR))));

                List<Transaction> arTransactions = mDbHelper.getTransactionsByEvent(event.getId());
                Double expense = 0.0, income = 0.0;

                for (Transaction tran : arTransactions) {
                    if (tran.getTransactionType() == Transaction.TransactionEnum.Expense.getValue()) {
                        expense += tran.getAmount();
                    } else if (tran.getTransactionType() == Transaction.TransactionEnum.Income.getValue()) {
                        income += tran.getAmount();
                    } else if (tran.getTransactionType() == Transaction.TransactionEnum.Adjustment.getValue()) {
                        if(tran.getFromAccountId() != 0) {
                            expense += tran.getAmount();
                        } else if(tran.getToAccountId() != 0) {
                            income += tran.getAmount();
                        }
                    }
                }

                if(expense != 0) {
                    viewHolder.tvExpense.setText(String.format(getResources().getString(R.string.content_expense,
                                                                Currency.formatCurrency(getContext(), mConfigs.getInt(Configs.Key.Currency), expense))));
                } else {
                    viewHolder.tvExpense.setVisibility(View.GONE);
                }
                if(income != 0) {
                    viewHolder.tvIncome.setText(String.format(getResources().getString(R.string.content_income,
                                                            Currency.formatCurrency(getContext(), mConfigs.getInt(Configs.Key.Currency), income))));
                } else {
                    viewHolder.tvIncome.setVisibility(View.GONE);
                }

            }

            return convertView;
        }
    }
}
