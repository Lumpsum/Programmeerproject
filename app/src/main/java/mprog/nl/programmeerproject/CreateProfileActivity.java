package mprog.nl.programmeerproject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class CreateProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseRef;
    private String userId;

    protected EditText firstNameEdit;
    protected EditText lastNameEdit;
    protected EditText streetEdit;
    protected EditText numberEdit;
    protected EditText cityEdit;

    protected Spinner sportSpinner;
    protected Spinner levelSpinner;

    protected List<String> sportSpinnerArray;
    protected List<String> levelSpinnerArray;

    protected ArrayAdapter<String> sportAdapter;
    protected ArrayAdapter<String> levelAdapter;

    protected Button createProfButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        firstNameEdit = (EditText)findViewById(R.id.createProfFirstEdit);
        lastNameEdit = (EditText)findViewById(R.id.createProfLastEdit);
        streetEdit = (EditText)findViewById(R.id.createProfStreetEdit);
        numberEdit = (EditText)findViewById(R.id.createProfNumberEdit);
        cityEdit = (EditText)findViewById(R.id.createProfCityEdit);

        sportSpinner = (Spinner) findViewById(R.id.createProfSportSpinner);
        levelSpinner = (Spinner)findViewById(R.id.createProfLevelSpinner);

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

        createProfButton = (Button)findViewById(R.id.createProfButton);

        createProfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEdit.getText().toString().trim();
                String lastName = lastNameEdit.getText().toString().trim();
                String street = streetEdit.getText().toString().trim();
                String num = numberEdit.getText().toString().trim();
                int houseNum = Integer.parseInt(num);
                String city = cityEdit.getText().toString().trim();

                if (firstName.isEmpty() || lastName.isEmpty() || street.isEmpty() || num.isEmpty() || city.isEmpty()) {
                    MainActivity.createAlert("Please fill in every field", CreateProfileActivity.this);
                }
                else {
                    DatabaseReference ref = databaseRef.child("Users").child(userId);
                    ref.child("FirstName").setValue(firstName);
                    ref.child("LastName").setValue(lastName);
                    ref.child("Street").setValue(street);
                    ref.child("Number").setValue(houseNum);
                    ref.child("City").setValue(city);
                    ref.child("Sport").setValue(sportSpinner.getSelectedItem().toString());
                    ref.child("Level").setValue(levelSpinner.getSelectedItem().toString());

                    AsyncTask<String, String, StringBuilder> aSyncTask = new ASyncTask();
                    StringBuilder result;
                    try {
                        result = aSyncTask.execute(street, num, city).get();
                        JSONObject jsonObject = new JSONObject(result.toString());
                        JSONArray loc = jsonObject.getJSONArray("results");
                        JSONObject loc2 = loc.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                        String location = loc2.getString("lat") + "," + loc2.getString("lng");
                        ref.child("Location").setValue(location);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                startActivity(MainActivity.createNewIntent(CreateProfileActivity.this, MainActivity.class));
            }
        });
    }
}
