package com.upv.dadm.valenparking.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.upv.dadm.valenparking.Adapters.CustomInfoWindowGoogleMap;
import com.upv.dadm.valenparking.Parkings;
import com.upv.dadm.valenparking.Pojo.GoogleMapInfoWindowData;
import com.upv.dadm.valenparking.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Constants
    private static final int REQUEST_LOCATION_PERMISSIONS_CODE = 3;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    // Widgets
    private AutoCompleteTextView searchText;
    private ImageView gpsImage;

    // Variables
    private Boolean mLocationPermissionsGranted = false;

    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private CollectionReference userDBRef;
    private String documentId = "";
    private JSONArray favouritesJSON;
    private List<Parkings> favouriteParkings;



    private View view;
    private ProgressBar progressBar;

    private AutocompleteSupportFragment placeAutocompleteFragment;


    public MapFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        favouriteParkings = new ArrayList<>();

        // Initialize places
        Places.initialize(getContext(), getString(R.string.google_key));

        GetUserFav(new FavouriteFragment.MyCallback() {
            @Override
            public void onCallback(JSONArray value) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map, null);
        progressBar = view.findViewById(R.id.map_progress_bar);
        //searchText = view.findViewById(R.id.map_search_bar_tv);
        gpsImage = view.findViewById(R.id.map_ic_gps);

        // Initialize the AutocompleteSupportFragment.
        placeAutocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.map_place_autocomplete);

        // Specify the types of place data to return.
        placeAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG));

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.d("SELECCIONAR", place.toString());
                //geoLocate(place.getName());
                moveCamera(place.getLatLng(), DEFAULT_ZOOM, "Camera");
                //Toast.makeText(getContext(), "Place name: " + place.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getContext(), "Error: " + status.toString(), Toast.LENGTH_SHORT);
            }
        });


        if (mapFragment == null) {
            progressBar.setVisibility(View.VISIBLE);
            initMap();
        }

        getChildFragmentManager().beginTransaction().replace(R.id.map_fragment, mapFragment).commit();

        return view;
    }

    private void initFragment() {
       /* placeAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(), googleApiClient,
                LAT_LNG_BOUNDS, null);

        searchText.setAdapter(placeAutocompleteAdapter);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    // Execute searching method
                    geoLocate();
                }
                return false;
            }
        });*/

        gpsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

        hideSoftKeyboard();
    }

    private void geoLocate(String searchString) {
        //String searchString = searchText.getText().toString();

        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addressList = new ArrayList<>();

        try {
            addressList = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {

        }

        if (addressList.size() > 0) {
            Address address = addressList.get(0);

            Log.d("LOCATION", "Location get");
            //Toast.makeText(getContext(), address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }
    }

    private void initMap() {
        mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        try {
            if (mLocationPermissionsGranted) {

                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();

                            if(currentLocation != null) { // There is no lastLocation from this or other app
                                // Move the camera and zoom to device location
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM, "My Location");
                            }
                        } else {
                            Toast.makeText(getContext(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {

        }
    }

    public void moveCamera(LatLng latLng, float zoom, String title) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
/*
        if (title != "My Location") {
            MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
            map.addMarker(options);
        }
        */

        hideSoftKeyboard();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;


        initFragment();

        addMarkers();

        getLocationPermission();

        getDeviceLocation();

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);
        }

        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getContext());
        map.setInfoWindowAdapter(customInfoWindow);

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (!((GoogleMapInfoWindowData)marker.getTag()).isFavourite()) {
                    GoogleMapInfoWindowData infoWindow = new GoogleMapInfoWindowData();
                    infoWindow = (GoogleMapInfoWindowData) marker.getTag();
                    Toast.makeText(getContext(), "Has añadido '" + infoWindow.getName() + "' a favoritos", Toast.LENGTH_SHORT).show();
                    infoWindow.setFavourite(true);
                    marker.hideInfoWindow();
                    marker.showInfoWindow();

                    AddToFavourites(marker);
                } else {
                    Toast.makeText(getContext(), "Este parking ya se encuentra en su lista de favoritos", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void addMarkers() {
        Log.d("USER_DEBUG", currentUser.getDisplayName());

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("parkings");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String latitud = postSnapshot.child("coordinates").child("lat").getValue().toString();
                    String longitud = postSnapshot.child("coordinates").child("lon").getValue().toString();
                    String name = postSnapshot.child("name").getValue().toString();
                    name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                    boolean isFavourite = false;

                    for (Parkings p : favouriteParkings) {

                        Log.v("FAVOURITE", "Mis parkings: " + p.getParkingName());
                        Log.v("FAVOURITE", name);

                        if (p.getParkingName().equals(name))
                            isFavourite = true;
                    }

                    String address = postSnapshot.child("address").getValue().toString();
                    String totalPlaces = postSnapshot.child("total").getValue().toString();
                    String freePlaces = postSnapshot.child("free").getValue().toString();


                    LatLng latLng = new LatLng(Float.valueOf(latitud), Float.valueOf(longitud));

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);

                    int freePlacesInt = freePlaces.equals("") ? 0 : Integer.parseInt(freePlaces);
                    int totalPlacesInt = totalPlaces.equals("") ? 0 : Integer.parseInt(totalPlaces);
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
                    infoWindow.setFavourite(isFavourite);

                    map.addMarker(markerOptions).setTag(infoWindow);
                    builder.include(markerOptions.getPosition());
                }

                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                map.animateCamera(cu);

                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
    }

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

    public void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getContext(),
                        COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
                    permissions, REQUEST_LOCATION_PERMISSIONS_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    // We get permissions for picking the user location
                    mLocationPermissionsGranted = true;
                }
                break;
        }
    }

    private void hideSoftKeyboard() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void GetUserFav(final FavouriteFragment.MyCallback myCallback){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userDBRef = db.collection("users");
        currentUser = mAuth.getCurrentUser();
        Query query = userDBRef.whereEqualTo("userID", currentUser.getUid());
        final Task<QuerySnapshot> taskQuery = query.get();

        taskQuery.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            documentId =  document.getId();
                            Object[] data = document.getData().values().toArray();
                            try {
                                favouritesJSON = new JSONArray(data[1].toString());
                                for (int i = 0; i < favouritesJSON.length(); i++) {
                                    Parkings parking = new Parkings();
                                    try {
                                        parking.setParkingName(favouritesJSON.getJSONObject(i).get("name").toString());
                                        parking.setCalle(favouritesJSON.getJSONObject(i).get("address").toString());
                                        favouriteParkings.add(parking);
                                        myCallback.onCallback(null);
                                    } catch (Exception e) { }
                                }
                            }catch (Exception e){
                                favouritesJSON = new JSONArray();
                            }
                        }


                    }
                }
            }
        });
    }

    public void AddToFavourites(Marker marker) {
            try {
                JSONObject park = new JSONObject();
                park.put("name", ((GoogleMapInfoWindowData)marker.getTag()).getName());
                park.put("address", ((GoogleMapInfoWindowData)marker.getTag()).getAddress());
                favouritesJSON.put(park);
                db.collection("users").document(documentId).update("userFavourites", favouritesJSON.toString());

            }catch (Exception e){}
    }
}