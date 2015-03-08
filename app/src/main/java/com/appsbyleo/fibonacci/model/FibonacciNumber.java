/*
Property of Daniel Leonardis 2015.
Free to distribute, use, or modify under open source license
*/
package com.appsbyleo.fibonacci.model;

import android.net.Uri;

import com.appsbyleo.fibonacci.application.FibonacciApplication;
import com.appsbyleo.fibonacci.model.daos.implementation.FibonacciNumberDaoImp;
import com.appsbyleo.fibonacci.providers.DatabaseQueryProvider;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigInteger;

/**
 * FibonacciNumber object. Represents what will be in database.
 */
@DatabaseTable(tableName = "FibonacciNumber", daoClass = FibonacciNumberDaoImp.class)
public class FibonacciNumber {

    public static final String ID = "_id";
    public static final String SEQUENCE_NUMBER = "SequenceNumber";
    public static final String FIBONACCI_NUMBER = "FibonacciNumber";
    public static final Uri TABLE_URI =  Uri.parse("sqlite://" + FibonacciApplication.getInstance().getBaseContext().getPackageName() + "/" + FIBONACCI_NUMBER);
    public static final Uri CONTENT_URI = Uri.parse("content://" + DatabaseQueryProvider.AUTHORITY + "/" + FIBONACCI_NUMBER);

    //Note - generate the IDs. We'll be deleting everything and inserting a new if updates are detected.
    @DatabaseField(columnName = ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = SEQUENCE_NUMBER, canBeNull = false)
    private int sequenceNumber;

    @DatabaseField(columnName = FIBONACCI_NUMBER, canBeNull = false)
    private BigInteger fibonacciNumber;

    /**
     * Constructor for ORMLITE only.
     */
    public FibonacciNumber() {
        //Do Nothing. Here for ORMLITE,
    }

    /**
     * Constructor
     * * @param id - the n entry
     * @param fibonacciNumber - the number
     */
    public FibonacciNumber(int sequenceNumber, BigInteger fibonacciNumber) {
        this.sequenceNumber = sequenceNumber;
        this.fibonacciNumber = fibonacciNumber;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public BigInteger getFibonacciNumber() {
        return fibonacciNumber;
    }
}
