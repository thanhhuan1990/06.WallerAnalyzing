package local.wallet.analyzing;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import org.droidparts.widget.ClearableEditText;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.AccountType;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Event;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/22/2016.
 */
public class FragmentReportEventUpdate extends Fragment implements View.OnClickListener {
    public static final String Tag = "ReportEventUpdate";

    private DatabaseHelper      mDbHelper;
    private Configurations      mConfigs;
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
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        mEventId      = bundle.getInt("EventID", 0);

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End onCreate

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_report_event_update, container, false);
    } // End onCreateView

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mCal            = Calendar.getInstance();
        mConfigs        = new Configurations(getContext());
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

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End onActivityCreated

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(((ActivityMain) getActivity()).getCurrentVisibleItem() != ActivityMain.TAB_POSITION_REPORTS) {
            return;
        }

        LogUtils.logEnterFunction(Tag, null);
        super.onCreateOptionsMenu(menu, inflater);

        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        View mCustomView            = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView  tvTitle           = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(mEvent.getName());

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(Tag, null, null);
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
        LogUtils.logEnterFunction(Tag, null);

        mEvent.setName(etName.getText().toString());
        mEvent.setEndDate(tbFinished.isChecked() ? mCal : null);

        mDbHelper.updateEvent(mEvent);

        // Back to EventTransactions
        getFragmentManager().popBackStackImmediate();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Delete Event
     */
    private void deleteEvent() {
        LogUtils.logEnterFunction(Tag, null);

        mDbHelper.deleteEvent(mEventId);

        // Back to EventTransactions
        getFragmentManager().popBackStackImmediate();

        LogUtils.logLeaveFunction(Tag, null, null);
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
