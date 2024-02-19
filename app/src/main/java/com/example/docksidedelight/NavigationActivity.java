package com.example.docksidedelight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationActivity extends AppCompatActivity {

    private BottomNavigationView bottom_nav_menu;
    private FrameLayout frame_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        bottom_nav_menu = findViewById(R.id.bottom_nav_menu);
        frame_layout = findViewById(R.id.frame_layout);

        bottom_nav_menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Finds the item ID of the icon selected in nav bar
                int itemID = item.getItemId();

                // Determines which icon is selected and calls loadFragment method with corresponding parameters
                if (itemID == R.id.profile) {
                    loadFragment(new DashboardFragment(), false);
                } else if (itemID == R.id.favourites) {
                    loadFragment(new FavouritesFragment(), false);
                } else if (itemID == R.id.book) {
                    loadFragment(new BookingFragment(), false);
                } else if (itemID == R.id.edit) {
                    loadFragment(new SelectEditBookingFragment(), false);
                } else if (itemID == R.id.settings) {
                    loadFragment(new SettingsFragment(), false);
                }
                return true;
            }
        });
        loadFragment(new DashboardFragment(), false);
    }

    // Method for loading or replacing fragment
    private void loadFragment(Fragment fragment, boolean hasFragment){
        FragmentManager fragman = getSupportFragmentManager();
        FragmentTransaction fragtrans = fragman.beginTransaction();

        // Determines if fragment should be added or replaced
        if(hasFragment){
            fragtrans.add(R.id.frame_layout, fragment);
        }
        else {
            fragtrans.replace(R.id.frame_layout, fragment);
        }
        fragtrans.commit();
    }
}