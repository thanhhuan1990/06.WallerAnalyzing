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
import java.util.Locale;

import local.wallet.analyzing.R;
import local.wallet.analyzing.account.FragmentAccountsSelect;
import local.wallet.analyzing.account.FragmentAccountsSelect.ISelectAccount;
import local.wallet.analyzing.transaction.FragmentDescription.IUpdateDescription;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.main.Configurations;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.model.Transaction.TransactionEnum;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 03/24/2016.
 */
public class FragmentTransactionCUDTransfer extends Fragment implements View.OnClickListener, FragmentTransactionSelectCategory.ISelectCategory, ISelectAccount, IUpdateDescription {
    private int                 mTab = 1;
    private String              Tag = "---[" + mTab + "]---TransactionCUDTransfer";

    private ActivityMain        mActivity;

    private int                 mContainerViewId = -1;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);

        mConfigs = new Configurations(getActivity());
        mDbHelper = new DatabaseHelper(getActivity());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mTab = bundle.getInt("Tab", mTab);
            Tag = "---[" + mTab + ".3]---" + "FragmentTransactionCUD";

            mTransaction = (Transaction) bundle.get("Transaction");
            LogUtils.trace(Tag, "mTransaction = " + mTransaction.toString());

            // Retry ContainerViewId from bundle
            if(bundle.getInt("ContainerViewId") != 0) {
                mContainerViewId = bundle.getInt("ContainerViewId");
            } else {
                mContainerViewId    = mTransaction.getId() != 0 ? R.id.ll_transaction_update : R.id.ll_transaction_create;
            }

            mCategory       = mDbHelper.getCategory(mTransaction.getCategoryId());

            if(mTransaction.getFromAccountId() != 0) {
                mFromAccount    = mDbHelper.getAccount(mTransaction.getFromAccountId());
            } else if(mDbHelper.getAllAccounts().size() > 0){
                mFromAccount    = mDbHelper.getAllAccounts().get(0);
            }

            if(mTransaction.getToAccountId() != 0) {
                mToAccount  = mDbHelper.getAccount(mTransaction.getToAccountId());
            } else if(mDbHelper.getAllAccounts().size() > 1){
                mFromAccount    = mDbHelper.getAllAccounts().get(1);
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

        View view           = inflater.inflate(R.layout.layout_fragment_transaction_cud_transfer, container, false);

        llSave              = (LinearLayout) view.findViewById(R.id.llSave);
        llSave.setOnClickListener(this);
        llDelete            = (LinearLayout) view.findViewById(R.id.llDelete);
        llDelete.setOnClickListener(this);

        if (mTransaction.getId() == 0) {
            llDelete.setVisibility(View.GONE);
        }

        etAmount            = (ClearableEditText) view.findViewById(R.id.etAmount);
        etAmount.addTextChangedListener(new CurrencyTextWatcher(etAmount));
        tvCurrencyIcon      = (TextView) view.findViewById(R.id.tvCurrencyIcon);

        llFromAccount       = (LinearLayout) view.findViewById(R.id.llFromAccount);
        llFromAccount.setOnClickListener(this);
        tvFromAccount       = (TextView) view.findViewById(R.id.tvFromAccount);

        llToAccount         = (LinearLayout) view.findViewById(R.id.llToAccount);
        llToAccount.setOnClickListener(this);
        tvToAccount         = (TextView) view.findViewById(R.id.tvToAccount);

        llDescription       = (LinearLayout) view.findViewById(R.id.llDescription);
        llDescription.setOnClickListener(this);
        tvDescription       = (TextView) view.findViewById(R.id.tvDescription);

        llDate              = (LinearLayout) view.findViewById(R.id.llDate);
        llDate.setOnClickListener(this);
        tvDate              = (TextView) view.findViewById(R.id.tvDate);

        etFee               = (ClearableEditText) view.findViewById(R.id.etFee);
        etFee.addTextChangedListener(new CurrencyTextWatcher(etFee));
        tvFeeCurrencyIcon   = (TextView) view.findViewById(R.id.tvFeeCurrencyIcon);

        llCategory          = (LinearLayout) view.findViewById(R.id.llCategory);
        llCategory.setOnClickListener(this);
        tvCategory          = (TextView) view.findViewById(R.id.tvCategory);

        // Setup View by Data from Transaction
        etAmount.setText(Currency.formatCurrencyDouble(mConfigs.getInt(Configurations.Key.Currency), mTransaction.getAmount()));
        tvCurrencyIcon.setText(Currency.getDefaultCurrencyIcon(getContext()));
        tvFromAccount.setText(mFromAccount != null ? mFromAccount.getName() : "");
        tvToAccount.setText(mToAccount != null ? mToAccount.getName() : "");
        tvDescription.setText(mTransaction.getDescription());
        tvDate.setText(getDateString(mCal));
        tvCategory.setText(mCategory != null ? mCategory.getName() : "");

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
            case R.id.llFromAccount:
                ((ActivityMain) getActivity()).hideKeyboard();
                startFragmentSelectAccount(TransactionEnum.TransferFrom, mToAccount != null ? mToAccount.getId() : 0);
                break;
            case R.id.llToAccount:
                ((ActivityMain) getActivity()).hideKeyboard();
                startFragmentSelectAccount(TransactionEnum.TransferTo, mToAccount != null ? mToAccount.getId() : 0);
                break;
            case R.id.llDescription:
                ((ActivityMain) getActivity()).hideKeyboard();
                startFragmentDescription(tvDescription.getText().toString());
                break;
            case R.id.llDate:
                ((ActivityMain) getActivity()).hideKeyboard();
                showDialogTime();
                break;
            case R.id.llCategory:
                ((ActivityMain) getActivity()).hideKeyboard();
                startFragmentSelectCategory(mCategory != null ? mCategory.getId() : 0);
                break;
            case R.id.llSave:
                ((ActivityMain) getActivity()).hideKeyboard();
                if (mTransaction.getId() != 0) {
                    updateTransaction();
                } else {
                    createTransaction();
                }
                break;
            case R.id.llDelete:
                ((ActivityMain) getActivity()).hideKeyboard();
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
        bundle.putInt("Tab", mTab);
        bundle.putBoolean("CategoryType", true);
        bundle.putInt("CategoryID", oldCategoryId);
        bundle.putSerializable("Callback", this);
        nextFrag.setArguments(bundle);
        mActivity.addFragment(mTab, mContainerViewId, nextFrag, "FragmentTransactionSelectCategory", true);

        LogUtils.logLeaveFunction(Tag, "OldCategoryId = " + oldCategoryId, null);
    }

    /**
     * Start fragment input description
     *
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
     *
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
