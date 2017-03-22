package com.powerglide.andy;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.powerglide.andy.adapter.RequestDetailAdapter;
import com.powerglide.andy.objects.Driver;
import com.powerglide.andy.objects.Glider;
import com.powerglide.andy.utility.Constants;
import com.powerglide.andy.utility.UtilityClass;
import com.powerglide.andy.utility.ViewRequestFragmentListener;

import java.util.ArrayList;
import java.util.List;


public class ViewRequestsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastDriverLocation;
    private LocationRequest mLocationRequest;
    public static final String LOG_TAG = ViewRequestsFragment.class.getSimpleName();

    private RecyclerView mRequestRecyclerView;
    private RequestDetailAdapter mRequestDetailAdapter;


    private ArrayList<String> mRequestList = new ArrayList<String>();
    private ArrayList<String> mRequestAddressList = new ArrayList<String>();
    private ArrayList<ParseGeoPoint> mRequestGeoPoint = new ArrayList<ParseGeoPoint>();
    private ArrayList<String> mRequestDisplayName = new ArrayList<String>();
    private ArrayList<String> mRequestUserName = new ArrayList<String>();
    private ViewRequestFragmentListener mViewRequestFragmentListener;
    private SwipeRefreshLayout mViewRequestSwipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener mViewRequestSwipeListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (UtilityClass.isNetworkConnected(getActivity())) {
                UpdateRequestView();
            } else {
                mViewRequestFragmentListener.PrintSnackBarNoConnectionMessage(null, getString(R.string.no_connection));
                mViewRequestSwipeRefreshLayout.setRefreshing(false);
                mViewRequestFragmentListener.ShowProgressBar(false);

                mRequestList.clear();
                mRequestAddressList.clear();
                mRequestGeoPoint.clear();
                mRequestDisplayName.clear();
                mRequestUserName.clear();
                mRequestList.add(getString(R.string.no_network));
                mRequestDetailAdapter.notifyDataSetChanged();
            }

        }
    };

    public ViewRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Query_GliderRequest();

        View view = inflater.inflate(R.layout.fragment_view_requests, container, false);
        mRequestRecyclerView = (RecyclerView) view.findViewById(R.id.request_recycler_view);
        mRequestDetailAdapter = new RequestDetailAdapter(getActivity(), mRequestList, mRequestAddressList,
                mRequestGeoPoint, mRequestDisplayName, mRequestUserName, mViewRequestFragmentListener);
        mRequestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRequestRecyclerView.setAdapter(mRequestDetailAdapter);


        mRequestList.clear();
        mRequestAddressList.clear();
        mRequestDetailAdapter.notifyDataSetChanged();
        mViewRequestSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.view_request_swipe_refresh);
        mViewRequestSwipeRefreshLayout.setOnRefreshListener(mViewRequestSwipeListener);

        buildGoogleAPIClient();
        return view;
    }

    private void UpdateRequestView() {
        if (mLastDriverLocation == null) {
            return;
        } else {
            final ParseGeoPoint driverCurrentLocation = new ParseGeoPoint(mLastDriverLocation.getLatitude(),
                    mLastDriverLocation.getLongitude());
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_TABLE_GLIDER_REQUEST);
            query.whereDoesNotExist(Constants.PARSE_DRIVER_USERNAME);   //not the same as driverName
            query.whereNear(Constants.PARSE_GLIDER_LOCATION, driverCurrentLocation);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects == null) {
                        return;
                    }
                    if (objects.size() > 0 && e == null) {
                        if (mLastDriverLocation != null) {
                            mRequestList.clear();
                            mRequestAddressList.clear();
                            mRequestGeoPoint.clear();
                            mRequestDisplayName.clear();
                            mRequestUserName.clear();
                            for (ParseObject object : objects) {
                                ParseGeoPoint gliderGeoPoint = object.getParseGeoPoint(Constants.PARSE_GLIDER_LOCATION);
                                String gliderDisplayName = object.getString(Constants.PARSE_GLIDER_DISPLAY_NAME);
                                String gliderUserName = object.getString(Constants.PARSE_GLIDER_USERNAME);
                                Double distanceInKM = driverCurrentLocation.distanceInKilometersTo(gliderGeoPoint);
                                Double distanceRound = (double) Math.round(distanceInKM * 10) / 10;

                                //GET glider address
                                String gliderAddress = object.get(Constants.PARSE_GLIDER_ADDRESS).toString();
                                mRequestAddressList.add(gliderAddress);
                                mRequestList.add(distanceRound + " " + getString(R.string.km_away) + ".");
                                mRequestGeoPoint.add(gliderGeoPoint);
                                mRequestDisplayName.add(gliderDisplayName);
                                mRequestUserName.add(gliderUserName);
                            }
                            mRequestDetailAdapter.notifyDataSetChanged();
                            mViewRequestSwipeRefreshLayout.setRefreshing(false);
                            mViewRequestFragmentListener.ShowProgressBar(false);
                        }
                    } else {  //no data returned
                        mViewRequestFragmentListener.ShowProgressBar(false);
                        mViewRequestSwipeRefreshLayout.setRefreshing(false);
                        mRequestList.clear();
                        mRequestAddressList.clear();
                        mRequestGeoPoint.clear();
                        mRequestDisplayName.clear();
                        mRequestUserName.clear();
                        mRequestList.add(getString(R.string.no_data_returned));
                        mRequestDetailAdapter.notifyDataSetChanged();
                    }
                }
            });

        }
    }

    /* Assumption: there can be only one request for a driver */
    private void Query_GliderRequest() {
        if (mLastDriverLocation != null) {
            if (ParseUser.getCurrentUser() == null) {
                return;
            }
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_TABLE_GLIDER_REQUEST);
            query.whereEqualTo(Constants.PARSE_DRIVER_USERNAME, ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects == null) {
                        return;
                    }
                    if (objects.size() > 0 && e == null) {
                        String gliderUsername = objects.get(0).getString(Constants.PARSE_GLIDER_USERNAME);
                        String gliderDisplayName = objects.get(0).getString(Constants.PARSE_GLIDER_DISPLAY_NAME);
                        ParseGeoPoint driverGeoPoint = objects.get(0).getParseGeoPoint(Constants.PARSE_GLIDER_LOCATION);
                        String gliderAddress = objects.get(0).getString(Constants.PARSE_GLIDER_ADDRESS);
                        Glider glider = new Glider(gliderUsername, gliderDisplayName, gliderAddress,
                                driverGeoPoint.getLatitude(), driverGeoPoint.getLongitude());
                        Driver driver = new Driver(null, null, mLastDriverLocation.getLatitude(), mLastDriverLocation.getLongitude());
                        Intent driverIntent = new Intent(getActivity(), DriverMapActivity.class);
                        driverIntent.putExtra(Constants.EXTRA_GLIDER_DATA, glider);
                        driverIntent.putExtra(Constants.EXTRA_DRIVER_DATA, driver);
                        startActivity(driverIntent);
                    }
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        UpdateRequestView();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ViewRequestFragmentListener) {
            mViewRequestFragmentListener = (ViewRequestFragmentListener) context;

        }
    }

    /**
     * *******************************************************************************************
     * <p>
     * Permission , Locations
     * <p>
     * ******************************************************************************************
     */

    //LocationListener
    @Override
    public void onLocationChanged(Location location) {
        if (UtilityClass.isNetworkConnected(getActivity())) {
            mLastDriverLocation = location;   //update location
            mRequestDetailAdapter.setDriverCurrentLocation(location);
            Query_GliderRequest();
            UpdateRequestView();
        } else {
            mViewRequestFragmentListener.PrintSnackBarNoConnectionMessage(null, getString(R.string.no_connection));
            mViewRequestFragmentListener.ShowProgressBar(false);
            mViewRequestSwipeRefreshLayout.setRefreshing(false);

            mRequestList.clear();
            mRequestAddressList.clear();
            mRequestGeoPoint.clear();
            mRequestDisplayName.clear();
            mRequestUserName.clear();
            mRequestList.add(getString(R.string.no_network));
            mRequestDetailAdapter.notifyDataSetChanged();
        }

    }

    private boolean checkPermission() {

        return (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

    }

    private void buildGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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


    private void getLastKnownLocation() {
        if (checkPermission()) {
            mLastDriverLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastDriverLocation != null) {
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

    private void askPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_ACCESS_FINE_PERMISSION);
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
