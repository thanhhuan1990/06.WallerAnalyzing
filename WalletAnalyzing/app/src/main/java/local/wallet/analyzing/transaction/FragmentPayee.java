package local.wallet.analyzing.transaction;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentPayee extends Fragment {
    private int                     mTab = 1;
    public final String             Tag = "---[" + mTab + "]---Payee";

    private ActivityMain            mActivity;

    public interface IUpdatePayee extends Serializable {
        void onPayeeUpdated(String payee);
    }

    private DatabaseHelper          mDbHelper;

    private String                  mPayee;

    private ClearableEditText       etPayee;
    private ListView                lvPayee;
    private List<String>            payees                  = new ArrayList<String>();
    private ArrayAdapter<String>    mAdapter;

    private IUpdatePayee            mCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle                   = this.getArguments();
        mTab                            = bundle.getInt("Tab", mTab);
        mPayee                          = bundle.getString("Payee", "");
        mCallback                       = (IUpdatePayee) bundle.get("Callback");

        LogUtils.trace(Tag, "mPayee = " + mPayee);

        LogUtils.logLeaveFunction(Tag);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        LogUtils.logLeaveFunction(Tag);
        return inflater.inflate(R.layout.layout_fragment_payee, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        super.onActivityCreated(savedInstanceState);

        mActivity   = (ActivityMain) getActivity();

        mDbHelper = new DatabaseHelper(getActivity());

        etPayee = (ClearableEditText) getView().findViewById(R.id.etPayee);
        etPayee.setText(mPayee);
        etPayee.addTextChangedListener(new PayeeTextWatcher());

        lvPayee = (ListView) getView().findViewById(R.id.lvPayee);
        payees = mDbHelper.getPayees("");
        mAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                payees);
        lvPayee.setAdapter(mAdapter);

        lvPayee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ActivityMain) getActivity()).hideKeyboard();
                etPayee.setText(payees.get(position));
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

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_done, null);
        TextView    tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_payee));
        ImageView ivDone    = (ImageView) mCustomView.findViewById(R.id.ivDone);
        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain) getActivity()).hideKeyboard();

                mCallback.onPayeeUpdated(etPayee.getText().toString());
                // Back
                getFragmentManager().popBackStackImmediate();
            }
        });
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(Tag);
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
            LogUtils.logEnterFunction(Tag);

            if(!s.toString().trim().equals(current)){
                payees.clear();
                List<String> arTemp = mDbHelper.getPayees(s.toString().trim());
                for(int i = 0 ; i < arTemp.size(); i++) {
                    payees.add(arTemp.get(i));
                }
                mAdapter.notifyDataSetChanged();

                current = s.toString().trim();
            }

            LogUtils.logLeaveFunction(Tag);
        }

    }
}
