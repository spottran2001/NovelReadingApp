package com.huawei.hms.novelreadingapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.huawei.hms.novelreadingapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        binding = ActivityMainBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );
//        Intent intent = getIntent();
//        String avt = intent.getStringExtra("avt");
//        String email = intent.getStringExtra("email");
//        String name = intent.getStringExtra("name");
//        String id = intent.getStringExtra("userId");

//        Bundle bundle = new Bundle();
//        bundle.putString("avt",avt);
//        bundle.putString("email",email);
//        bundle.putString("name",name);
////        bundle.putString( "userId",id );
//        ProfileFragment profile = new ProfileFragment();
//        profile.setArguments(bundle);
//
//        HomeFragment home = new HomeFragment();
//        home.setArguments(bundle);
//
//
//        WishlistFragment wishlist = new WishlistFragment();
//        wishlist.setArguments(bundle);

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