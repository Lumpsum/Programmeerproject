package mprog.nl.programmeerproject.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import mprog.nl.programmeerproject.Classes.User;
import mprog.nl.programmeerproject.R;

/**
 * Activity that displays information about a user who matches with your parameters.
 */
public class SpecificUserActivity extends AppCompatActivity implements View.OnClickListener {

    // Init variables
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId;
    DatabaseReference databaseRef;
    DatabaseReference ref;

    TextView firstText;
    TextView lastText;
    TextView genderText;
    TextView ageText;
    TextView sportText;
    TextView levelText;
    TextView descText;

    ImageButton searchButton;
    ImageButton addButton;
    ImageButton homeButton;
    ImageButton findButton;
    ImageButton chatButton;
    ImageButton schemeButton;

    String foundUserId;

    ArrayList<String> foundUserIds;

    int selector;
    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_user);

        // Retrieve information from the previous activity and picks an
        // userId from the given list.
        retrieveIntentData();


        // Assign firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        ref = databaseRef.child("Users");

        // Assign to the xml elements and init the variables
        assignTextViews();

        assignButtons();

        // Fills the activity with information of the chosen userId
        findAndFillUserInfo();
    }

    // Generic button handler for the bottom menu and the other buttons.
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            // Search again button that takes the next userId of the array or returns
            // to the findUserActivity if no users are left.
            case R.id.specUserSearchButton:
                tryNextUser();
                break;

            // Adds your userId to the request list of the other user and start the main Activity.ImageButton
            case R.id.specUserAddButton:
                sendUserRequest();
                break;
            case R.id.homeButton:
                startActivity(MainActivity.createNewIntent(SpecificUserActivity.this, MainActivity.class));
                break;
            case R.id.findButton:
                startActivity(MainActivity.createNewIntent(SpecificUserActivity.this, FindUserActivity.class));
                break;
            case R.id.chatButton:
                startActivity(MainActivity.createNewIntent(SpecificUserActivity.this, ChatActvity.class));
                break;
            case R.id.schemeButton:
                startActivity(MainActivity.createNewIntent(SpecificUserActivity.this, SchemeActivity.class));
                break;
        }
    }

    /**
     * Assigns the buttons to the xml elements and sets the listeners.
     */
    void assignButtons() {
        searchButton = (ImageButton)findViewById(R.id.specUserSearchButton);
        addButton = (ImageButton)findViewById(R.id.specUserAddButton);
        homeButton = (ImageButton)findViewById(R.id.homeButton);
        findButton = (ImageButton)findViewById(R.id.findButton);
        chatButton = (ImageButton)findViewById(R.id.chatButton);
        schemeButton = (ImageButton)findViewById(R.id.schemeButton);

        searchButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);
        schemeButton.setOnClickListener(this);
    }

    /**
     * Assigns the textviews to the xml elements.
     */
    void assignTextViews() {
        firstText = (TextView)findViewById(R.id.specUserFirstText);
        lastText = (TextView)findViewById(R.id.specUserLastText);
        genderText = (TextView)findViewById(R.id.specUserGenderText);
        ageText = (TextView)findViewById(R.id.specUserAgeText);
        sportText = (TextView)findViewById(R.id.specUserSportText);
        levelText = (TextView)findViewById(R.id.specUserLevelText);
        descText = (TextView)findViewById(R.id.specUserDescText);
    }

    /**
     * Retrieves the intent data from the previous activity.
     */
    void retrieveIntentData() {
        Bundle bundle = this.getIntent().getExtras();
        foundUserIds = bundle.getStringArrayList("foundUserIds");
        selector = this.getIntent().getIntExtra("selector", 0);
        foundUserId = foundUserIds.get(selector);
        selector = selector + 1;
        size = foundUserIds.size();
    }

    /**
     * Sets the text to the user information.
     *
     * @param u User class that contains the data.
     */
    void fillUserInfo(User u) {
        firstText.setText(firstText.getText() + ": " + u.FirstName);
        lastText.setText(lastText.getText() + ": " + u.LastName);
        genderText.setText(genderText.getText().toString() + u.Gender);
        ageText.setText(ageText.getText() + ": " + u.Age);
        sportText.setText(sportText.getText() + ": " + u.Sport);
        levelText.setText(levelText.getText() + " " + u.Level);
        descText.setText(descText.getText().toString() + u.Description);
    }

    /**
     * Finds the user info in the database and fills the textviews.
     */
    void findAndFillUserInfo() {
        ref.child(foundUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                fillUserInfo(user);
            }
        });
    }

    /**
     * Sends a user request to the user.
     */
    void sendUserRequest() {
        ref.child(userId).child("RefusedUsers").child(foundUserId).setValue(foundUserId);
        ref.child(foundUserId).child("RefusedUsers").child(userId).setValue(userId);
        ref.child(foundUserId).child("UserRequests").child(userId).setValue(userId);
        MainActivity.createToast(SpecificUserActivity.this, "User request send.").show();
        startActivity(MainActivity.createNewIntent(SpecificUserActivity.this, MainActivity.class));
    }

    /**
     * Retrieves the next user from the list and starts an activity with that users information.
     */
    void presentNextUser() {
        Intent newIntent = MainActivity.createNewIntent(SpecificUserActivity.this, SpecificUserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("foundUserIds", foundUserIds);
        newIntent.putExtras(bundle);
        newIntent.putExtra("selector", selector);
        MainActivity.createToast(SpecificUserActivity.this, "Next user.").show();
        startActivity(newIntent);
    }

    /**
     * Adds the refused user and tries to grab the next.
     */
    void tryNextUser() {
        ref.child(userId).child("RefusedUsers").child(foundUserId).setValue(foundUserId);
        ref.child(foundUserId).child("RefusedUsers").child(userId).setValue(userId);
        if (selector < size) {
            presentNextUser();
        }
        else {
            MainActivity.createToast(SpecificUserActivity.this, "All users found, please adjust parameters.").show();
            startActivity(MainActivity.createNewIntent(SpecificUserActivity.this, FindUserActivity.class));
        }
    }
}
