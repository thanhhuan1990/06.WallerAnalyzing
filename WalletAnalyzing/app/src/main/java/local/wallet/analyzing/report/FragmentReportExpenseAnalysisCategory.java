package local.wallet.analyzing.report;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/19/2016.
 */
public class FragmentReportExpenseAnalysisCategory extends Fragment implements CompoundButton.OnCheckedChangeListener {
    public static int               mTab = 4;
    public static final String      Tag = "---[" + mTab + ".2]---ReportExpenseAnalysisCategory";

    private ActivityMain            mActivity;

    public interface ISelectReportExpenseAnalysisCategory {
        void onReportExpenseAnalysisCategorySelected(int[] categories);
    }

    private DatabaseHelper      mDbHelper;

    private ToggleButton        tbAllCategory;

    private ImageView           ivIncomeExpand;
    private CheckBox            cbSelectedIncome;
    private LinearLayout        llIncomeCategories;
    private List<CategoryView>  arCategoriesIncomeView = new ArrayList<CategoryView>();

    private ImageView           ivExpenseExpand;
    private CheckBox            cbSelectedExpense;
    private LinearLayout        llExpenseCategories;
    private List<CategoryView>  arCategoriesExpenseView = new ArrayList<CategoryView>();
    private int[]               arCategories;

    private ISelectReportExpenseAnalysisCategory    mCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        /* Get data from Bundle */
        Bundle bundle       = this.getArguments();
        mTab                = bundle.getInt("Tab", mTab);
        arCategories        = bundle.getIntArray("Categories");
        mCallback           = (ISelectReportExpenseAnalysisCategory) bundle.getSerializable("Callback");

        LogUtils.trace(Tag, "Categories = " + arCategories != null ? Arrays.toString(arCategories) : "''");

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End onCreate

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        LogUtils.logLeaveFunction(Tag, null, null);

        return inflater.inflate(R.layout.layout_fragment_report_expense_analysis_category, container, false);
    } // End onCreateView

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mActivity       = (ActivityMain) getActivity();

        mDbHelper       = new DatabaseHelper(getActivity());

        initIncomeView();
        initExpenseView();

        tbAllCategory   = (ToggleButton) getView().findViewById(R.id.tbAllCategory);
        tbAllCategory.setOnCheckedChangeListener(this);
        if(arCategories == null || arCategories.length == mDbHelper.getAllCategories().size()) {
            tbAllCategory.setChecked(true);
        } else {
            tbAllCategory.setChecked(false);
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End onActivityCreated

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreateOptionsMenu(menu, inflater);

        if(mTab != mActivity.getCurrentVisibleItem()) {
            LogUtils.error(Tag, "Wrong Tab. Return");
            LogUtils.logLeaveFunction(Tag, null, null);
            return;
        }

        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        View mCustomView            = mInflater.inflate(R.layout.action_bar_with_button_done, null);
        TextView tvTitle            = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_report_expense_analysis_category));

        ImageView ivDone = (ImageView) mCustomView.findViewById(R.id.ivDone);
        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Integer> arSelectedId = new ArrayList<Integer>();
                for (int i = 0; i < arCategoriesIncomeView.size(); i++) {
                    if (arCategoriesIncomeView.get(i).isChecked) {
                        arSelectedId.add(arCategoriesIncomeView.get(i).category.getId());
                    }
                }
                for (int i = 0; i < arCategoriesExpenseView.size(); i++) {
                    if (arCategoriesExpenseView.get(i).isChecked) {
                        arSelectedId.add(arCategoriesExpenseView.get(i).category.getId());
                    }
                }

                if (arSelectedId.size() > 0) {
                    int[] categories = new int[arSelectedId.size()];
                    for (int i = 0; i < arSelectedId.size(); i++) {
                        categories[i] = arSelectedId.get(i);
                    }

                    LogUtils.trace(Tag, "Categories: " + Arrays.toString(categories));

                    mCallback.onReportExpenseAnalysisCategorySelected(categories);

                    getFragmentManager().popBackStackImmediate();
                } else {
                    ((ActivityMain) getActivity()).showError(getResources().getString(R.string.Input_Error_budget_category_none));
                }


            }
        });

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End onCreateOptionsMenu

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LogUtils.logEnterFunction(Tag, "isChecked = " + isChecked);
        switch (buttonView.getId()) {
            case R.id.cbSelectedIncome:
                for(int i = 0 ; i < arCategoriesIncomeView.size(); i++) {
                    arCategoriesIncomeView.get(i).isChecked = isChecked;
                }
                updateListCategories(llIncomeCategories, arCategoriesIncomeView);
                break;
            case R.id.cbSelectedExpense:
                for(int i = 0 ; i < arCategoriesExpenseView.size(); i++) {
                    arCategoriesExpenseView.get(i).isChecked = isChecked;
                }
                updateListCategories(llExpenseCategories, arCategoriesExpenseView);
                break;
            case R.id.tbAllCategory:
                cbSelectedIncome.setChecked(isChecked);
                cbSelectedExpense.setChecked(isChecked);
                break;
            default:
                break;
        }

        LogUtils.logLeaveFunction(Tag, "isChecked = " + isChecked, null);
    } // End onCheckedChanged

    private void initIncomeView() {
        final Animation expandIncome = AnimationUtils.loadAnimation(getActivity(), R.anim.expand_no_refresh);
        expandIncome.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LogUtils.trace(Tag, "expand Animation");
                llIncomeCategories.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        final Animation shrinkIncome = AnimationUtils.loadAnimation(getActivity(), R.anim.shrink_no_refresh);
        shrinkIncome.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                LogUtils.trace(Tag, "shrink Animation");
                llIncomeCategories.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        ivIncomeExpand          = (ImageView) getView().findViewById(R.id.ivIncomeExpand);
        ivIncomeExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llIncomeCategories.getVisibility() == View.VISIBLE) {
                    v.startAnimation(shrinkIncome);
                } else {
                    v.startAnimation(expandIncome);
                }

            }
        });

        cbSelectedIncome        = (CheckBox) getView().findViewById(R.id.cbSelectedIncome);
        cbSelectedIncome.setOnCheckedChangeListener(this);

        boolean isAllIncome = true;

        llIncomeCategories    = (LinearLayout) getView().findViewById(R.id.llIncomeCategories);
        arCategoriesIncomeView.clear();
        List<Category> arParentCategoriesIncome = mDbHelper.getAllParentCategories(false);
        for(Category category : arParentCategoriesIncome) {

            arCategoriesIncomeView.add(new CategoryView(category, true, checkContain(category.getId())));

            List<Category> arChildCategories = mDbHelper.getCategoriesByParent(category.getId());

            for(Category cate : arChildCategories) {

                arCategoriesIncomeView.add(new CategoryView(cate, true, checkContain(cate.getId())));

            }
        }
//        List<Category> arParentCategoriesIncomeLoan = mDbHelper.getAllParentCategories(false, true);
//        for(Category category : arParentCategoriesIncomeLoan) {
//
//            arCategoriesIncomeView.add(new CategoryView(category, true, checkContain(category.getId())));
//
//            List<Category> arChildCategories = mDbHelper.getCategoriesByParent(category.getId());
//
//            for(Category cate : arChildCategories) {
//
//                arCategoriesIncomeView.add(new CategoryView(cate, true, checkContain(cate.getId())));
//
//            }
//        }

        for(CategoryView cateView : arCategoriesIncomeView) {
            if(!checkContain(cateView.category.getId())) {
                isAllIncome = false;
                break;
            }
        }

        cbSelectedIncome.setChecked(isAllIncome);

        updateListCategories(llIncomeCategories, arCategoriesIncomeView);
    } // end initIncomeView

    private void initExpenseView() {
        final Animation expandExpense = AnimationUtils.loadAnimation(getActivity(), R.anim.expand_no_refresh);
        expandExpense.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LogUtils.trace(Tag, "expand Animation");
                llExpenseCategories.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        final Animation shrinkExpense = AnimationUtils.loadAnimation(getActivity(), R.anim.shrink_no_refresh);
        shrinkExpense.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LogUtils.trace(Tag, "shrink Animation");
                llExpenseCategories.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        ivExpenseExpand         = (ImageView) getView().findViewById(R.id.ivExpenseExpand);
        ivExpenseExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llExpenseCategories.getVisibility() == View.VISIBLE) {
                    v.startAnimation(shrinkExpense);
                } else {
                    v.startAnimation(expandExpense);
                }

            }
        });

        cbSelectedExpense        = (CheckBox) getView().findViewById(R.id.cbSelectedExpense);
        cbSelectedExpense.setOnCheckedChangeListener(this);

        boolean isAllExpense = true;

        llExpenseCategories     = (LinearLayout) getView().findViewById(R.id.llExpenseCategories);
        arCategoriesExpenseView.clear();
        List<Category> arParentCategoriesExpense = mDbHelper.getAllParentCategories(true);
        for(Category category : arParentCategoriesExpense) {

            arCategoriesExpenseView.add(new CategoryView(category, true, checkContain(category.getId())));

            List<Category> arChildCategories = mDbHelper.getCategoriesByParent(category.getId());

            for(Category cate : arChildCategories) {

                arCategoriesExpenseView.add(new CategoryView(cate, true, checkContain(cate.getId())));

            }
        }
//        List<Category> arParentCategoriesExpenseLoan = mDbHelper.getAllParentCategories(true, true);
//        for(Category category : arParentCategoriesExpenseLoan) {
//
//            arCategoriesExpenseView.add(new CategoryView(category, true, checkContain(category.getId())));
//
//            List<Category> arChildCategories = mDbHelper.getCategoriesByParent(category.getId());
//
//            for(Category cate : arChildCategories) {
//
//                arCategoriesExpenseView.add(new CategoryView(cate, true, checkContain(cate.getId())));
//
//            }
//        }

        for(CategoryView cateView : arCategoriesExpenseView) {
            if(!checkContain(cateView.category.getId())) {
                isAllExpense = false;
                break;
            }
        }

        cbSelectedExpense.setChecked(isAllExpense);

        updateListCategories(llExpenseCategories, arCategoriesExpenseView);
    } // end initExpenseView

    /**
     * Update list of Category
     */
    private void updateListCategories(final LinearLayout layout, final List<CategoryView>  arCategoriesView) {
        LogUtils.logEnterFunction(Tag, null);
        layout.removeAllViews();

        LayoutInflater mInflater = LayoutInflater.from(getActivity());

        for(final CategoryView category : arCategoriesView) {

            if(!category.isShow) {
                continue;
            }
            View cateView               = mInflater.inflate(R.layout.listview_item_report_expense_analysis_category, null);
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

                    updateListCategories(layout, arCategoriesView);
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

                    updateListCategories(layout, arCategoriesView);
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
                    layout.removeAllViews();
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

                    tbAllCategory.setOnCheckedChangeListener(null);
                    tbAllCategory.setChecked(isAll);
                    tbAllCategory.setOnCheckedChangeListener(FragmentReportExpenseAnalysisCategory.this);

                    updateListCategories(layout, arCategoriesView);
                } // End onCheckedChanged
            }); // End setOnCheckedChangeListener

            layout.addView(cateView);
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
