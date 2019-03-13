package com.upv.dadm.valenparking;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.upv.dadm.valenparking.Fragments.AccountFragment;
import com.upv.dadm.valenparking.Fragments.FavouriteFragment;
import com.upv.dadm.valenparking.Fragments.MapFragment;
import com.upv.dadm.valenparking.Fragments.TimerFragment;
import com.upv.dadm.valenparking.Fragments.VehicleFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //esta línea quizás no haga falta
        //getSupportActionBar().setTitle(getString(R.string.app_name));

        ((BottomNavigationView) findViewById(R.id.main_bottomNavigationView)).setOnNavigationItemSelectedListener(this);

        //que se muestre por defecto MapFragment al entrar en la aplicación
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, new MapFragment())
                    .commit();
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        String tag = null;
        switch (menuItem.getItemId()) {
            case R.id.main_menu_timer:
                tag = "TimerFragment";
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) { fragment = new TimerFragment(); }
                break;

            case R.id.main_menu_favourites:
                tag = "FavouriteFragment";
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) { fragment = new FavouriteFragment(); }
                break;

            case R.id.main_menu_map:
                tag = "MapFragment";
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) { fragment = new MapFragment(); }
                break;

            case R.id.main_menu_vehicle:
                tag = "VehicleFragment";
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) { fragment = new VehicleFragment(); }
                break;

            case R.id.main_menu_account:
                tag = "AccountFragment";
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) { fragment = new AccountFragment(); }
                break;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, fragment, tag)
                .commit();

        return true;
    }
}
