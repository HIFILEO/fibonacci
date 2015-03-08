/*
Property of Daniel Leonardis 2015.
Free to distribute, use, or modify under open source license
*/
package com.appsbyleo.fibonacci.adapters;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.appsbyleo.fibonacci.R;
import com.appsbyleo.fibonacci.model.FibonacciNumber;

import java.math.BigInteger;
import java.text.DecimalFormat;

/**
 * Cursor adapter for the listview
 */
public class FibonacciAdapter extends CursorAdapter {
    public static final String TAG = FibonacciAdapter.class.getSimpleName();

    //In order to insert commas into BigInteger, do this
    private String regex = "(\\d)(?=(\\d{3})+$)";

    public FibonacciAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Get the cursor loader that this adapter users to create cursor and draw data in bind view.
     * @param context
     * @return
     */
    public static CursorLoader getCursorLoader(Context context) {

        //
        //The 'what'
        //
        String[] projection = {FibonacciNumber.ID, FibonacciNumber.SEQUENCE_NUMBER, FibonacciNumber.FIBONACCI_NUMBER};

        //
        //The 'order'
        //
        String orderBy = FibonacciNumber.SEQUENCE_NUMBER + " ASC";

        //
        //Return created loader
        //
        return new CursorLoader(context,
                FibonacciNumber.CONTENT_URI,
                projection,
                null,
                null,
                orderBy);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view =  LayoutInflater.from(context).inflate(R.layout.main_cell, null);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //
        //Get the fibonacci number from cursor
        //
        int sequenceNumber = cursor.getInt(1);
        BigInteger fibonacciNumber = new BigInteger(cursor.getString(2));

        //
        //Fill in cell data
        //
        //sequence number
        TextView textView = (TextView) view.findViewById(R.id.SequenceTextView);
        textView.setText("#" + getCommaFormatedNumber(sequenceNumber));

        //fib number
        textView = (TextView) view.findViewById(R.id.FibonacciNumberTextView);
        textView.setText(fibonacciNumber.toString().replaceAll(regex, "$1,"));
    }

    /**
     * Convert integer to comma separated number (good for other locales as well).
     * @param number - the integer to convert
     * @return - comma separated String
     */
    private String getCommaFormatedNumber(long number) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(number);
    }
}
