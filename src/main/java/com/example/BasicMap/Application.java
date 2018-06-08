package com.example.BasicMap;

import android.content.ComponentName;
import android.content.Intent;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this,MyService.class));
    }

    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }

}
