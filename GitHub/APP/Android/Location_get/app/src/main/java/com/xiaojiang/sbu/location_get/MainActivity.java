package com.xiaojiang.sbu.location_get;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView txtLong,txtAddress;
    private ProgressBar pb;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private ResultReceiver resultReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultReceiver = new AddressResultReceiver(new Handler());

        txtLong = findViewById(R.id.outputLong);
        txtAddress = findViewById(R.id.outputAdress);
        pb = findViewById(R.id.bar);
        findViewById(R.id.getlocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
                }else{
                    getCurrentLocation();
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length>0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }else{
                Toast.makeText(this, "permition dined",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation(){
        pb.setVisibility(View.VISIBLE);

        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(MainActivity.this).requestLocationUpdates(locationRequest, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);
                if(locationResult != null &&locationResult.getLocations().size() > 0 ){
                    int latestLocationIdex = locationResult.getLocations().size() - 1 ;
                    double latitude = locationResult.getLocations().get(latestLocationIdex).getLatitude();
                    double longitude = locationResult.getLocations().get(latestLocationIdex).getLongitude();
                    txtLong.setText(
                        String.format(
                            "Latitude : %s \n Longitude : %s",
                                latitude,longitude
                        )
                    );
                    Location location = new Location("providerNA");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    fetchAddressFromLAtLong(location);
                }
                else{
                    pb.setVisibility(View.GONE);
                }

            }
        }, Looper.getMainLooper());
    }

    private void fetchAddressFromLAtLong(Location location){
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,location);
        startService(intent);
    }


    private  class AddressResultReceiver extends ResultReceiver{

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if(resultCode == Constants.SUCCESS_RESULT){
                txtAddress.setText(resultData.getString(Constants.RESULT_DATA_KEY));
            }else{
                Toast.makeText(MainActivity.this,resultData.getString(Constants.RESULT_DATA_KEY),Toast.LENGTH_SHORT).show();
            }
            pb.setVisibility(View.GONE);
        }
    }


    private void requestLocationUpdate(final Context context, LocationCallback locationCallback){


    }

}
