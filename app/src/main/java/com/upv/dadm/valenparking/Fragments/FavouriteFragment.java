package com.upv.dadm.valenparking.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.firestore.QuerySnapshot;
import com.upv.dadm.valenparking.Parkings;
import com.upv.dadm.valenparking.R;
import com.upv.dadm.valenparking.Adapters.fauvoriteAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavouriteFragment extends Fragment {

    List<Parkings> listParkings = new ArrayList<Parkings>();
    fauvoriteAdapter adapter;
    Integer position = 0;
    RecyclerView recyclerview_parkings;
    View view;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private CollectionReference userDBRef;



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
        recuperarFav(new MyCallback() {
            @Override
            public void onCallback(String value) {
                String[] aparcamientos = value.split(",");
                Log.v("prueba", aparcamientos.toString());
                for (String x : aparcamientos){
                    Parkings parking = new Parkings();
                    parking.setParkingName(x.toString());
                    parking.setFree(100);
                    listParkings.add(parking);
                }



                adapter = new fauvoriteAdapter(getContext(), R.layout.recyclerview_list, listParkings);
                recyclerview_parkings = view.findViewById(R.id.fauvorite_list);
                recyclerview_parkings.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                recyclerview_parkings.setAdapter(adapter);
            }

        });

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

    public void recuperarFav(final MyCallback myCallback){
        String user = getUserUID();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(user);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String valor = dataSnapshot.getValue();
                String res = dataSnapshot.child("fav").child("name").getValue().toString();
                Log.v("prueba", res);
                myCallback.onCallback(res);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("error", "Error!", databaseError.toException());
            }

        });

    }

    public void GetUserFav(){
        db = FirebaseFirestore.getInstance();
        String email = "";
        String password = "";
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            if (currentUser.isEmailVerified()) {
                                Query query = userDBRef.whereEqualTo("userID", currentUser.getUid());
                                final Task<QuerySnapshot> taskQuery = query.get();
                                taskQuery.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().size() > 0) {
                                                String[] name = currentUser.getEmail().split("@");

                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
    }
    public interface MyCallback {
        void onCallback(String value);
    }


}
