package local.wallet.analyzing;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/22/2016.
 */
public class FragmentReportEventDetail extends Fragment implements View.OnClickListener {
    private static final String TAG = "ReportEventDetail";

    private DatabaseHelper  mDbHelper;

    private ImageView       ivExpandExpense;
    private TextView        tvTotalExpense;
    private LinearLayout    llExpenses;
    private ImageView       ivExpandIncome;
    private TextView        tvTotalIncome;
    private LinearLayout    llIncomes;
    private LinearLayout    llComplete;

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
        return inflater.inflate(R.layout.layout_fragment_report_event_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        super.onActivityCreated(savedInstanceState);

        mDbHelper       = new DatabaseHelper(getActivity());

        ivExpandExpense = (ImageView) getView().findViewById(R.id.ivExpandExpense);
        ivExpandExpense.setOnClickListener(this);
        tvTotalExpense  = (TextView) getView().findViewById(R.id.tvTotalExpense);
        llExpenses      = (LinearLayout) getView().findViewById(R.id.llExpenses);
        ivExpandIncome  = (ImageView) getView().findViewById(R.id.ivExpandIncome);
        ivExpandIncome.setOnClickListener(this);
        tvTotalIncome   = (TextView) getView().findViewById(R.id.tvTotalIncome);
        llIncomes       = (LinearLayout) getView().findViewById(R.id.llIncomes);
        llComplete      = (LinearLayout) getView().findViewById(R.id.llComplete);
        llComplete.setOnClickListener(this);

        /* Todo: Update view by data from mDbHelper */

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(((ActivityMain) getActivity()).getCurrentVisibleItem() != ActivityMain.TAB_POSITION_REPORTS) {
            return;
        }

        LogUtils.logEnterFunction(TAG, null);
        super.onCreateOptionsMenu(menu, inflater);

        LayoutInflater mInflater    = LayoutInflater.from(getActivity());
        View mCustomView            = mInflater.inflate(R.layout.action_bar_with_button_update, null);
        ImageView ivUpdate          = (ImageView) mCustomView.findViewById(R.id.ivUpdate);
        ivUpdate.setOnClickListener(this);

         ((ActivityMain) getActivity()).updateActionBar(mCustomView);

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivUpdate:
                break;
            case R.id.ivExpandExpense:
                final Animation expand = AnimationUtils.loadAnimation(getActivity(), R.anim.expand);
                final Animation shrink = AnimationUtils.loadAnimation(getActivity(), R.anim.shrink);
                break;
            case R.id.ivExpandIncome:
                final Animation expandIncome = AnimationUtils.loadAnimation(getActivity(), R.anim.expand);
                final Animation shrinkIncome = AnimationUtils.loadAnimation(getActivity(), R.anim.shrink);
                break;
            case R.id.llComplete:
                break;
            default:
                break;
        }
    }
}
