package com.upv.dadm.valenparking.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.upv.dadm.valenparking.Pojo.GoogleMapInfoWindowData;
import com.upv.dadm.valenparking.R;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowGoogleMap(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.fragment_map_marker_info_window, null);
        TextView name = view.findViewById(R.id.markerInfo_name);
        TextView address = view.findViewById(R.id.markerInfo_address);
        TextView freePlaces = view.findViewById(R.id.markerInfo_places);
        TextView type = view.findViewById(R.id.markerInfo_type);
        ImageView star = view.findViewById(R.id.markerInfo_favourite);

        GoogleMapInfoWindowData infoData = (GoogleMapInfoWindowData) marker.getTag();

        name.setText(infoData.getName());
        address.setText(infoData.getAddress());
        freePlaces.setText(infoData.getPlaces());
        type.setText(infoData.getType());

        if(infoData.isFavourite()) {
            star.setImageResource(android.R.drawable.star_on);
        } else {
            star.setImageResource(android.R.drawable.star_off);
        }

        return view;
    }
}
