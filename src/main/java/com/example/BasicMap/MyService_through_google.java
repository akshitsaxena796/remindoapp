package com.example.BasicMap;


import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.LocationDataSourceHERE;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Location;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;


import java.lang.ref.WeakReference;

public class MyService_through_google extends Service {
    private Map map = null;



    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    // Permissions that need to be explicitly requested from end user.
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET,
            Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.WAKE_LOCK};

    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 30000;
    private static final float LOCATION_DISTANCE = 1000;
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
    // static BasicMapActivity activity = BasicMapActivity.instance;
    // helper for the very first fix after startup (we want to jump to that position then)
    private boolean firstPositionSet = false;

    IBinder mBinder = new LocalBinder();
 //   MyService.LocalBinder  localBinder=new LocalBinder();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");

        //return null;
    }

    public class LocalBinder extends Binder {
        public MyService_through_google getServerInstance() {
            return MyService_through_google.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("on","start");
        onTaskRemoved(intent);
        initializeLocationManager();
        //  onCreate();
        //  Toast.makeText(getApplicationContext(),"service started `",Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    private void initializeLocationManager() {
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent intent = new Intent(getApplicationContext(), this.getClass());
        intent.setPackage(getPackageName());
        startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("on","create");

     //   Log.d("serviceCallbacks",":"+serviceCallbacks);

     //   if (serviceCallbacks != null) {
      //      serviceCallbacks.doSomething();

            // Search for the map fragment to finish setup by calling init().
            //     mapFragment = (MapFragment) activity.getFragmentManager().findFragmentById(R.id.mapfragment);

            mapFragment.init(new OnEngineInitListener() {

                @Override
                public void onEngineInitializationCompleted(Error error) {
                    Log.d("error", ":"+error);
                    if (error == Error.NONE) {
                        map = mapFragment.getMap();
                        PositioningManager.getInstance().addListener(new WeakReference<>(mapPositionHandler));
                        // use gps plus cell and wifi
                        PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
                        GeoPosition lkp = PositioningManager.getInstance().getLastKnownPosition();
                        Log.d("lkp",":"+lkp);
                        if (lkp != null && lkp.isValid())
                            Log.d("on", "create:" + lkp.getCoordinate());

                    } else {
                        Log.e(TAG, "ERROR: Cannot initialize Map Fragment " + error.name());
                        Log.e(TAG, error.getDetails());
                        Log.e(TAG, error.getStackTrace());
                    }
                }
            });
        }
 //   }

 //   public void setCallbacks(ServiceCallbacks callbacks) {
   //     serviceCallbacks = callbacks;
 //   }


    // listen for positioning events
    private PositioningManager.OnPositionChangedListener mapPositionHandler = new PositioningManager.OnPositionChangedListener() {
        @Override
        public void onPositionUpdated(PositioningManager.LocationMethod method, GeoPosition position, boolean isMapMatched) {
            if (!position.isValid())
                return;
            if (!firstPositionSet) {
                Log.d("TAG","pos:" +position.getCoordinate());
                firstPositionSet = true;
            }
            GeoCoordinate pos = position.getCoordinate();
            Log.d(TAG, "New position: " + pos.getLatitude() + " / " + pos.getLongitude() + " / " + pos.getAltitude());
            // ... do something with position ...
        }

        @Override
        public void onPositionFixChanged(PositioningManager.LocationMethod method, PositioningManager.LocationStatus status) {
            Log.i(TAG, "Position fix changed : " + status.name() + " / " + method.name());
            // only allow guidance, when we have a position fix
            if (status == PositioningManager.LocationStatus.AVAILABLE &&
                    (method == PositioningManager.LocationMethod.NETWORK || method == PositioningManager.LocationMethod.GPS)) {
                // we have a fix, so allow start of guidance now

            }
        }
    };

}