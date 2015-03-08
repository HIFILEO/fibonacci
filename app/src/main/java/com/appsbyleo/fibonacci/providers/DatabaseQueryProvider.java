/*
Property of Daniel Leonardis 2015.
Free to distribute, use, or modify under open source license
*/
package com.appsbyleo.fibonacci.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.appsbyleo.fibonacci.model.FibonacciNumber;
import com.appsbyleo.fibonacci.model.helpers.DatabaseHelper;

/**
 * This provider's sole purpose is to provide cursors to CursorLoader when doing simple
 * queries on the database that are meant for lists.
 */
public class DatabaseQueryProvider extends ContentProvider {

    public static final String TAG = DatabaseQueryProvider.class.getSimpleName();
    private DatabaseHelper databaseHelper;

    // authority is the symbolic name of your provider. Must be universally unique
    public static final String AUTHORITY = "com.appsbyleo.fibonacci.providers.DatabaseQueryProvider";

    //Used to only allow queries on the following tables:
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, FibonacciNumber.FIBONACCI_NUMBER, 0);
    }

    @Override
    public boolean onCreate() {

        //create a brand new database connection specifically for this provider.
        databaseHelper = DatabaseHelper.create(getContext());

        return false;
    }

    /**
     * Perform the query using the provided parameters. Performs query on the local database
     * returning the cursor to be returned. If an internal error occurs during the query
     * process, return null. Otherwise will throw IllegalArgumentException when bad URI.
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        //Guard - make sure we match the allowed URIs
        if (uriMatcher.match(uri) == -1){
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor;
        cursor = databaseHelper.getWritableDatabase().query(
                getTableName(uri),  //table name
                projection,         //a list of columns to return
                selection,          //A filter - SQL WHERE
                selectionArgs,      //filter parameters  - SQL = X
                null,               //group by - SQL GROUP BY
                null,               //having - filter which row groups to include
                sortOrder);         //orderBy - SQL - ORDER BY

        return cursor;
    }

    /**
     * Return the MIME type corresponding to a content URI. Not supporting this so always NULL.
     * @param uri
     * @return - always null. Don't call.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Get table name from URI.
     * @param uri
     * @return
     */
    public static String getTableName(Uri uri){
        String value = uri.getPath();
        value = value.replace("/", "");//we need to remove '/'
        return value;
    }
}
