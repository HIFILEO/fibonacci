/*
Property of Daniel Leonardis 2015.
Free to distribute, use, or modify under open source license
*/
package com.appsbyleo.fibonacci.model.daos.interfaces;

import com.appsbyleo.fibonacci.model.FibonacciNumber;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Interface for data access object
 */
public interface FibonacciNumberDao extends Dao<FibonacciNumber, Integer> {
    public int getLastFibonacciEntry() throws SQLException;
}
