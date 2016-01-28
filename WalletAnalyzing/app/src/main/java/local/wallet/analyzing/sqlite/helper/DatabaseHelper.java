package local.wallet.analyzing.sqlite.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import local.wallet.analyzing.Utils.LogUtils;
import local.wallet.analyzing.model.Account;
import local.wallet.analyzing.model.Category;
import local.wallet.analyzing.model.Kind;
import local.wallet.analyzing.model.Transaction;

/**
 * Created by huynh.thanh.huan on 11/24/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String TAG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION       = 1;

    public static final int ERROR_DB_EXISTED        = -1;

    // Database Name
    private static final String DATABASE_NAME       = "WalletManaging.db";

    // Table Names
    private static final String TABLE_KIND          = "kinds";
    private static final String TABLE_CATEGORY      = "categories";
    private static final String TABLE_ACCOUNT       = "accounts";
    private static final String TABLE_TRANSACTION   = "transactions";

    // Common column names
    private static final String KEY_ID              = "id";
    private static final String KEY_NAME            = "name";

    // CATEGORY Table - column names
    private static final String KEY_CATEGORY_PARENT_ID          = "parent_id";
    private static final String KEY_CATEGORY_EXPENSE            = "expense";
    private static final String KEY_CATEGORY_BORROW             = "borrow";

    // Table ACCOUNT - column names
    private static final String KEY_ACCOUNT_TYPE_ID             = "type_id";
    private static final String KEY_ACCOUNT_CURRENCY            = "currency";
    private static final String KEY_ACCOUNT_INITIAL_BALANCE     = "initial_balance";
    private static final String KEY_ACCOUNT_DESCRIPTION         = "description";

    // TRANSACTION Table - column names
    private static final String KEY_TRANSACTION_AMOUNT          = "amount";
    private static final String KEY_TRANSACTION_DESCRIPTION     = "description";
    private static final String KEY_TRANSACTION_CATEGORY_ID     = "category_id";
    private static final String KEY_TRANSACTION_ACCOUNT_ID      = "account_id";
    private static final String KEY_TRANSACTION_TIME            = "time";
    private static final String KEY_TRANSACTION_PAYEE           = "payee";
    private static final String KEY_TRANSACTION_EVENT           = "event";

    // Table Create Statements
    // KIND table create statement
    private static final String CREATE_TABLE_KIND = "CREATE TABLE "
            + TABLE_KIND + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT" + ")";

    // CATEGORY table create statement
    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE "
            + TABLE_CATEGORY+ "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CATEGORY_PARENT_ID + " INTEGER DEFAULT 0, "
            + KEY_NAME + " TEXT, "
            + KEY_CATEGORY_EXPENSE + " INTEGER, "
            + KEY_CATEGORY_BORROW + " INTEGER" + ")";

    // ACCOUNT table create statement
    private static final String CREATE_TABLE_ACCOUNT = "CREATE TABLE "
            + TABLE_ACCOUNT + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT,"
            + KEY_ACCOUNT_TYPE_ID + " INTEGER,"
            + KEY_ACCOUNT_CURRENCY + " INTEGER, "
            + KEY_ACCOUNT_INITIAL_BALANCE + " DOUBLE,"
            + KEY_ACCOUNT_DESCRIPTION + " TEXT" + ")";

    // TRANSACTION table create statement
    private static final String CREATE_TABLE_TRANSACTION = "CREATE TABLE "
            + TABLE_TRANSACTION + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TRANSACTION_AMOUNT + " DOUBLE,"
            + KEY_TRANSACTION_DESCRIPTION + " TEXT,"
            + KEY_TRANSACTION_CATEGORY_ID + " INTEGER,"
            + KEY_TRANSACTION_ACCOUNT_ID + " INTEGER,"
            + KEY_TRANSACTION_TIME + " DATETIME,"
            + KEY_TRANSACTION_PAYEE + " TEXT,"
            + KEY_TRANSACTION_EVENT + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtils.logEnterFunction(TAG, "onCreate");
        // creating required tables
        LogUtils.trace(TAG, CREATE_TABLE_KIND);
        db.execSQL(CREATE_TABLE_KIND);

        LogUtils.trace(TAG, CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_CATEGORY);

        LogUtils.trace(TAG, CREATE_TABLE_ACCOUNT);
        db.execSQL(CREATE_TABLE_ACCOUNT);

        LogUtils.trace(TAG, CREATE_TABLE_TRANSACTION);
        db.execSQL(CREATE_TABLE_TRANSACTION);

        LogUtils.logLeaveFunction(TAG, "onCreate", null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtils.logEnterFunction(TAG, "onUpgrade");
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_KIND);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_ACCOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_TRANSACTION);

        // create new tables
        onCreate(db);
        LogUtils.logLeaveFunction(TAG, "onUpgrade", null);
    }

    // ------------------------ KIND table methods ----------------//

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

        LogUtils.trace(TAG, selectQuery);

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

        LogUtils.trace(TAG, selectQuery);

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

    // ------------------------ CATEGORY table methods ----------------//

    /*
     * Creating a CATEGORY
     */
    public long createCategory(int parentId, String name, boolean expense, boolean borrow) {
        LogUtils.logEnterFunction(TAG, "ParentId = " + parentId + ", Name = " + name + ", expense = " + expense + ", borrow = " + borrow );
        SQLiteDatabase db = this.getWritableDatabase();

        List<Category> categories = getAllCategories(expense, borrow);

        for(Category category : categories) {
            if(name.equals(category.getName())) {
                LogUtils.trace(TAG, "Category " + name + " is existed!");
                LogUtils.logLeaveFunction(TAG, "ParentId = " + parentId + ", Name = " + name + ", expense = " + expense + ", borrow = " + borrow, ERROR_DB_EXISTED + "");
                return ERROR_DB_EXISTED;
            }
        }

        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_PARENT_ID, parentId);
        values.put(KEY_NAME, name);
        values.put(KEY_CATEGORY_EXPENSE, expense ? 1 : 0);
        values.put(KEY_CATEGORY_BORROW, borrow ? 1 : 0);

        // insert row
        long category_id = db.insert(TABLE_CATEGORY, null, values);

        LogUtils.logLeaveFunction(TAG, "ParentId = " + parentId + ", Name = " + name + ", expense = " + expense + ", borrow = " + borrow, "New Category's id: " + category_id);
        return category_id;
    }

    /*
     * get single CATEGORY
     */
    public Category getCategory(long category_id) {
        LogUtils.logEnterFunction(TAG, "Id " + category_id);

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY + " WHERE " + KEY_ID + " = " + category_id;

        LogUtils.trace(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {

            Category category = new Category();
            category.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            category.setParentId(c.getInt(c.getColumnIndex(KEY_CATEGORY_PARENT_ID)));
            category.setName(c.getString(c.getColumnIndex(KEY_NAME)));
            category.setExpense(c.getInt(c.getColumnIndex(KEY_CATEGORY_EXPENSE)) == 1 ? true : false);
            category.setBorrow(c.getInt(c.getColumnIndex(KEY_CATEGORY_BORROW)) == 1 ? true : false);

            LogUtils.logLeaveFunction(TAG, "Id " + category_id, category.toString());

            return category;
        } else {
            LogUtils.trace(TAG, "");
            return null;
        }
    }

    public Category getCategory(String category_name) {
        LogUtils.logEnterFunction(TAG, "Name " + category_name);

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY + " WHERE " + KEY_NAME + " like '%" + category_name + "%'";

        LogUtils.trace(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
        }

        Category category = new Category();
        category.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        category.setParentId(c.getInt(c.getColumnIndex(KEY_CATEGORY_PARENT_ID)));
        category.setName(c.getString(c.getColumnIndex(KEY_NAME)));
        category.setExpense(c.getInt(c.getColumnIndex(KEY_CATEGORY_EXPENSE)) == 1 ? true : false);
        category.setBorrow(c.getInt(c.getColumnIndex(KEY_CATEGORY_BORROW)) == 1 ? true : false);

        LogUtils.logLeaveFunction(TAG, "Name " + category_name, category.toString());

        return category;
    }

    /**
     * Getting all CATEGORIES
     * */
    public List<Category> getAllCategories(boolean expense, boolean borrow) {
        LogUtils.logEnterFunction(TAG, null);

        List<Category> categorys = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY + " WHERE " + KEY_CATEGORY_EXPENSE + " = " + (expense ? 1 : 0) + " AND " + KEY_CATEGORY_BORROW + " = " + (borrow ? 1 : 0);

        LogUtils.trace(TAG, selectQuery);

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
                category.setBorrow(c.getInt(c.getColumnIndex(KEY_CATEGORY_BORROW)) == 1 ? true : false);

                // adding to kinds list
                categorys.add(category);
            } while (c.moveToNext());
        }

        LogUtils.logLeaveFunction(TAG, null, null);

        return categorys;
    }

    /**
     * Getting all PARENT_CATEGORIES
     * */
    public List<Category> getAllParentCategories(boolean expense, boolean borrow) {
        LogUtils.logEnterFunction(TAG, null);

        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY + " WHERE " + KEY_CATEGORY_EXPENSE + " = " + (expense ? 1 : 0) + " AND " + KEY_CATEGORY_BORROW + " = " + (borrow ? 1 : 0) + " AND " + KEY_CATEGORY_PARENT_ID + " = 0";

        LogUtils.trace(TAG, selectQuery);

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
                category.setBorrow(c.getInt(c.getColumnIndex(KEY_CATEGORY_BORROW)) == 1 ? true : false);

                // adding to kinds list
                categories.add(category);
            } while (c.moveToNext());
        }

        LogUtils.logLeaveFunction(TAG, null, "categories size = " + categories.size());

        return categories;
    }

    /**
     * Getting all CATEGORIES follow ParentID
     * */
    public List<Category> getCategoriesByParent(int parentId) {
        LogUtils.logEnterFunction(TAG, null);

        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY + " WHERE " + KEY_CATEGORY_PARENT_ID + " = " + parentId;

        LogUtils.trace(TAG, selectQuery);

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
                category.setBorrow(c.getInt(c.getColumnIndex(KEY_CATEGORY_BORROW)) == 1 ? true : false);

                // adding to kinds list
                categories.add(category);
            } while (c.moveToNext());
        }

        LogUtils.logLeaveFunction(TAG, null, null);

        return categories;
    }

    /*
     * Getting CATEGORY count
     */
    public int getCategoryCount() {
        LogUtils.logEnterFunction(TAG, null);

        String countQuery = "SELECT  * FROM " + TABLE_CATEGORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        LogUtils.logLeaveFunction(TAG, null, null);

        // return count
        return count;
    }

    /*
     * Updating a CATEGORY
     */
    public int updateCategory(Category category) {
        LogUtils.logEnterFunction(TAG, null);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, category.getId());
        values.put(KEY_CATEGORY_PARENT_ID, category.getParentId());
        values.put(KEY_NAME, category.getName());
        values.put(KEY_CATEGORY_EXPENSE, category.isExpense() ? 1 : 0);
        values.put(KEY_CATEGORY_BORROW, category.isBorrow() ? 1 : 0);

        LogUtils.logLeaveFunction(TAG, null, null);

        // updating row
        return db.update(TABLE_CATEGORY, values, KEY_ID + " = ?",
                new String[] { String.valueOf(category.getId()) });
    }

    /*
     * Deleting a CATEGORY
     */
    public void deleteCategory(long category_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORY, KEY_ID + " = ?",
                new String[] { String.valueOf(category_id) });
    }

    // ------------------------ ACCOUNT table methods ----------------//

    /*
     * Creating a ACCOUNT
     */
    public long createAccount(String account_name, int type_id, int currency_id, double initial_balance, String description) {
        LogUtils.logEnterFunction(TAG, "account_name = " + account_name + ", type_id = " + type_id + ", currency_id = " + currency_id + ", initial_balance = " + initial_balance + ", description = " + description);
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

        LogUtils.logLeaveFunction(TAG, "account_name = " + account_name + ", type_id = " + type_id + ", currency_id = " + currency_id + ", initial_balance = " + initial_balance + ", description = " + description, "Account id = " + account_id);

        return account_id;
    }

    /*
     * get single ACCOUNT
     */
    public Account getAccount(long account_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT + " WHERE " + KEY_ID + " = " + account_id;

        LogUtils.trace(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
        }

        Account account = new Account();
        account.setId(c.getInt((c.getColumnIndex(KEY_ID))));
        account.setName((c.getString(c.getColumnIndex(KEY_NAME))));
        account.setTypeId(c.getInt(c.getColumnIndex(KEY_ACCOUNT_TYPE_ID)));
        account.setCurrencyId(c.getInt(c.getColumnIndex(KEY_ACCOUNT_CURRENCY)));
        account.setRemain(c.getDouble(c.getColumnIndex(KEY_ACCOUNT_INITIAL_BALANCE)));
        account.setDescription(c.getString(c.getColumnIndex(KEY_ACCOUNT_DESCRIPTION)));

        return account;
    }

    /**
     * getting all ACCOUNTs
     * */
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<Account>();
        String selectQuery = "SELECT  * FROM " + TABLE_ACCOUNT;

        LogUtils.trace(TAG, selectQuery);

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
                account.setRemain(c.getDouble(c.getColumnIndex(KEY_ACCOUNT_INITIAL_BALANCE)));
                account.setDescription(c.getString(c.getColumnIndex(KEY_ACCOUNT_DESCRIPTION)));

                // adding to kinds list
                accounts.add(account);
            } while (c.moveToNext());
        }

        return accounts;
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
        values.put(KEY_ACCOUNT_INITIAL_BALANCE, account.getRemain());
        values.put(KEY_ACCOUNT_DESCRIPTION, account.getDescription());

        // updating row
        db.update(TABLE_ACCOUNT, values, KEY_ID + " = ?", new String[] { String.valueOf(account.getId()) });
    }

    /*
     * Deleting a ACCOUNT
     */
    public void deleteAccount(long account_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCOUNT, KEY_ID + " = ?", new String[] { String.valueOf(account_id) });
    }

    // ------------------------ TRANSACTION table methods ----------------//

    /*
     * Creating a TRANSACTION
     */
    public long createTransaction(Transaction transaction) {
        LogUtils.logEnterFunction(TAG, "transaction = " + transaction.toString());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRANSACTION_AMOUNT, transaction.getAmount());
        values.put(KEY_TRANSACTION_DESCRIPTION, transaction.getDescription());
        values.put(KEY_TRANSACTION_CATEGORY_ID, transaction.getCategoryId());
        values.put(KEY_TRANSACTION_ACCOUNT_ID, transaction.getAccountId());
        values.put(KEY_TRANSACTION_TIME, getDateTime(transaction.getTime().getTime()));
        values.put(KEY_TRANSACTION_PAYEE, transaction.getPayee());
        values.put(KEY_TRANSACTION_EVENT, transaction.getEvent());

        // insert row
        long transaction_id = db.insert(TABLE_TRANSACTION, null, values);

        LogUtils.logLeaveFunction(TAG, "transaction = " + transaction.toString(), "transaction_id = " + transaction_id);

        return transaction_id;
    }

    /*
     * get single TRANSACTION
     */
    public Transaction getTransaction(long transaction_id) {
        LogUtils.logEnterFunction(TAG, "transaction_id = " + transaction_id);
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION + " WHERE " + KEY_ID + " = " + transaction_id;

        LogUtils.trace(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            c.moveToFirst();

            Transaction transaction = new Transaction();
            transaction.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            transaction.setAmount(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_AMOUNT)));
            transaction.setCategoryId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_CATEGORY_ID)));
            transaction.setAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_ACCOUNT_ID)));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_TRANSACTION_TIME))));
            transaction.setTime(calendar);
            transaction.setPayee(c.getString(c.getColumnIndex(KEY_TRANSACTION_PAYEE)));
            transaction.setEvent(c.getString(c.getColumnIndex(KEY_TRANSACTION_EVENT)));

            LogUtils.logLeaveFunction(TAG, "transaction_id = " + transaction_id, transaction.toString());

            return transaction;
        }

        LogUtils.logLeaveFunction(TAG, "transaction_id = " + transaction_id, null);

        return null;

    }

    /**
     * getting all TRANSACTIONs
     * */
    public List<Transaction> getAllTransactions() {
        LogUtils.logEnterFunction(TAG, null);
        List<Transaction> transactions = new ArrayList<Transaction>();
        String selectQuery = "SELECT  * FROM " + TABLE_TRANSACTION;

        LogUtils.trace(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                Transaction transaction = new Transaction();
                transaction.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                transaction.setAmount(c.getDouble(c.getColumnIndex(KEY_TRANSACTION_AMOUNT)));
                transaction.setCategoryId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_CATEGORY_ID)));
                transaction.setAccountId(c.getInt(c.getColumnIndex(KEY_TRANSACTION_ACCOUNT_ID)));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(getDateTime(c.getString(c.getColumnIndex(KEY_TRANSACTION_TIME))));
                transaction.setTime(calendar);
                transaction.setPayee(c.getString(c.getColumnIndex(KEY_TRANSACTION_PAYEE)));
                transaction.setEvent(c.getString(c.getColumnIndex(KEY_TRANSACTION_EVENT)));

                // adding to kinds list
                transactions.add(transaction);
            } while (c.moveToNext());
        }

        LogUtils.logLeaveFunction(TAG, null, transactions.toString());
        return transactions;
    }

    /*
     * getting TRANSACTION count
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

    /*
     * Updating a TRANSACTION
     */
    public int updateTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRANSACTION_AMOUNT, transaction.getAmount());
        values.put(KEY_TRANSACTION_DESCRIPTION, transaction.getDescription());
        values.put(KEY_TRANSACTION_CATEGORY_ID, transaction.getCategoryId());
        values.put(KEY_TRANSACTION_ACCOUNT_ID, transaction.getAccountId());
        values.put(KEY_TRANSACTION_TIME, getDateTime(transaction.getTime().getTime()));
        values.put(KEY_TRANSACTION_PAYEE, transaction.getPayee());
        values.put(KEY_TRANSACTION_EVENT, transaction.getEvent());

        // updating row
        return db.update(TABLE_TRANSACTION, values, KEY_ID + " = ?", new String[] { String.valueOf(transaction.getId()) });
    }

    /*
     * Deleting a TRANSACTION
     */
    public void deleteTransaction(long transaction_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTION, KEY_ID + " = ?",
                new String[] { String.valueOf(transaction_id) });
    }

    /**
     * get datetime
     * */
    private String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    /**
     * get datetime
     * */
    private java.util.Date getDateTime(String strDate) {
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
        createCategory(0, "Lương",                  false, false);
        createCategory(0, "Thưởng",                 false, false);
        createCategory(0, "Được cho/tặng",          false, false);
        createCategory(0, "Tiền lãi",               false, false);
        createCategory(0, "Khác",                   false, false);
        createCategory(0, "Lãi tiết kiệm",          false, false);
        createCategory(0, "Đi vay",                 false, true);
        createCategory(0, "Thu nợ",                 false, true);
        createCategory(0, "Dịch vụ sinh hoạt",      true, false);
        createCategory(0, "Hưởng thụ",              true, false);
        createCategory(0, "Đi lại",                 true, false);
        createCategory(0, "Phí phát sinh",          true, false);
        createCategory(0, "Ăn uống",                true, false);
        createCategory(0, "Hiếu hỉ",                true, false);
        createCategory(0, "Tình cảm",               true, false);
        createCategory(0, "Sức khỏe",               true, false);
        createCategory(0, "Nhà cửa",                true, false);
        createCategory(0, "Trang phục",             true, false);
        createCategory(0, "Phát triển bản thân",    true, false);
        createCategory(0, "Chưa nhớ",               true, false);
        createCategory(0, "Khác",                   true, false);
        createCategory(0, "Cho vay",                true, true);
        createCategory(0, "Trả nợ",                 true, true);
    }
}
