package mprog.nl.programmeerproject.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import mprog.nl.programmeerproject.R;

/**
 * Activity that handles the second page of the profile creation, containing
 * information about the sport and an additional optional description.
 */
public class CreateProfileSecondActivity extends AppCompatActivity {

    // Init variables
    private DatabaseReference ref;

    protected Spinner sportSpinner;
    protected Spinner levelSpinner;

    protected List<String> sportSpinnerArray;
    protected List<String> levelSpinnerArray;

    protected ArrayAdapter<String> sportAdapter;
    protected ArrayAdapter<String> levelAdapter;

    protected EditText descEdit;

    protected Button createProfButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile_second);

        // Init FireBase variables
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        ref = databaseRef.child("Users").child(userId);

        // Assign to the xml elements and init the variables
        sportSpinner = (Spinner) findViewById(R.id.createProfSportSpinner);
        levelSpinner = (Spinner)findViewById(R.id.createProfLevelSpinner);

        descEdit = (EditText)findViewById(R.id.createProfDescEdit);

        createProfButton = (Button)findViewById(R.id.createProfButton);

        sportSpinnerArray = MainActivity.createSportArray();
        levelSpinnerArray = new ArrayList<String>();
        levelSpinnerArray.add("Beginner");
        levelSpinnerArray.add("Intermediate");
        levelSpinnerArray.add("Expert");

        sportAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sportSpinnerArray);
        levelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, levelSpinnerArray);

        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sportSpinner.setAdapter(sportAdapter);
        levelSpinner.setAdapter(levelAdapter);

        // Creates the entries in the FireBase and start the MainActivity.
        createProfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child("Sport").setValue(sportSpinner.getSelectedItem().toString());
                ref.child("Level").setValue(levelSpinner.getSelectedItem().toString());
                ref.child("Description").setValue(descEdit.getText().toString());

                startActivity(MainActivity.createNewIntent(CreateProfileSecondActivity.this, MainActivity.class));
            }
        });
    }
}
