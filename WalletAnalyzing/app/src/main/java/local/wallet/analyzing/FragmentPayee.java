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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.droidparts.widget.ClearableEditText;

import java.util.ArrayList;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;
import local.wallet.analyzing.model.Transaction.TransactionEnum;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentPayee extends Fragment {

    private static final String TAG     = "FragmentPayee";

    private DatabaseHelper db;

    private String mTagOfSource = "";
    private TransactionEnum mCurrentTransactionType     = TransactionEnum.Expense;
    private String mPayee;

    private ClearableEditText   etPayee;
    private ListView            lvPayee;
    private List<String>        payees = new ArrayList<String>();
    private ArrayAdapter<String> mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle                   = this.getArguments();
        mTagOfSource                    = bundle.getString("Tag");
        mCurrentTransactionType         = (TransactionEnum)bundle.get("TransactionType");
        mPayee                          = bundle.getString("Payee", "");

        LogUtils.trace(TAG, "mTagOfSource = " + mTagOfSource);
        LogUtils.trace(TAG, "mCurrentTransactionType = " + mCurrentTransactionType);
        LogUtils.trace(TAG, "mPayee = " + mPayee);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        String myTag = getTag();
        ((ActivityMain)getActivity()).setFragmentAccountCreate(myTag);

        LogUtils.logLeaveFunction(TAG, null, null);

        return inflater.inflate(R.layout.layout_fragment_payee, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onActivityCreated(savedInstanceState);

        db = new DatabaseHelper(getActivity());

        etPayee = (ClearableEditText) getView().findViewById(R.id.etPayee);
        etPayee.setText(mPayee);
        etPayee.addTextChangedListener(new PayeeTextWatcher());

        lvPayee = (ListView) getView().findViewById(R.id.lvPayee);
        payees = db.getPayees("");
        mAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                payees);
        lvPayee.setAdapter(mAdapter);

        lvPayee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etPayee.setText(payees.get(position));
            }
        });

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_done, null);
        TextView    tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_payee));
        ImageView ivDone    = (ImageView) mCustomView.findViewById(R.id.ivDone);
        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.trace(TAG, "Click Menu Action Done.");

                if(mTagOfSource.equals(FragmentTransactionCreate.Tag)) {

                    LogUtils.trace(TAG, "Setup for FragmentTransactionCreate");
                    FragmentTransactionCreate fragment = (FragmentTransactionCreate)((ActivityMain)getActivity()).getFragment(ActivityMain.TAB_POSITION_TRANSACTION_CREATE);
                    fragment.updatePayee(mCurrentTransactionType, etPayee.getText().toString());

                } else if(mTagOfSource.equals(((ActivityMain) getActivity()).getFragmentTransactionUpdate())) {

                    LogUtils.trace(TAG, "Setup for FragmentTransactionUpdate");
                    String tagOfFragment = ((ActivityMain) getActivity()).getFragmentTransactionUpdate();
                    FragmentTransactionUpdate fragment = (FragmentTransactionUpdate) getActivity().getSupportFragmentManager().findFragmentByTag(tagOfFragment);
                    fragment.updatePayee(mCurrentTransactionType, etPayee.getText().toString());

                }

                // Back
                getFragmentManager().popBackStackImmediate();
            }
        });
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        super.onCreateOptionsMenu(menu, inflater);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    /**
     * Initial Payee EditText's TextWatcher
     */
    private class PayeeTextWatcher implements TextWatcher {
        private String current = "";

        public PayeeTextWatcher() {}

        public synchronized void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LogUtils.logEnterFunction(TAG, null);

            if(!s.toString().trim().equals(current)){
                payees.clear();
                List<String> arTemp = db.getPayees(s.toString().trim());
                for(int i = 0 ; i < arTemp.size(); i++) {
                    payees.add(arTemp.get(i));
                }
                mAdapter.notifyDataSetChanged();

                current = s.toString().trim();
            }

            LogUtils.logLeaveFunction(TAG, null, null);
        }

    }
}
