package com.upv.dadm.valenparking.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.upv.dadm.valenparking.EditProfileActivity;
import com.upv.dadm.valenparking.LoginActivity;
import com.upv.dadm.valenparking.R;
import com.upv.dadm.valenparking.Utils.CircleTransform;

public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private View view;
    private ImageView imgUser;

    public AccountFragment(){ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, null);

        mAuth = FirebaseAuth.getInstance();

        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        TextView edtxtUserName = (TextView) view.findViewById(R.id.account_useName);
        imgUser = (ImageView) view.findViewById(R.id.account_userPicture);
        edtxtUserName.setText(currentUser.getDisplayName());

        //Cargar la imagen en el imageView
        Uri photouri = currentUser.getPhotoUrl();
        Picasso.with(getContext()).load(currentUser.getPhotoUrl().toString())
                .resize(300,300)
                .centerCrop()
                .transform(new CircleTransform())
                .into(imgUser);


        Button btnEditProfile = view.findViewById(R.id.account_edit_profile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button btnLogOut = view.findViewById(R.id.account_log_out);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == 0){
            TextView edtxtUserName = (TextView) view.findViewById(R.id.account_useName);
            imgUser = (ImageView) view.findViewById(R.id.account_userPicture);
            String Name = data.getStringExtra("Name");
            String Image = data.getStringExtra("Image");
            if(Name != null) edtxtUserName.setText(Name);
            if(Image != null) {
                Picasso.with(getContext()).load(Image)
                        .resize(300,300)
                        .centerCrop()
                        .transform(new CircleTransform())
                        .into(imgUser);
            }
        }
    }
}
