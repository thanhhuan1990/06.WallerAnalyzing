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
import local.wallet.analyzing.utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.main.Configs;
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
    public static int                   mTab = 2;
    public static final String          Tag = "---[" + mTab + "]---AccountCreate";

    private ActivityMain                mActivity;
    private Configs 					mConfigs;
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
        LogUtils.logEnterFunction(Tag);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle       = this.getArguments();
        mTab                = bundle.getInt("Tab", mTab);
        mCallback           = (IAccountCallback) bundle.getSerializable("Callback");

        LogUtils.logLeaveFunction(Tag);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        LogUtils.logLeaveFunction(Tag);
        return inflater.inflate(R.layout.layout_fragment_account_create, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);

        super.onActivityCreated(savedInstanceState);

        mActivity           = (ActivityMain) getActivity();

        mConfigs            = new Configs(getActivity());
        mCurrency           = Currency.getCurrencyById(mConfigs.getInt(Configs.Key.Currency));
        mDbHelper           = new DatabaseHelper(getActivity());

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
                ((ActivityMain) getActivity()).hideKeyboard();
                FragmentAccountTypeSelect nextFrag = new FragmentAccountTypeSelect();
                Bundle bundle = new Bundle();
                bundle.putInt("Tab", mTab);
                bundle.putInt("AccountType", mAccountType.getId());
                bundle.putSerializable("Callback", FragmentAccountCreate.this);
                nextFrag.setArguments(bundle);
                mActivity.addFragment(mTab, R.id.layout_account, nextFrag, FragmentAccountTypeSelect.Tag, true);
            }
        });

        llCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain) getActivity()).hideKeyboard();
                FragmentCurrencySelect nextFrag = new FragmentCurrencySelect();
                Bundle bundle = new Bundle();
                bundle.putInt("Tab", mTab);
                bundle.putInt("Currency", mCurrency.getValue());
                bundle.putSerializable("Callback", FragmentAccountCreate.this);
                nextFrag.setArguments(bundle);
                mActivity.addFragment(mTab, R.id.layout_account, nextFrag, FragmentCurrencySelect.Tag, true);
            }
        });

        etInitialBalance.addTextChangedListener(new CurrencyTextWatcher());

        llDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain) getActivity()).hideKeyboard();
                FragmentDescription nextFrag = new FragmentDescription();
                Bundle bundle = new Bundle();
                bundle.putInt("Tab", mTab);
                bundle.putString("Description", tvDescription.getText().toString());
                bundle.putSerializable("Callback", FragmentAccountCreate.this);
                nextFrag.setArguments(bundle);
                mActivity.addFragment(mTab, R.id.layout_account, nextFrag, FragmentDescription.Tag, true);
            }
        });

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain) getActivity()).hideKeyboard();
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
        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag);
        super.onCreateOptionsMenu(menu, inflater);

        if(mTab != mActivity.getCurrentVisibleItem()) {
            LogUtils.error(Tag, "Wrong Tab. Return");
            LogUtils.logLeaveFunction(Tag);
            return;
        }

        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onAccountTypeSelected(int accountTypeId) {
        LogUtils.logEnterFunction(Tag, "accountTypeId = " + accountTypeId);

        mAccountType = AccountType.getAccountTypeById(accountTypeId);
        tvType.setText(mAccountType.getName());

        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onCurrencySelected(Currency.CurrencyList currency) {
        LogUtils.logEnterFunction(Tag, "currencyId = " + currency.name());

        mCurrency = currency;
        tvCurrency.setText(Currency.getCurrencyName(mCurrency));
        tvCurrencyIcon.setText(Currency.getCurrencyIcon(mCurrency.getValue()));

        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onDescriptionUpdated(String description) {
        LogUtils.logEnterFunction(Tag, "description = " + description);

        tvDescription.setText(description);

        LogUtils.logLeaveFunction(Tag);
    }

    private void initActionBar() {
        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        View mCustomView            = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle            = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_account_add));
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);
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
            LogUtils.logEnterFunction(Tag);

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

            LogUtils.logLeaveFunction(Tag);
        }

    }
}
