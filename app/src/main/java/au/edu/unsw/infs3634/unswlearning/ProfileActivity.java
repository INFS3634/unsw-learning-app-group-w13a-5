package au.edu.unsw.infs3634.unswlearning;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;

    private TextView mName, mUsername, mCountLevel, mCountPoint;
    private ImageView mProfilePhoto;
    private ImageView africaBadge;
    private ImageView asiaBadge;
    private ImageView europeBadge;
    private ImageView northAmericaBadge;
    private ImageView southAmericaBadge;
    private ImageView oceaniaBadge;

    //Edit profile
    private ImageView settingButton;

    //Firebase
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods mFirebaseMethods;

    //Create a new user (example)
    User user = new User("Paul Ramos", "paul_ramos_01", "hi", "test");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Interface attributes
        mName = findViewById(R.id.mName);
        mUsername = findViewById(R.id.mUsername);
        mCountLevel = findViewById(R.id.countLevelPassed);
        mCountPoint = findViewById(R.id.countTotalPoints);
        mProfilePhoto = findViewById(R.id.mProfilePhoto);

        africaBadge = findViewById(R.id.africaBadge);
        asiaBadge = findViewById(R.id.asiaBadge);
        europeBadge = findViewById(R.id.europeBadge);
        northAmericaBadge = findViewById(R.id.northAmericaBadge);
        southAmericaBadge = findViewById(R.id.southAmericaBadge);
        oceaniaBadge = findViewById(R.id.oceaniaBadge);

        //Bottom Navigation View
        bottomNav = findViewById(R.id.bottomNavigationView);
        //Setting
        settingButton = findViewById(R.id.settingButton);
        //Firebase
        mContext = getApplicationContext();
        mFirebaseMethods = new FirebaseMethods(mContext);

        //Setup FirebaseAuth
        setupFirebaseAuth();

        /**
         * Set up Bottom Navigation View
         */
        bottomNav.setSelectedItemId(R.id.profile);

        //Perform item selected listener
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                //Check which item is selected
                switch (item.getItemId()) {
                    case R.id.quiz:
                        startActivity(new Intent(getApplicationContext(), QuizActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.learn:
                        startActivity(new Intent(getApplicationContext(), LearnActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        return true;
                }
                return false;
            }
        });

        /**
         * Setting screen
         */
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSetting();
            }
        });
    }
/*
------------------------------Firebase------------------------
 */

    /**
     * Set up Firebase Auth
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase authentication");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = mFirebaseDatabase.getReference(String.valueOf(R.string.database_url));

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in: " + user.getUid());
                }
                else {
                    //User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        //Get the data snapshot to read data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Retrieve user data from database
                displayUserProfile(mFirebaseMethods.getUserSettings(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("databaseReference failed!");
            }
        });
    }

    private void displayUserProfile(UserSettings userSettings) {
        //User user = userSettings.getUser();
        Log.d(TAG, "displayUserProfile: retrieve data from firebase authentication");
        UserAccountSettings settings = userSettings.getSettings();

        mName.setText(settings.getName());
        mUsername.setText(settings.getUsername());
        mCountLevel.setText(String.valueOf(settings.getCount_level()));
        mCountPoint.setText(String.valueOf(settings.getCount_point()));

    }

    public void launchSetting() {
        Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
        //switch to Setting screen
        startActivity(intent);
    }




    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
