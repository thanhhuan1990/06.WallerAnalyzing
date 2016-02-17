package local.wallet.analyzing;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Created by huynh.thanh.huan on 12/30/2015.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

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
                FragmentTransactionCreate tabNewTransaction = new FragmentTransactionCreate();
                return tabNewTransaction;
            case 2:
//                return FragmentListAccount.newInstance();
//                FragmentListAccount fragmentListAccount = new FragmentListAccount();
//                return fragmentListAccount;
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
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
