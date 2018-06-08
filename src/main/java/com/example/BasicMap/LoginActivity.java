package com.example.BasicMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs";
    private static final int REQUEST_SIGNUP = 0;
    EditText editLogin, editPassword;
    Button btnLogin;
    TextView txtSignuplink;
    boolean connected = false;
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

   /*     if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;}

            else
            {
                connected = false;
                buildAlertMessageNoLocation();

                //   startActivity(new Intent(LoginActivity.this,LoginActivity.class));


            }
            */

        setContentView(R.layout.activity_login);
        editLogin = findViewById(R.id.input_email);
        editPassword = findViewById(R.id.input_password);
        btnLogin = findViewById(R.id.btn_login);
        txtSignuplink = findViewById(R.id.link_signup);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        final boolean hasLoggedIn = sharedpreferences.getBoolean("hasLoggedIn", false);
        String username = sharedpreferences.getString("Emailkey", "");
        String password = sharedpreferences.getString("Passwordkey", "");

        final RequestParams requestParams = new RequestParams();

        Intent startingIntent = getIntent();
        String whatYouSent = startingIntent.getStringExtra("message");
        editLogin.setText(whatYouSent);


        if (hasLoggedIn) {


            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Credentials Found...");
            progressDialog.show();
            // TODO: Implement your own authentication logic here.
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
//                            Intent reminder = new Intent(getApplicationContext(), BaseDrawerActivity.class);
//                            startActivity(reminder);
//                            finish();
                            progressDialog.dismiss();
                        }
                    }, 3000);

            requestParams.add("user", username);
            requestParams.add("pwd", password);

            HttpUtils.post("/Login", requestParams, new JsonHttpResponseHandler() {

                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    try {
                        JSONObject serverResp = new JSONObject(response.toString());

                        Log.d("login", "rp" + requestParams.toString());
                        Log.d("login", "res" + serverResp);


                        if (serverResp.toString().contains("Invalid Credentials !")) {
                            Log.d("in", "inside if");
                            Toast.makeText(getApplicationContext(), "User is deleted from DB.Need to sign up again", Toast.LENGTH_LONG).show();


                        } else {
                            Log.d("sahi login", "data " + serverResp);

                            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Authenticating...");
                            progressDialog.show();
                            // TODO: Implement your own authentication logic here.
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            // On complete call either onLoginSuccess or onLoginFailed
                                            Intent reminder = new Intent(getApplicationContext(), BaseDrawerActivity.class);
                                            startActivity(reminder);
                                            finish();
                                            progressDialog.dismiss();
                                        }
                                    }, 3000);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (!validate()) {
                        onLoginFailed();
                        return;
                    }

                    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));


                                    final String email = editLogin.getText().toString();
                                    final String password = editPassword.getText().toString();
                                    final RequestParams rp = new RequestParams();
                                    rp.add("user", email);
                                    rp.add("pwd", password);
                                    HttpUtils.post("/Login", rp, new JsonHttpResponseHandler() {
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            // If the response is JSONObject instead of expected JSONArray
                                            try {
                                                JSONObject serverResp = new JSONObject(response.toString());

                                                Log.d("login", "rp" + rp.toString());
                                                Log.d("login", "res" + serverResp);

                                                if (serverResp.toString().contains("Invalid Credentials !")) {
                                                    Log.d("in", "inside if");
                                                    Toast.makeText(getApplicationContext(), "Invalid Credentials !", Toast.LENGTH_LONG).show();
                                                    onLoginFailed();
                                                } else {
                                                    Log.d("sahi login", "data " + serverResp);

                                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                                    editor.putBoolean("hasLoggedIn", true);
                                                    editor.putString("Emailkey", email);
                                                    editor.putString("Passwordkey", password);

                                                    editor.commit();
                                                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme);
                                                    progressDialog.setIndeterminate(true);
                                                    progressDialog.setMessage("Authenticating...");
                                                    progressDialog.show();
                                                    // TODO: Implement your own authentication logic here.
                                                    new android.os.Handler().postDelayed(
                                                            new Runnable() {
                                                                public void run() {
                                                                    // On complete call either onLoginSuccess or onLoginFailed
                                                                    onLoginSuccess();
                                                                    progressDialog.dismiss();
                                                                }
                                                            }, 3000);

                                                }
                                                Log.d("response", "get:" + serverResp);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });


                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    dialog.cancel();
                                }
                            });

                    final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        final AlertDialog alert = builder.create();
                        alert.show();
                    } else {

                        final String email = editLogin.getText().toString();
                        final String password = editPassword.getText().toString();
                        final RequestParams rp = new RequestParams();
                        rp.add("user", email);
                        rp.add("pwd", password);
                        HttpUtils.post("/Login", rp, new JsonHttpResponseHandler() {
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                // If the response is JSONObject instead of expected JSONArray
                                try {
                                    JSONObject serverResp = new JSONObject(response.toString());

                                    Log.d("login", "rp" + rp.toString());
                                    Log.d("login", "res" + serverResp);

                                    if (serverResp.toString().contains("Invalid Credentials !")) {
                                        Log.d("in", "inside if");
                                        Toast.makeText(getApplicationContext(), "Invalid Credentials !", Toast.LENGTH_LONG).show();
                                        onLoginFailed();
                                    } else {
                                        Log.d("sahi login", "data " + serverResp);

                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putBoolean("hasLoggedIn", true);
                                        editor.putString("Emailkey", email);
                                        editor.putString("Passwordkey", password);

                                        editor.commit();
                                        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme);
                                        progressDialog.setIndeterminate(true);
                                        progressDialog.setMessage("Authenticating...");
                                        progressDialog.show();
                                        // TODO: Implement your own authentication logic here.
                                        new android.os.Handler().postDelayed(
                                                new Runnable() {
                                                    public void run() {
                                                        // On complete call either onLoginSuccess or onLoginFailed
                                                        onLoginSuccess();
                                                        progressDialog.dismiss();
                                                    }
                                                }, 3000);

                                    }
                                    Log.d("response", "get:" + serverResp);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }

                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        txtSignuplink.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                try {
                    Intent signIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                    //   startActivityForResult(signIntent, REQUEST_SIGNUP);
                    startActivity(signIntent);

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Link" + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    /*  @Override
       protected void onActivityResult(int requestCode, int resultCode, Intent data) {
           if (requestCode == REQUEST_SIGNUP) {
               if (resultCode == RESULT_OK) {

                   // TODO: Implement successful signup logic here
                   // By default we just finish the Activity and log them in automatically
                   this.finish();
               }
           }
       }
   */
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginFailed() {
        editLogin.setEnabled(true);
    }


    public void onLoginSuccess() {
        editLogin.setEnabled(true);
        Intent remainder = new Intent(getApplicationContext(), BaseDrawerActivity.class);
        startActivity(remainder);
        finish();
    }


    private void buildAlertMessageNoInternet() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your internet seems to be disabled. Please enable WIFI or Mobile Data.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private void buildAlertMessageNoGps() {

    }


    public boolean validate() {
        boolean valid = true;
        String email = editLogin.getText().toString();
        String password = editPassword.getText().toString();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED &&
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            buildAlertMessageNoInternet();
            valid = false;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editLogin.setError("enter a valid email address");
            valid = false;
        } else {
            editLogin.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            editPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            editPassword.setError(null);
        }

/*
        if (editLogin.getText().toString().equals("admin@gmail.com") &&  editPassword.getText().toString().equals("admin"))
        {
            valid = true;
        }
        else{
            Toast.makeText(getApplicationContext(),"Incorrect cerdentials",Toast.LENGTH_LONG).show();
            valid = false;
        } */
        return valid;
    }
}


