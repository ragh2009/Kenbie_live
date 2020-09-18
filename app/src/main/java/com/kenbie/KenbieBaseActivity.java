package com.kenbie;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.kenbie.connection.MConnection;
import com.kenbie.util.LocationTrack;
import com.kenbie.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class KenbieBaseActivity extends AppCompatActivity {
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    public LocationTrack locationTrack;
    public double longitude = 0, latitude = 0;
    public SharedPreferences mPref = null;
    public Utility utility;
    public int position = 0, uType;
    public MConnection mConnection;
    public String ip, deviceId = "";
    public static Map<String, String> searchParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initUtils();
        super.onCreate(savedInstanceState);
    }

    private void initUtils() {
        try {
            mPref = getSharedPreferences("kPrefs", MODE_PRIVATE);
            uType = mPref.getInt("UserType", 1);
            position = uType - 1;
            utility = new Utility();
            mConnection = new MConnection();
//            mProgress = new ProgressDialog(this);
//            mProgress.setMessage("Please wait...");
//            mProgress.setIndeterminate(false);
//            mProgress.setCancelable(true);
//            mProgress.setCanceledOnTouchOutside(false);
            initData();
            if (mPref.getBoolean("isLogin", false)) {
                latitude = mPref.getFloat("latitude", 0);
                longitude = mPref.getFloat("longitude", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        try {
            ip = utility.getIpAddress(this);
            if (ip == null) ip = "";
            deviceId = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            ip = "";
            e.printStackTrace();
        }
    }

    public void initLocations() {
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }else {
            locationTrack = new LocationTrack(KenbieBaseActivity.this);
            if (locationTrack.canGetLocation()) {
                longitude = locationTrack.getLongitude();
                latitude = locationTrack.getLatitude();
//            Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
            } else
                locationTrack.showSettingsAlert();
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public boolean isOnline() {
        boolean isConnected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            isConnected = cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception ex) {
            isConnected = false;
        }
        return isConnected;
    }

    public void showToast(Context applicationContext, String string) {
        Toast.makeText(applicationContext, string, Toast.LENGTH_LONG).show();
    }

    public boolean isValidEmail(String email, Context context) {
        String EMAIL_REGEX = context.getResources().getString(R.string.email_val);
        Boolean b = email.matches(EMAIL_REGEX);
        System.out.println("is e-mail: " + email + " :Valid = " + b);
        return b;
    }

    public void hideKeyboard(Context context, EditText mEditText) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Show dialog with message and title
    public void showMessageWithTitle(Context context, String title, String message) {
        try {
            new AlertDialog.Builder(context)
                    .setTitle("")
                    .setMessage(Html.fromHtml(message))
                    .setPositiveButton(mPref.getString("21", "Yes"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(R.mipmap.ic_stat_notification)
                    .show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                ActivityCompat.requestPermissions(KenbieBaseActivity.this, permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    locationTrack = new LocationTrack(KenbieBaseActivity.this);
                    if (locationTrack != null && locationTrack.canGetLocation()) {
                        longitude = locationTrack.getLongitude();
                        latitude = locationTrack.getLatitude();
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(KenbieBaseActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationTrack != null)
            locationTrack.stopListener();
    }


//    public void showProgressDialog(boolean isShow) {
//        try {
//            if (isShow) {
//                mProgress.setMessage("Please wait...");
//                if (!mProgress.isShowing())
//                    mProgress.show();
//            } else if (mProgress.isShowing())
//                mProgress.dismiss();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (mProgress != null)
//                mProgress.dismiss();
//        }
//    }
}
