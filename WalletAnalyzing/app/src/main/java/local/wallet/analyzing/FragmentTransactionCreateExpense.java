package local.wallet.analyzing;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Parcel;
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
import local.wallet.analyzing.model.Category.EnumDebt;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Event;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.model.Transaction.TransactionEnum;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentTransactionCreateExpense extends Fragment implements  View.OnClickListener, FragmentTransactionCreateHost.OnSetupData {
    public static final String Tag = "TransactionCreateExpense";

    private Configurations      mConfigs;
    private DatabaseHelper      mDbHelper;

    private Category            mCategory;
    private Account             mFromAccount;

    private ClearableEditText   etAmount;
    private TextView            tvCurrencyIcon;
    private LinearLayout        llCategory;
    private TextView            tvCategory;
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
        return inflater.inflate(R.layout.layout_fragment_transaction_create_expense, container, false);
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

        initViewExpense();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llCategory:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentSelectCategory(TransactionEnum.Expense, mCategory != null ? mCategory.getId() : 0);
                break;
            case R.id.llDescription:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentDescription(TransactionEnum.Expense, tvDescription.getText().toString());
                break;
            case R.id.llAccount:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentSelectAccount(TransactionEnum.Expense, mFromAccount != null ? mFromAccount.getId() : 0);
                break;
            case R.id.llDate:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                showDialogTime();
            case R.id.llPayee:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentPayee(TransactionEnum.Expense, tvPayee.getText().toString());
                break;
            case R.id.llEvent:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentEvent(TransactionEnum.Expense, tvEvent.getText().toString());
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
                mDbHelper.deleteTransaction(mTransaction.getId());

                cleanup();

                // Return to FragmentListTransaction
                getFragmentManager().popBackStackImmediate();
                break;
            default:
                break;
        }
    }

    /**
     * Todo: Init view EXPENSE
     */
    private void initViewExpense() {
        LogUtils.logEnterFunction(Tag, null);

        etAmount         = (ClearableEditText) getView().findViewById(R.id.etAmount);
        etAmount.addTextChangedListener(new CurrencyTextWatcher(etAmount));
        tvCurrencyIcon   = (TextView) getView().findViewById(R.id.tvCurrencyIcon);
        tvCurrencyIcon.setText(Currency.getCurrencyIcon(mConfigs.getInt(Configurations.Key.Currency)));

        llCategory       = (LinearLayout) getView().findViewById(R.id.llCategory);
        llCategory.setOnClickListener(this);
        tvCategory       = (TextView) getView().findViewById(R.id.tvCategory);

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
        etAmount.setText(Currency.formatCurrencyDouble(mConfigs.getInt(Configurations.Key.Currency), mTransaction.getAmount()));
        tvDescription.setText(mTransaction.getDescription());
        tvPayee.setText(mTransaction.getPayee());
        tvEvent.setText(mTransaction.getEvent() != null ? mTransaction.getEvent().getName() : "");

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
        Transaction transaction = new Transaction();

        String inputtedAmount = etAmount.getText().toString().trim().replaceAll(",", "");

        if (inputtedAmount.equals("") || Double.parseDouble(inputtedAmount) == 0) {
            etAmount.setError(getResources().getString(R.string.Input_Error_Amount_Empty));
            return;
        }

        if(mFromAccount == null) {
            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Account_Empty));
            return;
        }

        Double  Amount        = Double.parseDouble(etAmount.getText().toString().replaceAll(",", ""));
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

        transaction = new Transaction(0,
                                        TransactionEnum.Expense.getValue(),
                                        Amount,
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
            ((ActivityMain) getActivity()).showToastSuccessful(getResources().getString(R.string.message_transaction_create_successful));
            cleanup();
        }

    } // End createTransaction

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

        Transaction transaction     = new Transaction(mTransaction.getId(),
                                                        TransactionEnum.Expense.getValue(),
                                                        amount,
                                                        categoryId,
                                                        description,
                                                        accountId,
                                                        0,
                                                        mCal,
                                                        0.0,
                                                        strEvent,
                                                        event);

        int row = mDbHelper.updateTransaction(transaction);
        if (row == 1) {
            ((ActivityMain) getActivity()).showToastSuccessful(getResources().getString(R.string.message_transaction_update_successful));
            cleanup();
        }

        LogUtils.logLeaveFunction(Tag, null, null);
        // Return to last fragment
        getFragmentManager().popBackStackImmediate();
    } // End Update Transaction

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
     * @param transactionType
     * @param oldCategoryId
     */
    private void startFragmentSelectCategory(TransactionEnum transactionType, int oldCategoryId) {
        FragmentTransactionSelectCategory nextFrag = new FragmentTransactionSelectCategory();
        Bundle bundle = new Bundle();
        bundle.putString("Tag", Tag);
        bundle.putInt("CategoryID", oldCategoryId);
        bundle.putSerializable("TransactionType", transactionType);
        bundle.putSerializable("Callback", this);
        nextFrag.setArguments(bundle);
        FragmentTransactionCreateExpense.this.getFragmentManager().beginTransaction()
                .add(mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update, nextFrag, FragmentTransactionSelectCategory.Tag)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Update Category, call from ActivityMain
     * @param categoryId
     */
    @Override
    public void updateCategory(TransactionEnum type, int categoryId) {
        LogUtils.logEnterFunction(Tag, "TransactionType = " + type.name() + ",  categoryId = " + categoryId);

        mCategory = mDbHelper.getCategory(categoryId);

        switch (type) {
            case Expense:

                String amount = etAmount.getText().toString().replaceAll(",", "");

                mTransaction.setAmount(amount.equals("") ? 0 : Double.parseDouble(amount));
                mTransaction.setCategoryId(categoryId);
                mTransaction.setDescription(tvDescription.getText().toString());
                mTransaction.setFromAccountId(mFromAccount != null ? mFromAccount.getId() : 0);

                Bundle bundle = new Bundle();
                bundle.putSerializable("Transaction", mTransaction);

                if(mCategory.getDebtType() == EnumDebt.MORE) {
                    FragmentTransactionCreateExpenseLend nextFrag = new FragmentTransactionCreateExpenseLend();
                    nextFrag.setArguments(bundle);
                    FragmentTransactionCreateExpense.this.getFragmentManager().beginTransaction()
                            .replace(mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update, nextFrag, FragmentTransactionCreateExpenseLend.Tag)
                            .commit();
                } else if(mCategory.getDebtType() == EnumDebt.NONE) {
                    tvCategory.setText(mCategory.getName());
                } else if(mCategory.getDebtType() == EnumDebt.LESS) {
                    FragmentTransactionCreateExpenseRepayment nextFrag = new FragmentTransactionCreateExpenseRepayment();
                    nextFrag.setArguments(bundle);
                    FragmentTransactionCreateExpense.this.getFragmentManager().beginTransaction()
                            .replace(mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update, nextFrag, FragmentTransactionCreateExpenseRepayment.Tag)
                            .commit();
                }
                break;
            default:
                break;
        }

        LogUtils.logLeaveFunction(Tag, "TransactionType = " + type.name() + ", categoryId = " + categoryId, null);
    }

    /**
     * Start fragment input description
     * @param transactionType
     * @param oldDescription
     */
    private void startFragmentDescription(TransactionEnum transactionType, String oldDescription) {
        FragmentDescription fragmentDescription = new FragmentDescription();
        Bundle bundleExpenseDescription = new Bundle();
        bundleExpenseDescription.putString("Tag", Tag);
        bundleExpenseDescription.putSerializable("TransactionType", transactionType);
        bundleExpenseDescription.putString("Description", oldDescription);
        fragmentDescription.setArguments(bundleExpenseDescription);
        FragmentTransactionCreateExpense.this.getFragmentManager().beginTransaction()
                .add(mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update, fragmentDescription, FragmentDescription.Tag)
                .addToBackStack(Tag)
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
                tvDescription.setText(description);
                break;
            default:
                break;
        }


        LogUtils.logLeaveFunction(Tag, "TransactionType = " + type.name() + ", description = " + description, null);
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
        bundle.putString("Tag", Tag);
        bundle.putInt("AccountID", oldAccountId);
        bundle.putSerializable("TransactionType", transactionType);
        fragment.setArguments(bundle);
        FragmentTransactionCreateExpense.this.getFragmentManager().beginTransaction()
                .add(mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update, fragment, FragmentAccountsSelect.Tag)
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
                tvAccount.setText(mFromAccount.getName());
                tvCurrencyIcon.setText(getResources().getString(Currency.getCurrencyIcon(mFromAccount.getCurrencyId())));
                break;
            default:
                break;
        }

        LogUtils.logLeaveFunction(Tag, "TransactionType = " + type.name() + ", accountId = " + accountId, null);
    }

    /**
     * Start fragment Payee
     * @param transactionType
     * @param oldPayee
     */
    private void startFragmentPayee(TransactionEnum transactionType, String oldPayee) {
        FragmentPayee fragmentExpensePayee = new FragmentPayee();
        Bundle bundleExpensePayee = new Bundle();
        bundleExpensePayee.putString("Tag", Tag);
        bundleExpensePayee.putSerializable("TransactionType", transactionType);
        bundleExpensePayee.putString("Payee", oldPayee);
        fragmentExpensePayee.setArguments(bundleExpensePayee);
        FragmentTransactionCreateExpense.this.getFragmentManager().beginTransaction()
                .add(mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update, fragmentExpensePayee, "FragmentPayee")
                .addToBackStack(Tag)
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
                tvPayee.setText(payee);
                break;
            default:
                break;
        }


        LogUtils.logLeaveFunction(Tag, "TransactionType = " + type.name() + ", payee = " + payee, null);
    }

    /**
     * Start fragment Event
     * @param transactionType
     * @param oldEvent
     */
    private void startFragmentEvent(TransactionEnum transactionType, String oldEvent) {
        FragmentEvent fragmentExpenseEvent = new FragmentEvent();
        Bundle bundleExpenseEvent = new Bundle();
        bundleExpenseEvent.putString("Tag", Tag);
        bundleExpenseEvent.putSerializable("TransactionType", transactionType);
        bundleExpenseEvent.putString("Event", oldEvent);
        fragmentExpenseEvent.setArguments(bundleExpenseEvent);
        FragmentTransactionCreateExpense.this.getFragmentManager().beginTransaction()
                .add(mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update, fragmentExpenseEvent, "FragmentEvent")
                .addToBackStack(Tag)
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
                tvEvent.setText(event);
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
