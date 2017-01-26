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
        firstNameEdit = (EditText)findViewById(R.id.editProfFirstEdit);
        lastNameEdit = (EditText)findViewById(R.id.editProfLastEdit);
        streetEdit = (EditText)findViewById(R.id.editProfStreetEdit);
        numberEdit = (EditText)findViewById(R.id.editProfNumEdit);
        cityEdit = (EditText)findViewById(R.id.editProfCityEdit);
        ageEdit = (EditText)findViewById(R.id.editProfAgeEdit);
        descEdit = (EditText)findViewById(R.id.editProfDescEdit);

        genderSpinner = (Spinner) findViewById(R.id.editProfGenderSpinner);
        sportSpinner = (Spinner) findViewById(R.id.editProfSportSpinner);
        levelSpinner = (Spinner)findViewById(R.id.editProfLevelSpinner);

        saveButton = (Button)findViewById(R.id.editProfSaveButton);

        // Retrieves the user's information from the given hashmap.
        firstNameEdit.setText(userMap.get("FirstName"));
        lastNameEdit.setText(userMap.get("LastName"));
        streetEdit.setText(userMap.get("Street"));
        numberEdit.setText(userMap.get("Number"));
        cityEdit.setText(userMap.get("City"));
        ageEdit.setText(userMap.get("Age"));
        descEdit.setText(userMap.get("Description"));

        genderSpinnerArray = MainActivity.createGenderArray();
        sportSpinnerArray = MainActivity.createSportArray();
        levelSpinnerArray = MainActivity.createSportArray();

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

        // Retrieves the new values and adjusts them inside FireBase.
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = firstNameEdit.getText().toString().trim();
                lastName = lastNameEdit.getText().toString().trim();
                street = streetEdit.getText().toString().trim();
                num = numberEdit.getText().toString().trim();
                city = cityEdit.getText().toString().trim();
                age = ageEdit.getText().toString().trim();

                // Checks whether every field is filled in.
                if (firstName.isEmpty() || lastName.isEmpty() || street.isEmpty() || num.isEmpty() || city.isEmpty() || age.isEmpty()) {
                    MainActivity.createAlert("Please fill in every field", EditProfileActivity.this);
                }
                else {
                    DatabaseReference ref = databaseRef.child("Users").child(userId);

                    AsyncTask<String, String, StringBuilder> aSyncTask = new ASyncTask();
                    StringBuilder result;
                    try {
                        result = aSyncTask.execute(street, num, city).get();
                        JSONObject jsonObject = new JSONObject(result.toString());
                        String status = jsonObject.getString("status");

                        // Checks whether the given location is valid.
                        if (status.equals("OK")) {
                            JSONArray loc = jsonObject.getJSONArray("results");
                            JSONObject loc2 = loc.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                            location = loc2.getString("lat") + "," + loc2.getString("lng");
                            adjustFireBaseUserProfile(ref);
                            startActivity(MainActivity.createNewIntent(EditProfileActivity.this, MainActivity.class));
                        }
                        else {
                            MainActivity.createAlert("Your adress can't be found, please change your adress.", EditProfileActivity.this).show();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
}
