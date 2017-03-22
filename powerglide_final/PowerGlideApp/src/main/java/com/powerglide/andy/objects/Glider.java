package com.powerglide.andy.objects;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Andy on 12/21/2016.
 */

public class Glider implements Parcelable {
    private String mGliderUserName;   //username = email
    private String mGliderDisplayName;
    private String mGliderAddress;
    private Double mGliderLatitude;
    private Double mGliderLongitude;

    public Glider(String username, String displayName, String address, Double lat, Double lng) {
        mGliderUserName = username;
        mGliderDisplayName = displayName;
        mGliderAddress = address;
        mGliderLatitude = lat;
        mGliderLongitude = lng;
    }

    protected Glider(Parcel in) {
        mGliderUserName = in.readString();
        mGliderDisplayName = in.readString();
        mGliderAddress = in.readString();
        mGliderLatitude = in.readDouble();
        mGliderLongitude = in.readDouble();
    }

    public static final Creator<Glider> CREATOR = new Creator<Glider>() {
        @Override
        public Glider createFromParcel(Parcel in) {
            return new Glider(in);
        }

        @Override
        public Glider[] newArray(int size) {
            return new Glider[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mGliderUserName);
        dest.writeString(mGliderDisplayName);
        dest.writeString(mGliderAddress);
        dest.writeDouble(mGliderLatitude);
        dest.writeDouble(mGliderLongitude);
    }

    public String getmGliderUserName() {
        return mGliderUserName;
    }

    public void setmGliderUserName(String mGliderUserName) {
        this.mGliderUserName = mGliderUserName;
    }

    public String getmGliderDisplayName() {
        return mGliderDisplayName;
    }

    public void setmGliderDisplayName(String mGliderDisplayName) {
        this.mGliderDisplayName = mGliderDisplayName;
    }

    public String getmGliderAddress() {
        return mGliderAddress;
    }

    public void setmGliderAddress(String mGliderAddress) {
        this.mGliderAddress = mGliderAddress;
    }

    public Double getmGliderLatitude() {
        return mGliderLatitude;
    }

    public void setmGliderLatitude(Double mGliderLatitude) {
        this.mGliderLatitude = mGliderLatitude;
    }

    public Double getmGliderLongitude() {
        return mGliderLongitude;
    }

    public void setmGliderLongitude(Double mGliderLongitude) {
        this.mGliderLongitude = mGliderLongitude;
    }


}
