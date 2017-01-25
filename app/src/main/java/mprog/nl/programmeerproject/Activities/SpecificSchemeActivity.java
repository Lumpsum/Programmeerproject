package mprog.nl.programmeerproject.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import mprog.nl.programmeerproject.R;

public class SpecificSchemeActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId;
    DatabaseReference databaseRef;
    DatabaseReference ref;

    Button homeButton;
    Button findButton;
    Button chatButton;
    Button schemeButton;
    Button rateEditButton;

    TextView titleText;
    TextView catText;
    TextView keyText;
    TextView descText;

    RatingBar rateBar;

    float rating;
    int ratingAmount;

    String title;
    String category;
    String description;

    ArrayList<String> keywords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_scheme);

        Intent intent = getIntent();
        title = intent.getStringExtra("Title");
        category = intent.getStringExtra("Category");

        // Assign firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        ref = databaseRef.child("Schemes");

        homeButton = (Button)findViewById(R.id.homeButton);
        findButton = (Button)findViewById(R.id.findButton);
        chatButton = (Button)findViewById(R.id.chatButton);
        schemeButton = (Button)findViewById(R.id.schemeButton);
        rateEditButton = (Button)findViewById(R.id.specSchemeButton);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);
        schemeButton.setOnClickListener(this);
        rateEditButton.setOnClickListener(this);

        titleText = (TextView)findViewById(R.id.specSchemeTitleText);
        catText = (TextView)findViewById(R.id.specSchemeCatText);
        keyText = (TextView)findViewById(R.id.specSchemeKeyText);
        descText = (TextView)findViewById(R.id.specSchemeDescText);

        titleText.setText(title);
        catText.setText(category);

        keywords = new ArrayList<String>();

        rateBar = (RatingBar)findViewById(R.id.specSchemeRating);
        ref = ref.child(category).child(title);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    switch (postSnapshot.getKey()) {
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
                        case "User":
                            if (userId.equals(postSnapshot.getValue())) {
                                rateEditButton.setText("Edit");
                            } else {
                                rateEditButton.setText("Rate");
                            }
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            case R.id.specSchemeButton:
                if (rateEditButton.getText().equals("Edit")) {
                    Intent newIntent = MainActivity.createNewIntent(SpecificSchemeActivity.this, EditSchemeActivity.class);
                    newIntent.putExtra("Title", title);
                    newIntent.putExtra("Category", category);
                    newIntent.putExtra("Description", description);
                    newIntent.putExtra("Rating", rating);
                    newIntent.putExtra("RatingAmount", ratingAmount);
                    newIntent.putStringArrayListExtra("Keywords", keywords);
                    Log.d("Start", "New Activity");
                    startActivity(newIntent);
                } else {
                    rating = calcNewRating();
                    ratingAmount = (ratingAmount + 1);
                    ref = databaseRef.child("Schemes").child(category).child(title);
                    ref.child("Rating").setValue(rating);
                    ref.child("RateAmount").setValue(ratingAmount);
                    rateBar.setRating(rating);
                }
                break;
        }
    }

    Float calcNewRating() {
        Float newRating = (((rating * ratingAmount) + rateBar.getRating()) / (ratingAmount + 1));
        return newRating;
    }
}
