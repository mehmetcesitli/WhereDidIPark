package com.example.wheredidipark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.content.pm.PackageManager;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
//import android.location.LocationListener;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import com.google.android.gms.location.LocationListener;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks,OnConnectionFailedListener /*implements LocationListener*/ {


    Button parkHereButton;
    TextView currentlyParked;
    TextView parkingSpotAddress;
    TextView parkingSpotCoordinates;

    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    String latitude;
    String longitude;
    double dLatitude;
    double dLongitude;
    public static final int RequestPermissionCode = 1;

    Geocoder geocoder;
    List<Address> addresses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parkHereButton = findViewById(R.id.parkHereButton);
        currentlyParked = findViewById(R.id.currentlyParkedText);
        parkingSpotAddress = findViewById(R.id.parkingSpotAddress);
        parkingSpotCoordinates = findViewById(R.id.parkingSpotCoordinates);

        parkingSpotCoordinates.setText("Longitude, Latitude");
        currentlyParked.setVisibility(View.INVISIBLE);
        parkingSpotCoordinates.setVisibility(View.INVISIBLE);
        parkingSpotAddress.setVisibility(View.INVISIBLE);

        parkHereButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                currentlyParked.setVisibility(View.VISIBLE);
                parkingSpotCoordinates.setVisibility(View.VISIBLE);
                parkingSpotAddress.setVisibility(View.VISIBLE);

            }

        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        geocoder = new Geocoder(this, Locale.getDefault());


            try {

                addresses = geocoder.getFromLocation(dLatitude,dLongitude,1);

                String address = addresses.get(0).getAddressLine(0);
                String area = addresses.get(0).getLocality();
                String city = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();

                String fullAddress = address+", "+ area +", "+ city + ", "+ country +", "+ postalCode;

                parkingSpotAddress.setText(fullAddress);

            } catch (IOException e) {
              e.printStackTrace();
            }


        }


    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("MainActivity", "Connection Suspended.");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("MainActivity","Connection Failed: "+connectionResult.getErrorCode());

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {

                                latitude = String.valueOf(location.getLatitude());
                                longitude = String.valueOf(location.getLongitude());

                                parkingSpotCoordinates.setText(latitude + ", " + longitude);
                            }
                        }
                    });
        }
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{ACCESS_FINE_LOCATION}, RequestPermissionCode);
    }




    }
