package com.powerglide.andy.nearby;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.powerglide.andy.R;
import com.powerglide.andy.utility.Constants;
import com.powerglide.andy.utility.NearbyActivityFragmentListener;
import com.powerglide.andy.utility.UtilityClass;

public class NearbyActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, NearbyActivityFragmentListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastGliderLocation;
    private LocationRequest mLocationRequest;
    private View mNearbyParentLayout;
    private TextView mChooseItemTV;

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_NETWORK_MESSAGE)) {
                String msg = intent.getStringExtra(Constants.NETWORK_INFO);
                if (msg.equals(getString(R.string.sorry_no_network))) {
                    Snackbar.make(mNearbyParentLayout, msg, Snackbar.LENGTH_INDEFINITE)
                            .setAction(getString(R.string.ok_try_later), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                } else if (msg.equals(getString(R.string.there_is_connection))) {
                    Snackbar.make(mNearbyParentLayout, msg, Snackbar.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //ActionBar ab = getSupportActionBar();
        mNearbyParentLayout = findViewById(R.id.nearby_parent_layout);
        mChooseItemTV = (TextView) findViewById(R.id.choose_items_tv);
        mChooseItemTV.setVisibility(View.VISIBLE);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NearbyDialog();
            }
        });

        buildGoogleAPIClient();
        RegisterReceiver();
    }

    private void RegisterReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_NETWORK_MESSAGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(myBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * Listeners
     */
    private boolean checkPermission() {

        return (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

    }

    private void buildGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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

    private double mGliderLatitude = 0;
    private double mGliderLongitude = 0;

    //LocationListner
    @Override
    public void onLocationChanged(Location location) {
        mLastGliderLocation = location;
        saveCurrentGliderLocation(mLastGliderLocation);
        mGliderLatitude = mLastGliderLocation.getLatitude();
        mGliderLongitude = mLastGliderLocation.getLongitude();

    }

    private void saveCurrentGliderLocation(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.GLIDER_LATITUDE, Double.toString(currentLatitude));
        editor.putString(Constants.GLIDER_LONGITUDE, Double.toString(currentLongitude));
        editor.commit();

    }

    /**
     *
     */

    public static final int REQUEST_ACCESS_FINE_PERMISSION = 1;

    private void askPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ACCESS_FINE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getLastKnownLocation();

            } else {
                // Permission denied
            }
        }

    }

    private void getLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setInterval(Constants.LOCATION_REQUEST_INTERVAL_10000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (checkPermission()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    private void getLastKnownLocation() {
        if (checkPermission()) {
            mLastGliderLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastGliderLocation != null) {
                getLocationRequest();
            } else {
                getLocationRequest();
            }
        } else
            askPermission();

    }

    /**
     * ********************************************************************************************
     * Dialog
     * <p>
     * *******************************************************************************************
     */
    private FloatingActionButton mFab;
    private Dialog mDialog;
    private RelativeLayout mAtmLayout, mGasStationLayout, mHotelsLayout, mParkingLayout,
            mPharmacyLayout, mRestaurantLayout, mCrossLayout;

    private View.OnClickListener myDialogClickListener = new View.OnClickListener() {


        @Override
        public void onClick(View v) {
            String typeString = null;
            Class fragmentClass = null;
            Fragment newFragment = null;
            if (mGliderLatitude == 0 && mGliderLongitude == 0) {
                Snackbar.make(mNearbyParentLayout, getString(R.string.switch_on_gps), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
            }
            switch (v.getId()) {
                case R.id.cross_layout:
                    mDialog.dismiss();
                    return;
                case R.id.atm_layout:
                    fragmentClass = AtmFragment.class;
                    typeString = getString(R.string.atm);
                    mDialog.dismiss();
                    break;
                case R.id.gas_station_layout:
                    fragmentClass = GasStationFragment.class;
                    typeString = getString(R.string.gas_station);
                    mDialog.dismiss();
                    break;
                case R.id.hotels_layout:
                    fragmentClass = HotelFragment.class;
                    typeString = getString(R.string.hotels);
                    mDialog.dismiss();
                    break;
                case R.id.parking_layout:
                    fragmentClass = ParkingFragment.class;
                    typeString = getString(R.string.parking);
                    mDialog.dismiss();
                    break;

                case R.id.pharmacy_layout:
                    typeString = getString(R.string.pharmacy);
                    fragmentClass = PharmacyFragment.class;
                    mDialog.dismiss();
                    break;
                case R.id.restaurant_layout:
                    typeString = getString(R.string.restaurant);
                    fragmentClass = RestaurantFragment.class;
                    mDialog.dismiss();
                    break;
            }
            try {
                newFragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if ((mLastGliderLocation != null) && (mGliderLatitude != 0 && mGliderLongitude != 0)) {
                NearbyService nearByService = new NearbyService(getApplicationContext(), "" + mGliderLatitude + "," + mGliderLongitude);
                nearByService.execute(typeString);
                FragmentManager fm = getSupportFragmentManager();
                ViewGroup fmContainer = (ViewGroup) findViewById(R.id.fragment);
                fmContainer.removeAllViews();
                fm.beginTransaction().replace(R.id.fragment, newFragment).commit();
            } else {
                //Toast.makeText(getApplicationContext(), "Location not known!", Toast.LENGTH_LONG).show();
            }


        }
    };

    private void NearbyDialog() {
        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);   //before setContentView
        mDialog.setContentView(R.layout.nearby_places_dialog);

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
        mCrossLayout = (RelativeLayout) dialog.findViewById(R.id.cross_layout);
        mAtmLayout = (RelativeLayout) dialog.findViewById(R.id.atm_layout);
        mGasStationLayout = (RelativeLayout) dialog.findViewById(R.id.gas_station_layout);
        mHotelsLayout = (RelativeLayout) dialog.findViewById(R.id.hotels_layout);
        mParkingLayout = (RelativeLayout) dialog.findViewById(R.id.parking_layout);
        mPharmacyLayout = (RelativeLayout) dialog.findViewById(R.id.pharmacy_layout);
        mRestaurantLayout = (RelativeLayout) dialog.findViewById(R.id.restaurant_layout);

        mCrossLayout.setOnClickListener(myDialogClickListener);
        mAtmLayout.setOnClickListener(myDialogClickListener);
        mGasStationLayout.setOnClickListener(myDialogClickListener);
        mHotelsLayout.setOnClickListener(myDialogClickListener);
        mParkingLayout.setOnClickListener(myDialogClickListener);
        mPharmacyLayout.setOnClickListener(myDialogClickListener);
        mRestaurantLayout.setOnClickListener(myDialogClickListener);

    }

    @Override
    public void ShowChooseMenuItems(boolean toShow) {
        if (!toShow) {
            mChooseItemTV.setVisibility(View.GONE);
        } else {
            mChooseItemTV.setVisibility(View.VISIBLE);
        }

    }
}
