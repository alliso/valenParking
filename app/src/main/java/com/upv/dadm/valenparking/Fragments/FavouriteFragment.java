package com.upv.dadm.valenparking.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upv.dadm.valenparking.Parkings;
import com.upv.dadm.valenparking.R;
import com.upv.dadm.valenparking.Adapters.fauvoriteAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavouriteFragment extends Fragment {

    List<Parkings> listParkings = new ArrayList<Parkings>();
    Parkings parking = new Parkings();
    Parkings parking1 = new Parkings();
    fauvoriteAdapter adapter;
    Integer position = 0;
    RecyclerView recyclerview_parkings;
    View view;
    String favourites;


    public FavouriteFragment(){ }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recuperarFav();
        if(savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
        }
        parking.setFree(150);
        parking.setParkingName("HOLA QUE TAL");
        listParkings.add(parking);


        parking1.setFree(111);
        parking1.setParkingName(favourites);
        listParkings.add(parking1);


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

    public String getUserUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = "";
        if (user != null) {
            uid = user.getUid();

            Log.v("prueba", uid);
        }
        return uid;
    }

    public void recuperarFav(){
        String user = getUserUID();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(user);



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String valor = dataSnapshot.getValue();
                favourites = valor;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("error", "Error!", databaseError.toException());
            }

        });
    }
}
