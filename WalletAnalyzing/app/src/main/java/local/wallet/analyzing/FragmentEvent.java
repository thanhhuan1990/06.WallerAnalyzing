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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.droidparts.widget.ClearableEditText;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;
import local.wallet.analyzing.FragmentNewTransaction.TransactionEnum;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentEvent extends Fragment {

    private static final String TAG     = "FragmentEvent";

    private DatabaseHelper db;

    private String mTagOfSource = "";
    private TransactionEnum mCurrentTransactionType     = TransactionEnum.Expense;
    private String mEvent;

    private ClearableEditText   etEvent;
    private ListView            lvEvent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle                   = this.getArguments();
        mTagOfSource                    = bundle.getString("Tag");
        mCurrentTransactionType         = (TransactionEnum)bundle.get("TransactionType");
        mEvent                          = bundle.getString("Event", "");

        LogUtils.trace(TAG, "mTagOfSource = " + mTagOfSource);
        LogUtils.trace(TAG, "mCurrentTransactionType = " + mCurrentTransactionType);
        LogUtils.trace(TAG, "mEvent = " + mEvent);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_done, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_event));
        ImageView ivDone    = (ImageView) mCustomView.findViewById(R.id.ivDone);
        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.trace(TAG, "Click Menu Action Done.");

                if(mTagOfSource == null) {
                    LogUtils.trace(TAG, "Setup for FragmentNewTransaction");
                    // Set input string for Payee's description in FragmentNewTransaction, and then return.
                    FragmentNewTransaction fragmentNewTransaction = (FragmentNewTransaction)((ActivityMain)getActivity()).getFragment(ActivityMain.TAB_POSITION_NEW_TRANSACTION);
                    fragmentNewTransaction.updateEvent(mCurrentTransactionType, etEvent.getText().toString());
                }

                // Back
                getFragmentManager().popBackStackImmediate();
            }
        });
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

        return inflater.inflate(R.layout.layout_fragment_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onActivityCreated(savedInstanceState);

        db = new DatabaseHelper(getActivity());

        etEvent = (ClearableEditText) getView().findViewById(R.id.etEvent);
        etEvent.setText(mEvent);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    /**
     * Initial Payee EditText's TextWatcher
     */
    private class EventTextWatcher implements TextWatcher {
        private String current = "";

        public EventTextWatcher() {}

        public synchronized void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LogUtils.logEnterFunction(TAG, null);

            if(!s.toString().equals(current)){
            }

            LogUtils.logLeaveFunction(TAG, null, null);
        }

    }
}
