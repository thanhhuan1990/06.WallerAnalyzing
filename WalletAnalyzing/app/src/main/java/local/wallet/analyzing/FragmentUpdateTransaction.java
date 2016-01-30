package local.wallet.analyzing;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.droidparts.widget.ClearableEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.model.Transaction.TransactionEnum;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentUpdateTransaction extends Fragment implements  View.OnClickListener {
    private static final String Tag = "FragmentUpdateTransaction";

    private DatabaseHelper db;

    private Transaction     mTransaction;
    private Category        mCategory;
    private Account         mFromAccount;
    private Account         mToAccount;
    private Spinner         spTransactionType;
    private TransactionEnum mCurrentTransactionType;

    /* Layout Expense */
    private LinearLayout        llExpense;
    private ClearableEditText   etExpenseAmount;
    private TextView            tvExpenseCurrencyIcon;
    private LinearLayout        llExpenseCategory;
    private TextView            tvExpenseCategory;
    private LinearLayout        llExpenseDescription;
    private TextView            tvExpenseDescription;
    private LinearLayout        llExpenseAccount;
    private TextView            tvExpenseAccount;
    private LinearLayout        llExpenseDate;
    private TextView            tvExpenseDate;
    private LinearLayout        llExpensePayee;
    private TextView            tvExpensePayee;
    private LinearLayout        llExpenseEvent;
    private TextView            tvExpenseEvent;

    /* Layout Income */
    private LinearLayout        llIncome;
    private ClearableEditText   etIncomeAmount;
    private TextView            tvIncomeCurrencyIcon;
    private LinearLayout        llIncomeCategory;
    private TextView            tvIncomeCategory;
    private LinearLayout        llIncomeDescription;
    private TextView            tvIncomeDescription;
    private LinearLayout        llIncomeToAccount;
    private TextView            tvIncomeToAccount;
    private LinearLayout        llIncomeDate;
    private TextView            tvIncomeDate;
    private LinearLayout        llIncomeEvent;
    private TextView            tvIncomeEvent;

    /* Layout Transfer */
    private LinearLayout        llTransfer;
    private ClearableEditText   etTransferAmount;
    private TextView            tvTransferCurrencyIcon;
    private LinearLayout        llTransferFromAccount;
    private TextView            tvTransferFromAccount;
    private LinearLayout        llTransferToAccount;
    private TextView            tvTransferToAccount;
    private LinearLayout        llTransferDescription;
    private TextView            tvTransferDescription;
    private LinearLayout        llTransferDate;
    private TextView            tvTransferDate;
    private ClearableEditText   etTransferFee;
    private TextView            tvTransferFeeCurrencyIcon;
    private LinearLayout        llTransferCategory;
    private TextView            tvTransferCategory;

    /* Adjustment */
    private LinearLayout        llAdjustment;
    private LinearLayout        llAdjustmentAccount;
    private TextView            tvAdjustmentAccount;
    private ClearableEditText   etAdjustmentBalance;
    private TextView            tvAdjustmentCurrencyIcon;
    private TextView            tvAdjustmentSpent;
    private LinearLayout        llAdjustmentCategory;
    private TextView            tvAdjustmentCategory;
    private LinearLayout        llAdjustmentDescription;
    private TextView            tvAdjustmentDescription;
    private LinearLayout        llAdjustmentDate;
    private TextView            tvAdjustmentDate;
    private LinearLayout        llAdjustmentPayee;
    private TextView            tvAdjustmentPayee;
    private LinearLayout        llAdjustmentEvent;
    private TextView            tvAdjustmentEvent;

    private LinearLayout        llSave;

    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        mTransaction                = (Transaction)bundle.get("Transaction");

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreateOptionsMenu(menu, inflater);

        initActionBar();

        switch (mCurrentTransactionType) {
            case Expense:
                spTransactionType.setSelection(0);
                break;
            case Income:
                spTransactionType.setSelection(1);
                break;
            case Transfer:
            case TransferFrom:
            case TransferTo:
                spTransactionType.setSelection(2);
                break;
            case Adjustment:
                spTransactionType.setSelection(3);
                break;
            default:
                spTransactionType.setSelection(0);
                break;
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_new_transaction, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();

        db = new DatabaseHelper(getActivity());
        Transaction tran = db.getLastTransaction();
        if(tran != null) {
            mFromAccount = db.getAccount(tran.getFromAccountId());
            mToAccount  = db.getAccount(tran.getToAccountId());
        } else {
            List<Account> accs = db.getAllAccounts();
            mFromAccount    = accs.size() > 0 ? accs.get(0) : null;
            mToAccount      = (accs.size() > 1 && mFromAccount != null) ? accs.get(1) : null;
        }

        // Get Current DateTime
        final Calendar c = Calendar.getInstance();
        mYear   = c.get(Calendar.YEAR);
        mMonth  = c.get(Calendar.MONTH);
        mDay    = c.get(Calendar.DAY_OF_MONTH);
        mHour   = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        initViewExpense();
        initViewIncome();
        initViewTransfer();
        initViewAdjustment();

        llSave      = (LinearLayout) getView().findViewById(R.id.llSave);
        llSave.setOnClickListener(this);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llExpenseCategory:
                startFragmentSelectCategory(TransactionEnum.Expense, mCategory != null ? mCategory.getId() : 0);
                break;
            case R.id.llIncomeCategory:
                startFragmentSelectCategory(TransactionEnum.Income, mCategory != null ? mCategory.getId() : 0);
                break;
            case R.id.llTransferCategory:
                startFragmentSelectCategory(TransactionEnum.TransferTo, mCategory != null ? mCategory.getId() : 0);
                break;
            case R.id.llAdjustmentCategory:
                startFragmentSelectCategory(TransactionEnum.Adjustment, mCategory != null ? mCategory.getId() : 0);
                break;
            case R.id.llExpenseDescription:
                startFragmentDescription(TransactionEnum.Expense, tvExpenseDescription.getText().toString());
                break;
            case R.id.llIncomeDescription:
                startFragmentDescription(TransactionEnum.Income, tvExpenseDescription.getText().toString());
                break;
            case R.id.llTransferDescription:
                startFragmentDescription(TransactionEnum.Transfer, tvExpenseDescription.getText().toString());
                break;
            case R.id.llAdjustmentDescription:
                startFragmentDescription(TransactionEnum.Adjustment, tvExpenseDescription.getText().toString());
                break;
            case R.id.llExpenseAccount:
                startFragmentSelectAccount(TransactionEnum.Expense, mFromAccount != null ? mFromAccount.getId() : 0);
                break;
            case R.id.llToAccount:
                startFragmentSelectAccount(TransactionEnum.Income, mToAccount != null ? mToAccount.getId() : 0);
                break;
            case R.id.llTransferFromAccount:
                startFragmentSelectAccount(TransactionEnum.TransferFrom, mFromAccount != null ? mFromAccount.getId() : 0);
                break;
            case R.id.llTransferToAccount:
                startFragmentSelectAccount(TransactionEnum.TransferTo, mToAccount != null ? mToAccount.getId() : 0);
                break;
            case R.id.llAdjustmentAccount:
                startFragmentSelectAccount(TransactionEnum.Adjustment, mFromAccount != null ? mFromAccount.getId() : 0);
                break;
            case R.id.llExpenseDate:
            case R.id.llIncomeDate:
            case R.id.llTransferDate:
            case R.id.llAdjustmentDate:
                final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                                                                        new TimePickerDialog.OnTimeSetListener() {

                                                                            @Override
                                                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                                                mHour   = hourOfDay;
                                                                                mMinute = minute;

                                                                                tvExpenseDate.setText(getDateString());
                                                                                tvIncomeDate.setText(getDateString());
                                                                                tvTransferDate.setText(getDateString());
                                                                                tvAdjustmentDate.setText(getDateString());

                                                                            }
                                                                        }, mHour, mMinute, true);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                                                                        new DatePickerDialog.OnDateSetListener() {

                                                                            @Override
                                                                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                                                mYear   = year;
                                                                                mMonth  = monthOfYear;
                                                                                mDay    = dayOfMonth;
                                                                                timePickerDialog.show();
                                                                            }
                                                                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            case R.id.llExpensePayee:
                startFragmentPayee(TransactionEnum.Expense, tvExpensePayee.getText().toString());
                break;
            case R.id.llAdjustmentPayee:
                startFragmentPayee(TransactionEnum.Adjustment, tvAdjustmentPayee.getText().toString());
                break;
            case R.id.llExpenseEvent:
                startFragmentEvent(TransactionEnum.Expense, tvExpenseEvent.getText().toString());
                break;
            case R.id.llIncomeEvent:
                startFragmentEvent(TransactionEnum.Income, tvIncomeEvent.getText().toString());
                break;
            case R.id.llAdjustmentEvent:
                startFragmentEvent(TransactionEnum.Adjustment, tvAdjustmentEvent.getText().toString());
                break;
            case R.id.llSave:
                switch (mCurrentTransactionType) {
                    case Expense: {

                        if (etExpenseAmount.getText().toString().equals("")) {
                            etExpenseAmount.setError(getResources().getString(R.string.Input_Error_Amount_Empty));
                            return;
                        }

                        if (mCategory == null) {
                            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Category_Empty));
                            return;
                        }
                        if(mFromAccount == null) {
                            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Account_Empty));
                            return;
                        }

                        Double expenseAmount = Double.parseDouble(etExpenseAmount.getText().toString().replaceAll(",", ""));
                        int expenseCategoryId = mCategory.getId();
                        String expenseDescription = tvExpenseDescription.getText().toString();
                        int expenseAccountId = mFromAccount.getId();
                        Date expenseDate = getDate(mYear, mMonth, mDay, mHour, mMinute);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(expenseDate);
                        String expensePayee = tvExpensePayee.getText().toString();
                        String expenseEvent = tvExpenseEvent.getText().toString();

                        // Todo: Update transaction
                        break;
                    }
                    case Income: {
                        if (etIncomeAmount.getText().toString().equals("")) {
                            etIncomeAmount.setError(getResources().getString(R.string.Input_Error_Amount_Empty));
                            return;
                        }
                        if (mCategory == null) {
                            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Category_Empty));
                            return;
                        }
                        if(mToAccount == null) {
                            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Account_Empty));
                            return;
                        }

                        Double incomeAmount = Double.parseDouble(etIncomeAmount.getText().toString().replaceAll(",", ""));
                        int incomeCategoryId = mCategory.getId();
                        String incomeDescription = tvIncomeDescription.getText().toString();
                        int incomeAccountId = mToAccount.getId();
                        Date expenseDate = getDate(mYear, mMonth, mDay, mHour, mMinute);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(expenseDate);
                        String incomeEvent = tvIncomeEvent.getText().toString();

                        // Todo: Update transaction
                        break;
                    }
                    case Transfer: {
                        if (etTransferAmount.getText().toString().equals("")) {
                            etTransferAmount.setError(getResources().getString(R.string.Input_Error_Amount_Empty));
                            return;
                        }

                        if(mFromAccount == null) {
                            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Account_Empty));
                            return;
                        }

                        if(mToAccount == null) {
                            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_ToAccount_Empty));
                            return;
                        }

                        Double transferAmount = Double.parseDouble(etTransferAmount.getText().toString().replaceAll(",", ""));

                        int fromAccountId = mFromAccount.getId();
                        int toAccountId = mToAccount.getId();

                        String transferDescription = tvTransferDescription.getText().toString();
                        Date transferDate = getDate(mYear, mMonth, mDay, mHour, mMinute);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(transferDate);

                        Double transferFee = !etTransferFee.getText().toString().equals("") ? Double.parseDouble(etTransferFee.getText().toString().replaceAll(",", "")) : 0.0;

                        int transferCategoryId = mCategory != null ? mCategory.getId() : 0;

                        // Todo: Update transaction

                        break;
                    }
                    case Adjustment:
                        break;
                    default:
                        break;
                }
            default:
                break;
        }
    }

    private void initActionBar() {
        LogUtils.logEnterFunction(Tag, null);
        /* Todo: Init Data */
        String[] arTransactionTypeName      = getResources().getStringArray(R.array.transaction_type);
        String[] arTransactionDescription   = getResources().getStringArray(R.array.transaction_type_description);

        ArrayList<TransactionType> arTransaction = new ArrayList<TransactionType>();
        for(int i = 0 ; i < arTransactionTypeName.length; i++) {
            arTransaction.add(new TransactionType(arTransactionTypeName[i], arTransactionDescription[i]));
        }

        /* Todo: Update ActionBar: Spinner TransactionType */
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_spinner, null);

        spTransactionType   = (Spinner) mCustomView.findViewById(R.id.spinner);
        spTransactionType.setAdapter(new TransactionTypeAdapter(getActivity().getApplicationContext(), arTransaction));

        spTransactionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.trace(Tag, "onItemSelected: " + position);

                llExpense.setVisibility(View.GONE);
                llIncome.setVisibility(View.GONE);
                llTransfer.setVisibility(View.GONE);
                llAdjustment.setVisibility(View.GONE);

                switch (position) {
                    case 0:
                        llExpense.setVisibility(View.VISIBLE);
                        mCurrentTransactionType = TransactionEnum.Expense;
                        break;
                    case 1:
                        llIncome.setVisibility(View.VISIBLE);
                        mCurrentTransactionType = TransactionEnum.Income;
                        break;
                    case 2:
                        llTransfer.setVisibility(View.VISIBLE);
                        mCurrentTransactionType = TransactionEnum.Transfer;
                        break;
                    case 3:
                        llAdjustment.setVisibility(View.VISIBLE);
                        mCurrentTransactionType = TransactionEnum.Adjustment;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ((ActivityMain)getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Todo: Init view EXPENSE
     */
    private void initViewExpense() {
        LogUtils.logEnterFunction(Tag, null);

        llExpense               = (LinearLayout) getView().findViewById(R.id.llExpense);

        etExpenseAmount         = (ClearableEditText) getView().findViewById(R.id.etExpenseAmount);
        etExpenseAmount.addTextChangedListener(new CurrencyTextWatcher(etExpenseAmount));
        tvExpenseCurrencyIcon   = (TextView) getView().findViewById(R.id.tvExpenseCurrencyIcon);

        llExpenseDescription    = (LinearLayout) getView().findViewById(R.id.llExpenseDescription);
        llExpenseDescription.setOnClickListener(this);
        tvExpenseDescription    = (TextView) getView().findViewById(R.id.tvExpenseDescription);

        llExpenseCategory       = (LinearLayout) getView().findViewById(R.id.llExpenseCategory);
        llExpenseCategory.setOnClickListener(this);
        tvExpenseCategory       = (TextView) getView().findViewById(R.id.tvExpenseCategory);

        llExpenseAccount        = (LinearLayout) getView().findViewById(R.id.llExpenseAccount);
        llExpenseAccount.setOnClickListener(this);
        tvExpenseAccount        = (TextView) getView().findViewById(R.id.tvExpenseAccount);
        tvExpenseAccount.setText(mFromAccount != null ? mFromAccount.getName() : "");

        llExpenseDate           = (LinearLayout) getView().findViewById(R.id.llExpenseDate);
        llExpenseDate.setOnClickListener(this);
        tvExpenseDate           = (TextView) getView().findViewById(R.id.tvExpenseDate);
        tvExpenseDate.setText(getDateString());

        llExpensePayee          = (LinearLayout) getView().findViewById(R.id.llExpensePayee);
        llExpensePayee.setOnClickListener(this);
        tvExpensePayee          = (TextView) getView().findViewById(R.id.tvExpensePayee);

        llExpenseEvent          = (LinearLayout) getView().findViewById(R.id.llExpenseEvent);
        llExpenseEvent.setOnClickListener(this);
        tvExpenseEvent          = (TextView) getView().findViewById(R.id.tvExpenseEvent);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private void initViewIncome() {
        LogUtils.logEnterFunction(Tag, null);

        llIncome                = (LinearLayout) getView().findViewById(R.id.llIncome);

        etIncomeAmount          = (ClearableEditText) getView().findViewById(R.id.etIncomeAmount);
        etIncomeAmount.addTextChangedListener(new CurrencyTextWatcher(etIncomeAmount));
        tvIncomeCurrencyIcon    = (TextView) getView().findViewById(R.id.tvIncomeCurrencyIcon);

        llIncomeCategory        = (LinearLayout) getView().findViewById(R.id.llIncomeCategory);
        llIncomeCategory.setOnClickListener(this);
        tvIncomeCategory        = (TextView) getView().findViewById(R.id.tvIncomeCategory);

        llIncomeDescription     = (LinearLayout) getView().findViewById(R.id.llIncomeDescription);
        llIncomeDescription.setOnClickListener(this);
        tvIncomeDescription     = (TextView) getView().findViewById(R.id.tvIncomeDescription);

        llIncomeToAccount = (LinearLayout) getView().findViewById(R.id.llToAccount);
        llIncomeToAccount.setOnClickListener(this);
        tvIncomeToAccount = (TextView) getView().findViewById(R.id.tvToAccount);
        tvIncomeToAccount.setText(mFromAccount != null ? mFromAccount.getName() : "");

        llIncomeDate            = (LinearLayout) getView().findViewById(R.id.llIncomeDate);
        llIncomeDate.setOnClickListener(this);
        tvIncomeDate            = (TextView) getView().findViewById(R.id.tvIncomeDate);
        tvIncomeDate.setText(getDateString());

        llIncomeEvent           = (LinearLayout) getView().findViewById(R.id.llIncomeEvent);
        llIncomeEvent.setOnClickListener(this);
        tvIncomeEvent           = (TextView) getView().findViewById(R.id.tvIncomeEvent);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private void initViewTransfer() {
        LogUtils.logEnterFunction(Tag, null);

        llTransfer                  = (LinearLayout) getView().findViewById(R.id.llTransfer);
        etTransferAmount            = (ClearableEditText) getView().findViewById(R.id.etTransferAmount);
        etTransferAmount.addTextChangedListener(new CurrencyTextWatcher(etTransferAmount));
        tvTransferCurrencyIcon      = (TextView) getView().findViewById(R.id.tvTransferCurrencyIcon);

        llTransferFromAccount       = (LinearLayout) getView().findViewById(R.id.llTransferFromAccount);
        llTransferFromAccount.setOnClickListener(this);
        tvTransferFromAccount       = (TextView) getView().findViewById(R.id.tvTransferFromAccount);

        llTransferToAccount         = (LinearLayout) getView().findViewById(R.id.llTransferToAccount);
        llTransferToAccount.setOnClickListener(this);
        tvTransferToAccount         = (TextView) getView().findViewById(R.id.tvTransferToAccount);

        llTransferDescription       = (LinearLayout) getView().findViewById(R.id.llTransferDescription);
        llTransferDescription.setOnClickListener(this);
        tvTransferDescription       = (TextView) getView().findViewById(R.id.tvTransferDescription);

        llTransferDate              = (LinearLayout) getView().findViewById(R.id.llTransferDate);
        llTransferDate.setOnClickListener(this);
        tvTransferDate              = (TextView) getView().findViewById(R.id.tvTransferDate);
        tvTransferDate.setText(getDateString());

        etTransferFee               = (ClearableEditText) getView().findViewById(R.id.etTransferFee);
        tvTransferFeeCurrencyIcon   = (TextView) getView().findViewById(R.id.tvTransferFeeCurrencyIcon);

        llTransferCategory          = (LinearLayout) getView().findViewById(R.id.llTransferCategory);
        llTransferCategory.setOnClickListener(this);
        tvTransferCategory          = (TextView) getView().findViewById(R.id.tvTransferCategory);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private void initViewAdjustment() {
        LogUtils.logEnterFunction(Tag, null);

        llAdjustment                = (LinearLayout) getView().findViewById(R.id.llAdjustment);

        llAdjustmentAccount         = (LinearLayout) getView().findViewById(R.id.llAdjustmentAccount);
        llAdjustmentAccount.setOnClickListener(this);
        tvAdjustmentAccount         = (TextView) getView().findViewById(R.id.tvAdjustmentAccount);

        etAdjustmentBalance         = (ClearableEditText) getView().findViewById(R.id.etAdjustmentBalance);
        etAdjustmentBalance.addTextChangedListener(new CurrencyTextWatcher(etAdjustmentBalance));

        tvAdjustmentCurrencyIcon    = (TextView) getView().findViewById(R.id.tvAdjustmentCurrencyIcon);
        tvAdjustmentSpent           = (TextView) getView().findViewById(R.id.tvAdjustmentSpent);

        llAdjustmentCategory        = (LinearLayout) getView().findViewById(R.id.llAdjustmentCategory);
        llAdjustmentCategory.setOnClickListener(this);
        tvAdjustmentCategory        = (TextView) getView().findViewById(R.id.tvAdjustmentCategory);

        llAdjustmentDescription     = (LinearLayout) getView().findViewById(R.id.llAdjustmentDescription);
        llAdjustmentDescription.setOnClickListener(this);
        tvAdjustmentDescription     = (TextView) getView().findViewById(R.id.tvAdjustmentDescription);

        llAdjustmentDate            = (LinearLayout) getView().findViewById(R.id.llAdjustmentDate);
        llAdjustmentDate.setOnClickListener(this);
        tvAdjustmentDate            = (TextView) getView().findViewById(R.id.tvAdjustmentDate);
        tvAdjustmentDate.setText(getDateString());

        llAdjustmentPayee           = (LinearLayout) getView().findViewById(R.id.llAdjustmentPayee);
        llAdjustmentPayee.setOnClickListener(this);
        tvAdjustmentPayee           = (TextView) getView().findViewById(R.id.tvAdjustmentPayee);

        llAdjustmentEvent           = (LinearLayout) getView().findViewById(R.id.llAdjustmentEvent);
        llAdjustmentEvent.setOnClickListener(this);
        tvAdjustmentEvent           = (TextView) getView().findViewById(R.id.tvAdjustmentEvent);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Private class for Spinner TransactionType
     */
    private class TransactionType {
        private String name;
        private String description;

        public TransactionType(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Spinner Transaction Type's adapter
     */
    private class TransactionTypeAdapter extends ArrayAdapter<TransactionType> {
        private class ViewHolder {
            TextView tvType;
        }
        private class DropdownViewHolder {
            TextView tvType;
            TextView tvDescription;
        }

        private List<TransactionType> mList;

        public TransactionTypeAdapter(Context context, List<TransactionType> items) {
            super(context, R.layout.spinner_transaction_type_dropdown_item, items);
            this.mList = items;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public TransactionType getItem(int position) {
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
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.spinner_transaction_type, parent, false);
                viewHolder.tvType           = (TextView) convertView.findViewById(R.id.tvType);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tvType.setText(mList.get(position).getName());

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            DropdownViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new DropdownViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.spinner_transaction_type_dropdown_item, parent, false);
                viewHolder.tvType           = (TextView) convertView.findViewById(R.id.tvType);
                viewHolder.tvDescription    = (TextView) convertView.findViewById(R.id.tvDescription);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (DropdownViewHolder) convertView.getTag();
            }

            viewHolder.tvType.setText(mList.get(position).getName());
            viewHolder.tvDescription.setText(mList.get(position).getDescription());

            return convertView;
        }
    }

    /**
     * Initial Balance EditText's TextWatcher
     */
    private class CurrencyTextWatcher implements TextWatcher {
        private String current = "";

        private EditText mEdittext;

        public CurrencyTextWatcher(EditText et) {
            mEdittext = et;
        }

        public synchronized void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LogUtils.logEnterFunction(Tag, null);

            if(!s.toString().equals(current)){
                if(s.toString().equals("")) {
                    return;
                }
                mEdittext.removeTextChangedListener(this);

                LogUtils.trace(Tag, "input: " + s.toString());
                String inputted = s.toString().replaceAll(",", "");

                LogUtils.trace(Tag, "input replace: " + inputted);
                String[] ar = inputted.split("\\.");

                StringBuilder formatted = new StringBuilder();
                if(ar[0].length() > 0) {
                    LogUtils.trace(Tag, "ar[0]: " + ar[0]);
                    for(int i = 0; i < ar[0].length(); i++) {
                        formatted.append(ar[0].charAt(i));
                        if(((ar[0].length() - (i+1)) % 3 == 0) && (i != ar[0].length()-1)) {
                            formatted.append(",");
                        }
                        LogUtils.trace(Tag, "formatted :" + formatted.toString());
                    }
                }

                if(s.toString().charAt(s.toString().length()-1) == '.') {
                    formatted.append(".");
                    LogUtils.trace(Tag, "formatted Add '.' :" + formatted.toString());
                } else if(ar.length == 2) {
                    LogUtils.trace(Tag, "ar[1]: " + ar[1]);
                    formatted.append(".");
                    for(int i = 0; i < ar[1].length(); i++) {
                        formatted.append(ar[1].charAt(i));
                        if(((ar[1].length() - (i+1)) % 3 == 0) && (i != ar[1].length()-1)) {
                            formatted.append(",");
                        }
                        LogUtils.trace(Tag, "formatted :" + formatted.toString());
                    }
                }

                current = formatted.toString();
                mEdittext.setText(formatted);
                mEdittext.setSelection(formatted.length());

                mEdittext.addTextChangedListener(this);
            }

            LogUtils.logLeaveFunction(Tag, null, null);
        }

    }

    private Date getDate(int year, int month, int day, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void startFragmentSelectCategory(TransactionEnum transactionType, int oldCategoryId) {
        FragmentNewTransactionSelectCategory nextFrag = new FragmentNewTransactionSelectCategory();
        Bundle bundleSelectCategory = new Bundle();
        bundleSelectCategory.putSerializable("TransactionType", transactionType);
        bundleSelectCategory.putInt("CategoryID", oldCategoryId);
        nextFrag.setArguments(bundleSelectCategory);
        FragmentUpdateTransaction.this.getFragmentManager().beginTransaction()
                .add(R.id.layout_new_transaction, nextFrag, "FragmentNewTransactionSelectCategory")
                .addToBackStack(null)
                .commit();
    }
    /**
     * Update Category, call from ActivityMain
     * @param categoryId
     */
    public void updateCategory(TransactionEnum type, int categoryId) {
        LogUtils.logEnterFunction(Tag, "TransactionType = " + type.name() + ",  categoryId = " + categoryId);

        mCategory = db.getCategory(categoryId);

        switch (type) {
            case Expense:
                tvExpenseCategory.setText(mCategory.getName());
                break;
            case Income:
                tvIncomeCategory.setText(mCategory.getName());
                break;
            case Transfer:
            case TransferFrom:
            case TransferTo:
                tvTransferCategory.setText(mCategory.getName());
                break;
            case Adjustment:
                tvAdjustmentCategory.setText(mCategory.getName());
                break;
            default:
                break;
        }

        if(mCategory != null) {
        } else {
            tvExpenseCategory.setText("");
        }

        LogUtils.logLeaveFunction(Tag, "TransactionType = " + type.name() + ", categoryId = " + categoryId, null);
    }

    private void startFragmentDescription(TransactionEnum transactionType, String oldDescription) {
        FragmentDescription fragmentDescription = new FragmentDescription();
        Bundle bundleExpenseDescription = new Bundle();
        bundleExpenseDescription.putSerializable("TransactionType", transactionType);
        bundleExpenseDescription.putString("Description", oldDescription);
        fragmentDescription.setArguments(bundleExpenseDescription);
        FragmentUpdateTransaction.this.getFragmentManager().beginTransaction()
                .add(R.id.layout_new_transaction, fragmentDescription, "FragmentDescription")
                .addToBackStack(null)
                .commit();
    }
    /**
     * Update description
     * @param description
     */
    public void updateDescription(TransactionEnum type, String description) {
        LogUtils.logEnterFunction(Tag, "TransactionType = " + type.name() + ", description = " + description);

        switch (type) {
            case Expense:
                tvExpenseDescription.setText(description);
                break;
            case Income:
                tvIncomeDescription.setText(description);
                break;
            case Transfer:
                tvTransferDescription.setText(description);
                break;
            case Adjustment:
                tvAdjustmentDescription.setText(description);
                break;
            default:
                break;
        }


        LogUtils.logLeaveFunction(Tag, "TransactionType = " + type.name() + ", description = " + description, null);
    }

    private void startFragmentSelectAccount(TransactionEnum transactionType, int oldAccountId) {
        LogUtils.logEnterFunction(Tag, "TransactionType = " + transactionType.name() + ", oldAccountId = " + oldAccountId);
        FragmentNewTransactionSelectAccount fragmentAccount = new FragmentNewTransactionSelectAccount();
        Bundle bundleAccount = new Bundle();
        bundleAccount.putSerializable("TransactionType", transactionType);
        bundleAccount.putInt("AccountID", oldAccountId);
        fragmentAccount.setArguments(bundleAccount);
        FragmentUpdateTransaction.this.getFragmentManager().beginTransaction()
                .add(R.id.layout_new_transaction, fragmentAccount, "FragmentNewTransactionSelectAccount")
                .addToBackStack(null)
                .commit();
        LogUtils.logLeaveFunction(Tag, "TransactionType = " + transactionType.name() + ", oldAccountId = " + oldAccountId, null);
    }
    /**
     * Update Account, call from ActivityMain
     * @param type
     * @param accountId
     */
    public void updateAccount(TransactionEnum type, int accountId) {
        LogUtils.logEnterFunction(Tag, "TransactionType = " + type.name() + ", accountId = " + accountId);

        switch (type) {
            case Expense:
                mFromAccount = db.getAccount(accountId);
                tvExpenseAccount.setText(mFromAccount.getName());
                break;
            case Income:
                mToAccount = db.getAccount(accountId);
                tvIncomeToAccount.setText(mToAccount.getName());
                break;
            case TransferFrom:
                mFromAccount = db.getAccount(accountId);
                tvTransferFromAccount.setText(mFromAccount.getName());
                break;
            case TransferTo:
                mToAccount = db.getAccount(accountId);
                tvTransferToAccount.setText(mToAccount.getName());
                break;
            case Adjustment:
                mFromAccount = db.getAccount(accountId);
                tvAdjustmentAccount.setText(mFromAccount.getName());
                break;
            default:
                break;
        }

        LogUtils.logLeaveFunction(Tag, "TransactionType = " + type.name() + ", accountId = " + accountId, null);
    }

    private void startFragmentPayee(TransactionEnum transactionType, String oldPayee) {
        FragmentPayee fragmentExpensePayee = new FragmentPayee();
        Bundle bundleExpensePayee = new Bundle();
        bundleExpensePayee.putSerializable("TransactionType", transactionType);
        bundleExpensePayee.putString("Payee", oldPayee);
        fragmentExpensePayee.setArguments(bundleExpensePayee);
        FragmentUpdateTransaction.this.getFragmentManager().beginTransaction()
                .add(R.id.layout_new_transaction, fragmentExpensePayee, "FragmentPayee")
                .addToBackStack(null)
                .commit();
    }
    /**
     * Update Payee
     * @param payee
     */
    public void updatePayee(TransactionEnum type, String payee) {
        LogUtils.logEnterFunction(Tag, "TransactionType = " + type.name() + ", payee = " + payee);

        switch (type) {
            case Expense:
                tvExpensePayee.setText(payee);
                break;
            case Adjustment:
                tvAdjustmentPayee.setText(payee);
                break;
            case Income:
            case Transfer:
            case TransferFrom:
            case TransferTo:
            default:
                break;
        }


        LogUtils.logLeaveFunction(Tag, "TransactionType = " + type.name() + ", payee = " + payee, null);
    }

    private void startFragmentEvent(TransactionEnum transactionType, String oldEvent) {
        FragmentEvent fragmentExpenseEvent = new FragmentEvent();
        Bundle bundleExpenseEvent = new Bundle();
        bundleExpenseEvent.putSerializable("TransactionType", transactionType);
        bundleExpenseEvent.putString("Event", oldEvent);
        fragmentExpenseEvent.setArguments(bundleExpenseEvent);
        FragmentUpdateTransaction.this.getFragmentManager().beginTransaction()
                .add(R.id.layout_new_transaction, fragmentExpenseEvent, "FragmentEvent")
                .addToBackStack(null)
                .commit();
    }
    /**
     * Update Payee
     * @param event
     */
    public void updateEvent(TransactionEnum type, String event) {
        LogUtils.logEnterFunction(Tag, "TransactionType = " + type.name() + ", event = " + event);

        switch (type) {
            case Expense:
                tvExpenseEvent.setText(event);
                break;
            case Income:
                tvIncomeEvent.setText(event);
                break;
            case Adjustment:
                tvAdjustmentEvent.setText(event);
                break;
            case Transfer:
            case TransferFrom:
            case TransferTo:
            default:
                break;
        }

        LogUtils.logLeaveFunction(Tag, "TransactionType = " + type.name() + ", event = " + event, null);
    }

    private String getDateString() {
        Calendar car = Calendar.getInstance();
        String date = "";
        if(car.get(Calendar.DAY_OF_YEAR) == mDay) {
            date = getResources().getString(R.string.content_today);
        } else if((car.get(Calendar.DAY_OF_YEAR) - 1) == mDay) {
            date = getResources().getString(R.string.content_yesterday);
        } else if((car.get(Calendar.DAY_OF_YEAR) - 2) == mDay
                && getResources().getConfiguration().locale.equals(Locale.forLanguageTag("vi_VN"))) {
            date = getResources().getString(R.string.content_before_yesterday);
        } else {
            date = String.format("%02d-%02d-%02d %02d:%02d", mDay, mMonth + 1, mYear, mHour, mMinute);
        }

        return date;
    }
}
