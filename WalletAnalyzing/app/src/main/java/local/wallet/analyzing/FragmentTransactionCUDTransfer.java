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
import java.util.Locale;

import local.wallet.analyzing.FragmentAccountsSelect.ISelectAccount;
import local.wallet.analyzing.FragmentDescription.IUpdateDescription;
import local.wallet.analyzing.FragmentEvent.IUpdateEvent;
import local.wallet.analyzing.FragmentTransactionSelectCategory.ISelectCategory;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Event;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.model.Transaction.TransactionEnum;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 03/24/2016.
 */
public class FragmentTransactionCUDTransfer extends Fragment implements View.OnClickListener, ISelectCategory, ISelectAccount, IUpdateDescription {
    public static final String Tag = "TransactionCUDTransfer";

    private Configurations      mConfigs;
    private DatabaseHelper      mDbHelper;

    private Category            mCategory;
    private Account             mFromAccount;
    private Account             mToAccount;

    private ClearableEditText   etAmount;
    private TextView            tvCurrencyIcon;
    private LinearLayout        llFromAccount;
    private TextView            tvFromAccount;
    private LinearLayout        llToAccount;
    private TextView            tvToAccount;
    private LinearLayout        llDescription;
    private TextView            tvDescription;
    private LinearLayout        llDate;
    private TextView            tvDate;
    private ClearableEditText   etFee;
    private TextView            tvFeeCurrencyIcon;
    private LinearLayout        llCategory;
    private TextView            tvCategory;

    private LinearLayout        llSave;
    private LinearLayout        llDelete;

    private Calendar            mCal;

    private Transaction         mTransaction;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_transaction_cud_transfer, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mTransaction = (Transaction) bundle.get("Transaction");

            LogUtils.trace(Tag, "mTransaction = " + mTransaction.toString());
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onActivityCreated(savedInstanceState);

        mConfigs = new Configurations(getActivity());
        mDbHelper = new DatabaseHelper(getActivity());

        llSave = (LinearLayout) getView().findViewById(R.id.llSave);
        llSave.setOnClickListener(this);
        llDelete = (LinearLayout) getView().findViewById(R.id.llDelete);
        llDelete.setOnClickListener(this);

        if (mTransaction.getId() == 0) {
            llDelete.setVisibility(View.GONE);
        }

        mCategory       = mDbHelper.getCategory(mTransaction.getCategoryId());
        mFromAccount    = mTransaction.getFromAccountId() != 0 ? mDbHelper.getAccount(mTransaction.getFromAccountId()) : null;
        mToAccount      = mTransaction.getToAccountId() != 0 ? mDbHelper.getAccount(mTransaction.getToAccountId()) : null;
        mCal            = mTransaction.getTime();

        initView();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llFromAccount:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentSelectAccount(TransactionEnum.TransferFrom, mToAccount != null ? mToAccount.getId() : 0);
                break;
            case R.id.llToAccount:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentSelectAccount(TransactionEnum.TransferTo, mToAccount != null ? mToAccount.getId() : 0);
                break;
            case R.id.llDescription:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentDescription(tvDescription.getText().toString());
                break;
            case R.id.llDate:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                showDialogTime();
                break;
            case R.id.llCategory:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                startFragmentSelectCategory(mCategory != null ? mCategory.getId() : 0);
                break;
            case R.id.llSave:
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                if (mTransaction.getId() != 0) {
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

    @Override
    public void onCategorySelected(int categoryId) {
        LogUtils.logEnterFunction(Tag, "categoryId = " + categoryId);

        mCategory = mDbHelper.getCategory(categoryId);

        if(mCategory != null) {
            tvCategory.setText(mCategory.getName());
        } else {
            tvCategory.setText("");
        }

        LogUtils.logLeaveFunction(Tag, "categoryId = " + categoryId, null);
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

        if (type == TransactionEnum.TransferFrom) {
            mFromAccount = mDbHelper.getAccount(accountId);
            tvFromAccount.setText(mFromAccount.getName());
            tvCurrencyIcon.setText(getResources().getString(Currency.getCurrencyIcon(mFromAccount.getCurrencyId())));
        } else if(type == TransactionEnum.TransferTo) {
            mToAccount = mDbHelper.getAccount(accountId);
            tvToAccount.setText(mToAccount.getName());
            tvCurrencyIcon.setText(getResources().getString(Currency.getCurrencyIcon(mToAccount.getCurrencyId())));
            tvFeeCurrencyIcon.setText(getResources().getString(Currency.getCurrencyIcon(mToAccount.getCurrencyId())));
        }

        LogUtils.logLeaveFunction(Tag, "TransactionType = " + type.name() + ", accountId = " + accountId, null);
    }

    /**
     * Todo: Init view
     */
    private void initView() {
        LogUtils.logEnterFunction(Tag, null);

        etAmount            = (ClearableEditText) getView().findViewById(R.id.etAmount);
        etAmount.addTextChangedListener(new CurrencyTextWatcher(etAmount));
        tvCurrencyIcon      = (TextView) getView().findViewById(R.id.tvCurrencyIcon);

        llFromAccount       = (LinearLayout) getView().findViewById(R.id.llFromAccount);
        llFromAccount.setOnClickListener(this);
        tvFromAccount       = (TextView) getView().findViewById(R.id.tvFromAccount);

        llToAccount         = (LinearLayout) getView().findViewById(R.id.llToAccount);
        llToAccount.setOnClickListener(this);
        tvToAccount         = (TextView) getView().findViewById(R.id.tvToAccount);

        llDescription       = (LinearLayout) getView().findViewById(R.id.llDescription);
        llDescription.setOnClickListener(this);
        tvDescription       = (TextView) getView().findViewById(R.id.tvDescription);

        llDate = (LinearLayout) getView().findViewById(R.id.llDate);
        llDate.setOnClickListener(this);
        tvDate = (TextView) getView().findViewById(R.id.tvDate);

        etFee               = (ClearableEditText) getView().findViewById(R.id.etFee);
        etFee.addTextChangedListener(new CurrencyTextWatcher(etFee));
        tvFeeCurrencyIcon   = (TextView) getView().findViewById(R.id.tvFeeCurrencyIcon);

        llCategory          = (LinearLayout) getView().findViewById(R.id.llCategory);
        llCategory.setOnClickListener(this);
        tvCategory          = (TextView) getView().findViewById(R.id.tvCategory);

        etAmount.setText(Currency.formatCurrencyDouble(mConfigs.getInt(Configurations.Key.Currency), mTransaction.getAmount()));
        tvCurrencyIcon.setText(Currency.getCurrencyIcon(mConfigs.getInt(Configurations.Key.Currency)));
        tvFromAccount.setText(mFromAccount != null ? mFromAccount.getName() : "");
        tvToAccount.setText(mToAccount != null ? mToAccount.getName() : "");
        tvDescription.setText(mTransaction.getDescription());
        tvDate.setText(getDateString(mCal));
        tvCategory.setText(mCategory != null ? mCategory.getName() : "");

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

        public synchronized void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LogUtils.logEnterFunction(Tag, null);

            if (!s.toString().equals(current)) {
                mEdittext.removeTextChangedListener(this);

                String inputted = s.toString().replaceAll(",", "").replaceAll(" ", "");
                if (!inputted.equals("")) {
                    String formatted = Currency.formatCurrencyDouble(mToAccount != null ?
                                    mToAccount.getCurrencyId()
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

        String inputtedAmount = etAmount.getText().toString().trim().replaceAll(",", "");

        if (inputtedAmount.equals("") || Double.parseDouble(inputtedAmount) == 0) {
            etAmount.setError(getResources().getString(R.string.Input_Error_Amount_Empty));
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

        if(mFromAccount.getId() == mToAccount.getId()) {
            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Transfer_Same_Account));
            return;
        }

        Double  amount          = Double.parseDouble(etAmount.getText().toString().replaceAll(",", ""));
        int     fromAccountId   = mFromAccount.getId();
        int     toAccountId     = mToAccount.getId();
        String  description     = tvDescription.getText().toString();
        Double  fee             = !etFee.getText().toString().equals("") ? Double.parseDouble(etFee.getText().toString().replaceAll(",", "")) : 0.0;
        int     categoryId      = mCategory != null ? mCategory.getId() : 0;

        Transaction transaction     = new Transaction(0,
                                                        TransactionEnum.Transfer.getValue(),
                                                        amount,
                                                        categoryId,
                                                        description,
                                                        fromAccountId,
                                                        toAccountId,
                                                        mCal,
                                                        fee,
                                                        "",
                                                        null);

        long newTransactionId = mDbHelper.createTransaction(transaction);

        if (newTransactionId != -1) {
            ((ActivityMain) getActivity()).showToastSuccessful(getResources().getString(R.string.message_transaction_create_successful));
            cleanup();
        }

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

        if(mFromAccount == null) {
            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Account_Empty));
            return;
        }

        if(mToAccount == null) {
            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_ToAccount_Empty));
            return;
        }

        if(mFromAccount.getId() == mToAccount.getId()) {
            ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_Transfer_Same_Account));
            return;
        }

        Double  amount          = Double.parseDouble(etAmount.getText().toString().replaceAll(",", ""));
        int     fromAccountId   = mFromAccount.getId();
        int     toAccountId     = mToAccount.getId();
        String  description     = tvDescription.getText().toString();
        Double  fee             = !etFee.getText().toString().equals("") ? Double.parseDouble(etFee.getText().toString().replaceAll(",", "")) : 0.0;
        int     categoryId      = mCategory != null ? mCategory.getId() : 0;

        Transaction transaction = new Transaction(mTransaction.getId(),
                                                TransactionEnum.Transfer.getValue(),
                                                amount,
                                                categoryId,
                                                description,
                                                fromAccountId,
                                                toAccountId,
                                                mCal,
                                                fee,
                                                "",
                                                null);

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
        FragmentTransactionCUDTransfer.this.getFragmentManager().beginTransaction()
                .add(mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update, nextFrag, FragmentTransactionSelectCategory.Tag)
                .addToBackStack(null)
                .commit();
        LogUtils.logLeaveFunction(Tag, "OldCategoryId = " + oldCategoryId, null);
    }

    /**
     * Start fragment input description
     *
     * @param oldDescription
     */
    private void startFragmentDescription(String oldDescription) {
        FragmentDescription fragmentDescription = new FragmentDescription();
        Bundle bundle = new Bundle();
        bundle.putString("Description", oldDescription);
        bundle.putSerializable("Callback", this);
        fragmentDescription.setArguments(bundle);
        FragmentTransactionCUDTransfer.this.getFragmentManager().beginTransaction()
                .add(mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update, fragmentDescription, FragmentDescription.Tag)
                .addToBackStack(Tag)
                .commit();
    }

    /**
     * Start fragment to Select Account
     *
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
        FragmentTransactionCUDTransfer.this.getFragmentManager().beginTransaction()
                .add(mTransaction.getId() == 0 ? R.id.ll_transaction_create : R.id.ll_transaction_update, fragment, FragmentAccountsSelect.Tag)
                .addToBackStack(null)
                .commit();

        LogUtils.logLeaveFunction(Tag, "TransactionType = " + transactionType.name() + ", oldAccountId = " + oldAccountId, null);
    }

    /**
     * Get Date's String
     *
     * @return
     */
    private String getDateString(Calendar cal) {
        Calendar current = Calendar.getInstance();
        String date = "";
        if (cal.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)) {
            date = getResources().getString(R.string.content_today) + String.format(" %02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        } else if ((cal.get(Calendar.DAY_OF_YEAR) - 1) == current.get(Calendar.DAY_OF_YEAR)) {
            date = getResources().getString(R.string.content_yesterday) + String.format(" %02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        } else if ((cal.get(Calendar.DAY_OF_YEAR) - 2) == current.get(Calendar.DAY_OF_YEAR)
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
        mCategory       = null;
        mFromAccount    = null;
        mToAccount      = null;
        mCal            = null;

        /* Reset value */
        etAmount.setText("");
        tvFromAccount.setText("");
        tvToAccount.setText("");
        tvDescription.setText("");
        tvDate.setText("");
        tvCategory.setText("");
    }
}
