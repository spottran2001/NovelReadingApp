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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.agconnect.cloud.database.CloudDBZoneTask;
import com.huawei.agconnect.cloud.database.OnFailureListener;
import com.huawei.agconnect.cloud.database.OnSuccessListener;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.novelreadingapp.databinding.ActivityMainBinding;
import com.huawei.hms.novelreadingapp.model.Novel;
import com.huawei.hms.novelreadingapp.model.ObjectTypeInfoHelper;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private CloudDBZone mCloudDBZone = null;
    private ArrayList<Novel> infoList;
    private String Userid;
    private AccountAuthService mAuthService;
    private AccountAuthParams mAuthParam;
    private static final int REQUEST_CODE_SIGN_IN = 1000;
    private static final String TAG = "Account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        binding = ActivityMainBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );

        BottomNavigationView navView = findViewById( R.id.nav_view );
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications )
                .build();
        NavController navController = Navigation.findNavController( this, R.id.nav_host_fragment_activity_main );
        NavigationUI.setupActionBarWithNavController( this, navController, appBarConfiguration );
        NavigationUI.setupWithNavController( binding.navView, navController );
        Intent intent = getIntent();
        Userid =intent.getStringExtra("UserId");
    }

    private void SignInUsingHwId() {
        // If your app needs to obtain the user's email address, call setEmail() , similarly you  need to acess ID token or authorisation code setIDToken() and setAuthorisationCode()
        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setEmail()
                .createParams();
        mAuthService = AccountAuthManager.getService(this, mAuthParam);
        Intent signInIntent = mAuthService.getSignInIntent();
        // startActivityForResult() method, we can get result from another activity
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
    }

    // startActivityForResult method, requires a result from the second activity (activity to be invoked).
    //In such case, we need to override the onActivityResult method that is invoked automatically when second activity returns result.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // requestcode is checked to make that the activity that invoked passed the result.This can be any code

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Log.i(TAG, "onActivitResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN);
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                // The sign-in is successful, and the authAccount object that contains the HUAWEI ID information is obtained.
                AuthAccount authAccount = authAccountTask.getResult();
                Log.i(TAG, "display name:" + authAccount.getDisplayName());
                Log.i(TAG, "photo uri string:" + authAccount.getAvatarUriString());
                Log.i(TAG, "photo uri:" + authAccount.getAvatarUri());
                Log.i(TAG, "email:" + authAccount.getEmail());
                Log.i(TAG, "openid:" + authAccount.getOpenId());
                Log.i(TAG, "unionid:" + authAccount.getAuthorizationCode());
                Log.i(TAG, "accounttype:" + authAccount.getAccount());
                Log.i(TAG, "agerange:" + authAccount.getUid());

                // when the user login sucesfully , i will get all the details and i am passing all to the next activity via the intent .
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("name", authAccount.getDisplayName());
                intent.putExtra("email", authAccount.getEmail());
                intent.putExtra("photo_uri", authAccount.getAvatarUriString());
                intent.putExtra("UserId", authAccount.getUid());

                startActivity(intent);
            } else {
                // The sign-in fails. Find the failure cause from the status code. For more information, please refer to the "Error Codes" section in the API Reference.
                Log.e(TAG, "sign in failed : " + ((ApiException) authAccountTask.getException()).getStatusCode());
            }
        }
    }








    public static void initAGConnectCloudDB(Context context) {
        AGConnectCloudDB.initialize(context);
    }

    //Writing
    public void insertDataToCloudDB(Novel info) throws AGConnectCloudDBException {
        AGConnectCloudDB mCloudDB = AGConnectCloudDB.getInstance();
        CloudDBZoneConfig mConfig;
        //
        mCloudDB.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo());
        mConfig = new CloudDBZoneConfig( "novelZone", CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC );
        mConfig.setPersistenceEnabled(true);
        try {
            mCloudDBZone = mCloudDB.openCloudDBZone(mConfig, true);
        } catch (AGConnectCloudDBException e) {
            Log.w(TAG, "openCloudDBZone: " + e.getMessage());
        }

        if (mCloudDBZone == null) {
            Log.w(TAG, "CloudDBZone is null, try re-open it");
            return;
        }
        CloudDBZoneTask<Integer> upsertTask = mCloudDBZone.executeUpsert(info);

        upsertTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                Log.w(TAG, "upsert " + integer + " records");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Insertion failed : "+ e.getMessage());
                e.printStackTrace();
            }
        });
    }

    //Query

    private void queryItinerary(CloudDBZoneQuery<Novel> query) {
        if (mCloudDBZone == null) {
            Log.w(TAG, "CloudDBZone is null, try re-open it");
            return;
        }
        CloudDBZoneTask<CloudDBZoneSnapshot<Novel>> queryTask = mCloudDBZone.executeQuery(query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.await();
        if (queryTask.getException() != null) {
            Log.e(TAG ,"Query failed" );
            return;
        }
        processQueryResult(queryTask.getResult());
    }

    private void processQueryResult(CloudDBZoneSnapshot<Novel> snapshot) {
        CloudDBZoneObjectList<Novel> cursor = snapshot.getSnapshotObjects();
        infoList = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                Novel info = cursor.next();
                int i = 0;
                infoList.add(info);
            }
        } catch (AGConnectCloudDBException e) {
            Log.w(TAG, "processQueryResult: " + e.getMessage());
        }

        //ADAPTER
//        adapter = new IteneraryAdapter(infoList, ItineraryActivity.this);
//        itinerary_recyclerview.setAdapter(adapter);
        snapshot.release();

    }
}