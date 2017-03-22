package com.powerglide.andy.utility;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.powerglide.andy.R;

/**
 * Created by Andy on 2/20/2017.
 */

public class NetworkStatusReceiver extends BroadcastReceiver {
    //public static final String LOG_TAG = NetworkStatusReceiver.class.getSimpleName();
    private static final String EXTRA_NETWORK_INFO = "networkInfo";  // a workaround to deprecated

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        boolean noNetwork = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        String extraMessage = null;

        if (extras != null) {
            String networkInfoString = NetworkStatusReceiver.EXTRA_NETWORK_INFO;
            NetworkInfo netInfo = (NetworkInfo) extras.get(networkInfoString);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                extraMessage = context.getString(R.string.there_is_connection);
            } else if (noNetwork) {
                extraMessage = context.getString(R.string.sorry_no_network);
            }

            Intent networkIntent = new Intent(Constants.ACTION_NETWORK_MESSAGE);
            networkIntent.putExtra(Constants.NETWORK_INFO, extraMessage);
            LocalBroadcastManager.getInstance(context).sendBroadcast(networkIntent);
        }
    }
}
