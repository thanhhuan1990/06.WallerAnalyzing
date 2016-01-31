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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
public class FragmentCategoryParentSelect extends Fragment {

    private static final String TAG = "FragmentCategoryParentSelect";

    private TransactionEnum mTransactionType     = TransactionEnum.Expense;

    private DatabaseHelper db;
    private List<Category> arParentCategories = new ArrayList<Category>();

    private String mTagOfSource = "";
    private int mCurrentParentCategoryId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        mTransactionType                = (TransactionEnum)bundle.get("TransactionType");
        mTagOfSource                    = bundle.getString("Tag");
        mCurrentParentCategoryId        = bundle.getInt("ParentCategoryId", 0);

        LogUtils.trace(TAG, "mTransactionType = " + mTransactionType);
        LogUtils.trace(TAG, "mTagOfSource = " + mTagOfSource);
        LogUtils.trace(TAG, "mCurrentParentCategoryId = " + mCurrentParentCategoryId);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, null);
        return inflater.inflate(R.layout.layout_fragment_category_parent, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onActivityCreated(savedInstanceState);

        db = new DatabaseHelper(getActivity());

        arParentCategories.add(new Category(0, 0, getResources().getString(R.string.new_category_select_as_parent), true, false, null));
        arParentCategories.addAll(db.getAllParentCategories((mTransactionType == TransactionEnum.Expense
                                                            || mTransactionType == TransactionEnum.Transfer
                                                            || mTransactionType == TransactionEnum.Adjustment)
                                                                        ? true : false
                                                                , false));

        ListView lvParentCategory   = (ListView) getView().findViewById(R.id.lvParentCategory);
        ParentCategoryAdapter accountTypeAdapter = new ParentCategoryAdapter(getActivity(), arParentCategories);
        lvParentCategory.setAdapter(accountTypeAdapter);

        lvParentCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mTagOfSource.equals(((ActivityMain)getActivity()).getFragmentCategoryAdd())) {

                    LogUtils.trace(TAG, "Setup for FragmentCategoryCreate");
                    // Return ParentCategory's Id to FragmentCategoryCreate
                    String tagOfFragment = ((ActivityMain)getActivity()).getFragmentCategoryAdd();
                    FragmentCategoryCreate fragment = (FragmentCategoryCreate)getActivity()
                                                        .getSupportFragmentManager()
                                                            .findFragmentByTag(tagOfFragment);
                    fragment.updateParentCategory(arParentCategories.get(position).getId(), false);

                }

                // Back to last fragment
                getFragmentManager().popBackStackImmediate();
            }
        });

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);

        super.onCreateOptionsMenu(menu, inflater);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        if(mTransactionType == TransactionEnum.Expense) {
            tvTitle.setText(getResources().getString(R.string.title_category_expense_select_parent));
        } else if(mTransactionType == TransactionEnum.Income) {
            tvTitle.setText(getResources().getString(R.string.title_category_income));
        }

        // Update ActionBar
        ((ActivityMain)getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(TAG, null, null);
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
            super(context, R.layout.listview_item_account_type, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.listview_item_category_parent, parent, false);
                viewHolder.tvCategoryName = (TextView) convertView.findViewById(R.id.tvCategoryName);
                viewHolder.ivUsing  = (ImageView) convertView.findViewById(R.id.ivUsing);
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
