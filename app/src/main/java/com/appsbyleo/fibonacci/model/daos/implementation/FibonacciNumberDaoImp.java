/*
Property of Daniel Leonardis 2015.
Free to distribute, use, or modify under open source license
*/
package com.appsbyleo.fibonacci.model.daos.implementation;

import com.appsbyleo.fibonacci.model.FibonacciNumber;
import com.appsbyleo.fibonacci.model.daos.interfaces.FibonacciNumberDao;
import com.j256.ormlite.dao.BaseDaoImpl;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;


/**
 * The implementation of data access object
 */
public class FibonacciNumberDaoImp extends BaseDaoImpl<FibonacciNumber, Integer> implements FibonacciNumberDao {

    public FibonacciNumberDaoImp(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, FibonacciNumber.class);
    }

    /**
     * Get the last fibonacci number
     * @return - java.util.Date
     * @throws SQLException
     */
    public int getLastFibonacciEntry() throws  SQLException {

        //select max from fibonacci number table
        //String query = "SELECT MAX(" + FibonacciNumber.FIBONACCI_NUMBER + ") FROM " + FibonacciNumber.FIBONACCI_NUMBER;
        String query = "SELECT MAX(" + FibonacciNumber.ID + ") FROM " + FibonacciNumber.FIBONACCI_NUMBER;

        GenericRawResults<Object[]> rawResults = queryRaw(query,
        new DataType[] {DataType.INTEGER});

        Object[] object = rawResults.getFirstResult();
        return (int) object[0];
    }

    /**
     * Get the second to last fibonacci number
     * @return
     * @throws SQLException
     */
    public int getSecondToLastFibonacciNumber() throws  SQLException {

        //select max from fibonacci number table
        String query = "SELECT MAX(" + FibonacciNumber.FIBONACCI_NUMBER + ") FROM " + FibonacciNumber.FIBONACCI_NUMBER +
                " WHERE " + FibonacciNumber.FIBONACCI_NUMBER + " < (SELECT MAX(" + FibonacciNumber.FIBONACCI_NUMBER + ") FROM " + FibonacciNumber.FIBONACCI_NUMBER + ")";

        GenericRawResults<Object[]> rawResults = queryRaw(query,
                new DataType[] {DataType.INTEGER});

        Object[] object = rawResults.getFirstResult();
        return (int) object[0];
    }
}
