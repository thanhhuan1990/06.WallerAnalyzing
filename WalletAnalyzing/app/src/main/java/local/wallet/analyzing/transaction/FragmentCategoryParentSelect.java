package local.wallet.analyzing.transaction;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Category.EnumDebt;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentCategoryParentSelect extends Fragment {

    public static final String Tag = "FragmentCategoryParentSelect";

    public interface ISelectParentCategory extends Serializable {
        void onParentCategorySelected(int categoryId);
    }


    private DatabaseHelper  mDbHelper;
    private List<Category>  arParentCategories = new ArrayList<Category>();

    private boolean                     mIsExpense = true;
    private ISelectParentCategory       mCallback;
    private int                         mCurrentParentCategoryId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();

        mIsExpense                      = bundle.getBoolean("CategoryType");
        mCallback                       = (ISelectParentCategory) bundle.getSerializable("Callback");
        mCurrentParentCategoryId        = bundle.getInt("ParentCategoryId", 0);

        LogUtils.trace(Tag, "mCurrentParentCategoryId = " + mCurrentParentCategoryId);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_category_parent, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onActivityCreated(savedInstanceState);

        mDbHelper = new DatabaseHelper(getActivity());

        arParentCategories.add(new Category(0, 0, getResources().getString(R.string.new_category_select_as_parent), true, EnumDebt.NONE));
        arParentCategories.addAll(mDbHelper.getAllParentCategories(mIsExpense, EnumDebt.NONE));

        ListView lvParentCategory   = (ListView) getView().findViewById(R.id.lvParentCategory);
        ParentCategoryAdapter accountTypeAdapter = new ParentCategoryAdapter(getActivity(), arParentCategories);
        lvParentCategory.setAdapter(accountTypeAdapter);

        lvParentCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallback.onParentCategorySelected(arParentCategories.get(position).getId());

                // Back to last fragment
                getFragmentManager().popBackStackImmediate();
            }
        });

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreateOptionsMenu(menu, inflater);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        if(mIsExpense) {
            tvTitle.setText(getResources().getString(R.string.title_category_expense_select_parent));
        } else {
            tvTitle.setText(getResources().getString(R.string.title_category_income));
        }

        // Update ActionBar
        ((ActivityMain)getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * ParentCategoryAdapter
     */
    private class ParentCategoryAdapter extends ArrayAdapter<Category> {

        private class ViewHolder {
            TextView tvCategoryName;
            ImageView ivUsing;
        }

        public ParentCategoryAdapter(Context context, List<Category> items) {
            super(context, R.layout.listview_item_category_parent, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.listview_item_category_parent, parent, false);
                viewHolder.tvCategoryName   = (TextView) convertView.findViewById(R.id.tvCategoryName);
                viewHolder.ivUsing          = (ImageView) convertView.findViewById(R.id.ivUsing);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Category category = getItem(position);

            if (category != null) {

                viewHolder.tvCategoryName.setText(category.getName());
                if(category.getId() == 0) {
                    viewHolder.tvCategoryName.setTextColor(getResources().getColor(R.color.textcolor_none));
                }

                if(mCurrentParentCategoryId == category.getId()) {
                    viewHolder.ivUsing.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.ivUsing.setVisibility(View.INVISIBLE);
                }
            }

            return convertView;
        }
    }
}
