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
    protected EditText ageEdit;

    protected Spinner genderSpinner;

    protected List<String> genderSpinnerArray;

    protected ArrayAdapter<String> genderAdapter;

    protected Button createProfNextButton;

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
        ageEdit = (EditText)findViewById(R.id.createProfAgeEdit);

        genderSpinner = (Spinner) findViewById(R.id.createProfGenderSpinner);

        genderSpinnerArray = new ArrayList<String>();
        genderSpinnerArray.add("Male");
        genderSpinnerArray.add("Female");

        genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genderSpinnerArray);

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genderSpinner.setAdapter(genderAdapter);

        createProfNextButton = (Button)findViewById(R.id.createProfNextButton);

        createProfNextButton.setOnClickListener(new View.OnClickListener() {
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
                    MainActivity.createAlert("Please fill in every field", CreateProfileActivity.this);
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
                            startActivity(MainActivity.createNewIntent(CreateProfileActivity.this, CreateProfileSecondActivity.class));
                        }
                        else {
                            MainActivity.createAlert("Your adress can't be found, please change your adress.", CreateProfileActivity.this).show();
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
