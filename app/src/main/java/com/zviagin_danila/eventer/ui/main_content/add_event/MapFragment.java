package com.zviagin_danila.eventer.ui.main_content.add_event;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.zviagin_danila.eventer.R;

import java.util.List;


public class MapFragment extends Fragment {

    LocationManager locationManager;
    LocationListener locationListener;
    LatLng userLatLng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.add_event_google_map);

        // Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                // When map is loaded
                // Set current user position
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        // check userLatLng that marker sets only one time
                        if (userLatLng == null) {
                            // Store user latLng
                            userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            // add user marker to map
                            googleMap.clear(); // clear all location marker in google maps
                            googleMap.addMarker(new MarkerOptions().position(userLatLng).title("Your location"));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLng));
                        }
                    }
                };

                // set on map click listener that add marker where user click
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        // When clicked on map
                        // Initialize marker options
                        MarkerOptions markerOptions = new MarkerOptions();
                        // Set position on marker
                        markerOptions.position(latLng);
                        // Set title of marker
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                        // Remove all marker
                        googleMap.clear();
                        // Animating to zoom the marker
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                latLng, 10
                        ));
                        // Add marker on map
                        googleMap.addMarker(markerOptions);

                        userLatLng = latLng;
                    }
                });

                askLocationPermission(googleMap);
            }
        });

        // Return view
        return view;
    }

    private void askLocationPermission(@NonNull GoogleMap googleMap) {
        Dexter.withContext(getActivity()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                if (ActivityCompat.checkSelfPermission(getActivity().getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


                try {
                    // getting user last location to set the default location marker in the map
                    Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    // add user marker to map
                    userLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    googleMap.clear(); // clear all location marker in google maps
                    googleMap.addMarker(new MarkerOptions().position(userLatLng).title("Your location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLng));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public String getAddress(double latitude, double longitude) {
        String address = "";
        String city = "";
        String country = "";
        if (latitude != 0 && longitude != 0) {
            try {
                Geocoder geocoder = new Geocoder(getActivity());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                address = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getAddressLine(1);
                country = addresses.get(0).getAddressLine(2);
                Log.d("TAG", "address = " + address + ", city = " + city + ", country = " + country);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "latitude and longitude are null", Toast.LENGTH_LONG).show();
        }
        return country + ", " + city + ", " + address;
    }

    public double getLatitude() {
        Log.i("Location latitude", String.valueOf(userLatLng.latitude));
        return userLatLng.latitude;
    }

    public double getLongitude() {
        Log.i("Location longitude", String.valueOf(userLatLng.longitude));
        return userLatLng.longitude;
    }
}