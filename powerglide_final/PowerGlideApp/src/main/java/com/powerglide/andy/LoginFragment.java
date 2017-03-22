package com.powerglide.andy;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.powerglide.andy.utility.Constants;
import com.powerglide.andy.utility.UtilityClass;


public class LoginFragment extends Fragment {
    private Switch mSwitchUser;
    private EditText mEmailEditText, mUsernameEditText, mPasswordEditText;
    private Button mLogin_SignupButton;
    private TextView mLogin_SignupTextView, mUsernameTextview;
    private RelativeLayout mMainRelativeLayout;
    private ImageView mImageLogo;
    private boolean mLogin_status = false;
    private String mDisplayName;


    public LoginFragment() {
        // Required empty public constructor
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals((Constants.ACTION_NETWORK_MESSAGE))) {
                String msg = intent.getStringExtra(Constants.NETWORK_INFO);
                if (msg.equals(getString(R.string.sorry_no_network))) {
                    mMainRelativeLayout.setContentDescription(getString(R.string.there_is_no_network));
                    Snackbar.make(mMainRelativeLayout, msg, Snackbar.LENGTH_INDEFINITE)
                            .setAction(getString(R.string.ok_try_later), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                } else if (msg.equals(getString(R.string.there_is_connection))) {
                    Snackbar.make(mMainRelativeLayout, msg, Snackbar.LENGTH_LONG).show();
                }
            }
        }
    };

    private View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.login_signup_textview) {

                ToggleView();

            } else if (v.getId() == R.id.login_button) {
                if (!UtilityClass.isNetworkConnected(getActivity())) {
                    Snackbar.make(mMainRelativeLayout, getString(R.string.no_network_connection), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getString(R.string.ok_try_later), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
                    return;
                }
                if (mLogin_status && !mSwitchUser.isChecked()) { //Login status is true and user is glider
                    if (UtilityClass.isEmptyEmailPasswordFields(getActivity(), mEmailEditText, mPasswordEditText)) {
                        return;
                    }
                    if (!UtilityClass.isEmailValid(mEmailEditText.getText().toString())) {
                        Toast.makeText(getActivity(), getString(R.string.email_invalid), Toast.LENGTH_LONG).show();
                        return;
                    }
                    /*check if email domain is powerglide.com*/
                    if (!UtilityClass.checkGliderEmailDomain(getActivity(), mEmailEditText.getText().toString())) {
                        return;
                    }
                    ParseUser.logInInBackground(mEmailEditText.getText().toString(),
                            mPasswordEditText.getText().toString(), new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException e) {
                                    if (user != null && e == null) {
                                        mDisplayName = user.get(Constants.PARSE_DISPLAYNAME).toString();
                                        redirect_activity();
                                    } else {
                                        Toast.makeText(getActivity(), getString(R.string.wrong_login), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else if (!mLogin_status && !mSwitchUser.isChecked()) {  //sign up status and user is glider
                    if (UtilityClass.isEmptyAllFields(getActivity(), mEmailEditText, mUsernameEditText, mPasswordEditText)) {
                        return;
                    }
                    if (!UtilityClass.isEmailValid(mEmailEditText.getText().toString())) {
                        Toast.makeText(getActivity(), getString(R.string.email_invalid), Toast.LENGTH_LONG).show();
                        return;
                    }
                    /*check if email domain is powerglide.com*/
                    if (!UtilityClass.checkGliderEmailDomain(getActivity(), mEmailEditText.getText().toString())) {
                        return;
                    }
                    ParseUser user = new ParseUser();
                    user.setEmail(mEmailEditText.getText().toString());
                    user.setPassword(mPasswordEditText.getText().toString());
                    user.setUsername(mEmailEditText.getText().toString());
                    user.put(Constants.PARSE_DISPLAYNAME, mUsernameEditText.getText().toString());
                    user.put(Constants.PARSE_GLIDER_DRIVER, getString(R.string.glider_type));

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ToggleView();

                            } else {
                                /* sign up fail */
                            }
                        }
                    });
                } else if (mLogin_status && mSwitchUser.isChecked()) {  //login status is true and user is driver
                    if (UtilityClass.isEmptyEmailPasswordFields(getActivity(), mEmailEditText, mPasswordEditText)) {
                        return;
                    }
                    if (!UtilityClass.isEmailValid(mEmailEditText.getText().toString())) {
                        Toast.makeText(getActivity(), getString(R.string.email_invalid), Toast.LENGTH_LONG).show();
                        return;
                    }
                    /* make sure email entered is powerglide.com */
                    if (!UtilityClass.checkDriverEmailDomain(getActivity(), mEmailEditText.getText().toString())) {
                        return;
                    }
                    ParseUser.logInInBackground(mEmailEditText.getText().toString(),
                            mPasswordEditText.getText().toString(), new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException e) {
                                    if (user != null && e == null) {
                                        mDisplayName = user.get(Constants.PARSE_DISPLAYNAME).toString();
                                        redirect_activity();
                                    } else {
                                        Toast.makeText(getActivity(), getString(R.string.wrong_login), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else if (!mLogin_status && mSwitchUser.isChecked()) {   //sign up status and user is driver

                    Snackbar.make(mMainRelativeLayout, getString(R.string.sign_up_at_company), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getString(R.string.ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                }
            }

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initializeUIWidget(view);
        registerReceiver();
        return view;
    }

    private void initializeUIWidget(View view) {
        mSwitchUser = (Switch) view.findViewById(R.id.switch_user);
        mEmailEditText = (EditText) view.findViewById(R.id.email_edittext);
        mUsernameEditText = (EditText) view.findViewById(R.id.username_edittext);
        mPasswordEditText = (EditText) view.findViewById(R.id.password_edittext);
        mLogin_SignupButton = (Button) view.findViewById(R.id.login_button);
        mLogin_SignupTextView = (TextView) view.findViewById(R.id.login_signup_textview);
        mUsernameTextview = (TextView) view.findViewById(R.id.username_textview);
        mMainRelativeLayout = (RelativeLayout) view.findViewById(R.id.main_relative_layout);
        mImageLogo = (ImageView) view.findViewById(R.id.image_logo);

        mLogin_SignupTextView.setOnClickListener(myOnClickListener);
        mLogin_SignupButton.setOnClickListener(myOnClickListener);
        mSwitchUser.setOnClickListener(myOnClickListener);
        mMainRelativeLayout.setOnClickListener(myOnClickListener);
        mImageLogo.setOnClickListener(myOnClickListener);

        ToggleView();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_NETWORK_MESSAGE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void ToggleView() {
        if (mLogin_status) {
            mLogin_status = false;
            mLogin_SignupTextView.setText(getString(R.string.or_login));
            mLogin_SignupButton.setText(getString(R.string.sign_up));
            mUsernameEditText.setVisibility(View.VISIBLE);
            mUsernameTextview.setVisibility(View.VISIBLE);

        } else {  //first time when app is running
            mLogin_status = true;
            mLogin_SignupTextView.setText(getString(R.string.or_comma_signup));
            mLogin_SignupButton.setText(getString(R.string.log_in));
            mUsernameEditText.setVisibility(View.GONE);
            mUsernameTextview.setVisibility(View.GONE);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ParseUser.getCurrentUser() != null) {  //if user is logged in , redirect to GliderMapActivity
            redirect_activity();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void redirect_activity() {
        if (!mSwitchUser.isChecked()) {   //user is glider and login mode
            Intent i = new Intent(getActivity(), GliderMapActivity.class);
            String displayName = mDisplayName;
            i.putExtra(Constants.EXTRA_DISPLAY_NAME, displayName);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.push_down_in, R.anim.slide_out_left);
        } else if (mSwitchUser.isChecked()) {
            Intent i = new Intent(getActivity(), ViewRequestsActivity.class);
            i.putExtra(Constants.EXTRA_DISPLAY_NAME, mDisplayName);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.push_down_in, R.anim.slide_out_left);
        }

    }


}
