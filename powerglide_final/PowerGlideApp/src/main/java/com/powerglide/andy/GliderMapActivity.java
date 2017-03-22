package com.powerglide.andy;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.powerglide.andy.nearby.NearbyActivity;
import com.powerglide.andy.otheractivity.DriverDetailsActivity;
import com.powerglide.andy.otheractivity.GliderRatingActivity;
import com.powerglide.andy.utility.Constants;
import com.powerglide.andy.utility.DatabaseLogoutAsyncTask;
import com.powerglide.andy.utility.FetchAddressIntentService;
import com.powerglide.andy.utility.UtilityClass;

import java.util.ArrayList;
import java.util.List;

public class GliderMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastGliderLocation;
    private LocationRequest mLocationRequest;
    private Marker mCurrentGliderMarker;
    public static final String LOG_TAG = GliderMapActivity.class.getSimpleName();
    private String mDisplayName;
    private String mGliderUsername;

    //Driver Details Info
    private String mDriverUsername;
    private int mDriverAge;
    private String mDriverGender;
    private String mDriverDisplayName;
    private View mParentGliderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glider_map);
        mParentGliderLayout = findViewById(R.id.glider_map_coordinator_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();


        if (getIntent() != null) {
            mDisplayName = getIntent().getStringExtra(Constants.EXTRA_DISPLAY_NAME);
            //ab.setIcon(R.drawable.powerglidelogo);  //get a small icon
            ab.setTitle(mDisplayName);
        }

        if (savedInstanceState != null) {
            mDriverUsername = savedInstanceState.getString("DRIVER_USERNAME");
        }

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GliderDialog();
            }
        });

        initializeMap();
        mAddressResultReceiver = new AddressResultReceiver(new Handler());

        Query_GliderRequest();
        checkForTransactionUpdates();

    }

    private void initializeMap() {
        buildGoogleAPIClient();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("DRIVER_USERNAME", mDriverUsername);
    }

    private void LogOut() {
        ParseUser.logOut();
        if (mDialog == null) {
            return;
        }
        if (mDialog.isShowing()) mDialog.dismiss();
        myHandler.removeCallbacks(myRunnable);
        new DatabaseLogoutAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.DELETE_ALL_NEARBY_DATABASE);
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        overridePendingTransition(R.anim.push_down_in, R.anim.slide_out_right);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

        checkForTransactionUpdates();
        checkForUpdates();
        if (mDisplayName != null) {
            getSupportActionBar().setTitle(mDisplayName);
        } else {
            LogOut();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        myHandler.removeCallbacks(myRunnable);
        myTransactionHandler.removeCallbacks(myTransactionRunnable);
        mGoogleApiClient.disconnect();

    }

    /**
     * ****************************************************************************************
     * <p>
     * Parse Related
     * <p>
     * ****************************************************************************************
     */

    //Initialzing ; to determine mRequestActive is false or true
    private void Query_GliderRequest() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_TABLE_GLIDER_REQUEST);
        if (ParseUser.getCurrentUser() == null) {
            return;
        }
        mGliderUsername = ParseUser.getCurrentUser().getUsername();   // get glider user name once
        query.whereEqualTo(Constants.PARSE_GLIDER_USERNAME, mGliderUsername);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects == null) {
                    return;
                }
                if (objects.size() > 0 && e == null) {
                    mRequestActive = true;
                    checkForUpdates();
                } else {  //not found
                    mRequestActive = false;
                }
            }
        });
    }

    private boolean mRequestActive = false;
    private boolean mRequestLock = false;  // if true then cannot request driver

    private void RequestDriver() {
        if (mRequestLock) {
            Toast.makeText(this, getString(R.string.cannot_request_driver), Toast.LENGTH_SHORT).show();
            return;
        }

        if (mRequestActive) {  //since request is active then need to cancel
            Toast.makeText(this, getString(R.string.cancelling_request) + "...", Toast.LENGTH_LONG).show();
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_TABLE_GLIDER_REQUEST);
            query.whereEqualTo(Constants.PARSE_GLIDER_USERNAME, mGliderUsername);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects == null) {
                        return;
                    }
                    if (objects.size() > 0 && e == null) {  //found
                        for (ParseObject object : objects) {
                            object.deleteInBackground();
                        }
                        mRequestActive = false;
                        //update map
                        if (mLastGliderLocation != null) {
                            mMap.clear();
                            writeGliderLocation(mLastGliderLocation);
                            mRequestTextView.setText(getString(R.string.request_driver));
                            myHandler.removeCallbacks(myRunnable);
                        }
                    }
                }
            });
        } else {       //request driver here
            if (mLastGliderLocation != null) {
                Toast.makeText(this, getString(R.string.request_driver) + "...", Toast.LENGTH_LONG).show();
                ParseObject requestParseObject = new ParseObject(Constants.PARSE_TABLE_GLIDER_REQUEST);
                requestParseObject.put(Constants.PARSE_GLIDER_USERNAME, mGliderUsername);
                ParseGeoPoint gliderGeoPoint = new ParseGeoPoint(mLastGliderLocation.getLatitude(), mLastGliderLocation.getLongitude());
                requestParseObject.put(Constants.PARSE_GLIDER_LOCATION, gliderGeoPoint);
                requestParseObject.put(Constants.PARSE_GLIDER_DISPLAY_NAME, mDisplayName);
                requestParseObject.put(Constants.PARSE_GLIDER_ADDRESS, mGliderAddress);
                requestParseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            mRequestActive = true;
                            //mRequestLock = true;
                            mRequestTextView.setText(getString(R.string.cancel_request));
                            writeGliderLocation(mLastGliderLocation);
                            checkForUpdates();
                        } else {
                                /* FAIL IN SAVING in ParseServer */
                        }
                    }
                });
            } else {   //glider location not known

            }

        }

    }

    //private static final int PERIODIC_TIME = 10000;  // in milliseconds
    private Handler myHandler = new Handler();
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            //Toast.makeText(getApplicationContext(),"checkForUpdates Thread running",Toast.LENGTH_LONG).show();
            checkForUpdates();
        }
    };

    private synchronized void checkForUpdates() {   //check for driver location updates and drawn on map
        if (mLastGliderLocation == null) return;
        ParseQuery<ParseObject> queryGliderRequest = ParseQuery.getQuery(Constants.PARSE_TABLE_GLIDER_REQUEST);
        queryGliderRequest.whereEqualTo(Constants.PARSE_GLIDER_USERNAME, mGliderUsername);
        queryGliderRequest.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects == null) {
                    return;
                }
                if (objects.size() > 0 && e == null) {
                    String driverUserName = objects.get(objects.size() - 1).getString(Constants.PARSE_DRIVER_USERNAME);
                    if (driverUserName == null) {
                        // no driver user name
                        myHandler.postDelayed(myRunnable, Constants.THREAD_PERIODIC_TIME_10000);   //passed to m. queue
                        return;
                    }
                    // Pull out driver data; location data then ..
                    // Draw glider and driver location on the same map.
                    mDriverUsername = driverUserName;
                    ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
                    queryUser.whereEqualTo(Constants.PARSE_USERNAME, driverUserName);
                    queryUser.whereEqualTo(Constants.PARSE_GLIDER_DRIVER, "driver");
                    queryUser.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (objects == null) {
                                return;
                            }
                            if (objects.size() > 0 && e == null) {
                                ParseGeoPoint driverGeoPoint = objects.get(objects.size() - 1).getParseGeoPoint(Constants.PARSE_LOCATION);
                                mDriverAge = objects.get(objects.size() - 1).getInt(Constants.PARSE_DRIVER_AGE);
                                mDriverGender = objects.get(objects.size() - 1).getString(Constants.PARSE_DRIVER_GENDER);
                                mDriverDisplayName = objects.get(objects.size() - 1).getString(Constants.PARSE_DISPLAYNAME);
                                if (mLastGliderLocation != null && driverGeoPoint != null) {
                                    mDriverLayoutVisible = true;
                                    ParseGeoPoint gliderGeoPoint =
                                            new ParseGeoPoint(mLastGliderLocation.getLatitude(), mLastGliderLocation.getLongitude());

                                    // then draw glider and driver location on a map
                                    LatLng driverLatLng = new LatLng(driverGeoPoint.getLatitude(), driverGeoPoint.getLongitude());
                                    LatLng gliderLatLng = new LatLng(gliderGeoPoint.getLatitude(), gliderGeoPoint.getLongitude());

                                    ArrayList<Marker> markers = new ArrayList<Marker>();

                                    if (mMap != null) {
                                        mMap.clear();

                                        markers.add(mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Driver")
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
                                        markers.add(mMap.addMarker(new MarkerOptions().position(gliderLatLng).title("Glider")));

                                        LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                        for (Marker marker : markers) {
                                            builder.include(marker.getPosition());
                                        }
                                        LatLngBounds bounds = builder.build();
                                        int padding = 220;
                                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                                        mMap.animateCamera(cu);
                                    }
                                }
                            } else {   // no driver found
                                mDriverLayoutVisible = false;
                            }
                        }
                    });

                    if (mRequestActive) {
                        //use handlers to call checkForUpdates()
                        myHandler.postDelayed(myRunnable, Constants.THREAD_PERIODIC_TIME_10000);    //passed to m. queue
                        return;
                    }
                } else {
                        /* no request made by glider current user*/
                }
            }
        });

    }

    private Handler myTransactionHandler = new Handler();
    private Runnable myTransactionRunnable = new Runnable() {
        @Override
        public void run() {
            checkForTransactionUpdates();
        }
    };

    private synchronized void checkForTransactionUpdates() {

        ParseQuery<ParseObject> queryTransaction = ParseQuery.getQuery(Constants.PARSE_TABLE_POWER_TRANSACTION);
        if (mGliderUsername == null) {
            return;
        }
        queryTransaction.whereEqualTo(Constants.PARSE_GLIDER_USERNAME, mGliderUsername);
        queryTransaction.whereDoesNotExist(Constants.PARSE_RATING);
        queryTransaction.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects == null) {
                    return;
                }
                if (objects.size() > 0 && e == null) {
                    mRatingLayoutVisible = true;
                    mRequestActive = false;
                    mRequestLock = true;
                }
                myTransactionHandler.postDelayed(myTransactionRunnable, Constants.THREAD_PERIODIC_TIME_10000);    //passed to m. queue
            }
        });
    }


    /**
     * *****************************************************************************************
     * <p>
     * Permissions and  Listeners
     * <p>
     * ****************************************************************************************
     */

    private boolean checkPermission() {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void buildGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //OnMapReadyCallback
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap == null) {
            return;
        }
        mMap = googleMap;

    }

    //ConnectionCallback
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastKnownLocation();
    }

    private void getLastKnownLocation() {
        if (checkPermission()) {
            mLastGliderLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastGliderLocation != null) {
                writeGliderLocation(mLastGliderLocation);
                //get glider address
                Intent fetchAddressIntent = new Intent(this, FetchAddressIntentService.class);
                fetchAddressIntent.putExtra(Constants.EXTRA_LOCATION_DATA, mLastGliderLocation);
                fetchAddressIntent.putExtra(Constants.RECEIVER, mAddressResultReceiver);
                startService(fetchAddressIntent);
                getLocationRequest();
            } else {
                getLocationRequest();
            }
        } else
            askPermission();

    }


    private void getLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setInterval(Constants.LOCATION_REQUEST_INTERVAL_10000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (checkPermission()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    //ConnectionCallback
    @Override
    public void onConnectionSuspended(int i) {

    }

    //OnConnectionFailedListener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //LocationListener
    @Override
    public void onLocationChanged(Location location) {
        if (UtilityClass.isNetworkConnected(this)) {
            mLastGliderLocation = location;

            if (!mRequestActive) {
                writeGliderLocation(location);
            }

            //get glider address
            Intent fetchAddressIntent = new Intent(this, FetchAddressIntentService.class);
            fetchAddressIntent.putExtra(Constants.EXTRA_LOCATION_DATA, location);
            fetchAddressIntent.putExtra(Constants.RECEIVER, mAddressResultReceiver);
            startService(fetchAddressIntent);
        } else {
            Snackbar.make(mParentGliderLayout, getString(R.string.no_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
        }
    }

    private void writeGliderLocation(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("Glider Location");
        if (mMap != null) {
            if (mCurrentGliderMarker != null) mCurrentGliderMarker.remove();

            mCurrentGliderMarker = mMap.addMarker(markerOptions);
            float zoom = 16f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            mMap.animateCamera(cameraUpdate);
        }


    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_ACCESS_FINE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_ACCESS_FINE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getLastKnownLocation();

            } else {
                // Permission denied
            }
        }

    }

    /**
     * ****************************************************************************************
     * <p>
     * FAB Dialog
     * <p>
     * *****************************************************************************************
     */
    private FloatingActionButton mFab;
    private Dialog mDialog;
    private RelativeLayout mLogoutLayout, mRequestLayout, mCrossLayout, mDriverDetailsLayout,
            mRatingLayout, mNearbyLayout;
    private TextView mRequestTextView;
    private boolean mDriverLayoutVisible = false;
    private boolean mRatingLayoutVisible = false;
    private static final int GLIDER_RATING = 101;

    private View.OnClickListener myDialogClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!UtilityClass.isNetworkConnected(getApplicationContext())) {
                Snackbar.make(mParentGliderLayout, getString(R.string.no_connection), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
                mDialog.dismiss();
                return;
            }
            switch (v.getId()) {
                case R.id.cross_layout:
                    mDialog.dismiss();
                    break;
                case R.id.logout_layout:
                    mDialog.dismiss();
                    LogOut();
                    break;
                case R.id.request_layout:
                    mDialog.dismiss();
                    RequestDriver();

                    break;
                case R.id.driver_layout:
                    Intent intentDriver = new Intent(getApplicationContext(), DriverDetailsActivity.class);
                    intentDriver.putExtra(Constants.EXTRA_DRIVER_USERNAME, mDriverUsername);
                    intentDriver.putExtra(Constants.EXTRA_DRIVER_AGE, mDriverAge);
                    intentDriver.putExtra(Constants.EXTRA_DRIVER_GENDER, mDriverGender);
                    intentDriver.putExtra(Constants.EXTRA_DRIVER_DISPLAYNAME, mDriverDisplayName);
                    startActivity(intentDriver);
                    mDialog.dismiss();
                    overridePendingTransition(R.anim.push_down_in, R.anim.slide_out_left);
                    break;

                case R.id.rating_layout:
                    Intent intentGliderRating = new Intent(getApplicationContext(), GliderRatingActivity.class);
                    startActivityForResult(intentGliderRating, GLIDER_RATING);
                    mDialog.dismiss();
                    overridePendingTransition(R.anim.push_down_in, R.anim.slide_out_left);
                    break;
                case R.id.nearby_layout:
                    Intent nearbyIntent = new Intent(getApplicationContext(), NearbyActivity.class);
                    startActivity(nearbyIntent);
                    mDialog.dismiss();
                    overridePendingTransition(R.anim.push_down_in, R.anim.slide_out_left);
                    break;

            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GLIDER_RATING) {
            if (resultCode == Activity.RESULT_OK) {
                final int returned_rating = data.getIntExtra(GliderRatingActivity.RATING_VALUE, 0);

                ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_TABLE_POWER_TRANSACTION);

                query.whereEqualTo(Constants.PARSE_DRIVER_USERNAME, mDriverUsername);
                if (ParseUser.getCurrentUser() == null) {
                    return;
                }
                query.whereEqualTo(Constants.PARSE_GLIDER_USERNAME, ParseUser.getCurrentUser().getUsername());
                query.whereDoesNotExist(Constants.PARSE_RATING);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (objects.size() > 0 && e == null) {
                            if (objects == null) {
                                return;
                            }
                            ParseObject object = objects.get(0);
                            object.put(Constants.PARSE_RATING, returned_rating);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.thank_you_for_your_feedback),
                                                Toast.LENGTH_LONG).show();

                                        mRequestActive = false;
                                        mRatingLayoutVisible = false;
                                        mRequestLock = false;
                                        mMap.clear();
                                    } else {
                                        Toast.makeText(getApplicationContext(), getString(R.string.no_feedback_saved), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    private void GliderDialog() {
        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);   //before setContentView
        mDialog.setContentView(R.layout.glider_dialog_box);
        initializeDialogWidget(mDialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        if (UtilityClass.isRTL(this)) {
            params.gravity = Gravity.BOTTOM | Gravity.START;
        } else {
            params.gravity = Gravity.BOTTOM | Gravity.END;
        }

        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }


    private void initializeDialogWidget(Dialog dialog) {
        mLogoutLayout = (RelativeLayout) dialog.findViewById(R.id.logout_layout);
        mRequestLayout = (RelativeLayout) dialog.findViewById(R.id.request_layout);
        mCrossLayout = (RelativeLayout) dialog.findViewById(R.id.cross_layout);
        mDriverDetailsLayout = (RelativeLayout) dialog.findViewById(R.id.driver_layout);
        mRequestTextView = (TextView) dialog.findViewById(R.id.request_textview);
        mRatingLayout = (RelativeLayout) dialog.findViewById(R.id.rating_layout);
        mNearbyLayout = (RelativeLayout) dialog.findViewById(R.id.nearby_layout);

        if (mRequestActive) {
            mRequestTextView.setText(getString(R.string.cancel_request));
            if (mDriverLayoutVisible) {
                mDriverDetailsLayout.setVisibility(View.VISIBLE);
            } else {
                mDriverDetailsLayout.setVisibility(View.GONE);
            }

        } else {
            mRequestTextView.setText(getString(R.string.request_driver));
            mDriverDetailsLayout.setVisibility(View.GONE);
            mRatingLayout.setVisibility(View.GONE);
            mRatingLayout.setVisibility(View.GONE);
        }

        if (mRatingLayoutVisible) {
            mRatingLayout.setVisibility(View.VISIBLE);
        } else if (!mRatingLayoutVisible) {
            mRatingLayout.setVisibility(View.GONE);
        }


        mLogoutLayout.setOnClickListener(myDialogClickListener);
        mRequestLayout.setOnClickListener(myDialogClickListener);
        mCrossLayout.setOnClickListener(myDialogClickListener);
        mDriverDetailsLayout.setOnClickListener(myDialogClickListener);
        mRatingLayout.setOnClickListener(myDialogClickListener);
        mNearbyLayout.setOnClickListener(myDialogClickListener);

    }

    /***************************************************************************************
     * *************************************************************************************
     */
    private String mGliderAddress;
    private AddressResultReceiver mAddressResultReceiver;

    public class AddressResultReceiver extends ResultReceiver {

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if (resultData != null) {
                mGliderAddress = resultData.getString(Constants.KEY_RESULT_ADDRESS);
            }
        }
    }
}
