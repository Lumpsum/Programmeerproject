package mprog.nl.programmeerproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SpecificUserActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId;
    DatabaseReference databaseRef;
    DatabaseReference ref;

    TextView firstText;
    TextView lastText;
    TextView genderText;
    TextView ageText;
    TextView sportText;
    TextView levelText;
    TextView descText;

    Button searchButton;
    Button addButton;
    Button homeButton;
    Button findButton;

    String foundUserId;

    ArrayList<String> foundUserIds;

    int selector;
    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_user);

        Bundle bundle = this.getIntent().getExtras();
        foundUserIds = bundle.getStringArrayList("foundUserIds");
        selector = this.getIntent().getIntExtra("selector", 0);
        foundUserId = foundUserIds.get(selector);
        selector = selector + 1;
        size = foundUserIds.size();


        // Assign firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        ref = databaseRef.child("Users");

        firstText = (TextView)findViewById(R.id.specUserFirstText);
        lastText = (TextView)findViewById(R.id.specUserLastText);
        genderText = (TextView)findViewById(R.id.specUserGenderText);
        ageText = (TextView)findViewById(R.id.specUserAgeText);
        sportText = (TextView)findViewById(R.id.specUserSportText);
        levelText = (TextView)findViewById(R.id.specUserLevelText);
        descText = (TextView)findViewById(R.id.specUserDescText);

        searchButton = (Button)findViewById(R.id.specUserSearchButton);
        addButton = (Button)findViewById(R.id.specUserAddButton);
        homeButton = (Button)findViewById(R.id.homeButton);
        findButton = (Button)findViewById(R.id.findButton);

        searchButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);

        ref.child(foundUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    switch (postSnapshot.getKey()) {
                        case "FirstName":
                            firstText.setText(firstText.getText() + ": " + postSnapshot.getValue());
                            break;
                        case "LastName":
                            lastText.setText(lastText.getText() + ": " + postSnapshot.getValue());
                            break;
                        case "Gender":
                            genderText.setText(genderText.getText().toString() + postSnapshot.getValue());
                            break;
                        case "Age":
                            ageText.setText(ageText.getText() + ": " + postSnapshot.getValue());
                            break;
                        case "Sport":
                            sportText.setText(sportText.getText() + ": " + postSnapshot.getValue());
                            break;
                        case "Level":
                            levelText.setText(levelText.getText() + " " + postSnapshot.getValue());
                            break;
                        case "Description":
                            descText.setText(descText.getText().toString() + postSnapshot.getValue());
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.specUserSearchButton:
                //ref.child(userId).child("RefusedUsers").child(foundUserId).setValue(foundUserId);
                //ref.child(foundUserId).child("RefusedUsers").child(userId).setValue(userId);
                if (selector < size) {
                    Intent newIntent = MainActivity.createNewIntent(SpecificUserActivity.this, SpecificUserActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("foundUserIds", foundUserIds);
                    newIntent.putExtras(bundle);
                    newIntent.putExtra("selector", selector);
                    startActivity(newIntent);
                }
                else {
                    startActivity(MainActivity.createNewIntent(SpecificUserActivity.this, FindUserActivity.class));
                }
                break;
            case R.id.specUserAddButton:
                ref.child(userId).child("RefusedUsers").child(foundUserId).setValue(foundUserId);
                ref.child(foundUserId).child("RefusedUsers").child(userId).setValue(userId);
                ref.child(foundUserId).child("UserRequests").child(userId).setValue(userId);
                startActivity(MainActivity.createNewIntent(SpecificUserActivity.this, MainActivity.class));
                break;
            case R.id.homeButton:
                startActivity(MainActivity.createNewIntent(SpecificUserActivity.this, MainActivity.class));
            case R.id.findButton:
                startActivity(MainActivity.createNewIntent(SpecificUserActivity.this, FindUserActivity.class));
                break;
        }
    }
}
