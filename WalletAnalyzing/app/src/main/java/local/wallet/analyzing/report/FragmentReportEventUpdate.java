package local.wallet.analyzing.report;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.droidparts.widget.ClearableEditText;

import java.util.Calendar;
import java.util.Locale;

import local.wallet.analyzing.R;
import local.wallet.analyzing.utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.main.Configs;
import local.wallet.analyzing.model.Event;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/22/2016.
 */
public class FragmentReportEventUpdate extends Fragment implements View.OnClickListener {
    public static int               mTab = 4;
    public static final String      Tag = "---[" + mTab + ".5]---ReportEventUpdate";

    private ActivityMain            mActivity;

    private DatabaseHelper      mDbHelper;
    private Configs mConfigs;
    private Calendar            mCal;

    private int                 mEventId;
    private Event               mEvent;

    private ClearableEditText   etName;
    private ToggleButton        tbFinished;
    private LinearLayout        llFinishDate;
    private TextView            tvFinishDate;
    private LinearLayout        llSave;
    private LinearLayout        llDelete;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle   = this.getArguments();
        mTab            = bundle.getInt("Tab", mTab);
        mEventId        = bundle.getInt("EventID", 0);

        LogUtils.logLeaveFunction(Tag);
    } // End onCreate

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        LogUtils.logLeaveFunction(Tag);
        return inflater.inflate(R.layout.layout_fragment_report_event_update, container, false);
    } // End onCreateView

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        super.onActivityCreated(savedInstanceState);

        mActivity       = (ActivityMain) getActivity();

        mCal            = Calendar.getInstance();
        mConfigs        = new Configs(getContext());
        mDbHelper       = new DatabaseHelper(getActivity());

        mEvent          = mDbHelper.getEvent(mEventId);

        etName          = (ClearableEditText) getView().findViewById(R.id.etName);
        etName.setText(mEvent.getName());

        llFinishDate    = (LinearLayout) getView().findViewById(R.id.llFinishDate);
        llFinishDate.setOnClickListener(this);
        tvFinishDate    = (TextView) getView().findViewById(R.id.tvFinishDate);
        tvFinishDate.setText(mEvent.getEndDate() != null ? String.format(getResources().getString(R.string.format_day_month_year),
                                                                        mEvent.getEndDate().get(Calendar.DATE),
                                                                        mEvent.getEndDate().get(Calendar.MONTH),
                                                                        mEvent.getEndDate().get(Calendar.YEAR)) :
                                                            "");

        llSave          = (LinearLayout) getView().findViewById(R.id.llSave);
        llSave.setOnClickListener(this);
        llDelete        = (LinearLayout) getView().findViewById(R.id.llDelete);
        llDelete.setOnClickListener(this);

        tbFinished      = (ToggleButton) getView().findViewById(R.id.tbFinished);
        tbFinished.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                llFinishDate.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                if (mEvent.getEndDate() != null) {
                    tvFinishDate.setText(getDateString(mEvent.getEndDate()));
                } else {
                    tvFinishDate.setText(getDateString(mCal));
                }
            }
        });
        tbFinished.setChecked(mEvent.getEndDate() != null ? true : false);

        LogUtils.logLeaveFunction(Tag);
    } // End onActivityCreated

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag);
        super.onCreateOptionsMenu(menu, inflater);

        if(mTab != mActivity.getCurrentVisibleItem()) {
            LogUtils.error(Tag, "Wrong Tab. Return");
            LogUtils.logLeaveFunction(Tag);
            return;
        }

        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        View mCustomView            = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView  tvTitle           = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(mEvent.getName());

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(Tag);
    } // End onCreateOptionsMenu

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llFinishDate:
                showDialogTime();
                break;
            case R.id.llSave:
                updateEvent();
                break;
            case R.id.llDelete:
                deleteEvent();
                break;
            default:
                break;
        }
    } // End onClick

    /**
     * Show Dialog to select Time
     */
    private void showDialogTime() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mCal.set(Calendar.YEAR, year);
                        mCal.set(Calendar.MONTH, monthOfYear);
                        mCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        tvFinishDate.setText(getDateString(mCal));
                    }
                }, mCal.get(Calendar.YEAR), mCal.get(Calendar.MONTH), mCal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    } // End showDialogTime

    /**
     * Update current Event
     */
    private void updateEvent() {
        LogUtils.logEnterFunction(Tag);

        mEvent.setName(etName.getText().toString());
        mEvent.setEndDate(tbFinished.isChecked() ? mCal : null);

        mDbHelper.updateEvent(mEvent);

        // Back to EventTransactions
        getFragmentManager().popBackStackImmediate();

        LogUtils.logLeaveFunction(Tag);
    }

    /**
     * Delete Event
     */
    private void deleteEvent() {
        LogUtils.logEnterFunction(Tag);

        mDbHelper.deleteEvent(mEventId);

        // Back to EventTransactions
        getFragmentManager().popBackStackImmediate();

        LogUtils.logLeaveFunction(Tag);
    }

    /**
     * Convert Calendar to String DD/MM/YYYY
     * @param cal
     * @return
     */
    private String getDateString(Calendar cal) {
        Calendar current = Calendar.getInstance();
        String date = "";
        if(cal.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)) {
            date = getResources().getString(R.string.content_today);
        } else if((cal.get(Calendar.DAY_OF_YEAR) + 1) == current.get(Calendar.DAY_OF_YEAR)) {
            date = getResources().getString(R.string.content_yesterday);
        } else if((cal.get(Calendar.DAY_OF_YEAR) + 2) == current.get(Calendar.DAY_OF_YEAR) && getResources().getConfiguration().locale.equals(Locale.forLanguageTag("vi_VN"))) {
            date = getResources().getString(R.string.content_before_yesterday);
        } else {
            date = String.format("%02d-%02d-%02d", mCal.get(Calendar.DAY_OF_MONTH), mCal.get(Calendar.MONTH) + 1, mCal.get(Calendar.YEAR));
        }

        return date;
    }

} // End class FragmentReportEventTransactions
