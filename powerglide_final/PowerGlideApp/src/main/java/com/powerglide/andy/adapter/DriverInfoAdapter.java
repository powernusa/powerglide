package com.powerglide.andy.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.powerglide.andy.R;
import com.powerglide.andy.database.DatabaseConstants;

/**
 * Created by Andy on 2/1/2017.
 */
/* Driver Transaction */
public class DriverInfoAdapter extends CursorRecyclerViewAdapter<DriverInfoAdapter.TransactionViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    public static final String LOG_TAG = DriverInfoAdapter.class.getSimpleName();


    public DriverInfoAdapter(Context context, Cursor c) {
        super(c);
        mContext = context;
        mCursor = c;
    }


    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.driver_transaction_detail, parent, false);
        TransactionViewHolder viewHolder = new TransactionViewHolder(rootView);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder holder, Cursor cursor) {
        holder.mDate.setText(cursor.getString(cursor.getColumnIndex(DatabaseConstants.TABLE_ROW_DATE)));
        holder.mDriverDisplayName.setText(cursor.getString(cursor.getColumnIndex(DatabaseConstants.TABLE_ROW_DRIVER_USERNAME)));
        holder.mRating.setText(cursor.getString(cursor.getColumnIndex(DatabaseConstants.TABLE_ROW_RATING)));
        holder.mRatedBy.setText(cursor.getString(cursor.getColumnIndex(DatabaseConstants.TABLE_ROW_GLIDER_USERNAME)));

    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {
        private View mRootView;
        private TextView mDriverDisplayName;
        private TextView mRating;
        private TextView mRatedBy;
        private TextView mDate;


        public TransactionViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            mDriverDisplayName = (TextView) itemView.findViewById(R.id.driver_displayname_tv);
            mRating = (TextView) itemView.findViewById(R.id.driver_rating_tv);
            mRatedBy = (TextView) itemView.findViewById(R.id.rated_by_tv);
            mDate = (TextView) itemView.findViewById(R.id.date_tv);
        }
    }
}
