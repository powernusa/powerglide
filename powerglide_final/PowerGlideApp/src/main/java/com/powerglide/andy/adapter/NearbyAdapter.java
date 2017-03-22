package com.powerglide.andy.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.powerglide.andy.R;
import com.powerglide.andy.provider.PowerGlideContract;
import com.powerglide.andy.utility.Constants;

/**
 * Created by Andy on 2/27/2017.
 */

public class NearbyAdapter extends CursorRecyclerViewAdapter<NearbyAdapter.ViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    public static final String LOG_TAG = NearbyAdapter.class.getSimpleName();

    public NearbyAdapter(Context context, Cursor cursor) {
        super(cursor);
        mContext = context;
        mCursor = cursor;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final Cursor cursor) {
        Double dist = 0.0;
        holder.mName.setText(cursor.getString(cursor.getColumnIndex(PowerGlideContract.COL_NAME)));
        holder.mAddress.setText(cursor.getString(cursor.getColumnIndex(PowerGlideContract.COL_ADDRESS)));
        holder.mDistance.setText(cursor.getString(cursor.getColumnIndex(PowerGlideContract.COL_DISTANCE)));

        dist = Double.parseDouble(cursor.getString(cursor.getColumnIndex(PowerGlideContract.COL_DISTANCE)));
        dist = dist / 1000;
        dist = round(dist, 2);
        String distance_km = dist + " km";
        holder.mDistance.setText(distance_km);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        final double currentGliderLatitude = Double.parseDouble(sp.getString(Constants.GLIDER_LATITUDE, "0"));
        final double currentGliderLongitude = Double.parseDouble(sp.getString(Constants.GLIDER_LONGITUDE, "0"));

        holder.mDirectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = Constants.GOOGLE_DIRECTION_URI + currentGliderLatitude + "," + currentGliderLongitude
                        + "&daddr=" + cursor.getString(cursor.getColumnIndex(PowerGlideContract.COL_LAT))
                        + "," + cursor.getString(cursor.getColumnIndex(PowerGlideContract.COL_LON));

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mContext.startActivity(intent);

            }
        });
        if (cursor.getString(cursor.getColumnIndex(PowerGlideContract.COL_OPEN_NOW)) != null) {
            if (cursor.getString(cursor.getColumnIndex(PowerGlideContract.COL_OPEN_NOW)).equals("true")) {
                holder.mTiming.setText(mContext.getString(R.string.open_now));
            }

        } else {
            holder.mTiming.setText(mContext.getString(R.string.closed));
        }


        holder.mRatings.setRating(cursor.getFloat(cursor.getColumnIndex(PowerGlideContract.COL_RATING)));


    }

    private static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.list_nearby_item, parent, false);

        return new ViewHolder(rootView);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    /**
     * ************************************************************************************
     * <p>
     * ViewHolder
     * <p>
     * ************************************************************************************
     */

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private RatingBar mRatings;
        private TextView mAddress;
        private TextView mTiming;
        private Button mDirectionBtn;
        private TextView mDistance;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name_tv);
            mRatings = (RatingBar) itemView.findViewById(R.id.ratings);
            mAddress = (TextView) itemView.findViewById(R.id.address_tv);
            mTiming = (TextView) itemView.findViewById(R.id.timing_tv);
            mDirectionBtn = (Button) itemView.findViewById(R.id.direction_btn);
            mDistance = (TextView) itemView.findViewById(R.id.distance_tv);

        }
    }
}
