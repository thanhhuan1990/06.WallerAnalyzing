package local.wallet.analyzing;

import android.content.Context;
import android.view.View;
import android.widget.TabHost;

/**
 * Created by huynh.thanh.huan on 2/18/2016.
 */
public class DummyTabContent implements TabHost.TabContentFactory {
    private Context mContext;

    public DummyTabContent(Context context){
        mContext = context;
    }

    @Override
    public View createTabContent(String tag) {
        View v = new View(mContext);
        return v;
    }
}
