package com.powerglide.andy.otheractivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.powerglide.andy.R;
import com.powerglide.andy.adapter.DriverInfoAdapter;
import com.powerglide.andy.database.DatabaseConstants;
import com.powerglide.andy.provider.PowerGlideContract;
import com.powerglide.andy.utility.Constants;
import com.powerglide.andy.utility.UtilityClass;

import java.util.Date;
import java.util.List;

/* Displaying Power Transaction*/
public class DriverInfoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = DriverInfoActivity.class.getSimpleName();

    private RecyclerView mTransactionList;
    private View mInfoParentLayout;
    public static final int CURSOR_LOADER = 0;
    private DriverInfoAdapter mDriverInfoAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mNoDataLayout;
    private ProgressBar mProgressBar;

    private SwipeRefreshLayout.OnRefreshListener mSwipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mProgressBar.setVisibility(View.GONE);
            mNoDataLayout.setVisibility(View.GONE);
            RefreshLayout();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.ratings_for) + ": " + ParseUser.getCurrentUser().getUsername());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        bindViews();
        if (UtilityClass.isNetworkConnected(this)) {
            clearData();
            loadingData();
        }


        if (!UtilityClass.isNetworkConnected(this)) {
            Snackbar.make(mInfoParentLayout, getString(R.string.loading_from_local_database), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
        }
        getSupportLoaderManager().initLoader(CURSOR_LOADER, null, this);

    }

    private void RefreshLayout() {
        if (UtilityClass.isNetworkConnected(this)) {
            clearData();
            loadingData();

        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            getSupportLoaderManager().restartLoader(CURSOR_LOADER, null, this);

            Snackbar.make(mInfoParentLayout, getString(R.string.loading_from_local_database), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
        }

    }

    private void bindViews() {
        mTransactionList = (RecyclerView) findViewById(R.id.driver_transaction_list);
        mInfoParentLayout = findViewById(R.id.info_coordinator_layout);
        mDriverInfoAdapter = new DriverInfoAdapter(this, null);
        mTransactionList.setLayoutManager(new LinearLayoutManager(this));
        mTransactionList.setAdapter(mDriverInfoAdapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.info_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(mSwipeRefreshListener);
        mNoDataLayout = findViewById(R.id.no_data_layout);
        mNoDataLayout.setVisibility(View.GONE);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

    }


    private void loadingData() {
        mProgressBar.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> queryPowerTransaction = ParseQuery.getQuery(Constants.PARSE_TABLE_POWER_TRANSACTION);
        queryPowerTransaction.whereEqualTo(Constants.PARSE_DRIVER_USERNAME, ParseUser.getCurrentUser().getUsername());
        queryPowerTransaction.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects == null) {
                    return;
                }
                if (objects.size() > 0 && e == null) {
                    for (ParseObject object : objects) {
                        Date dateString = object.getCreatedAt();
                        long ratingString = object.getLong(Constants.PARSE_RATING);
                        String gliderUsername = object.getString(Constants.PARSE_GLIDER_USERNAME);
                        String driverUsername = object.getString(Constants.PARSE_DRIVER_USERNAME);

                        ContentValues cv = new ContentValues();

                        cv.put(DatabaseConstants.TABLE_ROW_DATE, dateString.toString());
                        cv.put(DatabaseConstants.TABLE_ROW_DRIVER_USERNAME, driverUsername);
                        cv.put(DatabaseConstants.TABLE_ROW_GLIDER_USERNAME, gliderUsername);
                        cv.put(DatabaseConstants.TABLE_ROW_RATING, Long.toString(ratingString));

                        getContentResolver().insert(PowerGlideContract.DriverTransaction.CONTENT_URI, cv);

                    }

                    mNoDataLayout.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);

                } else { // no data returned

                    mNoDataLayout.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }


    private void clearData() {
        getContentResolver().delete(PowerGlideContract.DriverTransaction.CONTENT_URI, null, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(CURSOR_LOADER, null, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getSupportLoaderManager().destroyLoader(CURSOR_LOADER);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CURSOR_LOADER:
                return new CursorLoader(this,
                        PowerGlideContract.DriverTransaction.CONTENT_URI,
                        PowerGlideContract.DriverTransaction.PROJECTION_ALL,
                        null,
                        null,
                        null);
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null) {
            mDriverInfoAdapter.swapCursor(data);
            if (data.getCount() == 0) {
                mNoDataLayout.setVisibility(View.VISIBLE);
            }
        }
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDriverInfoAdapter.swapCursor(null);

    }
}
