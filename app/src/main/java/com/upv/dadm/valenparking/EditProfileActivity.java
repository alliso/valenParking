package com.upv.dadm.valenparking;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.upv.dadm.valenparking.Fragments.AccountFragment;
import com.upv.dadm.valenparking.Utils.CircleTransform;


import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private CollectionReference userDBRef;
    //private ArrayList<User> userArrayList;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        userDBRef = db.collection("users");

        final EditText edtxtUserName = (EditText) findViewById(R.id.edit_profile_userName_value);
        final EditText edtxtUserEmail = (EditText) findViewById(R.id.edit_profile_userEmail_value);
        final ImageView imgUser = (ImageView) findViewById(R.id.edit_profile_userPicture);
        edtxtUserName.setText(currentUser.getDisplayName());
        edtxtUserEmail.setText(currentUser.getEmail());
        //Cargar la imagen en el imageView
        Picasso.with(this).load(currentUser.getPhotoUrl().toString())
                .resize(300,300)
                .centerCrop()
                .transform(new CircleTransform())
                .into(imgUser);

        final Button btnSaveChanges = (Button) findViewById(R.id.edit_profile_save_changes);
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query = userDBRef.whereEqualTo("userID", currentUser.getUid());
                final Task<QuerySnapshot> taskQuery = query.get();
                taskQuery.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.v("documeeeeeeent", document.getData().values().toString());
                                }

                            }
                        }
                    }
                });

                if(!edtxtUserName.getText().toString().equals(currentUser.getDisplayName())) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(edtxtUserName.getText().toString())
                            .build();

                    currentUser.updateProfile(profileUpdates);
                }

                if(!edtxtUserEmail.getText().toString().equals(currentUser.getEmail())){
                    String m1 = edtxtUserEmail.getText().toString();
                    currentUser.updateEmail(edtxtUserEmail.getText().toString());
                    String m = currentUser.getEmail();
                }
                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
