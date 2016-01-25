package local.wallet.analyzing;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.AccountType;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentDescription extends Fragment {

    private static final String TAG = "FragmentDescription";

    private String mTagOfSource = "";
    private String oldDescription;
    private EditText etDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle       = this.getArguments();
        mTagOfSource        = bundle.getString("Tag");
        oldDescription      = bundle.getString("Description", "");

        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);
        LogUtils.logLeaveFunction(TAG, null, null);
        return inflater.inflate(R.layout.layout_fragment_description, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(TAG, null);

        super.onActivityCreated(savedInstanceState);

        etDescription = (EditText) getView().findViewById(R.id.etDescription);
        etDescription.setText(oldDescription);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(TAG, null);
        super.onCreateOptionsMenu(menu, inflater);
//        menu.findItem(R.id.action_account_edit).setVisible(false);
//        getActivity().getMenuInflater().inflate(R.menu.menu_done, menu);
        LogUtils.logLeaveFunction(TAG, null, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LogUtils.logEnterFunction(TAG, null);
        int id = item.getItemId();

        if (id == R.id.action_done) {

            LogUtils.trace(TAG, "Click Menu Action Done.");

            if(mTagOfSource.equals(((ActivityMain)getActivity()).getFragmentAccountAdd())) {

                LogUtils.trace(TAG, "Setup for FragmentAccountAdd");

                // Set input string for Account's description in FragmentAccountAdd, and then return.
                String TagOfFragmentAccountAdd = ((ActivityMain)getActivity()).getFragmentAccountAdd();
                FragmentAccountAdd fragmentAccountAdd = (FragmentAccountAdd) getActivity().getSupportFragmentManager().findFragmentByTag(TagOfFragmentAccountAdd);
                fragmentAccountAdd.updateDescription(etDescription.getText().toString());

            } else if(mTagOfSource.equals(((ActivityMain)getActivity()).getFragmentAccountEdit())) {
                LogUtils.trace(TAG, "Setup for FragmentAccountEdit");
                // Return Type's Id to FragmentAccountAdd
                String TabOfFragmentAccountEdit = ((ActivityMain)getActivity()).getFragmentAccountEdit();
                FragmentAccountEdit fragmentAccountEdit = (FragmentAccountEdit)getActivity().getSupportFragmentManager().findFragmentByTag(TabOfFragmentAccountEdit);
                fragmentAccountEdit.updateDescription(etDescription.getText().toString());

            }

            getFragmentManager().popBackStackImmediate();
        }
        LogUtils.logLeaveFunction(TAG, null, null);
        return super.onOptionsItemSelected(item);
    }
}
