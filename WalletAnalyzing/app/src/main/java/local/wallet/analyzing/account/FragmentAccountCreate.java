package local.wallet.analyzing.account;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.droidparts.widget.ClearableEditText;

import local.wallet.analyzing.transaction.FragmentDescription;
import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.main.Configurations;
import local.wallet.analyzing.model.Account.IAccountCallback;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;
import local.wallet.analyzing.model.AccountType;
import local.wallet.analyzing.transaction.FragmentDescription.IUpdateDescription;
import local.wallet.analyzing.account.FragmentCurrencySelect.ISelectCurrency;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentAccountCreate extends Fragment implements IUpdateDescription, FragmentAccountTypeSelect.ISelectAccountType, ISelectCurrency {

    public static final String Tag      = "AccountCreate";

    private Configurations mConfigs;
    private DatabaseHelper              mDbHelper;
    private IAccountCallback            mCallback;

    private AccountType                 mAccountType    = AccountType.Accounts.get(0);
    private Currency.CurrencyList       mCurrency;

    private ClearableEditText           etName;
    private LinearLayout                llType;
    private TextView                    tvType;
    private LinearLayout                llCurrency;
    private TextView                    tvCurrency;
    private EditText                    etInitialBalance;
    private TextView                    tvCurrencyIcon;
    private LinearLayout                llDescription;
    private TextView                    tvDescription;
    private LinearLayout                llSave;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle       = this.getArguments();
        mCallback           = (IAccountCallback) bundle.getSerializable("Callback");

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(((ActivityMain) getActivity()).getCurrentVisibleItem() != ActivityMain.TAB_POSITION_LIST_ACCOUNT) {
            return;
        }

        LogUtils.logEnterFunction(Tag, null);

        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        View mCustomView            = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle            = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_account_add));
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        super.onCreateOptionsMenu(menu, inflater);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);

        return inflater.inflate(R.layout.layout_fragment_account_create, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onActivityCreated(savedInstanceState);

        mConfigs    = new Configurations(getActivity());
        mCurrency   = Currency.getCurrencyById(mConfigs.getInt(Configurations.Key.Currency));
        mDbHelper   = new DatabaseHelper(getActivity());

        // Initialize View
        etName              = (ClearableEditText) getView().findViewById(R.id.etName);
        llType              = (LinearLayout) getView().findViewById(R.id.llType);
        tvType              = (TextView) getView().findViewById(R.id.tvType);
        tvType.setText(AccountType.Accounts.get(0).getName());
        llCurrency          = (LinearLayout) getView().findViewById(R.id.llCurrency);
        tvCurrency          = (TextView) getView().findViewById(R.id.tvCurrency);
        tvCurrency.setText(Currency.getCurrencyName(mCurrency));
        etInitialBalance    = (EditText) getView().findViewById(R.id.etInitialBalance);
        tvCurrencyIcon      = (TextView) getView().findViewById(R.id.tvCurrencyIcon);
        llDescription       = (LinearLayout) getView().findViewById(R.id.llDescription);
        tvDescription       = (TextView) getView().findViewById(R.id.tvDescription);
        llSave              = (LinearLayout) getView().findViewById(R.id.llSave);

        llType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                FragmentAccountTypeSelect nextFrag = new FragmentAccountTypeSelect();
                Bundle bundle = new Bundle();
                bundle.putInt("AccountType", mAccountType.getId());
                bundle.putSerializable("Callback", FragmentAccountCreate.this);
                nextFrag.setArguments(bundle);
                FragmentAccountCreate.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_account, nextFrag, FragmentAccountTypeSelect.Tag)
                        .addToBackStack(null)
                        .commit();
            }
        });

        llCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                FragmentCurrencySelect nextFrag = new FragmentCurrencySelect();
                Bundle bundle = new Bundle();
                bundle.putInt("Currency", mCurrency.getValue());
                bundle.putSerializable("Callback", FragmentAccountCreate.this);
                nextFrag.setArguments(bundle);
                FragmentAccountCreate.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_account, nextFrag, FragmentCurrencySelect.Tag)
                        .addToBackStack(null)
                        .commit();
            }
        });

        etInitialBalance.addTextChangedListener(new CurrencyTextWatcher());

        llDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                FragmentDescription nextFrag = new FragmentDescription();
                Bundle bundle = new Bundle();
                bundle.putString("Description", tvDescription.getText().toString());
                bundle.putSerializable("Callback", FragmentAccountCreate.this);
                nextFrag.setArguments(bundle);
                FragmentAccountCreate.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_account, nextFrag, FragmentDescription.Tag)
                        .addToBackStack(null)
                        .commit();
            }
        });

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                LogUtils.trace(Tag, "Click button SAVE");
                // Check Account's name
                if(etName.getText().toString().equals("")) {
                    LogUtils.trace(Tag, "Name is empty");
                    etName.setError(getResources().getString(R.string.Input_Error_Account_Name_Empty));
                    return;
                }

                String accountName = etName.getText().toString();
                Double initialBalance =  etInitialBalance.getText().toString().equals("") ? 0 : Double.parseDouble(etInitialBalance.getText().toString().replaceAll(",", ""));
                String description = tvDescription.getText().toString();

                // Insert account to DB
                long account_id = mDbHelper.createAccount(accountName, mAccountType.getId(), mCurrency.getValue(), initialBalance, description);

                if(account_id != -1) {

                    ((ActivityMain) getActivity()).showToastSuccessful(getResources().getString(R.string.message_account_create_successful));

                    // Update list of Account in FragmentListAccount
                    mCallback.onListAccountUpdated();

                    // Return to FragmentListAccount
                    getFragmentManager().popBackStackImmediate();
                }
            }
        });
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onAccountTypeSelected(int accountTypeId) {
        LogUtils.logEnterFunction(Tag, "accountTypeId = " + accountTypeId);

        mAccountType = AccountType.getAccountTypeById(accountTypeId);
        tvType.setText(mAccountType.getName());

        LogUtils.logLeaveFunction(Tag, "accountTypeId = " + accountTypeId, null);
    }

    @Override
    public void onCurrencySelected(Currency.CurrencyList currency) {
        LogUtils.logEnterFunction(Tag, "currencyId = " + currency.name());

        mCurrency = currency;
        tvCurrency.setText(Currency.getCurrencyName(mCurrency));
        tvCurrencyIcon.setText(Currency.getCurrencyIcon(mCurrency.getValue()));

        LogUtils.logLeaveFunction(Tag, "currencyId = " + currency.name(), null);
    }

    @Override
    public void onDescriptionUpdated(String description) {
        LogUtils.logEnterFunction(Tag, "description = " + description);

        tvDescription.setText(description);

        LogUtils.logLeaveFunction(Tag, "description = " + description, null);
    }

    /**
     * Initial Balance EditText's TextWatcher
     */
    private class CurrencyTextWatcher implements TextWatcher {
        private String current = "";

        public CurrencyTextWatcher() {}

        public synchronized void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LogUtils.logEnterFunction(Tag, null);

            if(!s.toString().equals(current)){
                etInitialBalance.removeTextChangedListener(this);

                String inputted = s.toString().replaceAll(",", "").replaceAll(" ", "");

                if(inputted.equals("")) {
                    return;
                }
                String formatted = Currency.formatCurrencyDouble(mCurrency.getValue(), Double.parseDouble(inputted));

                current = formatted;
                etInitialBalance.setText(formatted);
                etInitialBalance.setSelection(formatted.length());

                etInitialBalance.addTextChangedListener(this);
            }

            LogUtils.logLeaveFunction(Tag, null, null);
        }

    }
}
