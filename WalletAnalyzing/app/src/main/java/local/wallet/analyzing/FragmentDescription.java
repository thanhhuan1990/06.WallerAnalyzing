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

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Transaction.TransactionEnum;

/**
 * Created by huynh.thanh.huan on 1/6/2016.
 */
public class FragmentDescription extends Fragment {

    public static final String Tag = "FragmentDescription";

    private String              mTagOfSource = "";
    private TransactionEnum     mTransactionType;
    private String              oldDescription;

    private EditText            etDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle       = this.getArguments();
        mTagOfSource        = bundle.getString("Tag");
        oldDescription      = bundle.getString("Description", "");
        mTransactionType    = (TransactionEnum) bundle.get("TransactionType");

        LogUtils.trace(Tag, "mTagOfSource = " + mTagOfSource);
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

                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(mTagOfSource);

                if (mTagOfSource.equals(FragmentTransactionCreateExpense.Tag)) {
                    ((FragmentTransactionCreateExpense) fragment).updateDescription(mTransactionType, etDescription.getText().toString());
                } else if (mTagOfSource.equals(FragmentTransactionCreateExpenseLend.Tag)) {
                    ((FragmentTransactionCreateExpenseLend) fragment).updateDescription(mTransactionType, etDescription.getText().toString());
                } else if (mTagOfSource.equals(FragmentTransactionCreateExpenseRepayment.Tag)) {
                    ((FragmentTransactionCreateExpenseRepayment) fragment).updateDescription(mTransactionType, etDescription.getText().toString());
                } else if(mTagOfSource.equals(((ActivityMain)getActivity()).getFragmentTransactionUpdate())) {

                    // Set input string for Account's description in TransactionUpdate, and then return.
//                    String tagOfFragment = ((ActivityMain) getActivity()).getFragmentTransactionUpdate();
//                    FragmentTransactionUpdate fragment = (FragmentTransactionUpdate) getActivity().getSupportFragmentManager().findFragmentByTag(tagOfFragment);
//                    fragment.updateDescription(mTransactionType, etDescription.getText().toString());

                } else if(mTagOfSource.equals(FragmentAccountCreate.Tag)) {

                    ((FragmentAccountCreate) fragment).updateDescription(etDescription.getText().toString());

                } else if(mTagOfSource.equals(FragmentAccountUpdate.Tag)) {

                    ((FragmentAccountUpdate) fragment).updateDescription(etDescription.getText().toString());

                } else if(mTagOfSource.equals(FragmentCategoryCreate.Tag)) {

                    ((FragmentCategoryCreate) fragment).updateDescription(etDescription.getText().toString());

                }

                /*if(mTagOfSource.equals(((ActivityMain)getActivity()).getFragmentTransactionCreateExpense())) {

                    LogUtils.trace(Tag, "Setup for TransactionCreate");
                    // Set input string for Account's description in TransactionCreate, and then return.
                    String tagOfFragment = ((ActivityMain) getActivity()).getFragmentTransactionCreateExpense();
                    FragmentTransactionCreateExpense fragment = (FragmentTransactionCreateExpense) getActivity().getSupportFragmentManager().findFragmentByTag(tagOfFragment);
                    fragment.updateDescription(mTransactionType, etDescription.getText().toString());

                } else if(mTagOfSource.equals(((ActivityMain)getActivity()).getFragmentTransactionUpdate())) {

                    LogUtils.trace(Tag, "Setup for TransactionUpdate");
                    // Set input string for Account's description in TransactionUpdate, and then return.
                    String tagOfFragment = ((ActivityMain) getActivity()).getFragmentTransactionUpdate();
                    FragmentTransactionUpdate fragment = (FragmentTransactionUpdate) getActivity().getSupportFragmentManager().findFragmentByTag(tagOfFragment);
                    fragment.updateDescription(mTransactionType, etDescription.getText().toString());

                } else if(mTagOfSource.equals(((ActivityMain)getActivity()).getFragmentAccountCreate())) {

                    LogUtils.trace(Tag, "Setup for AccountCreate");
                    // Set input string for Account's description in AccountCreate, and then return.
                    String tagOfFragment = ((ActivityMain)getActivity()).getFragmentAccountCreate();
                    FragmentAccountCreate fragment = (FragmentAccountCreate) getActivity().getSupportFragmentManager().findFragmentByTag(tagOfFragment);
                    fragment.updateDescription(etDescription.getText().toString());

                } else if(mTagOfSource.equals(((ActivityMain)getActivity()).getFragmentAccountUpdate())) {

                    LogUtils.trace(Tag, "Setup for AccountUpdate");
                    // Set input string for Account's description in AccountUpdate, and then return.
                    String tagOfFragment = ((ActivityMain)getActivity()).getFragmentAccountUpdate();
                    FragmentAccountUpdate fragment = (FragmentAccountUpdate)getActivity().getSupportFragmentManager().findFragmentByTag(tagOfFragment);
                    fragment.updateDescription(etDescription.getText().toString());

                } else if(mTagOfSource.equals(((ActivityMain) getActivity()).getFragmentCategoryCreate())) {

                    LogUtils.trace(Tag, "Setup for CategoryCreate");
                    // Set input string for Account's description in CategoryCreate, and then return.
                    String tagOfFragment = ((ActivityMain)getActivity()).getFragmentCategoryCreate();
                    FragmentCategoryCreate fragment = (FragmentCategoryCreate)getActivity().getSupportFragmentManager().findFragmentByTag(tagOfFragment);
                    fragment.updateDescription(etDescription.getText().toString());

                }*/

                    // Back
                    getFragmentManager().popBackStackImmediate();
                }
            }

            );

            ((ActivityMain)

            getActivity()

            ).

            updateActionBar(mCustomView);

            LogUtils.logLeaveFunction(Tag, null, null);
        }

    }
