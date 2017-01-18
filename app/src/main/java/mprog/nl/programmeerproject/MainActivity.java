package mprog.nl.programmeerproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
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

import static android.R.attr.data;
import static android.R.attr.start;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId;
    DatabaseReference databaseRef;
    DatabaseReference ref;

    Button logOutButton;
    Button homeButton;
    Button findButton;
    Button chatButton;
    Button schemeButton;

    TextView welcomeText;
    TextView firstNameText;
    TextView lastNameText;
    TextView ageText;
    TextView genderText;
    TextView streetText;
    TextView numText;
    TextView cityText;
    TextView sportText;
    TextView levelText;
    TextView descText;

    ListView userRequestList;

    ArrayList<UserReqestItem> userRequestArray;

    UserRequestAdapter userRequestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        ref = databaseRef.child("Users");

        if (firebaseUser == null) {
            startActivity(createNewIntent(this, LogInActivity.class));
        }
        else {
            userId = firebaseUser.getUid();

            logOutButton = (Button)findViewById(R.id.mainLogOutButton);
            homeButton = (Button)findViewById(R.id.homeButton);
            findButton = (Button)findViewById(R.id.findButton);
            chatButton = (Button)findViewById(R.id.chatButton);
            logOutButton.setOnClickListener(this);
            homeButton.setOnClickListener(this);
            findButton.setOnClickListener(this);
            chatButton.setOnClickListener(this);

            welcomeText = (TextView)findViewById(R.id.mainWelcomeText);
            firstNameText = (TextView)findViewById(R.id.mainFirstText);
            lastNameText = (TextView)findViewById(R.id.mainLastText);
            genderText = (TextView)findViewById(R.id.mainGenderText);
            ageText = (TextView)findViewById(R.id.mainAgeText);
            streetText = (TextView)findViewById(R.id.mainStreetText);
            numText = (TextView)findViewById(R.id.mainNumberText);
            cityText = (TextView)findViewById(R.id.mainCityText);
            sportText = (TextView)findViewById(R.id.mainSportText);
            levelText = (TextView)findViewById(R.id.mainLevelText);
            descText = (TextView)findViewById(R.id.mainDescText);

            userRequestList = (ListView)findViewById(R.id.mainRequestList);

            userRequestArray = new ArrayList<UserReqestItem>();

            userRequestAdapter = new UserRequestAdapter(MainActivity.this, userRequestArray);

            userRequestList.setAdapter(userRequestAdapter);

            ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        switch (postSnapshot.getKey()) {
                            case "FirstName" :
                                welcomeText.setText(welcomeText.getText() + " " + postSnapshot.getValue());
                                firstNameText.setText(firstNameText.getText() + ": " + postSnapshot.getValue());
                                break;
                            case "LastName" :
                                lastNameText.setText(lastNameText.getText() + ": " + postSnapshot.getValue());
                                break;
                            case "Gender":
                                genderText.setText(genderText.getText().toString() + postSnapshot.getValue());
                                break;
                            case "Age":
                                ageText.setText(ageText.getText() + ": " + postSnapshot.getValue());
                                break;
                            case "Street" :
                                streetText.setText(streetText.getText() + ": " + postSnapshot.getValue());
                                break;
                            case "Number" :
                                numText.setText(numText.getText() + ": " + postSnapshot.getValue());
                                break;
                            case "City" :
                                cityText.setText(cityText.getText() + ": " + postSnapshot.getValue());
                                break;
                            case "Sport" :
                                sportText.setText(sportText.getText() + " " + postSnapshot.getValue());
                                break;
                            case "Level" :
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

            ref.child(userId).child("UserRequests").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        addUserRequestItems(postSnapshot.getKey());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            userRequestList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView dataText = (TextView)view.findViewById(R.id.dataText);
                    String newUserId = dataText.getText().toString();
                    ref.child(userId).child("UserRequests").child(newUserId).removeValue();
                    userRequestArray.remove(position);
                    userRequestAdapter.notifyDataSetChanged();
                    return true;
                }
            });

            userRequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView dataText = (TextView)view.findViewById(R.id.dataText);
                    String newUserId = dataText.getText().toString();
                    ref.child(userId).child("UserRequests").child(newUserId).removeValue();
                    ref.child(userId).child("Chats").child(newUserId).setValue(newUserId);
                    ref.child(newUserId).child("Chats").child(userId).setValue(userId);
                    userRequestArray.remove(position);
                    userRequestAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    void addUserRequestItems(final String data) {
        ref.child(data).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("FirstName").getValue().toString() +
                        " " + dataSnapshot.child("LastName").getValue().toString();
                String gender = dataSnapshot.child("Gender").getValue().toString();
                String age = dataSnapshot.child("Age").getValue().toString();
                String description = dataSnapshot.child("Description").getValue().toString();
                UserReqestItem item = new UserReqestItem(userName, data, age, gender, description);
                userRequestArray.add(item);
                userRequestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static AlertDialog createAlert(String error, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(error)
                .setTitle("Error")
                .setPositiveButton(android.R.string.ok,null);
        AlertDialog dialog = builder.create();
        return dialog;
    }

    public static Intent createNewIntent(Context context, Class newClass) {
        Intent intent = new Intent(context, newClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    public static Intent signOut(Context context, FirebaseAuth firebaseAuth) {
        firebaseAuth.signOut();
        return (createNewIntent(context, LogInActivity.class));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homeButton:
                startActivity(MainActivity.createNewIntent(MainActivity.this, MainActivity.class));
                break;
            case R.id.findButton:
                startActivity(MainActivity.createNewIntent(MainActivity.this, FindUserActivity.class));
                break;
            case R.id.chatButton:
                startActivity(MainActivity.createNewIntent(MainActivity.this, ChatActvity.class));
                break;
            case R.id.mainLogOutButton:
                startActivity(signOut(MainActivity.this, firebaseAuth));
                break;
        }
    }
}
