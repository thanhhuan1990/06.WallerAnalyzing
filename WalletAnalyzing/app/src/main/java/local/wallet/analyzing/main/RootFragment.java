package local.wallet.analyzing.main;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import local.wallet.analyzing.Utils.LogUtils;

/**
 * Created by huynh.thanh.huan on 6/20/2016.
 */
public class RootFragment extends Fragment {
    public String       TAG     = "";
    public int          mTab    = -1;

    public void init(int tab, String tag) {
        this.mTab   = tab;
        this.TAG    = "---[" + mTab + "]---" + tag;
    }

    @Override
    public void onAttach(Context context) {
        LogUtils.logEnterFunction(TAG, null);
        super.onAttach(context);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        super.onCreate(savedInstanceState);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, null);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        super.onActivityCreated(savedInstanceState);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onStart() {
        LogUtils.logEnterFunction(TAG, null);
        super.onStart();
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onResume() {
        LogUtils.logEnterFunction(TAG, null);
        super.onResume();
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);
        super.onCreateOptionsMenu(menu, inflater);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LogUtils.logEnterFunction(TAG, null);
        super.onConfigurationChanged(newConfig);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onDestroyView() {
        LogUtils.logEnterFunction(TAG, null);
        super.onDestroyView();
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onPause() {
        LogUtils.logEnterFunction(TAG, null);
        super.onPause();
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onStop() {
        LogUtils.logEnterFunction(TAG, null);
        super.onStop();
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onDestroy() {
        LogUtils.logEnterFunction(TAG, null);
        super.onDestroy();
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onDetach() {
        LogUtils.logEnterFunction(TAG, null);
        super.onDetach();
        LogUtils.logLeaveFunction(TAG, null, null);
    }
}
