package com.powerglide.andy.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.powerglide.andy.R;
import com.powerglide.andy.database.DatabaseConstants;
import com.powerglide.andy.provider.PowerGlideContract;
import com.powerglide.andy.utility.UtilityClass;

/**
 * Created by Andy on 2/5/2017.
 */

public class DriverTransWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private Cursor mCursor;
    private Context mContext;
    private int mWidgetId;
    public static final String LOG_TAG = DriverTransWidgetFactory.class.getSimpleName();

    public DriverTransWidgetFactory(Context context, Intent intent) {
        mContext = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }
        final long identityToken = Binder.clearCallingIdentity();
        mCursor = mContext.getContentResolver().query(PowerGlideContract.DriverTransaction.CONTENT_URI,
                PowerGlideContract.DriverTransaction.PROJECTION_ALL, null, null, null);
        Binder.restoreCallingIdentity(identityToken);

    }

    @Override
    public void onDestroy() {
        if (mCursor != null)
            mCursor.close();
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.driver_trans_widget_list_content);
        if (mCursor.moveToPosition(position)) {
            String dateString = mCursor.getString(mCursor.getColumnIndex(DatabaseConstants.TABLE_ROW_DATE));
            String formattedDate = UtilityClass.FormattingDate(dateString);

            rv.setTextViewText(R.id.transaction_date, formattedDate);
            rv.setTextViewText(R.id.transaction_rating,
                    mCursor.getString(mCursor.getColumnIndex(DatabaseConstants.TABLE_ROW_RATING)));
            rv.setTextViewText(R.id.transaction_ratedby,
                    mCursor.getString(mCursor.getColumnIndex(DatabaseConstants.TABLE_ROW_GLIDER_USERNAME)));
        }
        return rv;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
