package com.powerglide.andy.otheractivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.powerglide.andy.R;
import com.powerglide.andy.utility.Constants;

/**
 * Created by Andy on 1/30/2017.
 */

public class DriverDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mOkButton;
    private String mStringUsername, mStringDriverGender, mStringDisplayName;
    private int mIntDriverAge;
    private TextView mDriverDisplayname, mDriverAge, mDriverGender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_details);
        if (getIntent() != null) {
            mStringUsername = getIntent().getStringExtra(Constants.EXTRA_DRIVER_USERNAME);
            mStringDisplayName = getIntent().getStringExtra(Constants.EXTRA_DRIVER_DISPLAYNAME);
            mIntDriverAge = getIntent().getIntExtra(Constants.EXTRA_DRIVER_AGE, 0);
            mStringDriverGender = getIntent().getStringExtra(Constants.EXTRA_DRIVER_GENDER);
        }
        bindViews();
    }

    @SuppressLint("SetTextI18n")
    private void bindViews() {
        mOkButton = (Button) findViewById(R.id.ok_button);
        mOkButton.setOnClickListener(this);
        mDriverDisplayname = (TextView) findViewById(R.id.driver_name_tv);
        mDriverAge = (TextView) findViewById(R.id.driver_age_tv);
        mDriverGender = (TextView) findViewById(R.id.driver_gender_tv);


        mDriverDisplayname.setText(mStringDisplayName);
        mDriverAge.setText(Integer.toString(mIntDriverAge));
        mDriverGender.setText(mStringDriverGender);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ok_button) {
            finish();
            overridePendingTransition(R.anim.push_down_in, R.anim.slide_out_right);
        }
    }
}
