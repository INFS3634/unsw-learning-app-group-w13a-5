package au.edu.unsw.infs3634.unswlearning;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SettingActivity extends AppCompatActivity {

    private EditText currentName, currentUsername, currentEmail, currentPassword;
    private Button saveChange;
    private TextView changePhoto;
    private ImageView currentPhoto;

    //Firebase
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods mFirebaseMethods;
    private String user_id;

    //Variables
    private UserSettings mUserSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setting);

        currentName = findViewById(R.id.currentName);
        currentUsername = findViewById(R.id.currentUsername);
        currentEmail = findViewById(R.id.currentEmail);
        currentPassword = findViewById(R.id.currentPassword);
        saveChange = findViewById(R.id.saveChange);
        changePhoto = findViewById(R.id.changePhoto);
        currentPhoto = findViewById(R.id.currentPhoto);

        //Firebase
        mContext = getApplicationContext();
        mFirebaseMethods = new FirebaseMethods(mContext);

        //Set up Firebase Auth
        setupFirebaseAuth();
        //Pre-load current information to EditTextView
        //displayUserProfile(mUserSettings);
        //When user clicks on any of the EditTextView, saveChange button will appear

        //Save changes
        saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes");
                saveProfileSettings();
            }
        });

    }

    //Set up Firebase Auth
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase authentication");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = mFirebaseDatabase.getReference(String.valueOf(R.string.database_url));
        user_id = mAuth.getCurrentUser().getUid();

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

        //Retrieve current user data from database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                displayUserProfile(mFirebaseMethods.getUserSettings(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Retrieve data from EditTextzview and submit it to the database
     * Before doing so, check to make sure username is unique
     */

    private void saveProfileSettings() {
        final String displayName = currentName.getText().toString();
        final String username = currentUsername.getText().toString();
        final String email = currentEmail.getText().toString();
        final String password = currentPassword.getText().toString();

        //Listen for changes once
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Case 1: User changed their username --> require to check unique username
                //Compare username in EditTextView and username loaded from database
                if(!mUserSettings.getUser().getUsername().equals(username)) {
                    checkIfUsernameExists(username);
                }
                //Case 2: User changed their display name
                else if (!mUserSettings.getUser().getName().equals(displayName)){
                    mFirebaseMethods.updateDisplayName(displayName);
                }
                //Case 3: User changed Firebase Authenticated Email

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Check if @param username already exists in the database
     * @param username
     */
    private void checkIfUsernameExists(String username) {
        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //Use Query database
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    //Update username
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(getApplicationContext(), "Saved new username", Toast.LENGTH_SHORT).show();
                }
                for(DataSnapshot singleSnapshot: snapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(getApplicationContext(), "Username already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void displayUserProfile(UserSettings userSettings) {
        Log.d(TAG, "displayUserProfile with data retrieving from firebase database: " + userSettings.toString());

        //User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        mUserSettings = userSettings;
        currentName.setText(settings.getName());
        currentUsername.setText(settings.getUsername());
        currentEmail.setText(userSettings.getUser().getEmail());
        currentPassword.setText(settings.getPassword());

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
