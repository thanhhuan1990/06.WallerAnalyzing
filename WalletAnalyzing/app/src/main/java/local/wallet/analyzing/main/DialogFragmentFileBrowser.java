package local.wallet.analyzing.main;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.FileUtils;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;

/**
 * Created by huynh.thanh.huan on 1/5/2016.
 */
public class DialogFragmentFileBrowser extends DialogFragment {

    private static final String Tag = "DialogFragmentFileBrowser";

    /** SDCard directory */
    public static final String SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    /** Parent path */
    private static final String PARENT_PATH = "../";
    /** The adapter of list of directories */
    private ArrayAdapter<Item> mListAdapter = null;
    /** List names of sub-directories */
    private List<Item> mListFilesAnddirectories = new ArrayList<Item>();
    /** The current directory name */
    private String mDirectoryPath = SDCARD_DIR;

    private String mDataFilePath = SDCARD_DIR;

    DatabaseHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        View rootView = inflater.inflate(R.layout.file_browser_layout, container, false);

        db = new DatabaseHelper(getActivity());

        final TextView tv_directory = (TextView) rootView.findViewById(R.id.tv_directory_path);
        tv_directory.setText(mDirectoryPath);
        final ListView lv_files = (ListView) rootView.findViewById(R.id.lv_files);
        mListFilesAnddirectories = new ArrayList<Item>();

        List<Item> listFiles = getItems(mDirectoryPath);
        mListFilesAnddirectories.addAll(listFiles);

        mListAdapter = new FileListAdapter(getActivity(), mListFilesAnddirectories);
        lv_files.setAdapter(mListAdapter);
        final AlertDialog selectFileDialog = new AlertDialog.Builder(getActivity())
                // Set Layout.
                .setView(rootView)
                // Set Title.
                .setTitle(R.string.select_file_title)
                .setNegativeButton(android.R.string.cancel, null).create();

        lv_files.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item item = mListFilesAnddirectories.get(i);
                if (item.mIsFile) {
                    selectFileDialog.dismiss();
                    mDataFilePath = mDirectoryPath + File.separator + item.mName;
                    try {
                        read("MISA");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (item.mName.equals(PARENT_PATH)) {
                        String[] arPath = mDirectoryPath.split(File.separator);
                        mDirectoryPath = "/";
                        for (int index = 0; index < arPath.length - 1; index++) {
                            if (!arPath[index].equals("")) {
                                if (index != arPath.length - 2) {
                                    mDirectoryPath += arPath[index] + File.separator;
                                } else {
                                    mDirectoryPath += arPath[index];
                                }
                            }
                        }
                    } else {
                        mDirectoryPath += File.separator + item.mName;
                    }
                    tv_directory.setText(mDirectoryPath);
                    updateDirectory();
                }
            }
        });

        LogUtils.logLeaveFunction(Tag, null, null);
        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        LogUtils.logLeaveFunction(Tag, null, null);
        return dialog;
    }

    @Override
    public void onStart() {
        LogUtils.logEnterFunction(Tag, null);
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    public void read(String key) throws IOException {
        LogUtils.logEnterFunction(Tag, null);

        class Data {
            String date = "";
            String time = "";
            String account = "";
            String amount = "";
            String parentCategory = "";
            String category = "";
            String reason = "";
            String event = "";
        }

        List<Data> datas = new ArrayList<Data>();

        File inputWorkbook = new File(mDataFilePath);
        if(inputWorkbook.exists()){
            Workbook w;
            try {
                w = Workbook.getWorkbook(inputWorkbook);
                // Get the first sheet
                Sheet sheet = w.getSheet(0);

                // Loop over columns and lines
                for (int j = 0; j < sheet.getRows(); j++) {
                    if((j > 9)) {
                        Data data = new Data();
                        for (int i = 0; i < sheet.getColumns(); i++) {

                            Cell cell = sheet.getCell(i, j);
                            switch (i) {
                                case 0:
                                    data.date = cell.getContents();
                                    break;
                                case 1:
                                    data.time = cell.getContents();
                                    break;
                                case 2:
                                    data.account = cell.getContents();
                                    break;
                                case 3:
                                    data.amount = cell.getContents();
                                    break;
                                case 5:
                                    data.parentCategory = cell.getContents();
                                    break;
                                case 6:
                                    data.category = cell.getContents();
                                    break;
                                case 7:
                                    data.reason = cell.getContents();
                                    break;
                                case 8:
                                    data.event = cell.getContents();
                                    break;
                            }
                        }
                        datas.add(data);
                    }
                }
            } catch (BiffException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }

        dismiss();

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    /**
     * Select File In SDCard
     */
    private class Item {

        String mName = "";
        String mPath = "";
        boolean mIsFile = false;

        public Item(String name, String path, boolean isFile) {
            this.mName = name;
            this.mPath = path;
            this.mIsFile = isFile;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    /**
     * Adapter to show ListView of folders and files in SDCard
     */
    private class FileListAdapter extends ArrayAdapter<Item> {

        private class ViewHolder {
            ImageView mIcon;
            TextView mName;
        }

        public FileListAdapter(Context context, List<Item> objects) {
            super(context, R.layout.file_browser_item, objects);
        }

        @Override
        public View getView(int position, View rowView, ViewGroup parent) {

            final int pos = position;
            Item item = getItem(pos);

            if (item == null)
                return rowView;

            ViewHolder holder;
            if (rowView == null) {
                rowView = LayoutInflater.from(getContext()).inflate(R.layout.file_browser_item, parent, false);
                holder = new ViewHolder();
                holder.mIcon = (ImageView) rowView.findViewById(R.id.iv_icon);
                holder.mName = (TextView) rowView.findViewById(R.id.tv_file_name);

                rowView.setTag(holder);
            } else {
                holder = (ViewHolder) rowView.getTag();
            }

            holder.mName.setText(item.mName);

            if (item.mIsFile) {
                holder.mIcon.setImageResource(R.drawable.icon_file);
            } else {
                holder.mIcon.setImageResource(R.drawable.icon_folder);
            }

            return rowView;
        }

    }

    /**
     * Refresh ListView of Folders and Files in dialog select file
     */
    private void updateDirectory() {

        File nextDir = FileUtils.createFile(mDirectoryPath);

        // If mDirectoryPath is not accaeptable, return
        if (nextDir == null || !nextDir.exists() || !nextDir.isDirectory()) {
            return;
        }

        // If mDirectoryPath is acceptable, get list of Folders and Files in this path
        mListFilesAnddirectories.clear();
        List<Item> listFiles = getItems(mDirectoryPath);
        mListFilesAnddirectories.addAll(listFiles);

        // Refresh ListView
        mListAdapter.notifyDataSetChanged();
    }

    /**
     * Get list of Folder and Files in input path in SDCard
     *
     * @param directoryPath
     *                      Path to directory in SDCard
     * @return List<Item>
     */
    private List<Item> getItems(String directoryPath) {

        List<Item> objects = new ArrayList<Item>();

        // If current directoru isn't SDCard's path, add up item
        if (!mDirectoryPath.equals(SDCARD_DIR)) {
            objects.add(0, new Item(PARENT_PATH, mDirectoryPath, false));
        }

        try {
            File dirFile = FileUtils.createFile(directoryPath);
            if (dirFile == null || !dirFile.exists() || !dirFile.isDirectory()) {
                return objects;
            }

            File[] files = dirFile.listFiles();
            if (files == null) {
                LogUtils.trace(Tag, directoryPath + " has no file!");
                return objects;
            }

            for (File file : files) {

                if (file == null) {
                    continue;
                }

                String fileName = file.getName();

                if (file.isFile()) {
                    // Get list of certificate_file_extension in string.xml
                    String[] certificate_file_extension = this.getResources().getStringArray(R.array.data_file_extension);

                    String fullPath = file.getAbsolutePath();
                    int dot = fullPath.lastIndexOf(".");
                    String ext = fullPath.substring(dot + 1);

                    for(int i = 0 ; i < certificate_file_extension.length; i++) {
                        // If file's extension is acceptable, add to list
                        if(ext.equals(certificate_file_extension[i])) {
                            objects.add(new Item(fileName, file.getAbsolutePath(), true));
                        }
                    }

                } else {
                    objects.add(new Item(fileName, file.getAbsolutePath(), false));
                }
            }
        } catch (Exception e) {
        }

        // Sort list by File's Type and File's Name: Folders and then Files
        Collections.sort(objects, new Comparator<Item>() {
            public int compare(Item o1, Item o2) {
                if (o1.mIsFile && !o2.mIsFile) {
                    return +1;
                }
                if (!o1.mIsFile && o2.mIsFile) {
                    return -1;
                }
                return o1.toString().compareTo(o2.toString());
            }
        });

        return objects;
    }
}
