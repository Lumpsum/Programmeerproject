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
        ref = ref.child(category).child(title);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    switch (postSnapshot.getKey()) {

                        //Creates an array of keywords and sets the text of the keywords.
                        case "Keywords":
                            for (DataSnapshot postPostSnapshot : postSnapshot.getChildren()) {
                                String keyword = postPostSnapshot.getValue().toString();
                                keywords.add(keyword);
                                keyText.setText(keyText.getText() + keyword + " ");
                            }
                            break;
                        case "Description":
                            description = postSnapshot.getValue().toString();
                            descText.setText(description);
                            break;
                        case "Rating":
                            rating = Float.parseFloat(postSnapshot.getValue().toString());
                            rateBar.setRating(rating);
                            break;
                        case "RateAmount":
                            ratingAmount = Integer.parseInt(postSnapshot.getValue().toString());
                            break;

                        // Checks whether the user is the author or not and changes the button accordingly.
                        case "Author":
                            if (userId.equals(postSnapshot.getValue())) {
                                rateEditButton.setText("Edit");
                            } else if (!rateEditButton.getText().equals("Rate Again")) {
                                rateEditButton.setText("Rate");
                            }
                            break;

                        // Checks whether the user already rated this scheme and adjust to that knowledge.
                        case "Users":
                            for (DataSnapshot postPostSnapshot : postSnapshot.getChildren()) {
                                if (postPostSnapshot.getKey().equals(userId)) {
                                    currentRating = Float.parseFloat(postPostSnapshot.getValue().toString());
                                    rateEditButton.setText("Rate Again");
                                }
                                users.put(postPostSnapshot.getKey(), postPostSnapshot.getValue().toString());
                            }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

                // Starts the edit scheme activity and passes the current information to that activity.
                if (rateEditButton.getText().equals("Edit")) {
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

                // If the user already rated the scheme adjust the rating accordingly.
                else if (rateEditButton.getText().equals("Rate Again")) {

                    // Calculate the new rating.
                    rating = calcRatingAgain();
                    ref = databaseRef.child("Schemes").child(category).child(title);
                    ref.child("Rating").setValue(rating);
                    ref.child("Users").child(userId).setValue(rateBar.getRating());
                    rateBar.setRating(rating);
                    MainActivity.createToast(SpecificSchemeActivity.this, "Rated this scheme again.").show();
                }

                // If rated for the first time, calculate the rating different and add the user to the
                // already rated by users entry.
                else {
                    // Calculate rating.
                    rating = calcNewRating();
                    ratingAmount = (ratingAmount + 1);
                    ref = databaseRef.child("Schemes").child(category).child(title);
                    ref.child("Rating").setValue(rating);
                    ref.child("RateAmount").setValue(ratingAmount);
                    ref.child("Users").child(userId).setValue(rateBar.getRating());
                    rateBar.setRating(rating);
                    MainActivity.createToast(SpecificSchemeActivity.this, "Rated this scheme.").show();
                }
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
}
