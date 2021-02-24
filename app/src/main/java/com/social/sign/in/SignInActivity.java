package com.social.sign.in;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.social.sign.in.utilities.CreateKeyHashes;
import com.social.sign.in.utilities.LogcatUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = SignInActivity.class.getSimpleName();

    private static final String PERMISSIONS         = "public_profile, email";
    private static final String REQUEST_FIELDS      = "fields";
    private static final String REQUEST_FIELDS_LIST = "id, name, first_name, last_name, link, email, picture.width(120).height(120)";

    private LinearLayout signInButtonLinearLayout;

    private ProgressDialog progressDialog;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Initialize the SDK before executing any other operations */
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_sign_in);
        //createKeyHashes();
        initializeView();
        initializeObject();
        initializeEvent();
    }

    protected void createKeyHashes() {
        /* If you require Hashes Then call */
        CreateKeyHashes.printKeyHash(SignInActivity.this);
    }

    protected void initializeView() {
        signInButtonLinearLayout    = findViewById(R.id.signInButtonLinearLayout);
    }

    protected void initializeObject() {
        callbackManager = CallbackManager.Factory.create();
    }

    protected void initializeEvent() {
        signInButtonLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookSignIn();
            }
        });
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading");
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /*
     ***********************************************************************************************
     ******************************************* Helper methods ************************************
     ***********************************************************************************************
     */
    public void ifAlreadySignIn()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn)
        {
            showProgressDialog();
            handleSignInResult(accessToken);
        }
    }

    public void facebookSignIn() {
        LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList(PERMISSIONS));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>()
                {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {
                        AccessToken accessToken = loginResult.getAccessToken();
                        boolean isLoggedInSuccess = accessToken != null && !accessToken.isExpired();
                        if (isLoggedInSuccess)
                        {
                            handleSignInResult(accessToken);
                        }
                        assert accessToken != null;
                        LogcatUtils.informationMessage(TAG, "Response Access Token : "+accessToken.toString());
                        LogcatUtils.informationMessage(TAG, "User sign in successfully");
                    }

                    @Override
                    public void onCancel()
                    {
                        LogcatUtils.informationMessage(TAG, "User cancel sign in");
                    }

                    @Override
                    public void onError(FacebookException exception)
                    {
                        LogcatUtils.errorMessage(TAG, "Problem for sign in "+exception);
                    }
                });
    }

    private void handleSignInResult(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(currentAccessToken, new GraphRequest.GraphJSONObjectCallback()
        {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                if (response != null)
                {
                    JSONObject json = response.getJSONObject();

                    String name;
                    String firstName;
                    String lastName;
                    String email = null;
                    String facebookId;
                    String photoUrl;
                    String photoUrlSecond;

                    try {
                        if (json != null) {
                            Log.d("response", json.toString());

                            try
                            {
                                email = json.getString("email");
                            }
                            catch (Exception exception)
                            {
                                exception.printStackTrace();
                                LogcatUtils.errorMessage(TAG, "problem" + exception.getMessage());
                                hideProgressDialog();
                                Toast.makeText(getApplicationContext(), "Sorry!!! Your email is not verified on facebook.", Toast.LENGTH_LONG).show();
                            }

                            facebookId      = json.getString("id");
                            name            = json.getString("name");
                            firstName       = json.getString("first_name");
                            lastName        = json.getString("last_name");
                            photoUrl        = "https://graph.facebook.com/"+facebookId+"/picture?type=large";
                            photoUrlSecond  = object.getJSONObject("picture").getJSONObject("data").getString("url");

                            if (email != null)
                            {
                                openDetailActivity(name, firstName, lastName, email, facebookId, photoUrl);
                            }

                            hideProgressDialog();
                        }
                    }
                    catch (JSONException jSONException)
                    {
                        jSONException.printStackTrace();
                        LogcatUtils.errorMessage(TAG, "problem" + jSONException.getMessage());
                        hideProgressDialog();
                    }
                }
            }
        });
        Bundle parameters = new Bundle();
        /**
         * If you used image with parse data then write this code
         * parameters.putString("fields", "id, name, first_name, last_name, link, email, picture.width(200)");
         */
        parameters.putString(REQUEST_FIELDS, REQUEST_FIELDS_LIST);

        /**
         * If you used image custom url then write this code
         * parameters.putString("fields", "id,name,first_name,last_name,link,email,picture");
         */
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void openDetailActivity(String name, String firstName, String lastName, String email, String facebookId, String photoUrl) {
        FacebookUsersDetail facebookUsersDetail = new FacebookUsersDetail();
        facebookUsersDetail.setName(name);
        facebookUsersDetail.setFirstName(firstName);
        facebookUsersDetail.setLastName(lastName);
        facebookUsersDetail.setEmail(email);
        facebookUsersDetail.setFacebookId(facebookId);
        facebookUsersDetail.setPhotoUrl(photoUrl);

        Intent intent = new Intent(SignInActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.FACEBOOK_ACCOUNT_DETAILS, facebookUsersDetail);
        startActivity(intent);
        finish();
    }
    /*
     ***********************************************************************************************
     ********************************* Activity lifecycle methods **********************************
     ***********************************************************************************************
     */
    @Override
    protected void onStart() {
        super.onStart();
        ifAlreadySignIn();
    }
}