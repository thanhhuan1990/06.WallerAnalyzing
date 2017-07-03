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

import local.wallet.analyzing.utils.LogUtils;

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
        LogUtils.logEnterFunction(TAG);
        super.onAttach(context);
        LogUtils.logLeaveFunction(TAG);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG);
        super.onCreate(savedInstanceState);
        LogUtils.logLeaveFunction(TAG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG);
        LogUtils.logLeaveFunction(TAG);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG);
        super.onActivityCreated(savedInstanceState);
        LogUtils.logEnterFunction(TAG);
    }

    @Override
    public void onStart() {
        LogUtils.logEnterFunction(TAG);
        super.onStart();
        LogUtils.logEnterFunction(TAG);
    }

    @Override
    public void onResume() {
        LogUtils.logEnterFunction(TAG);
        super.onResume();
        LogUtils.logEnterFunction(TAG);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG);
        super.onCreateOptionsMenu(menu, inflater);
        LogUtils.logEnterFunction(TAG);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LogUtils.logEnterFunction(TAG);
        super.onConfigurationChanged(newConfig);
        LogUtils.logEnterFunction(TAG);
    }

    @Override
    public void onDestroyView() {
        LogUtils.logEnterFunction(TAG);
        super.onDestroyView();
        LogUtils.logEnterFunction(TAG);
    }

    @Override
    public void onPause() {
        LogUtils.logEnterFunction(TAG);
        super.onPause();
        LogUtils.logEnterFunction(TAG);
    }

    @Override
    public void onStop() {
        LogUtils.logEnterFunction(TAG);
        super.onStop();
        LogUtils.logEnterFunction(TAG);
    }

    @Override
    public void onDestroy() {
        LogUtils.logEnterFunction(TAG);
        super.onDestroy();
        LogUtils.logEnterFunction(TAG);
    }

    @Override
    public void onDetach() {
        LogUtils.logEnterFunction(TAG);
        super.onDetach();
        LogUtils.logEnterFunction(TAG);
    }
}
