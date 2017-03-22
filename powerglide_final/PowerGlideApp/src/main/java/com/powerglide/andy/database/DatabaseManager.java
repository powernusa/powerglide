package com.powerglide.andy.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.powerglide.andy.provider.PowerGlideContract;

/**
 * Created by Andy on 1/30/2017.
 */

public class DatabaseManager {

    private CustomSQliteOpenHelper mDBHelper;
    private Context mContext;
    public static final String DB_NAME = "power_glide.db";
    public static final int DB_VERSION = 1;


    //Database Columns
    public static final String TABLE_ROW_ID = "_id";
    public static final String TABLE_ROW_DATE = "driver_transaction_date";
    public static final String TABLE_ROW_DRIVER_USERNAME = "driver_transaction_driver_username";
    public static final String TABLE_ROW_RATING = "driver_transaction_rating";
    public static final String TABLE_ROW_GLIDER_USERNAME = "driver_transaction_glider_username";

    public static final String DB_ERROR = "db_error";

    public DatabaseManager(Context context) {
        mContext = context;
        mDBHelper = new CustomSQliteOpenHelper(mContext);
    }

    public Cursor getAllCursor(String table_name, String[] projection, String selection,
                               String[] selectionArgs, String sortOrder) {

        Cursor cr = null;
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        try {
            cr = db.query(table_name, projection, selection, selectionArgs, null, null, sortOrder);
        } catch (SQLException e) {
            Log.e(DB_ERROR, e.toString());
            e.printStackTrace();
        }

        return cr;
    }

    public long insertRow(String table_name, ContentValues cv) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long id = -1;
        try {
            id = db.insert(table_name, null, cv);
        } catch (SQLException e) {
            Log.e("DB_ERROR", e.toString());
            e.printStackTrace();
        }
        return id;

    }

    public int insertRows(String table_name, ContentValues[] values) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        int retCount = 0;
        try {
            for (ContentValues cv : values) {
                long _id = db.insert(table_name, null, cv);
                if (_id != -1) {
                    retCount++;
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return retCount;

    }


    public int delete(String table_name, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int returnInt = 0;
        try {
            returnInt = db.delete(table_name, whereClause, whereArgs);
        } catch (SQLException e) {
            Log.e("DB_ERROR", e.toString());
            e.printStackTrace();
        }
        return returnInt;
    }

    private class CustomSQliteOpenHelper extends SQLiteOpenHelper {

        public CustomSQliteOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createString = "create  table " + PowerGlideContract.TABLE_DRIVER_TRANSACTION + "("
                    + TABLE_ROW_ID + " integer primary key autoincrement, "
                    + TABLE_ROW_DATE + " text not null, "
                    + TABLE_ROW_DRIVER_USERNAME + " text not null, "
                    + TABLE_ROW_RATING + " integer, "
                    + TABLE_ROW_GLIDER_USERNAME + " text not null);";

            final String SQL_CREATE_ATM_TABLE = "CREATE TABLE " + PowerGlideContract.TABLE_ATM + "(" +
                    PowerGlideContract.Atm._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PowerGlideContract.COL_LAT + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_LON + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_NAME + " TEXT , " +
                    PowerGlideContract.COL_PLACE_ID + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_RATING + " NUMERIC , " +
                    PowerGlideContract.COL_OPEN_NOW + " TEXT , " +
                    PowerGlideContract.COL_ADDRESS + " TEXT , " +
                    PowerGlideContract.COL_DISTANCE + " NUMERIC NOT NULL, " +
                    " UNIQUE ( " + PowerGlideContract.COL_PLACE_ID + ") ON CONFLICT REPLACE " +
                    ");";
            final String SQL_CREATE_GAS_TABLE = "CREATE TABLE " + PowerGlideContract.TABLE_GAS_STATION + "(" +
                    PowerGlideContract.Gas_Station._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PowerGlideContract.COL_LAT + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_LON + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_NAME + " TEXT , " +
                    PowerGlideContract.COL_PLACE_ID + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_RATING + " NUMERIC , " +
                    PowerGlideContract.COL_OPEN_NOW + " TEXT , " +
                    PowerGlideContract.COL_ADDRESS + " TEXT , " +
                    PowerGlideContract.COL_DISTANCE + " NUMERIC NOT NULL, " +
                    " UNIQUE ( " + PowerGlideContract.COL_PLACE_ID + ") ON CONFLICT REPLACE " +
                    ");";

            final String SQL_CREATE_HOTEL_TABLE = "CREATE TABLE " + PowerGlideContract.TABLE_HOTEL + "(" +
                    PowerGlideContract.Hotel._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PowerGlideContract.COL_LAT + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_LON + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_NAME + " TEXT , " +
                    PowerGlideContract.COL_PLACE_ID + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_RATING + " NUMERIC , " +
                    PowerGlideContract.COL_OPEN_NOW + " TEXT , " +
                    PowerGlideContract.COL_ADDRESS + " TEXT , " +
                    PowerGlideContract.COL_DISTANCE + " NUMERIC NOT NULL, " +
                    " UNIQUE ( " + PowerGlideContract.COL_PLACE_ID + ") ON CONFLICT REPLACE " +
                    ");";

            final String SQL_CREATE_PARKING_TABLE = "CREATE TABLE " + PowerGlideContract.TABLE_PARKING + "(" +
                    PowerGlideContract.Parking._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PowerGlideContract.COL_LAT + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_LON + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_NAME + " TEXT , " +
                    PowerGlideContract.COL_PLACE_ID + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_RATING + " NUMERIC , " +
                    PowerGlideContract.COL_OPEN_NOW + " TEXT , " +
                    PowerGlideContract.COL_ADDRESS + " TEXT , " +
                    PowerGlideContract.COL_DISTANCE + " NUMERIC NOT NULL, " +
                    " UNIQUE ( " + PowerGlideContract.COL_PLACE_ID + ") ON CONFLICT REPLACE " +
                    ");";

            final String SQL_CREATE_PHARMACY_TABLE = "CREATE TABLE " + PowerGlideContract.TABLE_PHARMACY + "(" +
                    PowerGlideContract.Pharmacy._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PowerGlideContract.COL_LAT + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_LON + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_NAME + " TEXT , " +
                    PowerGlideContract.COL_PLACE_ID + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_RATING + " NUMERIC , " +
                    PowerGlideContract.COL_OPEN_NOW + " TEXT , " +
                    PowerGlideContract.COL_ADDRESS + " TEXT , " +
                    PowerGlideContract.COL_DISTANCE + " NUMERIC NOT NULL, " +
                    " UNIQUE ( " + PowerGlideContract.COL_PLACE_ID + ") ON CONFLICT REPLACE " +
                    ");";
            final String SQL_CREATE_RESTAURANT_TABLE = "CREATE TABLE " + PowerGlideContract.TABLE_RESTAURANT + "(" +
                    PowerGlideContract.Restaurant._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PowerGlideContract.COL_LAT + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_LON + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_NAME + " TEXT , " +
                    PowerGlideContract.COL_PLACE_ID + " TEXT NOT NULL, " +
                    PowerGlideContract.COL_RATING + " NUMERIC , " +
                    PowerGlideContract.COL_OPEN_NOW + " TEXT , " +
                    PowerGlideContract.COL_ADDRESS + " TEXT , " +
                    PowerGlideContract.COL_DISTANCE + " NUMERIC NOT NULL, " +
                    " UNIQUE ( " + PowerGlideContract.COL_PLACE_ID + ") ON CONFLICT REPLACE " +
                    ");";


            db.execSQL(SQL_CREATE_ATM_TABLE);
            db.execSQL(SQL_CREATE_GAS_TABLE);
            db.execSQL(SQL_CREATE_HOTEL_TABLE);
            db.execSQL(SQL_CREATE_PARKING_TABLE);
            db.execSQL(SQL_CREATE_RESTAURANT_TABLE);
            db.execSQL(SQL_CREATE_PHARMACY_TABLE);

            db.execSQL(createString);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String drop_table_string = "drop table if exists " + PowerGlideContract.TABLE_DRIVER_TRANSACTION;
            db.execSQL(drop_table_string);


            final String dropTable1 = "DROP TABLE IF EXISTS " + PowerGlideContract.TABLE_RESTAURANT;
            final String dropTable2 = "DROP TABLE IF EXISTS " + PowerGlideContract.TABLE_GAS_STATION;
            final String dropTable3 = "DROP TABLE IF EXISTS " + PowerGlideContract.TABLE_ATM;
            final String dropTable5 = "DROP TABLE IF EXISTS " + PowerGlideContract.TABLE_PHARMACY;
            final String dropTable7 = "DROP TABLE IF EXISTS " + PowerGlideContract.TABLE_HOTEL;
            final String dropTable11 = "DROP TABLE IF EXISTS " + PowerGlideContract.TABLE_PARKING;

            db.execSQL(dropTable1);
            db.execSQL(dropTable5);
            db.execSQL(dropTable2);
            db.execSQL(dropTable3);
            db.execSQL(dropTable7);
            db.execSQL(dropTable11);

            onCreate(db);

        }
    }
}
