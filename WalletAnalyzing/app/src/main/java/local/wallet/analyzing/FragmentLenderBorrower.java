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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 3/28/2016.
 */
public class FragmentLenderBorrower extends Fragment {

    public static final String     Tag                     = "LenderBorrower";

    public interface IUpdateLenderBorrower extends Serializable {
        void onLenderBorrowerUpdated(String people);
    }

    private DatabaseHelper          mDbHelper;

    private Category                mCategory;
    private String                  mPeople;

    private ClearableEditText       etPeople;
    private ListView                lvPeople;
    private List<String>            peoples                  = new ArrayList<String>();
    private ArrayAdapter<String>    mAdapter;

    private IUpdateLenderBorrower            mCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle                   = this.getArguments();
        mCategory                       = (Category) bundle.getSerializable("Category");
        mPeople                         = bundle.getString("People", "");
        mCallback                       = (IUpdateLenderBorrower) bundle.get("Callback");

        LogUtils.trace(Tag, "mPeople = " + mPeople);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_lender_borrower, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onActivityCreated(savedInstanceState);

        mDbHelper = new DatabaseHelper(getActivity());

        etPeople = (ClearableEditText) getView().findViewById(R.id.etPeople);
        if((mCategory.isExpense() && mCategory.getDebtType() == Category.EnumDebt.LESS) ||
                (!mCategory.isExpense() && mCategory.getDebtType() == Category.EnumDebt.MORE)) {
            etPeople.setHint(getResources().getText(R.string.report_Lent_borrow_lender));
        } else if((mCategory.isExpense() && mCategory.getDebtType() == Category.EnumDebt.MORE) ||
                (!mCategory.isExpense() && mCategory.getDebtType() == Category.EnumDebt.LESS)) {
            etPeople.setHint(getResources().getText(R.string.report_Lent_borrow_borrower));
        }
        etPeople.setText(mPeople);
        etPeople.addTextChangedListener(new PeopleTextWatcher());

        lvPeople = (ListView) getView().findViewById(R.id.lvPeople);
        peoples = mDbHelper.getPeoples("");
        mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, peoples);
        lvPeople.setAdapter(mAdapter);

        lvPeople.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());
                etPeople.setText(peoples.get(position));
            }
        });

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_done, null);
        TextView    tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        if((mCategory.isExpense() && mCategory.getDebtType() == Category.EnumDebt.LESS) || (!mCategory.isExpense() && mCategory.getDebtType() == Category.EnumDebt.MORE)) {
            tvTitle.setText(getResources().getString(R.string.title_lender));
        } else if((mCategory.isExpense() && mCategory.getDebtType() == Category.EnumDebt.MORE) || (!mCategory.isExpense() && mCategory.getDebtType() == Category.EnumDebt.LESS)) {
            tvTitle.setText(getResources().getString(R.string.title_borrower));
        }

        ImageView ivDone    = (ImageView) mCustomView.findViewById(R.id.ivDone);
        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());

                mCallback.onLenderBorrowerUpdated(etPeople.getText().toString());
                // Back
                getFragmentManager().popBackStackImmediate();
            }
        });
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        super.onCreateOptionsMenu(menu, inflater);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Initial People EditText's TextWatcher
     */
    private class PeopleTextWatcher implements TextWatcher {
        private String current = "";

        public PeopleTextWatcher() {}

        public synchronized void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LogUtils.logEnterFunction(Tag, null);

            if(!s.toString().trim().equals(current)){
                peoples.clear();
                List<String> arTemp = mDbHelper.getPayees(s.toString().trim());
                for(int i = 0 ; i < arTemp.size(); i++) {
                    peoples.add(arTemp.get(i));
                }
                mAdapter.notifyDataSetChanged();

                current = s.toString().trim();
            }

            LogUtils.logLeaveFunction(Tag, null, null);
        }

    }
}
