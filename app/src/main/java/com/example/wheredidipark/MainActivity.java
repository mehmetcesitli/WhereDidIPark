package com.example.wheredidipark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.content.pm.PackageManager;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
//import android.location.LocationListener;

import android.net.Uri;
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


    private Button parkHereButton;
    private Button whereAmIButton;
    private Button getMeToMyCarButton;
    private TextView currentlyParked;
    private TextView parkingSpotAddress;
    private TextView parkingSpotCoordinates;
    private TextView currentLocationText;
    private TextView currentAddressText;
    private TextView currentLocationCoordinates;
    private TextView distanceToCarText;


    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private double latitude;
    private double longitude;
    private String fullAddress;

    public static final int RequestPermissionCode = 1;

    private Geocoder geocoder;
    private List<Address> addresses;

    private LocationInfo parkingSpotLocation;
    private LocationInfo currentLocation;

    class LocationInfo{
        private double latitude;
        private double longitude;
        private String address;

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public LocationInfo (double latitude, double longitude, String address){
            setLatitude(latitude);
            setLongitude(longitude);
            setAddress(address);
        }

    }

    public String distanceBetween (LocationInfo l1, LocationInfo l2){

        Location location1 = new Location("");
        Location location2 = new Location("");

        location1.setLatitude(l1.getLatitude());
        location1.setLongitude(l1.getLongitude());
        location2.setLatitude(l2.getLatitude());
        location2.setLongitude(l2.getLongitude());

        String distanceInKiloMeters = String.format("%.2f",location1.distanceTo(location2)/1000);

        return distanceInKiloMeters;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parkHereButton = findViewById(R.id.parkHereButton);
        whereAmIButton = findViewById(R.id.whereAmIButton);
        getMeToMyCarButton = findViewById(R.id.getMeToMyCarButton);

        currentlyParked = findViewById(R.id.currentlyParkedText);
        parkingSpotAddress = findViewById(R.id.parkingSpotAddress);
        parkingSpotCoordinates = findViewById(R.id.parkingSpotCoordinates);
        currentLocationText = findViewById(R.id.currentLocationText);
        currentAddressText = findViewById(R.id.currentAddressText);
        currentLocationCoordinates = findViewById(R.id.currentLocationCoordinates);
        distanceToCarText = findViewById(R.id.distanceToCarText);

        currentlyParked.setVisibility(View.INVISIBLE);
        parkingSpotCoordinates.setVisibility(View.INVISIBLE);
        parkingSpotAddress.setVisibility(View.INVISIBLE);

        whereAmIButton.setVisibility(View.INVISIBLE);
        getMeToMyCarButton.setVisibility(View.INVISIBLE);
        currentLocationText.setVisibility(View.INVISIBLE);
        currentAddressText.setVisibility(View.INVISIBLE);
        currentLocationCoordinates.setVisibility(View.INVISIBLE);
        distanceToCarText.setVisibility(View.INVISIBLE);


        parkHereButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                parkingSpotLocation = new LocationInfo(latitude,longitude,fullAddress);

                parkingSpotCoordinates.setText(parkingSpotLocation.getLatitude() + ", " + parkingSpotLocation.getLongitude());
                parkingSpotAddress.setText(parkingSpotLocation.getAddress());

                currentlyParked.setVisibility(View.VISIBLE);
                parkingSpotCoordinates.setVisibility(View.VISIBLE);
                parkingSpotAddress.setVisibility(View.VISIBLE);
                whereAmIButton.setVisibility(View.VISIBLE);

            }

        });


        whereAmIButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                currentLocation = new LocationInfo(latitude,longitude,fullAddress);

                currentLocationCoordinates.setText(currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
                currentAddressText.setText(currentLocation.getAddress());
                distanceToCarText.setText(distanceBetween(parkingSpotLocation,currentLocation)+ " km");

                getMeToMyCarButton.setVisibility(View.VISIBLE);
                currentLocationText.setVisibility(View.VISIBLE);
                currentAddressText.setVisibility(View.VISIBLE);
                currentLocationCoordinates.setVisibility(View.VISIBLE);
                distanceToCarText.setVisibility(View.VISIBLE);

            }
        });

        getMeToMyCarButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Uri uri = Uri.parse("geo:37.7749,-122.4194");
                Uri uri = Uri.parse("http://maps.google.com/maps?saddr="+currentLocation.getLatitude()+","+currentLocation.getLongitude()+"&daddr="+parkingSpotLocation.getLatitude()+","+parkingSpotLocation.getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());

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

                                latitude = location.getLatitude();
                                longitude =location.getLongitude();

                                try {

                                    addresses = geocoder.getFromLocation(latitude,longitude,1);

                                    String address = addresses.get(0).getAddressLine(0);
                                    String area = addresses.get(0).getLocality();
                                    String city = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();

                                    fullAddress = address+", "+ area +", "+ city + ", "+ country +", "+ postalCode;

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

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
