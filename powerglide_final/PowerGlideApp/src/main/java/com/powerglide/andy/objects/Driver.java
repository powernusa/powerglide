package com.powerglide.andy.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Andy on 12/21/2016.
 */

public class Driver implements Parcelable {
    private String mDriverEmail;
    private String mDriverDisplayName;
    private Double mDriverLatitude;
    private Double mDriverLongitude;

    public Driver(String email, String displayName, Double latitude, Double longitude) {
        mDriverEmail = email;
        mDriverDisplayName = displayName;
        mDriverLatitude = latitude;
        mDriverLongitude = longitude;
    }

    protected Driver(Parcel in) {
        mDriverEmail = in.readString();
        mDriverDisplayName = in.readString();
        mDriverLatitude = in.readDouble();
        mDriverLongitude = in.readDouble();
    }

    public static final Creator<Driver> CREATOR = new Creator<Driver>() {
        @Override
        public Driver createFromParcel(Parcel in) {
            return new Driver(in);
        }

        @Override
        public Driver[] newArray(int size) {
            return new Driver[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDriverEmail);
        dest.writeString(mDriverDisplayName);
        dest.writeDouble(mDriverLatitude);
        dest.writeDouble(mDriverLongitude);
    }

    public String getmDriverEmail() {
        return mDriverEmail;
    }

    public void setmDriverEmail(String mDriverEmail) {
        this.mDriverEmail = mDriverEmail;
    }

    public String getmDriverDisplayName() {
        return mDriverDisplayName;
    }

    public void setmDriverDisplayName(String mDriverDisplayName) {
        this.mDriverDisplayName = mDriverDisplayName;
    }

    public Double getmDriverLatitude() {
        return mDriverLatitude;
    }

    public void setmDriverLatitude(Double mDriverLatitude) {
        this.mDriverLatitude = mDriverLatitude;
    }

    public Double getmDriverLongitude() {
        return mDriverLongitude;
    }

    public void setmDriverLongitude(Double mDriverLongitude) {
        this.mDriverLongitude = mDriverLongitude;
    }


}
