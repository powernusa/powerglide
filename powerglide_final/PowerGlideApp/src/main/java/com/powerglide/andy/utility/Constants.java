package com.powerglide.andy.utility;

/**
 * Created by Andy on 12/23/2016.
 */

public class Constants {
    public static final int SUCCESS_RESULT = 0;

    public static final int FAILURE_RESULT = 1;

    public static final String PACKAGE_NAME = "com.powerglide.andy";

    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    public static final String KEY_RESULT_ADDRESS = PACKAGE_NAME + ".KEY_RESULT_ADDRESS";

    public static final String EXTRA_LOCATION_DATA = PACKAGE_NAME + ".EXTRA_LOCATION_DATA";
    public static final String EXTRA_GLIDER_DATA = PACKAGE_NAME + ".EXTRA_GLIDER_DATA";
    public static final String EXTRA_DRIVER_DATA = PACKAGE_NAME + ".EXTRA_DRIVER_DATA";
    public static final String EXTRA_DISPLAY_NAME = PACKAGE_NAME + ".EXTRA_DISPLAY_NAME";

    /* GliderMapActivity */
    public static final String EXTRA_DRIVER_USERNAME = "DriverUsername";
    public static final String EXTRA_DRIVER_AGE = "DriverAge";
    public static final String EXTRA_DRIVER_GENDER = "DriverGender";
    public static final String EXTRA_DRIVER_DISPLAYNAME = "DriverDisplayName";

    /* ViewRequestsActivity.java */
    public static final String DISPLAY_NAME = "DisplayName";
    public static final String DRIVER_DISPLAY_NAME = "driver_display_name";

    /*GliderRequest*/
    public static final int REQUEST_ACCESS_FINE_PERMISSION = 999;

    /*Parse Related */
    public static final String PARSE_TABLE_GLIDER_REQUEST = "GliderRequest";
    public static final String PARSE_GLIDER_USERNAME = "gliderUsername";
    public static final String PARSE_GLIDER_DISPLAY_NAME = "gliderDisplayName";
    public static final String PARSE_DRIVER_USERNAME = "driverUsername";
    public static final String PARSE_ACCEPTED_REQUEST = "acceptedRequest";
    public static final String PARSE_GLIDER_ADDRESS = "gliderAddress";
    public static final String PARSE_GLIDER_LOCATION = "gliderLocation";

    public static final String PARSE_LOCATION = "location";  //from  table User
    public static final String PARSE_USERNAME = "username";  //username == email
    public static final String PARSE_GLIDER_DRIVER = "glider_driver";  //from table User
    public static final String PARSE_DRIVER_AGE = "driverAge";   //from table User
    public static final String PARSE_DRIVER_GENDER = "driverGender";   //from table User
    public static final String PARSE_DISPLAYNAME = "displayname";    //from table User

    public static final String PARSE_TABLE_POWER_TRANSACTION = "PowerTransaction";
    public static final String PARSE_RATING = "rating";

    public static final String PARSE_TABLE_HISTORY_REQUEST = "HistoryRequest";
    public static final String PARSE_GLIDER_PICKED_UP_ADDRESS = "gliderPickedUpAddress";

    /* Google Direction Uri */
    public static final String GOOGLE_DIRECTION_URI = "http://maps.google.com/maps?saddr=";

    /*  threads*/
    public static final int THREAD_PERIODIC_TIME_10000 = 10000;  //milliseconds

    /* LocationRequest */
    public static final int LOCATION_REQUEST_INTERVAL_10000 = 10000;  //milliseconds

    /* NetworkInfo */
    public static final String ACTION_NETWORK_MESSAGE = "action_network_message";
    public static final String NETWORK_INFO = "network_info";

    /* DatabaseLogoutAsyncTask.java */
    public static final String DELETE_ALL_NEARBY_DATABASE = "DeleteAllNearbyDatabase";
    public static final String DELETE_DRIVER_TRANSACTION_DATABASE = "DeleteDriverTransactionDatabase";

    /* NearbyActivity.java */
    public static final String GLIDER_LATITUDE = "GliderLatitude";
    public static final String GLIDER_LONGITUDE = "GliderLongitude";

}
