package local.wallet.analyzing;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentNewTransaction extends Fragment implements  View.OnClickListener {
    private static final String Tag = "FragmentNewTransaction";

    private DatabaseHelper db;

    private Category    mCategory;
    private Account     mAccount;
    private Spinner spTransactionType;

    /* Layout Expense */
    private LinearLayout    llExpense;
    private EditText        etExpenseAmount;
    private ImageView       ivExpenseCurrencyIcon;
    private LinearLayout    llExpenseCategory;
    private TextView        tvExpenseCategory;
    private LinearLayout    llExpenseDescription;
    private TextView        tvExpenseDescription;
    private LinearLayout    llExpenseAccount;
    private TextView        tvExpenseAccount;
    private LinearLayout    llExpenseDate;
    private TextView        tvExpenseDate;
    private LinearLayout    llExpensePayee;
    private TextView        tvExpensePayee;
    private LinearLayout    llExpenseEvent;
    private TextView        tvExpenseEvent;

    /* Layout Income */
    private LinearLayout    llIncome;
    private EditText        etIncomeAmount;
    private ImageView       ivIncomeCurrencyIcon;
    private LinearLayout    llIncomeCategory;
    private TextView        tvIncomeCategory;
    private LinearLayout    llIncomeDescription;
    private TextView        tvIncomeDescription;
    private LinearLayout    llToAccount;
    private TextView        tvToAccount;
    private LinearLayout    llIncomeDate;
    private TextView        tvIncomeDate;
    private LinearLayout    llIncomeEvent;
    private TextView        tvIncomeEvent;

    /* Layout Transfer */
    private LinearLayout    llTransfer;
    private EditText        etTransferAmount;
    private ImageView       ivTransferCurrencyIcon;
    private LinearLayout    llTransferFromAccount;
    private TextView        tvTransferFromAccount;
    private LinearLayout    llTransferToAccount;
    private TextView        tvTransferToAccount;
    private LinearLayout    llTransferDescription;
    private TextView        tvTransferDescription;
    private LinearLayout    llTransferDate;
    private TextView        tvTransferDate;
    private EditText        etTransferFee;
    private ImageView       ivTransferFeeCurrencyIcon;
    private LinearLayout    llTransferCategory;
    private TextView        tvTransferCategory;

    /* Adjustment */
    private LinearLayout    llAdjustment;
    private LinearLayout    llAdjustmentAccount;
    private TextView        tvAdjustmentAccount;
    private EditText        etAdjustmentBalance;
    private ImageView       ivAdjustmentCurrencyIcon;
    private TextView        tvAdjustmentSpent;
    private LinearLayout    llAdjustmentCategory;
    private TextView        tvAdjustmentCategory;
    private LinearLayout    llAdjustmentDescription;
    private TextView        tvAdjustmentDescription;
    private LinearLayout    llAdjustmentDate;
    private TextView        tvAdjustmentDate;
    private LinearLayout    llAdjustmentPayee;
    private TextView        tvAdjustmentPayee;
    private LinearLayout    llAdjustmentEvent;
    private TextView        tvAdjustmentEvent;

    private LinearLayout    llSave;

    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreateOptionsMenu(menu, inflater);

        initActionBar();

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

        // Get Current DateTime
        final Calendar c = Calendar.getInstance();
        mYear   = c.get(Calendar.YEAR);
        mMonth  = c.get(Calendar.MONTH);
        mDay    = c.get(Calendar.DAY_OF_MONTH);
        mHour   = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        llSave      = (LinearLayout) getView().findViewById(R.id.llSave);
        llSave.setOnClickListener(this);
        initViewExpense();
        initViewIncome();
        initViewTransfer();
        initViewAdjustment();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llExpenseCategory:
                FragmentNewTransactionSelectCategory nextFrag = new FragmentNewTransactionSelectCategory();
                Bundle bundleSelectCategory = new Bundle();
                bundleSelectCategory.putInt("TransactionType", spTransactionType.getSelectedItemPosition());
                bundleSelectCategory.putInt("CategoryID", mCategory != null ? mCategory.getId() : 0);
                nextFrag.setArguments(bundleSelectCategory);
                FragmentNewTransaction.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_new_transaction, nextFrag, "FragmentNewTransactionSelectCategory")
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.llExpenseDescription:
                FragmentDescription fragmentDescription = new FragmentDescription();
                Bundle bundleExpenseDescription = new Bundle();
                bundleExpenseDescription.putString("Description", tvExpenseDescription.getText().toString());
                fragmentDescription.setArguments(bundleExpenseDescription);
                FragmentNewTransaction.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_new_transaction, fragmentDescription, "FragmentDescription")
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.llExpenseAccount:
                FragmentNewTransactionSelectAccount fragmentAccount = new FragmentNewTransactionSelectAccount();
                Bundle bundleExpenseAccount = new Bundle();
                bundleExpenseAccount.putInt("AccountID", mAccount != null ? mAccount.getId() : 0);
                fragmentAccount.setArguments(bundleExpenseAccount);
                FragmentNewTransaction.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_new_transaction, fragmentAccount, "FragmentNewTransactionSelectAccount")
                        .addToBackStack(null)
                        .commit();

                break;
            case R.id.llExpenseDate:
                final TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                                                                        new TimePickerDialog.OnTimeSetListener() {

                                                                            @Override
                                                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                                                mHour   = hourOfDay;
                                                                                mMinute = minute;
                                                                                tvExpenseDate.setText(tvExpenseDate.getText().toString() + " "
                                                                                                        + hourOfDay + ":" + String.format("%02d", minute));
                                                                            }
                                                                        }, mHour, mMinute, true);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                                                                        new DatePickerDialog.OnDateSetListener() {

                                                                            @Override
                                                                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                                                mYear   = year;
                                                                                mMonth  = monthOfYear;
                                                                                mDay    = dayOfMonth;
                                                                                tvExpenseDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                                                                timePickerDialog.show();
                                                                            }
                                                                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            case R.id.llExpensePayee:
                FragmentPayee fragmentExpensePayee = new FragmentPayee();
                Bundle bundleExpensePayee = new Bundle();
                bundleExpensePayee.putString("Payee", tvExpensePayee.getText().toString());
                fragmentExpensePayee.setArguments(bundleExpensePayee);
                FragmentNewTransaction.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_new_transaction, fragmentExpensePayee, "FragmentPayee")
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.llExpenseEvent:
                FragmentEvent fragmentExpenseEvent = new FragmentEvent();
                Bundle bundleExpenseEvent = new Bundle();
                bundleExpenseEvent.putString("Event", tvExpenseEvent.getText().toString());
                fragmentExpenseEvent.setArguments(bundleExpenseEvent);
                FragmentNewTransaction.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_new_transaction, fragmentExpenseEvent, "FragmentEvent")
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.llSave:
                switch (spTransactionType.getSelectedItemPosition()) {
                    case 0:
                        Double expenseAmount =  etExpenseAmount.getText().toString().equals("") ? 0 : Double.parseDouble(etExpenseAmount.getText().toString().replaceAll(",", ""));
                        int expenseCategoryId = mCategory.getId();
                        String expenseDescription = tvExpenseDescription.getText().toString();
                        int expenseAccountId    = mAccount.getId();
                        Date expenseDate = getDate(mYear, mMonth, mDay, mHour, mMinute);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(expenseDate);
                        String expensePayee = tvExpensePayee.getText().toString();
                        String expenseEvent = tvExpenseEvent.getText().toString();

                        Transaction transaction = new Transaction(0, expenseAmount, expenseCategoryId, expenseDescription, expenseAccountId, cal, expensePayee, expenseEvent);
                        db.createTransaction(transaction);

                        // Set input string for Payee's description in FragmentNewTransaction, and then return.
                        FragmentTransactions fragmentTransactions = (FragmentTransactions)((ActivityMain)getActivity()).getFragment(ActivityMain.TAB_POSITION_TRANSACTIONS);
                        fragmentTransactions.updateListTransaction();

                        ((ActivityMain) getActivity()).updateTabs(ActivityMain.TAB_POSITION_NEW_TRANSACTION, ActivityMain.TAB_POSITION_TRANSACTIONS);

                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
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
                        break;
                    case 1:
                        llIncome.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        llTransfer.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        llAdjustment.setVisibility(View.VISIBLE);
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

        etExpenseAmount         = (EditText) getView().findViewById(R.id.etExpenseAmount);
        etExpenseAmount.addTextChangedListener(new CurrencyTextWatcher(etExpenseAmount));
        ivExpenseCurrencyIcon   = (ImageView) getView().findViewById(R.id.ivExpenseCurrencyIcon);

        llExpenseDescription    = (LinearLayout) getView().findViewById(R.id.llExpenseDescription);
        llExpenseDescription.setOnClickListener(this);
        tvExpenseDescription    = (TextView) getView().findViewById(R.id.tvExpenseDescription);

        llExpenseCategory       = (LinearLayout) getView().findViewById(R.id.llExpenseCategory);
        llExpenseCategory.setOnClickListener(this);
        tvExpenseCategory       = (TextView) getView().findViewById(R.id.tvExpenseCategory);

        llExpenseAccount        = (LinearLayout) getView().findViewById(R.id.llExpenseAccount);
        llExpenseAccount.setOnClickListener(this);
        tvExpenseAccount        = (TextView) getView().findViewById(R.id.tvExpenseAccount);
        llExpenseDate           = (LinearLayout) getView().findViewById(R.id.llExpenseDate);
        llExpenseDate.setOnClickListener(this);
        tvExpenseDate           = (TextView) getView().findViewById(R.id.tvExpenseDate);
        tvExpenseDate.setText(mDay + "-" + mMonth + "-" + mYear + " " + mHour + ":" + String.format("%02d", mMinute));
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
        etIncomeAmount          = (EditText) getView().findViewById(R.id.etIncomeAmount);
        etIncomeAmount.addTextChangedListener(new CurrencyTextWatcher(etIncomeAmount));
        ivIncomeCurrencyIcon    = (ImageView) getView().findViewById(R.id.ivIncomeCurrencyIcon);
        llIncomeCategory        = (LinearLayout) getView().findViewById(R.id.llIncomeCategory);
        tvIncomeCategory        = (TextView) getView().findViewById(R.id.tvIncomeCategory);
        llIncomeDescription     = (LinearLayout) getView().findViewById(R.id.llIncomeDescription);
        tvIncomeDescription     = (TextView) getView().findViewById(R.id.tvIncomeDescription);
        llToAccount             = (LinearLayout) getView().findViewById(R.id.llToAccount);
        tvToAccount             = (TextView) getView().findViewById(R.id.tvToAccount);
        llIncomeDate            = (LinearLayout) getView().findViewById(R.id.llIncomeDate);
        tvIncomeDate            = (TextView) getView().findViewById(R.id.tvIncomeDate);
        tvIncomeDate.setText(mDay + "-" + mMonth + "-" + mYear + " " + mHour + ":" + String.format("%02d", mMinute));
        llIncomeEvent           = (LinearLayout) getView().findViewById(R.id.llIncomeEvent);
        tvIncomeEvent           = (TextView) getView().findViewById(R.id.tvIncomeEvent);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private void initViewTransfer() {
        LogUtils.logEnterFunction(Tag, null);

        llTransfer                  = (LinearLayout) getView().findViewById(R.id.llTransfer);
        etTransferAmount            = (EditText) getView().findViewById(R.id.etTransferAmount);
        etTransferAmount.addTextChangedListener(new CurrencyTextWatcher(etTransferAmount));
        ivTransferCurrencyIcon      = (ImageView) getView().findViewById(R.id.ivTransferCurrencyIcon);
        llTransferFromAccount       = (LinearLayout) getView().findViewById(R.id.llTransferFromAccount);
        tvTransferFromAccount       = (TextView) getView().findViewById(R.id.tvTransferFromAccount);
        llTransferToAccount         = (LinearLayout) getView().findViewById(R.id.llTransferToAccount);
        tvTransferToAccount         = (TextView) getView().findViewById(R.id.tvTransferToAccount);
        llTransferDescription       = (LinearLayout) getView().findViewById(R.id.llTransferDescription);
        tvTransferDescription       = (TextView) getView().findViewById(R.id.tvTransferDescription);
        llTransferDate              = (LinearLayout) getView().findViewById(R.id.llTransferDate);
        tvTransferDate              = (TextView) getView().findViewById(R.id.tvTransferDate);
        tvTransferDate.setText(mDay + "-" + mMonth + "-" + mYear + " " + mHour + ":" + String.format("%02d", mMinute));
        etTransferFee               = (EditText) getView().findViewById(R.id.etTransferFee);
        ivTransferFeeCurrencyIcon   = (ImageView) getView().findViewById(R.id.ivTransferFeeCurrencyIcon);
        llTransferCategory          = (LinearLayout) getView().findViewById(R.id.llTransferCategory);
        tvTransferCategory          = (TextView) getView().findViewById(R.id.tvTransferCategory);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private void initViewAdjustment() {
        LogUtils.logEnterFunction(Tag, null);

        llAdjustment                = (LinearLayout) getView().findViewById(R.id.llAdjustment);
        llAdjustmentAccount         = (LinearLayout) getView().findViewById(R.id.llAdjustmentAccount);
        tvAdjustmentAccount         = (TextView) getView().findViewById(R.id.tvAdjustmentAccount);
        etAdjustmentBalance         = (EditText) getView().findViewById(R.id.etAdjustmentBalance);
        etAdjustmentBalance.addTextChangedListener(new CurrencyTextWatcher(etAdjustmentBalance));
        ivAdjustmentCurrencyIcon    = (ImageView) getView().findViewById(R.id.ivAdjustmentCurrencyIcon);
        tvAdjustmentSpent           = (TextView) getView().findViewById(R.id.tvAdjustmentSpent);
        llAdjustmentCategory        = (LinearLayout) getView().findViewById(R.id.llAdjustmentCategory);
        tvAdjustmentCategory        = (TextView) getView().findViewById(R.id.tvAdjustmentCategory);
        llAdjustmentDescription     = (LinearLayout) getView().findViewById(R.id.llAdjustmentDescription);
        tvAdjustmentDescription     = (TextView) getView().findViewById(R.id.tvAdjustmentDescription);
        llAdjustmentDate            = (LinearLayout) getView().findViewById(R.id.llAdjustmentDate);
        tvAdjustmentDate            = (TextView) getView().findViewById(R.id.tvAdjustmentDate);
        tvAdjustmentDate.setText(mDay + "-" + mMonth + "-" + mYear + " " + mHour + ":" + String.format("%02d", mMinute));
        llAdjustmentPayee           = (LinearLayout) getView().findViewById(R.id.llAdjustmentPayee);
        tvAdjustmentPayee           = (TextView) getView().findViewById(R.id.tvAdjustmentPayee);
        llAdjustmentEvent           = (LinearLayout) getView().findViewById(R.id.llAdjustmentEvent);
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

    /**
     * Update Category, call from ActivityMain
     * @param categoryId
     */
    public void updateCategory(int categoryId) {
        LogUtils.logEnterFunction(Tag, "categoryId = " + categoryId);

        mCategory = db.getCategory(categoryId);
        if(mCategory != null) {
            tvExpenseCategory.setText(mCategory.getName());
        } else {
            tvExpenseCategory.setText("");
        }

        LogUtils.logLeaveFunction(Tag, "categoryId = " + categoryId, null);
    }

    /**
     * Update description
     * @param description
     */
    public void updateDescription(String description) {
        LogUtils.logEnterFunction(Tag, "description = " + description);

        tvExpenseDescription.setText(description);
        tvIncomeDescription.setText(description);
        tvTransferDescription.setText(description);
        tvAdjustmentDescription.setText(description);

        LogUtils.logLeaveFunction(Tag, "description = " + description, null);
    }

    /**
     * Update Account, call from ActivityMain
     * @param accountId
     */
    public void updateAccount(int accountId) {
        LogUtils.logEnterFunction(Tag, "accountId = " + accountId);

        mAccount = db.getAccount(accountId);
        if(mAccount != null) {
            tvExpenseAccount.setText(mAccount.getName());
        } else {
            tvExpenseAccount.setText("");
        }

        LogUtils.logLeaveFunction(Tag, "accountId = " + accountId, null);
    }

    /**
     * Update Payee
     * @param payee
     */
    public void updatePayee(String payee) {
        LogUtils.logEnterFunction(Tag, "payee = " + payee);

        tvExpensePayee.setText(payee);
        tvAdjustmentPayee.setText(payee);

        LogUtils.logLeaveFunction(Tag, "payee = " + payee, null);
    }

    /**
     * Update Payee
     * @param event
     */
    public void updateEvent(String event) {
        LogUtils.logEnterFunction(Tag, "event = " + event);

        tvExpenseEvent.setText(event);
        tvIncomeEvent.setText(event);
        tvAdjustmentEvent.setText(event);

        LogUtils.logLeaveFunction(Tag, "event = " + event, null);
    }
}
