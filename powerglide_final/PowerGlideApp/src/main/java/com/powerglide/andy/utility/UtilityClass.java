package com.powerglide.andy.utility;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.powerglide.andy.R;
import com.powerglide.andy.provider.PowerGlideContract;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andy on 1/11/2017.
 */

public class UtilityClass {
    private static final String POWERGLIDE_COM = "powerglide.com";

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /* return false when email domain is powerglide.com */
    public static boolean checkGliderEmailDomain(Context context, String email) {
        String[] returnedStringArray = TextUtils.split(email, "@");

        if (returnedStringArray[1].equals(POWERGLIDE_COM)) {
            Toast.makeText(context, POWERGLIDE_COM + " " + context.getString(R.string.is_not_a_glider_email_domain), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /* return true when email is powerglide.com */
    public static boolean checkDriverEmailDomain(Context context, String email) {
        String[] returnedStringArray = TextUtils.split(email, "@");
        if (returnedStringArray[1].equals(POWERGLIDE_COM)) {

            return true;
        }
        Toast.makeText(context, context.getString(R.string.you_need_to_enter_powerglide_com_domain), Toast.LENGTH_LONG).show();
        return false;
    }

    public static boolean isEmptyEmailPasswordFields(Context context, EditText emailEditText, EditText passwordEditText) {
        if (emailEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("")) {
            Toast.makeText(context, context.getString(R.string.must_enter_all_fields), Toast.LENGTH_SHORT).show();
            return true;
        } else
            return false;
    }

    public static boolean isEmptyAllFields(Context context, EditText emailEditText, EditText userNameEditText, EditText passwordEditText) {
        if (emailEditText.getText().toString().equals("") || userNameEditText.getText().toString().equals("")
                || passwordEditText.getText().toString().equals("")) {
            Toast.makeText(context, context.getString(R.string.must_enter_all_fields), Toast.LENGTH_SHORT).show();
            return true;
        } else
            return false;

    }

    public static String FormattingDate(String stringDate) {
        stringDate = stringDate.trim();
        SimpleDateFormat simpleFormatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
        Date parsedDate = null;
        try {
            parsedDate = simpleFormatter.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(parsedDate);
        String formattedDate = cal.get(Calendar.DATE) + "/"
                + getMonth((cal.get(Calendar.MONTH) + 1)) + "/"
                + cal.get(Calendar.YEAR) + "    "
                + cal.get(Calendar.HOUR_OF_DAY) + ":"
                + cal.get(Calendar.MINUTE) + ":"
                + cal.get(Calendar.SECOND);

        return formattedDate;

    }

    private static String getMonth(int month) {
        String monthString = null;
        switch (month) {
            case 1:
                monthString = "Jan";
                break;
            case 2:
                monthString = "Feb";
                break;
            case 3:
                monthString = "Mar";
                break;
            case 4:
                monthString = "Apr";
                break;
            case 5:
                monthString = "May";
                break;
            case 6:
                monthString = "Jun";
                break;
            case 7:
                monthString = "Jul";
                break;
            case 8:
                monthString = "Aug";
                break;
            case 9:
                monthString = "Sep";
                break;
            case 10:
                monthString = "Oct";
                break;
            case 11:
                monthString = "Nov";
                break;
            case 12:
                monthString = "Dec";
                break;
            default:
                throw new IllegalArgumentException("Unknow Month: " + month);
        }


        return monthString;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    public void parseNearByJSON(Context context, String jsonString, Double startLat, Double startLon, String type) {


        String lat;
        String lon;
        String name = null;
        String place_id = null;
        Double rating = null;
        String openNow = null;
        String address = null;


        try {
            JSONObject nearByString = new JSONObject(jsonString);
            JSONArray resultsArray = nearByString.getJSONArray("results");

            Vector<ContentValues> cVVector = new Vector<>(resultsArray.length());

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject nearByObject = resultsArray.getJSONObject(i);

                if (nearByObject.has("place_id")) {
                    if (nearByObject.getString("place_id") != null) {
                        place_id = nearByObject.getString("place_id");
                    }
                }


                if (nearByObject.has("rating")) {
                    if (nearByObject.getString("rating") != null) {
                        rating = Double.parseDouble(nearByObject.getString("rating"));
                    }
                }

                if (nearByObject.has("name")) {
                    if (nearByObject.getString("name") != null) {
                        name = nearByObject.getString("name");
                    }

                }

                if (nearByObject.has("vicinity")) {
                    if (nearByObject.getString("vicinity") != null) {
                        address = nearByObject.getString("vicinity");
                    }

                }


                JSONObject geometry = nearByObject.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                lat = location.getString("lat");
                lon = location.getString("lng");

                if (nearByObject.has("opening_hours")) {
                    JSONObject openingObject = nearByObject.getJSONObject("opening_hours");
                    if (openingObject.has("open_now")) {
                        openNow = openingObject.getString("open_now");
                    }
                }

                ContentValues cv = new ContentValues();
                cv.put(PowerGlideContract.COL_ADDRESS, address);
                cv.put(PowerGlideContract.COL_LAT, lat);
                cv.put(PowerGlideContract.COL_LON, lon);
                cv.put(PowerGlideContract.COL_NAME, name);
                cv.put(PowerGlideContract.COL_OPEN_NOW, openNow);
                cv.put(PowerGlideContract.COL_PLACE_ID, place_id);
                cv.put(PowerGlideContract.COL_RATING, rating);

                Location startLocation = new Location("Dummy");
                startLocation.setLatitude(startLat);
                startLocation.setLongitude(startLon);
                Double endLat = Double.parseDouble(lat);
                Double endLon = Double.parseDouble(lon);
                Location endLocation = new Location("Dummy");
                endLocation.setLatitude(endLat);
                endLocation.setLongitude(endLon);


                float distanceBetween = startLocation.distanceTo(endLocation);

                cv.put(PowerGlideContract.COL_DISTANCE, distanceBetween);
                cVVector.add(cv);


            }
            int inserted;
            if (cVVector.size() > 0) {
                ContentValues[] values = new ContentValues[cVVector.size()];
                cVVector.toArray(values);

                switch (type) {
                    case "":
                        //inserted=context.getContentResolver().bulkInsert(NearMeContract.Places.CONTENT_URI,values);
                        break;
                    case "restaurant":
                        inserted = context.getContentResolver().bulkInsert(PowerGlideContract.Restaurant.CONTENT_URI, values);
                        break;
                    case "gas_station":
                        inserted = context.getContentResolver().bulkInsert(PowerGlideContract.Gas_Station.CONTENT_URI, values);
                        break;
                    case "atm":
                        inserted = context.getContentResolver().bulkInsert(PowerGlideContract.Atm.CONTENT_URI, values);
                        break;
                    case "pharmacy":
                        inserted = context.getContentResolver().bulkInsert(PowerGlideContract.Pharmacy.CONTENT_URI, values);
                        break;
                    case "lodging":
                        inserted = context.getContentResolver().bulkInsert(PowerGlideContract.Hotel.CONTENT_URI, values);
                        break;

                    case "parking":
                        inserted = context.getContentResolver().bulkInsert(PowerGlideContract.Parking.CONTENT_URI, values);
                        break;

                    default:
                        Log.v("Error", "Not supported");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean isRTL(Context ctx) {
        Configuration config = ctx.getResources().getConfiguration();
        return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    public static void SaveToPreference(Context context, String constantString, String stringSaved) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(constantString, stringSaved);
        editor.commit();
    }

    public static void ClearPreference(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
}
