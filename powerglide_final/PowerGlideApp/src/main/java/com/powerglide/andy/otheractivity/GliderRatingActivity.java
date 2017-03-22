package com.powerglide.andy.otheractivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.powerglide.andy.R;


/**
 * Created by Andy on 1/25/2017.
 */

public class GliderRatingActivity extends AppCompatActivity {
    private RatingBar mRatingBar;
    private Button mSubmitButton;
    private int mRatingValue;
    public static final String RATING_VALUE = "rating_value";
    private final Intent mReturningIntent = new Intent();

    private RatingBar.OnRatingBarChangeListener ratingBarChangeListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            if (getIntent() != null) {
                mRatingValue = (int) rating;
            } else {
                finish();
            }
        }
    };

    private View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int itemId = v.getId();
            switch (itemId) {
                case R.id.submit_button:
                    mReturningIntent.putExtra(RATING_VALUE, mRatingValue);
                    setResult(Activity.RESULT_OK, mReturningIntent);
                    finish();
                    overridePendingTransition(R.anim.push_down_in, R.anim.slide_out_right);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.glider_rating);
        bindViews();
        setViewListeners();
    }

    private void bindViews() {

        mRatingBar = (RatingBar) findViewById(R.id.rating_bar);
        mSubmitButton = (Button) findViewById(R.id.submit_button);
    }

    private void setViewListeners() {
        mRatingBar.setOnRatingBarChangeListener(ratingBarChangeListener);
        mSubmitButton.setOnClickListener(myOnClickListener);

    }
}
