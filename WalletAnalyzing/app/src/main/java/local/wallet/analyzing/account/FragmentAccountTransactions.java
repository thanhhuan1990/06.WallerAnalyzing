package local.wallet.analyzing.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import local.wallet.analyzing.transaction.FragmentTransactionCUD;
import local.wallet.analyzing.R;
import local.wallet.analyzing.utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.model.Transaction.TransactionEnum;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 2/3/2016.
 */
public class FragmentAccountTransactions extends Fragment {
    public static int                   mTab = 2;
    public static final String          Tag = "---[" + mTab + "]---AccountTransactions";

    private ActivityMain                mActivity;

    private int                 mAccountId;
    private int                 mContainerViewId;

    private DatabaseHelper      mDbHelper;
    private List<Transaction>   arTransactions = new ArrayList<Transaction>();

    private TextView            tvInitBalance;
    private TextView            tvBalance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);

        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        /* Get data from Bundle */
        Bundle bundle           = this.getArguments();
        if(bundle != null) {
            mTab                = bundle.getInt("Tab", mTab);
            mAccountId          = bundle.getInt("AccountID", 0);
            mContainerViewId    = bundle.getInt("ContainerViewId");

            LogUtils.trace(Tag, "mAccountId     = " + mAccountId);

            /* Initialize Database, insert default category */
            mDbHelper           = new DatabaseHelper(getActivity());
        } else {
            ((ActivityMain) getActivity()).showError("Bundle is NULL!");
        }

        LogUtils.logLeaveFunction(Tag);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);

        View view           = inflater.inflate(R.layout.layout_fragment_account_transactions, container, false);

        tvInitBalance       = (TextView) view.findViewById(R.id.tvAccountInitBalance);
        tvBalance           = (TextView) view.findViewById(R.id.tvAccountRemain);

        LogUtils.logLeaveFunction(Tag);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag);
        super.onActivityCreated(savedInstanceState);

        mActivity   = (ActivityMain) getActivity();

        LogUtils.logLeaveFunction(Tag);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag);
        super.onCreateOptionsMenu(menu, inflater);

        if(mTab != mActivity.getCurrentVisibleItem()) {
            LogUtils.error(Tag, "Wrong Tab. Return");
            LogUtils.logLeaveFunction(Tag);
            return;
        }

        // Update Action bar
        updateActionBar();
        // Update list Transactions
        updateListTransactions();

        LogUtils.logLeaveFunction(Tag);
    }

    private void updateActionBar() {
        /* Init ActionBar */
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.action_bar_with_button_add, null);

        TextView tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText(mDbHelper.getAccount(mAccountId).getName());

        ImageView ivAdd = (ImageView) mCustomView.findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransactionCUD nextFrag = new FragmentTransactionCUD();
                Bundle bundle = new Bundle();
//                bundle.putInt("Tab", mTab);
//                bundle.putInt("ContainerViewId", mContainerViewId);
                Transaction transaction = new Transaction();
                transaction.setFromAccountId(mAccountId);
                bundle.putSerializable("Transaction", transaction);
                nextFrag.setArguments(bundle);
                mActivity.addFragment(mTab, mContainerViewId, nextFrag, "FragmentTransactionCUD", true);
            }
        });

        ((ActivityMain)getActivity()).updateActionBar(mCustomView);

    }

    private void updateListTransactions() {
        LogUtils.logEnterFunction(Tag);

        Account account = mDbHelper.getAccount(mAccountId);
        tvInitBalance.setText(Currency.formatCurrency(getContext(), account.getCurrencyId(), mDbHelper.getAccount(mAccountId).getInitBalance()));
        tvBalance.setText(Currency.formatCurrency(getContext(), account.getCurrencyId(), mDbHelper.getAccountRemain(mAccountId)));

        arTransactions  = mDbHelper.getTransactionsByAccount(mAccountId);
        Collections.sort(arTransactions);

        LinearLayout    llTransactions = (LinearLayout) getView().findViewById(R.id.llTransactions);
        llTransactions.removeAllViews();

        LayoutInflater mInflater = LayoutInflater.from(getActivity());

        int position = 0;
        for(final Transaction tran : arTransactions) {
            LogUtils.trace(Tag, tran.toString());
            View mTransactionView = mInflater.inflate(R.layout.listview_item_account_transaction, null);
            TextView tvCategory     = (TextView) mTransactionView.findViewById(R.id.tvCategory);
            TextView tvDescription  = (TextView) mTransactionView.findViewById(R.id.tvDescription);
            TextView tvDate         = (TextView) mTransactionView.findViewById(R.id.tvDate);
            TextView tvAmount       = (TextView) mTransactionView.findViewById(R.id.tvAmount);
            TextView tvBalance      = (TextView) mTransactionView.findViewById(R.id.tvBalance);

            if(position % 2 == 0) {
                mTransactionView.setBackgroundColor(getResources().getColor(R.color.listview_account_transactions_event_background));
            } else {
                mTransactionView.setBackgroundColor(getResources().getColor(R.color.listview_account_transactions_odd_background));
            }

            tvDate.setText(String.format(getResources().getString(R.string.format_day_month_year),
                                            tran.getTime().get(Calendar.DAY_OF_MONTH),
                                            tran.getTime().get(Calendar.MONTH) + 1,
                                            tran.getTime().get(Calendar.YEAR)));

            Category cate = mDbHelper.getCategory(tran.getCategoryId());
            Account fromAcc, toAcc;
            switch (TransactionEnum.getTransactionEnum(tran.getTransactionType())) {
                case Expense: {
                    fromAcc = mDbHelper.getAccount(tran.getFromAccountId());

                    tvCategory.setText(String.format(getResources().getString(R.string.content_expense), cate != null ? cate.getName() : ""));
                    tvDescription.setText(tran.getDescription());
                    tvAmount.setText(Currency.formatCurrency(getContext(), fromAcc.getCurrencyId(), tran.getAmount()));
                    tvBalance.setText(String.format(getResources().getString(R.string.account_list_balance),
                            Currency.formatCurrency(getContext(),
                                    fromAcc.getCurrencyId(),
                                    mDbHelper.getAccountRemainAfter(fromAcc.getId(), tran.getTime()))));
                    break;
                } // End case Expense
                case Income: {
                    toAcc = mDbHelper.getAccount(tran.getToAccountId());

                    tvCategory.setText(String.format(getResources().getString(R.string.content_income), cate != null ? cate.getName() : ""));
                    tvDescription.setText(tran.getDescription());
                    tvAmount.setText(Currency.formatCurrency(getContext(), toAcc.getCurrencyId(), tran.getAmount()));
                    tvBalance.setText(String.format(getResources().getString(R.string.account_list_balance),
                            Currency.formatCurrency(getContext(),
                                    toAcc.getCurrencyId(),
                                    mDbHelper.getAccountRemainAfter(toAcc.getId(), tran.getTime()))));

                    tvCategory.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tvDescription.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tvAmount.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
                } // End case Income
                case Transfer: {
                    fromAcc = mDbHelper.getAccount(tran.getFromAccountId());
                    toAcc = mDbHelper.getAccount(tran.getToAccountId());

                    tvAmount.setText(Currency.formatCurrency(getContext(), fromAcc.getCurrencyId(), tran.getAmount()));

                    if (mAccountId == fromAcc.getId()) {
                        tvCategory.setText(String.format(getResources().getString(R.string.content_transfer_to), toAcc.getName()));
                        String description = tran.getDescription();

                        if (tran.getFee() != 0) {
                            if (!description.equals("")) {
                                description += "\n";
                            }
                            description += String.format(getResources().getString(R.string.content_transfer_fee),
                                    Currency.formatCurrency(getContext(),
                                            toAcc.getCurrencyId(),
                                            tran.getFee()));
                        }

                        tvDescription.setText(description);
                        tvBalance.setText(String.format(getResources().getString(R.string.account_list_balance),
                                Currency.formatCurrency(getContext(),
                                        fromAcc.getCurrencyId(),
                                        mDbHelper.getAccountRemainAfter(fromAcc.getId(), tran.getTime()))));

                    } else if (mAccountId == toAcc.getId()) {

                        tvCategory.setText(String.format(getResources().getString(R.string.content_transfer_from), fromAcc.getName()));
                        tvDescription.setText(tran.getDescription());
                        tvBalance.setText(String.format(getResources().getString(R.string.account_list_balance),
                                Currency.formatCurrency(getContext(),
                                        toAcc.getCurrencyId(),
                                        mDbHelper.getAccountRemainAfter(toAcc.getId(), tran.getTime()))));

                        tvCategory.setTextColor(getResources().getColor(R.color.colorPrimary));
                        tvDescription.setTextColor(getResources().getColor(R.color.colorPrimary));
                        tvAmount.setTextColor(getResources().getColor(R.color.colorPrimary));

                    }
                    break;
                } // End case Transfer
                case Adjustment: {
                    fromAcc = mDbHelper.getAccount(tran.getFromAccountId());
                    toAcc = mDbHelper.getAccount(tran.getToAccountId());

                    if (fromAcc != null) {
                        tvCategory.setText(String.format(getResources().getString(R.string.content_expense), cate != null ? cate.getName() : ""));
                        tvDescription.setText(tran.getDescription());
                        tvAmount.setText(Currency.formatCurrency(getContext(), fromAcc.getCurrencyId(), tran.getAmount()));
                        tvBalance.setText(String.format(getResources().getString(R.string.account_list_balance),
                                Currency.formatCurrency(getContext(),
                                        fromAcc.getCurrencyId(),
                                        mDbHelper.getAccountRemainAfter(fromAcc.getId(), tran.getTime()))));
                    } else if (toAcc != null) {
                        tvCategory.setText(String.format(getResources().getString(R.string.content_income), cate != null ? cate.getName() : ""));
                        tvDescription.setText(tran.getDescription());
                        tvAmount.setText(Currency.formatCurrency(getContext(), toAcc.getCurrencyId(), tran.getAmount()));
                        tvBalance.setText(String.format(getResources().getString(R.string.account_list_balance),
                                Currency.formatCurrency(getContext(),
                                        toAcc.getCurrencyId(),
                                        mDbHelper.getAccountRemainAfter(toAcc.getId(), tran.getTime()))));

                        tvCategory.setTextColor(getResources().getColor(R.color.colorPrimary));
                        tvDescription.setTextColor(getResources().getColor(R.color.colorPrimary));
                        tvAmount.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                    break;
                } // End Adjustment
                default:
                    break;
            } // End Switch

            if(tvDescription.getText().toString().equals("")) {
                tvDescription.setVisibility(View.GONE);
            }

            mTransactionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.warn(Tag, "AccountTransaction -> TransactionCUD");

                    FragmentTransactionCUD nextFrag = new FragmentTransactionCUD();
                    Bundle bundle = new Bundle();
                    bundle.putInt("Tab", mTab);
                    bundle.putSerializable("Transaction", tran);
                    nextFrag.setArguments(bundle);
                    mActivity.addFragment(mTab, mContainerViewId, nextFrag, "FragmentTransactionCUD", true);
                }
            });
            llTransactions.addView(mTransactionView);
            position++;
        } // End loop arTransactions

        LogUtils.logLeaveFunction(Tag);
    } // End updateTransactionList
}
