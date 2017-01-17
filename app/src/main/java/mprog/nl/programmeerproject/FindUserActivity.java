package mprog.nl.programmeerproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class FindUserActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId;
    DatabaseReference databaseRef;
    DatabaseReference ref;

    EditText radiusEdit;

    Button searchButton;

    String userSport;
    String userLevel;
    String userLat;
    String userLong;
    String foundUserId;
    String radius;
    String foundUserLat;
    String foundUserLong;

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

        searchButton = (Button) findViewById(R.id.findUserSearchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radius = radiusEdit.getText().toString().trim();
                if (radius.isEmpty()) {
                    MainActivity.createAlert("Please fill in a radius", FindUserActivity.this).show();
                } else {
                    findUser();
                }
            }
        });

        t = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = MainActivity.createNewIntent(FindUserActivity.this, SpecificUserActivity.class);
                intent.putExtra("foundUserId", foundUserId);
                startActivity(intent);
            }
        });

        ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    switch (postSnapshot.getKey()) {
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
                            foundUserId = postSnapshot.getKey();
                            t.run();
                            break;
                        }
                    }
                }
                MainActivity.createAlert("No users found, please adjust the radius", FindUserActivity.this).show();
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

        foundUserLat = map.get("Location").toString().split(",")[0].trim();
        foundUserLong = map.get("Location").toString().split(",")[1].trim();
        double foundDistance = distance(Double.parseDouble(userLat), Double.parseDouble(foundUserLat),
                Double.parseDouble(userLong), Double.parseDouble(foundUserLong),
                0.0, 0.0);

        if (!map.get("Sport").equals(userSport) || !map.get("Level").equals(userLevel) || Double.parseDouble(radius) < foundDistance / 1000) {
            validUser = false;
        }
        if (validUser) {
            for (Map.Entry<String, Object> entry : newMap.entrySet()) {
                Log.d("Test", "OverHere" + entry.getKey());
                if (entry.getKey().equals(userId)) {
                    validUser = false;
                }
            }
        }
        Log.d("Test", "" + validUser);

        return validUser;
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

}


