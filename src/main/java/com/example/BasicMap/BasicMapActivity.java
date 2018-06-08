package com.example.BasicMap;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.IconCategory;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.LocationDataSourceHERE;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.mapping.MapState;
import com.here.android.mpa.mapping.OnMapRenderListener;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteTta;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.RoutingError;
import com.nokia.maps.RouteManagerImpl;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.here.android.mpa.routing.Route.WHOLE_ROUTE;
//import com.example.BasicMap.MyService.LocalBinder;


public class BasicMapActivity extends AppCompatActivity  {

    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    // Permissions that need to be explicitly requested from end user.
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.INTERNET,
            Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.WAKE_LOCK};
    static BasicMapActivity instance;
    //  MyService.LocalBinder  localBinder=service.new LocalBinder();
    MyService service = new MyService();
    MapRoute mapRoute;
    private MyService myService;
    private boolean bound = false;
    // map embedded in the map fragment
    private Map map = null;
    // map fragment embedded in this activity
    private MapFragment mapFragment = null;
    // positioning manager instance
    private PositioningManager mPositioningManager;
    // HERE location data source instance
    private LocationDataSourceHERE mHereLocation;
    // flag that indicates whether maps is being transformed
    private boolean mTransforming;
    // callback that is called when transforming ends
    private Runnable mPendingUpdate;
    Location location;
    final private Map.OnTransformListener onTransformListener = new Map.OnTransformListener() {
        @Override
        public void onMapTransformStart() {
            mTransforming = true;

        }

        @Override
        public void onMapTransformEnd(MapState mapState) {
            mTransforming = false;
            if (mPendingUpdate != null) {
                mPendingUpdate.run();
                mPendingUpdate = null;
            }
        }

    };
    private TextView mLocationInfo,txtETA,txtdistance;
    private MapMarker m_positionIndicatorFixed = null;
    private PointF m_mapTransformCenter;
    final private NavigationManager.RoadView.Listener roadViewListener = new NavigationManager.RoadView.Listener() {
        @Override
        public void onPositionChanged(GeoCoordinate geoCoordinate) {
            // an active RoadView provides coordinates that is the map transform center of it's
            // movements.
            m_mapTransformCenter = map.projectToPixel
                    (geoCoordinate).getResult();
        }
    };
    private boolean m_returningToRoadViewMode = false;
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 30000;
    //30000;
    private static final float LOCATION_DISTANCE = 1000;

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


//    @Override
//    protected void onStart() {
//        super.onStart();
//        // bind to Service
//        Intent intent = new Intent(this, MyService.class);
//        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//    }

    //    @Override
//    protected void onStop() {
//        super.onStop();
//        // Unbind from service
//        if (bound) {
//          //  myService.setCallbacks(null); // unregister
//            unbindService(serviceConnection);
//            bound = false;
//        }
//    }
    private double m_lastZoomLevelInRoadViewMode = 0.0;
    // listen for positioning events
    private PositioningManager.OnPositionChangedListener mapPositionHandler = new PositioningManager.OnPositionChangedListener() {
        @Override
        public void onPositionUpdated(final PositioningManager.LocationMethod locationMethod, final GeoPosition geoPosition, final boolean mapMatched) {
            final GeoCoordinate coordinate = geoPosition.getCoordinate();

            if (mTransforming) {
                mPendingUpdate = new Runnable() {
                    @Override
                    public void run() {
                        onPositionUpdated(locationMethod, geoPosition, mapMatched);
                    }
                };
            } else {
                map.setCenter(coordinate, Map.Animation.BOW);
                updateLocationInfo(locationMethod, geoPosition);
            }

        }

        @Override
        public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {

        }


    };
    // application design suggestion: pause roadview when user gesture is detected.
    private MapGesture.OnGestureListener gestureListener = new MapGesture.OnGestureListener() {
        @Override
        public void onPanStart() {
            pauseRoadView();
        }

        @Override
        public void onPanEnd() {
        }

        @Override
        public void onMultiFingerManipulationStart() {
        }

        @Override
        public void onMultiFingerManipulationEnd() {
        }


        @Override
        public boolean onMapObjectsSelected(List<ViewObject> objects) {
            return false;
        }

        @Override
        public boolean onTapEvent(PointF p) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(PointF p) {
            return false;
        }

        @Override
        public void onPinchLocked() {
        }

        @Override
        public boolean onPinchZoomEvent(float scaleFactor, PointF p) {
            pauseRoadView();
            return false;
        }

        @Override
        public void onRotateLocked() {
        }

        @Override
        public boolean onRotateEvent(float rotateAngle) {
            return false;
        }

        @Override
        public boolean onTiltEvent(float angle) {
            pauseRoadView();
            return false;
        }

        @Override
        public boolean onLongPressEvent(PointF p) {
            return false;
        }

        @Override
        public void onLongPressRelease() {
        }

        @Override
        public boolean onTwoFingerTapEvent(PointF p) {
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();


        checkPermissions();
        instance = this;
        Intent myIntent = new Intent(getBaseContext(), MyService.class);
        startService(myIntent);
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

    @Override
    protected void onPause() {
        super.onPause();
        if (mPositioningManager != null) {
            mPositioningManager.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPositioningManager != null) {
            mPositioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
        }
    }

    /**
     * Callbacks for service binding, passed to bindService()
     */
//    private ServiceConnection serviceConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            // cast the IBinder and get MyService instance
//            bound = true;
//
//          //  LocalBinder binder = (LocalBinder) service;
//         //    myService = ((MyService.LocalBinder) service).getServerInstance();
//           // myService = binder.getServerInstance();
//         //   myService.setCallbacks(BasicMapActivity.this);
//
//
//          //  myService.setCallbacks(this); // register
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            bound = false;
//            myService = null;
//        }
//    };
    private void initialize() {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        setContentView(R.layout.activity_basic_map);

        mLocationInfo = findViewById(R.id.textViewLocationInfo);
        txtETA = findViewById(R.id.textDuration);
        txtdistance = findViewById(R.id.textDistance);



        Log.d("if", "network_provider");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("mLocationManager", ":" + mLocationManager);
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, locationListener);

                        location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        Log.d("location", ":" + location);
                        Log.d("basicmap", "network lbs provider:" + (location == null ? "null" : String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude())));

                }

                if(location!= null) {

                    final Double current_lat = location.getLatitude();
                    final Double current_long = location.getLongitude();

                    Toast.makeText(getApplicationContext(), " current loc:" + location, Toast.LENGTH_LONG).show();


                    Intent intent = getIntent();
                    String latitude = intent.getStringExtra("Lat");
                    String longitude = intent.getStringExtra("Long");

                    final Double doubLat = Double.valueOf(latitude);
                    final Double doubLong = Double.valueOf(longitude);


                    // Search for the map fragment to finish setup by calling init().
                    mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);

                    // Set up disk cache path for the map service for this application
                    boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
                            getApplicationContext().getFilesDir().getAbsolutePath() + ".here-maps",
                            "MapIntent");
                    try {

                        if (!success) {
                            Toast.makeText(getApplicationContext(), "Unable to set isolated disk cache path.", Toast.LENGTH_LONG).show();
                        } else {
                            mapFragment.init(new OnEngineInitListener() {
                                @Override
                                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                                    if (error == OnEngineInitListener.Error.NONE) {

                                        mapFragment.getMapGesture().addOnGestureListener(gestureListener, 100, true);

                                        // retrieve a reference of the map from the map fragment
                                        map = mapFragment.getMap();
                                        List<String> schemes = map.getMapSchemes();
                                        map.setMapScheme(schemes.get(15));

                                        map.setTrafficInfoVisible(true);
                                        map.setExtrudedBuildingsVisible(true);  // enable 3D building footprints
                                        map.setLandmarksVisible(true);  // 3D Landmarks visible
                                        map.setCartoMarkersVisible(IconCategory.ALL, true);  // show embedded map markers
                                        map.setSafetySpotsVisible(true); // show speed cameras as embedded markers on the map

                                        // Set the zoom level to the average between min and max
                                        map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);

                                        map.addTransformListener(onTransformListener);

                                        mHereLocation = LocationDataSourceHERE.getInstance();

                                        if (mHereLocation != null) {
                                            mPositioningManager = PositioningManager.getInstance();
                                            Log.d("mPositioningManager", ":" + mPositioningManager);

                                            mPositioningManager.setDataSource(mHereLocation);
                                            mapFragment.getPositionIndicator().setVisible(true);

                                            mPositioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK);
                                            GeoCoordinate destn_coor = new GeoCoordinate(doubLat, doubLong);


                                            //    m_positionIndicatorFixed.setVisible(true);
                                            //     m_positionIndicatorFixed.setCoordinate(map.getCenter());
                                            //    map.addMapObject(m_positionIndicatorFixed);

                                            // setting MapUpdateMode to RoadView will enable automatic map
                                            // movements and zoom level adjustments


                                            Image image = new Image();
                                            try {
                                                image.setImageResource(R.drawable.markerformap);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            MapMarker mapMarker = new MapMarker(destn_coor, image);
                                            map.addMapObject(mapMarker);

                                            NavigationManager.getInstance().setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);

                                            // adjust tilt to show 3D view
                                            map.setTilt(80);


                                            NavigationManager.getInstance().setMap(map);

                                            //  mPositioningManager.addListener(new WeakReference<>(mapPositionHandler));
                                            //  if (mPositioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK))
                                            if (1 == 1) {
                                                // Position updates started successfully.
                                                mapFragment.getPositionIndicator().setVisible(true);

                                                // calculate a route for navigation
                                                CoreRouter coreRouter = new CoreRouter();

                                                final RoutePlan routePlan = new RoutePlan();
                                                Log.d("test", ":" + mPositioningManager.getLocationStatus(PositioningManager.LocationMethod.GPS_NETWORK));
                                                GeoCoordinate geolocation = mPositioningManager.getPosition().getCoordinate();


                                                // Define waypoints for the route
                                                //   Log.d("msg test",new GeoCoordinate(geolocation.getLatitude(),geolocation.getLongitude()).toString());

                                                //nidhi
                                                //  GeoCoordinate lastPosition = mPositioningManager.getLastKnownPosition().getCoordinate();
                                                Log.d("geolocation", ":" + geolocation);

                                                RouteWaypoint startPoint = new RouteWaypoint(new GeoCoordinate(current_lat, current_long));

                                                //    RouteWaypoint startPoint = new RouteWaypoint(new GeoCoordinate(lastPosition.getLatitude(), lastPosition.getLongitude()));
                                                Log.d("start", "point:" + startPoint);

                                                //   RouteWaypoint startPoint = new RouteWaypoint(new GeoCoordinate(28.548244, 77.288719));
                                                RouteWaypoint destination = new RouteWaypoint(new GeoCoordinate(doubLat, doubLong));
                                                Route.TrafficPenaltyMode r = Route.TrafficPenaltyMode.OPTIMAL;

                                                routePlan.addWaypoint(startPoint);
                                                routePlan.addWaypoint(destination);


                                                try {

                                                    Log.d("test 2", ":" + mPositioningManager.getLocationStatus(PositioningManager.LocationMethod.GPS_NETWORK));

                                                    final RouteOptions routeOptions = new RouteOptions();
                                                    routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);

                                                    routeOptions.setRouteType(RouteOptions.Type.SHORTEST);
                                                    /* Calculate 1 route. */
                                                    routeOptions.setRouteCount(1);
                                                    routePlan.setRouteOptions(routeOptions);

                                                    // listen to real position updates. This is used when RoadView is
                                                    // not active.
                                                    mPositioningManager.addListener(
                                                            new WeakReference<PositioningManager.OnPositionChangedListener>(mapPositionHandler));


                                                    // listen to updates from RoadView which tells you where the map
                                                    // center should be situated. This is used when RoadView is active.
                                                    NavigationManager.getInstance().getRoadView().addListener(new
                                                            WeakReference<NavigationManager.RoadView.Listener>(roadViewListener));

                                                    map.setCenter(routePlan.getWaypoint(0).getNavigablePosition(), Map.Animation.NONE);


                                   /*     if (mapMarker == null) {
                                            Image image = new Image();
                                            try {
                                                image.setImageResource(R.drawable.markerformap);
                                            } catch (final IOException e) {
                                                e.printStackTrace();
                                            }

                                            map.addMapObject(mapMarker);
                                        } else {
                                            mapMarker.setCoordinate(destn_coor);
                                        }
                                       */

                                                    // coreRouter.setConnectivity(CoreRouter.Connectivity.ONLINE);
                                                    coreRouter.calculateRoute(routePlan, new CoreRouter.Listener() {
                                                        @Override
                                                        public void onCalculateRouteFinished(List<RouteResult> list,
                                                                                             RoutingError routingError) {


                                                            if (routingError == RoutingError.NONE) {

                                                                Log.d("list:", ":" + list);
                                                                Log.d("routingError", ":" + routingError);

                                                                Route route = list.get(0).getRoute();

                                                                if (route != null) {
                                                                    /* Create a MapRoute so that it can be placed on the map */
                                                                    mapRoute = new MapRoute(route);

                                                                    /* Show the maneuver number on top of the route */
                                                                    mapRoute.setManeuverNumberVisible(true);

                                                                    RouteTta routeTta = route.getTta(Route.TrafficPenaltyMode.OPTIMAL, WHOLE_ROUTE);
                                                                    int dur = routeTta.getDuration();

                                                                    float durInHr = dur / 3600;

                                                                    txtETA.setText("ETA:" + durInHr + "Hr");

                                                                    int distance = route.getLength() / 1000;

                                                                    txtdistance.setText("Distance:" + distance + "Km");

                                                                    NavigationManager.getInstance().setTrafficAvoidanceMode(NavigationManager.TrafficAvoidanceMode.DYNAMIC);


                                                                    /* Add the MapRoute to the map */
                                                                    map.addMapObject(mapRoute);

                                                                    mapRoute.setRenderType(MapRoute.RenderType.SECONDARY);

                                                                    /*
                                                                     * We may also want to make sure the map view is orientated properly
                                                                     * so the entire route can be easily seen.
                                                                     */
                                                                    GeoBoundingBox gbb = list.get(0).getRoute().getBoundingBox();
                                                                    map.zoomTo(gbb, Map.Animation.NONE, Map.MOVE_PRESERVE_ORIENTATION);

                                                                    // start navigation simulation travelling at 13 meters per second
                                                                    NavigationManager.getInstance().simulate(route, 25);

                                                                    //   NavigationManager.getInstance().startNavigation(route);
                                                                    //   NavigationManager.getInstance().getTrafficAvoidanceMode();


                                                                } else {
                                                                    Toast.makeText(getApplicationContext(),
                                                                            "Error:route results returned is not valid", Toast.LENGTH_LONG).show();
                                                                }
                                                            } else {


                                                                Toast.makeText(getApplicationContext(),
                                                                        "Error:route calculation returned error code: " + routingError, Toast.LENGTH_LONG).show();

                                                                //   mPositioningManager.stop();
                                                                // mPositioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);

                                                                //    onBackPressed();
                                                                //    startActivity(new Intent(getApplicationContext(),BasicMapActivity.class));

                                                            }
                                                        }

                                                        @Override
                                                        public void onProgress(int i) {

                                                        }
                                                    });
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            } else {
                                                Toast.makeText(BasicMapActivity.this, "PositioningManager.start: failed, exiting", Toast.LENGTH_LONG).show();
                                                finish();
                                            }

                                        }
                                    } else {
                                        System.out.println("ERROR: Cannot initialize Map Fragment");
                                    }
                                }

                            });
                        }


                        mapFragment.addOnMapRenderListener(new OnMapRenderListener() {
                            @Override
                            public void onPreDraw() {
                                if (m_positionIndicatorFixed != null) {
                                    if (NavigationManager.getInstance()
                                            .getMapUpdateMode().equals(NavigationManager.MapUpdateMode.ROADVIEW)) {
                                        if (!m_returningToRoadViewMode) {
                                            // when road view is active, we set the position indicator to align
                                            // with the current map transform center to synchronize map and map
                                            // marker movements.
                                            m_positionIndicatorFixed.setCoordinate(map.pixelToGeo(m_mapTransformCenter));
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onPostDraw(boolean b, long l) {

                            }

                            @Override
                            public void onSizeChanged(int i, int i1) {

                            }

                            @Override
                            public void onGraphicsDetached() {

                            }

                            @Override
                            public void onRenderBufferCreated() {

                            }


                        });
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Initialize" + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

    }

    /**
     * Checks the dynamically controlled permissions and requests missing permissions from end user.
     */
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
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }

    /**
     * Update location information.
     *
     * @param geoPosition Latest geo position update.
     */
    private void updateLocationInfo(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition) {
        if (mLocationInfo == null) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        final GeoCoordinate coord = geoPosition.getCoordinate();
        sb.append("Type: ").append(String.format(Locale.US, "%s\n", locationMethod.name()));
     //   sb.append("Coordinate:").append(String.format(Locale.US, "%.6f, %.6f\n", coord.getLatitude(), coord.getLongitude()));
     /*   if (coord.getAltitude() != GeoCoordinate.UNKNOWN_ALTITUDE) {
            sb.append("Altitude:").append(String.format(Locale.US, "%.2fm\n", coord.getAltitude()));
        }
        if (geoPosition.getHeading() != GeoPosition.UNKNOWN) {
            sb.append("Heading:").append(String.format(Locale.US, "%.2f\n", geoPosition.getHeading()));
        }*/
        if (geoPosition.getSpeed() != GeoPosition.UNKNOWN) {
            sb.append("Speed:").append(String.format(Locale.US, "%.2fkm/hr\n", geoPosition.getSpeed()*3.6));
        }
        if (geoPosition.getBuildingName() != null) {
            sb.append("Building: ").append(geoPosition.getBuildingName());
            if (geoPosition.getBuildingId() != null) {
                sb.append(" (").append(geoPosition.getBuildingId()).append(")\n");
            } else {
                sb.append("\n");
            }
        }
        if (geoPosition.getFloorId() != null) {
            sb.append("Floor: ").append(geoPosition.getFloorId()).append("\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        mLocationInfo.setText(sb.toString());
    }

    private void pauseRoadView() {
        // pause RoadView so that map will stop moving, the map marker will use updates from
        // PositionManager callback to update its position.

        if (NavigationManager.getInstance().getMapUpdateMode().equals(NavigationManager.MapUpdateMode.ROADVIEW)) {
            NavigationManager.getInstance().setMapUpdateMode(NavigationManager.MapUpdateMode.NONE);
            NavigationManager.getInstance().getRoadView().removeListener(roadViewListener);
            m_lastZoomLevelInRoadViewMode = map.getZoomLevel();
        }
    }

    private void resumeRoadView() {
        // move map back to it's current position.
        map.setCenter(mPositioningManager.getPosition().getCoordinate(), Map
                        .Animation.BOW, m_lastZoomLevelInRoadViewMode, Map.MOVE_PRESERVE_ORIENTATION,
                80);
        // do not start RoadView and its listener until the map movement is complete.
        m_returningToRoadViewMode = true;
    }


}
