package local.wallet.analyzing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Transaction.TransactionEnum;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentDescription extends Fragment {

    public static final String Tag = "FragmentDescription";

    public interface IUpdateDescription extends Serializable {
        void onDescriptionUpdated(String description);
    }

    private String              oldDescription;

    private EditText            etDescription;

    private IUpdateDescription  mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle       = this.getArguments();
        oldDescription      = bundle.getString("Description", "");
        mCallback           = (IUpdateDescription) bundle.get("Callback");

        LogUtils.trace(Tag, "oldDescription = " + oldDescription);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_description, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onActivityCreated(savedInstanceState);

        etDescription = (EditText) getView().findViewById(R.id.etDescription);
        etDescription.setText(oldDescription);
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreateOptionsMenu(menu, inflater);

        /* Todo: Update ActionBar: Spinner TransactionType */
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_done, null);
        TextView tvTitle    = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(getResources().getString(R.string.title_description));
        ImageView ivDone    = (ImageView) mCustomView.findViewById(R.id.ivDone);
        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.trace(Tag, "Click Menu Action Done.");
                ((ActivityMain) getActivity()).hideKeyboard(getActivity());

                mCallback.onDescriptionUpdated(etDescription.getText().toString());
                // Back
                getFragmentManager().popBackStackImmediate();
                }
            }

            );

            ((ActivityMain) getActivity()).updateActionBar(mCustomView);

            LogUtils.logLeaveFunction(Tag, null, null);
        }

    }
