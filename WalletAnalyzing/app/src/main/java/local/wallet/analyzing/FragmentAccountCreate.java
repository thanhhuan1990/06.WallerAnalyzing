package local.wallet.analyzing;

import android.app.Activity;
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

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;
import local.wallet.analyzing.model.AccountType;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentAccountCreate extends Fragment {

    private static final String TAG     = "FragmentAccountCreate";

    private Configurations              mConfigs;
    private DatabaseHelper              mDbHelper;

    private AccountType                 mAccountType    = AccountType.Accounts.get(0);
    private Currency.CurrencyList       mCurrency;

    private ClearableEditText           etName;
    private LinearLayout                llType;
    private TextView                    tvType;
    private LinearLayout                llCurrency;
    private TextView                    tvCurrency;
    private EditText                    etInitialBalance;
    private LinearLayout                llDescription;
    private TextView                    tvDescription;
    private LinearLayout                llSave;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);

        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        View mCustomView            = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle            = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_account_add));
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        super.onCreateOptionsMenu(menu, inflater);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        String myTag = getTag();
        ((ActivityMain)getActivity()).setFragmentAccountAdd(myTag);

        LogUtils.logLeaveFunction(TAG, null, null);

        return inflater.inflate(R.layout.layout_fragment_account_create, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

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
        llDescription       = (LinearLayout) getView().findViewById(R.id.llDescription);
        tvDescription       = (TextView) getView().findViewById(R.id.tvDescription);
        llSave              = (LinearLayout) getView().findViewById(R.id.llSave);

        llType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentAccountTypeSelect nextFrag = new FragmentAccountTypeSelect();
                Bundle bundle = new Bundle();
                bundle.putString("Tag", ((ActivityMain)getActivity()).getFragmentAccountAdd());
                bundle.putInt("AccountType", mAccountType.getId());
                nextFrag.setArguments(bundle);
                FragmentAccountCreate.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_account, nextFrag, "FragmentAccountTypeSelect")
                        .addToBackStack(null)
                        .commit();
            }
        });

        llCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCurrencySelect nextFrag = new FragmentCurrencySelect();
                Bundle bundle = new Bundle();
                bundle.putString("Tag", ((ActivityMain)getActivity()).getFragmentAccountAdd());
                bundle.putInt("Currency", mCurrency.getValue());
                nextFrag.setArguments(bundle);
                FragmentAccountCreate.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_account, nextFrag, "FragmentCurrencySelect")
                        .addToBackStack(null)
                        .commit();
            }
        });

        etInitialBalance.addTextChangedListener(new CurrencyTextWatcher());

        llDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDescription nextFrag = new FragmentDescription();
                Bundle bundle = new Bundle();
                bundle.putString("Tag", ((ActivityMain)getActivity()).getFragmentAccountAdd());
                bundle.putString("Description", tvDescription.getText().toString());
                nextFrag.setArguments(bundle);
                FragmentAccountCreate.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_account, nextFrag, "FragmentDescription")
                        .addToBackStack(null)
                        .commit();
            }
        });

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.trace(TAG, "Click button SAVE");
                // Check Account's name
                if(etName.getText().toString().equals("")) {
                    LogUtils.trace(TAG, "Name is empty");
                    etName.setError(getResources().getString(R.string.Input_Error_Account_Name_Empty));
                    return;
                }

                String accountName = etName.getText().toString();
                Double initialBalance =  etInitialBalance.getText().toString().equals("") ? 0 : Double.parseDouble(etInitialBalance.getText().toString().replaceAll(",", ""));
                String description = tvDescription.getText().toString();

                // Insert account to DB
                long account_id = mDbHelper.createAccount(accountName, mAccountType.getId(), mCurrency.getValue(), initialBalance, description);

                // Update list of Account in FragmentListAccount
                FragmentListAccount fragmentListAccount = (FragmentListAccount)((ActivityMain)getActivity()).getFragment(ActivityMain.TAB_POSITION_ACCOUNTS);
                fragmentListAccount.addToAccountList(mDbHelper.getAccount(account_id));

                // Return to FragmentListAccount
                getFragmentManager().popBackStackImmediate();
            }
        });
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onAttach(Activity activity) {
        LogUtils.logEnterFunction(TAG, null);
        super.onAttach(activity);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onStart() {
        LogUtils.logEnterFunction(TAG, null);
        super.onStart();
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onResume() {
        LogUtils.logEnterFunction(TAG, null);
        super.onResume();
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onPause() {
        LogUtils.logEnterFunction(TAG, null);
        super.onPause();
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onStop() {
        LogUtils.logEnterFunction(TAG, null);
        super.onStop();
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onDestroyView() {
        LogUtils.logEnterFunction(TAG, null);
        super.onDestroyView();
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onDestroy() {
        LogUtils.logEnterFunction(TAG, null);
        super.onDestroy();
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onDetach() {
        LogUtils.logEnterFunction(TAG, null);
        super.onDetach();
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onDestroyOptionsMenu() {
        LogUtils.logEnterFunction(TAG, null);
        super.onDestroyOptionsMenu();
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    /**
     * Update AccountType, call from ActivityMain
     * @param accountTypeId
     */
    public void updateAccountType(int accountTypeId) {
        LogUtils.logEnterFunction(TAG, "accountTypeId = " + accountTypeId);

        mAccountType = AccountType.getAccountTypeById(accountTypeId);
        tvType.setText(mAccountType.getName());

        LogUtils.logLeaveFunction(TAG, "accountTypeId = " + accountTypeId, null);
    }

    /**
     * Update Currency, call from ActivityMain
     * @param currency
     */
    public void updateCurrency(Currency.CurrencyList currency) {
        LogUtils.logEnterFunction(TAG, "currencyId = " + currency.name());

        mCurrency = currency;
        tvCurrency.setText(Currency.getCurrencyName(mCurrency));

        LogUtils.logLeaveFunction(TAG, "currencyId = " + currency.name(), null);
    }

    /**
     * Update Description, call from ActivityMain
     * @param description
     */
    public void updateDescription(String description) {
        LogUtils.logEnterFunction(TAG, "description = " + description);

        tvDescription.setText(description);

        LogUtils.logLeaveFunction(TAG, "description = " + description, null);
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
            LogUtils.logEnterFunction(TAG, null);

            if(!s.toString().equals(current)){
                etInitialBalance.removeTextChangedListener(this);

                LogUtils.trace(TAG, "input: " + s.toString());

                String inputted = s.toString().replaceAll(",", "").replaceAll(" ", "");
                String formatted = Currency.formatCurrencyDouble(mCurrency, Double.parseDouble(inputted));

                current = formatted;
                etInitialBalance.setText(formatted);
                etInitialBalance.setSelection(formatted.length());

                etInitialBalance.addTextChangedListener(this);
            }

            LogUtils.logLeaveFunction(TAG, null, null);
        }

    }
}
