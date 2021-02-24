package com.social.sign.in;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.material.button.MaterialButton;
import com.social.sign.in.utilities.ActivityUtils;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    public static final String FACEBOOK_ACCOUNT_DETAILS = "facebook_account_details";

    private CircleImageView profileCircleImageView;
    private TextView nameTextView;
    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView emailTextView;
    private TextView facebookIdTextView;

    private MaterialButton signOutMaterialButton;

    private FacebookUsersDetail facebookUsersDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initializeView();
        initializeObject();
        initializeEvent();
        sendAccountDetails();
    }

    protected void initializeView() {
        profileCircleImageView      = findViewById(R.id.pictureCircleImageView);
        nameTextView                = findViewById(R.id.nameTextView);
        firstNameTextView           = findViewById(R.id.firstNameTextView);
        lastNameTextView            = findViewById(R.id.lastNameTextView);
        emailTextView               = findViewById(R.id.emailTextView);
        facebookIdTextView          = findViewById(R.id.facebookIdTextView);
        signOutMaterialButton       = findViewById(R.id.signOutMaterialButton);
    }

    protected void initializeObject() {
        facebookUsersDetail = getIntent().getParcelableExtra(FACEBOOK_ACCOUNT_DETAILS);
    }

    protected void initializeEvent() {
        signOutMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void sendAccountDetails() {
        if (facebookUsersDetail != null)
        {
            String photoUrl = facebookUsersDetail.getPhotoUrl();
            System.out.println("Photo Url = "+photoUrl);

            Glide.with(getApplicationContext())
                    .load(photoUrl)
                    .error(R.drawable.placeholder)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(profileCircleImageView);

            String name = facebookUsersDetail.getName();
            System.out.println("Name = "+name);
            nameTextView.setText("Name : "+name);

            String firstName = facebookUsersDetail.getFirstName();
            System.out.println("First Name = "+firstName);
            firstNameTextView.setText("First Name : "+firstName);

            String lastName = facebookUsersDetail.getLastName();
            System.out.println("Last Name = "+lastName);
            lastNameTextView.setText("Last Name : "+lastName);

            String email = facebookUsersDetail.getEmail();
            System.out.println("Email = "+email);
            emailTextView.setText("Email : "+email);

            String facebookId = facebookUsersDetail.getFacebookId();
            System.out.println("Facebook Id = "+facebookId);
            facebookIdTextView.setText("Facebook Id : "+facebookId);
        }
        else
        {
            ActivityUtils.launchActivityWithClearBackStack(DetailActivity.this, SignInActivity.class);
        }
    }

    /*
     ***********************************************************************************************
     ******************************************* Helper methods ************************************
     ***********************************************************************************************
     */

    /**
     * Method to do facebook sign out
     * This code clears which account is connected to the app. To sign in again, the user must choose their account again.
     */
    private void signOut() {
        if(AccessToken.getCurrentAccessToken() != null)
        {
            LoginManager.getInstance().logOut();
            ActivityUtils.launchActivityWithClearBackStack(DetailActivity.this, SignInActivity.class);
        }
    }

    private void signOutSecond() {
        if (AccessToken.getCurrentAccessToken() != null) {
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                    .Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {
                    AccessToken.setCurrentAccessToken(null);
                    LoginManager.getInstance().logOut();
                    ActivityUtils.launchActivityWithClearBackStack(DetailActivity.this, SignInActivity.class);
                }
            }).executeAsync();
        }
    }
}