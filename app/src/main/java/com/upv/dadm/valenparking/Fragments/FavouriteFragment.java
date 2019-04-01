package com.upv.dadm.valenparking.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.upv.dadm.valenparking.Parkings;
import com.upv.dadm.valenparking.R;
import com.upv.dadm.valenparking.fauvoriteAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavouriteFragment extends Fragment {

    List<Parkings> listParkings = new ArrayList<Parkings>();
    Parkings parking = new Parkings();
    fauvoriteAdapter adapter;
    Integer position = 0;
    RecyclerView recyclerview_parkings;
    View view;

    public FavouriteFragment(){ }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
        }
        parking.setFree(150);
        parking.setParkingName("HOLA QUE TAL");
        listParkings.add(parking);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favourite, null);
        adapter = new fauvoriteAdapter(getContext(), R.layout.recyclerview_list, listParkings);
        recyclerview_parkings = view.findViewById(R.id.fauvorite_list);
        recyclerview_parkings.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerview_parkings.setAdapter(adapter);
        return view;
    }
}
