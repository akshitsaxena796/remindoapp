package com.example.BasicMap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.common.LocationDataSourceHERE;
import com.here.android.mpa.common.PositioningManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Calendar;

public class RemainderDetailsActivity extends AppCompatActivity {

    String lastdate, placeLat, placeLong, city;
    int timevisited;
    private PositioningManager mPositioningManager;
    // HERE location data source instance
    private LocationDataSourceHERE mHereLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remainder_details);
        setupActionBar();


        TextView txtDetails = findViewById(R.id.txtDetails);
        TextView txtCity = findViewById(R.id.cityname);
        TextView lastvisitedDate = findViewById(R.id.lastvisiteddate);
        TextView timesvisited = findViewById(R.id.timesvisited);
        TextView nextRem = findViewById(R.id.timesnextreminder);

        Button btnShowMap = findViewById(R.id.btnShowMap);

        Intent intent = getIntent();
        String loadsPosition = intent.getStringExtra("loadsPosition");
        int position = intent.getIntExtra("Position", -99);
        String place = intent.getStringExtra("Place");
        String jsonString = intent.getStringExtra("jsonObject");


        Log.d("details", ":" + loadsPosition);
        Log.d("details", ":" + position);
        Log.d("details", ":" + place);
        Log.d("JSONobject", ":" + jsonString);

        txtDetails.setText(place);

        if (jsonString != null) {

            try {

                JSONObject jObj = new JSONObject(jsonString);

                lastdate = (String) jObj.get("lastDate");
                timevisited = jObj.getInt("totalVisits");
                city = (String) jObj.get("city");
                placeLat = (String) jObj.get("placeLat");
                placeLong = (String) jObj.get("placeLong");
                Log.d("reminderDetails", ";" + timevisited);

                txtCity.setText(city);
                lastvisitedDate.setText(lastdate);
                timesvisited.setText(String.valueOf(timevisited));

                Log.d("lastdate", lastdate);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mapintent = new Intent(getApplicationContext(), BasicMapActivity.class);
                mapintent.putExtra("Lat", placeLat);
                mapintent.putExtra("Long", placeLong);
                startActivity(mapintent);

            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
