package local.wallet.analyzing;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;
import local.wallet.analyzing.model.Transaction.TransactionEnum;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentCategorySelect extends Fragment {

    private static final String Tag = "FragmentCategorySelect";

    private String              mTagOfSource = "";
    private TransactionEnum     mCurrentTransactionType     = TransactionEnum.Expense;
    private int                 mUsingCategoryId;

    private DatabaseHelper      db;
    private List<CategoryView>  arCategoriesView = new ArrayList<CategoryView>();
    private LinearLayout        llCategories;
    private TextView            tvEmpty;

    private Button              btnExpense;
    private Button              btnBorrow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        /* Get data from Bundle */
        Bundle bundle                   = this.getArguments();
        mTagOfSource                    = bundle.getString("Tag");
        mCurrentTransactionType         = (TransactionEnum)bundle.get("TransactionType");
        mUsingCategoryId                = bundle.getInt("CategoryID", 0);

        LogUtils.trace(Tag, "mTagOfSource = " + mTagOfSource);
        LogUtils.trace(Tag, "mCurrentTransactionType = " + mCurrentTransactionType);
        LogUtils.trace(Tag, "mUsingCategoryId = " + mUsingCategoryId);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        // Set this fragment tag to ActivityMain
        String myTag = getTag();
        ((ActivityMain)getActivity()).setFragmentNewTransactionSelectCategory(myTag);

        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_category_select, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onActivityCreated(savedInstanceState);

        /* Initialize Database, insert default category */
        db = new DatabaseHelper(getActivity());

        btnExpense      = (Button) getView().findViewById(R.id.btnExpense);
        btnExpense.setText(getResources().getString((mCurrentTransactionType == TransactionEnum.Expense || mCurrentTransactionType == TransactionEnum.Transfer)
                                                        ? R.string.expense_category : R.string.income_category));
        btnExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Update background of button */
                btnExpense.setBackgroundResource(R.drawable.background_button_left_case_selected);
                btnExpense.setTextColor(getResources().getColorStateList(R.color.button_textcolor_2));
                btnBorrow.setBackgroundResource(R.drawable.background_button_right_case);
                btnBorrow.setTextColor(getResources().getColorStateList(R.color.button_textcolor));

                /* Change datasource and update listview */
                arCategoriesView.clear();
                List<Category> arParentCategories = db.getAllParentCategories(((mCurrentTransactionType == TransactionEnum.Expense || mCurrentTransactionType == TransactionEnum.Transfer) ? true : false), false);
                for (Category category : arParentCategories) {
                    arCategoriesView.add(new CategoryView(category, true));
                    List<Category> arCategories = db.getCategoriesByParent(category.getId());
                    for (Category cate : arCategories) {
                        arCategoriesView.add(new CategoryView(cate, true));
                    }
                }

                updateListCategories();

            }
        });

        btnBorrow       = (Button) getView().findViewById(R.id.btnBorrow);
        btnBorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Update background of button */
                btnExpense.setBackgroundResource(R.drawable.background_button_left_case);
                btnExpense.setTextColor(getResources().getColorStateList(R.color.button_textcolor));
                btnBorrow.setBackgroundResource(R.drawable.background_button_right_case_selected);
                btnBorrow.setTextColor(getResources().getColorStateList(R.color.button_textcolor_2));

                /* Change datasource and update listview */
                arCategoriesView.clear();
                List<Category> arParentCategories = db.getAllCategories(((mCurrentTransactionType == TransactionEnum.Expense || mCurrentTransactionType == TransactionEnum.Transfer) ? true : false), true);
                for(Category category : arParentCategories) {
                    arCategoriesView.add(new CategoryView(category, category.getParentId() == 0 ? true : false));
                    List<Category> arCategories = db.getCategoriesByParent(category.getId());
                    for(Category cate : arCategories) {
                        arCategoriesView.add(new CategoryView(cate, true));
                    }
                }

                updateListCategories();
            }
        });

        llCategories    = (LinearLayout) getView().findViewById(R.id.llCategories);
        tvEmpty         = (TextView) getView().findViewById(R.id.tvEmpty);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreateOptionsMenu(menu, inflater);

        /* Init ActionBar */
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_add, null);
        TextView tvTitle    = (TextView) mCustomView.findViewById(R.id.tvTitle);

        // Change title of fragment
        if(mCurrentTransactionType == TransactionEnum.Expense ||
                mCurrentTransactionType == TransactionEnum.Transfer ||
                mCurrentTransactionType == TransactionEnum.Adjustment  ) {
            tvTitle.setText(getResources().getString(R.string.title_category_expense));
        } else if(mCurrentTransactionType == TransactionEnum.Income) {
            tvTitle.setText(getResources().getString(R.string.title_category_income));
        }

        /* Click on button Add to add new Category */
        ImageView ivAdd = (ImageView) mCustomView.findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.trace(Tag, "Click Menu Action Add Category.");
                FragmentCategoryCreate nextFrag = new FragmentCategoryCreate();
                Bundle bundle = new Bundle();
                bundle.putSerializable("TransactionType", mCurrentTransactionType);
                nextFrag.setArguments(bundle);
                FragmentCategorySelect.this.getFragmentManager().beginTransaction()
                        .add(R.id.layout_transaction_create, nextFrag, "FragmentCategoryCreate")
                        .addToBackStack(null)
                        .commit();
            }
        });

        ((ActivityMain)getActivity()).updateActionBar(mCustomView);

        arCategoriesView.clear();
        List<Category> arParentCategories = db.getAllCategories(((mCurrentTransactionType == TransactionEnum.Expense || mCurrentTransactionType == TransactionEnum.Transfer) ? true : false), false);
        for(Category category : arParentCategories) {
            arCategoriesView.add(new CategoryView(category, category.getParentId() == 0 ? true : false));
            List<Category> arCategories = db.getCategoriesByParent(category.getId());
            for(Category cate : arCategories) {
                arCategoriesView.add(new CategoryView(cate, true));
            }
        }

        /* Show/Hide TextView Empty Category */
        if(arParentCategories.size() > 0) {
            tvEmpty.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
        }

        updateListCategories();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * CategoryView: Use to control show/hide category in listview
     */
    private class CategoryView {
        Category category;
        boolean isShow;

        public CategoryView(Category category, boolean isShow) {
            this.category = category;
            this.isShow = isShow;
        }

        @Override
        public String toString() {
            return "CategoryView{" +
                    "category=" + category.toString() +
                    ", isShow=" + isShow +
                    '}';
        }
    }

    private void updateListCategories() {
        LogUtils.logEnterFunction(Tag, null);
        llCategories.removeAllViews();

        LayoutInflater mInflater = LayoutInflater.from(getActivity());

        for(final CategoryView category : arCategoriesView) {

            if(!category.isShow) {
                continue;
            }
            View cateView               = mInflater.inflate(R.layout.listview_item_category, null);
            LinearLayout llCategory     = (LinearLayout) cateView.findViewById(R.id.llCategory);
            final ImageView ivExpand    = (ImageView) cateView.findViewById(R.id.ivExpand);
            TextView tvName             = (TextView) cateView.findViewById(R.id.tvParentCategory);
            ImageView ivUsing           = (ImageView) cateView.findViewById(R.id.ivUsing);

            llCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mTagOfSource.equals(FragmentTransactionCreate.Tag)) {

                        LogUtils.trace(Tag, "Setup for TransactionCreate");
                        FragmentTransactionCreate fragment = (FragmentTransactionCreate)((ActivityMain)getActivity()).getFragment(ActivityMain.TAB_POSITION_TRANSACTION_CREATE);
                        fragment.updateCategory(mCurrentTransactionType, category.category.getId());

                    } else if(mTagOfSource.equals(((ActivityMain) getActivity()).getFragmentTransactionUpdate())) {

                        LogUtils.trace(Tag, "Setup for TransactionUpdate");
                        String tagOfFragment = ((ActivityMain) getActivity()).getFragmentTransactionUpdate();
                        FragmentTransactionUpdate fragment = (FragmentTransactionUpdate) getActivity().getSupportFragmentManager().findFragmentByTag(tagOfFragment);
                        fragment.updateCategory(mCurrentTransactionType, category.category.getId());

                    }

                    // Back to FragmentTransactionNew
                    getFragmentManager().popBackStackImmediate();
                }
            });

            final Animation expand = AnimationUtils.loadAnimation(getActivity(), R.anim.expand);
            expand.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    LogUtils.trace(Tag, "expand Animation");
                    for (CategoryView cate : arCategoriesView) {
                        if (cate.category.getParentId() == category.category.getId()) {
                            cate.isShow = true;
                        }
                    }

                    updateListCategories();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            final Animation shrink = AnimationUtils.loadAnimation(getActivity(), R.anim.shrink);
            shrink.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    LogUtils.trace(Tag, "shrink Animation");
                    for (CategoryView cate : arCategoriesView) {
                        if (cate.category.getParentId() == category.category.getId()) {
                            cate.isShow = false;
                        }
                    }

                    updateListCategories();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            ivExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (CategoryView cate : arCategoriesView) {
                        if (cate.category.getParentId() == category.category.getId()) {
                            if(cate.isShow) {
                                v.startAnimation(shrink);
                            } else {
                                v.startAnimation(expand);
                            }

                            break;
                        }
                    }
                }
            });

            for (CategoryView cate : arCategoriesView) {
                if (cate.category.getParentId() == category.category.getId()) {
                    if(cate.isShow) {
                        ivExpand.setImageResource(R.drawable.icon_expanding);
                    } else {
                        ivExpand.setImageResource(R.drawable.icon_shrinking);
                    }
                    break;
                }
            }

            if(category.category.getParentId() != 0) {
                ivExpand.setVisibility(View.INVISIBLE);
                llCategory.setBackgroundColor(getResources().getColor(android.R.color.white));
            } else {
                ivExpand.setVisibility(View.VISIBLE);
                llCategory.setBackgroundColor(getResources().getColor(R.color.listview_parent_item_background));
            }

            tvName.setText(category.category.getName());

            if(mUsingCategoryId == category.category.getId()) {
                ivUsing.setVisibility(View.VISIBLE);
            } else {
                ivUsing.setVisibility(View.INVISIBLE);
            }

            llCategories.addView(cateView);
        }

        LogUtils.logLeaveFunction(Tag, null, null);

    } // End updateListCategories

}
