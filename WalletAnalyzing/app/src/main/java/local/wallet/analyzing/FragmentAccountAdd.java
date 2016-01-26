package local.wallet.analyzing;

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
import android.widget.ImageView;
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
public class FragmentAccountAdd extends Fragment {

    private static final String TAG     = "FragmentAccountAdd";

    private DatabaseHelper db;

    private AccountType mAccountType = AccountType.Accounts.get(0);
    private Currency    mCurrency = Currency.Currencies.get(0);

    private ClearableEditText etName;
    private LinearLayout llType;
    private TextView tvType;
    private LinearLayout llCurrency;
    private TextView tvCurrency;
    private EditText etInitialBalance;
    private ImageView ivCurrencyIcon;
    private LinearLayout llDescription;
    private TextView tvDescription;
    private LinearLayout llSave;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
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

        return inflater.inflate(R.layout.layout_fragment_account_add, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onActivityCreated(savedInstanceState);

        db = new DatabaseHelper(getActivity());

        // Initialize View
        etName              = (ClearableEditText) getView().findViewById(R.id.etName);
        llType              = (LinearLayout) getView().findViewById(R.id.llType);
        tvType              = (TextView) getView().findViewById(R.id.tvType);
        llCurrency          = (LinearLayout) getView().findViewById(R.id.llCurrency);
        tvCurrency          = (TextView) getView().findViewById(R.id.tvCurrency);
        etInitialBalance    = (EditText) getView().findViewById(R.id.etInitialBalance);
        ivCurrencyIcon      = (ImageView) getView().findViewById(R.id.ivCurrencyIcon);
        llDescription       = (LinearLayout) getView().findViewById(R.id.llDescription);
        tvDescription       = (TextView) getView().findViewById(R.id.tvDescription);
        llSave              = (LinearLayout) getView().findViewById(R.id.llSave);

        llType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentAccountSelectType nextFrag = new FragmentAccountSelectType();
                Bundle bundle = new Bundle();
                bundle.putString("Tag", ((ActivityMain)getActivity()).getFragmentAccountAdd());
                bundle.putInt("AccountType", mAccountType.getId());
                nextFrag.setArguments(bundle);
                FragmentAccountAdd.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_account, nextFrag, "FragmentAccountSelectType")
                        .addToBackStack(null)
                        .commit();
            }
        });

        llCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentSelectCurrency nextFrag = new FragmentSelectCurrency();
                Bundle bundle = new Bundle();
                bundle.putString("Tag", ((ActivityMain)getActivity()).getFragmentAccountAdd());
                bundle.putInt("Currency", mCurrency.getId());
                nextFrag.setArguments(bundle);
                FragmentAccountAdd.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_account, nextFrag, "FragmentSelectCurrency")
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
                FragmentAccountAdd.this.getFragmentManager().beginTransaction()
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
                long account_id = db.createAccount(accountName, mAccountType.getId(), mCurrency.getId(), initialBalance, description);

                // Update list of Account in FragmentAccount
                FragmentAccount fragmentAccount = (FragmentAccount)((ActivityMain)getActivity()).getFragment(ActivityMain.TAB_POSITION_ACCOUNTS);
                fragmentAccount.addToAccountList(db.getAccount(account_id));

                // Return to FragmentAccount
                getFragmentManager().popBackStackImmediate();
            }
        });
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
     * @param currencyId
     */
    public void updateCurrency(int currencyId) {
        LogUtils.logEnterFunction(TAG, "currencyId = " + currencyId);

        mCurrency = Currency.getCurrencyById(currencyId);
        tvCurrency.setText(mCurrency.getName());
        ivCurrencyIcon.setImageResource(mCurrency.getIcon());

        LogUtils.logLeaveFunction(TAG, "currencyId = " + currencyId, null);
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
                String inputted = s.toString().replaceAll(",", "");

                LogUtils.trace(TAG, "input replace: " + inputted);
                String[] ar = inputted.split("\\.");

                StringBuilder formatted = new StringBuilder();
                if(ar[0].length() > 0) {
                    LogUtils.trace(TAG, "ar[0]: " + ar[0]);
                    for(int i = 0; i < ar[0].length(); i++) {
                        formatted.append(ar[0].charAt(i));
                        if(((ar[0].length() - (i+1)) % 3 == 0) && (i != ar[0].length()-1)) {
                            formatted.append(",");
                        }
                        LogUtils.trace(TAG, "formatted :" + formatted.toString());
                    }
                }

                if(s.toString().charAt(s.toString().length()-1) == '.') {
                    formatted.append(".");
                    LogUtils.trace(TAG, "formatted Add '.' :" + formatted.toString());
                } else if(ar.length == 2) {
                    LogUtils.trace(TAG, "ar[1]: " + ar[1]);
                    formatted.append(".");
                    for(int i = 0; i < ar[1].length(); i++) {
                        formatted.append(ar[1].charAt(i));
                        if(((ar[1].length() - (i+1)) % 3 == 0) && (i != ar[1].length()-1)) {
                            formatted.append(",");
                        }
                        LogUtils.trace(TAG, "formatted :" + formatted.toString());
                    }
                }

                current = formatted.toString();
                etInitialBalance.setText(formatted);
                etInitialBalance.setSelection(formatted.length());

                etInitialBalance.addTextChangedListener(this);
            }

            LogUtils.logLeaveFunction(TAG, null, null);
        }

    }
}
