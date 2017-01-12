package mprog.nl.programmeerproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId;
    DatabaseReference databaseRef;

    Button logOutButton;

    TextView welcomeText;
    TextView firstNameText;
    TextView lastNameText;
    TextView streetText;
    TextView numText;
    TextView cityText;
    TextView sportText;
    TextView levelText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        if (firebaseUser == null) {
            startActivity(createNewIntent(this, LogInActivity.class));
        }
        else {
            logOutButton = (Button)findViewById(R.id.mainLogOutButton);

            welcomeText = (TextView)findViewById(R.id.mainWelcomeText);
            firstNameText = (TextView)findViewById(R.id.mainFirstText);
            lastNameText = (TextView)findViewById(R.id.mainLastText);
            streetText = (TextView)findViewById(R.id.mainStreetText);
            numText = (TextView)findViewById(R.id.mainNumberText);
            cityText = (TextView)findViewById(R.id.mainCityText);
            sportText = (TextView)findViewById(R.id.mainSportText);
            levelText = (TextView)findViewById(R.id.mainLevelText);

            DatabaseReference ref = databaseRef.child("Users").child(userId);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        switch (postSnapshot.getKey()) {
                            case "FirstName" :
                                welcomeText.setText(welcomeText.getText() + "   " + postSnapshot.getValue());
                                firstNameText.setText(firstNameText.getText() + ": " + postSnapshot.getValue());
                                break;
                            case "LastName" :
                                lastNameText.setText(lastNameText.getText() + ": " + postSnapshot.getValue());
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
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            logOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(signOut(MainActivity.this, firebaseAuth));
                }
            });

        }
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
}
