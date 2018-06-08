package com.example.BasicMap;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import com.here.android.mpa.common.LocationDataSourceHERE;
import com.here.android.mpa.mapping.MapFragment;
import com.here.services.common.PositioningError;
import com.here.services.location.OptionsChangedEvent;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.LocationDataSourceHERE;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.ReverseGeocodeRequest2;
import com.here.services.common.PositioningError;
import com.here.services.location.OptionsChangedEvent;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import cz.msebera.android.httpclient.Header;

public class MyService extends  Service{


    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    // Permissions that need to be explicitly requested from end user.
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET,
            Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.WAKE_LOCK};

    public static final String NOTIFICATION_CHANNEL_ID = "10001";


    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 3000;
    //30000;
    private static final float LOCATION_DISTANCE = 0;
    private boolean mTransforming;
    private Runnable mPendingUpdate;
    private LocationDataSourceHERE mHereLocation;
    private MapFragment mapFragment;
    Location location;
    double lati, longi;
    Location mLastLocation;
    String currentaddr;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    static SplashScreen activity = SplashScreen.instance;
    public static JSONObject json_static;


    private class Mylocation implements com.here.services.location.LocationListener {
        Context context;
     /*   public Mylocation(String provider) {
            Log.d(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }*/

        public Mylocation(Context context) {
            this.context = context;

            getLocation();
        }


        private void getLocation() {
            Log.d(TAG, "initializeLocationManager");
            if (mLocationManager == null) {
                mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                Log.d("mLocationManager:", ":" + mLocationManager);

            }

        }

        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.d(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            Toast.makeText(getApplicationContext(), "loc changed" + location.getLatitude() + "==>" + location.getLongitude(), Toast.LENGTH_SHORT).show();

            lati = location.getLatitude();
            longi = location.getLongitude();

            Log.d("Lat", ":" + lati);
            Log.d("Log", ":" + longi);
        }

        @Override
        public void onLocationRequestFailed(@NonNull PositioningError positioningError) {

        }

        @Override
        public void onOptionsChanged(@NonNull OptionsChangedEvent optionsChangedEvent) {

        }
    }
            /*PositioningManager.OnPositionChangedListener {


        @Override
        public void onPositionUpdated(final PositioningManager.LocationMethod locationMethod, final GeoPosition geoPosition, final boolean b) {
            final GeoCoordinate coordinate = geoPosition.getCoordinate();
            if (mTransforming) {
                mPendingUpdate = new Runnable() {
                    @Override
                    public void run() {
                        onPositionUpdated(locationMethod, geoPosition, b);
                    }
                };
            }


        }

        @Override
        public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {

        }*/


    /*    @Override
        public void onCreate() {

           getPackageManager().getActivityBanner();
            this.BasicMapActivty.setContentView(R.layout.activity_basic_map);

            mapFragment = (MapFragment)BasicMapActivty.getFragmentManager().findFragmentById(R.id.mapfragment);

            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE) {
                        PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK);
                        mHereLocation = LocationDataSourceHERE.getInstance();
                        PositioningManager mPositioningManager = PositioningManager.getInstance();
                        mPositioningManager.setDataSource(mHereLocation);
                        mPositioningManager.addListener(new WeakReference<PositioningManager.OnPositionChangedListener>(null));

                    }

                }
            });
        }
        */
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //   return  null;
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        Date currentTime = Calendar.getInstance().getTime();
        Log.d("currect", ":" + currentTime);

        String currDate = DateFormat.getDateInstance().format(currentTime);
        Log.d("currDate", ":" + currDate); //Jun 2, 2018

        String currTime = DateFormat.getTimeInstance().format(currentTime);
        Log.d("currTime", ":" + currTime);

        String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        Log.d("dayLongName", ":" + dayLongName);

        Toast.makeText(getApplicationContext(), "service oncreate", Toast.LENGTH_LONG).show();
        //currect:Sat Jun 02 12:04:42 GMT+05:30 2018
        // take weekday like SAT for weekday checkbox
        //date as : 02 jun 2018
        //time as : 12:04:42
        Log.d(TAG, "in oncreate");
        initializeLocationManager();
        checkPermissions();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        final boolean hasLoggedIn = sharedpreferences.getBoolean("hasLoggedIn", false);
        String username = sharedpreferences.getString("Emailkey", "");
        String password = sharedpreferences.getString("Passwordkey", "");


        try {
            String providerType;
            boolean gpsProviderEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkProviderEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.d("gpsProviderEnabled", ":" + gpsProviderEnabled);
            Log.d("networkProviderEnabled", ":" + networkProviderEnabled);
            if (gpsProviderEnabled || networkProviderEnabled) {
                Log.d("inside if", ".");
                if (networkProviderEnabled) {
                    Log.d("if", "network_provider");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("permision", "is there");
                        Log.d("mLocationManager", ":" + mLocationManager);
                        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, locationListener);
                        if (mLocationManager != null) {
                            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            Log.d("location", ":" + location);
                            providerType = "network";
                            Log.d(TAG, "network lbs provider:" + (location == null ? "null" : String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude())));
                        }
                    }

                }

                if (gpsProviderEnabled && location == null) {
                    Log.d("if", "gps enabled");
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, locationListener);

                        if (mLocationManager != null) {
                            location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            providerType = "gps";
                            Log.d(TAG, "gps lbs provider:" + (location == null ? "null" : String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude())));

                        }
                    }
                }

                Log.d("Location", ":" + location);
                Toast.makeText(getApplicationContext(),"loc:"+location,Toast.LENGTH_LONG).show();


                if(location != null) {
                    Toast.makeText(getApplicationContext(), "loc" + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_LONG).show();

                    lati = location.getLatitude();
                    longi = location.getLongitude();
                    String la = Double.toString(lati);
                    String lon = Double.toString(longi);

                    currentaddr = getAddress(lati, longi);

                    Log.d("current",":"+currentaddr);


                    String[] locArray = currentaddr.split("\\s*,\\s*");
                    Log.d("locArray[0]",":"+locArray[0]);
                    Log.d("locArray[1]",":"+locArray[1]);
                    Log.d("locArray[1]",":"+locArray[2]);
                    Log.d("locArray[1]",":"+locArray[3]);
                    String nearBy = locArray[0];
                    String placename = locArray[1];
                    String sublocality = locArray[2];
                    String city = locArray[3];
                    String state = locArray[4];
                    String country = locArray[5];

                    Log.d("nearBy",":"+nearBy);
                    Log.d("placename",":"+placename);

                    Log.d("sublocality",":"+sublocality);

                    Log.d("city",":"+city);
                    Log.d("state",":"+state);

                    Log.d("country",":"+country);

                    // String[] city=  currentaddr.split(",[ ]*",1);
                    Log.d("currentaddr", currentaddr);

                    final RequestParams requestParams = new RequestParams();
                    requestParams.add("user", username);
                    requestParams.add("placeLat", la);
                    requestParams.add("placeLong", lon);
                    requestParams.add("placeName", placename);
                    requestParams.add("lastDate", currDate);
                    requestParams.add("lastDay", dayLongName);
                    requestParams.add("lastTime",currTime);
                    requestParams.add("dataMon", "Y");
                    requestParams.add("dataTue", "Y");
                    requestParams.add("dataWed", "Y");
                    requestParams.add("dataThu", "Y");
                    requestParams.add("dataFri", "Y");
                    requestParams.add("dataSat", "Y");
                    requestParams.add("dataSun", "Y");
                    requestParams.add("maxWeekDayCheck", "1");
                    requestParams.add("totalVisits", "1");
                    requestParams.add("nearBy", nearBy);
                    requestParams.add("subLocality", sublocality);
                    requestParams.add("city", city);
                    requestParams.add("state", state);
                    requestParams.add("country", country);


                    HttpUtils.post("/StoreLocationData", requestParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                JSONObject serverResp = new JSONObject(response.toString());
                                Log.d("store", ":" + serverResp);
                                Log.d("request", ":" + requestParams);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("e", e.toString());

                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.d("TAG", "onFailure : " + statusCode);
                            Log.d("Sting", "res" + responseString);
                            Log.d("Error : ", "" + throwable);
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    });


                }
                else
                {
                    Toast.makeText(getApplicationContext(), "loc is null", Toast.LENGTH_LONG).show();

                }

                //

       /*     mLocationManager.requestLocationUpdates(
                    NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, (LocationListener) locationListener);
        } catch (java.lang.SecurityException ex) {
            Log.d(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            Log.d("try1", "try1");

            mLocationManager.requestLocationUpdates(
                    GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, (LocationListener) locationListener);
        } catch (java.lang.SecurityException ex) {
            Log.d(TAG, "fail to request location update, ignore", ex);*/

            }
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        if(hasLoggedIn)
        {
            RequestParams rp = new RequestParams();
            rp.add("user",username);
            rp.add("lastDay",dayLongName);
            rp.add("lastTime",currTime);
        //    rp.add("lastTime","09:25:25 PM");
            Log.d("notif","request:"+rp);
            HttpUtils.post("/GetNotificationData",rp,new JsonHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    try {
                        JSONObject serverResponseobj = null;

                        serverResponseobj = response.getJSONObject(0);

                        Log.d("response notifi:", String.valueOf(serverResponseobj));


                        if (serverResponseobj.toString().contains("NoDataFound"))
                        {      Log.d("No","data found");

                        }
                        else
                        {
                            JSONObject serverRespObj =null;

                            for(int i=0;i<response.length();i++) {

                              serverRespObj = response.getJSONObject(i);
                              Log.d("serverRespObj",":"+serverRespObj);
                                final String place =  serverRespObj.getString("placeName");
                                final String nearby =  serverRespObj.getString("nearBy");
                                final String  lastday =serverRespObj.getString("lastDay");
                                json_static = serverRespObj;

                                Log.d("json_static",":"+json_static);


                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                calendar.set(Calendar.HOUR_OF_DAY, 7);
                                calendar.set(Calendar.MINUTE, 40);
                                calendar.set(Calendar.SECOND, 0);

                                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

                                Intent notificationmassage = new Intent(getApplicationContext(),NotificationClass.class);


                            //    notificationmassage.putExtra("nearby",nearby);
                            //    notificationmassage.setAction(Long.toString(System.currentTimeMillis()));
                              //  notificationmassage.putExtra("place",place);
                              //  notificationmassage.putExtra("lastDay",lastday);

                                //This is alarm manager
                                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0 , notificationmassage, PendingIntent.FLAG_UPDATE_CURRENT);



                           /*     NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),NOTIFICATION_CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_add_alarm_black_24dp)
                                        .setContentTitle(nearby+""+place)
                                        .setContentText("You visited this place last"+lastday+", Here is your hassle free route in case of re-visit");
                                        */


                           am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60000, pi);
                          //      am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                      Log.d("noti","resp:"+response);
                    try {
                        JSONObject serverResp = new JSONObject(response.toString());
                        if(serverResp.toString().contains("NoDataFound"))
                        {
                            Log.d("No","data found");

                        }
                        else
                        {
                            Log.d("res",":"+response);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d("statusCode",":"+statusCode);
                    Log.d("headers",";"+headers);
                    Log.d("throwable",":"+throwable);
                }
            });

        }

    }


    public String getAddress(double lat, double lng) {
        Log.d("in", "getaddress");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = addresses.get(0).getAddressLine(0);
            String nearby = addresses.get(0).getFeatureName();
            String placename = addresses.get(0).getSubAdminArea();
            String subloc = obj.getSubLocality();
            String city = obj.getLocality();
            String state = obj.getAdminArea();
            String country = obj.getCountryName();

            Log.d("placename",placename);

            Toast.makeText(this, "Address:" + add, Toast.LENGTH_LONG).show();
            String addret = nearby + "," + placename+ "," + subloc+ "," + city+ "," + state+ "," + country;
            //return add;
            return addret;

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return e.toString();
        }
    }

//
//    void getHereAddress(double lat,double log)
//    {    /* Create a ReverseGeocodeRequest object with a GeoCoordinate. */
//        GeoCoordinate coordinate = new GeoCoordinate(49.25914, -123.00777);
//        ReverseGeocodeRequest2 revGecodeRequest = new ReverseGeocodeRequest2(coordinate);
//        revGecodeRequest.execute(new ResultListener<Location>() {
//            @Override
//            public void onCompleted(Location location, ErrorCode errorCode) {
//                if (errorCode == ErrorCode.NONE) {
//                    /*
//                     * From the location object, we retrieve the address and display to the screen.
//                     * Please refer to HERE Android SDK doc for other supported APIs.
//                     */
//                } else {
//                    ("TAG","ERROR:RevGeocode Request returned error code:" + errorCode);
//                }
//            }
//        });
//
//    }`


    private void initializeLocationManager() {
        //    Log.d(TAG, "initializeLocationManager");
        //   Log.d("mLocationManager: 1", ":" + mLocationManager);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        }
    }

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            // getting the static instance of activity
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }


 /*  private static void  buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyAlertDialogStyle);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                      //  startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
         Handler myHandler = new Handler() {
            public static final int DISPLAY_DLG = 1;

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DISPLAY_DLG:
                        if (!SplashScreen.instance.isFinishing() && SplashScreen.instance != null) {
                          //  alert.show();
                        }
                        break;
                }
            }
        };

    }  */

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                // all permissions were granted
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    Log.d("mLocationManager :", ":" + mLocationManager);

                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 100, locationListener);

             /*   else
                {
                    location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Log.d("loc:",":"+location);


                } */
                }

                break;
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // Log.d("onstart","command");
        onTaskRemoved(intent);
        initializeLocationManager();
        // onCreate();
        //  Toast.makeText(getApplicationContext(),"service started `",Toast.LENGTH_LONG).show();
        return START_STICKY;
    }




    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Intent intent = new Intent(getApplicationContext(), this.getClass());
        intent.setPackage(getPackageName());
        startService(intent);
        // super.onTaskRemoved(rootIntent);

    }


    @Override
    public void onDestroy() {

        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < 2; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                    PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(locationListener);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }


     /*   try {
            mTimer.cancel();
            timerTask.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent("com.android.ServiceStopped");
        sendBroadcast(intent);
    }*/
}
