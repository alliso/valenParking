package com.upv.dadm.valenparking.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
import com.upv.dadm.valenparking.MainActivity;
import com.upv.dadm.valenparking.Parkings;
import com.upv.dadm.valenparking.R;
import com.upv.dadm.valenparking.Adapters.fauvoriteAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;
import static com.android.volley.VolleyLog.TAG;

public class FavouriteFragment extends Fragment {

    List<Parkings> listParkings = new ArrayList<Parkings>();
    List<Parkings> listALLParkings = new ArrayList<Parkings>();
    fauvoriteAdapter adapter;
    Integer position = 0;
    RecyclerView recyclerview_parkings;
    View view;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private CollectionReference userDBRef;
    JSONArray favouritesJSON;
    Menu fav_menu;
    Boolean hideIcon = true;
    fauvoriteAdapter.OnFavouriteLongClickListener listener2;
    fauvoriteAdapter.OnFavouriteShortClickListener listener;
    private ProgressBar progressBar;
    private TextView listavaciaMsg;
    String documentId = "";
    FloatingActionButton fab;

    public FavouriteFragment(){ }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_favourite, null);
        fab = view.findViewById(R.id.delete_favs);
        fab.hide();
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_favourites);
        listavaciaMsg = (TextView) view.findViewById(R.id.empty_fav_msg);
        progressBar.setVisibility(view.VISIBLE);
        listavaciaMsg.setText(R.string.empty_favourite_msg);
        listavaciaMsg.setVisibility(view.INVISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialogFragment DialogFragment = myDialogFragment.getInstance(getString(R.string.favourite_dialog_delete_all_msg));
                Bundle args = new Bundle();
                args.putString("click", "all");
                DialogFragment.setArguments(args);
                DialogFragment.setTargetFragment(getFragmentManager().findFragmentByTag("FavouriteFragment"), 0);
                DialogFragment.show(getFragmentManager(), "MyDialogFragment");

            }
        });
        //setHasOptionsMenu(true);


        listener2 = new fauvoriteAdapter.OnFavouriteLongClickListener() {
            @Override
            public void onFavouriteLongClick() {
                //fav_menu.findItem(R.id.delete_favs).setVisible(hideIcon);
                fab.show();

            }
        };

        listener = new fauvoriteAdapter.OnFavouriteShortClickListener() {
            @Override
            public void onFavouriteShortClick() {
                Boolean aux = false;
                for(Parkings p : listParkings){
                    if(p.isSelected()){
                        aux = true;
                    }
                }
                if(!aux){
                    //fav_menu.findItem(R.id.delete_favs).setVisible(!hideIcon);
                    fab.hide();
                }
            }
        };


        getAllParkings(new MyCallbackAll() {
            @Override
            public void onCallback(List<Parkings> lista) {
                GetUserFav(new MyCallback() {
                    @Override
                    public void onCallback(JSONArray value) {

                        try {
                            for (int i = 0; i < value.length(); i++) {

                                Parkings parking = new Parkings();
                                parking.setParkingName(value.getJSONObject(i).get("name").toString());
                                parking.setCalle(value.getJSONObject(i).get("address").toString());
                                parking.setLat(Float.parseFloat(value.getJSONObject(i).get("lat").toString()));
                                parking.setLon(Float.parseFloat(value.getJSONObject(i).get("lon").toString()));

                                for(Parkings p : listALLParkings){
                                    if(parking.getParkingName().equals(p.getParkingName())){
                                        parking.setFreePlaces(p.getFreePlaces());
                                    }
                                }
                                listParkings.add(parking);
                            }

                            adapter = new fauvoriteAdapter(getContext(), R.layout.recyclerview_list, listParkings, listener2, listener);
                            recyclerview_parkings = view.findViewById(R.id.fauvorite_list);
                            recyclerview_parkings.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                            DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), HORIZONTAL);
                            recyclerview_parkings.addItemDecoration(itemDecor);
                            recyclerview_parkings.setAdapter(adapter);
                            progressBar.setVisibility(view.INVISIBLE);
                        }catch(Exception e){}
                    }
                });
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 1){
            int cont = 0;
            List<Parkings> listParkingsAux = new ArrayList<Parkings>();
            for(Parkings p : listParkings){
                if(!p.isSelected()){
                    listParkingsAux.add(p);
                }else{
                    cont++;
                }
            }
            if(cont == listParkings.size()) {
                //fav_menu.findItem(R.id.delete_favs).setVisible(!hideIcon);

                listavaciaMsg.setVisibility(view.VISIBLE);
            }
            listParkings = listParkingsAux;
            adapter = new fauvoriteAdapter(getContext(), R.layout.recyclerview_list, listParkings, listener2, listener);
            recyclerview_parkings.setAdapter(adapter);
            fab.hide();
            updateUserFav();

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    public void GetUserFav(final MyCallback myCallback){
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
                                if(favouritesJSON.length()==0 || data[1] == null) listavaciaMsg.setVisibility(view.VISIBLE);
                                myCallback.onCallback(favouritesJSON);
                            }catch (Exception e){
                                listavaciaMsg.setVisibility(view.VISIBLE);
                                progressBar.setVisibility(view.INVISIBLE);
                            }
                        }


                    }
                }
            }
        });

    }

    public void getAllParkings(final MyCallbackAll myCallbackAll){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("parkings");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String name = postSnapshot.child("name").getValue().toString();
                    name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                    String free = postSnapshot.child("free").getValue().toString();

                    Parkings parking = new Parkings();
                    parking.setParkingName(name);
                    parking.setFreePlaces(free);

                    listALLParkings.add(parking);

                }
                myCallbackAll.onCallback(listALLParkings);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void updateUserFav(){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        JSONArray newList = new JSONArray();
        try {
            for (Parkings p : listParkings) {
                JSONObject park = new JSONObject();
                park.put("name", p.getParkingName());
                park.put("address", p.getCalle());
                park.put("lat", p.getLat());
                park.put("lon", p.getLon());

                newList.put(park);
            }
            db.collection("users").document(documentId).update("userFavourites", newList.toString());

        }catch (Exception e){}
    }

    public interface MyCallback {
        void onCallback(JSONArray value);
    }

    public interface MyCallbackAll {
        void onCallback(List<Parkings> lista);
    }

}
