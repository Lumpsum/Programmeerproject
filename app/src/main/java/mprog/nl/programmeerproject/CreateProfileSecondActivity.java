package mprog.nl.programmeerproject;

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

public class CreateProfileSecondActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseRef;
    private String userId;
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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        ref = databaseRef.child("Users").child(userId);

        sportSpinner = (Spinner) findViewById(R.id.createProfSportSpinner);
        levelSpinner = (Spinner)findViewById(R.id.createProfLevelSpinner);

        descEdit = (EditText)findViewById(R.id.createProfDescEdit);

        createProfButton = (Button)findViewById(R.id.createProfButton);

        sportSpinnerArray = new ArrayList<String>();
        sportSpinnerArray.add("Fitness");
        sportSpinnerArray.add("Running");
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
