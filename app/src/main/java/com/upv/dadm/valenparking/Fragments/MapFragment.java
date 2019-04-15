package com.upv.dadm.valenparking.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    // Constants
    private static final int REQUEST_LOCATION_PERMISSIONS_CODE = 3;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    // Widgets
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

    private boolean permissionsDenied = false;

    private View view;
    private ProgressBar progressBar;
    private ImageView searchIcon;

    private AutocompleteSupportFragment placeAutocompleteFragment;

    boolean venirDefavoritos;
    LatLng latLngFavourite;
    Parkings parkingClicked = new Parkings();


    public MapFragment() {
        venirDefavoritos = false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        favouriteParkings = new ArrayList<>();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        // Initialize places
        Places.initialize(getContext(), getString(R.string.google_key));


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map, null);
        progressBar = view.findViewById(R.id.map_progress_bar);
        searchIcon = (ImageView) ((LinearLayout)view.findViewById(R.id.map_place_autocomplete)).getChildAt(0);
        searchIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_black_24dp));
        gpsImage = view.findViewById(R.id.map_ic_gps);

        // Initialize the AutocompleteSupportFragment.
        placeAutocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.map_place_autocomplete);

        // Set the country we want to search in
        placeAutocompleteFragment.setCountry("ES");

        // Specify the types of place data to return.
        placeAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS));

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                moveCamera(place.getLatLng(), DEFAULT_ZOOM);
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getContext(), "Error: " + status.toString(), Toast.LENGTH_SHORT);
            }
        });

        GetUserFav(new FavouriteFragment.MyCallback() {
            @Override
            public void onCallback(JSONArray value) {

                if (mapFragment == null) {
                    progressBar.setVisibility(View.VISIBLE);
                    initMap();
                }

                getChildFragmentManager().beginTransaction().replace(R.id.map_fragment, mapFragment).commit();
            }
        });

        return view;
    }


    private void initFragment() {
        gpsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

        hideSoftKeyboard();
    }

    private void initMap() {
        mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionsGranted) {

                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();

                            if (currentLocation != null) { // There is no lastLocation from this or other app
                                // Move the camera and zoom to device location
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM);
                            } else {
                                if (isLocationEnabled(getContext())) {
                                    Toast.makeText(getContext(), getString(R.string.map_searching_gps), Toast.LENGTH_SHORT).show();
                                } else {
                                    Snackbar snackbar = Snackbar.make(view.findViewById(R.id.map_fragment_coordinator), getString(R.string.map_gps_disabled_sb_text), Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction(getString(R.string.map_gps_disable_sb_button_text), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    });
                                    View snackbarView = snackbar.getView();
                                    TextView snakbarText = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                                    snakbarText.setTextColor(ContextCompat.getColor(getContext(), R.color.snackbarText));
                                    Button snackbarButton = snackbarView.findViewById(android.support.design.R.id.snackbar_action);
                                    snackbarButton.setTextColor(ContextCompat.getColor(getContext(), R.color.snackbarButtonText));
                                    snackbar.show();
                                }
                            }
                        } else {
                            Snackbar snackbar = Snackbar.make(view.findViewById(R.id.map_fragment_coordinator), getString(R.string.map_gps_disabled_sb_text), Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction(getString(R.string.map_gps_disable_sb_button_text), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                            View snackbarView = snackbar.getView();
                            TextView snakbarText = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                            snakbarText.setTextColor(ContextCompat.getColor(getContext(), R.color.snackbarText));
                            Button snackbarButton = snackbarView.findViewById(android.support.design.R.id.snackbar_action);
                            snackbarButton.setTextColor(ContextCompat.getColor(getContext(), R.color.snackbarButtonText));
                            snackbar.show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {

        }
    }

    public void moveCamera(LatLng latLng, float zoom) {

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        hideSoftKeyboard();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        initFragment();

        addMarkers();

        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getContext());
        map.setInfoWindowAdapter(customInfoWindow);

        getLocationPermission();

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);
        }


        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (!((GoogleMapInfoWindowData) marker.getTag()).isFavourite()) {
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

        if (venirDefavoritos) map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngFavourite, 18f));
    }

    public void addMarkers() {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("parkings");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String latitud = postSnapshot.child("coordinates").child("lat").getValue().toString();
                    String longitud = postSnapshot.child("coordinates").child("lon").getValue().toString();
                    String type = postSnapshot.child("type").getValue().toString();
                    String name = postSnapshot.child("name").getValue().toString();
                    name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                    boolean isFavourite = false;
                    boolean isClicked = false;

                    for (Parkings p : favouriteParkings) {

                        if (p.getParkingName().equals(name))
                            isFavourite = true;

                        if(p.getParkingName().equals(name) && p.isClicked()){
                            isClicked = true;
                        }
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
                    } else if (freePlacesInt == 0) {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    } else {
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).alpha(0.2f);
                    }

                    GoogleMapInfoWindowData infoWindow = new GoogleMapInfoWindowData();
                    infoWindow.setName(name);
                    infoWindow.setAddress(address);
                    String places;
                    if (freePlacesInt < 0) {
                        places = "? ";
                    } else {
                        places = freePlaces + " ";
                    }

                    places += getString(R.string.map_free_places) + " " + totalPlaces;
                    infoWindow.setPlaces(places);
                    infoWindow.setType(type.equals("0") ? getString(R.string.map_public_parking) : getString(R.string.map_private_parking));
                    infoWindow.setFavourite(isFavourite);

                    Marker marker = map.addMarker(markerOptions);
                    marker.setTag(infoWindow);

                    if(isClicked) {
                        marker.showInfoWindow();
                    }
                    builder.include(markerOptions.getPosition());
                }


                if(!venirDefavoritos) {
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                    map.animateCamera(cu);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

                    if (map != null) {
                        map.setMyLocationEnabled(true);
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
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
                                        if(parking.getParkingName().equals(parkingClicked.getParkingName())){
                                            parking.setClicked(true);

                                        }
                                        favouriteParkings.add(parking);

                                    } catch (Exception e) { }
                                }
                                myCallback.onCallback(null);
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
                park.put("lat", marker.getPosition().latitude);
                park.put("lon", marker.getPosition().longitude);
                favouritesJSON.put(park);
                db.collection("users").document(documentId).update("userFavourites", favouritesJSON.toString());

            }catch (Exception e){}
    }

    public void setClickedParking(Parkings p){
        parkingClicked = p;
    }

    public void moveCameraFromFavourites(LatLng latLng, float zoom, String title) {
        venirDefavoritos = true;
        latLngFavourite = latLng;
    }

    public boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }

        return true;
    }
}