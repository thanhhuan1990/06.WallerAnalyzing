package local.wallet.analyzing.transaction;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Category.EnumDebt;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentTransactionSelectCategory extends Fragment {

    public interface ISelectCategory extends Serializable {
        void onCategorySelected(int categoryId);
    }

    private int                 mTab = 1;
    public final String         Tag = "---[" + mTab + "]---TransactionSelectCategory";

    private ActivityMain        mActivity;

    private int                 mContainerViewId = -1;

    private boolean             isExpense = true;
    private int                 mUsingCategoryId;

    private DatabaseHelper      mDbHelper;
    private List<CategoryView>  arCategoriesView = new ArrayList<CategoryView>();
    private LinearLayout        llCategories;
    private TextView            tvEmpty;

    private Button              btnExpense;
    private Button              btnDebt;

    private ISelectCategory     mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        /* Get data from Bundle */
        Bundle bundle                   = this.getArguments();

        mTab                            = bundle.getInt("Tab", mTab);
        isExpense                       = bundle.getBoolean("CategoryType");
        mUsingCategoryId                = bundle.getInt("CategoryID", 0);
        mCallback                       = (ISelectCategory) bundle.getSerializable("Callback");

        // Retry ContainerViewId from bundle
        if(bundle.getInt("ContainerViewId") != 0) {
            mContainerViewId = bundle.getInt("ContainerViewId");
        } else {
            mContainerViewId    = mUsingCategoryId != 0 ? R.id.ll_transaction_update : R.id.ll_transaction_create;
        }

        LogUtils.trace(Tag, "mUsingCategoryId = " + mUsingCategoryId);

        LogUtils.logLeaveFunction(Tag);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        LogUtils.logLeaveFunction(Tag);
        return inflater.inflate(R.layout.layout_fragment_category_select, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);

        super.onActivityCreated(savedInstanceState);

        mActivity       = (ActivityMain) getActivity();
        /* Initialize Database, insert default category */
        mDbHelper = new DatabaseHelper(getActivity());

        btnExpense      = (Button) getView().findViewById(R.id.btnExpense);
        btnExpense.setText(getResources().getString(isExpense ? R.string.expense_category : R.string.income_category));
        btnExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Update background of button */
                btnExpense.setBackgroundResource(R.drawable.background_button_left_case_selected);
                btnExpense.setTextColor(getResources().getColorStateList(R.color.button_textcolor_2));
                btnDebt.setBackgroundResource(R.drawable.background_button_right_case);
                btnDebt.setTextColor(getResources().getColorStateList(R.color.button_textcolor));

                /* Change datasource and update listview */
                arCategoriesView.clear();
                List<Category> arParentCategories = mDbHelper.getAllParentCategories(isExpense, EnumDebt.NONE);
                for (Category category : arParentCategories) {
                    arCategoriesView.add(new CategoryView(category, true));
                    List<Category> arCategories = mDbHelper.getCategoriesByParent(category.getId());
                    for (Category cate : arCategories) {
                        arCategoriesView.add(new CategoryView(cate, true));
                    }
                }

                updateListCategories();

            }
        });

        btnDebt = (Button) getView().findViewById(R.id.btnDebt);
        btnDebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Update background of button */
                btnExpense.setBackgroundResource(R.drawable.background_button_left_case);
                btnExpense.setTextColor(getResources().getColorStateList(R.color.button_textcolor));
                btnDebt.setBackgroundResource(R.drawable.background_button_right_case_selected);
                btnDebt.setTextColor(getResources().getColorStateList(R.color.button_textcolor_2));

                /* Change datasource and update listview */
                arCategoriesView.clear();
                List<Category> arParentLendCategories = mDbHelper.getAllCategories(isExpense, EnumDebt.MORE);
                for (Category category : arParentLendCategories) {
                    arCategoriesView.add(new CategoryView(category, category.getParentId() == 0 ? true : false));
                    List<Category> arCategories = mDbHelper.getCategoriesByParent(category.getId());
                    for (Category cate : arCategories) {
                        arCategoriesView.add(new CategoryView(cate, true));
                    }
                }

                List<Category> arParentRepaymentCategories = mDbHelper.getAllCategories(isExpense, EnumDebt.LESS);
                for (Category category : arParentRepaymentCategories) {
                    arCategoriesView.add(new CategoryView(category, category.getParentId() == 0 ? true : false));
                    List<Category> arCategories = mDbHelper.getCategoriesByParent(category.getId());
                    for (Category cate : arCategories) {
                        arCategoriesView.add(new CategoryView(cate, true));
                    }
                }

                updateListCategories();
            }
        });

        llCategories    = (LinearLayout) getView().findViewById(R.id.llCategories);
        tvEmpty         = (TextView) getView().findViewById(R.id.tvEmpty);

        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag);
        super.onCreateOptionsMenu(menu, inflater);

        if(mTab != mActivity.getCurrentVisibleItem()) {
            LogUtils.error(Tag, "Wrong tab: " + mTab + " vs " + mActivity.getCurrentVisibleItem());
            LogUtils.logLeaveFunction(Tag);
            return;
        }

        /* Init ActionBar */
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_add, null);
        TextView tvTitle    = (TextView) mCustomView.findViewById(R.id.tvTitle);

        // Change title of fragment
        if(isExpense) {
            tvTitle.setText(getResources().getString(R.string.title_category_expense));
        } else {
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
                bundle.putInt("Tab", mTab);
                bundle.putBoolean("CategoryType", isExpense);
                bundle.putInt("ContainerID", mUsingCategoryId != 0 ? R.id.ll_transaction_update : R.id.ll_transaction_create);
                nextFrag.setArguments(bundle);
                mActivity.addFragment(mTab, mContainerViewId, nextFrag, FragmentCategoryCreate.Tag, true);
            }
        });

        ((ActivityMain)getActivity()).updateActionBar(mCustomView);

        arCategoriesView.clear();

        List<Category> arParentCategories = mDbHelper.getAllCategories(isExpense, EnumDebt.NONE);
        for(Category category : arParentCategories) {
            arCategoriesView.add(new CategoryView(category, category.getParentId() == 0 ? true : false));
            List<Category> arCategories = mDbHelper.getCategoriesByParent(category.getId());
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

        // Update right list: Expense / Debt
        if(mUsingCategoryId != 0) { // Re-select category
            boolean isRightList = false;
            for(CategoryView cate : arCategoriesView) {
                if(cate.category.getId() == mUsingCategoryId) {
                    isRightList = true;
                }
            }

            if(isRightList) {
                btnExpense.performClick();
            } else {
                btnDebt.performClick();
            }
        } else { // Select first time
            btnExpense.performClick();
        }

        LogUtils.logLeaveFunction(Tag);
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
        LogUtils.logEnterFunction(Tag);
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
                    mCallback.onCategorySelected(category.category.getId());

                    // Back
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

        LogUtils.logLeaveFunction(Tag);

    } // End updateListCategories

}
