package com.powerglide.andy.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseGeoPoint;
import com.powerglide.andy.DriverMapActivity;
import com.powerglide.andy.R;
import com.powerglide.andy.objects.Driver;
import com.powerglide.andy.objects.Glider;
import com.powerglide.andy.utility.Constants;
import com.powerglide.andy.utility.ViewRequestFragmentListener;
import com.powerglide.andy.utility.UtilityClass;

import java.util.ArrayList;


/**
 * Created by Andy on 12/22/2016.
 */

public class RequestDetailAdapter extends RecyclerView.Adapter<RequestDetailAdapter.RequestViewHolder> {
    private Context mContext;
    private ArrayList<String> mRequests;
    private ArrayList<String> mAddressRequests;
    private ArrayList<ParseGeoPoint> mRequestGeoPoint;
    private ArrayList<String> mRequestDisplayName;
    private ArrayList<String> mRequestUserName;
    private Location mCurrentDriverLocation;
    private ViewRequestFragmentListener mSnackbarListener;

    public RequestDetailAdapter(Context context, ArrayList<String> requests, ArrayList<String> addressRequests,
                                ArrayList<ParseGeoPoint> pointRequest, ArrayList<String> requestDisplayName,
                                ArrayList<String> requestUserName, ViewRequestFragmentListener snackBarListener) {
        mContext = context;
        mRequests = requests;
        mAddressRequests = addressRequests;
        mRequestGeoPoint = pointRequest;
        mRequestDisplayName = requestDisplayName;
        mRequestUserName = requestUserName;
        mSnackbarListener = snackBarListener;
    }

    public void setDriverCurrentLocation(Location driverGeoPoint) {
        mCurrentDriverLocation = driverGeoPoint;
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.view_request_item, parent, false);
        return new RequestViewHolder(rootView);
    }


    @Override
    public void onBindViewHolder(RequestViewHolder holder, int position) {
        String detail = mRequests.get(position);
        String address = "";
        if (mAddressRequests.size() != 0) {
            address = mAddressRequests.get(position);
        }
        holder.mDistanceTextView.setText(detail);
        holder.mAddressTextView.setText(address);


    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View mRootView;
        private TextView mDistanceTextView;
        private TextView mAddressTextView;

        public RequestViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            mDistanceTextView = (TextView) mRootView.findViewById(R.id.distance_textview);
            mAddressTextView = (TextView) mRootView.findViewById(R.id.address_textview);
            mRootView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (!UtilityClass.isNetworkConnected(mContext)) {
                mSnackbarListener.PrintSnackBarNoConnectionMessage(null, mContext.getString(R.string.no_connection));
                return;
            }
            Double gliderLatitude = mRequestGeoPoint.get(getAdapterPosition()).getLatitude();
            Double gliderLongitude = mRequestGeoPoint.get(getAdapterPosition()).getLongitude();
            String displayName = mRequestDisplayName.get(getAdapterPosition());
            String address = mAddressRequests.get(getAdapterPosition());
            String userName = mRequestUserName.get(getAdapterPosition());

            Glider glider = new Glider(userName, displayName, address, gliderLatitude, gliderLongitude);
            Driver driver = new Driver(null, null, mCurrentDriverLocation.getLatitude(), mCurrentDriverLocation.getLongitude());

            Intent driverIntent = new Intent(mRootView.getContext(), DriverMapActivity.class);
            driverIntent.putExtra(Constants.EXTRA_GLIDER_DATA, glider);
            driverIntent.putExtra(Constants.EXTRA_DRIVER_DATA, driver);
            mRootView.getContext().startActivity(driverIntent);
        }
    }
}
