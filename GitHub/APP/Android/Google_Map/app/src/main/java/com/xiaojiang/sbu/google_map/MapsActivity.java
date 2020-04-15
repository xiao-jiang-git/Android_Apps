package com.xiaojiang.sbu.google_map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraIdleListener, GoogleMap.InfoWindowAdapter, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private Location mLastLocation;
    private GoogleMap mMap;
    private TextView mTapTextView, mCameraTextView;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean mLocationUpdateState;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private MapView mapView;
    private Button setsa,setno;



    Button.OnClickListener mSat_listener = new Button.OnClickListener(){
        public void onClick(View v){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);      //设置为卫星模式
        }
    };
    Button.OnClickListener mNor_listener = new Button.OnClickListener(){
        public void onClick(View v){
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);      //设置为卫星模式
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mTapTextView = findViewById(R.id.mTapTextView);
        mCameraTextView = findViewById(R.id.mCameraTextView);

        setsa = (Button)findViewById(R.id.satellite);
        setsa.setOnClickListener(mSat_listener);
        setno = (Button)findViewById(R.id.normal);
        setno.setOnClickListener(mNor_listener);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        getCurrentLocation();
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

        UiSettings setting = googleMap.getUiSettings();
        setting.setCompassEnabled(true);
        setting.setMyLocationButtonEnabled(true);
        setting.setZoomControlsEnabled(true);
        setting.setIndoorLevelPickerEnabled(true);

        mMap = googleMap;



        // Add a marker in Sydney and move the camera
        LatLng newyork = new LatLng(40, -74);
        mMap.addMarker(new MarkerOptions().position(newyork).title("Marker in NewYork"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newyork));
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraIdleListener(this);






    }

    private void setUpMap(Location location) {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }


        mMap.setMyLocationEnabled(true);

        mLastLocation = location;
        // 4
        if (mLastLocation != null) {
            LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation
                    .getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
        }

    }


    @Override
    public void onCameraIdle() {
        int lat = (int)mMap.getCameraPosition().target.latitude;
        int longi = (int)mMap.getCameraPosition().target.longitude;
        float zoom = mMap.getCameraPosition().zoom;
        int tilt = (int)mMap.getCameraPosition().tilt;
        mCameraTextView.setText("lat/long :" + lat +"/" +longi + "\n" + "zoom :"+ zoom +"\n" + "tilt :" + tilt);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mTapTextView.setText("tapped, point=" + (int)latLng.latitude +","+(int)latLng.longitude );
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mTapTextView.setText("long pressed, point=" + (int)latLng.latitude +","+(int)latLng.longitude );
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));


//        String sUrl = "google.streetview:cbll="+latLng.latitude+","+latLng.longitude;	//生成Uri字符串
//        Intent i = new Intent();							//创建Intent对象
//        i.setAction(Intent.ACTION_VIEW);				//设置Intent的Action
//        Uri uri = Uri.parse(sUrl);						//生成Uri对象
//        i.setData(uri);									//设置Intent的Data
//        startActivity(i);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    private void getCurrentLocation(){

        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(MapsActivity.this).requestLocationUpdates(locationRequest, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(MapsActivity.this).removeLocationUpdates(this);
                if(locationResult != null &&locationResult.getLocations().size() > 0 ){
                    int latestLocationIdex = locationResult.getLocations().size() - 1 ;
                    double latitude = locationResult.getLocations().get(latestLocationIdex).getLatitude();
                    double longitude = locationResult.getLocations().get(latestLocationIdex).getLongitude();

                    Location location = new Location("providerNA");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    setUpMap(location);
                }
                else{
                }

            }
        }, Looper.getMainLooper());
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
