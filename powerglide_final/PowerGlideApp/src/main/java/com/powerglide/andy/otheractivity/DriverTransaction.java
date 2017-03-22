package com.powerglide.andy.otheractivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.powerglide.andy.R;

public class DriverTransaction extends AppCompatActivity {
    private Button mSubmitButton;

    private View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(RESULT_OK);
            finish();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_transaction);
        bindViews();

    }

    private void bindViews() {
        mSubmitButton = (Button) findViewById(R.id.submit_button);

        mSubmitButton.setOnClickListener(myOnClickListener);
    }
}
