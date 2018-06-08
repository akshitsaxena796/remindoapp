package com.example.BasicMap;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutUsActivity extends AppCompatActivity {

TextView txtAboutApp2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        setContentView(R.layout.activity_about_us);
        txtAboutApp2 = findViewById(R.id.about_app2);
        txtAboutApp2.setText(
                "An application which can do all \n" +
                        "the work for you\n" +
                        "\n"+
                        "without any hustle\n" +
                        "and without referring dozens of apps.\n" +
                        "\n"+
                        "It can remind you,\n" +
                        "every time, you are about to approach\n" +
                        "an important day,\n" +
                        "or an important schedule,\n" +
                        "or just a daily routine.\n");

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
