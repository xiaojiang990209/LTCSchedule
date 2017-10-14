package com.roger.ltcschedule;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView textView;
    private Button executeFunctionsButton;
    private Marker mCurrLocationMarker;
    private Marker mSpecifiedLocationMarker;
    private RouteDatabase routeDatabase;
    private List<RouteStopModel> busStops = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        textView = (TextView) findViewById(R.id.text);
        executeFunctionsButton = (Button) findViewById(R.id.execte_functions_button);
        executeFunctionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessNearbyBusStops(mMap.getCameraPosition().target);
                //appendResultToTextView(busStops);

            }
        });
        routeDatabase = new RouteDatabase(this).createDatabase();
        busStops = new LinkedList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Disable location update once app is paused.
     */
    @Override
    public void onPause() {
        super.onPause();

        //Stops location updates when Activity is no longer active
        if(mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        textView.setText(textView.getText() + "onMapReady: \n");
        mMap = googleMap;

        //Initialize Google Play services
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            } else {
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        //Add a camera move listener to the map to capture if the user moves the camera
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            private int CAMERA_WAIT_THRESHOLD_TIME = 800;
            private long lastSnap = 0;
            @Override
            public void onCameraMove() {
                //Get current time in milliseconds
                long snap = System.currentTimeMillis();
                if(snap - lastSnap < CAMERA_WAIT_THRESHOLD_TIME) {
                    return;
                }
                textView.setText(textView.getText() + "onCameraMove: " + mMap.getCameraPosition().target + "\n");
                //accessNearbyBusStops(mMap.getCameraPosition().target);
                lastSnap = snap;
            }
        });
    }

    // buildGoogleApiClient() creates a google api client and connects it.
    // buildGoogleApiClient: Void -> Void
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    // checkLocationPermission() checks if the application is granted with the permission to access location
    // checkLocationPermisson(): Void -> Bool
    public boolean checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //Should we show an explanation?
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Shows an explanation to the user asynchronously -- dont block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission
                new AlertDialog.Builder(this)
                        .setTitle("Locaiton Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        }).create().show();
            } else {
                //No explanation needed, we can request the permission
                ActivityCompat.requestPermissions(MapsActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
        return true;
    }

    // onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResult) handles permission after the user
    //    responds to the request to use location
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if(grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission granted. Do the location related task you need to do
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if(mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    //Disable the functionality that depends on this permission
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //here mLastLocation = null on the first call to onLocationChanged
        if(mLastLocation != null) {
            textView.setText(textView.getText() + "onLocationChanged: " + location.distanceTo(mLastLocation) + "\n");
            textView.setText(textView.getText() + "mLastLocation accuracy: " + mLastLocation.getAccuracy() + "new location accuracy:" + location.getAccuracy() + "\n");
        }

        if(mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Current position");
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera on the first location udpate
        if(mLastLocation == null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }
        mLastLocation = location;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        textView.setText(textView.getText() + "onConnected:\n");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(20f);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    // accessNearByBusStops(LatLng location) takes a specific location
    //      given by the google map service and performs a query to find
    //      the nearby bus stops. Then it processes the json response and
    //      returns the List of JavaBean RouteStopModel
    // accessNearByBusStops: LatLng -> List<RouteStopModel>
    public void accessNearbyBusStops(LatLng location) {
        //Clear the List that contains previous query results so that
        // it can be populated with fresh new results.
        busStops.clear();
        textView.append("on accessNearbyBusStops:" + "\n");
        Log.d(TAG, "accessNearbyBusStops: ");
        RequestQueue queue = Volley.newRequestQueue(this);
//        String url = "https://maps.googleapis.com/maps/api/place/search/json?sensor=false&radius=500&types=bus_station&location="
//                + location.latitude + "," + location.longitude + "&key=AIzaSyDd6O3sJ2ql_7-FO0IH7ePvDy3wTOOpXLQ";
        String url = "https://maps.googleapis.com/maps/api/place/search/json?sensor=false&radius=200&types=bus_station&location="
                + 42.991399 + "," + -81.273297 + "&key=AIzaSyDd6O3sJ2ql_7-FO0IH7ePvDy3wTOOpXLQ";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            //When hearing back from the server, parse the JSON response.
            @Override
            public void onResponse(String response) {
                textView.setText(textView.getText() + "Parsing json response...\n");
                try {
                    Thread.sleep(3000);
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }
                //textView.setText(textView.getText() + response + "\n");
                parseJSONResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("That didnt work!");
                try {
                    Thread.sleep(3000);
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        queue.add(stringRequest);

    }

    // parseJSONResponse(String response) parses the string response
    //      transferred back from the server. Then turns the json into
    //      the objects RouteStopModel
    // parseJSONResponse: String -> List<RouteStopModel>
    private void parseJSONResponse(String response) {
        textView.append("on parseJSONResponse: " + "\n");
        Log.d(TAG, "parseJSONResponse: ");
        routeDatabase.open();
        try {
            JSONObject json = new JSONObject(response);
            JSONArray jsonArray = json.getJSONArray("results");
            //For every nearby bus stop found
            for(int i = 0 ; i < jsonArray.length() ; i++) {
                //Get each bus stop
                JSONObject bus_stop = jsonArray.getJSONObject(i);
                //Acquire stop name
                String stop_id = bus_stop.getString("name");
                //If the stop name contains the stop id, then
                //query the database to find time.
                if(stop_id.contains("#")) {
                    stop_id = stop_id.substring(stop_id.indexOf('#') + 1);
                } else {
                    continue;
                }
                textView.append("parsing... " + stop_id + "\n");
                Log.d(TAG, "parseJSONResponse: " + stop_id);
                //Query the database to find which buses are in service at that stop.
                Cursor cursor = routeDatabase.query(stop_id);
                if(cursor != null) {
                    // Iterate through all returned items
                    while (cursor.moveToNext()) {
                        String routeNumber = cursor.getString(cursor.getColumnIndex(RouteDatabase.ROUTE_COLUMN_NAME));
                        String routeDirection = cursor.getString(cursor.getColumnIndex(RouteDatabase.DIRECTION_COLUMN_NAME));
                        RouteStopModel routeStopModel = new RouteStopModel();
                        routeStopModel.setRouteNumber(routeNumber);
                        routeStopModel.setDirection(routeDirection);
                        routeStopModel.setStopId(stop_id);
                        //Pass the routeStopModel into the JsoupAsyncTask and
                        //start requesting the real time.
                        new JsoupAsyncTask(routeStopModel).execute();
                        busStops.add(routeStopModel);
                    }
                }
            }
            if(busStops.size() != 0) {
                for(RouteStopModel routeStopModel: busStops) {
                    textView.append(routeStopModel.toString() + "\n");
                }
            } else {
                textView.append("busStops size = 0 Error!");
            }
        } catch(JSONException e) {
            e.printStackTrace();
        } finally {
            routeDatabase.close();
        }
    }

    private void appendResultToTextView(List<RouteStopModel> routeStopModels) {
        for(RouteStopModel routeStopModel : routeStopModels) {
            textView.append(routeStopModel + "\n");
        }
    }

}