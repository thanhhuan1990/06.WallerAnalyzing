package local.wallet.analyzing.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by huynh.thanh.huan on 9/16/2015.
 */
public class Utilities {

    private static String Tag = "Utilities";

    private static final Random mRandomizer = new Random();

    /**
     * Parse String to Integer
     *
     * @param string
     * @return
     */
    public static int parseInt(String string) {
        //LogUtils.logEnterFunction(Tag, null);
        //LogUtils.trace(Tag, "String: " + string);
        int number = 0;
        try {
            number = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        //LogUtils.logLeaveFunction(Tag, null);
        return number;
    }

    /**
     * Generic function for setup Spinner
     *
     * @param spinner
     * @param values
     * @param selection
     * @param listener
     */
    public static void setupSpinner(Spinner spinner, ArrayList<String> values, int selection, AdapterView.OnItemSelectedListener listener) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(spinner.getContext(), android.R.layout.simple_spinner_item, values);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(listener);

        spinner.setSelection(selection);
    }

    /**
     * Convert Stream to ByteArray
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static byte[] convertStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[10240];
        int i = Integer.MAX_VALUE;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }

        return baos.toByteArray(); // be sure to close InputStream in calling function
    }

    /****
     * Method for Setting the Height of the ListView dynamically. Hack to fix
     * the issue of not showing all the items of the ListView when placed inside
     * a ScrollView
     ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
