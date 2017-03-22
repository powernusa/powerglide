package com.powerglide.andy;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.parse.ParseUser;
import com.powerglide.andy.otheractivity.DriverInfoActivity;
import com.powerglide.andy.utility.Constants;
import com.powerglide.andy.utility.DatabaseLogoutAsyncTask;
import com.powerglide.andy.utility.UtilityClass;
import com.powerglide.andy.utility.ViewRequestFragmentListener;

public class ViewRequestsActivity extends AppCompatActivity
        implements ViewRequestFragmentListener {

    private FloatingActionButton mFab;
    private String mDisplayName;
    private View mViewRequestParentLayout;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);
        mViewRequestParentLayout = findViewById(R.id.view_request_coordinator_layout);

        if (savedInstanceState != null) {
            /*doesn't always work*/
            mDisplayName = savedInstanceState.getString(Constants.DISPLAY_NAME);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.view_request_toolbar);
        setSupportActionBar(toolbar);

        if (getIntent() != null) {
            mDisplayName = getIntent().getStringExtra(Constants.EXTRA_DISPLAY_NAME);
            getSupportActionBar().setTitle(mDisplayName);
        }
        mProgressBar = (ProgressBar) findViewById(R.id.view_request_progress_bar);
        mFab = (FloatingActionButton) findViewById(R.id.view_request_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverDialog();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        /* doesn't always work */
        super.onSaveInstanceState(outState);
        String displayName = mDisplayName;
        outState.putString(Constants.DISPLAY_NAME, displayName);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {

        super.onStop();
        UtilityClass.SaveToPreference(this, Constants.DRIVER_DISPLAY_NAME, mDisplayName);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mDisplayName == null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            mDisplayName = sp.getString(Constants.DRIVER_DISPLAY_NAME, "PowerGlide");
        }

        getSupportActionBar().setTitle(mDisplayName);
    }


    /**
     * *****************************************************************************************
     * <p>
     * Dialog
     * <p>
     * *****************************************************************************************
     */


    private Dialog mDialog;
    private RelativeLayout mLogoutLayout, mCrossLayout, mInfoLayout;

    private View.OnClickListener myDialogClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cross_layout:
                    mDialog.dismiss();
                    break;
                case R.id.logout_layout:
                    mDialog.dismiss();
                    LogOut();
                    break;
                case R.id.info_layout:
                    Intent intentDriverInfo = new Intent(getApplicationContext(), DriverInfoActivity.class);
                    startActivity(intentDriverInfo);
                    mDialog.dismiss();
                    break;

            }

        }
    };

    private void DriverDialog() {
        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);   //before setContentView
        mDialog.setContentView(R.layout.driver_dialog_box);
        initializeDialogWidget(mDialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (UtilityClass.isRTL(this)) {
            params.gravity = Gravity.BOTTOM | Gravity.START;
        } else {
            params.gravity = Gravity.BOTTOM | Gravity.END;
        }

        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }

    private void initializeDialogWidget(Dialog dialog) {
        mLogoutLayout = (RelativeLayout) dialog.findViewById(R.id.logout_layout);
        mCrossLayout = (RelativeLayout) dialog.findViewById(R.id.cross_layout);
        mInfoLayout = (RelativeLayout) dialog.findViewById(R.id.info_layout);

        mLogoutLayout.setOnClickListener(myDialogClickListener);
        mCrossLayout.setOnClickListener(myDialogClickListener);
        mInfoLayout.setOnClickListener(myDialogClickListener);


    }

    private void LogOut() {
        if (UtilityClass.isNetworkConnected(this)) {
            new DatabaseLogoutAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Constants.DELETE_DRIVER_TRANSACTION_DATABASE);
            mDialog.dismiss();   //preventing leak
            ParseUser.logOut();
            UtilityClass.ClearPreference(this);
            if (mDialog.isShowing()) mDialog.dismiss();
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            overridePendingTransition(R.anim.push_down_in, R.anim.slide_out_right);
        } else {
            PrintSnackBarNoConnectionMessage(null, getString(R.string.no_connection_cannot_logout));
        }

    }

    @Override
    public void PrintSnackBarNoConnectionMessage(View parentView, String msg) {
        Snackbar.make(mViewRequestParentLayout, msg, Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }

    @Override
    public void ShowProgressBar(boolean toShow) {
        if (toShow) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
