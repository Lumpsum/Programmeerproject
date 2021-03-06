package mprog.nl.programmeerproject.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mprog.nl.programmeerproject.Classes.Scheme;
import mprog.nl.programmeerproject.R;

/**
 * Activity that displays information of a specific scheme and allows them to be
 * rated and editted if the user has those rights.
 */
public class SpecificSchemeActivity extends AppCompatActivity implements View.OnClickListener {

    // Init variables
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId;
    DatabaseReference databaseRef;
    DatabaseReference ref;

    ImageButton homeButton;
    ImageButton findButton;
    ImageButton chatButton;
    ImageButton schemeButton;
    Button rateEditButton;

    TextView titleText;
    TextView catText;
    TextView keyText;
    TextView descText;

    RatingBar rateBar;

    float rating;
    float currentRating;
    int ratingAmount;

    String title;
    String category;
    String description;

    ArrayList<String> keywords;

    HashMap<String, String> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_scheme);

        // Assign firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        ref = databaseRef.child("Schemes");
        userId = firebaseUser.getUid();

        // Retrieve information from the previous activity
        Intent intent = getIntent();
        title = intent.getStringExtra("Title");
        category = intent.getStringExtra("Category");

        // Assign to the xml elements and init the variables
        assignButtons();

        assignTextViews();

        keywords = new ArrayList<String>();

        users = new HashMap<>();

        rateBar = (RatingBar)findViewById(R.id.specSchemeRating);

        // Retrieve information from the specific title that is chosen.
        retrieveAndSetSchemeData();
    }

    /**
     * Calculates the rating if it's the first time, so adds to the rateAmount.
     *
     * @return Returns new rating of the scheme.
     */
    float calcNewRating() {
        float newRating = (((rating * ratingAmount) + rateBar.getRating()) / (ratingAmount + 1));
        return newRating;
    }

    /**
     * Calculates the rating if the user already rated the scheme, so without adding to the rateAmount
     * and taking into account the previous given rating for the calculation.
     *
     * @return Returns the new rating for the scheme.
     */
    float calcRatingAgain() {
        float newRating = ((((rating * ratingAmount) - currentRating) + rateBar.getRating()) / (ratingAmount));
        return newRating;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homeButton:
                startActivity(MainActivity.createNewIntent(SpecificSchemeActivity.this, MainActivity.class));
                break;
            case R.id.findButton:
                startActivity(MainActivity.createNewIntent(SpecificSchemeActivity.this, FindUserActivity.class));
                break;
            case R.id.chatButton:
                startActivity(MainActivity.createNewIntent(SpecificSchemeActivity.this, ChatActvity.class));
                break;
            case R.id.schemeButton:
                startActivity(MainActivity.createNewIntent(SpecificSchemeActivity.this, SchemeActivity.class));
                break;

            // Button that either lets the user edit if he's the author or rate if he is not.
            case R.id.specSchemeButton:
                rateOrEditScheme();
                break;
        }
    }

    /**
     * Assign the buttons to xml elements and set the listeners.
     */
    void assignButtons() {
        homeButton = (ImageButton)findViewById(R.id.homeButton);
        findButton = (ImageButton)findViewById(R.id.findButton);
        chatButton = (ImageButton)findViewById(R.id.chatButton);
        schemeButton = (ImageButton)findViewById(R.id.schemeButton);
        rateEditButton = (Button)findViewById(R.id.specSchemeButton);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);
        schemeButton.setOnClickListener(this);
        rateEditButton.setOnClickListener(this);
    }

    /**
     * Assign the textviews to xml elements and set the text.
     */
    void assignTextViews() {
        titleText = (TextView)findViewById(R.id.specSchemeTitleText);
        catText = (TextView)findViewById(R.id.specSchemeCatText);
        keyText = (TextView)findViewById(R.id.specSchemeKeyText);
        descText = (TextView)findViewById(R.id.specSchemeDescText);

        titleText.setText(title);
        catText.setText(category);
    }

    /**
     * Method that retrieves the data of the scheme object and sets the text accordingly.
     *
     * @param s Scheme object that contains all the data.
     */
    void setSchemeData(Scheme s) {
        for(Map.Entry<String, String> entry : s.Keywords.entrySet()) {
            keywords.add(String.valueOf(entry.getKey()));
            keyText.setText(keyText.getText() + entry.getValue() + " ");
        }

        description = s.Description;
        descText.setText(description);

        rating = (float) s.Rating;
        rateBar.setRating(rating);

        ratingAmount = s.RateAmount;

        if (userId.equals(s.Author)) {
            rateEditButton.setText("Edit");
        } else if (!rateEditButton.getText().equals("Rate Again")) {
            rateEditButton.setText("Rate");
        }

        if (s.Users != null) {
            for (Map.Entry<String, Double> entry : s.Users.entrySet()) {
                if (entry.getKey().equals(userId)) {
                    currentRating = Float.parseFloat(String.valueOf(entry.getValue()));
                    rateEditButton.setText("Rate Again");
                }
                users.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
    }

    /**
     * Method that behave differently based on the text that the button has.
     */
    void rateOrEditScheme() {
        // Starts the edit scheme activity and passes the current information to that activity.
        if (rateEditButton.getText().equals("Edit")) {
            startEditActivity();
        }

        // If the user already rated the scheme adjust the rating accordingly.
        else if (rateEditButton.getText().equals("Rate Again")) {

            // Calculate the new rating.
            editRatingAgain();
        }

        // If rated for the first time, calculate the rating different and add the user to the
        // already rated by users entry.
        else {
            // Calculate rating.
            editRating();
        }
    }

    /**
     * Starts a new edit activity based on the available information.
     */
    void startEditActivity() {
        Intent newIntent = MainActivity.createNewIntent(SpecificSchemeActivity.this, EditSchemeActivity.class);
        newIntent.putExtra("Title", title);
        newIntent.putExtra("Category", category);
        newIntent.putExtra("Description", description);
        newIntent.putExtra("Rating", rating);
        newIntent.putExtra("RatingAmount", ratingAmount);
        newIntent.putStringArrayListExtra("Keywords", keywords);
        newIntent.putExtra("Users", users);
        startActivity(newIntent);
    }

    /**
     * Recalculate the rating.
     */
    void editRatingAgain() {
        rating = calcRatingAgain();
        ref = databaseRef.child("Schemes").child(category).child(title);
        ref.child("Rating").setValue(rating);
        ref.child("Users").child(userId).setValue(rateBar.getRating());
        rateBar.setRating(rating);
        MainActivity.createToast(SpecificSchemeActivity.this, "Rated this scheme again.").show();
    }

    /**
     * Edits the rating after first time rating.
     */
    void editRating() {
        rating = calcNewRating();
        ratingAmount = (ratingAmount + 1);
        ref = databaseRef.child("Schemes").child(category).child(title);
        ref.child("Rating").setValue(rating);
        ref.child("RateAmount").setValue(ratingAmount);
        ref.child("Users").child(userId).setValue(rateBar.getRating());
        rateBar.setRating(rating);
        MainActivity.createToast(SpecificSchemeActivity.this, "Rated this scheme.").show();
    }

    /**
     * Retrieves the scheme data and sets the text and keywords accordingly.
     */
    void retrieveAndSetSchemeData() {
        ref = ref.child(category).child(title);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Scheme scheme = dataSnapshot.getValue(Scheme.class);
                setSchemeData(scheme);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
