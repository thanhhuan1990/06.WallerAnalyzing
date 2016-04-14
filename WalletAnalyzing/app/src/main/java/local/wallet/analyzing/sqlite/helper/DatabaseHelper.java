package local.wallet.analyzing.sqlite.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.Budget;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Category.EnumDebt;
import local.wallet.analyzing.model.Debt;
import local.wallet.analyzing.model.Event;
import local.wallet.analyzing.model.Kind;
import local.wallet.analyzing.model.Transaction;

/**
 * Created by huynh.thanh.huan on 11/24/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String TAG = "DatabaseHelper";

    private final boolean trace = false;

    // Database Version
    private static final int DATABASE_VERSION                   = 2;

    public static final int ERROR_DB_EXISTED                    = -1;

    // Database Name
    private static final String DATABASE_NAME                   = "WalletManaging.db";

    // Table Names
    private static final String TABLE_KIND                      = "kinds";
    private static final String TABLE_CATEGORY                  = "categories";
    private static final String TABLE_ACCOUNT                   = "accounts";
    private static final String TABLE_TRANSACTION               = "transactions";
    private static final String TABLE_BUDGET                    = "budgets";
    private static final String TABLE_EVENT                     = "events";
    private static final String TABLE_DEBTS                     = "debts";

    // Common column names
    private static final String KEY_ID                          = "id";
    private static final String KEY_NAME                        = "name";
    private static final String KEY_START_DATE                  = "start_date";
    private static final String KEY_END_DATE                    = "end_date";

    // CATEGORY Table - column names
    private static final String KEY_CATEGORY_PARENT_ID          = "parent_id";
    private static final String KEY_CATEGORY_EXPENSE            = "expense";
    private static final String KEY_CATEGORY_DEBT               = "debt";

    // Table ACCOUNT - column names
    private static final String KEY_ACCOUNT_TYPE_ID             = "type_id";
    private static final String KEY_ACCOUNT_CURRENCY            = "currency";
    private static final String KEY_ACCOUNT_INITIAL_BALANCE     = "initial_balance";
    private static final String KEY_ACCOUNT_DESCRIPTION         = "description";

    // TRANSACTION Table - column names
    private static final String KEY_TRANSACTION_TYPE            = "type";
    private static final String KEY_TRANSACTION_AMOUNT          = "amount";
    private static final String KEY_TRANSACTION_DESCRIPTION     = "description";
    private static final String KEY_TRANSACTION_CATEGORY_ID     = "category_id";
    private static final String KEY_TRANSACTION_FROM_ACCOUNT_ID = "from_account_id";
    private static final String KEY_TRANSACTION_TO_ACCOUNT_ID   = "to_account_id";
    private static final String KEY_TRANSACTION_TIME            = "time";
    private static final String KEY_TRANSACTION_FEE             = "fee";
    private static final String KEY_TRANSACTION_PAYEE           = "payee";
    private static final String KEY_TRANSACTION_EVENT           = "event";

    // BUDGET Table - column names
    private static final String KEY_BUDGET_AMOUNT               = "amount";
    private static final String KEY_BUDGET_CATEGORY             = "category";
    private static final String KEY_BUDGET_CURRENCY             = "currency";
    private static final String KEY_BUDGET_REPEAT_TYPE          = "repeat";
    private static final String KEY_BUDGET_INCREMENTAL          = "incremental";

    // DEBT table - column name
    private static final String KEY_DEBT_CATEGORY               = "category_id";
    private static final String KEY_DEBT_TRANSACTION            = "transaction_id";
    private static final String KEY_DEBT_AMOUNT                 = "amount";
    private static final String KEY_DEBT_PEOPLE                 = "people";

    //region LogUtils
    private void enter(String tag, String param) {
        if(trace) {
            enter(tag, param);
        }
    }

    private void leave(String tag, String param, String result) {
        if(trace) {
            leave(tag, param, result);
        }
    }

    private void trace(String tag, String param) {
        if(trace) {
            trace(tag, param);
        }
    }
    //endregion LogUtils

    //region DATABASE's method
    // Table Create Statements
    // KIND table create statement
    private static final String CREATE_TABLE_KIND = "CREATE TABLE "
            + TABLE_KIND + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT" + ")";

    // CATEGORY table create statement
    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE "
            + TABLE_CATEGORY+ "("
            + KEY_ID                    + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CATEGORY_PARENT_ID    + " INTEGER DEFAULT 0, "
            + KEY_NAME                  + " TEXT, "
            + KEY_CATEGORY_EXPENSE      + " INTEGER, "
            + KEY_CATEGORY_DEBT         + " INTEGER" + ")";

    // ACCOUNT table create statement
    private static final String CREATE_TABLE_ACCOUNT = "CREATE TABLE "
            + TABLE_ACCOUNT + "("
            + KEY_ID                        + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME                      + " TEXT,"
            + KEY_ACCOUNT_TYPE_ID           + " INTEGER,"
            + KEY_ACCOUNT_CURRENCY          + " INTEGER, "
            + KEY_ACCOUNT_INITIAL_BALANCE   + " DOUBLE,"
            + KEY_ACCOUNT_DESCRIPTION       + " TEXT" + ")";

    // TRANSACTION table create statement
    private static final String CREATE_TABLE_TRANSACTION = "CREATE TABLE "
            + TABLE_TRANSACTION + "("
            + KEY_ID                            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TRANSACTION_TYPE              + " INTEGER,"
            + KEY_TRANSACTION_AMOUNT            + " DOUBLE,"
            + KEY_TRANSACTION_DESCRIPTION       + " TEXT,"
            + KEY_TRANSACTION_CATEGORY_ID       + " INTEGER,"
            + KEY_TRANSACTION_FROM_ACCOUNT_ID   + " INTEGER,"
            + KEY_TRANSACTION_TO_ACCOUNT_ID     + " INTEGER,"
            + KEY_TRANSACTION_TIME              + " DATETIME,"
            + KEY_TRANSACTION_FEE               + " DOUBLE,"
            + KEY_TRANSACTION_PAYEE             + " TEXT,"
            + KEY_TRANSACTION_EVENT             + " INTEGER" + ")";

    // BUDGET table create statement
    private static final String CREATE_TABLE_BUDGET = "CREATE TABLE "
            + TABLE_BUDGET + "("
            + KEY_ID                    + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME                  + " TEXT,"
            + KEY_BUDGET_AMOUNT         + " DOUBLE,"
            + KEY_BUDGET_CATEGORY       + " TEXT,"
            + KEY_BUDGET_CURRENCY       + " INTEGER, "
            + KEY_BUDGET_REPEAT_TYPE    + " INTEGER,"
            + KEY_START_DATE            + " DATETIME,"
            + KEY_END_DATE              + " DATETIME,"
            + KEY_BUDGET_INCREMENTAL    + " INTEGER)";

    // EVENT table create statement
    private static final String CREATE_TABLE_EVENT = "CREATE TABLE "
            + TABLE_EVENT + "("
            + KEY_ID                    + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME                  + " TEXT,"
            + KEY_START_DATE            + " DATETIME,"
            + KEY_END_DATE              + " DATETIME)";

    // DEBT table create statement
    private static final String CREATE_TABLE_DEBT = "CREATE TABLE "
            + TABLE_DEBTS + "("
            + KEY_ID                    + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_DEBT_CATEGORY         + " INTEGER,"
            + KEY_DEBT_TRANSACTION      + " INTEGER,"
            + KEY_DEBT_AMOUNT           + " DOUBLE,"
            + KEY_DEBT_PEOPLE           + " TEXT)";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        enter(TAG, "onCreate");
        // creating required tables
        trace(TAG, CREATE_TABLE_KIND);
        db.execSQL(CREATE_TABLE_KIND);

        trace(TAG, CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_CATEGORY);

        trace(TAG, CREATE_TABLE_ACCOUNT);
        db.execSQL(CREATE_TABLE_ACCOUNT);

        trace(TAG, CREATE_TABLE_TRANSACTION);
        db.execSQL(CREATE_TABLE_TRANSACTION);

        trace(TAG, CREATE_TABLE_BUDGET);
        db.execSQL(CREATE_TABLE_BUDGET);

        trace(TAG, CREATE_TABLE_EVENT);
        db.execSQL(CREATE_TABLE_EVENT);

        trace(TAG, CREATE_TABLE_DEBT);
        db.execSQL(CREATE_TABLE_DEBT);

        leave(TAG, "onCreate", null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        enter(TAG, "onUpgrade");
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KIND);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEBTS);

        // create new tables
        onCreate(db);
        leave(TAG, "onUpgrade", null);
    }
    //endregion

    // ------------------------ KIND table methods ----------------//
    //region Table KIND
    /*
     * Creating a KIND
     */
    public long createKind(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);

        // insert row
        long kind_id = db.insert(TABLE_KIND, null, values);

        return kind_id;
    }

    /*
     * get single KIND
     */
    public Kind getKind(long kind_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_KIND + " WHERE " + KEY_ID + " = " + kind_id;

        trace(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
        }

        Kind td = new Kind();
        td.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        td.setName((c.getString(c.getColumnIndex(KEY_NAME))));

        return td;
    }

    /**
     * getting all KINDs
     * */
    public List<Kind> getAllKinds() {
        List<Kind> kinds = new ArrayList<Kind>();
        String selectQuery = "SELECT  * FROM " + TABLE_KIND;

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Kind kind = new Kind();
                kind.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                kind.setName((c.getString(c.getColumnIndex(KEY_NAME))));

                // adding to kinds list
                kinds.add(kind);
            } while (c.moveToNext());
        }

        return kinds;
    }

    /*
     * getting KIND count
     */
    public int getToDoCount() {
        String countQuery = "SELECT  * FROM " + TABLE_KIND;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    /*
     * Updating a KIND
     */
    public int updateToDo(Kind kind) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, kind.getId());
        values.put(KEY_NAME, kind.getName());

        // updating row
        return db.update(TABLE_KIND, values, KEY_ID + " = ?",
                new String[] { String.valueOf(kind.getId()) });
    }

    /*
     * Deleting a KIND
     */
    public void deleteKind(long kind_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_KIND, KEY_ID + " = ?",
                new String[]{String.valueOf(kind_id)});
    }
    //endregion

    // ------------------------ CATEGORY table methods ----------------//
    //region Table CATEGORY

    /*
     * Creating a CATEGORY
     */
    public long createCategory(int parentId, String name, boolean expense, EnumDebt debt) {
        enter(TAG, "ParentId = " + parentId + ", Name = " + name + ", expense = " + expense + ", debt = " + debt );
        SQLiteDatabase db = this.getWritableDatabase();

        List<Category> categories = getAllCategories(expense, debt);

        for(Category category : categories) {
            if(name.equals(category.getName())) {
                trace(TAG, "Category " + name + " is existed!");
                leave(TAG, "ParentId = " + parentId + ", Name = " + name + ", expense = " + expense + ", debt = " + debt, ERROR_DB_EXISTED + "");
                return ERROR_DB_EXISTED;
            }
        }

        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_PARENT_ID, parentId);
        values.put(KEY_NAME, name);
        values.put(KEY_CATEGORY_EXPENSE, expense ? 1 : 0);
        values.put(KEY_CATEGORY_DEBT, debt.getValue());

        // insert row
        long category_id = db.insert(TABLE_CATEGORY, null, values);

        leave(TAG, "ParentId = " + parentId + ", Name = " + name + ", expense = " + expense + ", debt = " + debt, "New Category's id: " + category_id);
        return category_id;
    }

    /**
     * Get Category by ID
     * @param category_id
     * @return Category
     */
    public Category getCategory(long category_id) {
        enter(TAG, "Id " + category_id);

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY + " WHERE " + KEY_ID + " = " + category_id;

        trace(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {

            Category category = new Category();
            category.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            category.setParentId(c.getInt(c.getColumnIndex(KEY_CATEGORY_PARENT_ID)));
            category.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            category.setExpense(c.getInt(c.getColumnIndex(KEY_CATEGORY_EXPENSE)) == 1 ? true : false);
            category.setDebtType(Category.EnumDebt.getEnumDebt(c.getInt(c.getColumnIndex(KEY_CATEGORY_DEBT))));

            leave(TAG, "Id " + category_id, category.toString());

            return category;
        } else {
            return null;
        }
    }

    /**
     * Get Category LIKE name
     * @param category_name
     * @return Category
     */
    public Category getCategory(String category_name) {
        enter(TAG, "Name " + category_name);

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY + " WHERE " + KEY_NAME + " like '%" + category_name + "%'";

        trace(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
        }

        Category category = new Category();
        category.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        category.setParentId(c.getInt(c.getColumnIndex(KEY_CATEGORY_PARENT_ID)));
        category.setName(c.getString(c.getColumnIndex(KEY_NAME)));
        category.setExpense(c.getInt(c.getColumnIndex(KEY_CATEGORY_EXPENSE)) == 1 ? true : false);
        category.setDebtType(Category.EnumDebt.getEnumDebt(c.getInt(c.getColumnIndex(KEY_CATEGORY_DEBT))));

        leave(TAG, "Name " + category_name, category.toString());

        return category;
    }

    /**
     * Get all parent category without condition
     * @return
     */
    public List<Category> getAllParentCategories() {
        enter(TAG, null);

        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + KEY_CATEGORY_PARENT_ID + " = 0";

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                category.setParentId(c.getInt((c.getColumnIndex(KEY_CATEGORY_PARENT_ID))));
                category.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                category.setExpense(c.getInt(c.getColumnIndex(KEY_CATEGORY_EXPENSE)) == 1 ? true : false);
                category.setDebtType(Category.EnumDebt.getEnumDebt(c.getInt(c.getColumnIndex(KEY_CATEGORY_DEBT))));

                // adding to kinds list
                categories.add(category);
            } while (c.moveToNext());
        }

        leave(TAG, null, "categories size = " + categories.size());

        return categories;
    }

    /**
     * Get all parent category with condition is true = Expense or false = Income
     * @param expense
     * @return
     */
    public List<Category> getAllParentCategories(boolean expense) {
        enter(TAG, null);

        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + KEY_CATEGORY_PARENT_ID + " = 0 AND " + KEY_CATEGORY_EXPENSE + " = " + (expense ? 1 : 0);

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                category.setParentId(c.getInt((c.getColumnIndex(KEY_CATEGORY_PARENT_ID))));
                category.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                category.setExpense(c.getInt(c.getColumnIndex(KEY_CATEGORY_EXPENSE)) == 1 ? true : false);
                category.setDebtType(Category.EnumDebt.getEnumDebt(c.getInt(c.getColumnIndex(KEY_CATEGORY_DEBT))));

                // adding to kinds list
                categories.add(category);
            } while (c.moveToNext());
        }

        leave(TAG, null, "categories size = " + categories.size());

        return categories;
    }

    /**
     * Get all parent category with condition
     * @param expense
     * @param debt
     * @return
     */
    public List<Category> getAllParentCategories(boolean expense, EnumDebt debt) {
        enter(TAG, null);

        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY + " WHERE " + KEY_CATEGORY_EXPENSE + " = " + (expense ? 1 : 0)
                                                                            + " AND " + KEY_CATEGORY_DEBT + " = " + debt.getValue()
                                                                            + " AND " + KEY_CATEGORY_PARENT_ID + " = 0";

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                category.setParentId(c.getInt((c.getColumnIndex(KEY_CATEGORY_PARENT_ID))));
                category.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                category.setExpense(c.getInt(c.getColumnIndex(KEY_CATEGORY_EXPENSE)) == 1 ? true : false);
                category.setDebtType(Category.EnumDebt.getEnumDebt(c.getInt(c.getColumnIndex(KEY_CATEGORY_DEBT))));

                // adding to kinds list
                categories.add(category);
            } while (c.moveToNext());
        }

        leave(TAG, null, "categories size = " + categories.size());

        return categories;
    }

    /**
     * Get All Categories without condition
     * @return List<Category>
     */
    public List<Category> getAllCategories() {
        enter(TAG, null);

        List<Category> categorys = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY;

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                category.setParentId(c.getInt((c.getColumnIndex(KEY_CATEGORY_PARENT_ID))));
                category.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                category.setExpense(c.getInt(c.getColumnIndex(KEY_CATEGORY_EXPENSE)) == 1 ? true : false);
                category.setDebtType(Category.EnumDebt.getEnumDebt(c.getInt(c.getColumnIndex(KEY_CATEGORY_DEBT))));

                // adding to kinds list
                categorys.add(category);
            } while (c.moveToNext());
        }

        leave(TAG, null, null);

        return categorys;
    }

    /**
     * Get All Categories with condition
     * @param expense
     * @return
     */
    public List<Category> getAllCategories(boolean expense) {
        enter(TAG, null);

        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY + " WHERE " + KEY_CATEGORY_EXPENSE + " = " + (expense ? 1 : 0);

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                category.setParentId(c.getInt((c.getColumnIndex(KEY_CATEGORY_PARENT_ID))));
                category.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                category.setExpense(c.getInt(c.getColumnIndex(KEY_CATEGORY_EXPENSE)) == 1 ? true : false);
                category.setDebtType(EnumDebt.getEnumDebt(c.getInt(c.getColumnIndex(KEY_CATEGORY_DEBT))));

                // adding to kinds list
                categories.add(category);
            } while (c.moveToNext());
        }

        leave(TAG, null, null);

        return categories;
    }

    /**
     * Get All Categories with condition
     * @param expense
     * @param debt
     * @return
     */
    public List<Category> getAllCategories(boolean expense, EnumDebt debt) {
        enter(TAG, null);

        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY + " WHERE " + KEY_CATEGORY_EXPENSE + " = " + (expense ? 1 : 0) + " AND " + KEY_CATEGORY_DEBT + " = " + debt.getValue();

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                category.setParentId(c.getInt((c.getColumnIndex(KEY_CATEGORY_PARENT_ID))));
                category.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                category.setExpense(c.getInt(c.getColumnIndex(KEY_CATEGORY_EXPENSE)) == 1 ? true : false);
                category.setDebtType(EnumDebt.getEnumDebt(c.getInt(c.getColumnIndex(KEY_CATEGORY_DEBT))));

                // adding to kinds list
                categories.add(category);
            } while (c.moveToNext());
        }

        leave(TAG, null, null);

        return categories;
    }


    /**
     * Getting all CATEGORIES follow ParentID
     * */
    public List<Category> getCategoriesByParent(int parentId) {
        enter(TAG, null);

        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY + " WHERE " + KEY_CATEGORY_PARENT_ID + " = " + parentId;

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                category.setParentId(c.getInt((c.getColumnIndex(KEY_CATEGORY_PARENT_ID))));
                category.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                category.setExpense(c.getInt(c.getColumnIndex(KEY_CATEGORY_EXPENSE)) == 1 ? true : false);
                category.setDebtType(Category.EnumDebt.getEnumDebt(c.getInt(c.getColumnIndex(KEY_CATEGORY_DEBT))));

                // adding to kinds list
                categories.add(category);
            } while (c.moveToNext());
        }

        leave(TAG, null, null);

        return categories;
    }

    /**
     * Getting CATEGORY count
     */
    public int getCategoryCount() {
        enter(TAG, null);

        String countQuery = "SELECT  * FROM " + TABLE_CATEGORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        leave(TAG, null, null);

        // return count
        return count;
    }

    /**
     * Updating a CATEGORY
     */
    public int updateCategory(Category category) {
        enter(TAG, null);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, category.getId());
        values.put(KEY_CATEGORY_PARENT_ID, category.getParentId());
        values.put(KEY_NAME, category.getName());
        values.put(KEY_CATEGORY_EXPENSE, category.isExpense() ? 1 : 0);
        values.put(KEY_CATEGORY_DEBT, category.getDebtType().getValue());

        leave(TAG, null, null);

        // updating row
        return db.update(TABLE_CATEGORY, values, KEY_ID + " = ?",
                new String[] { String.valueOf(category.getId()) });
    }

    /**
     * Deleting a CATEGORY
     */
    public void deleteCategory(long category_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORY, KEY_ID + " = ?",
                new String[] { String.valueOf(category_id) });
    }
    //endregion

    // ------------------------ ACCOUNT table methods ----------------//
    //region Table ACCOUNT

    /*
     * Creating a ACCOUNT
     */
    public long createAccount(String account_name, int type_id, int currency_id, double initial_balance, String description) {
        enter(TAG, "account_name = " + account_name + ", type_id = " + type_id + ", currency_id = " + currency_id + ", initial_balance = " + initial_balance + ", description = " + description);
        SQLiteDatabase db = this.getWritableDatabase();

        List<Account> accounts = getAllAccounts();

        for(Account acc : accounts) {
            if(account_name.equals(acc.getName())) {
                return -1;
            }
        }

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, account_name);
        values.put(KEY_ACCOUNT_TYPE_ID, type_id);
        values.put(KEY_ACCOUNT_CURRENCY, currency_id);
        values.put(KEY_ACCOUNT_INITIAL_BALANCE, initial_balance);
        values.put(KEY_ACCOUNT_DESCRIPTION, description);

        // insert row
        long account_id = db.insert(TABLE_ACCOUNT, null, values);

        leave(TAG, "account_name = " + account_name + ", type_id = " + type_id + ", currency_id = " + currency_id + ", initial_balance = " + initial_balance + ", description = " + description, "Account id = " + account_id);

        return account_id;
    }

    /*
     * get single ACCOUNT
     */
    public Account getAccount(long account_id) {
        enter(TAG, "account_id = " + account_id);

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT + " WHERE " + KEY_ID + " = " + account_id;

        trace(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            Account account = new Account();
            account.setId(c.getInt((c.getColumnIndex(KEY_ID))));
            account.setName((c.getString(c.getColumnIndex(KEY_NAME))));
            account.setTypeId(c.getInt(c.getColumnIndex(KEY_ACCOUNT_TYPE_ID)));
            account.setCurrencyId(c.getInt(c.getColumnIndex(KEY_ACCOUNT_CURRENCY)));
            account.setInitBalance(c.getDouble(c.getColumnIndex(KEY_ACCOUNT_INITIAL_BALANCE)));
            account.setDescription(c.getString(c.getColumnIndex(KEY_ACCOUNT_DESCRIPTION)));

            leave(TAG, "account_id = " + account_id, "Account = " + account.toString());
            return account;
        }

        leave(TAG, "account_id = " + account_id, null);
        return null;
    }

    /**
     * getting all ACCOUNTs
     * */
    public List<Account> getAllAccounts() {
        enter(TAG, null);

        List<Account> accounts = new ArrayList<Account>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT;

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Account account = new Account();
                account.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                account.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                account.setTypeId(c.getInt(c.getColumnIndex(KEY_ACCOUNT_TYPE_ID)));
                account.setCurrencyId(c.getInt(c.getColumnIndex(KEY_ACCOUNT_CURRENCY)));
                account.setInitBalance(c.getDouble(c.getColumnIndex(KEY_ACCOUNT_INITIAL_BALANCE)));
                account.setDescription(c.getString(c.getColumnIndex(KEY_ACCOUNT_DESCRIPTION)));

                // adding to kinds list
                accounts.add(account);
            } while (c.moveToNext());
        }

        leave(TAG, null, null);
        return accounts;
    }

    /**
     * getting all ACCOUNTs
     * */
    public List<Account> getAllAccountsByTypeId(int typeId) {
        enter(TAG, null);

        List<Account> accounts = new ArrayList<Account>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT + " WHERE " + KEY_ACCOUNT_TYPE_ID + " = " + typeId;

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Account account = new Account();
                account.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                account.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                account.setTypeId(c.getInt(c.getColumnIndex(KEY_ACCOUNT_TYPE_ID)));
                account.setCurrencyId(c.getInt(c.getColumnIndex(KEY_ACCOUNT_CURRENCY)));
                account.setInitBalance(c.getDouble(c.getColumnIndex(KEY_ACCOUNT_INITIAL_BALANCE)));
                account.setDescription(c.getString(c.getColumnIndex(KEY_ACCOUNT_DESCRIPTION)));

                // adding to kinds list
                accounts.add(account);
            } while (c.moveToNext());
        }

        leave(TAG, null, null);
        return accounts;
    }

    /**
     * Get account remain
     * @return
     */
    public Double getAccountRemain(int accountId) {
        enter(TAG, "accountId = " + accountId);

        Double remain = getAccount(accountId).getInitBalance();

        List<Transaction> arTransactions = getTransactionsByAccount(accountId);

        Collections.sort(arTransactions, new Comparator<Transaction>() {
            public int compare(Transaction o1, Transaction o2) {
                return o2.getTime().compareTo(o1.getTime());
            }
        });

        for (Transaction tran : arTransactions) {
            if (tran.getTransactionType() == Transaction.TransactionEnum.Expense.getValue()) {
                remain -= tran.getAmount();
            } else if (tran.getTransactionType() == Transaction.TransactionEnum.Income.getValue()) {
                remain += tran.getAmount();
            } else if (tran.getTransactionType() == Transaction.TransactionEnum.Transfer.getValue() ||
                    tran.getTransactionType() == Transaction.TransactionEnum.Adjustment.getValue()) {
                if (accountId == tran.getFromAccountId()) {
                    remain -= tran.getAmount();
                    remain -= tran.getFee();
                }
                if (accountId == tran.getToAccountId()) {
                    remain += tran.getAmount();
                }
            }
        }

        leave(TAG, "accountId = " + accountId, "remain = " + remain);
        return remain;
    }

    public Double getAccountRemainBefore(int accountId, Calendar time) {
        enter(TAG, "accountId = " + accountId + ", time = "  + time.toString());

        Double remain = getAccount(accountId).getInitBalance();

        List<Transaction> arTransactions = getTransactionsByAccount(accountId);

        Collections.sort(arTransactions, new Comparator<Transaction>() {
            public int compare(Transaction o1, Transaction o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        });

        for (Transaction tran : arTransactions) {
            if(tran.getTime().compareTo(time) >= 0) {
                return remain;
            }

            if (tran.getTransactionType() == Transaction.TransactionEnum.Expense.getValue()) {
                remain -= tran.getAmount();
            } else if (tran.getTransactionType() == Transaction.TransactionEnum.Income.getValue()) {
                remain += tran.getAmount();
            } else if (tran.getTransactionType() == Transaction.TransactionEnum.Transfer.getValue() ||
                    tran.getTransactionType() == Transaction.TransactionEnum.Adjustment.getValue() ) {
                if (accountId == tran.getFromAccountId()) {
                    remain -= tran.getAmount();
                    remain -= tran.getFee();
                }
                if (accountId == tran.getToAccountId()) {
                    remain += tran.getAmount();
                }
            }

        }

        leave(TAG, "accountId = " + accountId + ", time = " + time.toString(), "remain = " + remain);
        return remain;
    }

    public Double getAccountRemainAfter(int accountId, Calendar time) {
        enter(TAG, "accountId = " + accountId + ", time = " + time.toString());

        Double remain = getAccount(accountId).getInitBalance();

        List<Transaction> arTransactions = getTransactionsByAccount(accountId);

        Collections.sort(arTransactions, new Comparator<Transaction>() {
            public int compare(Transaction o1, Transaction o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        });

        for (Transaction tran : arTransactions) {

            if (tran.getTransactionType() == Transaction.TransactionEnum.Expense.getValue()) {
                remain -= tran.getAmount();
            } else if (tran.getTransactionType() == Transaction.TransactionEnum.Income.getValue()) {
                remain += tran.getAmount();
            } else if (tran.getTransactionType() == Transaction.TransactionEnum.Transfer.getValue() ||
                    tran.getTransactionType() == Transaction.TransactionEnum.Adjustment.getValue()) {
                if (accountId == tran.getFromAccountId()) {
                    remain -= tran.getAmount();
                    remain -= tran.getFee();
                }
                if (accountId == tran.getToAccountId()) {
                    remain += tran.getAmount();
                }
            }

            if(tran.getTime().compareTo(time) >= 0) {
                return remain;
            }

        }

        leave(TAG, "accountId = " + accountId + ", time = " + time.toString(), "remain = " + remain);
        return remain;
    }

    /*
     * getting ACCOUNT count
     */
    public int getAccountCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ACCOUNT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    /*
     * Updating a ACCOUNT
     */
    public void updateAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, account.getName());
        values.put(KEY_ACCOUNT_TYPE_ID, account.getTypeId());
        values.put(KEY_ACCOUNT_CURRENCY, account.getCurrencyId());
        values.put(KEY_ACCOUNT_INITIAL_BALANCE, account.getInitBalance());
        values.put(KEY_ACCOUNT_DESCRIPTION, account.getDescription());

        // updating row
        db.update(TABLE_ACCOUNT, values, KEY_ID + " = ?", new String[]{String.valueOf(account.getId())});
    }

    /*
     * Deleting a ACCOUNT
     */
    public void deleteAccount(long account_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCOUNT, KEY_ID + " = ?", new String[]{String.valueOf(account_id)});
    }
    //endregion

    // ------------------------ TRANSACTION table methods ----------------//
    //region Table TRANSACTION

    /**
     * Creating a TRANSACTION
     */
    public long createTransaction(Transaction transaction) {
        enter(TAG, "transaction = " + transaction.toString());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRANSACTION_TYPE, transaction.getTransactionType());
        values.put(KEY_TRANSACTION_AMOUNT, transaction.getAmount());
        values.put(KEY_TRANSACTION_DESCRIPTION, transaction.getDescription());
        values.put(KEY_TRANSACTION_CATEGORY_ID, transaction.getCategoryId());
        values.put(KEY_TRANSACTION_FROM_ACCOUNT_ID, transaction.getFromAccountId());
        values.put(KEY_TRANSACTION_TO_ACCOUNT_ID, transaction.getToAccountId());
        values.put(KEY_TRANSACTION_TIME, getStringDateTime(transaction.getTime().getTime()));
        values.put(KEY_TRANSACTION_FEE, transaction.getFee());
        values.put(KEY_TRANSACTION_PAYEE, transaction.getPayee());
        values.put(KEY_TRANSACTION_EVENT, transaction.getEvent() != null ? transaction.getEvent().getId() : 0);

        try {
            // insert row
            long transaction_id = db.insert(TABLE_TRANSACTION, null, values);

            leave(TAG, "transaction = " + transaction.toString(), "transaction_id = " + transaction_id);
            return transaction_id;

        } catch (android.database.SQLException e) {
            e.printStackTrace();
            leave(TAG, "transaction = " + transaction.toString(), "transaction_id = -1");
            return -1;
        }
    }

    public Transaction getLastTransaction() {
        enter(TAG, null);
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " ORDER BY " + KEY_TRANSACTION_TIME + " DESC LIMIT 1";

        trace(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            c.moveToFirst();

            Transaction transaction = new Transaction();
            transaction.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            transaction.setTransactionType(c.getInt(c.getColumnIndex(KEY_TRANSACTION_TYPE)));
            transaction.setAmount(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_AMOUNT)));
            transaction.setDescription(c.getString(c.getColumnIndex(KEY_TRANSACTION_DESCRIPTION)));
            transaction.setCategoryId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_CATEGORY_ID)));
            transaction.setFromAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_FROM_ACCOUNT_ID)));
            transaction.setToAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_TO_ACCOUNT_ID)));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_TRANSACTION_TIME))));
            transaction.setTime(calendar);
            transaction.setFee(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_FEE)));
            transaction.setPayee(c.getString(c.getColumnIndex(KEY_TRANSACTION_PAYEE)));
            transaction.setEvent(getEvent(c.getInt(c.getColumnIndex(KEY_TRANSACTION_EVENT))));

            leave(TAG, null, transaction.toString());

            return transaction;
        }

        leave(TAG, null, null);

        return null;
    }

    /**
     * get single TRANSACTION
     */
    public Transaction getTransaction(long transaction_id) {
        enter(TAG, "transaction_id = " + transaction_id);

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " WHERE " + KEY_ID + " = " + transaction_id;

        trace(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            c.moveToFirst();

            Transaction transaction = new Transaction();
            transaction.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            transaction.setTransactionType(c.getInt(c.getColumnIndex(KEY_TRANSACTION_TYPE)));
            transaction.setAmount(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_AMOUNT)));
            transaction.setDescription(c.getString(c.getColumnIndex(KEY_TRANSACTION_DESCRIPTION)));
            transaction.setCategoryId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_CATEGORY_ID)));
            transaction.setFromAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_FROM_ACCOUNT_ID)));
            transaction.setToAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_TO_ACCOUNT_ID)));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_TRANSACTION_TIME))));
            transaction.setTime(calendar);
            transaction.setFee(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_FEE)));
            transaction.setPayee(c.getString(c.getColumnIndex(KEY_TRANSACTION_PAYEE)));
            transaction.setEvent(getEvent(c.getInt(c.getColumnIndex(KEY_TRANSACTION_EVENT))));

            leave(TAG, "transaction_id = " + transaction_id, transaction.toString());

            return transaction;
        }

        leave(TAG, "transaction_id = " + transaction_id, null);

        return null;

    }

    /**
     * Get All transaction from startDate to endDate
     * @param categories
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Transaction> getTransactionsByTimeAndCategory(int[] categories, Calendar startDate, Calendar endDate) {
        enter(TAG, "categories = " + (categories != null ? categories.toString() : "")
                + ", startDate = " + (startDate != null ? startDate.getTimeInMillis() : "null")
                + ", endDate = " + (endDate != null ? endDate.getTimeInMillis() : "null"));

        SQLiteDatabase db = this.getReadableDatabase();

        String condition ="";
        if(categories != null) {
            condition = " WHERE " + KEY_TRANSACTION_CATEGORY_ID + " = " + categories[0];

            for(int i = 1 ; i < categories.length; i++) {
                condition += " OR " + KEY_TRANSACTION_CATEGORY_ID + " = " + categories[i];
            }
        }

        List<Transaction> transactions = new ArrayList<Transaction>();
        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + condition + " ORDER BY " + KEY_TRANSACTION_TIME + " DESC";

        trace(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            do {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_TRANSACTION_TIME))));

                if(startDate != null && endDate != null) {
                    if (calendar.getTimeInMillis() < startDate.getTimeInMillis() || endDate.getTimeInMillis() < calendar.getTimeInMillis()  ) {
                        continue;
                    }
                }

                Transaction transaction = new Transaction();
                transaction.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                transaction.setTransactionType(c.getInt(c.getColumnIndex(KEY_TRANSACTION_TYPE)));
                transaction.setAmount(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_AMOUNT)));
                transaction.setDescription(c.getString(c.getColumnIndex(KEY_TRANSACTION_DESCRIPTION)));
                transaction.setCategoryId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_CATEGORY_ID)));
                transaction.setFromAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_FROM_ACCOUNT_ID)));
                transaction.setToAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_TO_ACCOUNT_ID)));
                transaction.setTime(calendar);
                transaction.setFee(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_FEE)));
                transaction.setPayee(c.getString(c.getColumnIndex(KEY_TRANSACTION_PAYEE)));
                transaction.setEvent(getEvent(c.getInt(c.getColumnIndex(KEY_TRANSACTION_EVENT))));

                transactions.add(transaction);
            } while (c.moveToNext()) ;
        }

        leave(TAG, "categories = " + (categories != null ? categories.toString() : "") + ", startDate = " + (startDate != null ? startDate.getTimeInMillis() : "null")
                + ", endDate = " + (endDate != null ? endDate.getTimeInMillis() : "null"), transactions.toString());

        return transactions;

    }

    /**
     * Get All transaction from startDate to endDate by Accounts
     * @param accounts
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Transaction> getTransactionsByTimeAndAccount(int[] accounts, Calendar startDate, Calendar endDate) {
        enter(TAG, "accounts = " + (accounts != null ? accounts.toString() : "")
                + ", startDate = " + (startDate != null ? startDate.getTimeInMillis() : "0")
                + ", endDate = " + (endDate != null ? endDate.getTimeInMillis() : "0"));

        SQLiteDatabase db = this.getReadableDatabase();

        String condition ="";
        if(accounts != null) {
            condition = " WHERE " + KEY_TRANSACTION_FROM_ACCOUNT_ID + " = " + accounts[0] + " OR " + KEY_TRANSACTION_TO_ACCOUNT_ID + " = " + accounts[0];

            for(int i = 1 ; i < accounts.length; i++) {
                condition += " OR " + KEY_TRANSACTION_CATEGORY_ID + " = " + accounts[i] + " OR " + KEY_TRANSACTION_TO_ACCOUNT_ID + " = " + accounts[i];
            }
        }

        List<Transaction> transactions = new ArrayList<Transaction>();
        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + condition + " ORDER BY " + KEY_TRANSACTION_TIME + " DESC";

        trace(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            do {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_TRANSACTION_TIME))));

                if(startDate != null && endDate != null) {
                    if (calendar.getTimeInMillis() < startDate.getTimeInMillis() || endDate.getTimeInMillis() < calendar.getTimeInMillis()  ) {
                        continue;
                    }
                }

                Transaction transaction = new Transaction();
                transaction.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                transaction.setTransactionType(c.getInt(c.getColumnIndex(KEY_TRANSACTION_TYPE)));
                transaction.setAmount(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_AMOUNT)));
                transaction.setDescription(c.getString(c.getColumnIndex(KEY_TRANSACTION_DESCRIPTION)));
                transaction.setCategoryId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_CATEGORY_ID)));
                transaction.setFromAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_FROM_ACCOUNT_ID)));
                transaction.setToAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_TO_ACCOUNT_ID)));
                transaction.setTime(calendar);
                transaction.setFee(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_FEE)));
                transaction.setPayee(c.getString(c.getColumnIndex(KEY_TRANSACTION_PAYEE)));
                transaction.setEvent(getEvent(c.getInt(c.getColumnIndex(KEY_TRANSACTION_EVENT))));

                transactions.add(transaction);
            } while (c.moveToNext()) ;
        }

        leave(TAG, "accounts = " + (accounts != null ? accounts.toString() : "")
                + ", startDate = " + (startDate != null ? startDate.getTimeInMillis() : "0")
                + ", endDate = " + (endDate != null ? endDate.getTimeInMillis() : "0"), transactions.toString());

        return transactions;

    }

    /**
     * Get Transaction by Category, Account, Time
     * @param categories
     * @param accounts
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Transaction> getTransactionsByTimeCategoryAccount(int[] categories, int[] accounts, Calendar startDate, Calendar endDate) {
        enter(TAG, "categories = " + (categories != null ? categories.toString() : "")
                + "accounts = " + (accounts != null ? accounts.toString() : "")
                + "startDate = " + startDate.getTimeInMillis() + ", endDate = " + endDate);

        SQLiteDatabase db = this.getReadableDatabase();

        String condition = "";
        if(categories != null) {
            condition = " WHERE (" + KEY_TRANSACTION_CATEGORY_ID + " = " + categories[0];

            for(int i = 1 ; i < categories.length; i++) {
                condition += " OR " + KEY_TRANSACTION_CATEGORY_ID + " = " + categories[i];
            }

            condition += ")";
        }

        if(accounts != null) {
            if(condition.equals("")) {
                condition += " WHERE (";
            } else {
                condition += " And (";
            }
            condition += KEY_TRANSACTION_FROM_ACCOUNT_ID + " = " + accounts[0] + " OR " + KEY_TRANSACTION_TO_ACCOUNT_ID + " = " + accounts[0];

            for(int i = 1 ; i < accounts.length; i++) {
                condition += " OR " + KEY_TRANSACTION_CATEGORY_ID + " = " + accounts[i] + " OR " + KEY_TRANSACTION_TO_ACCOUNT_ID + " = " + accounts[i];
            }

            condition += ")";
        }

        List<Transaction> transactions = new ArrayList<Transaction>();
        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + condition + " ORDER BY " + KEY_TRANSACTION_TIME + " DESC";

        trace(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            do {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_TRANSACTION_TIME))));

                if(startDate != null && endDate != null) {
                    if (calendar.getTimeInMillis() < startDate.getTimeInMillis() || endDate.getTimeInMillis() < calendar.getTimeInMillis()  ) {
                        continue;
                    }
                }

                Transaction transaction = new Transaction();
                transaction.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                transaction.setTransactionType(c.getInt(c.getColumnIndex(KEY_TRANSACTION_TYPE)));
                transaction.setAmount(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_AMOUNT)));
                transaction.setDescription(c.getString(c.getColumnIndex(KEY_TRANSACTION_DESCRIPTION)));
                transaction.setCategoryId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_CATEGORY_ID)));
                transaction.setFromAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_FROM_ACCOUNT_ID)));
                transaction.setToAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_TO_ACCOUNT_ID)));
                transaction.setTime(calendar);
                transaction.setFee(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_FEE)));
                transaction.setPayee(c.getString(c.getColumnIndex(KEY_TRANSACTION_PAYEE)));
                transaction.setEvent(getEvent(c.getInt(c.getColumnIndex(KEY_TRANSACTION_EVENT))));

                transactions.add(transaction);
            } while (c.moveToNext()) ;
        }

        leave(TAG, "categories = " + (categories != null ? categories.toString() : "") + "startDate = " + startDate.getTimeInMillis() + ", endDate = " + endDate, transactions.toString());

        return transactions;

    }

    /**
     * getting all TRANSACTION follow Account
     * */
    public List<Transaction> getTransactionsByAccount(int accountId) {
        enter(TAG, null);

        List<Transaction> transactions = new ArrayList<Transaction>();
        String selectQuery = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + KEY_TRANSACTION_FROM_ACCOUNT_ID + " = " + accountId + " OR " + KEY_TRANSACTION_TO_ACCOUNT_ID + " = " + accountId;

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                Transaction transaction = new Transaction();
                transaction.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                transaction.setTransactionType(c.getInt(c.getColumnIndex(KEY_TRANSACTION_TYPE)));
                transaction.setAmount(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_AMOUNT)));
                transaction.setDescription(c.getString(c.getColumnIndex(KEY_TRANSACTION_DESCRIPTION)));
                transaction.setCategoryId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_CATEGORY_ID)));
                transaction.setFromAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_FROM_ACCOUNT_ID)));
                transaction.setToAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_TO_ACCOUNT_ID)));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_TRANSACTION_TIME))));
                transaction.setTime(calendar);
                transaction.setFee(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_FEE)));
                transaction.setPayee(c.getString(c.getColumnIndex(KEY_TRANSACTION_PAYEE)));
                transaction.setEvent(getEvent(c.getInt(c.getColumnIndex(KEY_TRANSACTION_EVENT))));

                // adding to kinds list
                transactions.add(transaction);
            } while (c.moveToNext());
        }

        leave(TAG, null, transactions.toString());
        return transactions;
    }

    /**
     * getting all TRANSACTIONs
     * */
    public List<Transaction> getAllTransactions() {
        enter(TAG, null);

        List<Transaction> transactions = new ArrayList<Transaction>();
        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " ORDER BY " + KEY_TRANSACTION_TIME + " DESC";

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                Transaction transaction = new Transaction();
                transaction.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                transaction.setTransactionType(c.getInt(c.getColumnIndex(KEY_TRANSACTION_TYPE)));
                transaction.setAmount(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_AMOUNT)));
                transaction.setDescription(c.getString(c.getColumnIndex(KEY_TRANSACTION_DESCRIPTION)));
                transaction.setCategoryId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_CATEGORY_ID)));
                transaction.setFromAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_FROM_ACCOUNT_ID)));
                transaction.setToAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_TO_ACCOUNT_ID)));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_TRANSACTION_TIME))));
                transaction.setTime(calendar);
                transaction.setFee(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_FEE)));
                transaction.setPayee(c.getString(c.getColumnIndex(KEY_TRANSACTION_PAYEE)));
                transaction.setEvent(getEvent(c.getInt(c.getColumnIndex(KEY_TRANSACTION_EVENT))));

                transactions.add(transaction);
            } while (c.moveToNext());
        }

        leave(TAG, null, transactions.toString());
        return transactions;
    }

    /**
     * Getting TRANSACTION count
     */
    public int getTransactionCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TRANSACTION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    /**
     * Updating a TRANSACTION
     */
    public int updateTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRANSACTION_TYPE, transaction.getTransactionType());
        values.put(KEY_TRANSACTION_AMOUNT, transaction.getAmount());
        values.put(KEY_TRANSACTION_DESCRIPTION, transaction.getDescription());
        values.put(KEY_TRANSACTION_CATEGORY_ID, transaction.getCategoryId());
        values.put(KEY_TRANSACTION_FROM_ACCOUNT_ID, transaction.getFromAccountId());
        values.put(KEY_TRANSACTION_TO_ACCOUNT_ID, transaction.getToAccountId());
        values.put(KEY_TRANSACTION_TIME, getStringDateTime(transaction.getTime().getTime()));
        values.put(KEY_TRANSACTION_FEE, transaction.getFee());
        values.put(KEY_TRANSACTION_PAYEE, transaction.getPayee());
        values.put(KEY_TRANSACTION_EVENT, transaction.getEvent() != null ? transaction.getEvent().getId() : 0);

        // updating row
        return db.update(TABLE_TRANSACTION, values, KEY_ID + " = ?", new String[] { String.valueOf(transaction.getId()) });
    }

    /**
     * Deleting a TRANSACTION
     */
    public void deleteTransaction(long transaction_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTION, KEY_ID + " = ?", new String[] { String.valueOf(transaction_id) });
    }

    /**
     * Deleting all TRANSACTION related with account
     */
    public void deleteAllTransaction(long accountId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTION, KEY_TRANSACTION_FROM_ACCOUNT_ID + " = ? OR " + KEY_TRANSACTION_TO_ACCOUNT_ID + " = ?", new String[] { String.valueOf(accountId), String.valueOf(accountId) });

    }

    /**
     * Get list of PAYEE from list Transaction
     */
    public List<String> getPayees(String contain) {
        List<String> payees = new ArrayList<String>();
        String selectQuery = "SELECT DISTINCT(" + KEY_TRANSACTION_PAYEE + ") FROM " + TABLE_TRANSACTION;
        if(!contain.equals("")) {
            selectQuery += " WHERE " + KEY_TRANSACTION_PAYEE + " LIKE '%" + contain + "%'";
        }

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                String payee = c.getString(c.getColumnIndex(KEY_TRANSACTION_PAYEE)).trim();
                if(!payee.equals("")) {
                    payees.add(c.getString(c.getColumnIndex(KEY_TRANSACTION_PAYEE)));
                }
            } while (c.moveToNext());
        }

        return payees;
    }

    /**
     * Get List of Transaction follow Event
     * @param eventId
     * @return
     */
    public List<Transaction> getTransactionsByEvent(int eventId) {
        enter(TAG, null);

        List<Transaction> transactions = new ArrayList<Transaction>();
        String selectQuery = "SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + KEY_TRANSACTION_EVENT + " = " + eventId + " ORDER BY " + KEY_TRANSACTION_TIME + " DESC";

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                Transaction transaction = new Transaction();
                transaction.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                transaction.setTransactionType(c.getInt(c.getColumnIndex(KEY_TRANSACTION_TYPE)));
                transaction.setAmount(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_AMOUNT)));
                transaction.setDescription(c.getString(c.getColumnIndex(KEY_TRANSACTION_DESCRIPTION)));
                transaction.setCategoryId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_CATEGORY_ID)));
                transaction.setFromAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_FROM_ACCOUNT_ID)));
                transaction.setToAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_TO_ACCOUNT_ID)));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_TRANSACTION_TIME))));
                transaction.setTime(calendar);
                transaction.setFee(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_FEE)));
                transaction.setPayee(c.getString(c.getColumnIndex(KEY_TRANSACTION_PAYEE)));
                transaction.setEvent(getEvent(c.getInt(c.getColumnIndex(KEY_TRANSACTION_EVENT))));

                // adding to transaction list
                transactions.add(transaction);
            } while (c.moveToNext());
        }

        leave(TAG, null, transactions.toString());
        return transactions;
    }

    //endregion

    // ------------------------ BUDGET table methods ----------------//
    //region Table BUDGET

    public List<Budget> getAllBudgets() {
        enter(TAG, null);

        List<Budget> budgets = new ArrayList<Budget>();
        String selectQuery = "SELECT  * FROM " + TABLE_BUDGET;

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Budget budget = new Budget();
                budget.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                budget.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                budget.setAmount(c.getDouble(c.getColumnIndex(KEY_BUDGET_AMOUNT)));

                String[] items = c.getString(c.getColumnIndex(KEY_BUDGET_CATEGORY)).replaceAll("\\[", "").replaceAll("\\]", "").split(",");

                int[] arCategories = new int[items.length];

                for (int i = 0; i < items.length; i++) {
                    try {
                        arCategories[i] = Integer.parseInt(items[i].trim());
                    } catch (NumberFormatException nfe) {};
                }

                budget.setCategories(arCategories);
                budget.setRepeatType(c.getInt(c.getColumnIndex(KEY_BUDGET_REPEAT_TYPE)));

                Calendar startDate = Calendar.getInstance();
                startDate.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_START_DATE))));
                budget.setStartDate(startDate);
                Calendar endDate = Calendar.getInstance();
                endDate.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_END_DATE))));
                budget.setEndDate(endDate);

                budget.setIsIncremental(c.getInt(c.getColumnIndex(KEY_BUDGET_INCREMENTAL)) == 1 ? true : false);

                // adding to kinds list
                budgets.add(budget);
            } while (c.moveToNext());
        }

        leave(TAG, null, "Budgets = " + budgets.toString());

        return budgets;
    }

    /*
     * Creating a BUDGET
     */
    public long createBudget(Budget budget) {
        enter(TAG, budget.toString());

        String categories = "[" + budget.getCategories()[0];

        for(int i = 1 ; i < budget.getCategories().length; i++) {
            categories += ", " + budget.getCategories()[i];
        }
        categories += "]";

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, budget.getName());
        values.put(KEY_BUDGET_AMOUNT, budget.getAmount());
        values.put(KEY_BUDGET_CATEGORY, categories);
        values.put(KEY_BUDGET_REPEAT_TYPE, budget.getRepeatType());
        values.put(KEY_START_DATE, getStringDateTime(budget.getStartDate().getTime()));
        values.put(KEY_END_DATE, getStringDateTime(budget.getEndDate().getTime()));
        values.put(KEY_BUDGET_INCREMENTAL, budget.isIncremental() ? 1 : 0);

        try {
            // insert row
            long budget_id = db.insert(TABLE_BUDGET, null, values);

            leave(TAG, "budget = " + budget.toString(), "budget_id = " + budget_id);
            return budget_id;

        } catch (android.database.SQLException e) {
            e.printStackTrace();
            leave(TAG, "budget = " + budget.toString(), "budget_id = -1");
            return -1;
        }
    }

    /**
     * Update budget
     *
     * @param budget
     * @return
     */
    public int updateBudget(Budget budget) {
        enter(TAG, "budget = " + budget.toString());
        String categories = "[" + budget.getCategories()[0];

        for(int i = 1 ; i < budget.getCategories().length; i++) {
            categories += ", " + budget.getCategories()[i];
        }
        categories += "]";


        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, budget.getName());
        values.put(KEY_BUDGET_AMOUNT, budget.getAmount());
        values.put(KEY_BUDGET_CATEGORY, categories);
        values.put(KEY_BUDGET_REPEAT_TYPE, budget.getRepeatType());
        values.put(KEY_START_DATE, getStringDateTime(budget.getStartDate().getTime()));
        values.put(KEY_END_DATE, getStringDateTime(budget.getEndDate().getTime()));
        values.put(KEY_BUDGET_INCREMENTAL, budget.isIncremental() ? 1 : 0);

        // updating row
        int result = db.update(TABLE_BUDGET, values, KEY_ID + " = ?", new String[]{String.valueOf(budget.getId())});

        leave(TAG, "budget = " + budget.toString(), "Result = " + result);
        return result;
    }

    /**
     * Delete Budget
     * @param budget_id
     */
    public void deleteBudget(long budget_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BUDGET, KEY_ID + " = ?", new String[]{String.valueOf(budget_id)});
    }

    /**
     * Get Budget
     * @param budget_id
     * @return
     */
    public Budget getBudget(long budget_id) {
        enter(TAG, "budget_id = " + budget_id);

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_BUDGET + " WHERE " + KEY_ID + " = " + budget_id;

        trace(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            c.moveToFirst();

            Budget budget = new Budget();
            budget.setId(c.getInt((c.getColumnIndex(KEY_ID))));
            budget.setName((c.getString(c.getColumnIndex(KEY_NAME))));
            budget.setAmount(c.getDouble(c.getColumnIndex(KEY_BUDGET_AMOUNT)));

            String[] items = c.getString(c.getColumnIndex(KEY_BUDGET_CATEGORY)).replaceAll("\\[", "").replaceAll("\\]", "").split(",");

            int[] arCategories = new int[items.length];

            for (int i = 0; i < items.length; i++) {
                try {
                    arCategories[i] = Integer.parseInt(items[i].trim());
                } catch (NumberFormatException nfe) {};
            }

            budget.setCategories(arCategories);
            budget.setRepeatType(c.getInt(c.getColumnIndex(KEY_BUDGET_REPEAT_TYPE)));

            Calendar startDate = Calendar.getInstance();
            startDate.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_START_DATE))));
            budget.setStartDate(startDate);
            Calendar endDate = Calendar.getInstance();
            endDate.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_END_DATE))));
            budget.setEndDate(endDate);

            budget.setIsIncremental(c.getInt(c.getColumnIndex(KEY_BUDGET_INCREMENTAL)) == 1 ? true : false);

            leave(TAG, "budget_id = " + budget_id, budget.toString());

            return budget;
        }

        leave(TAG, "budget_id = " + budget_id, null);

        return null;

    }
    //endregion

    // ------------------------ EVENT table methods ----------------//
    //region Table EVENT
    public List<Event> getAllEvents() {
        enter(TAG, null);

        List<Event> events = new ArrayList<Event>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENT;

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Event event = new Event();
                event.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                event.setName((c.getString(c.getColumnIndex(KEY_NAME))));

                Calendar startDate = Calendar.getInstance();
                startDate.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_START_DATE))));
                event.setStartDate(startDate);
                Calendar endDate = Calendar.getInstance();
                endDate.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_END_DATE))));
                event.setEndDate(endDate);

                // adding to kinds list
                events.add(event);
            } while (c.moveToNext());
        }

        leave(TAG, null, "Events = " + events.toString());

        return events;
    }

    public List<Event> getRunningEvents() {
        enter(TAG, null);

        List<Event> events = new ArrayList<Event>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENT;

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndex(KEY_END_DATE)).equals("")) {
                    Event event = new Event();
                    event.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                    event.setName((c.getString(c.getColumnIndex(KEY_NAME))));

                    Calendar startDate = Calendar.getInstance();
                    startDate.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_START_DATE))));
                    event.setStartDate(startDate);
                    event.setEndDate(null);

                    // adding to kinds list
                    events.add(event);
                }
            } while (c.moveToNext());
        }

        leave(TAG, null, "Events = " + events.toString());

        return events;
    }

    public List<Event> getFinishedEvents() {
        enter(TAG, null);

        List<Event> events = new ArrayList<Event>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENT;

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                if (!c.getString(c.getColumnIndex(KEY_END_DATE)).equals("")) {
                    Event event = new Event();
                    event.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                    event.setName((c.getString(c.getColumnIndex(KEY_NAME))));

                    Calendar startDate = Calendar.getInstance();
                    startDate.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_START_DATE))));
                    event.setStartDate(startDate);
                    Calendar endDate = Calendar.getInstance();
                    endDate.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_END_DATE))));
                    event.setEndDate(endDate);

                    // adding to kinds list
                    events.add(event);
                }
            } while (c.moveToNext());
        }

        leave(TAG, null, "Events = " + events.toString());

        return events;
    }

    /**
     * Get list of EVENT
     */
    public List<String> getEvents(String contain) {
        List<String> events = new ArrayList<String>();
        String selectQuery = "SELECT " + KEY_NAME  + " FROM " + TABLE_EVENT;
        if(!contain.equals("")) {
            selectQuery += " WHERE " + KEY_NAME + " LIKE '%" + contain + "%'";
        }

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                String event = c.getString(c.getColumnIndex(KEY_NAME)).trim();
                if(!event.equals("")) {
                    events.add(c.getString(c.getColumnIndex(KEY_NAME)));
                }
            } while (c.moveToNext());
        }

        return events;
    }

    public Event getEventByName(String name) {
        enter(TAG, "name = " + name);
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_EVENT + " WHERE " + KEY_NAME + " = '" + name + "'";

        trace(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            c.moveToFirst();

            Event event = new Event();
            event.setId(c.getInt((c.getColumnIndex(KEY_ID))));
            event.setName((c.getString(c.getColumnIndex(KEY_NAME))));

            Calendar startDate = Calendar.getInstance();
            startDate.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_START_DATE))));
            event.setStartDate(startDate);
            if(!c.getString(c.getColumnIndex(KEY_END_DATE)).equals("")) {
                Calendar endDate = Calendar.getInstance();
                endDate.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_END_DATE))));
                event.setEndDate(endDate);
            } else {
                event.setEndDate(null);
            }

            leave(TAG, "name = " + name, event.toString());

            return event;
        }

        leave(TAG, "name = " + name, null);
        return null;
    }

    /**
     * Creating a EVENT
     */
    public long createEvent(Event event) {
        enter(TAG, event.toString());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, event.getName());
        values.put(KEY_START_DATE, getStringDateTime(event.getStartDate().getTime()));
        values.put(KEY_END_DATE, "");

        try {
            // insert row
            long event_id = db.insert(TABLE_EVENT, null, values);

            leave(TAG, "event = " + event.toString(), "event_id = " + event_id);
            return event_id;

        } catch (android.database.SQLException e) {
            e.printStackTrace();
            leave(TAG, "event = " + event.toString(), "event_id = -1");
            return -1;
        }
    }

    /**
     * Update Event
     *
     * @param event
     * @return
     */
    public int updateEvent(Event event) {
        enter(TAG, "event = " + event.toString());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, event.getName());
        values.put(KEY_START_DATE, getStringDateTime(event.getStartDate().getTime()));
        values.put(KEY_END_DATE, event.getEndDate() != null ? getStringDateTime(event.getEndDate().getTime()) : "");

        // updating row
        int result = db.update(TABLE_EVENT, values, KEY_ID + " = ?", new String[]{String.valueOf(event.getId())});

        leave(TAG, "event = " + event.toString(), "Result = " + result);
        return result;
    }

    /**
     * Delete Event
     * @param event_id
     */
    public void deleteEvent(long event_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String updateTransaction = "UPDATE " + TABLE_TRANSACTION + " SET " + KEY_TRANSACTION_EVENT + " = 0 WHERE " + KEY_TRANSACTION_EVENT + " = " + event_id;

        db.execSQL(updateTransaction);

        db.delete(TABLE_EVENT, KEY_ID + " = ?", new String[]{String.valueOf(event_id)});
    }

    /**
     * Get Event
     * @param event_id
     * @return
     */
    public Event getEvent(long event_id) {
        enter(TAG, "event_id = " + event_id);

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_EVENT + " WHERE " + KEY_ID + " = " + event_id;

        trace(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            c.moveToFirst();

            Event event = new Event();
            event.setId(c.getInt((c.getColumnIndex(KEY_ID))));
            event.setName((c.getString(c.getColumnIndex(KEY_NAME))));

            Calendar startDate = Calendar.getInstance();
            startDate.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_START_DATE))));
            event.setStartDate(startDate);
            if(!c.getString(c.getColumnIndex(KEY_END_DATE)).equals("")) {
                Calendar endDate = Calendar.getInstance();
                endDate.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_END_DATE))));
                event.setEndDate(endDate);
            } else {
                event.setEndDate(null);
            }

            leave(TAG, "event_id = " + event_id, event.toString());

            return event;
        }

        leave(TAG, "event_id = " + event_id, null);

        return null;

    }
    //endregion

    // ------------------------ DEBT table methods ----------------//
    //region Table DEBT
    public long createDebt(Debt debt) {
        enter(TAG, "debt = " + debt.toString());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DEBT_CATEGORY, debt.getCategoryId());
        values.put(KEY_DEBT_TRANSACTION, debt.getTransactionId());
        values.put(KEY_DEBT_AMOUNT, debt.getAmount());
        values.put(KEY_DEBT_PEOPLE, debt.getPeople());

        try {
            // insert row
            long debt_id = db.insert(TABLE_DEBTS, null, values);

            leave(TAG, "debt = " + debt.toString(), "debt_id = " + debt_id);
            return debt_id;

        } catch (android.database.SQLException e) {
            e.printStackTrace();
            leave(TAG, "debt = " + debt.toString(), "debt_id = -1");
            return -1;
        }
    }

    public List<Debt> getAllDebts() {
        enter(TAG, null);

        List<Debt> debts = new ArrayList<Debt>();
        String selectQuery = "SELECT  * FROM " + TABLE_DEBTS;

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Debt debt = new Debt();
                debt.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                debt.setCategoryId(c.getInt(c.getColumnIndex(KEY_DEBT_CATEGORY)));
                debt.setTransactionId(c.getInt(c.getColumnIndex(KEY_DEBT_TRANSACTION)));
                debt.setAmount(c.getDouble(c.getColumnIndex(KEY_DEBT_AMOUNT)));
                debt.setPeople(c.getString(c.getColumnIndex(KEY_DEBT_PEOPLE)));

                debts.add(debt);
            } while (c.moveToNext());
        }

        leave(TAG, null, "debts = " + debts.toString());

        return debts;
    }

    public List<Debt> getAllDebtByPeople(String people) {
        enter(TAG, null);

        List<Debt> debts = new ArrayList<Debt>();
        String selectQuery = "SELECT  * FROM " + TABLE_DEBTS + " WHERE " + KEY_DEBT_PEOPLE + " LIKE '" + people + "'";

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Debt debt = new Debt();
                debt.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                debt.setCategoryId(c.getInt(c.getColumnIndex(KEY_DEBT_CATEGORY)));
                debt.setTransactionId(c.getInt(c.getColumnIndex(KEY_DEBT_TRANSACTION)));
                debt.setAmount(c.getDouble(c.getColumnIndex(KEY_DEBT_AMOUNT)));
                debt.setPeople(c.getString(c.getColumnIndex(KEY_DEBT_PEOPLE)));

                debts.add(debt);
            } while (c.moveToNext());
        }

        leave(TAG, null, "debts = " + debts.toString());

        return debts;
    }

    public List<Debt> getAllLent() {
        enter(TAG, null);

        List<Debt> debts = new ArrayList<Debt>();
        String selectQuery = "SELECT  * FROM " + TABLE_DEBTS;

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                Category category = getCategory(c.getInt(c.getColumnIndex(KEY_DEBT_CATEGORY)));


                Debt debt = new Debt();
                debt.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                debt.setCategoryId(c.getInt(c.getColumnIndex(KEY_DEBT_CATEGORY)));
                debt.setTransactionId(c.getInt(c.getColumnIndex(KEY_DEBT_TRANSACTION)));
                debt.setAmount(c.getDouble(c.getColumnIndex(KEY_BUDGET_AMOUNT)));
                debt.setPeople(c.getString(c.getColumnIndex(KEY_DEBT_PEOPLE)));

                debts.add(debt);
            } while (c.moveToNext());
        }

        leave(TAG, null, "debts = " + debts.toString());

        return debts;
    }

    public Debt getDebtByTransactionId(int transactionId) {
        enter(TAG, "transactionId = " + transactionId);

        String selectQuery = "SELECT  * FROM " + TABLE_DEBTS + " WHERE " + KEY_DEBT_TRANSACTION + " = " + transactionId;

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c != null && c.moveToFirst()) {
            Debt debt = new Debt();
            debt.setId(c.getInt((c.getColumnIndex(KEY_ID))));
            debt.setCategoryId(c.getInt(c.getColumnIndex(KEY_DEBT_CATEGORY)));
            debt.setTransactionId(c.getInt(c.getColumnIndex(KEY_DEBT_TRANSACTION)));
            debt.setAmount(c.getDouble(c.getColumnIndex(KEY_BUDGET_AMOUNT)));
            debt.setPeople(c.getString(c.getColumnIndex(KEY_DEBT_PEOPLE)));

            leave(TAG, null, debt.toString());
            return debt;

        }

        leave(TAG, "transactionId = " + transactionId, null);

        return null;
    }

    /**
     * Updating a Debt
     */
    public int updateDebt(Debt debt) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DEBT_CATEGORY, debt.getCategoryId());
        values.put(KEY_DEBT_TRANSACTION, debt.getTransactionId());
        values.put(KEY_DEBT_AMOUNT, debt.getAmount());
        values.put(KEY_DEBT_PEOPLE, debt.getPeople());

        // updating row
        return db.update(TABLE_DEBTS, values, KEY_ID + " = ?", new String[] { String.valueOf(debt.getId()) });
    }

    /**
     * Delete a Debt
     */
    public void deleteDebt(int debtId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DEBTS, KEY_ID + " = ?", new String[]{String.valueOf(debtId)});
    }

    /**
     * Get list of Lender & Borrower from list Debt
     */
    public List<String> getPeoples(String contain) {
        List<String> peoples = new ArrayList<String>();
        String selectQuery = "SELECT DISTINCT(" + KEY_DEBT_PEOPLE + ") FROM " + TABLE_DEBTS;
        if(!contain.equals("")) {
            selectQuery += " WHERE " + KEY_DEBT_PEOPLE + " LIKE '%" + contain + "%'";
        }

        trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                String people = c.getString(c.getColumnIndex(KEY_DEBT_PEOPLE)).trim();
                if(!people.equals("")) {
                    peoples.add(c.getString(c.getColumnIndex(KEY_DEBT_PEOPLE)));
                }
            } while (c.moveToNext());
        }

        return peoples;
    }
    //endregion

    //region UTILS method
    /**
     * get datetime
     * */
    private String getStringDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    /**
     * get datetime
     * */
    private Date getDateTime(String strDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            return dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void insertDefaultCategories() {
        /* ParentID, Name, Expense, Borrow */
        createCategory(0, "Lương",                  false,  EnumDebt.NONE);
        createCategory(0, "Thưởng",                 false,  EnumDebt.NONE);
        createCategory(0, "Được cho/tặng",          false,  EnumDebt.NONE);
        createCategory(0, "Tiền lãi",               false,  EnumDebt.NONE);
        createCategory(0, "Khác",                   false,  EnumDebt.NONE);
        createCategory(0, "Lãi tiết kiệm",          false,  EnumDebt.NONE);
        createCategory(0, "Đi vay",                 false,  EnumDebt.MORE);
        createCategory(0, "Thu nợ",                 false,  EnumDebt.LESS);
        createCategory(0, "Dịch vụ sinh hoạt",      true,   EnumDebt.NONE);
        createCategory(0, "Hưởng thụ",              true,   EnumDebt.NONE);
        createCategory(0, "Đi lại",                 true,   EnumDebt.NONE);
        createCategory(0, "Phí phát sinh",          true,   EnumDebt.NONE);
        createCategory(0, "Ăn uống",                true,   EnumDebt.NONE);
        createCategory(0, "Hiếu hỉ",                true,   EnumDebt.NONE);
        createCategory(0, "Tình cảm",               true,   EnumDebt.NONE);
        createCategory(0, "Sức khỏe",               true,   EnumDebt.NONE);
        createCategory(0, "Nhà cửa",                true,   EnumDebt.NONE);
        createCategory(0, "Trang phục",             true,   EnumDebt.NONE);
        createCategory(0, "Phát triển bản thân",    true,   EnumDebt.NONE);
        createCategory(0, "Chưa nhớ",               true,   EnumDebt.NONE);
        createCategory(0, "Khác",                   true,   EnumDebt.NONE);
        createCategory(0, "Cho vay",                true,   EnumDebt.MORE);
        createCategory(0, "Trả nợ",                 true,   EnumDebt.LESS);
    }
    //endregion

}
