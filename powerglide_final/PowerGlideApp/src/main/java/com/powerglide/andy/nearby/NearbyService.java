package com.powerglide.andy.nearby;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.powerglide.andy.R;
import com.powerglide.andy.utility.UtilityClass;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Andy on 2/27/2017.
 */

public class NearbyService extends AsyncTask<String, Void, Void> {

    private Context mContext;
    private String mLatLong;
    public static final String LOG_TAG = NearbyService.class.getSimpleName();

    public NearbyService(Context context, String latlong) {
        mContext = context;
        mLatLong = latlong;
    }


    @Override
    protected Void doInBackground(String... params) {
        String type = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        final String API_KEY = mContext.getResources().getString(R.string.google_maps_key);

        String URL = mContext.getResources().getString(R.string.nearby_base_url) + mLatLong + "&"
                + (mContext.getResources().getString(R.string.radius_url)) + "5000" + "&"
                + (mContext.getResources().getString(R.string.key_url)) + API_KEY;

        if (!type.equals("")) {
            URL = mContext.getResources().getString(R.string.nearby_base_url) + mLatLong + "&"
                    + (mContext.getResources().getString(R.string.radius_url)) + "5000" + "&"
                    + (mContext.getResources().getString(R.string.type_url)) + (type) + "&"
                    + (mContext.getResources().getString(R.string.key_url)) + API_KEY;
        }

        Uri builtNearByUri = Uri.parse(URL).buildUpon().build();

        try {
            URL url = new URL(builtNearByUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            }

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }

            UtilityClass utils = new UtilityClass();

            String[] arrLat = mLatLong.split(",");

            Double lat = Double.parseDouble(arrLat[0]);
            Double lon = Double.parseDouble(arrLat[1]);

            utils.parseNearByJSON(mContext, stringBuffer.toString(), lat, lon, type);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }


        return null;
    }
}
