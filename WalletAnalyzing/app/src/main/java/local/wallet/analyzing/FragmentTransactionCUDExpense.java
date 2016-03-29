package local.wallet.analyzing;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import org.droidparts.widget.ClearableEditText;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Debt;
import local.wallet.analyzing.model.Event;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.model.Transaction.TransactionEnum;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;
import local.wallet.analyzing.FragmentTransactionSelectCategory.ISelectCategory;
import local.wallet.analyzing.FragmentAccountsSelect.ISelectAccount;
import local.wallet.analyzing.FragmentDescription.IUpdateDescription;
import local.wallet.analyzing.FragmentPayee.IUpdatePayee;
import local.wallet.analyzing.FragmentEvent.IUpdateEvent;
import local.wallet.analyzing.FragmentLenderBorrower.IUpdateLenderBorrower;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentTransactionCUDExpense extends Fragment implements  View.OnClickListener, ISelectCategory, ISelectAccount, IUpdateDescription, IUpdatePayee, IUpdateEvent, IUpdateLenderBorrower {
    public static final String Tag = "TransactionCreateExpense";

    private Configurations      mConfigs;
    private DatabaseHelper      mDbHelper;

    private Category            mCategory;
    private Account             mFromAccount;

    private ClearableEditText   etAmount;
    private TextView            tvCurrencyIcon;
    private LinearLayout        llCategory;
    private TextView            tvCategory;
    private LinearLayout        llPeople;
    private TextView            tvTitlePeople;
    private TextView            tvPeople;
    private LinearLayout        llDescription;
    private TextView            tvDescription;
    private LinearLayout        llAccount;
    private TextView            tvAccount;
    private LinearLayout        llDate;
    private TextView            tvDate;
    private LinearLayout        llPayee;
    private TextView            tvPayee;
    private LinearLayout        llEvent;
    private TextView            tvEvent;

    private LinearLayout        llSave;
    private LinearLayout        llDelete;

    private Calendar            mCal;

    private Transaction         mTransaction;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_transaction_cud_expense, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);

        Bundle bundle           = this.getArguments();
        if(bundle != null) {
            mTransaction            = (Transaction)bundle.get("Transaction");

            LogUtils.trace(Tag, "mTransaction = " + mTransaction.toString());
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onActivityCreated(savedInstanceState);

        mConfigs    = new Configurations(getActivity());
        mDbHelper   = new DatabaseHelper(getActivity());

        llSave      = (LinearLayout) getView().findViewById(R.id.llSave);
        llSave.setOnClickListener(this);
        llDelete    = (LinearLayout) getView().findViewById(R.id.llDelete);
        llDelete.setOnClickListener(this);

        if(mTransaction.getId() == 0) {
            llDelete.setVisibility(View.GONE);
        }

        mCategory       = mDbHelper.getCategory(mTransaction.getCategoryId());
        mFromAccount    = mTransaction.getFromAccountId() != 0 ? mDbHelper.getAccount(mTransaction.getFromAccountId()) : mDbHelper.getAccount(mTransaction.getToAccountId());
        mCal            = mTransaction.getTime();

        initView();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llCategory:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentSelectCategory(mCategory != null ? mCategory.getId() : 0);
                break;
            case R.id.llPeople:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentLenderBorrower(tvPeople.getText().toString());
                break;
            case R.id.llDescription:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentDescription(tvDescription.getText().toString());
                break;
            case R.id.llAccount:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentSelectAccount(TransactionEnum.Expense, mFromAccount != null ? mFromAccount.getId() : 0);
                break;
            case R.id.llDate:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                showDialogTime();
                break;
            case R.id.llPayee:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentPayee(tvPayee.getText().toString());
                break;
            case R.id.llEvent:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentEvent(tvEvent.getText().toString());
                break;
            case R.id.llSave:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                if(mTransaction.getId() != 0) {
                    updateTransaction();
                } else {
                    createTransaction();
                }
                break;
            case R.id.llDelete:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                deleteTransaction();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCategorySelected(int categoryId) {
        LogUtils.logEnterFunction(Tag, "categoryId = " + categoryId);

        mCategory = mDbHelper.getCategory(categoryId);

        if(mCategory != null) {
            tvCategory.setText(mCategory.getName());

            switch (mCategory.getDebtType()) {
                case MORE:
                    llPeople.setVisibility(View.VISIBLE);
                    tvTitlePeople.setText(getResources().getString(R.string.new_transaction_borrower));
                    llEvent.setVisibility(View.GONE);
                    llPayee.setVisibility(View.GONE);
                    break;
                case NONE:
                    llPeople.setVisibility(View.GONE);
                    llEvent.setVisibility(View.VISIBLE);
                    llPayee.setVisibility(View.VISIBLE);
                    break;
                case LESS:
                    llPeople.setVisibility(View.VISIBLE);
                    tvTitlePeople.setText(getResources().getString(R.string.new_transaction_lender));
                    llEvent.setVisibility(View.GONE);
                    llPayee.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        } else {
            tvCategory.setText("");
        }


        LogUtils.logLeaveFunction(Tag, "categoryId = " + categoryId, null);
    }

    @Override
    public void onLenderBorrowerUpdated(String people) {
        LogUtils.logEnterFunction(Tag, "people = '" + people + "\'");
        tvPeople.setText(people);
        LogUtils.logLeaveFunction(Tag, "people = '" + people + "\'", null);
    }

    @Override
    public void onDescriptionUpdated(String description) {
        LogUtils.logEnterFunction(Tag, "description = " + description);

        tvDescription.setText(description);

        LogUtils.logLeaveFunction(Tag, "description = " + description, null);
    }

    @Override
    public void onAccountSelected(TransactionEnum type, int accountId) {
        LogUtils.logEnterFunction(Tag, "TransactionType = " + type.name() + ", accountId = " + accountId);

        if(type == TransactionEnum.Expense) {
            mFromAccount = mDbHelper.getAccount(accountId);
            tvAccount.setText(mFromAccount.getName());
            tvCurrencyIcon.setText(getResources().getString(Currency.getCurrencyIcon(mFromAccount.getCurrencyId())));
        }

        LogUtils.logLeaveFunction(Tag, "TransactionType = " + type.name() + ", accountId = " + accountId, null);
    }

    @Override
    public void onPayeeUpdated(String payee) {
        LogUtils.logEnterFunction(Tag, "payee = " + payee);

        tvPayee.setText(payee);

        LogUtils.logLeaveFunction(Tag, "payee = " + payee, null);
    }

    @Override
    public void onEventUpdated(String event) {
        LogUtils.logEnterFunction(Tag, "event = " + event);

        tvEvent.setText(event);

        LogUtils.logLeaveFunction(Tag, "event = " + event, null);
    }

    /**
     * Todo: Init view EXPENSE
     */
    private void initView() {
        LogUtils.logEnterFunction(Tag, null);

        etAmount         = (ClearableEditText) getView().findViewById(R.id.etAmount);
        etAmount.addTextChangedListener(new CurrencyTextWatcher(etAmount));
        tvCurrencyIcon   = (TextView) getView().findViewById(R.id.tvCurrencyIcon);
        tvCurrencyIcon.setText(Currency.getCurrencyIcon(mConfigs.getInt(Configurations.Key.Currency)));

        llCategory       = (LinearLayout) getView().findViewById(R.id.llCategory);
        llCategory.setOnClickListener(this);
        tvCategory       = (TextView) getView().findViewById(R.id.tvCategory);

        llPeople = (LinearLayout) getView().findViewById(R.id.llPeople);
        llPeople.setOnClickListener(this);
        tvTitlePeople = (TextView) getView().findViewById(R.id.tvTitlePeople);
        tvPeople = (TextView) getView().findViewById(R.id.tvPeople);


        llDescription    = (LinearLayout) getView().findViewById(R.id.llDescription);
        llDescription.setOnClickListener(this);
        tvDescription    = (TextView) getView().findViewById(R.id.tvDescription);
        tvDescription.setText(mTransaction != null ? mTransaction.getDescription() : "");

        llAccount        = (LinearLayout) getView().findViewById(R.id.llAccount);
        llAccount.setOnClickListener(this);
        tvAccount        = (TextView) getView().findViewById(R.id.tvAccount);
        tvAccount.setText(mFromAccount != null ? mFromAccount.getName() : "");

        llDate           = (LinearLayout) getView().findViewById(R.id.llDate);
        llDate.setOnClickListener(this);
        tvDate           = (TextView) getView().findViewById(R.id.tvDate);
        tvDate.setText(getDateString(mCal));

        llPayee          = (LinearLayout) getView().findViewById(R.id.llPayee);
        llPayee.setOnClickListener(this);
        tvPayee          = (TextView) getView().findViewById(R.id.tvPayee);

        llEvent          = (LinearLayout) getView().findViewById(R.id.llEvent);
        llEvent.setOnClickListener(this);
        tvEvent          = (TextView) getView().findViewById(R.id.tvEvent);

        tvCategory.setText(mCategory != null ? mCategory.getName() : "");
        tvPeople.setText(mDbHelper.getDebtByTransactionId(mTransaction.getId()) != null ? mDbHelper.getDebtByTransactionId(mTransaction.getId()).getPeople() : "");
        etAmount.setText(Currency.formatCurrencyDouble(mConfigs.getInt(Configurations.Key.Currency), mTransaction.getAmount()));
        tvDescription.setText(mTransaction.getDescription());
        tvPayee.setText(mTransaction.getPayee());
        tvEvent.setText(mTransaction.getEvent() != null ? mTransaction.getEvent().getName() : "");

        if(mCategory == null) {
            llPeople.setVisibility(View.GONE);
            llEvent.setVisibility(View.VISIBLE);
            llPayee.setVisibility(View.VISIBLE);
        } else {
            switch (mCategory.getDebtType()) {
                case MORE:
                    llPeople.setVisibility(View.VISIBLE);
                    tvTitlePeople.setText(getResources().getString(R.string.new_transaction_borrower));
                    llEvent.setVisibility(View.GONE);
                    llPayee.setVisibility(View.GONE);
                    break;
                case NONE:
                    llPeople.setVisibility(View.GONE);
                    llEvent.setVisibility(View.VISIBLE);
                    llPayee.setVisibility(View.VISIBLE);
                    break;
                case LESS:
                    llPeople.setVisibility(View.VISIBLE);
                    tvTitlePeople.setText(getResources().getString(R.string.new_transaction_lender));
                    llEvent.setVisibility(View.GONE);
                    llPayee.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }

        LogUtils.logLeaveFunction(Tag, null, null);
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

                String inputted = s.toString().replaceAll(",", "").replaceAll(" ", "");
                if(!inputted.equals("")) {
                    String formatted = Currency.formatCurrencyDouble(mFromAccount != null ?
                                    mFromAccount.getCurrencyId()
                                    : mConfigs.getInt(Configurations.Key.Currency),
                            Double.parseDouble(inputted));

                    current = formatted;
                    mEdittext.setText(formatted);
                    mEdittext.setSelection(formatted.length());

                }

                mEdittext.addTextChangedListener(this);
            }

            LogUtils.logLeaveFunction(Tag, null, null);
        }

    } // End CurrencyTextWatcher

    /**
     * Save Transaction to Database
     */
    private void createTransaction() {
        LogUtils.logEnterFunction(Tag, null);
        String inputtedAmount = etAmount.getText().toString().trim().replaceAll(",", "");

        if (inputtedAmount.equals("") || Double.parseDouble(inputtedAmount) == 0) {
            etAmount.setError(getResources().getString(R.string.Input_Error_Amount_Empty));
            return;
        }

        if(mFromAccount == null) {
            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Account_Empty));
            return;
        }

        Double  amount        = Double.parseDouble(etAmount.getText().toString().replaceAll(",", ""));
        int     CategoryId    = mCategory != null ? mCategory.getId() : 0;
        String  Description   = tvDescription.getText().toString();
        int     accountId     = mFromAccount.getId();
        String  payee         = tvPayee.getText().toString();
        String  strEvent      = tvEvent.getText().toString();
        Event   event         = null;

        if(!strEvent.equals("")) {
            event = mDbHelper.getEventByName(strEvent);
            if(event == null) {
                long eventId = mDbHelper.createEvent(new Event(0, strEvent, mCal, null));
                if(eventId != -1) {
                    event = mDbHelper.getEvent(eventId);
                }
            }
        }

        boolean isDebtValid = true;
        // Less: Repayment, More: Lend
        if(mCategory.getDebtType() == Category.EnumDebt.LESS || mCategory.getDebtType() == Category.EnumDebt.MORE) {


            if(mCategory.getDebtType() == Category.EnumDebt.LESS) { // Expense -> Repayment
                List<Debt> debts = mDbHelper.getAllDebts();

                Double borrowed = 0.0, repayment = 0.0, lend = 0.0, debtCollect = 0.0;
                for(Debt debt : debts) {
                    if(debt.getPeople().equals(tvPeople.getText().toString())) {
                        if(mDbHelper.getCategory(debt.getCategoryId()).isExpense() && mDbHelper.getCategory(debt.getCategoryId()).getDebtType() == Category.EnumDebt.LESS) {
                            repayment += debt.getAmount();
                        }

                        if(mDbHelper.getCategory(debt.getCategoryId()).isExpense() && mDbHelper.getCategory(debt.getCategoryId()).getDebtType() == Category.EnumDebt.MORE) {
                            lend += debt.getAmount();
                        }

                        if(!mDbHelper.getCategory(debt.getCategoryId()).isExpense() && mDbHelper.getCategory(debt.getCategoryId()).getDebtType() == Category.EnumDebt.LESS) {
                            debtCollect += debt.getAmount();
                        }

                        if(!mDbHelper.getCategory(debt.getCategoryId()).isExpense() && mDbHelper.getCategory(debt.getCategoryId()).getDebtType() == Category.EnumDebt.MORE) {
                            borrowed += debt.getAmount();
                        }
                    }
                }

                if(repayment + lend + amount > borrowed + debtCollect) {
                    isDebtValid = false;
                    ((ActivityMain) getActivity()).showError(getResources().getString(R.string.message_debt_repayment_invalid));
                }
            }

        }

        if(isDebtValid) {
            Transaction transaction = new Transaction(0,
                    TransactionEnum.Expense.getValue(),
                    amount,
                    CategoryId,
                    Description,
                    accountId,
                    0,
                    mCal,
                    0.0,
                    payee,
                    event);

            long newTransactionId = mDbHelper.createTransaction(transaction);

            if (newTransactionId != -1) {

                if(mCategory.getDebtType() == Category.EnumDebt.LESS || mCategory.getDebtType() == Category.EnumDebt.MORE) {
                    Debt newDebt = new Debt();
                    newDebt.setCategoryId(mCategory.getId());
                    newDebt.setTransactionId((int) newTransactionId);
                    newDebt.setAmount(amount);
                    newDebt.setPeople(tvPeople.getText().toString());

                    long debtId = mDbHelper.createDebt(newDebt);
                    if(debtId != -1) {
                        ((ActivityMain) getActivity()).showToastSuccessful(getResources().getString(R.string.message_transaction_create_successful));
                        cleanup();
                    } else {
                        ((ActivityMain) getActivity()).showError(getResources().getString(R.string.message_transaction_create_fail));
                        mDbHelper.deleteTransaction(newTransactionId);
                    }
                } else {
                    ((ActivityMain) getActivity()).showToastSuccessful(getResources().getString(R.string.message_transaction_create_successful));
                    cleanup();
                }

            } else {
                ((ActivityMain) getActivity()).showError(getResources().getString(R.string.message_transaction_create_fail));
            }
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End createTransaction

    /**
     * Update Transaction
     */
    private void updateTransaction() {
        LogUtils.logEnterFunction(Tag, null);

        String inputtedAmount = etAmount.getText().toString().trim().replaceAll(",", "");
        if (inputtedAmount.equals("") || Double.parseDouble(inputtedAmount) == 0) {
            etAmount.setError(getResources().getString(R.string.Input_Error_Amount_Empty));
            return;
        }

        if (mFromAccount == null) {
            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Account_Empty));
            return;
        }

        Double  amount          = Double.parseDouble(inputtedAmount);
        int     categoryId      = mCategory != null ? mCategory.getId() : 0;
        String  description     = tvDescription.getText().toString();
        int     accountId       = mFromAccount.getId();
        String  payee           = tvPayee.getText().toString();
        String  strEvent        = tvEvent.getText().toString();
        Event   event = null;

        if(!strEvent.equals("")) {
            event = mDbHelper.getEventByName(strEvent);
            if(event == null) {
                long eventId = mDbHelper.createEvent(new Event(0, strEvent, mCal, null));
                if(eventId != -1) {
                    event = mDbHelper.getEvent(eventId);
                }
            }
        }

        // Less: Repayment, More: Lend
        if(mCategory.getDebtType() == Category.EnumDebt.LESS || mCategory.getDebtType() == Category.EnumDebt.MORE) {

            boolean isDebtValid = true;
            if(mCategory.getDebtType() == Category.EnumDebt.LESS) { // Expense -> Repayment
                List<Debt> debts = mDbHelper.getAllDebts();

                Double borrowed = 0.0, repayment = 0.0, lend = 0.0, debtCollect = 0.0;
                for(Debt debt : debts) {
                    if(debt.getPeople().equals(tvPeople.getText().toString())) {
                        if(mDbHelper.getCategory(debt.getCategoryId()).isExpense() && mDbHelper.getCategory(debt.getCategoryId()).getDebtType() == Category.EnumDebt.LESS) {
                            repayment += debt.getAmount();
                        }

                        if(mDbHelper.getCategory(debt.getCategoryId()).isExpense() && mDbHelper.getCategory(debt.getCategoryId()).getDebtType() == Category.EnumDebt.MORE) {
                            lend += debt.getAmount();
                        }

                        if(!mDbHelper.getCategory(debt.getCategoryId()).isExpense() && mDbHelper.getCategory(debt.getCategoryId()).getDebtType() == Category.EnumDebt.LESS) {
                            debtCollect += debt.getAmount();
                        }

                        if(!mDbHelper.getCategory(debt.getCategoryId()).isExpense() && mDbHelper.getCategory(debt.getCategoryId()).getDebtType() == Category.EnumDebt.MORE) {
                            borrowed += debt.getAmount();
                        }
                    }
                }

                if(repayment + lend + amount > borrowed + debtCollect) {
                    isDebtValid = false;
                    ((ActivityMain) getActivity()).showError(getResources().getString(R.string.message_debt_repayment_invalid));
                } // End Check DEBT OK

            } // End DebtType() == Category.EnumDebt.LESS
            if(isDebtValid) {
                Transaction transaction     = new Transaction(mTransaction.getId(),
                        TransactionEnum.Expense.getValue(),
                        amount,
                        categoryId,
                        description,
                        accountId,
                        0,
                        mCal,
                        0.0,
                        payee,
                        event);

                int row = mDbHelper.updateTransaction(transaction);
                if (row == 1) { // Update transaction OK

                    if(mDbHelper.getDebtByTransactionId(mTransaction.getId()) != null) {
                        Debt debt = mDbHelper.getDebtByTransactionId(mTransaction.getId());
                        debt.setCategoryId(mCategory.getId());
                        debt.setAmount(amount);
                        debt.setPeople(tvPeople.getText().toString());

                        int debtRow = mDbHelper.updateDebt(debt);
                        if(debtRow == 1) {
                            ((ActivityMain) getActivity()).showToastSuccessful(getResources().getString(R.string.message_transaction_update_successful));
                            cleanup();

                            // Return to last fragment
                            getFragmentManager().popBackStackImmediate();
                        } else {
                            // Revert update
                            mDbHelper.updateTransaction(mTransaction);
                            ((ActivityMain) getActivity()).showToastSuccessful(getResources().getString(R.string.message_transaction_update_fail));
                        }
                    } else {
                        Debt newDebt = new Debt();
                        newDebt.setCategoryId(mCategory.getId());
                        newDebt.setTransactionId((int) mTransaction.getId());
                        newDebt.setAmount(amount);
                        newDebt.setPeople(tvPeople.getText().toString());

                        long debtId = mDbHelper.createDebt(newDebt);
                        if(debtId != -1) {
                            ((ActivityMain) getActivity()).showToastSuccessful(getResources().getString(R.string.message_transaction_create_successful));
                            cleanup();
                            // Return to last fragment
                            getFragmentManager().popBackStackImmediate();
                        } else {
                            // Revert update
                            mDbHelper.updateTransaction(mTransaction);
                            ((ActivityMain) getActivity()).showToastSuccessful(getResources().getString(R.string.message_transaction_update_fail));
                        }
                    } // End create new Debt

                } // End Update transaction OK
            }

        } else { // CATEGORY NORMAL
            Transaction transaction     = new Transaction(mTransaction.getId(),
                    TransactionEnum.Expense.getValue(),
                    amount,
                    categoryId,
                    description,
                    accountId,
                    0,
                    mCal,
                    0.0,
                    payee,
                    event);

            int row = mDbHelper.updateTransaction(transaction);
            if (row == 1) { // Update transaction OK
                if(mDbHelper.getDebtByTransactionId(mTransaction.getId()) != null) {
                    mDbHelper.deleteDebt(mDbHelper.getDebtByTransactionId(mTransaction.getId()).getId());
                }

                // Return to last fragment
                getFragmentManager().popBackStackImmediate();
            }
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End Update Transaction

    /**
     * Delete current Transaction
     */
    private void deleteTransaction() {
        LogUtils.logEnterFunction(Tag, null);
        mDbHelper.deleteTransaction(mTransaction.getId());
        if(mDbHelper.getDebtByTransactionId(mTransaction.getId()) != null) {
            mDbHelper.deleteDebt(mDbHelper.getDebtByTransactionId(mTransaction.getId()).getId());
        }

        cleanup();

        // Return to FragmentListTransaction
        getFragmentManager().popBackStackImmediate();
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Show Dialog to select Time
     */
    private void showDialogTime() {
        final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mCal.set(Calendar.MINUTE, minute);

                        tvDate.setText(getDateString(mCal));

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
    } // End showDialogTime

    /**
     * Start fragment to select Category
     * @param oldCategoryId
     */
    private void startFragmentSelectCategory(int oldCategoryId) {
        LogUtils.logEnterFunction(Tag, "OldCategoryId = " + oldCategoryId);
        FragmentTransactionSelectCategory nextFrag = new FragmentTransactionSelectCategory();
        Bundle bundle = new Bundle();
        bundle.putBoolean("CategoryType", true);
        bundle.putInt("CategoryID", oldCategoryId);
        bundle.putSerializable("Callback", this);
        nextFrag.setArguments(bundle);
        FragmentTransactionCUDExpense.this.getFragmentManager().beginTransaction()
                .add(mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update, nextFrag, FragmentTransactionSelectCategory.Tag)
                .addToBackStack(null)
                .commit();
        LogUtils.logLeaveFunction(Tag, "OldCategoryId = " + oldCategoryId, null);
    }

    /**
     * Start fragment LenderBorrower
     * @param oldPeople
     */
    private void startFragmentLenderBorrower(String oldPeople) {
        FragmentLenderBorrower nextFrag = new FragmentLenderBorrower();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Category", mCategory);
        bundle.putString("People", oldPeople);
        bundle.putSerializable("Callback", this);
        nextFrag.setArguments(bundle);
        FragmentTransactionCUDExpense.this.getFragmentManager().beginTransaction()
                .add(mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update, nextFrag, FragmentLenderBorrower.Tag)
                .addToBackStack(Tag)
                .commit();
    }

    /**
     * Start fragment input description
     * @param oldDescription
     */
    private void startFragmentDescription(String oldDescription) {
        FragmentDescription fragmentDescription = new FragmentDescription();
        Bundle bundle = new Bundle();
        bundle.putString("Description", oldDescription);
        bundle.putSerializable("Callback", this);
        fragmentDescription.setArguments(bundle);
        FragmentTransactionCUDExpense.this.getFragmentManager().beginTransaction()
                .add(mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update, fragmentDescription, FragmentDescription.Tag)
                .addToBackStack(Tag)
                .commit();
    }

    /**
     * Start fragment to Select Account
     * @param transactionType
     * @param oldAccountId
     */
    private void startFragmentSelectAccount(TransactionEnum transactionType, int oldAccountId) {
        LogUtils.logEnterFunction(Tag, "TransactionType = " + transactionType.name() + ", oldAccountId = " + oldAccountId);
        FragmentAccountsSelect fragment = new FragmentAccountsSelect();
        Bundle bundle = new Bundle();
        bundle.putInt("AccountID", oldAccountId);
        bundle.putSerializable("TransactionType", transactionType);
        bundle.putSerializable("Callback", this);
        fragment.setArguments(bundle);
        FragmentTransactionCUDExpense.this.getFragmentManager().beginTransaction()
                .add(mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update, fragment, FragmentAccountsSelect.Tag)
                .addToBackStack(null)
                .commit();

        LogUtils.logLeaveFunction(Tag, "TransactionType = " + transactionType.name() + ", oldAccountId = " + oldAccountId, null);
    }

    /**
     * Start fragment Payee
     * @param oldPayee
     */
    private void startFragmentPayee(String oldPayee) {
        FragmentPayee nextFrag = new FragmentPayee();
        Bundle bundle = new Bundle();
        bundle.putString("Payee", oldPayee);
        bundle.putSerializable("Callback", this);
        nextFrag.setArguments(bundle);
        FragmentTransactionCUDExpense.this.getFragmentManager().beginTransaction()
                .add(mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update, nextFrag, "FragmentPayee")
                .addToBackStack(Tag)
                .commit();
    }

    /**
     * Start fragment Event
     * @param oldEvent
     */
    private void startFragmentEvent(String oldEvent) {
        FragmentEvent nextFrag = new FragmentEvent();
        Bundle bundle = new Bundle();
        bundle.putString("Event", oldEvent);
        bundle.putSerializable("Callback", this);
        nextFrag.setArguments(bundle);
        FragmentTransactionCUDExpense.this.getFragmentManager().beginTransaction()
                .add(mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update, nextFrag, "FragmentEvent")
                .addToBackStack(Tag)
                .commit();
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
        mCal                        = null;

        /* Reset value */
        etAmount.setText("");
        tvCategory.setText("");
        tvDescription.setText("");
        tvAccount.setText("");
        tvPayee.setText("");
        tvEvent.setText("");
    }
}
