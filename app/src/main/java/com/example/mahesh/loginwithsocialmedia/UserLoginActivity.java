package com.example.mahesh.loginwithsocialmedia;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;


public class UserLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = UserLoginActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private TextView tvDetails;
    private ImageView ivProfileImage;
    private SignInButton btnSignIn;
    private Button btnSignOut, btnRevokeAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Set the dimensions of the sign-in button.
        btnSignIn = (SignInButton) findViewById(R.id.btnSignIn);
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setOnClickListener(this);

        btnSignOut = (Button) findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(this);

        btnRevokeAccess = (Button) findViewById(R.id.btnRevokeAccess);
        btnRevokeAccess.setOnClickListener(this);

        tvDetails = (TextView) findViewById(R.id.tvDetails);
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection Failed : " + connectionResult);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignIn:
                signIn();
                break;
            case R.id.btnSignOut:
                signOut();
                break;
            case R.id.btnRevokeAccess:
                revokeAccess();
                break;
            // ...
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        updateUI(false);
                    }
                });
    }

    private void revokeAccess() {

        /* Disconnect accounts : It is highly recommended that you provide users that signed in with Google the ability
        to disconnect their Google account from your app. If the user deletes their account,
        you must delete the information that your app obtained from the Google APIs.

        Note. clear user records from all storage places, i haven't stored any user records so i am just updating the UI.
        */

        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }


    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);
            ivProfileImage.setVisibility(View.VISIBLE);
            tvDetails.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.VISIBLE);
            btnRevokeAccess.setVisibility(View.VISIBLE);
        } else {
            ivProfileImage.setVisibility(View.GONE);
            tvDetails.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            btnRevokeAccess.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "handleSignInResult:" + result.isSuccess());
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                tvDetails.setText("Profile Name :" + acct.getDisplayName() +
                        "\nEmail : " + acct.getEmail() +
                        "\nFamily Name :" + acct.getFamilyName() +
                        "\n Given Name :" + acct.getGivenName() +
                        "\n ID :" + acct.getId());

                Picasso.with(UserLoginActivity.this)
                        .load(acct.getPhotoUrl())
                        .into(ivProfileImage);
                updateUI(true);
            } else {
                // Signed out, show unauthenticated UI.
                tvDetails.setText("error occured..!");
                updateUI(false);
            }
        }
    }
}

/*
public class UserLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_LOGIN = 10;
    private SignInButton btnLogin;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestProfile().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .addApi(Plus.API).build();

        btnLogin = (SignInButton) findViewById(R.id.signin_button);
        btnLogin.setSize(SignInButton.SIZE_ICON_ONLY);
        btnLogin.setScopes(googleSignInOptions.getScopeArray());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signin, REQUEST_CODE_LOGIN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOGIN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = googleSignInResult.getSignInAccount();
            Person profile = Plus.PeopleApi.getCurrentPerson(googleApiClient);

            try {
                Intent sendData = new Intent(UserLoginActivity.this, Detail.class);
                String name, email, gender, dpUrl = "";
                name = account.getDisplayName();
                email = account.getEmail();
                if (profile.getGender() == 0) {
                    gender = "Male";
                    sendData.putExtra("p_gender", gender);
                }
                if (profile.getGender() == 1) {
                    gender = "female";
                    sendData.putExtra("p_gender", gender);
                }

                dpUrl = account.getPhotoUrl().toString();
                sendData.putExtra("p_name", name);
                sendData.putExtra("p_email", email);
                sendData.putExtra("p_url", dpUrl);

                startActivity(sendData);

            } catch (Exception e) {
                Toast.makeText(UserLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(UserLoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
*/
