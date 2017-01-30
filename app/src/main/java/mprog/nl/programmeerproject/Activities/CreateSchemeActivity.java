package mprog.nl.programmeerproject.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import mprog.nl.programmeerproject.R;

/**
 * Activity that let's you create a scheme according to a given format.
 */
public class CreateSchemeActivity extends AppCompatActivity implements View.OnClickListener {

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
    Button createButton;

    EditText titleEdit;
    EditText descEdit;

    Spinner categorySpinner;
    Spinner firstKeySpinner;
    Spinner secondKeySpinner;
    Spinner thirdKeySpinner;

    ArrayList<String> categorySpinnerArray;
    ArrayList<String> keyArray;

    ArrayAdapter<String> categoryAdapter;
    ArrayAdapter<String> keyAdapter;
    ArrayAdapter<String> optionalKeyAdapter;

    String title;
    String desc;

    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_scheme);

        // Assign firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        ref = databaseRef.child("Schemes");

        // Assign to the xml elements and init the variables
        findButton = (ImageButton)findViewById(R.id.findButton);
        homeButton = (ImageButton)findViewById(R.id.homeButton);
        chatButton = (ImageButton)findViewById(R.id.chatButton);
        schemeButton = (ImageButton)findViewById(R.id.schemeButton);
        createButton = (Button)findViewById(R.id.createSchemeCreateButton);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);
        schemeButton.setOnClickListener(this);
        createButton.setOnClickListener(this);

        titleEdit = (EditText)findViewById(R.id.createSchemeTitleEdit);
        descEdit = (EditText)findViewById(R.id.createSchemeDescEdit);

        categorySpinner = (Spinner)findViewById(R.id.createSchemeCatSpinner);
        firstKeySpinner = (Spinner)findViewById(R.id.createSchemeFirstSpinner);
        secondKeySpinner = (Spinner)findViewById(R.id.createSchemeSecondSpinner);
        thirdKeySpinner = (Spinner)findViewById(R.id.createSchemeThirdSpinner);

        categorySpinnerArray = MainActivity.createSportArray();
        keyArray = MainActivity.createKeyArray();

        categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorySpinnerArray);
        keyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, keyArray);
        keyArray.add("");
        optionalKeyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, keyArray);

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        keyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(categoryAdapter);
        firstKeySpinner.setAdapter(keyAdapter);
        thirdKeySpinner.setAdapter(keyAdapter);
        secondKeySpinner.setAdapter(keyAdapter);

        // Sets the selection to empty in order to allow optional keywords
        secondKeySpinner.setSelection(optionalKeyAdapter.getPosition(""));
        thirdKeySpinner.setSelection(optionalKeyAdapter.getPosition(""));

        // Extra thread that fills the FireBase with the entries and starts a new activity.
        // Only get called when the FireBase has received all of it's results.
        t = new Thread(new Runnable() {
            @Override
            public void run() {
            ref = databaseRef.child("Schemes").child(categorySpinner.getSelectedItem().toString()).child(title);
            String firstKey = firstKeySpinner.getSelectedItem().toString();
            String secondKey = secondKeySpinner.getSelectedItem().toString();
            String thirdKey = thirdKeySpinner.getSelectedItem().toString();

            // Fills the Firebase with the given information
            setDescriptionAndKeywords(ref, desc, firstKey, secondKey, thirdKey);
            setUserAndRating(ref, userId, 0, 0);
            databaseRef.child("Users").child(userId).child("Schemes")
                    .child(categorySpinner.getSelectedItem().toString())
                    .child(title).setValue(title);

            MainActivity.createToast(CreateSchemeActivity.this, "Scheme succesfully created.").show();
            startActivity(MainActivity.createNewIntent(CreateSchemeActivity.this, SchemeActivity.class));
            }
        });
    }

    // Generic button handler that handles the menu buttons and all the other buttons inside this activity.
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homeButton:
                startActivity(MainActivity.createNewIntent(CreateSchemeActivity.this, MainActivity.class));
                break;
            case R.id.findButton:
                startActivity(MainActivity.createNewIntent(CreateSchemeActivity.this, FindUserActivity.class));
                break;
            case R.id.chatButton:
                startActivity(MainActivity.createNewIntent(CreateSchemeActivity.this, ChatActvity.class));
                break;
            case R.id.schemeButton:
                startActivity(MainActivity.createNewIntent(CreateSchemeActivity.this, SchemeActivity.class));
                break;

            //Checks whether all the fields are filled in and fills the database with the scheme
            // if this is the case.
            case R.id.createSchemeCreateButton:
                title = titleEdit.getText().toString();
                desc = descEdit.getText().toString();
                if (title.isEmpty() || desc.isEmpty()) {
                    MainActivity.createAlert("Please fill in both the title and description field.", CreateSchemeActivity.this).show();
                }
                else {
                    ref = databaseRef.child("Schemes").child(categorySpinner.getSelectedItem().toString());
                    checkTitleCreateScheme(ref);
                }
                break;
        }
    }

    /**
     * Method that checks whether the title is already in use.
     * If so gives an error, else fills the FireBase with the given data.
     *
     * @param ref Reference to the schemes and category that the scheme falls under
     */
    void checkTitleCreateScheme(DatabaseReference ref) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean validTitle = true;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getKey().equals(title)) {
                        validTitle = false;
                        break;
                    }
                }

                // Checks whether the title is valid
                if (validTitle) {
                    t.run();
                } else {
                    MainActivity.createAlert("Title is already used.", CreateSchemeActivity.this).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Method that fills the FireBase wit hteh given information and handles the keywords.
    static void setDescriptionAndKeywords(DatabaseReference ref, String desc, String firstKey, String secondKey, String thirdKey) {
        ref.child("Description").setValue(desc);
        ref.child("Keywords").child(firstKey).setValue(firstKey);

        // Checks whether additional keywords are given.
        if (!secondKey.isEmpty()) {
            ref.child("Keywords").child(secondKey).setValue(secondKey);
        }
        if (!thirdKey.isEmpty()) {
            ref.child("Keywords").child(thirdKey).setValue(thirdKey);
        }
    }

    // Fills the FireBase with the given user information.
    static void setUserAndRating(DatabaseReference ref, String userId, float rating, int rateAmount) {
        ref.child("Author").setValue(userId);
        ref.child("Rating").setValue(rating);
        ref.child("RateAmount").setValue(rateAmount);
    }
}
