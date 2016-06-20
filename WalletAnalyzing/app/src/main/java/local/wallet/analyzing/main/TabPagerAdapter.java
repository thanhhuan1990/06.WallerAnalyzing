package local.wallet.analyzing.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import local.wallet.analyzing.account.FragmentListAccount;
import local.wallet.analyzing.budget.FragmentListBudget;
import local.wallet.analyzing.transactions.FragmentListTransaction;
import local.wallet.analyzing.report.FragmentReport;
import local.wallet.analyzing.transaction.FragmentTransactionCUD;
import local.wallet.analyzing.R;
import local.wallet.analyzing.model.Transaction;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    List<List<Fragment>>    registeredFragment  = new ArrayList<>();

    private int mNumOfTabs;

    public TabPagerAdapter(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentListTransaction tabTransaction = new FragmentListTransaction();
                return tabTransaction;
            case 1:
                FragmentTransactionCUD tabTransactionCreate = new FragmentTransactionCUD();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Transaction", new Transaction());
                bundle.putInt("ContainerViewId", R.id.ll_transaction_create);
                tabTransactionCreate.setArguments(bundle);
                return tabTransactionCreate;
            case 2:
                return FragmentListAccount.getInstance();
            case 3:
                FragmentListBudget tabBudget = new FragmentListBudget();
                return tabBudget;
            case 4:
                FragmentReport tabReport = new FragmentReport();
                return tabReport;
            case 5:
                FragmentUtilities tabUtilities = new FragmentUtilities();
                return tabUtilities;
            default:
                return null;
        }
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragment.add(position, new ArrayList<Fragment>());
        registeredFragment.get(position).add(fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragment.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public int getBackStackCount(int tab) {
        return registeredFragment.get(tab).size();
    }

    public void popBackStack(int tab) {
        registeredFragment.get(tab).get(registeredFragment.get(tab).size() - 1).getFragmentManager().popBackStackImmediate();
    }

    public void resumeTopFragment(int tab) {
        registeredFragment.get(tab).get(registeredFragment.get(tab).size() - 1).onResume();
    }

    public void removeTopFragment(int tab) {
        registeredFragment.get(tab).remove(registeredFragment.get(tab).size() - 1);
    }

    public void addFragment(int tab, Fragment fragment) {
        registeredFragment.get(tab).add(fragment);
    }

    public void replaceFragment(int tab, Fragment fragment) {
        registeredFragment.get(tab).remove(registeredFragment.get(tab).size() - 1);
        registeredFragment.get(tab).add(fragment);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragment.get(position).get(0);
    }
}
