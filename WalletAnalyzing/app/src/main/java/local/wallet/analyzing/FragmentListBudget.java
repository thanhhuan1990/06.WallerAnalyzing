package local.wallet.analyzing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class FragmentListBudget extends Fragment {

    private static final String TAG = "ListBudget";

    private DatabaseHelper db;
    private ListView lvBudget;
    private TextView tvEmpty;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, null);
        return inflater.inflate(R.layout.layout_fragment_list_budget, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        super.onActivityCreated(savedInstanceState);

        db = new DatabaseHelper(getActivity());

        lvBudget = (ListView) getView().findViewById(R.id.lvBudget);
        tvEmpty = (TextView) getView().findViewById(R.id.tvEmpty);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);
        super.onCreateOptionsMenu(menu, inflater);

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_add, null);
        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_budget));

        ImageView ivAdd = (ImageView) mCustomView.findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.trace(TAG, "Click Menu Action Add Budget.");
                FragmentBudgetCreate nextFrag = new FragmentBudgetCreate();
                FragmentListBudget.this.getFragmentManager().beginTransaction()
                                                            .add(R.id.layout_budget, nextFrag, "FragmentBudgetCreate")
                                                            .addToBackStack(null)
                                                            .commit();
            }
        });

        ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(TAG, null, null);
    }
}
