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

    private static final String TAG = "FragmentCategorySelect";

    private String mTagOfSource = "";
    private TransactionEnum mCurrentTransactionType     = TransactionEnum.Expense;
    private int mUsingCategoryId;

    private DatabaseHelper db;
    private List<CategoryView> arCategoriesView = new ArrayList<CategoryView>();
    private CategoryAdapter categoryAdapter;

    private ListView lvCategory;
    private TextView tvEmpty;

    private Button  btnExpense;
    private Button  btnBorrow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        /* Get data from Bundle */
        Bundle bundle                   = this.getArguments();
        mTagOfSource                    = bundle.getString("Tag");
        mCurrentTransactionType         = (TransactionEnum)bundle.get("TransactionType");
        mUsingCategoryId                = bundle.getInt("CategoryID", 0);

        LogUtils.trace(TAG, "mTagOfSource = " + mTagOfSource);
        LogUtils.trace(TAG, "mCurrentTransactionType = " + mCurrentTransactionType);
        LogUtils.trace(TAG, "mUsingCategoryId = " + mUsingCategoryId);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        // Set this fragment tag to ActivityMain
        String myTag = getTag();
        ((ActivityMain)getActivity()).setFragmentNewTransactionSelectCategory(myTag);

        LogUtils.logLeaveFunction(TAG, null, null);
        return inflater.inflate(R.layout.layout_fragment_category_select, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

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
                for(Category category : arParentCategories) {
                    arCategoriesView.add(new CategoryView(category, true));
                    List<Category> arCategories = db.getCategoriesByParent(category.getId());
                    for(Category cate : arCategories) {
                        arCategoriesView.add(new CategoryView(cate, true));
                    }
                }

                categoryAdapter = new CategoryAdapter(getActivity(), arCategoriesView);
                lvCategory.setAdapter(categoryAdapter);
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
                categoryAdapter = new CategoryAdapter(getActivity(), arCategoriesView);
                lvCategory.setAdapter(categoryAdapter);
            }
        });

        tvEmpty         = (TextView) getView().findViewById(R.id.tvEmpty);
        lvCategory      = (ListView) getView().findViewById(R.id.lvCategory);

        /* Change datasource and update listview */
        arCategoriesView.clear();
        List<Category> arParentCategories = db.getAllCategories(((mCurrentTransactionType == TransactionEnum.Expense || mCurrentTransactionType == TransactionEnum.Transfer) ? true : false), false);
        for(Category category : arParentCategories) {
            arCategoriesView.add(new CategoryView(category, category.getParentId() == 0 ? true : false));
            List<Category> arCategories = db.getCategoriesByParent(category.getId());
            for(Category cate : arCategories) {
                arCategoriesView.add(new CategoryView(cate, true));
            }
        }
        categoryAdapter = new CategoryAdapter(getActivity(), arCategoriesView);
        lvCategory.setAdapter(categoryAdapter);

        /* Click on listview item to select category*/
        lvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mTagOfSource.equals(FragmentTransactionCreate.Tag)) {

                    LogUtils.trace(TAG, "Setup for FragmentTransactionCreate");
                    FragmentTransactionCreate fragment = (FragmentTransactionCreate)((ActivityMain)getActivity()).getFragment(ActivityMain.TAB_POSITION_TRANSACTION_CREATE);
                    fragment.updateCategory(mCurrentTransactionType, arCategoriesView.get(position).category.getId());

                } else if(mTagOfSource.equals(((ActivityMain) getActivity()).getFragmentTransactionUpdate())) {

                    LogUtils.trace(TAG, "Setup for FragmentTransactionUpdate");
                    String tagOfFragment = ((ActivityMain) getActivity()).getFragmentTransactionUpdate();
                    FragmentTransactionUpdate fragment = (FragmentTransactionUpdate) getActivity().getSupportFragmentManager().findFragmentByTag(tagOfFragment);
                    fragment.updateCategory(mCurrentTransactionType, arCategoriesView.get(position).category.getId());

                }

                // Back to FragmentTransactionNew
                getFragmentManager().popBackStackImmediate();
            }
        });

        /* Show/Hide TextView Empty Category */
        if(arParentCategories.size() > 0) {
            tvEmpty.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
        }
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);
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
                LogUtils.trace(TAG, "Click Menu Action Add Category.");
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

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    public void updateListCategory() {
        LogUtils.logEnterFunction(TAG, null);
        btnExpense.performClick();
        LogUtils.logLeaveFunction(TAG, null, null);
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

    /**
     * CategoryAdapter: Adapter of category's listview
     */
    private class CategoryAdapter extends ArrayAdapter<CategoryView> {

        private class ViewHolder {
            LinearLayout    llCategory;
            ImageView       ivExpand;
            TextView        tvName;
            ImageView       ivUsing;
        }

        private List<CategoryView> arCategoriesView;

        public CategoryAdapter(Context context, List<CategoryView> items) {
            super(context, R.layout.listview_item_category,items);
            this.arCategoriesView = items;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(getContext());

            if(!arCategoriesView.get(position).isShow) {
                return inflater.inflate(R.layout.listview_item_null, null);
            }

            final ViewHolder viewHolder;

            if (convertView == null || convertView.getTag() == null) {
                viewHolder = new ViewHolder();

                convertView = inflater.inflate(R.layout.listview_item_category, parent, false);
                viewHolder.llCategory   = (LinearLayout) convertView.findViewById(R.id.llCategory);
                viewHolder.ivExpand     = (ImageView) convertView.findViewById(R.id.ivExpand);
                viewHolder.tvName       = (TextView) convertView.findViewById(R.id.tvParentCategory);
                viewHolder.ivUsing      = (ImageView) convertView.findViewById(R.id.ivUsing);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if(arCategoriesView.get(position).category.getParentId() == 0) {
                viewHolder.llCategory.setBackgroundColor(getResources().getColor(R.color.listview_parent_item_background));
            } else {
                viewHolder.llCategory.setBackgroundColor(getResources().getColor(android.R.color.white));
            }

            final Animation expand = AnimationUtils.loadAnimation(getActivity(), R.anim.expand);
            expand.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    for (CategoryView cate : arCategoriesView) {
                        if (cate.category.getParentId() == arCategoriesView.get(position).category.getId()) {
                            cate.isShow = true;
                        }
                    }

                    notifyDataSetChanged();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            final Animation shrink = AnimationUtils.loadAnimation(getActivity(), R.anim.shrink);
            shrink.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    for (CategoryView cate : arCategoriesView) {
                        if (cate.category.getParentId() == arCategoriesView.get(position).category.getId()) {
                            cate.isShow = false;
                        }
                    }

                    notifyDataSetChanged();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            viewHolder.ivExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (viewHolder.ivExpand.getTag().toString().equals("shrink")) {
                        viewHolder.ivExpand.startAnimation(expand);
                        viewHolder.ivExpand.setTag("expand");

                    } else if (viewHolder.ivExpand.getTag().toString().equals("expand")) {
                        viewHolder.ivExpand.startAnimation(shrink);
                        viewHolder.ivExpand.setTag("shrink");
                    }

                }
            });

            if(arCategoriesView.get(position).category.getParentId() != 0) {
                viewHolder.ivExpand.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.ivExpand.setVisibility(View.VISIBLE);
            }

            viewHolder.tvName.setText(arCategoriesView.get(position).category.getName());

            if(mUsingCategoryId == arCategoriesView.get(position).category.getId()) {
                viewHolder.ivUsing.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivUsing.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

    }

}
