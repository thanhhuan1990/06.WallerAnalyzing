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
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.model.Transaction.TransactionEnum;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentTransactionUpdate extends Fragment implements  View.OnClickListener {
    private static final String Tag = "FragmentTransactionUpdate";

    private Configurations      mConfigs;
    private DatabaseHelper      mDbHelper;

    private Transaction         mTransaction;
    private Category            mCategory;
    private Account             mFromAccount;
    private Account             mToAccount;
    private Spinner             spTransactionType;
    private TransactionEnum     mCurrentTransactionType  = TransactionEnum.Expense;

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
    private LinearLayout        llDelete;

    private int mYear = 0, mMonth = 0, mDay = 0, mHour = 0, mMinute = 0;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();

        mConfigs    = new Configurations(getActivity());
        mDbHelper   = new DatabaseHelper(getActivity());

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);

        Bundle bundle       = this.getArguments();
        mTransaction            = (Transaction)bundle.get("Transaction");
        mCurrentTransactionType = TransactionEnum.getTransactionEnum(mTransaction.getTransactionType());

        LogUtils.trace(Tag, "mTransaction = " + mTransaction.toString());

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreateOptionsMenu(menu, inflater);

        initActionBar();

        if(mYear == 0 && mTransaction != null) {
            mCategory       = mDbHelper.getCategory(mTransaction.getCategoryId());
            mFromAccount    = mTransaction.getFromAccountId() != 0 ?
                                mDbHelper.getAccount(mTransaction.getFromAccountId()) : mDbHelper.getAccount(mTransaction.getToAccountId());
            mToAccount      = mTransaction.getToAccountId() != 0 ?
                                mDbHelper.getAccount(mTransaction.getToAccountId()) : mDbHelper.getAccount(mTransaction.getFromAccountId());

            final Calendar c = mTransaction.getTime();
            mYear   = c.get(Calendar.YEAR);
            mMonth  = c.get(Calendar.MONTH);
            mDay    = c.get(Calendar.DAY_OF_MONTH);
            mHour   = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            llSave      = (LinearLayout) getView().findViewById(R.id.llSave);
            llSave.setOnClickListener(this);
            llDelete    = (LinearLayout) getView().findViewById(R.id.llDelete);
            llDelete.setOnClickListener(this);

            initViewExpense();
            initViewIncome();
            initViewTransfer();
            initViewAdjustment();
        }

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

        String myTag = getTag();
        ((ActivityMain)getActivity()).setFragmentTransactionUpdate(myTag);

        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_transaction_update, container, false);
    }

    @Override
    public void onResume() {
        LogUtils.logEnterFunction(Tag, null);
        super.onResume();

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
            case R.id.llSave: {
                switch (mCurrentTransactionType) {
                    case Expense: {

                        if (etExpenseAmount.getText().toString().equals("")) {
                            etExpenseAmount.setError(getResources().getString(R.string.Input_Error_Amount_Empty));
                            return;
                        }

                        if (mFromAccount == null) {
                            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Account_Empty));
                            return;
                        }

                        Double expenseAmount        = Double.parseDouble(etExpenseAmount.getText().toString().replaceAll(",", ""));
                        int expenseCategoryId       = mCategory != null ? mCategory.getId() : 0;
                        String expenseDescription   = tvExpenseDescription.getText().toString();
                        int expenseAccountId        = mFromAccount.getId();
                        Date expenseDate            = getDate(mYear, mMonth, mDay, mHour, mMinute);
                        Calendar cal                = Calendar.getInstance();
                        cal.setTime(expenseDate);
                        String expensePayee         = tvExpensePayee.getText().toString();
                        String expenseEvent         = tvExpenseEvent.getText().toString();

                        Transaction transaction     = new Transaction(mTransaction.getId(),
                                                                        TransactionEnum.Expense.getValue(),
                                                                        expenseAmount,
                                                                        expenseCategoryId,
                                                                        expenseDescription,
                                                                        expenseAccountId,
                                                                        0,
                                                                        cal,
                                                                        0.0,
                                                                        expensePayee,
                                                                        expenseEvent);
                        int row = mDbHelper.updateTransaction(transaction);
                        if (row == 1) {

                            cleanup();

                            FragmentTransactions fragmentTransactions = (FragmentTransactions) ((ActivityMain) getActivity()).getFragment(ActivityMain.TAB_POSITION_TRANSACTIONS);
                            fragmentTransactions.updateListTransaction();

                            // Return to FragmentTransactions
                            getFragmentManager().popBackStackImmediate();

                        }

                        break;
                    }
                    case Income: {
                        if (etIncomeAmount.getText().toString().equals("")) {
                            etIncomeAmount.setError(getResources().getString(R.string.Input_Error_Amount_Empty));
                            return;
                        }

                        if (mToAccount == null) {
                            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Account_Empty));
                            return;
                        }

                        Double incomeAmount             = Double.parseDouble(etIncomeAmount.getText().toString().replaceAll(",", ""));
                        int incomeCategoryId            = mCategory != null ? mCategory.getId() : 0;
                        String incomeDescription        = tvIncomeDescription.getText().toString();
                        int incomeAccountId             = mToAccount.getId();
                        Date expenseDate                = getDate(mYear, mMonth, mDay, mHour, mMinute);
                        Calendar cal                    = Calendar.getInstance();
                        cal.setTime(expenseDate);
                        String incomeEvent              = tvIncomeEvent.getText().toString();

                        Transaction transaction = new Transaction(mTransaction.getId(),
                                                                    TransactionEnum.Income.getValue(),
                                                                    incomeAmount,
                                                                    incomeCategoryId,
                                                                    incomeDescription,
                                                                    0,
                                                                    incomeAccountId,
                                                                    cal,
                                                                    0.0,
                                                                    "",
                                                                    incomeEvent);
                        int row = mDbHelper.updateTransaction(transaction);
                        if (row == 1) {

                            cleanup();

                            FragmentTransactions fragmentTransactions = (FragmentTransactions) ((ActivityMain) getActivity()).getFragment(ActivityMain.TAB_POSITION_TRANSACTIONS);
                            fragmentTransactions.updateListTransaction();

                            // Return to FragmentTransactions
                            getFragmentManager().popBackStackImmediate();
                        }
                        break;
                    }
                    case Transfer: {
                        if (etTransferAmount.getText().toString().equals("")) {
                            etTransferAmount.setError(getResources().getString(R.string.Input_Error_Amount_Empty));
                            return;
                        }

                        if (mFromAccount == null) {
                            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Account_Empty));
                            return;
                        }

                        if (mToAccount == null) {
                            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_ToAccount_Empty));
                            return;
                        }

                        Double transferAmount       = Double.parseDouble(etTransferAmount.getText().toString().replaceAll(",", ""));

                        int fromAccountId           = mFromAccount.getId();
                        int toAccountId             = mToAccount.getId();

                        String transferDescription  = tvTransferDescription.getText().toString();
                        Date transferDate           = getDate(mYear, mMonth, mDay, mHour, mMinute);
                        Calendar cal                = Calendar.getInstance();
                        cal.setTime(transferDate);

                        Double transferFee          = !etTransferFee.getText().toString().equals("") ?
                                                            Double.parseDouble(etTransferFee.getText().toString().replaceAll(",", ""))
                                                            : 0.0;

                        int transferCategoryId      = mCategory != null ? mCategory.getId() : 0;

                        Transaction transaction = new Transaction(mTransaction.getId(),
                                                                    TransactionEnum.Transfer.getValue(),
                                                                    transferAmount,
                                                                    transferCategoryId,
                                                                    transferDescription,
                                                                    fromAccountId,
                                                                    toAccountId,
                                                                    cal,
                                                                    transferFee,
                                                                    "",
                                                                    "");
                        int row = mDbHelper.updateTransaction(transaction);
                        if (row == 1) {

                            cleanup();

                            FragmentTransactions fragmentTransactions = (FragmentTransactions) ((ActivityMain) getActivity()).getFragment(ActivityMain.TAB_POSITION_TRANSACTIONS);
                            fragmentTransactions.updateListTransaction();

                            // Return to FragmentTransactions
                            getFragmentManager().popBackStackImmediate();
                        }

                        break;
                    }
                    case Adjustment: {
                        if (mFromAccount == null) {
                            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Account_Empty));
                            return;
                        }

                        int fromAccountId               = mFromAccount.getId();

                        Double balance                  = !etAdjustmentBalance.getText().toString().equals("") ?
                                                                Double.parseDouble(etAdjustmentBalance.getText().toString().replaceAll(",", ""))
                                                                : 0;

                        Double remain                   = mDbHelper.getAccountRemain(fromAccountId) + mTransaction.getAmount();

                        int adjustmentCategoryId        = mCategory != null ? mCategory.getId() : 0;
                        String adjustmentDescription    = tvAdjustmentDescription.getText().toString();
                        Date adjustmentDate             = getDate(mYear, mMonth, mDay, mHour, mMinute);
                        Calendar cal                    = Calendar.getInstance();
                        cal.setTime(adjustmentDate);
                        String adjustmentPayee          = tvAdjustmentPayee.getText().toString();
                        String adjustmentEvent          = tvAdjustmentEvent.getText().toString();

                        Transaction transaction = new Transaction(mTransaction.getId(),
                                                                        TransactionEnum.Adjustment.getValue(),
                                                                        remain - balance,
                                                                        adjustmentCategoryId,
                                                                        adjustmentDescription,
                                                                        fromAccountId,
                                                                        0,
                                                                        cal,
                                                                        0.0,
                                                                        adjustmentPayee,
                                                                        adjustmentEvent);
                        int row = mDbHelper.updateTransaction(transaction);
                        if (row == 1) {

                            cleanup();

                            FragmentTransactions fragmentTransactions = (FragmentTransactions) ((ActivityMain) getActivity()).getFragment(ActivityMain.TAB_POSITION_TRANSACTIONS);
                            fragmentTransactions.updateListTransaction();

                            // Return to FragmentTransactions
                            getFragmentManager().popBackStackImmediate();
                        }

                        break;
                    }
                    default:
                        break;
                }
                break;
            }
            case R.id.llDelete: {
                mDbHelper.deleteTransaction(mTransaction.getId());

                cleanup();

                FragmentTransactions fragmentTransactions = (FragmentTransactions) ((ActivityMain) getActivity()).getFragment(ActivityMain.TAB_POSITION_TRANSACTIONS);
                fragmentTransactions.updateListTransaction();

                // Return to FragmentTransactions
                getFragmentManager().popBackStackImmediate();
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

                        if(mTransaction.getTransactionType() == mCurrentTransactionType.getValue()) {
                            mCategory = mDbHelper.getCategory(mTransaction.getCategoryId());
                            tvExpenseCategory.setText(mCategory != null ? mCategory.getName() : "");
                            tvExpenseAccount.setText(mFromAccount.getName());
                        }
                        break;
                    case 1:
                        llIncome.setVisibility(View.VISIBLE);
                        mCurrentTransactionType = TransactionEnum.Income;

                        if(mTransaction.getTransactionType() == mCurrentTransactionType.getValue()) {
                            mCategory = mDbHelper.getCategory(mTransaction.getCategoryId());
                            tvIncomeCategory.setText(mCategory != null ? mCategory.getName() : "");
                            tvIncomeToAccount.setText(mToAccount.getName());
                        }
                        break;
                    case 2:
                        llTransfer.setVisibility(View.VISIBLE);
                        mCurrentTransactionType = TransactionEnum.Transfer;

                        if(mTransaction.getTransactionType() == mCurrentTransactionType.getValue()) {
                            mCategory = mDbHelper.getCategory(mTransaction.getCategoryId());
                            tvTransferCategory.setText(mCategory != null ? mCategory.getName() : "");
                            tvTransferFromAccount.setText(mFromAccount.getName());
                            tvTransferToAccount.setText(mToAccount.getName());
                        }
                        break;
                    case 3:
                        llAdjustment.setVisibility(View.VISIBLE);
                        mCurrentTransactionType = TransactionEnum.Adjustment;

                        if(mTransaction.getTransactionType() == mCurrentTransactionType.getValue()) {
                            mCategory = mDbHelper.getCategory(mTransaction.getCategoryId());
                            tvAdjustmentCategory.setText(mCategory != null ? mCategory.getName() : "");
                            tvAdjustmentAccount.setText(mFromAccount.getName());
                        }
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
        etExpenseAmount.setText(Currency.formatCurrencyDouble(Currency.getCurrencyById(mFromAccount.getCurrencyId()),
                mTransaction.getAmount()));
        etExpenseAmount.addTextChangedListener(new CurrencyTextWatcher(etExpenseAmount));
        tvExpenseCurrencyIcon   = (TextView) getView().findViewById(R.id.tvExpenseCurrencyIcon);
        tvExpenseCurrencyIcon.setText(Currency.getCurrencyIcon(Currency.getCurrencyById(mFromAccount.getCurrencyId())));

        llExpenseDescription    = (LinearLayout) getView().findViewById(R.id.llExpenseDescription);
        llExpenseDescription.setOnClickListener(this);
        tvExpenseDescription    = (TextView) getView().findViewById(R.id.tvExpenseDescription);
        tvExpenseDescription.setText(mTransaction.getDescription());

        llExpenseCategory       = (LinearLayout) getView().findViewById(R.id.llExpenseCategory);
        llExpenseCategory.setOnClickListener(this);
        tvExpenseCategory       = (TextView) getView().findViewById(R.id.tvExpenseCategory);
        tvExpenseCategory.setText(mCategory != null ? mCategory.getName() : "");

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
        tvExpensePayee.setText(mTransaction.getPayee());

        llExpenseEvent          = (LinearLayout) getView().findViewById(R.id.llExpenseEvent);
        llExpenseEvent.setOnClickListener(this);
        tvExpenseEvent          = (TextView) getView().findViewById(R.id.tvExpenseEvent);
        tvExpenseEvent.setText(mTransaction.getEvent());

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private void initViewIncome() {
        LogUtils.logEnterFunction(Tag, null);

        llIncome                = (LinearLayout) getView().findViewById(R.id.llIncome);

        etIncomeAmount          = (ClearableEditText) getView().findViewById(R.id.etIncomeAmount);
        etIncomeAmount.setText(Currency.formatCurrencyDouble(Currency.getCurrencyById(mToAccount.getCurrencyId())
                                                              , mTransaction.getAmount()));
        etIncomeAmount.addTextChangedListener(new CurrencyTextWatcher(etIncomeAmount));
        tvIncomeCurrencyIcon    = (TextView) getView().findViewById(R.id.tvIncomeCurrencyIcon);
        tvIncomeCurrencyIcon.setText(Currency.getCurrencyIcon(Currency.getCurrencyById(mToAccount.getCurrencyId())));

        llIncomeCategory        = (LinearLayout) getView().findViewById(R.id.llIncomeCategory);
        llIncomeCategory.setOnClickListener(this);
        tvIncomeCategory        = (TextView) getView().findViewById(R.id.tvIncomeCategory);
        tvIncomeCategory.setText(mCategory != null ? mCategory.getName() : "");

        llIncomeDescription     = (LinearLayout) getView().findViewById(R.id.llIncomeDescription);
        llIncomeDescription.setOnClickListener(this);
        tvIncomeDescription     = (TextView) getView().findViewById(R.id.tvIncomeDescription);
        tvIncomeDescription.setText(mTransaction.getDescription());

        llIncomeToAccount = (LinearLayout) getView().findViewById(R.id.llToAccount);
        llIncomeToAccount.setOnClickListener(this);
        tvIncomeToAccount = (TextView) getView().findViewById(R.id.tvToAccount);
        tvIncomeToAccount.setText(mToAccount != null ? mToAccount.getName() : "");

        llIncomeDate            = (LinearLayout) getView().findViewById(R.id.llIncomeDate);
        llIncomeDate.setOnClickListener(this);
        tvIncomeDate            = (TextView) getView().findViewById(R.id.tvIncomeDate);
        tvIncomeDate.setText(getDateString());

        llIncomeEvent           = (LinearLayout) getView().findViewById(R.id.llIncomeEvent);
        llIncomeEvent.setOnClickListener(this);
        tvIncomeEvent           = (TextView) getView().findViewById(R.id.tvIncomeEvent);
        tvIncomeEvent.setText(mTransaction.getEvent());

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private void initViewTransfer() {
        LogUtils.logEnterFunction(Tag, null);

        llTransfer                  = (LinearLayout) getView().findViewById(R.id.llTransfer);
        etTransferAmount            = (ClearableEditText) getView().findViewById(R.id.etTransferAmount);
        etTransferAmount.setText(Currency.formatCurrencyDouble(Currency.getCurrencyById(mFromAccount.getCurrencyId()),
                                                                mTransaction.getAmount()));
        etTransferAmount.addTextChangedListener(new CurrencyTextWatcher(etTransferAmount));
        tvTransferCurrencyIcon      = (TextView) getView().findViewById(R.id.tvTransferCurrencyIcon);
        tvTransferCurrencyIcon.setText(Currency.getCurrencyIcon(Currency.getCurrencyById(mFromAccount.getCurrencyId())));

        llTransferFromAccount       = (LinearLayout) getView().findViewById(R.id.llTransferFromAccount);
        llTransferFromAccount.setOnClickListener(this);
        tvTransferFromAccount       = (TextView) getView().findViewById(R.id.tvTransferFromAccount);
        tvTransferFromAccount.setText(mFromAccount != null ? mFromAccount.getName() : "");

        llTransferToAccount         = (LinearLayout) getView().findViewById(R.id.llTransferToAccount);
        llTransferToAccount.setOnClickListener(this);
        tvTransferToAccount         = (TextView) getView().findViewById(R.id.tvTransferToAccount);
        tvTransferToAccount.setText(mToAccount != null ? mToAccount.getName() : "");

        llTransferDescription       = (LinearLayout) getView().findViewById(R.id.llTransferDescription);
        llTransferDescription.setOnClickListener(this);
        tvTransferDescription       = (TextView) getView().findViewById(R.id.tvTransferDescription);
        tvTransferDescription.setText(mTransaction.getDescription());

        llTransferDate              = (LinearLayout) getView().findViewById(R.id.llTransferDate);
        llTransferDate.setOnClickListener(this);
        tvTransferDate              = (TextView) getView().findViewById(R.id.tvTransferDate);
        tvTransferDate.setText(getDateString());

        etTransferFee               = (ClearableEditText) getView().findViewById(R.id.etTransferFee);
        etTransferFee.addTextChangedListener(new CurrencyTextWatcher(etTransferFee));
        tvTransferFeeCurrencyIcon   = (TextView) getView().findViewById(R.id.tvTransferFeeCurrencyIcon);
        tvTransferFeeCurrencyIcon.setText(Currency.getCurrencyIcon(Currency.getCurrencyById(mFromAccount.getCurrencyId())));

        llTransferCategory          = (LinearLayout) getView().findViewById(R.id.llTransferCategory);
        llTransferCategory.setOnClickListener(this);
        tvTransferCategory          = (TextView) getView().findViewById(R.id.tvTransferCategory);
        tvTransferCategory.setText(mCategory != null ? mCategory.getName() : "");

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private void initViewAdjustment() {
        LogUtils.logEnterFunction(Tag, null);

        llAdjustment                = (LinearLayout) getView().findViewById(R.id.llAdjustment);

        llAdjustmentAccount         = (LinearLayout) getView().findViewById(R.id.llAdjustmentAccount);
        llAdjustmentAccount.setOnClickListener(this);
        tvAdjustmentAccount         = (TextView) getView().findViewById(R.id.tvAdjustmentAccount);
        tvAdjustmentAccount.setText(mFromAccount != null ? mFromAccount.getName() : "");

        etAdjustmentBalance         = (ClearableEditText) getView().findViewById(R.id.etAdjustmentBalance);
        etAdjustmentBalance.addTextChangedListener(new CurrencyTextWatcher(etAdjustmentBalance));
        Double remain               = mDbHelper.getAccountRemain(mFromAccount.getId());
        etAdjustmentBalance.setText(Currency.formatCurrencyDouble(Currency.getCurrencyById(mFromAccount.getCurrencyId()), remain));

        tvAdjustmentCurrencyIcon    = (TextView) getView().findViewById(R.id.tvAdjustmentCurrencyIcon);
        tvAdjustmentSpent           = (TextView) getView().findViewById(R.id.tvAdjustmentSpent);

        if(mTransaction.getAmount() > 0) {
            tvAdjustmentSpent.setText(getResources().getString(R.string.content_expensed)
                                            +  Currency.formatCurrency(getContext(),
                                                                            Currency.getCurrencyById(mFromAccount.getCurrencyId()),
                                                                            mTransaction.getAmount()));
        } else {
            tvAdjustmentSpent.setText(getResources().getString(R.string.content_adjustment_income)
                                            +  Currency.formatCurrency(getContext(),
                                                                            Currency.getCurrencyById(mFromAccount.getCurrencyId()),
                                                                            mTransaction.getAmount() * -1));
        }

        llAdjustmentCategory        = (LinearLayout) getView().findViewById(R.id.llAdjustmentCategory);
        llAdjustmentCategory.setOnClickListener(this);
        tvAdjustmentCategory        = (TextView) getView().findViewById(R.id.tvAdjustmentCategory);
        tvAdjustmentCategory.setText(mCategory != null ? mCategory.getName() : "");

        llAdjustmentDescription     = (LinearLayout) getView().findViewById(R.id.llAdjustmentDescription);
        llAdjustmentDescription.setOnClickListener(this);
        tvAdjustmentDescription     = (TextView) getView().findViewById(R.id.tvAdjustmentDescription);
        tvAdjustmentDescription.setText(mTransaction.getDescription());

        llAdjustmentDate            = (LinearLayout) getView().findViewById(R.id.llAdjustmentDate);
        llAdjustmentDate.setOnClickListener(this);
        tvAdjustmentDate            = (TextView) getView().findViewById(R.id.tvAdjustmentDate);
        tvAdjustmentDate.setText(getDateString());

        llAdjustmentPayee           = (LinearLayout) getView().findViewById(R.id.llAdjustmentPayee);
        llAdjustmentPayee.setOnClickListener(this);
        tvAdjustmentPayee           = (TextView) getView().findViewById(R.id.tvAdjustmentPayee);
        tvAdjustmentPayee.setText(mTransaction.getPayee());

        llAdjustmentEvent           = (LinearLayout) getView().findViewById(R.id.llAdjustmentEvent);
        llAdjustmentEvent.setOnClickListener(this);
        tvAdjustmentEvent           = (TextView) getView().findViewById(R.id.tvAdjustmentEvent);
        tvAdjustmentEvent.setText(mTransaction.getEvent());

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
                mEdittext.removeTextChangedListener(this);

                LogUtils.trace(Tag, "input: " + s.toString());
                String inputted = s.toString().replaceAll(",", "").replaceAll(" ", "");
                if(inputted.equals("")) {
                    inputted = "0";
                }
                String formatted = Currency.formatCurrencyDouble(mFromAccount != null ?
                                                                        Currency.getCurrencyById(mFromAccount.getCurrencyId())
                                                                        : Currency.getCurrencyById(mConfigs.getInt(Configurations.Key.Currency)),
                                                                Double.parseDouble(inputted));

                current = formatted;
                mEdittext.setText(formatted);
                mEdittext.setSelection(formatted.length());

                if(mCurrentTransactionType == TransactionEnum.Adjustment) {
                    Double balance = !mEdittext.getText().toString().equals("") ?
                                        Double.parseDouble(mEdittext.getText().toString().replaceAll(",", ""))
                                        : 0;
                    Double remain   = mDbHelper.getAccountRemain(mFromAccount.getId());

                    if(remain > balance) {
                        tvAdjustmentSpent.setText(String.format(getResources().getString(R.string.content_expensed),
                                                                    Currency.formatCurrency(getContext(),
                                                                                                Currency.getCurrencyById(mFromAccount.getCurrencyId()),
                                                                                                remain - balance)));
                        ((TextView) getView().findViewById(R.id.tvTitleAdjustmentCategory)).setText(getResources().getString(R.string.transaction_category_expense));
                    } else {
                        tvAdjustmentSpent.setText(String.format(getResources().getString(R.string.content_adjustment_income),
                                                                    Currency.formatCurrency(getContext(),
                                                                                                Currency.getCurrencyById(mFromAccount.getCurrencyId()),
                                                                                                balance - remain)));
                        ((TextView) getView().findViewById(R.id.tvTitleAdjustmentCategory)).setText(getResources().getString(R.string.transaction_category_income));
                    }
                }

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
        FragmentCategorySelect nextFragment = new FragmentCategorySelect();
        Bundle bundle = new Bundle();
        bundle.putString("Tag", ((ActivityMain) getActivity()).getFragmentTransactionUpdate());
        bundle.putInt("CategoryID", oldCategoryId);
        bundle.putSerializable("TransactionType", transactionType);
        nextFragment.setArguments(bundle);
        FragmentTransactionUpdate.this.getFragmentManager().beginTransaction()
                .add(R.id.ll_transactions, nextFragment, "FragmentCategorySelect")
                .addToBackStack(null)
                .commit();
    }
    /**
     * Update Category, call from ActivityMain
     * @param categoryId
     */
    public void updateCategory(TransactionEnum type, int categoryId) {
        LogUtils.logEnterFunction(Tag, "TransactionType = " + type.name() + ",  categoryId = " + categoryId);

        mCategory = mDbHelper.getCategory(categoryId);

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
        FragmentDescription nextFragment = new FragmentDescription();
        Bundle bundle = new Bundle();
        bundle.putString("Tag", ((ActivityMain) getActivity()).getFragmentTransactionUpdate());
        bundle.putSerializable("TransactionType", transactionType);
        bundle.putString("Description", oldDescription);
        nextFragment.setArguments(bundle);
        FragmentTransactionUpdate.this.getFragmentManager().beginTransaction()
                .add(R.id.ll_transactions, nextFragment, "FragmentDescription")
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
        FragmentAccountsSelect nextFragment = new FragmentAccountsSelect();
        Bundle bundle = new Bundle();
        bundle.putString("Tag", ((ActivityMain) getActivity()).getFragmentTransactionUpdate());
        bundle.putInt("AccountID", oldAccountId);
        bundle.putSerializable("TransactionType", transactionType);
        nextFragment.setArguments(bundle);
        FragmentTransactionUpdate.this.getFragmentManager().beginTransaction()
                .add(R.id.ll_transactions, nextFragment, "FragmentAccountsSelect")
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
                mFromAccount = mDbHelper.getAccount(accountId);
                tvExpenseAccount.setText(mFromAccount.getName());
                tvExpenseCurrencyIcon.setText(getResources().getString(Currency.getCurrencyIcon(Currency.getCurrencyById(mFromAccount.getCurrencyId()))));
                break;
            case Income:
                mToAccount = mDbHelper.getAccount(accountId);
                tvIncomeToAccount.setText(mToAccount.getName());
                tvIncomeCurrencyIcon.setText(getResources().getString(Currency.getCurrencyIcon(Currency.getCurrencyById(mToAccount.getCurrencyId()))));
                break;
            case TransferFrom:
                mFromAccount = mDbHelper.getAccount(accountId);
                tvTransferFromAccount.setText(mFromAccount.getName());
                tvTransferCurrencyIcon.setText(getResources().getString(Currency.getCurrencyIcon(Currency.getCurrencyById(mFromAccount.getCurrencyId()))));
                tvTransferFeeCurrencyIcon.setText(getResources().getString(Currency.getCurrencyIcon(Currency.getCurrencyById(mFromAccount.getCurrencyId()))));
                break;
            case TransferTo:
                mToAccount = mDbHelper.getAccount(accountId);
                tvTransferToAccount.setText(mToAccount.getName());
                break;
            case Adjustment:
                mFromAccount = mDbHelper.getAccount(accountId);
                tvAdjustmentAccount.setText(mFromAccount.getName());

                Double balance = !etAdjustmentBalance.getText().toString().equals("") ?
                                                    Double.parseDouble(etAdjustmentBalance.getText().toString().replaceAll(",", ""))
                                                    : 0;
                Double remain   = mDbHelper.getAccountRemain(accountId);
                tvAdjustmentSpent.setText(getResources().getString(R.string.content_expensed) + " " +
                                            Currency.formatCurrency(getContext(),
                                                                    Currency.getCurrencyById(mFromAccount.getCurrencyId()),
                                                                    (remain - balance)));

                tvAdjustmentCurrencyIcon.setText(getResources().getString(Currency.getCurrencyIcon(Currency.getCurrencyById(mFromAccount.getCurrencyId()))));

                break;
            default:
                break;
        }

        LogUtils.logLeaveFunction(Tag, "TransactionType = " + type.name() + ", accountId = " + accountId, null);
    }

    private void startFragmentPayee(TransactionEnum transactionType, String oldPayee) {
        FragmentPayee nextFragment = new FragmentPayee();
        Bundle bundle = new Bundle();
        bundle.putString("Tag", ((ActivityMain) getActivity()).getFragmentTransactionUpdate());
        bundle.putSerializable("TransactionType", transactionType);
        bundle.putString("Payee", oldPayee);
        nextFragment.setArguments(bundle);
        FragmentTransactionUpdate.this.getFragmentManager().beginTransaction()
                .add(R.id.ll_transactions, nextFragment, "FragmentPayee")
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
        FragmentEvent nextFragment = new FragmentEvent();
        Bundle bundle = new Bundle();
        bundle.putString("Tag", ((ActivityMain) getActivity()).getFragmentTransactionUpdate());
        bundle.putSerializable("TransactionType", transactionType);
        bundle.putString("Event", oldEvent);
        nextFragment.setArguments(bundle);
        FragmentTransactionUpdate.this.getFragmentManager().beginTransaction()
                .add(R.id.ll_transactions, nextFragment, "FragmentEvent")
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
            date = getResources().getString(R.string.content_today) + String.format(" %02d:%02d", mHour, mMinute);
        } else if((car.get(Calendar.DAY_OF_YEAR) - 1) == mDay) {
            date = getResources().getString(R.string.content_yesterday) + String.format(" %02d:%02d", mHour, mMinute);
        } else if((car.get(Calendar.DAY_OF_YEAR) - 2) == mDay
                && getResources().getConfiguration().locale.equals(Locale.forLanguageTag("vi_VN"))) {
            date = getResources().getString(R.string.content_before_yesterday) + String.format(" %02d:%02d", mHour, mMinute);
        } else {
            date = String.format("%02d-%02d-%02d %02d:%02d", mDay, mMonth + 1, mYear, mHour, mMinute);
        }

        return date;
    }

    /**
     * Cleanup Old datas
     */
    private void cleanup() {
        mCategory                   = null;
        mFromAccount                = null;
        mToAccount                  = null;
        mCurrentTransactionType     = TransactionEnum.Expense;
        mYear                       = 0;
        mMonth                      = 0;
        mDay                        = 0;
        mHour                       = 0;
        mMinute                     = 0;

        /* Reset value */
        etExpenseAmount.setText("");
        tvExpenseCategory.setText("");
        tvExpenseDescription.setText("");
        tvExpenseAccount.setText("");
        tvExpensePayee.setText("");
        tvExpenseEvent.setText("");

        etIncomeAmount.setText("");
        tvIncomeCategory.setText("");
        tvIncomeDescription.setText("");
        tvIncomeToAccount.setText("");
        tvIncomeEvent.setText("");

        etTransferAmount.setText("");
        tvTransferFromAccount.setText("");
        tvTransferToAccount.setText("");
        tvTransferDescription.setText("");
        etTransferFee.setText("");
        tvTransferCategory.setText("");

        etAdjustmentBalance.setText("");
        tvAdjustmentAccount.setText("");
        tvAdjustmentCategory.setText("");
        tvAdjustmentDescription.setText("");
        tvAdjustmentPayee.setText("");
        tvAdjustmentEvent.setText("");
    }
}
