package com.powerglide.andy.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Andy on 1/31/2017.
 */

public class DriverTransaction implements Parcelable {
    private long mId;
    private String mTransactionDate;
    private String mRating;
    private String mDriverUsername;
    private String mGliderUsername;

    public DriverTransaction(long id, String transactionDate, String rating, String driverName, String gliderName) {
        mId = id;
        mTransactionDate = transactionDate;
        mRating = rating;
        mDriverUsername = driverName;
        mGliderUsername = gliderName;
    }


    protected DriverTransaction(Parcel in) {
        mId = in.readLong();
        mTransactionDate = in.readString();
        mRating = in.readString();
        mDriverUsername = in.readString();
        mGliderUsername = in.readString();
    }

    public static final Creator<DriverTransaction> CREATOR = new Creator<DriverTransaction>() {
        @Override
        public DriverTransaction createFromParcel(Parcel in) {
            return new DriverTransaction(in);
        }

        @Override
        public DriverTransaction[] newArray(int size) {
            return new DriverTransaction[size];
        }
    };

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public String getmTransactionDate() {
        return mTransactionDate;
    }

    public void setmTransactionDate(String mTransactionDate) {
        this.mTransactionDate = mTransactionDate;
    }

    public String getmRating() {
        return mRating;
    }

    public void setmRating(String mRating) {
        this.mRating = mRating;
    }

    public String getmDriverUsername() {
        return mDriverUsername;
    }

    public void setmDriverUsername(String mDriverUsername) {
        this.mDriverUsername = mDriverUsername;
    }

    public String getmGliderUsername() {
        return mGliderUsername;
    }

    public void setmGliderUsername(String mGliderUsername) {
        this.mGliderUsername = mGliderUsername;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mTransactionDate);
        dest.writeString(mRating);
        dest.writeString(mDriverUsername);
        dest.writeString(mGliderUsername);
    }
}
