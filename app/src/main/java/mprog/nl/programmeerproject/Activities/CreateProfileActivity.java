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
import java.util.concurrent.ExecutionException;

import mprog.nl.programmeerproject.Network.ASyncTask;
import mprog.nl.programmeerproject.R;

/**
 * Activity that creates the fields for the profile creation.
 * Creates the entries for the personal information of the user.
 */
public class CreateProfileActivity extends AppCompatActivity {

    private DatabaseReference databaseRef;
    private String userId;

    protected EditText firstNameEdit;
    protected EditText lastNameEdit;
    protected EditText streetEdit;
    protected EditText numberEdit;
    protected EditText cityEdit;
    protected EditText ageEdit;

    protected Spinner genderSpinner;

    protected List<String> genderSpinnerArray;

    protected ArrayAdapter<String> genderAdapter;

    protected Button createProfNextButton;

    private String firstName;
    private String lastName;
    private String street;
    private String num;
    private String city;
    private String age;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        // Init Firebase variables
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Assign to the xml elements and init the variables
        assignEditTexts();
        assignSpinner();

        createProfNextButton = (Button)findViewById(R.id.createProfNextButton);

        // Checks whether the fields are filled in, whether the location is
        // correct and finally creates the database entries.
        createProfNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trimUserInput();

                // Checks whether every field is filled in.
                if (firstName.isEmpty() || lastName.isEmpty() || street.isEmpty() || num.isEmpty() || city.isEmpty() || age.isEmpty()) {
                    MainActivity.createAlert("Please fill in every field", CreateProfileActivity.this);
                }
                else {
                    DatabaseReference ref = databaseRef.child("Users").child(userId);

                    AsyncTask<String, String, StringBuilder> aSyncTask = new ASyncTask();
                    StringBuilder result;
                    try {

                        //Starts the asynctask with the call to the google api
                        result = aSyncTask.execute(street, num, city).get();
                        JSONObject jsonObject = new JSONObject(result.toString());
                        String status = jsonObject.getString("status");

                        // Checks whether the given adress returns a valid location
                        if (status.equals("OK")) {
                            JSONArray loc = jsonObject.getJSONArray("results");
                            JSONObject loc2 = loc.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                            location = loc2.getString("lat") + "," + loc2.getString("lng");
                            fillFireBaseWithPersonalInfo(ref);
                            MainActivity.createToast(CreateProfileActivity.this, "Second step of the profile creation completed.").show();
                            startActivity(MainActivity.createNewIntent(CreateProfileActivity.this, CreateProfileSecondActivity.class));
                        }
                        else {
                            MainActivity.createAlert("Your adress can't be found, please change your adress.", CreateProfileActivity.this).show();
                        }
                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Fills the firebase with the given values.
     *
     * @param reference Reference to where the users profiles are located.
     */
    void fillFireBaseWithPersonalInfo(DatabaseReference reference) {
        reference.child("Location").setValue(location);
        reference.child("FirstName").setValue(firstName);
        reference.child("LastName").setValue(lastName);
        reference.child("Street").setValue(street);
        reference.child("Number").setValue(num);
        reference.child("City").setValue(city);
        reference.child("Gender").setValue(genderSpinner.getSelectedItem().toString());
        reference.child("Age").setValue(age);
    }

    /**
     * Assigns the edittexts to their xml elements.
     */
    void assignEditTexts() {
        firstNameEdit = (EditText)findViewById(R.id.createProfFirstEdit);
        lastNameEdit = (EditText)findViewById(R.id.createProfLastEdit);
        streetEdit = (EditText)findViewById(R.id.createProfStreetEdit);
        numberEdit = (EditText)findViewById(R.id.createProfNumberEdit);
        cityEdit = (EditText)findViewById(R.id.createProfCityEdit);
        ageEdit = (EditText)findViewById(R.id.createProfAgeEdit);
    }

    /**
     * Assigns the spinner to the xml elements and fill it with data.
     */
    void assignSpinner() {
        genderSpinner = (Spinner) findViewById(R.id.createProfGenderSpinner);
        genderSpinnerArray = MainActivity.createGenderArray();
        genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderSpinnerArray);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
    }

    /**
     * Takes the userinput and trims the text.
     */
    void trimUserInput() {
        firstName = firstNameEdit.getText().toString().trim();
        lastName = lastNameEdit.getText().toString().trim();
        street = streetEdit.getText().toString().trim();
        city = cityEdit.getText().toString().trim();
        num = numberEdit.getText().toString().trim();
        age = ageEdit.getText().toString().trim();
    }
}
