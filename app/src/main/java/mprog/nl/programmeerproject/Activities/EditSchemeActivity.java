package mprog.nl.programmeerproject.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

import mprog.nl.programmeerproject.R;

/**
 * Activity that let's the user edit the information of a particular scheme.
 * The information gets removed and readded completely.
 */
public class EditSchemeActivity extends AppCompatActivity implements View.OnClickListener {

    // Init variables
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId;
    DatabaseReference databaseRef;
    DatabaseReference ref;

    Button homeButton;
    Button findButton;
    Button chatButton;
    Button schemeButton;
    Button editButton;

    EditText titleEdit;
    EditText descEdit;

    Spinner firstKeySpinner;
    Spinner secondKeySpinner;
    Spinner thirdKeySpinner;

    ArrayList<String> keyArray;

    ArrayAdapter<String> keyAdapter;
    ArrayAdapter<String> optionalKeyAdapter;

    String title;
    String newTitle;
    String category;
    String desc;

    float rating;
    int ratingAmount;

    ArrayList<String> keywords;
    HashMap<String, String> users;

    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_scheme);

        // Retrieve the scheme information passed by the previous activity.
        Intent intent = getIntent();
        title = intent.getStringExtra("Title");
        category = intent.getStringExtra("Category");
        desc = intent.getStringExtra("Description");
        rating = intent.getFloatExtra("Rating", 0);
        ratingAmount = intent.getIntExtra("RatingAmount", 0);
        keywords = intent.getStringArrayListExtra("Keywords");
        users = (HashMap<String, String>) intent.getSerializableExtra("Users");

        // Assign firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        ref = databaseRef.child("Schemes");
        userId = firebaseUser.getUid();

        // Assign to the xml elements and init the variables
        homeButton = (Button)findViewById(R.id.homeButton);
        chatButton = (Button)findViewById(R.id.chatButton);
        findButton = (Button)findViewById(R.id.findButton);
        schemeButton = (Button)findViewById(R.id.schemeButton);
        editButton = (Button)findViewById(R.id.editSchemeButton);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);
        schemeButton.setOnClickListener(this);
        editButton.setOnClickListener(this);

        titleEdit = (EditText)findViewById(R.id.editSchemeTitleEdit);
        descEdit = (EditText)findViewById(R.id.editSchemeDescEdit);

        titleEdit.setText(title);
        descEdit.setText(desc);

        firstKeySpinner = (Spinner)findViewById(R.id.editSchemeFirstSpinner);
        secondKeySpinner = (Spinner)findViewById(R.id.editSchemeSecondSpinner);
        thirdKeySpinner = (Spinner)findViewById(R.id.editSchemeThirdSpinner);

        keyArray = MainActivity.createKeyArray();

        keyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, keyArray);
        keyArray.add("");
        optionalKeyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, keyArray);

        keyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        firstKeySpinner.setAdapter(keyAdapter);
        secondKeySpinner.setAdapter(keyAdapter);
        thirdKeySpinner.setAdapter(keyAdapter);

        secondKeySpinner.setSelection(optionalKeyAdapter.getPosition(""));
        thirdKeySpinner.setSelection(optionalKeyAdapter.getPosition(""));

        // Set the selection of the keywords to the previously chosen keywords.
        int i = 0;
        while (i < keywords.size()) {
            switch (i) {
                case 0:
                    firstKeySpinner.setSelection(keyAdapter.getPosition(keywords.get(i)));
                    break;
                case 1:
                    secondKeySpinner.setSelection(optionalKeyAdapter.getPosition(keywords.get(i)));
                    break;
                case 2:
                    thirdKeySpinner.setSelection(optionalKeyAdapter.getPosition(keywords.get(i)));
                    break;
            }
            i++;
        }

        // Thread that removes the scheme from the users entry and readds all the information.
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                databaseRef.child("Users").child(userId).child("Schemes").child(category).child(title).removeValue();
                ref = ref.child(newTitle);
                String firstKey = firstKeySpinner.getSelectedItem().toString();
                String secondKey = secondKeySpinner.getSelectedItem().toString();
                String thirdKey = thirdKeySpinner.getSelectedItem().toString();

                // Readds all the information in the FireBase
                CreateSchemeActivity.setDescriptionAndKeywords(ref, desc, firstKey, secondKey, thirdKey);
                CreateSchemeActivity.setUserAndRating(ref, userId, rating, ratingAmount);
                for (Map.Entry<String, String> entry : users.entrySet()) {
                    ref.child("Users").child(entry.getKey()).setValue(entry.getValue());
                }
                databaseRef.child("Users").child(userId).child("Schemes")
                        .child(category)
                        .child(newTitle).setValue(newTitle);

                startActivity(MainActivity.createNewIntent(EditSchemeActivity.this, SchemeActivity.class));
            }
        });
    }

    // Generic button handler for the bottom menu and all the other buttons in this activity.
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homeButton:
                startActivity(MainActivity.createNewIntent(EditSchemeActivity.this, MainActivity.class));
                break;
            case R.id.findButton:
                startActivity(MainActivity.createNewIntent(EditSchemeActivity.this, FindUserActivity.class));
                break;
            case R.id.chatButton:
                startActivity(MainActivity.createNewIntent(EditSchemeActivity.this, ChatActvity.class));
                break;
            case R.id.schemeButton:
                startActivity(MainActivity.createNewIntent(EditSchemeActivity.this, SchemeActivity.class));
                break;

            // Checks whther fields are not empty and adds the new information to the database.
            case R.id.editSchemeButton:
                newTitle = titleEdit.getText().toString();
                desc = descEdit.getText().toString();
                if (newTitle.isEmpty() || desc.isEmpty()) {
                    MainActivity.createAlert("Please fill in both the title and description field.", EditSchemeActivity.this).show();
                }
                else {
                    ref = databaseRef.child("Schemes").child(category);
                    ref.child(title).removeValue();
                    checkTitleCreateEdit(ref);
                }
                break;
        }
    }

    /**
     * Checks whether the title is unique and adds the information to database if so.
     *
     * @param ref Reference to the category of the scheme.
     */
    void checkTitleCreateEdit(DatabaseReference ref) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean validTitle = true;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getKey().equals(newTitle)) {
                        validTitle = false;
                        break;
                    }
                }

                // Checks the title and gives an error if already used.
                if (validTitle) {
                    t.run();
                } else {
                    MainActivity.createAlert("Title is already used.", EditSchemeActivity.this).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
