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
public class FragmentEvent extends Fragment {

    public static final String     Tag                     = "FragmentEvent";

    private DatabaseHelper          mDbHelper;

    private String                  mTagOfSource            = "";
    private TransactionEnum         mCurrentTransactionType = TransactionEnum.Expense;
    private String                  mEvent;

    private ClearableEditText       etEvent;
    private ListView                lvEvent;
    private List<String>            events                  = new ArrayList<String>();
    private ArrayAdapter<String>    mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle                   = this.getArguments();
        mTagOfSource                    = bundle.getString("Tag");
        mCurrentTransactionType         = (TransactionEnum)bundle.get("TransactionType");
        mEvent                          = bundle.getString("Event", "");

        LogUtils.trace(Tag, "mTagOfSource = " + mTagOfSource);
        LogUtils.trace(Tag, "mCurrentTransactionType = " + mCurrentTransactionType);
        LogUtils.trace(Tag, "mEvent = " + mEvent);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_done, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_event));
        ImageView ivDone    = (ImageView) mCustomView.findViewById(R.id.ivDone);
        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());

                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(mTagOfSource);

                if(mTagOfSource.equals(FragmentTransactionCreateExpense.Tag)) {

                    ((FragmentTransactionCreateExpense) fragment).updateEvent(mCurrentTransactionType, etEvent.getText().toString());

                }
//                if(mTagOfSource.equals(((ActivityMain) getActivity()).getFragmentTransactionCreateExpense())) {
//
//                    LogUtils.trace(Tag, "Setup for FragmentTransactionCreate");
//                    String tagOfFragment = ((ActivityMain) getActivity()).getFragmentTransactionUpdate();
//                    FragmentTransactionCreateExpense fragment = (FragmentTransactionCreateExpense) getActivity().getSupportFragmentManager().findFragmentByTag(tagOfFragment);
//                    fragment.updateEvent(mCurrentTransactionType, etEvent.getText().toString());
//
//                } else if(mTagOfSource.equals(((ActivityMain) getActivity()).getFragmentTransactionUpdate())) {
//
//                    LogUtils.trace(Tag, "Setup for FragmentTransactionUpdate");
//                    String tagOfFragment = ((ActivityMain) getActivity()).getFragmentTransactionUpdate();
//                    FragmentTransactionUpdate fragment = (FragmentTransactionUpdate) getActivity().getSupportFragmentManager().findFragmentByTag(tagOfFragment);
//                    fragment.updateEvent(mCurrentTransactionType, etEvent.getText().toString());
//
//                }

                // Back
                getFragmentManager().popBackStackImmediate();
            }
        });
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        super.onCreateOptionsMenu(menu, inflater);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);

        return inflater.inflate(R.layout.layout_fragment_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onActivityCreated(savedInstanceState);

        mDbHelper = new DatabaseHelper(getActivity());

        etEvent     = (ClearableEditText) getView().findViewById(R.id.etEvent);
        etEvent.setText(mEvent);
        etEvent.addTextChangedListener(new EventTextWatcher());

        lvEvent     = (ListView) getView().findViewById(R.id.lvEvent);
        events      = mDbHelper.getEvents(mEvent);
        mAdapter    = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, events);
        lvEvent.setAdapter(mAdapter);

        lvEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                etEvent.setText(events.get(position));
            }
        });

        LogUtils.logLeaveFunction(Tag, null, null);
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
            LogUtils.logEnterFunction(Tag, null);

            if(!s.toString().equals(current)){
                events.clear();
                List<String> arTemp = mDbHelper.getEvents(s.toString().trim());
                for(int i = 0 ; i < arTemp.size(); i++) {
                    events.add(arTemp.get(i));
                }
                mAdapter.notifyDataSetChanged();

                current = s.toString().trim();
            }

            LogUtils.logLeaveFunction(Tag, null, null);
        }

    }
}
