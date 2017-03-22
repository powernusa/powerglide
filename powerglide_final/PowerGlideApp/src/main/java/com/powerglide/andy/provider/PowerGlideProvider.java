package com.powerglide.andy.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.powerglide.andy.database.DatabaseManager;

/**
 * Created by Andy on 1/31/2017.
 */

public class PowerGlideProvider extends ContentProvider {
    public static final int DRIVER_TRANSACTION_TABLE = 1001;
    public static final int DRIVER_TRANSACTION_TABLE_ITEM = 1002;
    public static final int ATM = 101;
    public static final int GAS_STATION = 201;
    public static final int HOTELS = 301;
    public static final int PARKING = 401;
    public static final int PHARMACY = 501;
    public static final int RESTAURANT = 601;
    public static final String LOG_TAG = PowerGlideProvider.class.getSimpleName();

    private DatabaseManager mDb;

    public static UriMatcher mmUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String AUTHORITY = PowerGlideContract.AUTHORITY;
        uriMatcher.addURI(AUTHORITY, PowerGlideContract.PATH_DRIVER_TRANSACTION, DRIVER_TRANSACTION_TABLE);
        uriMatcher.addURI(PowerGlideContract.AUTHORITY, PowerGlideContract.PATH_DRIVER_TRANSACTION + "/#", DRIVER_TRANSACTION_TABLE_ITEM);

        uriMatcher.addURI(AUTHORITY, PowerGlideContract.PATH_ATM, ATM);
        uriMatcher.addURI(AUTHORITY, PowerGlideContract.PATH_GAS, GAS_STATION);
        uriMatcher.addURI(AUTHORITY, PowerGlideContract.PATH_HOTEL, HOTELS);
        uriMatcher.addURI(AUTHORITY, PowerGlideContract.PATH_PARKING, PARKING);
        uriMatcher.addURI(AUTHORITY, PowerGlideContract.PATH_PHARMACY, PHARMACY);
        uriMatcher.addURI(AUTHORITY, PowerGlideContract.PATH_RESTAURANT, RESTAURANT);
        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        mDb = new DatabaseManager(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cr = null;
        switch (mmUriMatcher.match(uri)) {
            case DRIVER_TRANSACTION_TABLE:
                cr = mDb.getAllCursor(PowerGlideContract.TABLE_DRIVER_TRANSACTION, projection, selection, selectionArgs, sortOrder);
                cr.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case ATM:
                cr = mDb.getAllCursor(PowerGlideContract.TABLE_ATM, projection, selection, selectionArgs, sortOrder);
                cr.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case GAS_STATION:
                cr = mDb.getAllCursor(PowerGlideContract.TABLE_GAS_STATION, projection, selection, selectionArgs, sortOrder);
                cr.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case HOTELS:
                cr = mDb.getAllCursor(PowerGlideContract.TABLE_HOTEL, projection, selection, selectionArgs, sortOrder);
                cr.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case PARKING:
                cr = mDb.getAllCursor(PowerGlideContract.TABLE_PARKING, projection, selection, selectionArgs, sortOrder);
                cr.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case PHARMACY:
                cr = mDb.getAllCursor(PowerGlideContract.TABLE_PHARMACY, projection, selection, selectionArgs, sortOrder);
                cr.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case RESTAURANT:
                cr = mDb.getAllCursor(PowerGlideContract.TABLE_RESTAURANT, projection, selection, selectionArgs, sortOrder);
                cr.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            default:
                throw new IllegalArgumentException("UNKNOWN URI: " + uri);
        }

        return cr;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final int match = mmUriMatcher.match(uri);
        int retCount = 0;
        switch (match) {
            case ATM:
                retCount = mDb.insertRows(PowerGlideContract.TABLE_ATM, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return retCount;
            case GAS_STATION:
                retCount = mDb.insertRows(PowerGlideContract.TABLE_GAS_STATION, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return retCount;
            case HOTELS:
                retCount = mDb.insertRows(PowerGlideContract.TABLE_HOTEL, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return retCount;
            case PARKING:
                retCount = mDb.insertRows(PowerGlideContract.TABLE_PARKING, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return retCount;
            case PHARMACY:
                retCount = mDb.insertRows(PowerGlideContract.TABLE_PHARMACY, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return retCount;
            case RESTAURANT:
                retCount = mDb.insertRows(PowerGlideContract.TABLE_RESTAURANT, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return retCount;

            default:
                return super.bulkInsert(uri, values);

        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = mmUriMatcher.match(uri);
        long id = -1;
        switch (uriType) {
            case DRIVER_TRANSACTION_TABLE:
                id = mDb.insertRow(PowerGlideContract.TABLE_DRIVER_TRANSACTION, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Uri ur = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(uri, null);
        return ur;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = mmUriMatcher.match(uri);
        int returnInt = 0;
        switch (uriType) {
            case DRIVER_TRANSACTION_TABLE:
                returnInt = mDb.delete(PowerGlideContract.TABLE_DRIVER_TRANSACTION, selection, selectionArgs);
                break;
            case ATM:
                returnInt = mDb.delete(PowerGlideContract.TABLE_ATM, selection, selectionArgs);
                break;
            case GAS_STATION:
                returnInt = mDb.delete(PowerGlideContract.TABLE_GAS_STATION, selection, selectionArgs);
                break;
            case HOTELS:
                returnInt = mDb.delete(PowerGlideContract.TABLE_HOTEL, selection, selectionArgs);
                break;
            case PARKING:
                returnInt = mDb.delete(PowerGlideContract.TABLE_PARKING, selection, selectionArgs);
                break;
            case PHARMACY:
                returnInt = mDb.delete(PowerGlideContract.TABLE_PHARMACY, selection, selectionArgs);
                break;
            case RESTAURANT:
                returnInt = mDb.delete(PowerGlideContract.TABLE_RESTAURANT, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnInt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


}
