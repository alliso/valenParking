package com.upv.dadm.valenparking;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.upv.dadm.valenparking.Utils.CircleTransform;


public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private CollectionReference userDBRef;
    private FirebaseStorage dbPicture;
    private StorageReference pictureDBref;
    private StorageReference riversRef;
    private ImageView imgUser;
    private Uri path;
    private String newName;
    private  String newEmail;
    private SharedPreferences prefs;
    private boolean imgUserChange = false;
    private final int REQUEST_GALLERY_PERMISSION = 20;
    private final int REQUEST_GALLERY_PICTURE = 25;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        userDBRef = db.collection("users");

        dbPicture = FirebaseStorage.getInstance();
        pictureDBref = dbPicture.getReference();

        final EditText edtxtUserName = (EditText) findViewById(R.id.edit_profile_userName_value);
        final EditText edtxtUserEmail = (EditText) findViewById(R.id.edit_profile_userEmail_value);
        imgUser = (ImageView) findViewById(R.id.edit_profile_userPicture);
        edtxtUserName.setText(currentUser.getDisplayName());
        edtxtUserEmail.setText(currentUser.getEmail());

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean isImgUserChange = prefs.getBoolean("imgUserChange", false);
        if(isImgUserChange){
            riversRef = pictureDBref.child(currentUser.getUid()).child("profilePicture");
            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(EditProfileActivity.this).load(uri.toString())
                            .resize(300,300)
                            .centerCrop()
                            .transform(new CircleTransform())
                            .into(imgUser);
                }
            });
        }else{
            Picasso.with(this).load(currentUser.getPhotoUrl().toString())
                    .resize(300,300)
                    .centerCrop()
                    .transform(new CircleTransform())
                    .into(imgUser);
        }

        final Button btnSaveChanges = (Button) findViewById(R.id.edit_profile_save_changes);
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtxtUserName.getText().toString().equals(currentUser.getDisplayName())
                        && edtxtUserEmail.getText().toString().equals(currentUser.getEmail())
                        && !imgUserChange) {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.edit_profile_save_incorrect_string), Toast.LENGTH_SHORT).show();
                } else {
                    Query query = userDBRef.whereEqualTo("userID", currentUser.getUid());
                    final Task<QuerySnapshot> taskQuery = query.get();
                    taskQuery.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() > 0) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (!edtxtUserName.getText().toString().equals(currentUser.getDisplayName())) {
                                            newName = edtxtUserName.getText().toString();
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(newName)
                                                    .build();
                                            currentUser.updateProfile(profileUpdates);
                                            userDBRef.document(document.getId()).update("userName", newName);
                                        }
                                        if (!edtxtUserEmail.getText().toString().equals(currentUser.getEmail())) {
                                            newEmail = edtxtUserEmail.getText().toString();
                                            currentUser.updateEmail(newEmail);
                                            userDBRef.document(document.getId()).update("userEmail", newEmail);
                                        }
                                        if(imgUserChange) {
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setPhotoUri(path)
                                                    .build();
                                            currentUser.updateProfile(profileUpdates);

                                            userDBRef.document(document.getId()).update("userPicture", path.toString());

                                        }
                                    }
                                }
                            }
                        }
                    });
                    Toast.makeText(EditProfileActivity.this, getString(R.string.edit_profile_save_correct_string), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("Name", edtxtUserName.getText().toString());
                    intent.putExtra("Image", path.toString());
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    setResult(0, intent);
                    finish();
                }
            }
        });

        ImageButton imgBtnEditImage = (ImageButton) findViewById(R.id.edit_profile_image);
        imgBtnEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                builder.setTitle(getString(R.string.edit_profile_image_title_string))
                        .setMessage(getString(R.string.edit_profile_image_message_string))

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(ActivityCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                                    //Si no ha sido aceptado previamente para versiones desde la 6.0 [API 23] en adelante
                                    ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY_PERMISSION);
                                }else{
                                    //Si ha sido aceptado previamente para versiones inferiores a la 6.0 (se acepta el permiso al instalarse la app)
                                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, REQUEST_GALLERY_PICTURE);
                                }
                            }
                        })

                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.ic_dialog_photo)
                        .create()
                        .show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_GALLERY_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_GALLERY_PICTURE);
            }else{
                Toast.makeText(EditProfileActivity.this, getString(R.string.edit_profile_gallery_permission_denied_string), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_GALLERY_PICTURE){
            if(resultCode == Activity.RESULT_OK){
                //Si se ha seleccionado una foto
                imgUserChange = true;
                path = data.getData();
                riversRef = pictureDBref.child(currentUser.getUid()).child("profilePicture");
                riversRef.putFile(path);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("imgUserChange", imgUserChange);
                editor.apply();

                Picasso.with(EditProfileActivity.this).load(path.toString())
                        .resize(300,300)
                        .centerCrop()
                        .transform(new CircleTransform())
                        .into(imgUser);
            }else{
                Toast.makeText(EditProfileActivity.this, getString(R.string.edit_profile_gallery_not_take_photo_string), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(1);
        super.onBackPressed();
    }

}

