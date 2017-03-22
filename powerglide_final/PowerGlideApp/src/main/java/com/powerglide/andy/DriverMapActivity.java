package com.powerglide.andy;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.powerglide.andy.objects.Driver;
import com.powerglide.andy.objects.Glider;
import com.powerglide.andy.otheractivity.DriverTransaction;
import com.powerglide.andy.utility.Constants;
import com.powerglide.andy.utility.DatabaseLogoutAsyncTask;
import com.powerglide.andy.utility.UtilityClass;

import java.util.ArrayList;
import java.util.List;

public class DriverMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String mDriverUsername;   /* ***  */
    private Location mLastDriverLocation;
    private Marker mCurrentDriverMarker;
    private FloatingActionButton mFab;

    private double mGliderLatitude;
    private double mGliderLongitude;
    private double mDriverLatitude;
    private double mDriverLongitude;
    private String mGliderAddress;
    private String mGliderUserName;  // username == email

    private boolean mAcceptedMode = false;
    private View mParentDriverMapLayout;

    public static final String LOG_TAG = DriverMapActivity.class.getSimpleName();
    public static final String SAVE_GLIDER_USERNAME = "save_glider_username";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_GLIDER_USERNAME, mGliderUserName);
    }

    private ViewTreeObserver.OnGlobalLayoutListener myGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (mMap == null) {  //mMap not ready return!!!
                return;
            }
            mMap.clear();
            LatLng driverLocation = null;
            LatLng gliderLocation = null;

            driverLocation = new LatLng(mDriverLatitude, mDriverLongitude);

            gliderLocation = new LatLng(mGliderLatitude, mGliderLongitude);

            ArrayList<Marker> markers = new ArrayList<>();

            markers.add(mMap.addMarker(new MarkerOptions().position(driverLocation).title("Your Location").rotation(90)));
            markers.add(mMap.addMarker(new MarkerOptions().position(gliderLocation).title(mGliderAddress)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();

            int padding = 220; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            mMap.animateCamera(cu);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(ParseUser.getCurrentUser().getUsername());  //should use display name instead

        mParentDriverMapLayout = findViewById(R.id.map_coordinator_layout);

        if (getIntent() == null) {
            return;
        } else if (getIntent() != null) {
            Glider glider = getIntent().getParcelableExtra(Constants.EXTRA_GLIDER_DATA);
            mGliderUserName = glider.getmGliderUserName();
            mGliderLatitude = glider.getmGliderLatitude();
            mGliderLongitude = glider.getmGliderLongitude();
            mGliderAddress = glider.getmGliderAddress();

            Driver driver = getIntent().getParcelableExtra(Constants.EXTRA_DRIVER_DATA);
            mDriverLatitude = driver.getmDriverLatitude();
            mDriverLongitude = driver.getmDriverLongitude();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driver_map);
        mapFragment.getMapAsync(this);

        buildGoogleAPIClient();
        mDriverUsername = ParseUser.getCurrentUser().getUsername();
        Query_GliderRequest();
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverMapDialog();
            }
        });

        View rootView = findViewById(R.id.map_coordinator_layout);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(myGlobalLayoutListener);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap == null) return;
        mMap = googleMap;
        mMap.clear();
    }

    private void LogOut() {
        ParseUser.logOut();
        UtilityClass.ClearPreference(getApplicationContext());
        new DatabaseLogoutAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.DELETE_DRIVER_TRANSACTION_DATABASE);
        driverHandler.removeCallbacks(gliderRequestRunnable);
        if (mDialog.isShowing()) mDialog.dismiss();
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
        RedrawMap();
        driverHandler.postDelayed(gliderRequestRunnable, Constants.THREAD_PERIODIC_TIME_10000);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        driverHandler.removeCallbacks(gliderRequestRunnable);
    }


    private void RedrawMap() {
        if (mMap == null) {  //mMap not ready return!!!
            return;
        }
        mMap.clear();
        LatLng driverLocation = null;
        LatLng gliderLocation = null;

        driverLocation = new LatLng(mDriverLatitude, mDriverLongitude);

        gliderLocation = new LatLng(mGliderLatitude, mGliderLongitude);

        ArrayList<Marker> markers = new ArrayList<>();

        markers.add(mMap.addMarker(new MarkerOptions().position(driverLocation).title(getString(R.string.your_location)).rotation(90)));
        markers.add(mMap.addMarker(new MarkerOptions().position(gliderLocation).title(mGliderAddress)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();


        int padding = 220; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu);

    }

    /**
     * ******************************************************************************************
     * <p>
     * Parse Related
     * <p>
     * ******************************************************************************************
     */

    private void Query_GliderRequest() {  //assumption: there can be only one request for a driver.

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_TABLE_GLIDER_REQUEST);
        mDriverUsername = ParseUser.getCurrentUser().getUsername();
        query.whereEqualTo(Constants.PARSE_DRIVER_USERNAME, mDriverUsername);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                mAcceptedMode = objects.size() > 0 && e == null;
            }
        });
    }

    public void AcceptRequest() {
        if (!mAcceptedMode) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_TABLE_GLIDER_REQUEST);
            query.whereEqualTo(Constants.PARSE_GLIDER_USERNAME, mGliderUserName);
            query.whereDoesNotExist(Constants.PARSE_DRIVER_USERNAME);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects == null) {
                        return;
                    }
                    if (e == null && objects.size() > 0) {
                        ParseObject object = objects.get(objects.size() - 1);  //get the last ParseObject
                        object.put(Constants.PARSE_DRIVER_USERNAME, mDriverUsername);
                        object.put(Constants.PARSE_ACCEPTED_REQUEST, true);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    mAcceptedMode = true;
                                    Double driverLatitude = mDriverLatitude;
                                    Double driverLongitude = mDriverLongitude;
                                    Double requestLatitude = mGliderLatitude;
                                    Double requestLongitude = mGliderLongitude;

                                    ParseUser.getCurrentUser().put(Constants.PARSE_LOCATION, new ParseGeoPoint(driverLatitude, driverLongitude));
                                    ParseUser.getCurrentUser().saveInBackground();

                                    Uri directionUri = Uri.parse(Constants.GOOGLE_DIRECTION_URI + driverLatitude + "," + driverLongitude
                                            + "&daddr=" + requestLatitude + "," + requestLongitude);

                                    Intent directionIntent = new Intent(android.content.Intent.ACTION_VIEW, directionUri);

                                    startActivity(directionIntent);

                                }
                            }
                        });
                    } else {  /* cant find glidername */
                        Toast.makeText(getApplicationContext(), getString(R.string.cannot_find_glidername), Toast.LENGTH_LONG).show();
                        finish();

                    }
                }
            });
        } else if (mAcceptedMode) { /* reaches here when app has stopped and later resumed*/
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_TABLE_GLIDER_REQUEST);
            query.whereEqualTo(Constants.PARSE_GLIDER_USERNAME, mGliderUserName);
            query.whereEqualTo(Constants.PARSE_ACCEPTED_REQUEST, true);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects == null) {
                        return;
                    }
                    Double driverLatitude = mDriverLatitude;
                    Double driverLongitude = mDriverLongitude;
                    Double requestLatitude = mGliderLatitude;
                    Double requestLongitude = mGliderLongitude;
                    Uri directionUri = Uri.parse(Constants.GOOGLE_DIRECTION_URI + driverLatitude + "," + driverLongitude
                            + "&daddr=" + requestLatitude + "," + requestLongitude);

                    Intent directionIntent = new Intent(android.content.Intent.ACTION_VIEW, directionUri);
                    startActivity(directionIntent);
                }
            });
        }
    }

    private Handler driverHandler = new Handler();
    private Runnable gliderRequestRunnable = new Runnable() {
        @Override
        public void run() {
            checkForGliderRequestUpdates();
        }
    };

    private synchronized void checkForGliderRequestUpdates() {
        if (ParseUser.getCurrentUser() == null) {
            return;
        }
        mDriverUsername = ParseUser.getCurrentUser().getUsername();
        if (!mAcceptedMode) {
            driverHandler.postDelayed(gliderRequestRunnable, Constants.THREAD_PERIODIC_TIME_10000);
            return;
        } else if (mAcceptedMode) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_TABLE_GLIDER_REQUEST);
            query.whereEqualTo(Constants.PARSE_DRIVER_USERNAME, mDriverUsername);
            query.whereEqualTo(Constants.PARSE_GLIDER_USERNAME, mGliderUserName);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects == null) {
                        return;
                    }
                    if (objects.size() > 0 && e == null) {
                        /* found then do nothing */
                    } else {
                        mAcceptedMode = false;
                        if (mTransactionLayout != null) {
                            mTransactionLayout.setVisibility(View.GONE);
                        }
                        mMap.clear();
                        Snackbar.make(mParentDriverMapLayout, getString(R.string.request_has_been_cancelled), Snackbar.LENGTH_INDEFINITE)
                                .setAction(getString(R.string.ok), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        /* return to previous view request */
                                        finish();
                                    }
                                }).show();
                        return;
                    }
                }
            });
        }

        driverHandler.postDelayed(gliderRequestRunnable, Constants.THREAD_PERIODIC_TIME_10000);

    }


    /**
     * *****************************************************************************************
     * <p>
     * Dialog
     * <p>
     * *****************************************************************************************
     */


    private Dialog mDialog;
    private RelativeLayout mLogoutLayout, mCrossLayout, mAcceptRequestLayout, mTransactionLayout;
    private TextView mAcceptRequestTextView;
    public static final int DRIVER_TRANSACTION = 201;

    private View.OnClickListener myDialogClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!UtilityClass.isNetworkConnected(getApplicationContext())) {
                Snackbar.make(mParentDriverMapLayout, getString(R.string.no_connection), Snackbar.LENGTH_INDEFINITE)
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
                case R.id.accept_request_layout:
                    AcceptRequest();
                    mAcceptRequestTextView.setText(getString(R.string.view_request));
                    mDialog.dismiss();
                    break;
                case R.id.transaction_layout:
                    Intent intentDriverTransaction = new Intent(getApplicationContext(), DriverTransaction.class);
                    startActivityForResult(intentDriverTransaction, DRIVER_TRANSACTION);
                    mDialog.dismiss();
                    overridePendingTransition(R.anim.push_down_in, R.anim.slide_out_left);
                    break;

            }

        }
    };


    private void DriverMapDialog() {
        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);   //before setContentView
        mDialog.setContentView(R.layout.driver_map_dialog_box);
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
        mCrossLayout = (RelativeLayout) dialog.findViewById(R.id.cross_layout);
        mAcceptRequestLayout = (RelativeLayout) dialog.findViewById(R.id.accept_request_layout);
        mAcceptRequestTextView = (TextView) dialog.findViewById(R.id.accept_request_textview);

        mTransactionLayout = (RelativeLayout) dialog.findViewById(R.id.transaction_layout);

        if (mAcceptedMode) {
            mAcceptRequestTextView.setText(getString(R.string.view_request));
            mTransactionLayout.setVisibility(View.VISIBLE);

        } else {
            mAcceptRequestTextView.setText(getString(R.string.accept_request));
            mTransactionLayout.setVisibility(View.GONE);
        }

        mLogoutLayout.setOnClickListener(myDialogClickListener);
        mCrossLayout.setOnClickListener(myDialogClickListener);
        mAcceptRequestLayout.setOnClickListener(myDialogClickListener);
        mTransactionLayout.setOnClickListener(myDialogClickListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DRIVER_TRANSACTION) {
            if (resultCode == RESULT_OK) {
                /* returning from DriverTransaction OK */
                final String local_gliderUsername = mGliderUserName;
                final String local_gliderAddress = mGliderAddress;
                final String local_driverName = mDriverUsername;

                ParseObject saveTransaction = new ParseObject(Constants.PARSE_TABLE_POWER_TRANSACTION);
                saveTransaction.put(Constants.PARSE_DRIVER_USERNAME, mDriverUsername);
                saveTransaction.put(Constants.PARSE_GLIDER_USERNAME, local_gliderUsername);
                saveTransaction.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            ParseQuery<ParseObject> queryDelete = ParseQuery.getQuery(Constants.PARSE_TABLE_GLIDER_REQUEST);
                            queryDelete.whereEqualTo(Constants.PARSE_GLIDER_USERNAME, local_gliderUsername);
                            queryDelete.whereEqualTo(Constants.PARSE_GLIDER_ADDRESS, local_gliderAddress);
                            queryDelete.whereEqualTo(Constants.PARSE_DRIVER_USERNAME, local_driverName);
                            queryDelete.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (objects == null) {
                                        return;
                                    }
                                    if (objects.size() > 0 && e == null) {
                                        for (ParseObject gliderRequestObject : objects) {
                                            gliderRequestObject.deleteInBackground();
                                        }
                                        //Then put the transaction to HistoryRequest
                                        ParseObject historyRequestObject = new ParseObject(Constants.PARSE_TABLE_HISTORY_REQUEST);
                                        historyRequestObject.put(Constants.PARSE_DRIVER_USERNAME, local_driverName);
                                        historyRequestObject.put(Constants.PARSE_GLIDER_PICKED_UP_ADDRESS, local_gliderAddress);
                                        historyRequestObject.put(Constants.PARSE_GLIDER_USERNAME, local_gliderUsername);
                                        historyRequestObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                /* SUCEESS */
                                            }
                                        });
                                        mAcceptedMode = false;
                                        mTransactionLayout.setVisibility(View.GONE);
                                        mMap.clear();
                                        finish();
                                    }
                                }
                            });


                        }

                    }
                });
            }
        }
    }

    private void updateDriverLocation(Location location) {
        if (location == null) {
            return;
        }

        final ParseGeoPoint driverGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

        ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
        queryUser.whereEqualTo(Constants.PARSE_USERNAME, ParseUser.getCurrentUser().getUsername());
        queryUser.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (objects == null) {
                    return;
                }
                if (objects.size() > 0 && e == null) {
                    ParseObject updateUser = objects.get(objects.size() - 1);
                    updateUser.put(Constants.PARSE_LOCATION, driverGeoPoint);
                    updateUser.saveInBackground();
                }
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
    //LocationListener
    @Override
    public void onLocationChanged(Location location) {

        if (UtilityClass.isNetworkConnected(this)) {
            mLastDriverLocation = location;
            mDriverLatitude = location.getLatitude();
            mDriverLongitude = location.getLongitude();
            writeDriverLocation(mLastDriverLocation);
            updateDriverLocation(location);
            RedrawMap();
        } else {
            Snackbar.make(mParentDriverMapLayout, getString(R.string.no_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
        }

    }

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

    private void getLastKnownLocation() {

        if (checkPermission()) {
            mLastDriverLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastDriverLocation != null) {
                writeDriverLocation(mLastDriverLocation);
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

    //ConnectionCallbacks
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastKnownLocation();
    }

    //ConnectionCallbacks
    @Override
    public void onConnectionSuspended(int i) {

    }

    //OnConnectionFailedListener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void writeDriverLocation(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Glider Location");
        if (mMap != null) {
            if (mCurrentDriverMarker != null) mCurrentDriverMarker.remove();

            mCurrentDriverMarker = mMap.addMarker(markerOptions);
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

}
