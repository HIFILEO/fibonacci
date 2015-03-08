package com.appsbyleo.fibonacci.model.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.appsbyleo.fibonacci.model.FibonacciNumber;
import com.appsbyleo.fibonacci.model.daos.interfaces.FibonacciNumberDao;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * Database helper to create and update the local database for android.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    public static String TAG = DatabaseHelper.class.getSimpleName();
    public static final String DATABASE_NAME = "fibonacci.db";
    private static final int DATABASE_VERSION = 1;

    public static DatabaseHelper create(Context context) {
        return new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private DatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, FibonacciNumber.class);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Error creating table", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    /**
     * Convenience
     */
    public FibonacciNumberDao getFibonacciNumberDao() {
        try {
            return DaoManager.createDao(getConnectionSource(), FibonacciNumber.class);
        } catch (SQLException e) {
            Log.e(TAG, "Failure to get dao", e);
        }

        return null;
    }
}
