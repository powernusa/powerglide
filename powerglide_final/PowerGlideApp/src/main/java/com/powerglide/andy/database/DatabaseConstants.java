package com.powerglide.andy.database;

/**
 * Created by Andy on 1/31/2017.
 */

public interface DatabaseConstants {
    String TABLE_DRIVER_TRANSACTION = "driver_transaction_table";
    String PATH_DRIVER_TRANSACTION = "path_driver_transaction";

    //Database Columns
    String TABLE_ROW_DATE = "driver_transaction_date";
    String TABLE_ROW_DRIVER_USERNAME = "driver_transaction_driver_username";
    String TABLE_ROW_RATING = "driver_transaction_rating";
    String TABLE_ROW_GLIDER_USERNAME = "driver_transaction_glider_username";

    /*  =================================================================================  */

    String TABLE_ATM = "atm";
    String TABLE_GAS_STATION = "gas_station";
    String TABLE_HOTEL = "hotel";
    String TABLE_PARKING = "parking";
    String TABLE_PHARMACY = "pharmacy";
    String TABLE_RESTAURANT = "restaurant";


    // All tables need these columns
    String COL_LAT = "lat";
    String COL_LON = "lon";
    String COL_NAME = "name";
    String COL_PLACE_ID = "place_id";
    String COL_RATING = "rating";
    String COL_OPEN_NOW = "open_now";
    String COL_ADDRESS = "address";
    String COL_DISTANCE = "distance";


    //path
    String PATH_ATM = "atm";
    String PATH_GAS = "gas";
    String PATH_HOTEL = "hotels";
    String PATH_PARKING = "parking";
    String PATH_PHARMACY = "pharmacy";
    String PATH_RESTAURANT = "restaurant";


}
