package mprog.nl.programmeerproject.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

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

import mprog.nl.programmeerproject.Classes.User;
import mprog.nl.programmeerproject.R;

/**
 * Activity that takes the user given parameters and matches with users within those parameters.
 * The parameters as of yet are age, gender and the range around your adress.
 */
public class FindUserActivity extends AppCompatActivity implements View.OnClickListener {

    // Init variables
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId;
    DatabaseReference databaseRef;
    DatabaseReference ref;

    EditText radiusEdit;
    EditText ageEdit;

    CheckBox genderCheck;
    CheckBox ageCheck;

    Button findUserButton;
    ImageButton chatButton;
    ImageButton homeButton;
    ImageButton findButton;
    ImageButton schemeButton;

    int userAge;

    String userGender;
    String userSport;
    String userLevel;
    String userLat;
    String userLong;
    String radius;
    String age;
    String foundUserLat;
    String foundUserLong;

    ArrayList<String> foundUserIds;

    Boolean validUser;

    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        // Assign firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        ref = databaseRef.child("Users");

        // Assign to the xml elements and init the variables
        radiusEdit = (EditText) findViewById(R.id.findUserRadiusEdit);
        ageEdit = (EditText) findViewById(R.id.findUserAgeEdit);

        genderCheck = (CheckBox) findViewById(R.id.findUserGenderCheck);
        ageCheck = (CheckBox) findViewById(R.id.findUserAgeCheck);

        assignButtons();

        foundUserIds = new ArrayList<>();

        // Thread that starts after the search is completed, which starts a new activity and passes
        // all the ids of the found users.
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = MainActivity.createNewIntent(FindUserActivity.this, SpecificUserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("foundUserIds", foundUserIds);
                intent.putExtras(bundle);
                intent.putExtra("selector", 0);
                startActivity(intent);
            }
        });

        // Retrieves the values of the current user to use in the parameter checks.
        ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assignValues(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Checks whether a user is a match and adds them to the array of found users.
     */
    void findUser() {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (!postSnapshot.getKey().equals(userId)) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map = (HashMap<String, Object>) postSnapshot.getValue();

                        if (checkConditions(map)) {
                            foundUserIds.add(postSnapshot.getKey());
                        }
                    }
                }
                // If no users are found, gives an error, else starts the thread.
                if (foundUserIds.isEmpty()) {
                    MainActivity.createAlert("No users found, please adjust the radius", FindUserActivity.this).show();
                }
                else {
                    t.run();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Method that checks all the parameters given by the user
     *
     * @param map Map which contains the user values
     * @return Returns a boolean with determines whether the user is a match or not.
     */
    boolean checkConditions(Map<String, Object> map) {
        validUser = true;
        Map<String, Object> newMap = new HashMap<String, Object>();

        // All the refused users so that you don't match with the same person over and over again.
        if (map.containsKey("RefusedUsers")) {
            newMap = (HashMap<String, Object>) map.get("RefusedUsers");
        }

        for (Map.Entry<String, Object> entry : newMap.entrySet()) {
            if (entry.getKey().equals(userId)) {
                validUser = false;
                break;
            }
        }
        foundUserLat = map.get("Location").toString().split(",")[0].trim();
        foundUserLong = map.get("Location").toString().split(",")[1].trim();
        if (validUser) {

            // Check the distance
            validUser = checkParameters(map);
            }

            // Check the optional parameters
            if (validUser) {
                validUser = checkAdditionalParameters(map);
            }

        return validUser;
        }

    /**
     * Checks the basic parameters of the user and the found users.
     *
     * @param map Map which conains the user values
     * @return Returns a boolean which determines whether the found users matches the parameters
     */
    Boolean checkParameters(Map<String, Object> map) {
        double foundDistance = distance(Double.parseDouble(userLat), Double.parseDouble(foundUserLat),
                Double.parseDouble(userLong), Double.parseDouble(foundUserLong),
                0.0, 0.0);
        if (!map.get("Sport").equals(userSport) || !map.get("Level").equals(userLevel) || Double.parseDouble(radius) < foundDistance / 1000) {
            return false;
        }
        return true;
    }

    /**
     * Checks whether optional parameters should be checked and checks them if so.
     *
     * @param map Map which contains the user values.
     * @return Returns a boolean which determines whether the found user matches the given
     * parameters.
     */
    Boolean checkAdditionalParameters(Map<String, Object> map) {
        if (genderCheck.isChecked()) {
            if (!map.get("Gender").equals(userGender)) {
                return false;
            }
        }
        if (ageCheck.isChecked()) {
            int ageNum = Integer.parseInt(age);
            int foundUserAge = Integer.parseInt(map.get("Age").toString());
            if (foundUserAge < (userAge - ageNum ) || foundUserAge > (userAge + ageNum)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Function that calculates the distances between two long, lat points.
     * Taken from: Taken from: http://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
     *
     * @param lat1 Latitude of the first point.
     * @param lat2 Latitude of the second point.
     * @param lon1 Longitude of the first point.
     * @param lon2 Longitude of the second point.
     * @param el1 Optional parameter that takes height difference into account.
     * @param el2 Optional parameter that takes height difference into account.
     * @return Returns a float which is the distance between the points.
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    // Standard button handler for the bottom menu buttons and other buttons
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homeButton:
                startActivity(MainActivity.createNewIntent(FindUserActivity.this, MainActivity.class));
                break;
            case R.id.findButton:
                startActivity(MainActivity.createNewIntent(FindUserActivity.this, FindUserActivity.class));
                break;
            case R.id.chatButton:
                startActivity(MainActivity.createNewIntent(FindUserActivity.this, ChatActvity.class));
                break;
            case R.id.schemeButton:
                startActivity(MainActivity.createNewIntent(FindUserActivity.this, SchemeActivity.class));
                break;

            // Button that starts the flow of finding users and checking parameters.
            case R.id.findUserSearchButton:
                radius = radiusEdit.getText().toString().trim();
                age = ageEdit.getText().toString().trim();
                if (radius.isEmpty()) {
                    MainActivity.createAlert("Please fill in a radius", FindUserActivity.this).show();
                } else if (ageCheck.isChecked() && age.isEmpty()) {
                    MainActivity.createAlert("Please fill in an age radius", FindUserActivity.this).show();
                } else {
                    MainActivity.createToast(FindUserActivity.this, "Searching for users...").show();
                    findUser();
                }
        }
    }

    /**
     * Assigns the buttons the xml elements.
     */
    void assignButtons() {
        findUserButton = (Button) findViewById(R.id.findUserSearchButton);
        chatButton = (ImageButton)findViewById(R.id.chatButton);
        homeButton = (ImageButton)findViewById(R.id.homeButton);
        findButton = (ImageButton)findViewById(R.id.findButton);
        schemeButton = (ImageButton)findViewById(R.id.schemeButton);

        findUserButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);
        schemeButton.setOnClickListener(this);
    }

    /**
     * Assigns user values to their respective variables.
     *
     * @param u Userclass that contains the users data.
     */
    void assignValues(User u) {
        userAge = Integer.parseInt(u.Age);
        userGender = u.Gender;
        userSport = u.Sport;
        userLevel = u.Level;
        userLat = u.Location.split(",")[0].trim();
        userLong = u.Location.split(",")[1].trim();
    }
}


