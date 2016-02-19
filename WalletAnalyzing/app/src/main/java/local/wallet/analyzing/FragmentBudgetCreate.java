package local.wallet.analyzing;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import org.droidparts.widget.ClearableEditText;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.AccountType;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/19/2016.
 */
public class FragmentBudgetCreate extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = "BudgetCreate";

    private Calendar            mCal;
    private Configurations      mConfigs;
    private DatabaseHelper      mDbHelper;

    private ClearableEditText   etName;
    private EditText            etAmount;
    private TextView            tvCurrencyIcon;
    private LinearLayout        llCategory;
    private TextView            tvCategory;
    private LinearLayout        llRepeat;
    private TextView            tvRepeat;
    private LinearLayout        llFromDate;
    private TextView            tvFromDate;
    private CheckBox            cbMoveToNext;
    private TextView            tvDescription;

    private List<String>        arRepeat;
    private int                 repeatType  = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, null);
        return inflater.inflate(R.layout.layout_fragment_budget_create, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        super.onActivityCreated(savedInstanceState);

        mCal            = Calendar.getInstance();
        mConfigs        = new Configurations(getActivity());
        mDbHelper       = new DatabaseHelper(getActivity());

        String[] arTemp = getResources().getStringArray(R.array.budget_repeat_type);
        arRepeat        = Arrays.asList(arTemp);

        etName          = (ClearableEditText) getView().findViewById(R.id.etName);
        etAmount        = (EditText) getView().findViewById(R.id.etAmount);
        etAmount.addTextChangedListener(new CurrencyTextWatcher());
        tvCurrencyIcon  = (TextView) getView().findViewById(R.id.tvCurrencyIcon);
        tvCurrencyIcon.setText(Currency.getCurrencyIcon(Currency.getCurrencyById(mConfigs.getInt(Configurations.Key.Currency))));
        llCategory      = (LinearLayout) getView().findViewById(R.id.llCategory);
        llCategory.setOnClickListener(this);
        tvCategory      = (TextView) getView().findViewById(R.id.tvCategory);
        llRepeat        = (LinearLayout) getView().findViewById(R.id.llRepeat);
        llRepeat.setOnClickListener(this);
        tvRepeat        = (TextView) getView().findViewById(R.id.tvRepeat);
        llFromDate      = (LinearLayout) getView().findViewById(R.id.llFromDate);
        llFromDate.setOnClickListener(this);
        tvFromDate      = (TextView) getView().findViewById(R.id.tvFromDate);
        tvFromDate.setText(String.format("%02d-%02d-%02d", mCal.get(Calendar.DAY_OF_MONTH), mCal.get(Calendar.MONTH) + 1, mCal.get(Calendar.YEAR)));
        cbMoveToNext    = (CheckBox) getView().findViewById(R.id.cbMoveToNext);
        cbMoveToNext.setOnCheckedChangeListener(this);
        tvDescription   = (TextView) getView().findViewById(R.id.tvDescription);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);
        super.onCreateOptionsMenu(menu, inflater);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_cancel, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_budget_create));

        ImageView ivCancel = (ImageView) mCustomView.findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            tvDescription.setText(getResources().getString(R.string.new_budget_move_to_next_description));
        } else {
            tvDescription.setText(getResources().getString(R.string.new_budget_move_to_next_description_not));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llCategory:
                startFragmentBudgetCategory();
                break;
            case R.id.llRepeat:
                showDialogRepeatType();
                break;
            case R.id.llFromDate:
                showDialogTime();
                break;
            default:
                break;
        }
    }

    private void showDialogRepeatType() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_repeat);

        ListView lvRepeat = (ListView) dialog.findViewById(R.id.lvRepeatType);
        lvRepeat.setAdapter(new RepeatAdapter(getContext(), arRepeat));

        lvRepeat.setSelection(repeatType);
        lvRepeat.setItemChecked(repeatType, true);
        lvRepeat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                repeatType  = position;
                tvRepeat.setText(arRepeat.get(repeatType));
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showDialogTime() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        mCal.set(Calendar.YEAR, year);
                        mCal.set(Calendar.MONTH, monthOfYear);
                        mCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        tvFromDate.setText(String.format("%02d-%02d-%02d", mCal.get(Calendar.DAY_OF_MONTH), mCal.get(Calendar.MONTH) + 1, mCal.get(Calendar.YEAR)));

                    }
                }, mCal.get(Calendar.YEAR), mCal.get(Calendar.MONTH), mCal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void startFragmentBudgetCategory() {
        FragmentBudgetCategory nextFrag = new FragmentBudgetCategory();
//        Bundle bundle = new Bundle();
//        bundle.putString("Tag", ((ActivityMain)getActivity()).getFragmentAccountCreate());
//        bundle.putInt("Currency", mCurrency.getValue());
//        nextFrag.setArguments(bundle);
        FragmentBudgetCreate.this.getFragmentManager().beginTransaction()
                .add(R.id.layout_budget, nextFrag, "FragmentBudgetCategory")
                .addToBackStack(null)
                .commit();
    }

    /**
     * EditText's TextWatcher
     */
    private class CurrencyTextWatcher implements TextWatcher {
        private String current = "";

        public CurrencyTextWatcher() {}

        public synchronized void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LogUtils.logEnterFunction(TAG, null);

            if(!s.toString().equals(current)){
                etAmount.removeTextChangedListener(this);

                LogUtils.trace(TAG, "input: " + s.toString());

                String inputted = s.toString().replaceAll(",", "").replaceAll(" ", "");
                String formatted = Currency.formatCurrencyDouble(Currency.getCurrencyById(mConfigs.getInt(Configurations.Key.Currency)), Double.parseDouble(inputted));

                current = formatted;
                etAmount.setText(formatted);
                etAmount.setSelection(formatted.length());

                etAmount.addTextChangedListener(this);
            }

            LogUtils.logLeaveFunction(TAG, null, null);
        }
    }

    private class RepeatAdapter extends ArrayAdapter<String> {
        private class ViewHolder {
            TextView tvRepeatName;
            ImageView ivChecked;
        }

        private List<String> mList;

        public RepeatAdapter(Context context, List<String> items) {
            super(context, R.layout.listview_item_repeat_type, items);
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

            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.listview_item_repeat_type, parent, false);
                viewHolder.tvRepeatName = (TextView) convertView.findViewById(R.id.tvRepeatName);
                viewHolder.ivChecked = (ImageView) convertView.findViewById(R.id.ivChecked);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tvRepeatName.setText(mList.get(position));
            if(repeatType != position) {
                viewHolder.ivChecked.setVisibility(View.GONE);
            } else {
                viewHolder.ivChecked.setVisibility(View.VISIBLE);
            }

            return convertView;
        }
    }
}
