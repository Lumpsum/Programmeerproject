package mprog.nl.programmeerproject.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

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

public class FindUserActivity extends AppCompatActivity implements View.OnClickListener {

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
    Button chatButton;
    Button homeButton;
    Button findButton;

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

        radiusEdit = (EditText) findViewById(R.id.findUserRadiusEdit);
        ageEdit = (EditText) findViewById(R.id.findUserAgeEdit);

        genderCheck = (CheckBox) findViewById(R.id.findUserGenderCheck);
        ageCheck = (CheckBox) findViewById(R.id.findUserAgeCheck);

        findUserButton = (Button) findViewById(R.id.findUserSearchButton);
        chatButton = (Button)findViewById(R.id.chatButton);
        homeButton = (Button)findViewById(R.id.homeButton);
        findButton = (Button)findViewById(R.id.findButton);

        chatButton.setOnClickListener(this);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);

        foundUserIds = new ArrayList<>();

        findUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radius = radiusEdit.getText().toString().trim();
                age = ageEdit.getText().toString().trim();
                if (radius.isEmpty()) {
                    MainActivity.createAlert("Please fill in a radius", FindUserActivity.this).show();
                } else if (ageCheck.isChecked() && age.isEmpty()) {
                    MainActivity.createAlert("Please fill in an age radius", FindUserActivity.this).show();
                } else {
                    findUser();
                }
            }
        });

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

        ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    switch (postSnapshot.getKey()) {
                        case "Age":
                            userAge = Integer.parseInt(postSnapshot.getValue().toString());
                            break;
                        case "Gender":
                            userGender = postSnapshot.getValue().toString();
                            break;
                        case "Sport":
                            userSport = postSnapshot.getValue().toString();
                            break;
                        case "Level":
                            userLevel = postSnapshot.getValue().toString();
                            break;
                        case "Location":
                            userLat = postSnapshot.getValue().toString().split(",")[0].trim();
                            userLong = postSnapshot.getValue().toString().split(",")[1].trim();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

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

    boolean checkConditions(Map<String, Object> map) {
        validUser = true;
        Map<String, Object> newMap = new HashMap<String, Object>();
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
            validUser = checkParameters(map);
            }
            if (validUser) {
                validUser = checkAdditionalParameters(map);
            }

        return validUser;
        }


    Boolean checkParameters(Map<String, Object> map) {
        double foundDistance = distance(Double.parseDouble(userLat), Double.parseDouble(foundUserLat),
                Double.parseDouble(userLong), Double.parseDouble(foundUserLong),
                0.0, 0.0);
        if (!map.get("Sport").equals(userSport) || !map.get("Level").equals(userLevel) || Double.parseDouble(radius) < foundDistance / 1000) {
            return false;
        }
        return true;
    }

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
        }
    }

}


