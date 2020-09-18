package com.kenbie;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kenbie.util.Utility;

public class KenbieBackActivity extends AppCompatActivity {
    public SharedPreferences mPref = null;
    public Utility utility;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initUtils();
        super.onCreate(savedInstanceState);
    }

    private void initUtils() {
        try {
            mPref = getSharedPreferences("kPrefs", MODE_PRIVATE);
            utility = new Utility();
            mProgress = new ProgressDialog(this);
            mProgress.setMessage("Please wait...");
            mProgress.setIndeterminate(false);
            mProgress.setCancelable(true);
            mProgress.setCanceledOnTouchOutside(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgressDialog(boolean isShow) {
        try {
            if (isShow) {
                mProgress.setMessage("Please wait...");
                mProgress.show();
            } else
                mProgress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    // Show dialog with message and title
    public void showMessageWithTitle(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle("")
                .setMessage(Html.fromHtml(message))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

}

