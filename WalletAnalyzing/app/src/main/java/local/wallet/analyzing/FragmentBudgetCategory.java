package local.wallet.analyzing;

import android.content.Context;
import android.nfc.Tag;
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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/19/2016.
 */
public class FragmentBudgetCategory extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG     = "BudgetCategory";

    private DatabaseHelper      db;

    private ToggleButton        tbAllCategory;
    private ListView            lvCategory;
    private List<CategoryView>  arCategoriesView = new ArrayList<CategoryView>();
    private CategoryAdapter     categoryAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);

        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        View mCustomView            = mInflater.inflate(R.layout.action_bar_with_button_done, null);
        TextView tvTitle            = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_category_expense));
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        super.onCreateOptionsMenu(menu, inflater);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        LogUtils.logLeaveFunction(TAG, null, null);

        return inflater.inflate(R.layout.layout_fragment_budget_category, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onActivityCreated(savedInstanceState);

        db = new DatabaseHelper(getActivity());

        tbAllCategory   = (ToggleButton) getView().findViewById(R.id.tbAllCategory);
        tbAllCategory.setChecked(true);
        tbAllCategory.setOnCheckedChangeListener(this);

        lvCategory      = (ListView) getView().findViewById(R.id.lvCategory);
        arCategoriesView.clear();
        List<Category> arParentCategories = db.getAllParentCategories(true, false);
        for(Category category : arParentCategories) {
            arCategoriesView.add(new CategoryView(category, true, true));
            List<Category> arCategories = db.getCategoriesByParent(category.getId());
            for(Category cate : arCategories) {
                arCategoriesView.add(new CategoryView(cate, true, true));
            }
        }
        categoryAdapter     = new CategoryAdapter(getActivity(), arCategoriesView);
        lvCategory.setAdapter(categoryAdapter);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LogUtils.logEnterFunction(TAG, "isChecked = " + isChecked);
        for(int i = 0 ; i < arCategoriesView.size(); i++) {
            arCategoriesView.get(i).isChecked = isChecked;
        }

        categoryAdapter.notifyDataSetChanged();
        LogUtils.logLeaveFunction(TAG, "isChecked = " + isChecked, null);
    }

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
    }

    /**
     * CategoryAdapter: Adapter of category's listview
     */
    private class CategoryAdapter extends ArrayAdapter<CategoryView> {

        private class ViewHolder {
            LinearLayout    llCategory;
            ImageView       ivExpand;
            TextView        tvName;
            CheckBox        cbSelected;
        }

        private List<CategoryView> arCategoriesView;

        public CategoryAdapter(Context context, List<CategoryView> items) {
            super(context, R.layout.listview_item_budget_category,items);
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

                convertView = inflater.inflate(R.layout.listview_item_budget_category, parent, false);
                viewHolder.llCategory   = (LinearLayout) convertView.findViewById(R.id.llCategory);
                viewHolder.ivExpand     = (ImageView) convertView.findViewById(R.id.ivExpand);
                viewHolder.tvName       = (TextView) convertView.findViewById(R.id.tvParentCategory);
                viewHolder.cbSelected   = (CheckBox) convertView.findViewById(R.id.cbSelected);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
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

            viewHolder.cbSelected.setChecked(arCategoriesView.get(position).isChecked);

            viewHolder.tvName.setText(arCategoriesView.get(position).category.getName());

            return convertView;
        }

    }
}
