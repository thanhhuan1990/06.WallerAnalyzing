package local.wallet.analyzing;

import android.app.DatePickerDialog;
import android.app.Dialog;
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

import org.droidparts.widget.ClearableEditText;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Budget;
import local.wallet.analyzing.model.Category.EnumDebt;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/19/2016.
 */
public class FragmentBudgetCUD extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    public static final String Tag = "BudgetCUD";

    private Calendar            mStartCal;
    private Calendar            mEndCal;
    private Configurations      mConfigs;
    private DatabaseHelper      mDbHelper;

    private ClearableEditText   etName;
    private EditText            etAmount;
    private TextView            tvCurrencyIcon;
    private LinearLayout        llCategory;
    private TextView            tvCategory;
    private LinearLayout        llRepeat;
    private TextView            tvRepeat;
    private LinearLayout        llStartDate;
    private TextView            tvStartDate;
    private LinearLayout        llEndDate;
    private TextView            tvEndDate;
    private LinearLayout        llIncremental;
    private CheckBox            cbIncremental;
    private TextView            tvDescription;
    private LinearLayout        llSave;
    private LinearLayout        llDelete;

    private Budget              mBudget;
    private int[]               arCategories = new int[0];
    private List<String>        arRepeat;
    private int                 repeatType  = 3;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle  bundle              = this.getArguments();
        if(bundle != null) {
            mBudget                 = (Budget)bundle.get("Budget");

            if(mBudget != null) {
                LogUtils.trace(Tag, mBudget.toString());
            }
        }
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_budget_create, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mStartCal = Calendar.getInstance();
        mStartCal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        mStartCal.clear(Calendar.MINUTE);
        mStartCal.clear(Calendar.SECOND);
        mStartCal.clear(Calendar.MILLISECOND);

        mEndCal = Calendar.getInstance();
        mEndCal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        mEndCal.clear(Calendar.MINUTE);
        mEndCal.clear(Calendar.SECOND);
        mEndCal.clear(Calendar.MILLISECOND);

        mConfigs        = new Configurations(getActivity());
        mDbHelper       = new DatabaseHelper(getActivity());

        String[] arTemp = getResources().getStringArray(R.array.budget_repeat_type);
        arRepeat        = Arrays.asList(arTemp);

        etName          = (ClearableEditText) getView().findViewById(R.id.etName);
        etAmount        = (EditText) getView().findViewById(R.id.etAmount);
        etAmount.addTextChangedListener(new CurrencyTextWatcher());
        tvCurrencyIcon  = (TextView) getView().findViewById(R.id.tvCurrencyIcon);
        tvCurrencyIcon.setText(Currency.getCurrencyIcon(mConfigs.getInt(Configurations.Key.Currency)));
        llCategory      = (LinearLayout) getView().findViewById(R.id.llCategory);
        llCategory.setOnClickListener(this);
        tvCategory      = (TextView) getView().findViewById(R.id.tvCategory);
        llRepeat        = (LinearLayout) getView().findViewById(R.id.llRepeat);
        llRepeat.setOnClickListener(this);
        tvRepeat        = (TextView) getView().findViewById(R.id.tvRepeat);
        tvRepeat.setText(arTemp[repeatType]);
        llStartDate     = (LinearLayout) getView().findViewById(R.id.llStartDate);
        llStartDate.setOnClickListener(this);
        tvStartDate     = (TextView) getView().findViewById(R.id.tvStartDate);
        tvStartDate.setText(String.format("%02d-%02d-%02d", mStartCal.get(Calendar.DAY_OF_MONTH), mStartCal.get(Calendar.MONTH) + 1, mStartCal.get(Calendar.YEAR)));

        llEndDate       = (LinearLayout) getView().findViewById(R.id.llEndDate);
        llEndDate.setOnClickListener(this);
        tvEndDate       = (TextView) getView().findViewById(R.id.tvEndDate);
        tvEndDate.setText(String.format("%02d-%02d-%02d", mStartCal.get(Calendar.DAY_OF_MONTH), mStartCal.get(Calendar.MONTH) + 1, mStartCal.get(Calendar.YEAR)));

        llIncremental   = (LinearLayout) getView().findViewById(R.id.llIncremental);
        cbIncremental   = (CheckBox) getView().findViewById(R.id.cbIncremental);
        cbIncremental.setOnCheckedChangeListener(this);
        tvDescription   = (TextView) getView().findViewById(R.id.tvDescription);
        llSave          = (LinearLayout) getView().findViewById(R.id.llSave);
        llSave.setOnClickListener(this);
        llDelete        = (LinearLayout) getView().findViewById(R.id.llDelete);
        llDelete.setOnClickListener(this);
        if(mBudget == null) {
            llDelete.setVisibility(View.GONE);
        }

        setViewData();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(((ActivityMain) getActivity()).getCurrentVisibleItem() != ActivityMain.TAB_POSITION_LIST_BUDGET) {
            return;
        }
        LogUtils.logEnterFunction(Tag, null);
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

        LogUtils.logLeaveFunction(Tag, null, null);
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
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentBudgetCategory();
                break;
            case R.id.llRepeat:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                showDialogRepeatType();
                break;
            case R.id.llStartDate:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                showDialogTime(R.id.llStartDate);
                break;
            case R.id.llEndDate:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                showDialogTime(R.id.llEndDate);
                break;
            case R.id.llSave:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                if(mBudget == null) {
                    createBudget();
                } else {
                    updateBudget();
                }
                break;
            case R.id.llDelete:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                deleteBudget();
                break;
            default:
                break;
        }
    }

    private void setViewData() {
        LogUtils.logEnterFunction(Tag, null);
        if(mBudget == null) {
            LogUtils.trace(Tag, "Create Budget");
            return;
        }

        mStartCal   = mBudget.getStartDate();
        mEndCal     = mBudget.getEndDate();

        etName.setText(mBudget.getName());

        String formatted = Currency.formatCurrencyDouble(mConfigs.getInt(Configurations.Key.Currency), mBudget.getAmount());
        etAmount.setText(formatted);

        tvCurrencyIcon.setText(Currency.getCurrencyIcon(mBudget.getCurrency()));
        updateCategory(mBudget.getCategories());

        repeatType  = mBudget.getRepeatType();
        tvRepeat.setText(arRepeat.get(repeatType));

        tvStartDate.setText(String.format("%02d-%02d-%02d", mBudget.getStartDate().get(Calendar.DAY_OF_MONTH), mBudget.getStartDate().get(Calendar.MONTH) + 1, mBudget.getStartDate().get(Calendar.YEAR)));

        if(repeatType == 0) {
            llEndDate.setVisibility(View.VISIBLE);
            tvEndDate.setText(String.format("%02d-%02d-%02d", mBudget.getEndDate().get(Calendar.DAY_OF_MONTH), mBudget.getEndDate().get(Calendar.MONTH) + 1, mBudget.getEndDate().get(Calendar.YEAR)));

            llIncremental.setVisibility(View.GONE);
        } else {
            llEndDate.setVisibility(View.GONE);
            llIncremental.setVisibility(View.VISIBLE);
        }

        cbIncremental.setChecked(mBudget.isIncremental());

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Create Budget
     */
    private void createBudget() {
        LogUtils.trace(Tag, null);
        String name = etName.getText().toString();
        if(name.equals("")) {
            etName.setError(getResources().getString(R.string.Input_Error_Account_Name_Empty));
            return;
        }

        if(arCategories.length == 0) {
            ((ActivityMain) getActivity()).showError("Please select Category!");
            return;
        }

        Double amount =  etAmount.getText().toString().equals("") ? 0 : Double.parseDouble(etAmount.getText().toString().replaceAll(",", ""));

        if(amount == 0) {
            ((ActivityMain) getActivity()).showError("Please input Budget Amount!");
            return;
        }

        Budget budget = new Budget(0,
                                    name,
                                    amount,
                                    arCategories,
                                    mConfigs.getInt(Configurations.Key.Currency),
                                    repeatType,
                                    mStartCal,
                                    mEndCal,
                                    cbIncremental.isChecked());
        LogUtils.trace(Tag, "Budget = " + budget.toString());
        long budgetId = mDbHelper.createBudget(budget);
        if(budgetId != 0) {
            getFragmentManager().popBackStackImmediate();
        }
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Update Budget
     */
    private void updateBudget() {
        LogUtils.trace(Tag, null);
        String name = etName.getText().toString();
        if(name.equals("")) {
            etName.setError(getResources().getString(R.string.Input_Error_Account_Name_Empty));
            return;
        }

        if(arCategories.length == 0) {
            ((ActivityMain) getActivity()).showError("Please select Category!");
            return;
        }

        Double amount =  etAmount.getText().toString().equals("") ? 0 : Double.parseDouble(etAmount.getText().toString().replaceAll(",", ""));

        if(amount == 0) {
            ((ActivityMain) getActivity()).showError("Please input Budget Amount!");
            return;
        }

        mBudget.setName(name);
        mBudget.setCategories(arCategories);
        mBudget.setAmount(amount);
        mBudget.setRepeatType(repeatType);
        mBudget.setStartDate(mStartCal);
        mBudget.setEndDate(mEndCal);
        mBudget.setIsIncremental(cbIncremental.isChecked());

        int result = mDbHelper.updateBudget(mBudget);

        getFragmentManager().popBackStackImmediate();

        LogUtils.logLeaveFunction(Tag, null, "Result = " + result);

    }

    /**
     * Delete Budget
     */
    private void deleteBudget() {
        LogUtils.trace(Tag, null);

        mDbHelper.deleteBudget(mBudget.getId());

        getFragmentManager().popBackStackImmediate();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Show dialog RepeatType
     */
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
                repeatType = position;
                tvRepeat.setText(arRepeat.get(repeatType));
                if (repeatType == 0) {
                    llEndDate.setVisibility(View.VISIBLE);
                    tvEndDate.setText(String.format("%02d-%02d-%02d", mEndCal.get(Calendar.DAY_OF_MONTH), mEndCal.get(Calendar.MONTH) + 1, mEndCal.get(Calendar.YEAR)));
                    llIncremental.setVisibility(View.GONE);
                } else {
                    llEndDate.setVisibility(View.GONE);
                    llIncremental.setVisibility(View.VISIBLE);
                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Show dialog Time
     * @param id
     */
    private void showDialogTime(final int id) {

        if(id == R.id.llStartDate) {

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            mStartCal.set(Calendar.YEAR, year);
                            mStartCal.set(Calendar.MONTH, monthOfYear);
                            mStartCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            tvStartDate.setText(String.format("%02d-%02d-%02d", mStartCal.get(Calendar.DAY_OF_MONTH), mStartCal.get(Calendar.MONTH) + 1, mStartCal.get(Calendar.YEAR)));

                        }
                    }, mStartCal.get(Calendar.YEAR), mStartCal.get(Calendar.MONTH), mStartCal.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        } else if(id == R.id.llEndDate) {

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            mEndCal.set(Calendar.YEAR, year);
                            mEndCal.set(Calendar.MONTH, monthOfYear);
                            mEndCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            tvEndDate.setText(String.format("%02d-%02d-%02d", mEndCal.get(Calendar.DAY_OF_MONTH), mEndCal.get(Calendar.MONTH) + 1, mEndCal.get(Calendar.YEAR)));

                        }
                    }, mEndCal.get(Calendar.YEAR), mEndCal.get(Calendar.MONTH), mEndCal.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        }

    }

    /**
     * Start fragment BudgetCategory ti select category
     */
    private void startFragmentBudgetCategory() {
        FragmentBudgetCategory nextFrag = new FragmentBudgetCategory();
        Bundle bundle = new Bundle();
        bundle.putIntArray("Categories", arCategories);
        nextFrag.setArguments(bundle);
        FragmentBudgetCUD.this.getFragmentManager().beginTransaction()
                .add(R.id.layout_budget, nextFrag, FragmentBudgetCategory.Tag)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Update category from return value from BudgetCategory
     * @param categories
     */
    public void updateCategory(int[] categories) {
        LogUtils.logEnterFunction(Tag, Arrays.toString(categories));
        arCategories = categories;

        if(arCategories.length == mDbHelper.getAllCategories(true, EnumDebt.NONE).size()) {
            tvCategory.setText("All");
        } else {
            String category = "";
            for(int i = 0 ; i < arCategories.length; i++) {
                if(checkContain(mDbHelper.getCategory(arCategories[i]).getParentId())) {
                    continue;
                }
                if(!category.equals("")) {
                    category += ", ";
                }
                category += mDbHelper.getCategory(arCategories[i]).getName();
            }

            tvCategory.setText(category);
        }

    }

    /**
     * Check ID is contain in list from BudgetCreateUpdateDelete
     * @param id
     * @return
     */
    private boolean checkContain(int id) {
        for(int i = 0 ; i < arCategories.length; i++) {
            if(arCategories[i] == id) {
                return true;
            }
        }

        return false;
    } // End checkContain

    /**
     * EditText's TextWatcher
     */
    private class CurrencyTextWatcher implements TextWatcher {
        private String current = "";

        public CurrencyTextWatcher() {}

        public synchronized void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LogUtils.logEnterFunction(Tag, null);

            if(!s.toString().equals(current)){
                etAmount.removeTextChangedListener(this);

                String inputted = s.toString().replaceAll(",", "").replaceAll(" ", "");
                if(inputted.equals("")) {
                    return;
                }
                String formatted = Currency.formatCurrencyDouble(mConfigs.getInt(Configurations.Key.Currency), Double.parseDouble(inputted));

                current = formatted;
                etAmount.setText(formatted);
                etAmount.setSelection(formatted.length());

                etAmount.addTextChangedListener(this);
            }

            LogUtils.logLeaveFunction(Tag, null, null);
        }
    } // End CurrencyTextWatcher

    /**
     * Adapter of Repeat Type
     */
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
        } // End getView
    } // End RepeatAdapter
}
