package com.example.BasicMap;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SignUpActivity extends AppCompatActivity {

    EditText editLoginid,editPWd,editName,editPhone,editDOb,editaddr_1,editaddr_2,editaddr_3,editaddr_colony,edit_pin,
             editcity,editstate,editcountry;
    Button btnSign;
    TextView textloglink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        btnSign= findViewById(R.id.btn_signup);

        editName = findViewById(R.id.input_name);
        editLoginid = findViewById(R.id.input_emailid);
        editDOb = findViewById(R.id.input_dob);
        editPWd = findViewById(R.id.input_pwd);
        editPhone = findViewById(R.id.input_phone);
        editaddr_1 = findViewById(R.id.input_addr1);
        editaddr_2 = findViewById(R.id.input_addr2);
        editaddr_3 = findViewById(R.id.input_addr3);
        editaddr_colony = findViewById(R.id.input_addrcolony);
        edit_pin = findViewById(R.id.input_addrpin);
        editcity = findViewById(R.id.input_addrcity);
        editstate = findViewById(R.id.input_addrstate);
        editcountry = findViewById(R.id.input_addrcountry);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate_signup()) {
                    onSignupFailed();
                    return;
                }

                btnSign.setEnabled(false);

                btnSign = findViewById(R.id.btn_signup);
                textloglink = findViewById(R.id.link_signup);
                final String name = editName.getText().toString();
                String email = editLoginid.getText().toString();
                String password = editPWd.getText().toString();
                String phone = editPhone.getText().toString();
                String addr1 = editaddr_1.getText().toString();
                String addr2 = editaddr_2.getText().toString();
                String addr3 = editaddr_3.getText().toString();
                String colony = editaddr_colony.getText().toString();
                String city = editcity.getText().toString();
                String state = editstate.getText().toString();
                String country = editcountry.getText().toString();
                String pin = edit_pin.getText().toString();
                String DOB = editDOb.getText().toString();

                RequestParams requestParams = new RequestParams();

                requestParams.add("name",name);
                requestParams.add("email",email);
                requestParams.add("dob",DOB);
                requestParams.add("password",password);
                requestParams.add("contact",phone);
                requestParams.add("house",addr1);
                requestParams.add("street",addr2);
                requestParams.add("apartment",addr3);
                requestParams.add("colony",colony);
                requestParams.add("pincode",pin);
                requestParams.add("city",city);
                requestParams.add("state",state);
                requestParams.add("country",country);
                Log.d("signup","page");


                    HttpUtils.get("/Registration", requestParams, new JsonHttpResponseHandler() {


                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            try {
                                JSONObject serverResp = new JSONObject(response.toString());

                                Log.d("sign", "up:" + serverResp);

                                if (serverResp.toString().contains("true")) {


                                    builder.setMessage("Email ID already exists. \n Please choose a different Email-ID.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                     Intent i= new Intent(SignUpActivity.this,SignUpActivity.class);
                                                      startActivity(i);
                                                      finish();
                                                  //  btnSign.performClick();


                                                }
                                            });
                                    final AlertDialog alert = builder.create();
                                    alert.show();
                                } else {
                                    final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this,
                                            R.style.AppTheme);
                                    progressDialog.setIndeterminate(true);
                                    progressDialog.setMessage("Creating Account...");
                                    progressDialog.show();
                                    new android.os.Handler().postDelayed(
                                            new Runnable() {
                                                public void run() {
                                                    // On complete call either onSignupSuccess or onSignupFailed
                                                    // depending on success
                                                    onSignupSuccess();
                                                    // onSignupFailed();
                                                    progressDialog.dismiss();
                                                }
                                            }, 3000);

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("e", e.toString());
                            }

                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                            Log.d("Array", response.toString());
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.d("TAG", "onFailure : " + statusCode);
                            Log.d("Sting", "res" + responseString);
                            Log.d("Error : ", "" + throwable);


                        }


                    });
                    // TODO: Implement your own signup logic here.
                }



        });


        /*  textloglink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginintent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(loginintent);
            }
        });
        */

    }


    public void onSignupSuccess() {
        btnSign.setEnabled(true);
        String email = editLoginid.getText().toString();
        Intent loginintent = new Intent(getApplicationContext(),LoginActivity.class);
        loginintent.putExtra("message", email);
        startActivity(loginintent);

      // setResult(RESULT_OK, null);
      // finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

         btnSign.setEnabled(true);
    }

    public boolean validate_signup() {
        boolean valid = true;

        String name = editName.getText().toString();
        String email = editLoginid.getText().toString();
        String password = editPWd.getText().toString();
        String number=editPhone.getText().toString();


        if (name.isEmpty() || name.length() < 3) {
            editName.setError("at least 3 characters");
            valid = false;
        } else {
            editName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editLoginid
                    .setError("enter a valid email address");
            valid = false;
        } else {
            editLoginid.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
           editPWd.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            editPWd.setError(null);
        }


        if (number.isEmpty() || number.length() < 10) {
            editPhone.setError("Mobile number cannot be less than 10 digits");
            valid = false;
        } else {
            editPhone.setError(null);
        }

        return valid;
    }
}



