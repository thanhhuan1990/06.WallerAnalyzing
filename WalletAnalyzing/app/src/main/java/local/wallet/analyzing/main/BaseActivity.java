package local.wallet.analyzing.main;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.LogUtils;

/**
 * Created by huynh.thanh.huan on 6/8/2016.
 */
public class BaseActivity extends AppCompatActivity {

    protected String TAG = getClass().getSimpleName();

    /**
     * The main handler
     */
    private Handler mHandler;

    private ProgressDialog      mProgressDialog;

    private List<Dialog> mListDialogs = new ArrayList<>();

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        LogUtils.trace(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.trace(TAG, "onCreate");

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void finalize() throws Throwable {
        LogUtils.info(TAG, "Release memory!");
        super.finalize();
    }

    @Override
    protected void onResume() {
        LogUtils.trace(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtils.trace(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStart() {
        LogUtils.trace(TAG, "onStart");

        super.onStart();
    }

    @Override
    protected void onStop() {
        LogUtils.trace(TAG, "onStop");

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtils.trace(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LogUtils.trace(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        LogUtils.trace(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        LogUtils.trace(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        LogUtils.trace(TAG, "onBackPressed");
        super.onBackPressed();
    }

    @Override
    public void onAttachedToWindow() {

        LogUtils.trace(TAG, "onAttachedToWindow");

        super.onAttachedToWindow();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        LogUtils.trace(TAG, "onPostCreate - savedInstanceState = " + savedInstanceState);

        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPostResume() {

        LogUtils.trace(TAG, "onPostResume");

        super.onPostResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {

        LogUtils.trace(TAG, "onNewIntent - intent = " + intent);

        super.onNewIntent(intent);
    }

    /**
     * Show customize Toast
     * @param error
     */
    public void showError(String error) {
        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.layout_toast_error, (ViewGroup) findViewById(R.id.llCustomToast));

        // set a message
        TextView text = (TextView) layout.findViewById(R.id.tvError);
        text.setText(error);

        // Toast...
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    /**
     * Show customize Toast
     * @param message
     */
    public void showToastSuccessful(String message) {
        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.layout_toast_successful, (ViewGroup) findViewById(R.id.llCustomToast));

        // set a message
        TextView text = (TextView) layout.findViewById(R.id.tvMessage);
        text.setText(message);

        // Toast...
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Add fragment
     *
     * @param containViewId
     * @param fragment
     * @param tag
     * @param addToBackStack
     */
    public void addFragment(int tab, int containViewId, Fragment fragment, String tag, boolean addToBackStack) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(containViewId, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();

    }

    /**
     * Replace fragment
     *
     * @param containViewId
     * @param fragment
     * @param tag
     * @param addToBackStack
     */
    public void replaceFragment(int tab, int containViewId, Fragment fragment, String tag, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(containViewId, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();

    }

    public void showProgressDialog() {
        mProgressDialog = ProgressDialog.show(BaseActivity.this, null, null);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mProgressDialog.setContentView(LayoutInflater.from(BaseActivity.this).inflate(R.layout.loader, null));
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    public void dismissProgressDialog() {
        dismissDialog(mProgressDialog);
    }

    /**
     * Hide all showing dialogs. If you show a dialog, should use functions of
     * this class or subclass because they will be managed.
     */
    public void hideAllDialogs() {
        for (Dialog dialog : mListDialogs) {
            if (dialog != null) {
                dialog.setOnDismissListener(null);
                dismissDialog(dialog);
            }
        }

        mListDialogs.clear();
    }

    /**
     * Show dialog in security mode
     *
     * @param dialog
     */
    public void showDialog(Dialog dialog) {

        try {
            dialog.show();
            mListDialogs.add(dialog);
        } catch (Throwable e) {
            // java.lang.RuntimeException: Adding window failed
            LogUtils.error(TAG, e);

            // Just skip
            return;
        }
    }

    /**
     * Dismiss dialog in security mode
     *
     * @param dialog
     */
    public void dismissDialog(DialogInterface dialog) {

        try {
            dialog.dismiss();
            mListDialogs.remove(dialog);
        } catch (Throwable e) {
            // java.lang.RuntimeException: Adding window failed
            LogUtils.error(TAG, e);

            // Just skip
            return;
        }
    }
}
