package local.wallet.analyzing.budget;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Category.EnumDebt;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/19/2016.
 */
public class FragmentBudgetCategory extends Fragment implements CompoundButton.OnCheckedChangeListener {

    public static final String Tag = "BudgetCategory";

    public interface ISelectBudgetCategory extends Serializable {
        void onBudgetCategorySelected(int[] categories);
    }

    private DatabaseHelper      mDbHelper;

    private ToggleButton        tbAllCategory;
    private LinearLayout        llCategories;
    private List<CategoryView>  arCategoriesView = new ArrayList<CategoryView>();
    private int[]               arCategories;

    private ISelectBudgetCategory   mCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        /* Get data from Bundle */
        Bundle bundle   = this.getArguments();
        arCategories    = bundle.getIntArray("Categories");
        mCallback       = (ISelectBudgetCategory) bundle.getSerializable("Callback");

        LogUtils.trace(Tag, "Categories = " + arCategories != null ? Arrays.toString(arCategories) : "''");

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End onCreate

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        LogUtils.logLeaveFunction(Tag, null, null);

        return inflater.inflate(R.layout.layout_fragment_budget_category, container, false);
    } // End onCreateView

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);

        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        View mCustomView            = mInflater.inflate(R.layout.action_bar_with_button_done, null);
        TextView tvTitle            = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_category_expense));

        ImageView ivDone = (ImageView) mCustomView.findViewById(R.id.ivDone);
        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Integer> arSelectedId = new ArrayList<Integer>();
                for(int i = 0; i < arCategoriesView.size(); i++) {
                    if(arCategoriesView.get(i).isChecked) {
                        arSelectedId.add(arCategoriesView.get(i).category.getId());
                    }
                }

                if(arSelectedId.size() > 0) {
                    int[] categories = new int[arSelectedId.size()];
                    for(int i = 0 ; i < arSelectedId.size(); i++) {
                        categories[i] = arSelectedId.get(i);
                    }

                    LogUtils.trace(Tag, "Categories: " + Arrays.toString(categories));

                    mCallback.onBudgetCategorySelected(categories);

                    getFragmentManager().popBackStackImmediate();
                } else {
                    ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_budget_category_none));
                }


            }
        });

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        super.onCreateOptionsMenu(menu, inflater);

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End onCreateOptionsMenu

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onActivityCreated(savedInstanceState);

        mDbHelper = new DatabaseHelper(getActivity());

        llCategories    = (LinearLayout) getView().findViewById(R.id.llCategories);
        arCategoriesView.clear();
        List<Category> arParentCategories = mDbHelper.getAllParentCategories(true, EnumDebt.NONE);
        for(Category category : arParentCategories) {

            arCategoriesView.add(new CategoryView(category, true, checkContain(category.getId())));

            List<Category> arChildCategories = mDbHelper.getCategoriesByParent(category.getId());

            for(Category cate : arChildCategories) {

                arCategoriesView.add(new CategoryView(cate, true, checkContain(cate.getId())));

            }
        }

        updateListCategories();

        tbAllCategory   = (ToggleButton) getView().findViewById(R.id.tbAllCategory);
        tbAllCategory.setOnCheckedChangeListener(this);
        if(arCategories == null || arCategories.length == mDbHelper.getAllCategories(true, EnumDebt.NONE).size()) {
            tbAllCategory.setChecked(true);
        } else {
            tbAllCategory.setChecked(false);
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End onActivityCreated

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LogUtils.logEnterFunction(Tag, "isChecked = " + isChecked);
        for(int i = 0 ; i < arCategoriesView.size(); i++) {
            arCategoriesView.get(i).isChecked = isChecked;
        }

        updateListCategories();
        LogUtils.logLeaveFunction(Tag, "isChecked = " + isChecked, null);
    } // End onCheckedChanged

    /**
     * Update list of Category
     */
    private void updateListCategories() {
        LogUtils.logEnterFunction(Tag, null);
        llCategories.removeAllViews();

        LayoutInflater mInflater = LayoutInflater.from(getActivity());

        for(final CategoryView category : arCategoriesView) {

            if(!category.isShow) {
                continue;
            }
            View cateView               = mInflater.inflate(R.layout.listview_item_budget_category, null);
            LinearLayout llCategory     = (LinearLayout) cateView.findViewById(R.id.llCategory);
            ImageView ivExpand          = (ImageView) cateView.findViewById(R.id.ivExpand);
            TextView tvName             = (TextView) cateView.findViewById(R.id.tvParentCategory);
            CheckBox cbSelected         = (CheckBox) cateView.findViewById(R.id.cbSelected);

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

            cbSelected.setChecked(category.isChecked);
            cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    LogUtils.trace(Tag, "cbSelected.setOnCheckedChange");
                    llCategories.removeAllViews();
                    category.isChecked = isChecked;

                    if (category.category.getParentId() == 0) { // Click on parent category
                        for (CategoryView cate : arCategoriesView) {
                            if (cate.category.getParentId() == category.category.getId()) {
                                cate.isChecked = isChecked;
                            }
                        }
                    } else { // Click on child category
                        boolean isSame = true;
                        boolean isMany = false;
                        for (CategoryView cateView : arCategoriesView) {
                            if (cateView.category.getParentId() == category.category.getParentId() &&
                                    cateView.category.getId() != category.category.getId()) {
                                isMany = true;
                                if (cateView.isChecked != category.isChecked) {
                                    isSame = false;
                                }
                            }
                        }

                        for (CategoryView cateView : arCategoriesView) {
                            if (cateView.category.getId() == category.category.getParentId()) {
                                cateView.isChecked = isSame & isMany & isChecked;
                            }
                        }
                    }

                    boolean isAll = true;
                    for (CategoryView cateView : arCategoriesView) {
                        if (cateView.isChecked == false) {
                            isAll = false;
                            break;
                        }
                    }

                    tbAllCategory.setChecked(isAll);

                    updateListCategories();
                } // End onCheckedChanged
            }); // End setOnCheckedChangeListener

            llCategories.addView(cateView);
        }

        LogUtils.logLeaveFunction(Tag, null, null);

    } // End updateListCategories

    /**
     * Check ID is contain in list from BudgetCreateUpdateDelete
     * @param id
     * @return
     */
    private boolean checkContain(int id) {
        for(int i = 0 ; i < arCategories.length; i++) {
            if(arCategories[i] == id) {
                return true;
            }
        }

        return false;
    } // End checkContain

    /**
     * CategoryView: Use to control show/hide category in listview
     */
    private class CategoryView {
        Category category;
        boolean isShow;
        boolean isChecked;

        public CategoryView(Category category, boolean isShow, boolean isChecked) {
            this.category = category;
            this.isShow = isShow;
            this.isChecked  = isChecked;
        }

        @Override
        public String toString() {
            return "CategoryView{" +
                    "category = " + category.toString() +
                    ", isShow = " + isShow +
                    ", isChecked = " + isChecked +
                    '}';
        }
    } // End class CategoryView

}
