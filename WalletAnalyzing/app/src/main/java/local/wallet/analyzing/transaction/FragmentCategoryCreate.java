package local.wallet.analyzing.transaction;

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

import local.wallet.analyzing.R;
import local.wallet.analyzing.utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Category.EnumDebt;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentCategoryCreate extends Fragment implements FragmentCategoryParentSelect.ISelectParentCategory, FragmentDescription.IUpdateDescription {
    public static int           mTab = 1;
    public static final String  Tag = "---[" + mTab + "]---CategoryCreate";

    private ActivityMain        mActivity;

    private DatabaseHelper      mDbHelper;

    // Keep return value from FragmentCategoryParentSelect
    private Category            mParentCategory;
    private int                 mContainerID;
    private boolean             mIsExpense = true;

    private ClearableEditText   etName;
    private LinearLayout        llParentCategory;
    private TextView            tvParentCategory;
    private EditText            etDescription;
    private LinearLayout        llSave;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle       = this.getArguments();
        mTab                = bundle.getInt("Tab", mTab);
        mIsExpense          = bundle.getBoolean("CategoryType");
        mContainerID        = bundle.getInt("ContainerID");

        LogUtils.logLeaveFunction(Tag);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        LogUtils.logLeaveFunction(Tag);

        return inflater.inflate(R.layout.layout_fragment_category_create, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        super.onActivityCreated(savedInstanceState);

        mActivity           = (ActivityMain) getActivity();

        mDbHelper           = new DatabaseHelper(getActivity());

        // Initialize View
        etName              = (ClearableEditText) getView().findViewById(R.id.etName);
        llParentCategory    = (LinearLayout) getView().findViewById(R.id.llParentCategory);
        tvParentCategory    = (TextView) getView().findViewById(R.id.tvParentCategory);
        etDescription       = (EditText) getView().findViewById(R.id.etDescription);
        llSave              = (LinearLayout) getView().findViewById(R.id.llSave);

        llParentCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityMain) getActivity()).hideKeyboard();
                FragmentCategoryParentSelect nextFrag = new FragmentCategoryParentSelect();
                Bundle bundle = new Bundle();
                bundle.putInt("Tab", mTab);
                bundle.putSerializable("Callback", FragmentCategoryCreate.this);
                bundle.putBoolean("CategoryType", mIsExpense);
                bundle.putInt("ParentCategoryId", mParentCategory != null ? mParentCategory.getId() : 0);
                nextFrag.setArguments(bundle);
                mActivity.addFragment(mTab, mContainerID, nextFrag, FragmentCategoryParentSelect.Tag, true);
            }
        });

        etDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDescription nextFrag = new FragmentDescription();
                Bundle bundle = new Bundle();
                bundle.putInt("Tab", mTab);
                bundle.putString("Description", etDescription.getText().toString());
                bundle.putSerializable("Callback", FragmentCategoryCreate.this);
                nextFrag.setArguments(bundle);
                mActivity.addFragment(mTab, mContainerID, nextFrag, FragmentDescription.Tag, true);
            }
        });

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.trace(Tag, "Click button SAVE");
                ((ActivityMain) getActivity()).hideKeyboard();
                // Check Category's name
                if (etName.getText().toString().equals("")) {
                    LogUtils.trace(Tag, "Name is empty");
                    etName.setError(getResources().getString(R.string.Input_Error_Account_Name_Empty));
                    return;
                }

                // Todo: Insert new Category to DB
                long categoryId = mDbHelper.createCategory(mParentCategory != null ? mParentCategory.getId() : 0,    // ParentID
                        etName.getText().toString(),                            // Name
                        mIsExpense,                   // Expense
                        EnumDebt.NONE);                                                 // Borrow

                if (categoryId <= 0) {
                    LogUtils.error(Tag, "Create Category Failed.");
                    ((ActivityMain) getActivity()).showError("Create Category Failed.");
                } else {
                    ((ActivityMain) getActivity()).showToastSuccessful("Category created successful.");
                    // Todo: Update list Category in TransactionSelectCategory

                    // Return to CategorySelect
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

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView        = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle        = (TextView) mCustomView.findViewById(R.id.tvTitle);
        if(mIsExpense) {
            tvTitle.setText(getResources().getString(R.string.title_category_expense_add));
        } else {
            tvTitle.setText(getResources().getString(R.string.title_category_income_add));
        }

        // Update ActionBar
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);
        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onParentCategorySelected(int categoryId) {
        LogUtils.logEnterFunction(Tag, "parentCategoryId = " + categoryId);

        mParentCategory = mDbHelper.getCategory(categoryId);
        if(mParentCategory != null) {
            tvParentCategory.setText(mParentCategory.getName());
        } else {
            tvParentCategory.setText("");
        }

        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onDescriptionUpdated(String description) {
        LogUtils.logEnterFunction(Tag, "description = " + description);
        etDescription.setText(description);
        LogUtils.logLeaveFunction(Tag);
    }

}
