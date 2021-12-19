package com.huawei.hms.novelreadingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.huawei.hms.novelreadingapp.databinding.ActivityMainBinding;
import com.huawei.hms.novelreadingapp.ui.home.HomeFragment;
import com.huawei.hms.novelreadingapp.ui.profile.ProfileFragment;
import com.huawei.hms.novelreadingapp.ui.wishlist.WishlistFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        binding = ActivityMainBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );
        Intent intent = getIntent();
        String avt = intent.getStringExtra("avt");
        String email = intent.getStringExtra("email");
        String name = intent.getStringExtra("name");

        Bundle bundle = new Bundle();
        bundle.putString("avt",avt);
        bundle.putString("email",email);
        bundle.putString("name",name);
        ProfileFragment profile = new ProfileFragment();
        profile.setArguments(bundle);

        HomeFragment home = new HomeFragment();
        home.setArguments(bundle);


        WishlistFragment wishlist = new WishlistFragment();
        wishlist.setArguments(bundle);

        BottomNavigationView navView = findViewById( R.id.nav_view );
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_wishlist, R.id.navigation_profile )
                .build();
        NavController navController = Navigation.findNavController( this, R.id.nav_host_fragment_activity_main );
        //NavigationUI.setupActionBarWithNavController( this, navController, appBarConfiguration );
        NavigationUI.setupWithNavController( binding.navView, navController );


    }


}