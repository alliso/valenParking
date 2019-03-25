package com.upv.dadm.valenparking.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.upv.dadm.valenparking.R;

public class FavouriteFragment extends Fragment {

    View view;

    public FavouriteFragment(){ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favourite, null);
        return view;
    }
}
