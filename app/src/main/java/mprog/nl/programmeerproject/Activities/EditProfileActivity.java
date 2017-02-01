package mprog.nl.programmeerproject.Activities;

import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import mprog.nl.programmeerproject.Network.ASyncTask;
import mprog.nl.programmeerproject.R;

/**
 * Activity that fills the fields with the current information
 * and let's the user change that information.
 */
public class EditProfileActivity extends AppCompatActivity {

    // Init variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseRef;
    private String userId;

    protected EditText lastNameEdit;
    protected EditText firstNameEdit;
    protected EditText streetEdit;
    protected EditText numberEdit;
    protected EditText cityEdit;
    protected EditText ageEdit;
    protected EditText descEdit;

    protected Spinner genderSpinner;
    protected Spinner sportSpinner;
    protected Spinner levelSpinner;

    protected List<String> genderSpinnerArray;
    protected List<String> sportSpinnerArray;
    protected List<String> levelSpinnerArray;

    protected ArrayAdapter<String> genderAdapter;
    protected ArrayAdapter<String> sportAdapter;
    protected ArrayAdapter<String> levelAdapter;

    protected Button saveButton;

    protected Map<String, String> userMap;

    private String firstName;
    private String lastName;
    private String street;
    private String num;
    private String city;
    private String location;
    private String age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Retrieves information from the previous activity
        userMap = (Map<String, String>) getIntent().getSerializableExtra("userMap");

        // Init FireBase variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Assign to the xml elements and init the variables
        assignEditTexts();

        saveButton = (Button)findViewById(R.id.editProfSaveButton);

        // Retrieves the user's information from the given hashmap.
        setTextOfTextViews();

        assignSpinners();

        // Retrieves the new values and adjusts them inside FireBase.
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trimUserInput();

                // Checks whether every field is filled in.
                if (firstName.isEmpty() || lastName.isEmpty() || street.isEmpty() || num.isEmpty() || city.isEmpty() || age.isEmpty()) {
                    MainActivity.createAlert("Please fill in every field", EditProfileActivity.this);
                }
                else {
                    DatabaseReference ref = databaseRef.child("Users").child(userId);

                    AsyncTask<String, String, StringBuilder> aSyncTask = new ASyncTask();
                    tryResult(ref, aSyncTask);
                }
            }
        });
    }

    /**
     * Retrieves all the given input from the user and adjusts the values in FireBase.
     *
     * @param reference reference to the location of the specific profile.
     */
    void adjustFireBaseUserProfile(DatabaseReference reference) {
        reference.child("Location").setValue(location);
        reference.child("FirstName").setValue(firstName);
        reference.child("LastName").setValue(lastName);
        reference.child("Street").setValue(street);
        reference.child("City").setValue(city);
        reference.child("Number").setValue(num);
        reference.child("Gender").setValue(genderSpinner.getSelectedItem().toString());
        reference.child("Age").setValue(age);
        reference.child("Sport").setValue(sportSpinner.getSelectedItem().toString());
        reference.child("Level").setValue(levelSpinner.getSelectedItem().toString());
        reference.child("Description").setValue(descEdit.getText().toString());
    }

    /**
     * Assigns the spinners to their xml elements and fill them with data.
     */
    void assignSpinners() {
        genderSpinner = (Spinner) findViewById(R.id.editProfGenderSpinner);
        sportSpinner = (Spinner) findViewById(R.id.editProfSportSpinner);
        levelSpinner = (Spinner)findViewById(R.id.editProfLevelSpinner);

        genderSpinnerArray = MainActivity.createGenderArray();
        sportSpinnerArray = MainActivity.createSportArray();
        levelSpinnerArray = MainActivity.createLevelArray();

        genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderSpinnerArray);
        sportAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sportSpinnerArray);
        levelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, levelSpinnerArray);

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genderSpinner.setAdapter(genderAdapter);
        sportSpinner.setAdapter(sportAdapter);
        levelSpinner.setAdapter(levelAdapter);

        // Sets the current selection to the user values.
        genderSpinner.setSelection(genderAdapter.getPosition(userMap.get("Gender")));
        sportSpinner.setSelection(sportAdapter.getPosition(userMap.get("Sport")));
        levelSpinner.setSelection(levelAdapter.getPosition(userMap.get("Level")));
    }

    /**
     * Assigns the edittexts to their corresponding xml elements.
     */
    void assignEditTexts() {
        firstNameEdit = (EditText)findViewById(R.id.editProfFirstEdit);
        lastNameEdit = (EditText)findViewById(R.id.editProfLastEdit);
        streetEdit = (EditText)findViewById(R.id.editProfStreetEdit);
        numberEdit = (EditText)findViewById(R.id.editProfNumEdit);
        cityEdit = (EditText)findViewById(R.id.editProfCityEdit);
        ageEdit = (EditText)findViewById(R.id.editProfAgeEdit);
        descEdit = (EditText)findViewById(R.id.editProfDescEdit);
    }

    /**
     * Retrieves the text from the given map and sets the edittexts accordingly.
     */
    void setTextOfTextViews() {
        firstNameEdit.setText(userMap.get("FirstName"));
        lastNameEdit.setText(userMap.get("LastName"));
        streetEdit.setText(userMap.get("Street"));
        numberEdit.setText(userMap.get("Number"));
        cityEdit.setText(userMap.get("City"));
        ageEdit.setText(userMap.get("Age"));
        descEdit.setText(userMap.get("Description"));
    }

    /**
     * Takes the input and removes the trailing whitespace.
     */
    void trimUserInput() {
        firstName = firstNameEdit.getText().toString().trim();
        lastName = lastNameEdit.getText().toString().trim();
        street = streetEdit.getText().toString().trim();
        num = numberEdit.getText().toString().trim();
        city = cityEdit.getText().toString().trim();
        age = ageEdit.getText().toString().trim();
    }

    /**
     * Tries to perform an API call to Google Geocode API.
     *
     * @param ref Reference to database where eventually changes are made if everything is correct.
     * @param aSyncTask Asynctask that handles the API call asynchronously.
     */
    void tryResult(DatabaseReference ref, AsyncTask<String, String, StringBuilder> aSyncTask) {
        try {
            StringBuilder result;
            result = aSyncTask.execute(street, num, city).get();
            JSONObject jsonObject = new JSONObject(result.toString());
            String status = jsonObject.getString("status");

            // Checks whether the given location is valid.
            checkResult(status, jsonObject, ref);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes the API results and checks whether the location is valid, if so starts new activity.
     * Else gives an error.
     *
     * @param s The status of the API call.
     * @param object The full result of the API
     * @param ref Reference to the database where the changes are made if the call is valid.
     * @throws JSONException Exception for the jsonObject and it's calls.
     */
    void checkResult(String s, JSONObject object, DatabaseReference ref) throws JSONException {
        if (s.equals("OK")) {
            JSONArray loc = object.getJSONArray("results");
            JSONObject loc2 = loc.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            location = loc2.getString("lat") + "," + loc2.getString("lng");
            adjustFireBaseUserProfile(ref);
            MainActivity.createToast(EditProfileActivity.this, "Profile succesfully edited.").show();
            startActivity(MainActivity.createNewIntent(EditProfileActivity.this, MainActivity.class));
        }
        else {
            MainActivity.createAlert("Your adress can't be found, please change your adress.", EditProfileActivity.this).show();
        }
    }
}
