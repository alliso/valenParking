package com.upv.dadm.valenparking.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.upv.dadm.valenparking.Adapters.CustomInfoWindowGoogleMap;
import com.upv.dadm.valenparking.Pojo.GoogleMapInfoWindowData;
import com.upv.dadm.valenparking.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap map;
    Marker[] markers;

    private SupportMapFragment mapFragment;

    public MapFragment(){ }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        markers = new Marker[10];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.map_fragment, mapFragment).commit();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng latLng = new LatLng(39.47805910846728, -0.4070261667406942);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Valencia");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        GoogleMapInfoWindowData infoWindow = new GoogleMapInfoWindowData();
        infoWindow.setName("Aparcamiento Biopark");
        infoWindow.setAddress("Av. Pío Baroja");
        infoWindow.setPlaces("25/256");
        infoWindow.setType("Tipo C");

        markers[0] = map.addMarker(markerOptions);
        markers[0].setTag(infoWindow);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));

        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getContext());
        map.setInfoWindowAdapter(customInfoWindow);
    }
}