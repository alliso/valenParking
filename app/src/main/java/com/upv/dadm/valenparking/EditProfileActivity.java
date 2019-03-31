package com.upv.dadm.valenparking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
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

        EditText edtxtUserName = (EditText) findViewById(R.id.edit_profile_userName_value);
        EditText edtxtUserEmail = (EditText) findViewById(R.id.edit_profile_userEmail_value);
        ImageView imgUser = (ImageView) findViewById(R.id.edit_profile_userPicture);
        edtxtUserName.setText(currentUser.getDisplayName());
        edtxtUserEmail.setText(currentUser.getEmail());
        //Cargar la imagen en el imageView
        Picasso.with(this).load(currentUser.getPhotoUrl().toString())
                .resize(300,300)
                .centerCrop()
                .transform(new CircleTransform())
                .into(imgUser);

        Button btnSaveChanges = (Button) findViewById(R.id.edit_profile_save_changes);
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
