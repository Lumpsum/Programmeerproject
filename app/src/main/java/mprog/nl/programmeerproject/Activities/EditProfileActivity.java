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

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseRef;
    private String userId;
    private DatabaseReference ref;

    protected EditText firstNameEdit;
    protected EditText lastNameEdit;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userMap = (Map<String, String>) getIntent().getSerializableExtra("userMap");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();

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

        firstNameEdit.setText(userMap.get("FirstName"));
        lastNameEdit.setText(userMap.get("LastName"));
        streetEdit.setText(userMap.get("Street"));
        numberEdit.setText(userMap.get("Number"));
        cityEdit.setText(userMap.get("City"));
        ageEdit.setText(userMap.get("Age"));
        descEdit.setText(userMap.get("Description"));

        genderSpinnerArray = new ArrayList<String>();
        genderSpinnerArray.add("Male");
        genderSpinnerArray.add("Female");
        sportSpinnerArray = new ArrayList<String>();
        sportSpinnerArray.add("Fitness");
        sportSpinnerArray.add("Running");
        levelSpinnerArray = new ArrayList<String>();
        levelSpinnerArray.add("Beginner");
        levelSpinnerArray.add("Intermediate");
        levelSpinnerArray.add("Expert");

        genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderSpinnerArray);
        sportAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sportSpinnerArray);
        levelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, levelSpinnerArray);

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genderSpinner.setAdapter(genderAdapter);
        sportSpinner.setAdapter(sportAdapter);
        levelSpinner.setAdapter(levelAdapter);

        genderSpinner.setSelection(genderAdapter.getPosition(userMap.get("Gender")));
        sportSpinner.setSelection(sportAdapter.getPosition(userMap.get("Sport")));
        levelSpinner.setSelection(levelAdapter.getPosition(userMap.get("Level")));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEdit.getText().toString().trim();
                String lastName = lastNameEdit.getText().toString().trim();
                String street = streetEdit.getText().toString().trim();
                String num = numberEdit.getText().toString().trim();
                int houseNum = Integer.parseInt(num);
                String city = cityEdit.getText().toString().trim();
                String age = ageEdit.getText().toString().trim();

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
                        if (status.equals("OK")) {
                            JSONArray loc = jsonObject.getJSONArray("results");
                            JSONObject loc2 = loc.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                            String location = loc2.getString("lat") + "," + loc2.getString("lng");
                            ref.child("Location").setValue(location);
                            ref.child("FirstName").setValue(firstName);
                            ref.child("LastName").setValue(lastName);
                            ref.child("Street").setValue(street);
                            ref.child("Number").setValue(houseNum);
                            ref.child("City").setValue(city);
                            ref.child("Gender").setValue(genderSpinner.getSelectedItem().toString());
                            ref.child("Age").setValue(age);
                            ref.child("Sport").setValue(sportSpinner.getSelectedItem().toString());
                            ref.child("Level").setValue(levelSpinner.getSelectedItem().toString());
                            ref.child("Description").setValue(descEdit.getText().toString());
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
}
