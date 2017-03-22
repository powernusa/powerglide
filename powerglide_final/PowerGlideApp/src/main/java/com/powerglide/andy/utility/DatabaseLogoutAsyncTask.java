package com.powerglide.andy.utility;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.powerglide.andy.R;
import com.powerglide.andy.provider.PowerGlideContract;

/**
 * Created by Andy on 3/6/2017.
 */

public class DatabaseLogoutAsyncTask extends AsyncTask<String, Void, Boolean> {
    private Context mContext;
    public static final String LOG_TAG = DatabaseLogoutAsyncTask.class.getSimpleName();

    public DatabaseLogoutAsyncTask(Context context) {
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        if (params[0] != null) {
            if (params[0].equals(Constants.DELETE_ALL_NEARBY_DATABASE)) {
                mContext.getContentResolver().delete(PowerGlideContract.Atm.CONTENT_URI, null, null);
                mContext.getContentResolver().delete(PowerGlideContract.Gas_Station.CONTENT_URI, null, null);
                mContext.getContentResolver().delete(PowerGlideContract.Hotel.CONTENT_URI, null, null);
                mContext.getContentResolver().delete(PowerGlideContract.Parking.CONTENT_URI, null, null);
                mContext.getContentResolver().delete(PowerGlideContract.Pharmacy.CONTENT_URI, null, null);
                mContext.getContentResolver().delete(PowerGlideContract.Restaurant.CONTENT_URI, null, null);

            } else if (params[0].equals(Constants.DELETE_DRIVER_TRANSACTION_DATABASE)) {
                mContext.getContentResolver().delete(PowerGlideContract.DriverTransaction.CONTENT_URI, null, null);

            }
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
            Toast.makeText(mContext, mContext.getString(R.string.database_cleared) + "!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.failure_in_database_deletion), Toast.LENGTH_SHORT).show();
        }
    }
}
