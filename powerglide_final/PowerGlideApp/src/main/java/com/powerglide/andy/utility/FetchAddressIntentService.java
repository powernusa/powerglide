package com.powerglide.andy.utility;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

import com.powerglide.andy.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//import com.parse.andy.R;

/**
 * Created by Andy on 12/23/2016.
 */

public class FetchAddressIntentService extends IntentService {
    public static final String LOG_TAG = FetchAddressIntentService.class.getSimpleName();
    private ResultReceiver mResultReceiver;
    private Location mLocation;
    private StringBuilder mStringBuilder; //don't forget to create a new object


    public FetchAddressIntentService() {  //to pacify android manifest
        super(LOG_TAG);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchAddressIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";
        if (intent == null) {
            errorMessage = getString(R.string.intent_is_null);
            return;
        }
        mLocation = intent.getParcelableExtra(Constants.EXTRA_LOCATION_DATA);

        mResultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        if (mLocation == null || mResultReceiver == null) {
            errorMessage = getString(R.string.location_is_null_or_result_receiver_is_null);
            return;
        }


        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        mStringBuilder = new StringBuilder();
        mStringBuilder.setLength(0);   //clear string builder
        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    mLocation.getLatitude(),
                    mLocation.getLongitude(),
                    // In this sample, we get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);

        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
        }

        if (addresses != null && addresses.size() > 0) {   // to prevent app crashes.
            Address address = addresses.get(0);

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                mStringBuilder.append(address.getAddressLine(i));
                mStringBuilder.append('\n');
            }
            mStringBuilder.append(address.getCountryName());

            DeliverAddressToReceiver(Constants.SUCCESS_RESULT, mStringBuilder.toString());
        } else {
            DeliverAddressToReceiver(Constants.FAILURE_RESULT, getString(R.string.geocoder_not_returning_anything));
        }


    }

    private void DeliverAddressToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_RESULT_ADDRESS, message);
        mResultReceiver.send(resultCode, bundle);

    }
}
