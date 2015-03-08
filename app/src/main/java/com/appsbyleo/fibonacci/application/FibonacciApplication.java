/*
Property of Daniel Leonardis 2015.
Free to distribute, use, or modify under open source license
*/
package com.appsbyleo.fibonacci.application;

import android.app.Application;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.appsbyleo.fibonacci.model.FibonacciNumber;
import com.appsbyleo.fibonacci.model.helpers.DatabaseHelper;
import com.j256.ormlite.table.TableUtils;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The application class for the app
 */
public class FibonacciApplication extends Application {

    public static final String TAG = FibonacciApplication.class.getSimpleName();
    private static FibonacciApplication instance;
    private static DatabaseHelper databaseHelper;
    private AtomicBoolean databaseReady = new AtomicBoolean();
    private AtomicBoolean calculatingFibonacci = new AtomicBoolean();
    private BackgroundThread backgroundThread = new BackgroundThread();

    public FibonacciApplication(){
        super();
        instance = this;

    }

    @Override
    public void onCreate() {
        super.onCreate();

        //create database helper
        databaseHelper = DatabaseHelper.create(FibonacciApplication.this);

        //open the database for writing.
        try {
            databaseHelper.getWritableDatabase();
            databaseReady.set(true);
        } catch (SQLiteException e) {
            Log.e(TAG, "Failure to open database!!!", e);
        }

        //create background thread
        new Thread(backgroundThread).start();
    }

    /**
     * Get an instance of the Application class
     * @return - application object or null
     */
    public static FibonacciApplication getInstance() {
        return instance;
    }

    /**
     * Tell background thread to create new fibonacci numbers
     */
    public void createNewFibonacciNumbers() {
        if (!calculatingFibonacci.get()) {
            calculatingFibonacci.set(true);
            backgroundThread.threadHandler.sendEmptyMessage(0);
        }
    }

    /***
     * Close Database and null out the handle.
     */
    public static void closeDatabase() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }

        databaseHelper = null;
    }

    /***
     * Get database helper for application. If null, we attempt to open.
     * @return db - database helper or null
     */
    public static DatabaseHelper getDatabase() {
        if (databaseHelper == null) {
            Log.w(TAG, "Database was null, create failed!");
            databaseHelper = DatabaseHelper.create(instance);
        }

        return databaseHelper;
    }

    /**
     * Clear database of all content. Done on BGT
     */
    public void clearDatabase() {
        backgroundThread.threadHandler.sendEmptyMessage(1);
    }

    /**
     * Background thread to calculate next 100 fibonacci numbers
     */
    private class BackgroundThread implements Runnable {

        public Handler threadHandler;

        @Override
        public void run() {
            Looper.prepare();

            threadHandler = new ThreadHandler();

            Looper.loop();
        }

        /**
         * Handler for the thread class. Creates the next 25 fibonacci numbers
         */
        private class ThreadHandler extends Handler {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        createFibonacci();
                        break;
                    case 1:
                        clearDatabase();
                        break;
                }
            }

            private void clearDatabase() {
                try {
                    TableUtils.clearTable(databaseHelper.getConnectionSource(), FibonacciNumber.class);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                //notify listeners
                FibonacciApplication.this.getContentResolver().notifyChange(FibonacciNumber.TABLE_URI, null);

                if (!threadHandler.hasMessages(0)) {
                    calculatingFibonacci.set(false);
                }
            }

            /**
             * Creates Fibonacci numbers and stores them into database
             */
            private void createFibonacci() {
                //
                //Determine if this is the first pass
                //
                long databaseSize = 0;

                try {
                    databaseSize = getDatabase().getFibonacciNumberDao().countOf();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                //
                //Calculate the fibonacci sequence for the next 25 numbers
                //
                List<FibonacciNumber> fibonacciNumberList = new ArrayList<>();

                if (databaseSize == 0) {
                    //First Pass
                    FibonacciNumber secondToLastFibNumber = new FibonacciNumber(0, new BigInteger("0"));
                    FibonacciNumber lastFibNumber = new FibonacciNumber(1, new BigInteger("1"));

                    fibonacciNumberList.add(secondToLastFibNumber);
                    fibonacciNumberList.add(lastFibNumber);

                    FibonacciNumber tempFibonacciNumber;
                    for (int i = 2; i < 25 ; i++) {
                        tempFibonacciNumber = new FibonacciNumber(i, calculateFibonacci(lastFibNumber.getFibonacciNumber(), secondToLastFibNumber.getFibonacciNumber()));
                        secondToLastFibNumber = lastFibNumber;
                        lastFibNumber = tempFibonacciNumber;
                        fibonacciNumberList.add(tempFibonacciNumber);
                    }
                } else {
                    //Every other pass
                    FibonacciNumber lastFibNumber;
                    FibonacciNumber secondToLastFibNumber;
                    int lastId;
                    try {
                        lastId = getDatabase().getFibonacciNumberDao().getLastFibonacciEntry();

                        lastFibNumber = getDatabase().getFibonacciNumberDao().queryForId(lastId);
                        secondToLastFibNumber = getDatabase().getFibonacciNumberDao().queryForId(lastId -1);

                    } catch (SQLException e) {
                        Log.e(TAG,"Unable to get last and second to last fibonacci number. Return");
                        return;
                    }

                    FibonacciNumber fibonacciNumber;
                    int start = lastFibNumber.getSequenceNumber() + 1;
                    int end = lastFibNumber.getSequenceNumber() + 25;
                    for (int i = start ; i <= end; i++) {
                        BigInteger newFibNumber = calculateFibonacci(lastFibNumber.getFibonacciNumber(), secondToLastFibNumber.getFibonacciNumber());
                        fibonacciNumber = new FibonacciNumber(i, newFibNumber);
                        secondToLastFibNumber = lastFibNumber;
                        lastFibNumber = fibonacciNumber;
                        fibonacciNumberList.add(fibonacciNumber);
                    }
                }

                //
                //Save to database
                //
                for (FibonacciNumber fibonacciNumber : fibonacciNumberList) {
                    try {
                        getDatabase().getFibonacciNumberDao().create(fibonacciNumber);

                        //For Debug
//                        Log.d(TAG, "Saving seq: " + fibonacciNumber.getSequenceNumber() + " with number " + fibonacciNumber.getFibonacciNumber());
//                        int lastSequence = getDatabase().getFibonacciNumberDao().getLastFibonacciEntry();
//                        FibonacciNumber testFib = getDatabase().getFibonacciNumberDao().queryForId(lastSequence);
//                        Log.d(TAG, "DB     seq: " + testFib.getSequenceNumber() + " with number " + testFib.getFibonacciNumber());
                    } catch (SQLException e) {
                        Log.e(TAG,"Create fibonacciNumber failed.");
                    }
                }

                //
                //Wrap up
                //
                //notify listeners
                FibonacciApplication.this.getContentResolver().notifyChange(FibonacciNumber.TABLE_URI, null);

                if (!threadHandler.hasMessages(0)) {
                    calculatingFibonacci.set(false);
                }
            }

            /**
             * Get the nxt fibonacci number.
             * Fibonacci Sequence. The Fibonacci Sequence is the series of numbers: 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, ... The next number is found by adding up the two numbers before it.
             * @param value1 - the first number in sequence
             * @param value2 - the second number in sequence
             * @return
             */
            private BigInteger calculateFibonacci(BigInteger value1, BigInteger value2) {
                BigInteger newValue = value1.add(value2);
                return newValue;
            }
        }
    }
}
