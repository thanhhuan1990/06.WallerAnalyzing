package local.wallet.analyzing.report;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import local.wallet.analyzing.R;
import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.main.ActivityMain;
import local.wallet.analyzing.main.Configurations;
import local.wallet.analyzing.model.AccountType;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Currency;
import local.wallet.analyzing.model.Transaction;
import local.wallet.analyzing.sqlite.helper.DatabaseHelper;
import local.wallet.analyzing.transaction.FragmentTransactionCUD;

/**
 * Created by huynh.thanh.huan on 3/19/2016.
 */
public class FragmentReportEVITransactions extends Fragment {
    public static int               mTab = 4;
    public static final String      Tag = "---[" + mTab + "]---ReportEVITransactions";

    private ActivityMain            mActivity;

    private DatabaseHelper  mDbHelper;
    private Configurations  mConfigs;
    private Calendar        mFromDate   = Calendar.getInstance();
    private Calendar        mToDate     = Calendar.getInstance();

    private int[]           mAccountId  = new int[0]; // 0 is All Accounts

    private LinearLayout    llExpense;
    private PieChart        chartExpense;
    private LinearLayout    llExpenseTransactions;
    private LinearLayout    llIncome;
    private PieChart        mChartIncome;
    private LinearLayout    llIncomeTransactions;

    private ArrayList<CategoryEVI> arData = new ArrayList<CategoryEVI>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle       = this.getArguments();
        mTab                = bundle.getInt("Tab", mTab);
        mAccountId          = bundle.getIntArray("Accounts");
        mFromDate.setTimeInMillis(bundle.getLong("FromDate"));
        mToDate.setTimeInMillis(bundle.getLong("ToDate"));

        LogUtils.trace(Tag, "mAccountId = " + mAccountId.toString());
        LogUtils.trace(Tag, "Date: " + String.format(getResources().getString(R.string.format_day_month_year),
                mFromDate.get(Calendar.DAY_OF_MONTH),
                mFromDate.get(Calendar.MONTH) + 1,
                mFromDate.get(Calendar.YEAR)) + " - " + String.format(getResources().getString(R.string.format_day_month_year),
                mToDate.get(Calendar.DAY_OF_MONTH),
                mToDate.get(Calendar.MONTH) + 1,
                mToDate.get(Calendar.YEAR)));

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        LogUtils.logLeaveFunction(Tag, null, null);
        return inflater.inflate(R.layout.layout_fragment_report_evi_detail_period, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtils.logEnterFunction(Tag, null);
        super.onActivityCreated(savedInstanceState);

        mActivity               = (ActivityMain) getActivity();

        mConfigs                = new Configurations(getContext());
        mDbHelper               = new DatabaseHelper(getActivity());

        llExpense               = (LinearLayout) getView().findViewById(R.id.llExpense);
        chartExpense            = (PieChart) getView().findViewById(R.id.chartExpense);
        llExpenseTransactions   = (LinearLayout) getView().findViewById(R.id.llExpenseTransactions);

        llIncome                = (LinearLayout) getView().findViewById(R.id.llIncome);
        mChartIncome             = (PieChart) getView().findViewById(R.id.chartIncome);
        llIncomeTransactions    = (LinearLayout) getView().findViewById(R.id.llIncomeTransactions);

        LogUtils.logLeaveFunction(Tag, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        LogUtils.logEnterFunction(Tag, null);
        super.onCreateOptionsMenu(menu, inflater);

        if(mTab != mActivity.getCurrentVisibleItem()) {
            LogUtils.error(Tag, "Wrong Tab. Return");
            LogUtils.logLeaveFunction(Tag, null, null);
            return;
        }

        updateChartExpense();
        llExpenseTransactions.removeAllViews();
        updateIncomeChart();
        llIncomeTransactions.removeAllViews();
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private void updateChartExpense() {
        LogUtils.logEnterFunction(Tag, null);
        //region CHART EXPENSE
        //region get expense data from Database
        final ArrayList<CategoryEVI> arData = new ArrayList<CategoryEVI>();

        List<Category> arParentExpenseCategory = mDbHelper.getAllParentCategories(true);
        for(Category parentCate : arParentExpenseCategory) {
            List<Category> arExpenseCategory = mDbHelper.getCategoriesByParent(parentCate.getId());
            double amount = 0.0;
            for(Category cate : arExpenseCategory) {
                List<Transaction> arTransaction = mDbHelper.getTransactionsByTimeCategoryAccount(new int[]{cate.getId()}, mAccountId.length != mDbHelper.getAccountCount() ? mAccountId : null, mFromDate, mToDate);

                for(Transaction tran : arTransaction) {
                    amount += tran.getAmount();
                }

            }

            if(amount != 0) {
                arData.add(new CategoryEVI(parentCate, amount));
            }
        }

//        List<Category> arParentExpenseCategoryLoan = mDbHelper.getAllParentCategories(true, true);
//        for(Category parentCate : arParentExpenseCategoryLoan) {
//            List<Category> arExpenseCategory = mDbHelper.getCategoriesByParent(parentCate.getId());
//            double amount = 0.0;
//            for(Category cate : arExpenseCategory) {
//                List<Transaction> arTransaction = mDbHelper.getTransactionsByTimeCategoryAccount(new int[]{cate.getId()}, mAccountId.length != mDbHelper.getAccountCount() ? mAccountId : null, mFromDate, mToDate);
//
//                for(Transaction tran : arTransaction) {
//                    amount += tran.getAmount();
//                }
//
//            }
//
//            if(amount != 0) {
//                arData.add(new CategoryEVI(parentCate, amount));
//            }
//        }

        Collections.sort(arData, new Comparator<CategoryEVI>() {
            @Override
            public int compare(CategoryEVI c1, CategoryEVI c2) {
                return Double.compare(c2.amount, c1.amount);
            }
        });

        LogUtils.trace(Tag, "Expense Data = " + arData.toString());

        ArrayList<String> xVals = new ArrayList<String>();
        for(CategoryEVI cate : arData) {
            xVals.add(cate.category.getName());
        }

        double expenseAmount = 0.0;
        final ArrayList<Entry> yVals = new ArrayList<Entry>();
        for(int i = 0 ; i < arData.size(); i++) {
            yVals.add(new Entry((float) arData.get(i).amount, i));
            expenseAmount += arData.get(i).amount;
        }
        //endregion

        //region Setup Chart Expense
        if(expenseAmount == 0) {
            llExpense.setVisibility(View.GONE);
        } else {
            llExpense.setVisibility(View.VISIBLE);
            chartExpense.setUsePercentValues(true);
            chartExpense.setDrawSliceText(false);
            chartExpense.setDescription("");
            chartExpense.setExtraOffsets(5, 10, 5, 5);

            chartExpense.setDragDecelerationFrictionCoef(0.95f);

            chartExpense.setCenterTextTypeface(Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf"));
            chartExpense.setCenterText(generateCenterSpannableText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), expenseAmount)));
            chartExpense.setDrawCenterText(true);

            chartExpense.setDrawHoleEnabled(true);
            chartExpense.setHoleColor(Color.WHITE);

            chartExpense.setTransparentCircleColor(Color.WHITE);
            chartExpense.setTransparentCircleAlpha(110);

            chartExpense.setHoleRadius(58f);
            chartExpense.setTransparentCircleRadius(61f);

            chartExpense.setRotationAngle(0);
            // enable rotation of the chart by touch
            chartExpense.setRotationEnabled(false);
            chartExpense.setHighlightPerTapEnabled(true);

            final double fExpenseAmount = expenseAmount;
            // add a selection listener
            chartExpense.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                    chartExpense.setCenterText(generateCenterSpannableText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), Double.parseDouble(Float.toString(e.getVal())))));
                    chartExpense.invalidate();

                    List<CategoryTransaction> arCategoryTransaction = new ArrayList<CategoryTransaction>();
                    List<Transaction> arParentTransactions = mDbHelper.getTransactionsByTimeCategoryAccount(new int[]{arData.get(e.getXIndex()).category.getId()}, mAccountId, mFromDate, mToDate);
                    if(arParentTransactions.size() > 0) {
                        arCategoryTransaction.add(new CategoryTransaction(arData.get(e.getXIndex()).category, arParentTransactions, true));
                    }

                    List<Category> arChildCategory = mDbHelper.getCategoriesByParent(arData.get(e.getXIndex()).category.getId());
                    for(Category cate : arChildCategory) {

                        List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeCategoryAccount(new int[]{cate.getId()}, mAccountId, mFromDate, mToDate);

                        if(arTransactions.size() > 0) {
                            arCategoryTransaction.add(new CategoryTransaction(cate, arTransactions, true));
                        }

                    }

                    updateListTransactions(llExpenseTransactions, arCategoryTransaction);
                }

                @Override
                public void onNothingSelected() {
                    chartExpense.setCenterText(generateCenterSpannableText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), fExpenseAmount)));
                    chartExpense.invalidate();
                    llExpenseTransactions.removeAllViews();
                }
            });

            PieDataSet dataSet = new PieDataSet(yVals, "");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);

            // add a lot of colors
            ArrayList<Integer> colorsExpense = new ArrayList<Integer>();

            for (int c : ColorTemplate.VORDIPLOM_COLORS) {
                colorsExpense.add(c);
            }

            for (int c : ColorTemplate.JOYFUL_COLORS) {
                colorsExpense.add(c);
            }

            for (int c : ColorTemplate.COLORFUL_COLORS) {
                colorsExpense.add(c);
            }

            for (int c : ColorTemplate.LIBERTY_COLORS) {
                colorsExpense.add(c);
            }

            for (int c : ColorTemplate.PASTEL_COLORS) {
                colorsExpense.add(c);
            }

            colorsExpense.add(ColorTemplate.getHoloBlue());

            dataSet.setColors(colorsExpense);

            PieData data = new PieData(xVals, dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.GRAY);
            data.setValueTypeface(Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf"));
            chartExpense.setData(data);

            // undo all highlights
            chartExpense.highlightValues(null);

            chartExpense.invalidate();

            chartExpense.animateY(1400, Easing.EasingOption.EaseInOutQuad);
            // mChart.spin(2000, 0, 360);

            Legend l = chartExpense.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
            l.setXEntrySpace(17f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);
            //endregion region Setup Chart Expense
        }
        //endregion Setup Chart Expense
        //endregion CHART EXPENSE
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private void updateIncomeChart() {
        LogUtils.logEnterFunction(Tag, null);
        //region CHART INCOME
        //region get income data from Database
        final ArrayList<CategoryEVI> arDataIncome = new ArrayList<CategoryEVI>();

        List<Category> arIncomeCategory = mDbHelper.getAllCategories(false);
        for(Category cate : arIncomeCategory) {
            List<Transaction> arTransaction = mDbHelper.getTransactionsByTimeCategoryAccount(new int[]{cate.getId()}, mAccountId.length != mDbHelper.getAccountCount() ? mAccountId : null, mFromDate, mToDate);
            double cateAmount = 0.0;
            for(Transaction tran : arTransaction) {
                cateAmount += tran.getAmount();
            }

            if(cateAmount != 0) {
                arDataIncome.add(new CategoryEVI(cate, cateAmount));
            }
        }

//        List<Category> arIncomeCategoryLoan = mDbHelper.getAllCategories(false, true);
//        for(Category cate : arIncomeCategoryLoan) {
//            List<Transaction> arTransaction = mDbHelper.getTransactionsByTimeCategoryAccount(new int[]{cate.getId()}, mAccountId.length != mDbHelper.getAccountCount() ? mAccountId : null, mFromDate, mToDate);
//            double cateAmount = 0.0;
//            for(Transaction tran : arTransaction) {
//                cateAmount += tran.getAmount();
//            }
//
//            if(cateAmount != 0) {
//                arDataIncome.add(new CategoryEVI(cate, cateAmount));
//            }
//        }

        Collections.sort(arDataIncome, new Comparator<CategoryEVI>() {
            @Override
            public int compare(CategoryEVI c1, CategoryEVI c2) {
                return Double.compare(c2.amount, c1.amount);
            }
        });

        LogUtils.trace(Tag, "Income Data = " + arData.toString());

        ArrayList<String> xValsIncome = new ArrayList<String>();
        for(CategoryEVI cate : arDataIncome) {
            xValsIncome.add(cate.category.getName());
        }

        double incomeAmount = 0.0;
        ArrayList<Entry> yValsIncome = new ArrayList<Entry>();
        for(int i = 0 ; i < arDataIncome.size(); i++) {
            yValsIncome.add(new Entry((float) arDataIncome.get(i).amount, i));
            incomeAmount += arDataIncome.get(i).amount;
        }
        //endregion

        //region Setup View Chart Income
        if(incomeAmount == 0) {
            llIncome.setVisibility(View.GONE);
        } else {
            llIncome.setVisibility(View.VISIBLE);
            mChartIncome.setUsePercentValues(true);
            mChartIncome.setDrawSliceText(false);
            mChartIncome.setDescription("");
            mChartIncome.setExtraOffsets(5, 10, 5, 5);

            mChartIncome.setDragDecelerationFrictionCoef(0.95f);

            mChartIncome.setCenterTextTypeface(Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf"));
            mChartIncome.setCenterText(generateCenterSpannableText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), incomeAmount)));
            mChartIncome.setDrawCenterText(true);

            mChartIncome.setDrawHoleEnabled(true);
            mChartIncome.setHoleColor(Color.WHITE);

            mChartIncome.setTransparentCircleColor(Color.WHITE);
            mChartIncome.setTransparentCircleAlpha(110);

            mChartIncome.setHoleRadius(58f);
            mChartIncome.setTransparentCircleRadius(61f);

            mChartIncome.setRotationAngle(0);
            // enable rotation of the chart by touch
            mChartIncome.setRotationEnabled(false);
            mChartIncome.setHighlightPerTapEnabled(true);

            final double fIncomeAmount = incomeAmount;
            // add a selection listener
            mChartIncome.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                    mChartIncome.setCenterText(generateCenterSpannableText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), Double.parseDouble(Float.toString(e.getVal())))));
                    mChartIncome.invalidate();

                    List<CategoryTransaction> arCategoryTransaction = new ArrayList<CategoryTransaction>();
                    List<Transaction> arParentTransactions = mDbHelper.getTransactionsByTimeCategoryAccount(new int[]{arDataIncome.get(e.getXIndex()).category.getId()}, mAccountId, mFromDate, mToDate);
                    if(arParentTransactions.size() > 0) {
                        arCategoryTransaction.add(new CategoryTransaction(arDataIncome.get(e.getXIndex()).category, arParentTransactions, true));
                    }

                    List<Category> arChildCategory = mDbHelper.getCategoriesByParent(arDataIncome.get(e.getXIndex()).category.getId());
                    for(Category cate : arChildCategory) {

                        List<Transaction> arTransactions = mDbHelper.getTransactionsByTimeCategoryAccount(new int[]{cate.getId()}, mAccountId, mFromDate, mToDate);

                        if(arTransactions.size() > 0) {
                            arCategoryTransaction.add(new CategoryTransaction(cate, arTransactions, true));
                        }

                    }

                    updateListTransactions(llIncomeTransactions, arCategoryTransaction);
                }

                @Override
                public void onNothingSelected() {
                    mChartIncome.setCenterText(generateCenterSpannableText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), fIncomeAmount)));
                    mChartIncome.invalidate();
                    llIncomeTransactions.removeAllViews();
                }
            });

            PieDataSet dataSetIncome = new PieDataSet(yValsIncome, "");
            dataSetIncome.setSliceSpace(3f);
            dataSetIncome.setSelectionShift(5f);

            // add a lot of colors
            ArrayList<Integer> colorsIncome = new ArrayList<Integer>();

            for (int c : ColorTemplate.JOYFUL_COLORS) {
                colorsIncome.add(c);
            }

            for (int c : ColorTemplate.COLORFUL_COLORS) {
                colorsIncome.add(c);
            }

            for (int c : ColorTemplate.LIBERTY_COLORS) {
                colorsIncome.add(c);
            }

            for (int c : ColorTemplate.PASTEL_COLORS) {
                colorsIncome.add(c);
            }

            for (int c : ColorTemplate.VORDIPLOM_COLORS) {
                colorsIncome.add(c);
            }

            colorsIncome.add(ColorTemplate.getHoloBlue());

            dataSetIncome.setColors(colorsIncome);

            PieData dataIncome = new PieData(xValsIncome, dataSetIncome);
            dataIncome.setValueFormatter(new PercentFormatter());
            dataIncome.setValueTextSize(11f);
            dataIncome.setValueTextColor(Color.GRAY);
            dataIncome.setValueTypeface(Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf"));
            mChartIncome.setData(dataIncome);

            // undo all highlights
            mChartIncome.highlightValues(null);

            mChartIncome.invalidate();

            mChartIncome.animateY(1400, Easing.EasingOption.EaseInOutQuad);

            Legend legendIncome = mChartIncome.getLegend();
            legendIncome.setPosition(Legend.LegendPosition.LEFT_OF_CHART_CENTER);
            legendIncome.setXEntrySpace(17f);
            legendIncome.setYEntrySpace(0f);
            legendIncome.setYOffset(0f);
            //endregion Set Data
        }
        //endregion Setup View Chart Income
        //endregion CHART INCOME
        LogUtils.logLeaveFunction(Tag, null, null);
    }

    private SpannableString generateCenterSpannableText(String amount) {

        SpannableString str = new SpannableString(amount);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new RelativeSizeSpan(1.7f), 0, str.length(), 0);
        str.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 0, str.length(), 0);

        return str;
    }

    /**
     * Show Expense vs Income as PieChart of Period
     */
    private class CategoryEVI {
        Category category;
        double      amount;

        public CategoryEVI(Category category, double amount) {
            this.category = category;
            this.amount = amount;
        }
    }

    private class CategoryTransaction {
        Category category;
        List<Transaction> arTransactions;
        boolean isShow;

        public CategoryTransaction(Category category, List<Transaction> arTransactions, boolean isShow) {
            this.category = category;
            this.arTransactions = arTransactions;
            this.isShow = isShow;
        }
    }

    /**
     * Update list Transactions
     */
    private void updateListTransactions(LinearLayout layout, List<CategoryTransaction> arTransactions) {
        LogUtils.logEnterFunction(Tag, null);
        layout.removeAllViews();

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        for(CategoryTransaction category : arTransactions) {

            View                    categoryView    = mInflater.inflate(R.layout.listview_item_transaction_follow_category, null);
            LinearLayout            llCategory      = (LinearLayout) categoryView.findViewById(R.id.llCategory);
            final ImageView ivExpand                = (ImageView) categoryView.findViewById(R.id.ivExpand);
            TextView                tvCategory      = (TextView) categoryView.findViewById(R.id.tvCategory);
            TextView                tvAmount        = (TextView) categoryView.findViewById(R.id.tvAmount);
            final LinearLayout      llTransactions  = (LinearLayout) categoryView.findViewById(R.id.llTransactions);

            final Animation expand = AnimationUtils.loadAnimation(getActivity(), R.anim.expand_no_refresh);
            expand.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    LogUtils.trace(Tag, "expand Animation");
                    llTransactions.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            final Animation shrink = AnimationUtils.loadAnimation(getActivity(), R.anim.shrink_no_refresh);
            shrink.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    LogUtils.trace(Tag, "shrink Animation");
                    llTransactions.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            llCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (llTransactions.getVisibility() == View.GONE) {
                        ivExpand.startAnimation(expand);
                    } else {
                        ivExpand.startAnimation(shrink);
                    }
                }
            });

            tvCategory.setText(String.format(getResources().getString(R.string.budget_detail_transaction_category_sum),
                    category.category.getName()));
            Double expensed = 0.0;
            for(Transaction tran : category.arTransactions) {
                expensed += tran.getAmount();
            }

            tvAmount.setText(Currency.formatCurrency(getContext(), mConfigs.getInt(Configurations.Key.Currency), expensed));

            /* Todo: Add list of transaction for category */
            for(final Transaction transaction : category.arTransactions) {
                View        transactionView     = mInflater.inflate(R.layout.listview_item_budget_transaction_detail, null);
                TextView    tvTranCategory      = (TextView) transactionView.findViewById(R.id.tvCategory);
                TextView    tvTranAmount        = (TextView) transactionView.findViewById(R.id.tvAmount);
                TextView    tvDescription       = (TextView) transactionView.findViewById(R.id.tvDescription);
                TextView    tvDate              = (TextView) transactionView.findViewById(R.id.tvDate);
                TextView    tvAccount           = (TextView) transactionView.findViewById(R.id.tvAccount);
                ImageView   ivAccountIcon       = (ImageView) transactionView.findViewById(R.id.ivAccountIcon);

                tvTranCategory.setText(String.format(getResources().getString(R.string.content_expense),
                        mDbHelper.getCategory(transaction.getCategoryId()).getName()));
                tvTranAmount.setText(Currency.formatCurrency(getActivity(), mConfigs.getInt(Configurations.Key.Currency), transaction.getAmount()));
                if(!transaction.getDescription().equals("")) {
                    tvDescription.setText(transaction.getDescription());
                } else {
                    tvDescription.setVisibility(View.GONE);
                }

                tvDate.setText(String.format(getResources().getString(R.string.format_day_month_year),
                        transaction.getTime().get(Calendar.DAY_OF_MONTH),
                        transaction.getTime().get(Calendar.MONTH) + 1,
                        transaction.getTime().get(Calendar.YEAR)));
                if(transaction.getFromAccountId() != 0) {
                    tvAccount.setText(mDbHelper.getAccount(transaction.getFromAccountId()).getName());
                    ivAccountIcon.setImageResource(AccountType.getAccountTypeById(mDbHelper.getAccount(transaction.getFromAccountId()).getTypeId()).getIcon());
                } else if(transaction.getToAccountId() != 0) {
                    tvAccount.setText(mDbHelper.getAccount(transaction.getToAccountId()).getName());
                    ivAccountIcon.setImageResource(AccountType.getAccountTypeById(mDbHelper.getAccount(transaction.getToAccountId()).getTypeId()).getIcon());
                }

                transactionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogUtils.trace(Tag, "ReportEVITransactions -> TransactionCUD");
                        FragmentTransactionCUD nextFrag = new FragmentTransactionCUD();
                        Bundle bundle = new Bundle();
                        bundle.putInt("Tab", mTab);
                        bundle.putSerializable("Transaction", transaction);
                        nextFrag.setArguments(bundle);
                        mActivity.addFragment(mTab, R.id.ll_report, nextFrag, "FragmentTransactionCUD", true);
                    }
                });

                llTransactions.addView(transactionView);
            }

            layout.addView(categoryView);
        }

        LogUtils.logLeaveFunction(Tag, null, null);
    } // End updateListTransactions

}
