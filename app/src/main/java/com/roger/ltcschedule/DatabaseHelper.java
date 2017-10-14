package com.roger.ltcschedule;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2017/9/15.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static String DB_PATH="";
    private static String DB_NAME="routes.db";
    private SQLiteDatabase mSQLiteDatabase;
    //Since we need the context instacne to get the assets, we cache
    // the instance of it here
    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        if(Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        mContext = context;
    }

    public void createDatabase() throws IOException {
        // If the database doesnt exist, copy it from assets.

        boolean mDatabaseExist = checkDatabase();

        if(!mDatabaseExist) {
            this.getReadableDatabase();
            this.close();
            try{
                copyDatabase();
                Log.e(TAG, "createDatabase: database created");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Check that the database exists here : /data/data/package/databases/route.db
    private boolean checkDatabase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    //Copy the database from assets
    private void copyDatabase() throws IOException {
        InputStream in = mContext.getAssets().open(DB_NAME);
        String outputFileName = DB_PATH + DB_NAME;
        OutputStream out = new FileOutputStream(outputFileName);
        byte[] buffer = new byte[1024];
        int mLength;
        while( (mLength = in.read(buffer)) > 0 ) {
            out.write(buffer, 0, mLength);
        }
        out.flush();
        in.close();
        out.close();
    }

    public boolean openDatabase() throws SQLException {
        String mPath = DB_PATH + DB_NAME;
        mSQLiteDatabase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mSQLiteDatabase != null;
    }

    @Override
    public synchronized void close() {
        if(mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
