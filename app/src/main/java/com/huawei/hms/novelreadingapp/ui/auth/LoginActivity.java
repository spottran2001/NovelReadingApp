package com.huawei.hms.novelreadingapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.novelreadingapp.MainActivity;
import com.huawei.hms.novelreadingapp.R;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.hwid.ui.HuaweiIdAuthButton;

public class LoginActivity extends AppCompatActivity {
    public static AccountAuthService getmAuthService() {
        return mAuthService;
    }

    public void setmAuthService(AccountAuthService mAuthService) {
        this.mAuthService = mAuthService;
    }

    private static AccountAuthService mAuthService;
    private AccountAuthParams mAuthParam;
    private static final int REQUEST_CODE_SIGN_IN = 1000;
    private static final String TAG = "Account";
    private static String userId;

    public static AuthAccount getAccount() {
        return account;
    }

    public static void setAccount(AuthAccount account) {
        LoginActivity.account = account;
    }

    public static AuthAccount account;
    public static String getUserId() {
        return userId;
    }

    public void  setUserId(String userId) {
        LoginActivity.userId = userId;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        HuaweiIdAuthButton btn = findViewById(R.id.HuaweiIdAuthButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                intent.putExtra( "email","AAC");
//                startActivity(intent);

                SignInUsingHwId();
            }
        });
    }
//    private void SignInUsingHwId() {
//        // If your app needs to obtain the user's email address, call setEmail() , similarly you  need to acess ID token or authorisation code setIDToken() and setAuthorisationCode()
//        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
//                .setEmail()
//                .createParams();
//        mAuthService = AccountAuthManager.getService(this, mAuthParam);
////        Intent signInIntent = mAuthService.getSignInIntent();
//        // startActivityForResult() method, we can get result from another activity
//        findViewById(R.id.HuaweiIdAuthButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                silentSignInByHwId();
//            }
//        });
//
//    }
//    private void silentSignInByHwId() {
//        // 1. Use AccountAuthParams to specify the user information to be obtained, including the user ID (OpenID and UnionID), email address, and profile (nickname and picture).
//        // 2. By default, DEFAULT_AUTH_REQUEST_PARAM specifies two items to be obtained, that is, the user ID and profile.
//        // 3. If your app needs to obtain the user's email address, call setEmail().
//        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
//                .setEmail()
//                .createParams();
//        // Use AccountAuthParams to build AccountAuthService.
//        mAuthService = AccountAuthManager.getService(this, mAuthParam);
//        // Use silent sign-in to sign in with a HUAWEI ID.
//        Task<AuthAccount> task = mAuthService.silentSignIn();
//        task.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
//            @Override
//            public void onSuccess(AuthAccount authAccount) {
//                // The silent sign-in is successful. Process the returned account object AuthAccount to obtain the HUAWEI ID information.
//                //dealWithResultOfSignIn(authAccount);
//
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                intent.putExtra("avt",authAccount.getAvatarUriString());
//                intent.putExtra("email",authAccount.getEmail());
//                intent.putExtra("name",authAccount.getDisplayName());
//                intent.putExtra( "userId",authAccount.getUid());
//                setUserId(authAccount.getUid());
//                startActivity(intent);
//            }
//        });
//        task.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(Exception e) {
//                // The silent sign-in fails. Use the getSignInIntent() method to show the authorization or sign-in screen.
//                if (e instanceof ApiException) {
//                    ApiException apiException = (ApiException) e;
//                    Intent signInIntent = mAuthService.getSignInIntent();
//                    startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
//                }
//            }
//        });
//    }
private void SignInUsingHwId() {
    // If your app needs to obtain the user's email address, call setEmail() , similarly you  need to acess ID token or authorisation code setIDToken() and setAuthorisationCode()
    mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setEmail()
            .createParams();
    mAuthService = AccountAuthManager.getService(this, mAuthParam);
    setmAuthService(mAuthService);
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
                setAccount(authAccount);

// when the user login sucesfully , i will get all the details and i am passing all to the next activity via the intent .
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                intent.putExtra("email", authAccount.getEmail());
//                intent.putExtra("auth", authAccount.getAuthorizationCode());
//
//                startActivity(intent);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra( "email",authAccount.getEmail());
                intent.putExtra("name", authAccount.getDisplayName());
                intent.putExtra("id",authAccount.getOpenId());
                intent.putExtra("avt", authAccount.getAvatarUriString());
                startActivity(intent);
            } else {
                // The sign-in fails. Find the failure cause from the status code. For more information, please refer to the "Error Codes" section in the API Reference.
                Log.e(TAG, "sign in failed : " + ((ApiException) authAccountTask.getException()).getStatusCode());
            }
        }
    }

    public static void signOut() {
        Task<Void> signOutTask = getmAuthService().signOut();
        signOutTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "signOut Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, "signOut fail");
            }
        });
    }
    public static void cancelAuthorization() {
        Task<Void> task = getmAuthService().cancelAuthorization();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "cancelAuthorization success");
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, "cancelAuthorization failure:" + e.getClass().getSimpleName());
            }
        });
    }
}