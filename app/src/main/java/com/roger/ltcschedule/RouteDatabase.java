package com.roger.ltcschedule;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Administrator on 2017/9/16.
 */

public class RouteDatabase {

    private static final String TAG = "RouteDatabase";
    public static final String ROUTE_COLUMN_NAME = "Route";
    public static final String DIRECTION_COLUMN_NAME = "Direction";
    public static final String STOP_COLUMN_NAME = "Stop";
    private SQLiteDatabase mSQLiteDatabase;
    private DatabaseHelper mDatabaseHelper;
    private Context mContext;

    public RouteDatabase(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
        mContext = context;
    }

    public RouteDatabase createDatabase() {
        try {
            mDatabaseHelper.createDatabase();
        } catch(IOException e) {
            Log.e(TAG, "createDatabase: Unable to create database");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public RouteDatabase open() {
        try {
            mDatabaseHelper.openDatabase();
            mDatabaseHelper.close();
            mSQLiteDatabase = mDatabaseHelper.getReadableDatabase();
        } catch(SQLException e) {
            Log.e(TAG, "open: " + e.toString() );
        }
        return this;
    }

    public void close() {
        mDatabaseHelper.close();
    }

    public Cursor query(String stopId) {
        Cursor cursor = null;
        try {
            cursor = mSQLiteDatabase.query("Routes", null, "Stop = ?", new String[]{stopId},
                            null, null, null);
//            if(cursor != null) {
//                cursor.moveToNext();
//            }
        } catch(SQLException e) {
            Log.e(TAG, "query: " + e.toString() );
        }
        return cursor;
    }

}
