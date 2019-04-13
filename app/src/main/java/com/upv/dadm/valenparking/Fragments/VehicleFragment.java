package com.upv.dadm.valenparking.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.upv.dadm.valenparking.R;

import java.io.IOException;
import java.util.List;

public class VehicleFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    View view;

    private String TAG = "Vehicle";


    private ImageButton locationButton;
    private ImageButton mapButton;
    private TextView streetText;
    private TextView descrText;
    private EditText descrEdit;
    private Button deleteButton;


    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    private String locationStreet;
    private String locationDescription;
    private String locationCoord;

    private GoogleApiClient client;
    private FusedLocationProviderClient locationProviderClient;
    private MyLocationCallback callback;

    public VehicleFragment(){ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_vehicle, null);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();

        locationStreet = prefs.getString("myLocation", "");
        locationDescription = prefs.getString("myLocationDescription", "");
        locationCoord = prefs.getString("myLocationCoord","");

        locationButton = view.findViewById(R.id.vehicleSaveButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
        mapButton = view.findViewById(R.id.vehicleMapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMaps();
            }
        });
        deleteButton = view.findViewById(R.id.vehicleDeleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanData();
                updateUi();
            }
        });
        streetText = view.findViewById(R.id.vehicleStreetText);
        descrText = view.findViewById(R.id.vehicleDescriptionText);
        descrEdit = view.findViewById(R.id.vehicleEditText);


        callback = new MyLocationCallback();
        client = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        updateUi();


        return view;
    }

    @Override
    public void onStart() {
        client.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        client.disconnect();
        locationProviderClient.removeLocationUpdates(callback);
        super.onStop();
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            locationProviderClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(getContext());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),  location.getLongitude(),1);
                                    if(addresses.size() > 0) {
                                        locationStreet = "" + addresses.get(0).getAddressLine(0);
                                        locationDescription = descrEdit.getText() + "";
                                        locationCoord =  "http://maps.google.com/maps?daddr=" + location.getLatitude() + "," + location.getLongitude() + " (" + "Tu coche" + ")";
                                        editor.putString("myLocation", locationStreet);
                                        editor.putString("myLocationDescription", locationDescription);
                                        editor.putString("myLocationCoord", locationCoord);
                                        editor.apply();
                                    } else {
                                        Toast.makeText(getContext(),getString(R.string.vehicle_not_known_street_string), Toast.LENGTH_LONG).show();
                                    }
                                }catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(getContext(),getString(R.string.vehicle_cant_get_location_string), Toast.LENGTH_LONG).show();
                            }
                            updateUi();
                        }
                    }).addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),getString(R.string.vehicle_not_known_street_string), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void cleanData(){
        editor.putString("myLocation",null);
        editor.putString("myLocationDescription",null);
        editor.apply();
        locationStreet = "";
    }

    public void openMaps(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationCoord));
        getContext().startActivity(intent);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(10000);
        request.setFastestInterval(5000);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationProviderClient.requestLocationUpdates(request,callback,null);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(),getString(R.string.connection_failed_string), Toast.LENGTH_LONG);
    }

    private void updateUi(){
        if(locationStreet.equals("")){
            //Visibles
            locationButton.setVisibility(View.VISIBLE);
            descrEdit.setVisibility(View.VISIBLE);
            //No Visibles
            mapButton.setVisibility(View.INVISIBLE);
            streetText.setVisibility(View.INVISIBLE);
            descrText.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
        } else {
            //Visibles
            mapButton.setVisibility(View.VISIBLE);
            streetText.setVisibility(View.VISIBLE);
            descrText.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            //No Visibles
            locationButton.setVisibility(View.INVISIBLE);
            descrEdit.setVisibility(View.INVISIBLE);

            streetText.setText(locationStreet);
            descrText.setText(locationDescription);
        }
    }


    private class MyLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
        }
    }
}
