/*
Property of Daniel Leonardis 2015.
Free to distribute, use, or modify under open source license
*/
package com.appsbyleo.fibonacci.viewcontrollers.activities;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import com.appsbyleo.fibonacci.R;
import com.appsbyleo.fibonacci.adapters.FibonacciAdapter;
import com.appsbyleo.fibonacci.application.FibonacciApplication;
import com.appsbyleo.fibonacci.model.FibonacciNumber;
import com.appsbyleo.fibonacci.services.CopyDatabaseService;

/**
 * This is the main activity for this application. Just shows the results from database.
 */
public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = MainActivity.class.getSimpleName();
    private FibonacciAdapter fibonacciAdapter;
    private MyScrollListener myScrollListener = new MyScrollListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //
        //Drop in list footer
        //
        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FibonacciApplication.getInstance().createNewFibonacciNumbers();
            }
        });
        getListView().addFooterView(progressBar);
        getListView().setOnScrollListener(myScrollListener);

        //
        //Create the adapter
        //
        fibonacciAdapter = new FibonacciAdapter(this, null, 0);
        setListAdapter(fibonacciAdapter);

        //
        //Kick off the load manager
        //
        //Prepare the loader to run in the background and get the cursor. It'll manage all cursors for us.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return FibonacciAdapter.getCursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //register notification uri
        data.setNotificationUri(getContentResolver(), FibonacciNumber.TABLE_URI);

        // Swap the new cursor in.
        fibonacciAdapter.swapCursor(data);

        if (data.getCount() == 0) {
            FibonacciApplication.getInstance().createNewFibonacciNumbers();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        fibonacciAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){
            case R.id.action_clear_database:
                FibonacciApplication.getInstance().clearDatabase();
                myScrollListener.lastTotalItemCound = -1;
                return true;
            case R.id.action_copy_database:
                Intent intent= new Intent(MainActivity.this, CopyDatabaseService.class);
                startService(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyScrollListener implements AbsListView.OnScrollListener {

        public int lastTotalItemCound = -1;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            /*Do Nothing*/
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            //subtract 1 for footer
            if((firstVisibleItem + visibleItemCount) ==  (totalItemCount -1) && (totalItemCount -1) != lastTotalItemCound) {
                lastTotalItemCound = totalItemCount - 1;
                Log.i(TAG, "Bottom of listview");
                FibonacciApplication.getInstance().createNewFibonacciNumbers();
            }
        }
    }
}
