package com.upv.dadm.valenparking;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //esta línea quizás no haga falta
        //getSupportActionBar().setTitle(getString(R.string.app_name));

        ((BottomNavigationView) findViewById(R.id.main_bottomNavigationView)).setOnNavigationItemSelectedListener(this);

        //que se muestre por defecto MapFragment al entrar en la aplicación
        /*if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment, new MapFragment())
                    .commit();
        }*/
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        String tag = null;
        switch (menuItem.getItemId()) {
            /*case R.id.:
                tag = "MapFragment";
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) { fragment = new MapFragment(); }
                break;*/
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, fragment, tag)
                .commit();
        return true;
    }
}
