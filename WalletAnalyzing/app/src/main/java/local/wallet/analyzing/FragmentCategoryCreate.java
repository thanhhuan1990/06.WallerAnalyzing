package local.wallet.analyzing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;
import local.wallet.analyzing.model.Transaction.TransactionEnum;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentCategoryCreate extends Fragment {

    private static final String TAG     = "FragmentCategoryCreate";

    private DatabaseHelper db;

    private TransactionEnum mTransactionType     = TransactionEnum.Expense;

    // Keep return value from FragmentCategoryParentSelect
    private Category mParentCategory;
    private boolean mBorrow = false;

    private ClearableEditText etName;
    private LinearLayout llParentCategory;
    private TextView tvParentCategory;
    private EditText etDescription;
    private LinearLayout llSave;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle                   = this.getArguments();
        mTransactionType                = (TransactionEnum)bundle.get("TransactionType");

        LogUtils.trace(TAG, "mTransactionType = " + mTransactionType);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);

        super.onCreateOptionsMenu(menu, inflater);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        if(mTransactionType == TransactionEnum.Expense ||
                mTransactionType == TransactionEnum.Transfer ||
                mTransactionType == TransactionEnum.Adjustment) {
            tvTitle.setText(getResources().getString(R.string.title_category_expense_add));
        } else if(mTransactionType == TransactionEnum.Income) {
            tvTitle.setText(getResources().getString(R.string.title_category_income_add));
        }

        // Update ActionBar
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        String myTag = getTag();
        ((ActivityMain)getActivity()).setFragmentCategoryCreate(myTag);

        LogUtils.logLeaveFunction(TAG, null, null);

        return inflater.inflate(R.layout.layout_fragment_category_create, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onActivityCreated(savedInstanceState);

        db = new DatabaseHelper(getActivity());

        // Initialize View
        etName              = (ClearableEditText) getView().findViewById(R.id.etName);
        llParentCategory    = (LinearLayout) getView().findViewById(R.id.llParentCategory);
        tvParentCategory    = (TextView) getView().findViewById(R.id.tvParentCategory);
        etDescription       = (EditText) getView().findViewById(R.id.etDescription);
        llSave              = (LinearLayout) getView().findViewById(R.id.llSave);

        llParentCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCategoryParentSelect nextFrag = new FragmentCategoryParentSelect();
                Bundle bundle = new Bundle();
                bundle.putSerializable("TransactionType", mTransactionType);
                bundle.putString("Tag", ((ActivityMain) getActivity()).getFragmentCategoryCreate());
                bundle.putInt("ParentCategoryId", mParentCategory != null ? mParentCategory.getId() : 0);
                nextFrag.setArguments(bundle);
                FragmentCategoryCreate.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_transaction_create, nextFrag, "FragmentCategoryParentSelect")
                        .addToBackStack(null)
                        .commit();
            }
        });

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.trace(TAG, "Click button SAVE");

                // Check Category's name
                if (etName.getText().toString().equals("")) {
                    LogUtils.trace(TAG, "Name is empty");
                    etName.setError(getResources().getString(R.string.Input_Error_Account_Name_Empty));
                    return;
                }

                // Todo: Insert new Category to DB
                long categoryId = db.createCategory(mParentCategory != null ? mParentCategory.getId() : 0,    // ParentID
                                                        etName.getText().toString(),                            // Name
                                                        (mTransactionType == TransactionEnum.Expense
                                                                    || mTransactionType == TransactionEnum.Transfer
                                                                    || mTransactionType == TransactionEnum.Adjustment)
                                                                                ? true : false,                   // Expense
                                                        mBorrow);                                                 // Borrow

                if (categoryId <= 0) {
                    LogUtils.error(TAG, "Create Category Failed.");
                } else {
                    // Todo: Update list Category in FragmentCategorySelect
                    String tagOfFragment = ((ActivityMain) getActivity()).getFragmentNewTransactionSelectCategory();
                    FragmentCategorySelect fragment = (FragmentCategorySelect) getActivity()
                                                                        .getSupportFragmentManager()
                                                                            .findFragmentByTag(tagOfFragment);
                    fragment.updateListCategory();
                }

                // Return to FragmentCategorySelect
                getFragmentManager().popBackStackImmediate();
            }
        });
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    /**
     * Update ParentCategory, call from ActivityMain
     * @param parentCategoryId
     */
    public void updateParentCategory(int parentCategoryId, boolean borrow) {
        LogUtils.logEnterFunction(TAG, "parentCategoryId = " + parentCategoryId + ", borrow = " + borrow);

        mBorrow = borrow;
        mParentCategory = db.getCategory(parentCategoryId);
        if(mParentCategory != null) {
            tvParentCategory.setText(mParentCategory.getName());
        } else {
            tvParentCategory.setText("");
        }

        LogUtils.logLeaveFunction(TAG, "parentCategoryId = " + parentCategoryId + ", borrow = " + borrow, null);
    }

}
