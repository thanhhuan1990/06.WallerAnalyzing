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
    private static final String Tag = "TransactionUpdate";

    private Configurations      mConfigs;
    private DatabaseHelper      mDbHelper;

    private Transaction         mTransaction;
    private Category            mCategory;
    private Account             mFromAccount;
    private Account             mToAccount;
    private Spinner             spTransactionType;
    private TransactionEnum     mCurrentTransactionType  = TransactionEnum.Expense;
    private int                 mContainerViewId;

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

    private Calendar            mCal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);

        Bundle bundle           = this.getArguments();
        mTransaction            = (Transaction)bundle.get("Transaction");
        mCurrentTransactionType = TransactionEnum.getTransactionEnum(mTransaction.getTransactionType());
        mContainerViewId        = bundle.getInt("ContainerViewId");

        LogUtils.trace(Tag, "mTransaction = " + mTransaction.toString());

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreateOptionsMenu(menu, inflater);

        initActionBar();

        if(mCal == null && mTransaction != null) {
            mCategory       = mDbHelper.getCategory(mTransaction.getCategoryId());
            mFromAccount    = mTransaction.getFromAccountId() != 0 ? mDbHelper.getAccount(mTransaction.getFromAccountId()) : mDbHelper.getAccount(mTransaction.getToAccountId());
            mToAccount      = mTransaction.getToAccountId() != 0 ? mDbHelper.getAccount(mTransaction.getToAccountId()) : mDbHelper.getAccount(mTransaction.getFromAccountId());

            mCal            = mTransaction.getTime();

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
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentSelectCategory(TransactionEnum.Expense, mCategory != null ? mCategory.getId() : 0);
                break;
            case R.id.llIncomeCategory:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentSelectCategory(TransactionEnum.Income, mCategory != null ? mCategory.getId() : 0);
                break;
            case R.id.llTransferCategory:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentSelectCategory(TransactionEnum.Transfer, mCategory != null ? mCategory.getId() : 0);
                break;
            case R.id.llAdjustmentCategory:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                Double balance      = !etAdjustmentBalance.getText().toString().equals("") ?
                        Double.parseDouble(etAdjustmentBalance.getText().toString().replaceAll(",", ""))
                        : 0;

                int fromAccountId   = mFromAccount.getId();
                Double remain       = mDbHelper.getAccountRemainBefore(fromAccountId, mCal);

                startFragmentSelectCategory(remain > balance ? TransactionEnum.Expense : TransactionEnum.Income, mCategory != null ? mCategory.getId() : 0);
                break;
            case R.id.llExpenseDescription:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentDescription(TransactionEnum.Expense, tvExpenseDescription.getText().toString());
                break;
            case R.id.llIncomeDescription:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentDescription(TransactionEnum.Income, tvExpenseDescription.getText().toString());
                break;
            case R.id.llTransferDescription:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentDescription(TransactionEnum.Transfer, tvExpenseDescription.getText().toString());
                break;
            case R.id.llAdjustmentDescription:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentDescription(TransactionEnum.Adjustment, tvExpenseDescription.getText().toString());
                break;
            case R.id.llExpenseAccount:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentSelectAccount(TransactionEnum.Expense, mFromAccount != null ? mFromAccount.getId() : 0);
                break;
            case R.id.llToAccount:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentSelectAccount(TransactionEnum.Income, mToAccount != null ? mToAccount.getId() : 0);
                break;
            case R.id.llTransferFromAccount:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentSelectAccount(TransactionEnum.TransferFrom, mFromAccount != null ? mFromAccount.getId() : 0);
                break;
            case R.id.llTransferToAccount:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentSelectAccount(TransactionEnum.TransferTo, mToAccount != null ? mToAccount.getId() : 0);
                break;
            case R.id.llAdjustmentAccount:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentSelectAccount(TransactionEnum.Adjustment, mFromAccount != null ? mFromAccount.getId() : 0);
                break;
            case R.id.llExpenseDate:
            case R.id.llIncomeDate:
            case R.id.llTransferDate:
            case R.id.llAdjustmentDate:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                showDialogTime();
                break;
            case R.id.llExpensePayee:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentPayee(TransactionEnum.Expense, tvExpensePayee.getText().toString());
                break;
            case R.id.llAdjustmentPayee:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentPayee(TransactionEnum.Adjustment, tvAdjustmentPayee.getText().toString());
                break;
            case R.id.llExpenseEvent:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentEvent(TransactionEnum.Expense, tvExpenseEvent.getText().toString());
                break;
            case R.id.llIncomeEvent:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentEvent(TransactionEnum.Income, tvIncomeEvent.getText().toString());
                break;
            case R.id.llAdjustmentEvent:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentEvent(TransactionEnum.Adjustment, tvAdjustmentEvent.getText().toString());
                break;
            case R.id.llSave: {
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                updateTransaction();
                break;
            }
            case R.id.llDelete: {
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                mDbHelper.deleteTransaction(mTransaction.getId());

                cleanup();

                // Return to FragmentListTransaction
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

                        if(mCategory != null && !mCategory.isExpense()) {
                            mCategory = null;
                            tvExpenseCategory.setText("");
                        }
                        break;
                    case 1:
                        llIncome.setVisibility(View.VISIBLE);
                        mCurrentTransactionType = TransactionEnum.Income;

                        if(mCategory != null && mCategory.isExpense()) {
                            mCategory = null;
                            tvIncomeCategory.setText("");
                        }
                        break;
                    case 2:
                        llTransfer.setVisibility(View.VISIBLE);
                        mCurrentTransactionType = TransactionEnum.Transfer;

                        if(mCategory != null && !mCategory.isExpense()) {
                            mCategory = mDbHelper.getCategory(mTransaction.getCategoryId());
                            tvExpenseCategory.setText(mCategory.getName());
                        }
                        break;
                    case 3:
                        llAdjustment.setVisibility(View.VISIBLE);
                        mCurrentTransactionType = TransactionEnum.Adjustment;

                        Double balance = !etAdjustmentBalance.getText().toString().equals("") ? Double.parseDouble(etAdjustmentBalance.getText().toString().replaceAll(",", "")) : 0;
                        Double remain   = mDbHelper.getAccountRemainBefore(mFromAccount.getId(), mCal);

                        if((remain.doubleValue() > balance.doubleValue() && mCategory != null && !mCategory.isExpense()) ||
                                (remain.doubleValue() < balance.doubleValue() && mCategory != null && mCategory.isExpense())) {
                            mCategory = null;
                            tvAdjustmentCategory.setText("");
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
        tvExpenseDate.setText(getDateString(mCal));

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
        tvIncomeDate.setText(getDateString(mCal));

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
        tvTransferDate.setText(getDateString(mCal));

        etTransferFee               = (ClearableEditText) getView().findViewById(R.id.etTransferFee);
        etTransferFee.setText(Currency.formatCurrencyDouble(Currency.getCurrencyById(mFromAccount.getCurrencyId()),
                                                                                     mTransaction.getFee()));
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
        Double remain               = mDbHelper.getAccountRemainAfter(mFromAccount.getId(), mCal);
        etAdjustmentBalance.setText(Currency.formatCurrencyDouble(Currency.getCurrencyById(mFromAccount.getCurrencyId()), remain));

        tvAdjustmentCurrencyIcon    = (TextView) getView().findViewById(R.id.tvAdjustmentCurrencyIcon);
        tvAdjustmentSpent           = (TextView) getView().findViewById(R.id.tvAdjustmentSpent);

        if(mTransaction.getFromAccountId() > 0) {
            tvAdjustmentSpent.setText(String.format(getResources().getString(R.string.content_expensed),
                                                    Currency.formatCurrency(getContext(),
                                                                            Currency.getCurrencyById(mFromAccount.getCurrencyId()),
                                                                            mTransaction.getAmount())));
        } else {
            tvAdjustmentSpent.setText(String.format(getResources().getString(R.string.content_adjustment_income),
                                                    Currency.formatCurrency(getContext(),
                                                                            Currency.getCurrencyById(mFromAccount.getCurrencyId()),
                                                                            mTransaction.getAmount())));
        }

        llAdjustmentCategory        = (LinearLayout) getView().findViewById(R.id.llAdjustmentCategory);
        llAdjustmentCategory.setOnClickListener(this);
        if(mTransaction.getFromAccountId() != 0) {
            ((TextView) getView().findViewById(R.id.tvTitleAdjustmentCategory)).setText(getResources().getString(R.string.transaction_category_expense));
        }
        if(mTransaction.getToAccountId() != 0) {
            ((TextView) getView().findViewById(R.id.tvTitleAdjustmentCategory)).setText(getResources().getString(R.string.transaction_category_income));
        }
        tvAdjustmentCategory        = (TextView) getView().findViewById(R.id.tvAdjustmentCategory);
        tvAdjustmentCategory.setText(mCategory != null ? mCategory.getName() : "");

        llAdjustmentDescription     = (LinearLayout) getView().findViewById(R.id.llAdjustmentDescription);
        llAdjustmentDescription.setOnClickListener(this);
        tvAdjustmentDescription     = (TextView) getView().findViewById(R.id.tvAdjustmentDescription);
        tvAdjustmentDescription.setText(mTransaction.getDescription());

        llAdjustmentDate            = (LinearLayout) getView().findViewById(R.id.llAdjustmentDate);
        llAdjustmentDate.setOnClickListener(this);
        tvAdjustmentDate            = (TextView) getView().findViewById(R.id.tvAdjustmentDate);
        tvAdjustmentDate.setText(getDateString(mCal));

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

                if(mCurrentTransactionType == TransactionEnum.Adjustment && tvAdjustmentSpent != null) {
                    Double balance = !mEdittext.getText().toString().equals("") ?
                                        Double.parseDouble(mEdittext.getText().toString().replaceAll(",", ""))
                                        : 0;
                    Double remain   = mDbHelper.getAccountRemainBefore(mFromAccount.getId(), mCal);

                    if(remain.doubleValue() > balance.doubleValue()) {
                        tvAdjustmentSpent.setText(String.format(getResources().getString(R.string.content_expensed),
                                                                    Currency.formatCurrency(getContext(),
                                                                                                Currency.getCurrencyById(mFromAccount.getCurrencyId()),
                                                                                                remain - balance)));
                        ((TextView) getView().findViewById(R.id.tvTitleAdjustmentCategory)).setText(getResources().getString(R.string.transaction_category_expense));
                        if(mCategory != null && !mCategory.isExpense()) {
                            tvAdjustmentCategory.setText("");
                            mCategory   = null;
                        }
                    } else {
                        tvAdjustmentSpent.setText(String.format(getResources().getString(R.string.content_adjustment_income),
                                                                    Currency.formatCurrency(getContext(),
                                                                                                Currency.getCurrencyById(mFromAccount.getCurrencyId()),
                                                                                                balance - remain)));
                        ((TextView) getView().findViewById(R.id.tvTitleAdjustmentCategory)).setText(getResources().getString(R.string.transaction_category_income));
                        if(mCategory != null && mCategory.isExpense()) {
                            tvAdjustmentCategory.setText("");
                            mCategory   = null;
                        }
                    }
                }

                mEdittext.addTextChangedListener(this);
            }

            LogUtils.logLeaveFunction(Tag, null, null);
        }

    }

    private void updateTransaction() {
        LogUtils.logEnterFunction(Tag, null);
        Transaction transaction = new Transaction();

        switch (mCurrentTransactionType) {
            case Expense: {

                String inputtedAmout = etExpenseAmount.getText().toString().trim().replaceAll(",", "");
                if (inputtedAmout.equals("") || Double.parseDouble(inputtedAmout) == 0) {
                    etExpenseAmount.setError(getResources().getString(R.string.Input_Error_Amount_Empty));
                    return;
                }

                if (mFromAccount == null) {
                    ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Account_Empty));
                    return;
                }

                Double expenseAmount        = Double.parseDouble(inputtedAmout);
                int expenseCategoryId       = mCategory != null ? mCategory.getId() : 0;
                String expenseDescription   = tvExpenseDescription.getText().toString();
                int expenseAccountId        = mFromAccount.getId();
                String expensePayee         = tvExpensePayee.getText().toString();
                String expenseEvent         = tvExpenseEvent.getText().toString();

                transaction     = new Transaction(mTransaction.getId(),
                                                    TransactionEnum.Expense.getValue(),
                                                    expenseAmount,
                                                    expenseCategoryId,
                                                    expenseDescription,
                                                    expenseAccountId,
                                                    0,
                                                    mCal,
                                                    0.0,
                                                    expensePayee,
                                                    expenseEvent);
                break;
            }
            case Income: {

                String inputtedAmount = etIncomeAmount.getText().toString().trim().replaceAll(",", "");

                if (inputtedAmount.equals("") || Double.parseDouble(inputtedAmount) == 0) {
                    etIncomeAmount.setError(getResources().getString(R.string.Input_Error_Amount_Empty));
                    return;
                }

                if (mToAccount == null) {
                    ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Account_Empty));
                    return;
                }

                Double incomeAmount             = Double.parseDouble(inputtedAmount);
                int incomeCategoryId            = mCategory != null ? mCategory.getId() : 0;
                String incomeDescription        = tvIncomeDescription.getText().toString();
                int incomeAccountId             = mToAccount.getId();
                String incomeEvent              = tvIncomeEvent.getText().toString();

                transaction = new Transaction(mTransaction.getId(),
                                                TransactionEnum.Income.getValue(),
                                                incomeAmount,
                                                incomeCategoryId,
                                                incomeDescription,
                                                0,
                                                incomeAccountId,
                                                mCal,
                                                0.0,
                                                "",
                                                incomeEvent);
                break;
            }
            case Transfer: {

                String inputtedAmount = etIncomeAmount.getText().toString().trim().replaceAll(",", "");

                if (inputtedAmount.equals("") || Double.parseDouble(inputtedAmount) == 0) {
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

                if(mFromAccount.getId() == mToAccount.getId()) {
                    ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Transfer_Same_Account));
                    return;
                }

                Double transferAmount       = Double.parseDouble(inputtedAmount);
                int fromAccountId           = mFromAccount.getId();
                int toAccountId             = mToAccount.getId();
                String transferDescription  = tvTransferDescription.getText().toString();
                Double transferFee          = !etTransferFee.getText().toString().equals("") ?
                        Double.parseDouble(etTransferFee.getText().toString().replaceAll(",", ""))
                        : 0.0;

                int transferCategoryId      = mCategory != null ? mCategory.getId() : 0;

                transaction = new Transaction(mTransaction.getId(),
                                                TransactionEnum.Transfer.getValue(),
                                                transferAmount,
                                                transferCategoryId,
                                                transferDescription,
                                                fromAccountId,
                                                toAccountId,
                                                mCal,
                                                transferFee,
                                                "",
                                                "");
                break;
            }
            case Adjustment: {
                if (mFromAccount == null) {
                    ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Account_Empty));
                    return;
                }

                Double balance                  = !etAdjustmentBalance.getText().toString().equals("") ? Double.parseDouble(etAdjustmentBalance.getText().toString().replaceAll(",", "")): 0;
                Double remain                   = mDbHelper.getAccountRemainBefore(mFromAccount.getId(), mCal);

                if(remain.doubleValue() == balance.doubleValue()) {
                    ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Adjustment_Same_Amount));
                    return;
                }

                int adjustmentCategoryId        = mCategory != null ? mCategory.getId() : 0;
                String adjustmentDescription    = tvAdjustmentDescription.getText().toString();
                String adjustmentPayee          = tvAdjustmentPayee.getText().toString();
                String adjustmentEvent          = tvAdjustmentEvent.getText().toString();
                transaction = new Transaction(mTransaction.getId(),
                                                TransactionEnum.Adjustment.getValue(),
                                                Math.abs(remain - balance),
                                                adjustmentCategoryId,
                                                adjustmentDescription,
                                                remain > balance ? mFromAccount.getId() : 0,
                                                remain > balance ? 0 : mFromAccount.getId(),
                                                mCal,
                                                0.0,
                                                adjustmentPayee,
                                                adjustmentEvent);
                break;
            }
            default:
                break;
        }

        int row = mDbHelper.updateTransaction(transaction);
        if (row == 1) {
            ((ActivityMain) getActivity()).showToastSuccessful(getResources().getString(R.string.message_transaction_update_successful));
            cleanup();
        }

        LogUtils.logLeaveFunction(Tag, null, null);
        // Return to last fragment
        getFragmentManager().popBackStackImmediate();
    }
    private void showDialogTime() {
        final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mCal.set(Calendar.MINUTE, minute);

                        tvExpenseDate.setText(getDateString(mCal));
                        tvIncomeDate.setText(getDateString(mCal));
                        tvTransferDate.setText(getDateString(mCal));
                        tvAdjustmentDate.setText(getDateString(mCal));

                        if(mCurrentTransactionType  != TransactionEnum.Adjustment) {
                            return;
                        }

                        Double remain   = mDbHelper.getAccountRemainBefore(mFromAccount.getId(), mCal);
                        Double balance  = !etAdjustmentBalance.getText().toString().equals("") ? Double.parseDouble(etAdjustmentBalance.getText().toString().replaceAll(",", "")) : 0;

                        if (remain.doubleValue() > balance.doubleValue()) {
                            tvAdjustmentSpent.setText(String.format(getResources().getString(R.string.content_expensed),
                                    Currency.formatCurrency(getContext(), Currency.getCurrencyById(mFromAccount.getCurrencyId()), remain - balance)));

                            ((TextView) getView().findViewById(R.id.tvTitleAdjustmentCategory)).setText(getResources().getString(R.string.transaction_category_expense));
                            if(mCategory != null && !mCategory.isExpense()) {
                                tvAdjustmentCategory.setText("");
                                mCategory   = null;
                            }
                        } else {
                            tvAdjustmentSpent.setText(String.format(getResources().getString(R.string.content_income),
                                    Currency.formatCurrency(getContext(), Currency.getCurrencyById(mFromAccount.getCurrencyId()), balance - remain)));

                            ((TextView) getView().findViewById(R.id.tvTitleAdjustmentCategory)).setText(getResources().getString(R.string.transaction_category_income));
                            if(mCategory != null && mCategory.isExpense()) {
                                tvAdjustmentCategory.setText("");
                                mCategory   = null;
                            }
                        }
                    }
                }, mCal.get(Calendar.HOUR_OF_DAY), mCal.get(Calendar.MINUTE), true);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        mCal.set(Calendar.YEAR, year);
                        mCal.set(Calendar.MONTH, monthOfYear);
                        mCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        timePickerDialog.show();
                    }
                }, mCal.get(Calendar.YEAR), mCal.get(Calendar.MONTH), mCal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void startFragmentSelectCategory(TransactionEnum transactionType, int oldCategoryId) {
        FragmentCategorySelect nextFragment = new FragmentCategorySelect();
        Bundle bundle = new Bundle();
        bundle.putString("Tag", ((ActivityMain) getActivity()).getFragmentTransactionUpdate());
        bundle.putInt("CategoryID", oldCategoryId);
        bundle.putSerializable("TransactionType", transactionType);
        nextFragment.setArguments(bundle);
        FragmentTransactionUpdate.this.getFragmentManager().beginTransaction()
                .add(mContainerViewId, nextFragment, "FragmentCategorySelect")
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
                if(mCurrentTransactionType == TransactionEnum.Expense) {
                    tvExpenseCategory.setText(mCategory.getName());
                } else if(mCurrentTransactionType == TransactionEnum.Adjustment) {
                    tvAdjustmentCategory.setText(mCategory.getName());
                }
                break;
            case Income:
                if(mCurrentTransactionType == TransactionEnum.Income) {
                    tvIncomeCategory.setText(mCategory.getName());
                } else if(mCurrentTransactionType == TransactionEnum.Adjustment) {
                    tvAdjustmentCategory.setText(mCategory.getName());
                }
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
                .add(mContainerViewId, nextFragment, "FragmentDescription")
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
                .add(mContainerViewId, nextFragment, "FragmentAccountsSelect")
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
                Double remain   = mDbHelper.getAccountRemainBefore(accountId, mCal);
                if(remain.doubleValue() > balance.doubleValue()) {
                    tvAdjustmentSpent.setText(String.format(getResources().getString(R.string.content_expensed),
                            Currency.formatCurrency(getContext(),
                                    Currency.getCurrencyById(mFromAccount.getCurrencyId()),
                                    remain - balance)));
                    ((TextView) getView().findViewById(R.id.tvTitleAdjustmentCategory)).setText(getResources().getString(R.string.transaction_category_expense));
                    if(mCategory != null && !mCategory.isExpense()) {
                        tvAdjustmentCategory.setText("");
                        mCategory   = null;
                    }
                } else {
                    tvAdjustmentSpent.setText(String.format(getResources().getString(R.string.content_adjustment_income),
                            Currency.formatCurrency(getContext(),
                                    Currency.getCurrencyById(mFromAccount.getCurrencyId()),
                                    balance - remain)));
                    ((TextView) getView().findViewById(R.id.tvTitleAdjustmentCategory)).setText(getResources().getString(R.string.transaction_category_income));
                    if(mCategory != null && mCategory.isExpense()) {
                        tvAdjustmentCategory.setText("");
                        mCategory   = null;
                    }
                }

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
                .add(mContainerViewId, nextFragment, "FragmentPayee")
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
                .add(mContainerViewId, nextFragment, "FragmentEvent")
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

    /**
     * Get Date's String
     * @return
     */
    private String getDateString(Calendar cal) {
        Calendar current = Calendar.getInstance();
        String date = "";
        if(cal.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)) {
            date = getResources().getString(R.string.content_today) + String.format(" %02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        } else if((cal.get(Calendar.DAY_OF_YEAR) - 1) == current.get(Calendar.DAY_OF_YEAR)) {
            date = getResources().getString(R.string.content_yesterday) + String.format(" %02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        } else if((cal.get(Calendar.DAY_OF_YEAR) - 2) == current.get(Calendar.DAY_OF_YEAR)
                && getResources().getConfiguration().locale.equals(Locale.forLanguageTag("vi_VN"))) {
            date = getResources().getString(R.string.content_before_yesterday) + String.format(" %02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        } else {
            date = String.format("%02d-%02d-%02d %02d:%02d", mCal.get(Calendar.DAY_OF_MONTH), mCal.get(Calendar.MONTH) + 1, mCal.get(Calendar.YEAR), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
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
        mCal                        = null;

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
