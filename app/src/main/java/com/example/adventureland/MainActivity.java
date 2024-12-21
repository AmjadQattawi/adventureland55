package com.example.adventureland;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.adventureland.fragments.CardFragment;
import com.example.adventureland.fragments.FavoriteFragment;
import com.example.adventureland.fragments.HomeFragment;
import com.example.adventureland.fragments.MoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_nav);


        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.homeFragment) {
                    pushFragment(new HomeFragment(),"home");
                } else if (item.getItemId() == R.id.cartFragment) {
                    pushFragment(new CardFragment(),"card");
                } else if (item.getItemId() == R.id.favouriteFragment) {
                    pushFragment(new FavoriteFragment(),"favorite");
                } else if (item.getItemId() == R.id.moreFragment) {
                    pushFragment(new MoreFragment(),"more");
                }
                return true;
            }
        });

        bottomNavigation.setSelectedItemId(R.id.homeFragment); // put it as default screen
    }


    private void pushFragment(Fragment fragment, String name) {
        // Implement the logic to push the fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutContainer, fragment)
                .addToBackStack(name)
                .commit();
    }

}



