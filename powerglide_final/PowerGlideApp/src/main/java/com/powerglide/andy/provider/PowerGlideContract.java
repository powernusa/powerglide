package com.powerglide.andy.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.powerglide.andy.database.DatabaseConstants;

/**
 * Created by Andy on 1/31/2017.
 */

public class PowerGlideContract implements DatabaseConstants {

    public static final String AUTHORITY = "com.powerglide.andy.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);


    public static class DriverTransaction implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DRIVER_TRANSACTION).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + AUTHORITY + "/" + PATH_DRIVER_TRANSACTION;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + AUTHORITY + "/" + PATH_DRIVER_TRANSACTION;

        public static final String[] PROJECTION_ALL = {"_id",
                "driver_transaction_date",
                "driver_transaction_driver_username",
                "driver_transaction_rating",
                "driver_transaction_glider_username"
        };
    }


    public static final class Atm implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ATM).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + AUTHORITY + "/" + PATH_ATM;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + AUTHORITY + "/" + PATH_ATM;

        public static Uri buildAtmUri_id(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class Gas_Station implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GAS).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + AUTHORITY + "/" + PATH_GAS;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + AUTHORITY + "/" + PATH_GAS;

        public static Uri buildGasStationUri_id(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class Hotel implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_HOTEL).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + AUTHORITY + "/" + PATH_HOTEL;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + AUTHORITY + "/" + PATH_HOTEL;

        public static Uri buildHotelUri_id(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class Parking implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PARKING).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + AUTHORITY + "/" + PATH_PARKING;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + AUTHORITY + "/" + PATH_PARKING;

        public static Uri buildParkingUri_id(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class Pharmacy implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PHARMACY).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + AUTHORITY + "/" + PATH_PHARMACY;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + AUTHORITY + "/" + PATH_PHARMACY;

        public static Uri buildPharmacyUri_id(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class Restaurant implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RESTAURANT).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + AUTHORITY + "/" + PATH_RESTAURANT;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + AUTHORITY + "/" + PATH_RESTAURANT;


        public static Uri buildRestaurantUri_id(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


}
