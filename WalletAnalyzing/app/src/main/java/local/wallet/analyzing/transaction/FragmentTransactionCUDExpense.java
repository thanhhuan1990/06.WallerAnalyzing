package local.wallet.analyzing.transaction;

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

import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.account.FragmentAccountsSelect;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.main.Configurations;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Debt;
import local.wallet.analyzing.model.Event;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.model.Transaction.TransactionEnum;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;
import local.wallet.analyzing.account.FragmentAccountsSelect.ISelectAccount;
import local.wallet.analyzing.transaction.FragmentDescription.IUpdateDescription;
import local.wallet.analyzing.transaction.FragmentPayee.IUpdatePayee;
import local.wallet.analyzing.transaction.FragmentEvent.IUpdateEvent;
import local.wallet.analyzing.transaction.FragmentLenderBorrower.IUpdateLenderBorrower;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentTransactionCUDExpense extends Fragment implements  View.OnClickListener, FragmentTransactionSelectCategory.ISelectCategory, ISelectAccount, IUpdateDescription, IUpdatePayee, IUpdateEvent, IUpdateLenderBorrower {
    private int                 mTab = 1;
    private String              Tag = "---[" + mTab + "]---TransactionCUDExpense";

    private ActivityMain        mActivity;

    private int                 mContainerViewId = -1;

    private Configurations      mConfigs;
    private DatabaseHelper      mDbHelper;

    private Category            mCategory;
    private Account             mAccount;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);

        mConfigs            = new Configurations(getActivity());
        mDbHelper           = new DatabaseHelper(getActivity());

        Bundle bundle       = this.getArguments();
        if(bundle != null) {
            mTab = bundle.getInt("Tab", mTab);
            Tag = "---[" + mTab + ".1]---" + "FragmentTransactionCUD";
            mTransaction    = (Transaction)bundle.get("Transaction");
            LogUtils.trace(Tag, "mTransaction = " + mTransaction.toString());
            // Retry ContainerViewId from bundle
            if(bundle.getInt("ContainerViewId") != 0) {
                mContainerViewId = bundle.getInt("ContainerViewId");
            } else {
                mContainerViewId    = mTransaction.getId() != 0 ? R.id.ll_transaction_update : R.id.ll_transaction_create;
            }

            mCategory       = mDbHelper.getCategory(mTransaction.getCategoryId());

            if(mTransaction.getFromAccountId() != 0) {
                mAccount    = mDbHelper.getAccount(mTransaction.getFromAccountId());
            } else if(mTransaction.getToAccountId() != 0){
                mAccount    = mDbHelper.getAccount(mTransaction.getToAccountId());
            } else if(mDbHelper.getAllAccounts().size() > 0){
                mAccount    = mDbHelper.getAllAccounts().get(0);
            }

            mCal            = mTransaction.getTime();
        } else {
            ((ActivityMain) getActivity()).showError("Bundle is NULL!");
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        View view       = inflater.inflate(R.layout.layout_fragment_transaction_cud_expense, container, false);
        llSave          = (LinearLayout) view.findViewById(R.id.llSave);
        llSave.setOnClickListener(this);
        llDelete        = (LinearLayout) view.findViewById(R.id.llDelete);
        llDelete.setOnClickListener(this);

        if(mTransaction.getId() == 0) {
            llDelete.setVisibility(View.GONE);
        }

        etAmount        = (ClearableEditText) view.findViewById(R.id.etAmount);
        etAmount.addTextChangedListener(new CurrencyTextWatcher(etAmount));
        tvCurrencyIcon  = (TextView) view.findViewById(R.id.tvCurrencyIcon);

        llCategory      = (LinearLayout) view.findViewById(R.id.llCategory);
        llCategory.setOnClickListener(this);
        tvCategory      = (TextView) view.findViewById(R.id.tvCategory);

        llPeople        = (LinearLayout) view.findViewById(R.id.llPeople);
        llPeople.setOnClickListener(this);
        tvTitlePeople   = (TextView) view.findViewById(R.id.tvTitlePeople);
        tvPeople        = (TextView) view.findViewById(R.id.tvPeople);

        llDescription   = (LinearLayout) view.findViewById(R.id.llDescription);
        llDescription.setOnClickListener(this);
        tvDescription   = (TextView) view.findViewById(R.id.tvDescription);

        llAccount       = (LinearLayout) view.findViewById(R.id.llAccount);
        llAccount.setOnClickListener(this);
        tvAccount       = (TextView) view.findViewById(R.id.tvAccount);

        llDate          = (LinearLayout) view.findViewById(R.id.llDate);
        llDate.setOnClickListener(this);
        tvDate          = (TextView) view.findViewById(R.id.tvDate);
        tvDate.setText(getDateString(mCal));

        llPayee         = (LinearLayout) view.findViewById(R.id.llPayee);
        llPayee.setOnClickListener(this);
        tvPayee         = (TextView) view.findViewById(R.id.tvPayee);

        llEvent         = (LinearLayout) view.findViewById(R.id.llEvent);
        llEvent.setOnClickListener(this);
        tvEvent         = (TextView) view.findViewById(R.id.tvEvent);

        // This block using for Create Debt's transaction
        if(mTransaction.getId() == 0 && !mTransaction.getPayee().equals("")) {
            tvPeople.setText(mTransaction.getPayee());
            mTransaction.setPayee("");
        } else {
            tvPeople.setText(mDbHelper.getDebtByTransactionId(mTransaction.getId()) != null ? mDbHelper.getDebtByTransactionId(mTransaction.getId()).getPeople() : "");
        }
        // Setup View by Data from Transaction
        etAmount.setText(Currency.formatCurrencyDouble(mConfigs.getInt(Configurations.Key.Currency), mTransaction.getAmount()));
        tvCurrencyIcon.setText(Currency.getDefaultCurrencyIcon(getContext()));
        tvDescription.setText(mTransaction.getDescription());
        tvAccount.setText(mAccount != null ? mAccount.getName() : "");
        tvPayee.setText(mTransaction.getPayee());
        tvEvent.setText(mTransaction.getEvent() != null ? mTransaction.getEvent().getName() : "");

        // Show/Hide View follow Category's Type
        if(mCategory == null) {
            llPeople.setVisibility(View.GONE);
            llEvent.setVisibility(View.VISIBLE);
            llPayee.setVisibility(View.VISIBLE);
        } else {
            onCategorySelected(mCategory.getId());
        }

        LogUtils.logLeaveFunction(Tag, null, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mActivity   = (ActivityMain) getActivity();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llCategory:
                ((ActivityMain) getActivity()).hideKeyboard();
                startFragmentSelectCategory(mCategory != null ? mCategory.getId() : 0);
                break;
            case R.id.llPeople:
                ((ActivityMain) getActivity()).hideKeyboard();
                startFragmentLenderBorrower(tvPeople.getText().toString());
                break;
            case R.id.llDescription:
                ((ActivityMain) getActivity()).hideKeyboard();
                startFragmentDescription(tvDescription.getText().toString());
                break;
            case R.id.llAccount:
                ((ActivityMain) getActivity()).hideKeyboard();
                startFragmentSelectAccount(TransactionEnum.Expense, mAccount != null ? mAccount.getId() : 0);
                break;
            case R.id.llDate:
                ((ActivityMain) getActivity()).hideKeyboard();
                showDialogTime();
                break;
            case R.id.llPayee:
                ((ActivityMain) getActivity()).hideKeyboard();
                startFragmentPayee(tvPayee.getText().toString());
                break;
            case R.id.llEvent:
                ((ActivityMain) getActivity()).hideKeyboard();
                startFragmentEvent(tvEvent.getText().toString());
                break;
            case R.id.llSave:
                ((ActivityMain) getActivity()).hideKeyboard();
                if(mTransaction.getId() != 0) {
                    updateTransaction();
                } else {
                    createTransaction();
                }
                break;
            case R.id.llDelete:
                ((ActivityMain) getActivity()).hideKeyboard();
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
            mAccount = mDbHelper.getAccount(accountId);
            tvAccount.setText(mAccount.getName());
            tvCurrencyIcon.setText(getResources().getString(Currency.getCurrencyIcon(mAccount.getCurrencyId())));
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
                    String formatted = Currency.formatCurrencyDouble(mAccount != null ?
                                    mAccount.getCurrencyId()
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

        if(mAccount == null) {
            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Account_Empty));
            return;
        }

        if(mCategory == null) {
            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Category_Expense_Empty));
            return;
        }

        Double  amount        = Double.parseDouble(etAmount.getText().toString().replaceAll(",", ""));

        if(amount > mDbHelper.getAccountRemainBefore(mAccount.getId(), mCal)) {
            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Amount_Greater_Than_Remain));
            return;
        }

        int     CategoryId    = mCategory != null ? mCategory.getId() : 0;
        String  Description   = tvDescription.getText().toString();
        int     accountId     = mAccount.getId();
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
        if(mCategory.getDebtType() == Category.EnumDebt.LESS) { // Expense -> Repayment
            List<Debt> debts = mDbHelper.getAllDebtByPeople(tvPeople.getText().toString());

            Double borrowed = 0.0, repayment = 0.0;
            for(Debt debt : debts) {
                if(mDbHelper.getCategory(debt.getCategoryId()).isExpense() && mDbHelper.getCategory(debt.getCategoryId()).getDebtType() == Category.EnumDebt.LESS) { // Repayment
                    repayment += debt.getAmount();
                }

                if(!mDbHelper.getCategory(debt.getCategoryId()).isExpense() && mDbHelper.getCategory(debt.getCategoryId()).getDebtType() == Category.EnumDebt.MORE) { // Borrow
                    borrowed += debt.getAmount();
                }
            }

            if(repayment + amount > borrowed) {
                isDebtValid = false;
                ((ActivityMain) getActivity()).showError(getResources().getString(R.string.message_debt_repayment_invalid));
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

                        if(getFragmentManager().getBackStackEntryCount() > 0) {
                            // Return to last fragment
                            getFragmentManager().popBackStackImmediate();
                        }
                    } else {
                        ((ActivityMain) getActivity()).showError(getResources().getString(R.string.message_transaction_create_fail));
                        mDbHelper.deleteTransaction(newTransactionId);
                    }
                } else {
                    ((ActivityMain) getActivity()).showToastSuccessful(getResources().getString(R.string.message_transaction_create_successful));
                    cleanup();

                    if(getFragmentManager().getBackStackEntryCount() > 0) {
                        getFragmentManager().popBackStackImmediate();
                    }
                }

            } else {
                ((ActivityMain) getActivity()).showError(getResources().getString(R.string.message_transaction_create_fail));
            }
        } // End if(isDebtValid)

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

        if (mAccount == null) {
            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Account_Empty));
            return;
        }

        Double  amount          = Double.parseDouble(inputtedAmount);
        if(amount > mDbHelper.getAccountRemainBefore(mAccount.getId(), mCal)) {
            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Amount_Greater_Than_Remain));
            return;
        } else if(amount < 0) {
            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Amount_Invalid));
            return;
        }

        int     categoryId      = mCategory != null ? mCategory.getId() : 0;
        String  description     = tvDescription.getText().toString();
        int     accountId       = mAccount.getId();
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

                Double borrowed = 0.0, repayment = 0.0;
                for(Debt debt : debts) {
                    if(debt.getTransactionId() == mTransaction.getId()) {
                        continue;
                    }
                    if(mDbHelper.getCategory(debt.getCategoryId()).isExpense() && mDbHelper.getCategory(debt.getCategoryId()).getDebtType() == Category.EnumDebt.LESS) { // Repayment
                        repayment += debt.getAmount();
                    }

                    if(!mDbHelper.getCategory(debt.getCategoryId()).isExpense() && mDbHelper.getCategory(debt.getCategoryId()).getDebtType() == Category.EnumDebt.MORE) { // Borrow
                        borrowed += debt.getAmount();
                    }
                }

                if(repayment + amount > borrowed) {
                    isDebtValid = false;
                    ((ActivityMain) getActivity()).showError(getResources().getString(R.string.message_debt_repayment_invalid));
                }

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

        boolean isDebtValid = true;
        if(mDbHelper.getCategory(mTransaction.getCategoryId()).getDebtType() == Category.EnumDebt.MORE) { // Lent
            List<Debt> debts = mDbHelper.getAllDebts();

            Double debtCollect = 0.0, lent = 0.0;
            for(Debt debt : debts) {
                if(!mDbHelper.getCategory(debt.getCategoryId()).isExpense() && mDbHelper.getCategory(debt.getCategoryId()).getDebtType() == Category.EnumDebt.LESS) { // Debt Collected
                    debtCollect += debt.getAmount();
                }

                if(mDbHelper.getCategory(debt.getCategoryId()).isExpense() && mDbHelper.getCategory(debt.getCategoryId()).getDebtType() == Category.EnumDebt.MORE) { // Lent
                    lent += debt.getAmount();
                }
            }

            if(lent - mTransaction.getAmount() < debtCollect) {
                isDebtValid = false;
                ((ActivityMain) getActivity()).showError(getResources().getString(R.string.message_debt_delete_invalid));
            }
        }

        if(isDebtValid) {
            mDbHelper.deleteTransaction(mTransaction.getId());
            if(mDbHelper.getDebtByTransactionId(mTransaction.getId()) != null) {
                mDbHelper.deleteDebt(mDbHelper.getDebtByTransactionId(mTransaction.getId()).getId());
            }

            cleanup();

            // Return to FragmentListTransaction
            getFragmentManager().popBackStackImmediate();
            LogUtils.logLeaveFunction(Tag, null, null);
        }
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
        bundle.putInt("Tab", mTab);
        bundle.putBoolean("CategoryType", true);
        bundle.putInt("CategoryID", oldCategoryId);
        bundle.putSerializable("Callback", this);
        nextFrag.setArguments(bundle);
        mActivity.addFragment(mTab, mContainerViewId, nextFrag, "FragmentTransactionSelectCategory", true);

        LogUtils.logLeaveFunction(Tag, "OldCategoryId = " + oldCategoryId, null);
    }

    /**
     * Start fragment LenderBorrower
     * @param oldPeople
     */
    private void startFragmentLenderBorrower(String oldPeople) {
        FragmentLenderBorrower nextFrag = new FragmentLenderBorrower();
        Bundle bundle = new Bundle();
        bundle.putInt("Tab", mTab);
        bundle.putSerializable("Category", mCategory);
        bundle.putString("People", oldPeople);
        bundle.putSerializable("Callback", this);
        nextFrag.setArguments(bundle);
        mActivity.addFragment(mTab, mContainerViewId, nextFrag, "FragmentLenderBorrower", true);
    }

    /**
     * Start fragment input description
     * @param oldDescription
     */
    private void startFragmentDescription(String oldDescription) {
        FragmentDescription nextFrag = new FragmentDescription();
        Bundle bundle = new Bundle();
        bundle.putInt("Tab", mTab);
        bundle.putString("Description", oldDescription);
        bundle.putSerializable("Callback", this);
        nextFrag.setArguments(bundle);
        mActivity.addFragment(mTab, mContainerViewId, nextFrag, FragmentDescription.Tag, true);
    }

    /**
     * Start fragment to Select Account
     * @param transactionType
     * @param oldAccountId
     */
    private void startFragmentSelectAccount(TransactionEnum transactionType, int oldAccountId) {
        LogUtils.logEnterFunction(Tag, "TransactionType = " + transactionType.name() + ", oldAccountId = " + oldAccountId);
        FragmentAccountsSelect nextFrag = new FragmentAccountsSelect();
        Bundle bundle = new Bundle();
        bundle.putInt("Tab", mTab);
        bundle.putInt("AccountID", oldAccountId);
        bundle.putSerializable("TransactionType", transactionType);
        bundle.putSerializable("Callback", this);
        nextFrag.setArguments(bundle);
        mActivity.addFragment(mTab, mContainerViewId, nextFrag, FragmentAccountsSelect.Tag, true);

        LogUtils.logLeaveFunction(Tag, "TransactionType = " + transactionType.name() + ", oldAccountId = " + oldAccountId, null);
    }

    /**
     * Start fragment Payee
     * @param oldPayee
     */
    private void startFragmentPayee(String oldPayee) {
        FragmentPayee nextFrag = new FragmentPayee();
        Bundle bundle = new Bundle();
        bundle.putInt("Tab", mTab);
        bundle.putString("Payee", oldPayee);
        bundle.putSerializable("Callback", this);
        nextFrag.setArguments(bundle);
        mActivity.addFragment(mTab, mContainerViewId, nextFrag, "FragmentPayee", true);
    }

    /**
     * Start fragment Event
     * @param oldEvent
     */
    private void startFragmentEvent(String oldEvent) {
        FragmentEvent nextFrag = new FragmentEvent();
        Bundle bundle = new Bundle();
        bundle.putInt("Tab", mTab);
        bundle.putString("Event", oldEvent);
        bundle.putInt("ContainerViewId", mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update);
        bundle.putSerializable("Callback", this);
        nextFrag.setArguments(bundle);
        mActivity.addFragment(mTab, mContainerViewId, nextFrag, "FragmentEvent", true);
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
                && getResources().getConfiguration().locale.equals(new Locale("vn"))) {
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
        mAccount = null;
        mCal                        = Calendar.getInstance();

        /* Reset value */
        etAmount.setText("");
        tvCategory.setText("");
        tvDescription.setText("");
        tvAccount.setText("");
        tvPayee.setText("");
        tvEvent.setText("");
    }
}
