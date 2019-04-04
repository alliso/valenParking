package com.upv.dadm.valenparking.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upv.dadm.valenparking.Adapters.CustomInfoWindowGoogleMap;
import com.upv.dadm.valenparking.Pojo.GoogleMapInfoWindowData;
import com.upv.dadm.valenparking.R;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final int REQUEST_LOCATION = 3;

    private GoogleMap map;
    private GoogleApiClient client;
    //private FusedLocationProviderClient locationProviderClient;
    private SupportMapFragment mapFragment;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    public MapFragment(){ }

   /* @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);
        if (mapFragment == null) {
            Log.d("USER_DEBUG", "PIccolo");
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.map_fragment, mapFragment).commit();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        addMarkers();

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }

        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getContext());
        map.setInfoWindowAdapter(customInfoWindow);
    }

    public void addMarkers() {
        Log.d("USER_DEBUG", currentUser.getDisplayName());

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("parkings");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String latitud = postSnapshot.child("coordinates").child("lat").getValue().toString();
                    String longitud = postSnapshot.child("coordinates").child("lon").getValue().toString();
                    String name = postSnapshot.child("name").getValue().toString();
                    name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                    String address = postSnapshot.child("address").getValue().toString();
                    String totalPlaces = postSnapshot.child("total").getValue().toString();
                    String freePlaces = postSnapshot.child("free").getValue().toString();


                    LatLng latLng = new LatLng(Float.valueOf(latitud), Float.valueOf(longitud));

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Valencia");

                    int freePlacesInt = Integer.parseInt(freePlaces);
                    int totalPlacesInt = Integer.parseInt(totalPlaces);
                    if (freePlacesInt > totalPlacesInt / 2) {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    } else if (freePlacesInt > 10) {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    } else if (freePlacesInt > 0) {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    } else {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }

                    GoogleMapInfoWindowData infoWindow = new GoogleMapInfoWindowData();
                    infoWindow.setName(name);
                    infoWindow.setAddress(address);
                    infoWindow.setPlaces(freePlaces + " libres de " + totalPlaces);
                    infoWindow.setType("Tipo C");
                    infoWindow.setFavourite(false);

                    map.addMarker(markerOptions).setTag(infoWindow);
                    builder.include(markerOptions.getPosition());
                }

                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                map.animateCamera(cu);

                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        GoogleMapInfoWindowData infoWindow = new GoogleMapInfoWindowData();
                        infoWindow = (GoogleMapInfoWindowData) marker.getTag();
                        Toast.makeText(getContext(), "Has añadido '" + infoWindow.getName() + "' a favoritos", Toast.LENGTH_SHORT).show();
                        infoWindow.setFavourite(true);
                        marker.hideInfoWindow();
                        marker.showInfoWindow();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            }
        }
    }*/

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_SHORT);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT);
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}