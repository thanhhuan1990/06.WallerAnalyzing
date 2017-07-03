package local.wallet.analyzing.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.utils.LogUtils;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.Category;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentUtilities extends Fragment {

    public static final int     mTab = 5;
    public static final String  Tag = "---[" + mTab + "]---Utilities";

    private ImageButton         btnFileBrowser;

    private TextView            txtCheckDB;

    private Button              btnAccount;
    private Button              btnCategory;

    private ListView            lvAccount;
    private AccountAdapter      accAdapter;
    private List<Account>       listAccount = new ArrayList<Account>();
    private ListView            lvCategory;
    private CategoryAdapter     categoryAdapter;
    private List<Category>      listCategory = new ArrayList<Category>();

    DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_utilities, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_only_title, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_utility));
        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        super.onCreateOptionsMenu(menu, inflater);

        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LogUtils.logEnterFunction(Tag);

        db = new DatabaseHelper(getActivity());

        btnFileBrowser = (ImageButton) getView().findViewById(R.id.btnFileBrowser);
        btnFileBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                DialogFragmentFileBrowser dl = new DialogFragmentFileBrowser();
                dl.setTargetFragment(FragmentUtilities.this, 0);
                dl.show(fm, "File Browser");
            }
        });

        txtCheckDB = (TextView) getView().findViewById(R.id.txtCheckDB);
        txtCheckDB.setText("Account: " + db.getAllAccounts().size());

        btnAccount  = (Button) getView().findViewById(R.id.btnAccount);
        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvAccount.setVisibility(View.VISIBLE);
                lvCategory.setVisibility(View.GONE);
            }
        });
        btnCategory = (Button) getView().findViewById(R.id.btnCategory);
        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvCategory.setVisibility(View.VISIBLE);
                lvAccount.setVisibility(View.GONE);
            }
        });

        lvAccount   = (ListView) getView().findViewById(R.id.lvAccount);
        listAccount = db.getAllAccounts();
        accAdapter = new AccountAdapter(getActivity(), R.layout.listview_item_account, listAccount);
        lvAccount.setAdapter(accAdapter);

        lvCategory  = (ListView) getView().findViewById(R.id.lvCategory);
//        listCategory    = db.getAllParentCategories();
//        categoryAdapter = new CategoryAdapter(getActivity(), R.layout.listview_item_category, listCategory);
//        lvCategory.setAdapter(categoryAdapter);

        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        txtCheckDB.setText("Account: " + db.getAllAccounts().size() + "\n" + "Category: " + db.getAllCategories().size());

        ArrayAdapter accAdapter = new AccountAdapter(getActivity(), R.layout.listview_item_account, db.getAllAccounts());
        lvAccount.setAdapter(accAdapter);

        listAccount = db.getAllAccounts();
        listCategory = db.getAllCategories(true);

        accAdapter.notifyDataSetChanged();
    }

    private class AccountAdapter extends ArrayAdapter<Account> {
        public AccountAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public AccountAdapter(Context context, int resource, List<Account> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;

            if (view == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                view = vi.inflate(R.layout.listview_item_account, null);
            }

            Account account = getItem(position);

            if (account != null) {
                TextView tvAccountName = (TextView) view.findViewById(R.id.tvAccount);
                TextView tvRemain = (TextView) view.findViewById(R.id.tvRemain);

                if (tvAccountName != null) {
                    tvAccountName.setText(account.getName());
                }

                if (tvRemain != null) {
                    tvRemain.setText(account.getName());
                }
            }

            return view;
        }
    }

    private class CategoryAdapter extends ArrayAdapter<Category> {
        private List<Category> arCategories;

        public CategoryAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public CategoryAdapter(Context context, int resource, List<Category> items) {
            super(context, resource, items);
            arCategories = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;

            if (view == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                view = vi.inflate(R.layout.listview_item_category, null);
            }

            Category category = getItem(position);

            if (category != null) {
                final ImageView   ivExpand    = (ImageView) view.findViewById(R.id.ivExpand);
                ivExpand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.expand);
                        ivExpand.startAnimation(rotate);
                    }
                });

                TextView tvParentCategoryName = (TextView) view.findViewById(R.id.tvParentCategory);

                if (tvParentCategoryName != null) {
                    tvParentCategoryName.setText(category.getName());
                }

            }

            return view;
        }
    }
}
