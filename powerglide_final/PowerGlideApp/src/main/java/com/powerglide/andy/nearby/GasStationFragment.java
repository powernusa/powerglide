package com.powerglide.andy.nearby;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.powerglide.andy.R;
import com.powerglide.andy.adapter.NearbyAdapter;
import com.powerglide.andy.provider.PowerGlideContract;
import com.powerglide.andy.utility.NearbyActivityFragmentListener;

/**
 * Created by Andy on 2/28/2017.
 */

public class GasStationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView mRecyclerView;
    private NearbyAdapter mNearbyAdapter;
    public static final int GAS_STATION_LOADER = 201;
    public static final String LOG_TAG = GasStationFragment.class.getSimpleName();
    private ProgressBar mNearbyProgressBar;
    private TextView mEmptyDataTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mNearbyProgressBar = (ProgressBar) view.findViewById(R.id.nearby_progressbar);
        mNearbyProgressBar.setVisibility(View.VISIBLE);
        mEmptyDataTV = (TextView) view.findViewById(R.id.nearby_empty_tv);
        mEmptyDataTV.setVisibility(View.GONE);

        mNearbyAdapter = new NearbyAdapter(getContext(), null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mNearbyAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(GAS_STATION_LOADER, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(GAS_STATION_LOADER, null, this);
        mNearbyProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getLoaderManager().destroyLoader(GAS_STATION_LOADER);
    }

    private NearbyActivityFragmentListener mNearbyActivityFragmentListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NearbyActivityFragmentListener) {
            mNearbyActivityFragmentListener = (NearbyActivityFragmentListener) context;
        }
    }

    /**
     * *******************************************************************************************
     * <p>
     * LoaderManager
     * <p>
     * ******************************************************************************************
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mNearbyProgressBar.setVisibility(View.VISIBLE);
        return new CursorLoader(getContext(),
                PowerGlideContract.Gas_Station.CONTENT_URI,
                null, null, null, PowerGlideContract.COL_DISTANCE + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() == 0) {
            mNearbyProgressBar.setVisibility(View.GONE);
            mEmptyDataTV.setVisibility(View.VISIBLE);
        } else if (data != null) {
            mNearbyAdapter.swapCursor(data);
            mNearbyProgressBar.setVisibility(View.GONE);
            mEmptyDataTV.setVisibility(View.GONE);
            mNearbyActivityFragmentListener.ShowChooseMenuItems(false);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNearbyAdapter.swapCursor(null);
    }
}
