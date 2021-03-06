package com.example.restaurantrecognition;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.restaurantrecognition.firestore.Prediction;
import com.example.restaurantrecognition.ui.FragmentInteractionListener;
import com.example.restaurantrecognition.ui.exit.ExitFragment;
import com.example.restaurantrecognition.ui.help.HelpFragment;
import com.example.restaurantrecognition.ui.recentmatches.RecentMatchesFragment;
import com.example.restaurantrecognition.ui.restaurantresult.RestaurantResultFragment;
import com.example.restaurantrecognition.ui.search.SearchFragment;
import com.example.restaurantrecognition.ui.searchresult.SearchResultFragment;
import com.example.restaurantrecognition.ui.settings.SettingsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;


import android.util.Log;
import android.view.Menu;

import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.restaurantrecognition.ui.exit.ExitFragment;
import com.example.restaurantrecognition.ui.help.HelpFragment;
import com.example.restaurantrecognition.ui.recentmatches.RecentMatchItem;
import com.example.restaurantrecognition.ui.recentmatches.RecentMatchesFragment;
import com.example.restaurantrecognition.ui.search.SearchFragment;
import com.example.restaurantrecognition.ui.settings.SettingsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;


public class SideMenuActivity extends AppCompatActivity
        implements FragmentInteractionListener , NavigationView.OnNavigationItemSelectedListener, RecentMatchesFragment.OnListFragmentInteractionListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Delete old sharedpreferences
//        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(RecentMatchItem.PREFERENCES_STORE_NAME, Context.MODE_PRIVATE);
//        sharedPreferences.edit().clear().commit();

        setContentView(R.layout.activity_side_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_search, R.id.nav_recent_matches, R.id.nav_settings,
                R.id.nav_help, R.id.nav_exit)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Condition to exit or not the application by exit button
        if (getIntent().getBooleanExtra("EXIT", false))
        {
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_search:
                fragmentClass = SearchFragment.class;
                break;
            case R.id.nav_recent_matches:
                fragmentClass = RecentMatchesFragment.class;
                break;
            case R.id.nav_settings:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.nav_help:
                fragmentClass = HelpFragment.class;
                break;
            case R.id.nav_exit:
                fragmentClass = ExitFragment.class;
                break;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContent, fragment).commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.side_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void changeFragment(int id) {
        Fragment fragment = null;
        Class fragmentClass = null;
        if (id == 1) {
            fragmentClass = SearchResultFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentContent, fragment).addToBackStack("Fragment A").commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return;
    }

    @Override
    public void onListFragmentInteraction(RecentMatchItem item) {
        // TODO: redirect to relevant restaurant info
        Log.d(getCallingPackage(), "selected: "+item.id);
//        Toast.makeText(getApplicationContext(), "clicked on restaurant: " + item.restaurantName, Toast.LENGTH_LONG).show();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("Name", item.restaurantName);
        bundle.putDouble("Latitude", item.latitude);
        bundle.putDouble("Longitude", item.longitude);
        bundle.putString("Address", item.address);
        bundle.putInt("ZomatoId", item.zomatoId);
        Log.i("Recent-match", item.restaurantName);

        RestaurantResultFragment fragment = new RestaurantResultFragment();

        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragmentContent, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
