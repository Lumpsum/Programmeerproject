package mprog.nl.programmeerproject.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import mprog.nl.programmeerproject.R;

public class SchemeActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId;
    DatabaseReference databaseRef;
    DatabaseReference ref;

    Button homeButton;
    Button findButton;
    Button chatButton;
    Button schemeButton;
    Button searchButton;
    Button createButton;

    ListView fitnessList;
    ListView runningList;

    ArrayAdapter fitnessAdapter;
    ArrayAdapter runningAdapter;

    ArrayList<String> fitnessArray;
    ArrayList<String> runningArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme);

        // Assign firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        ref = databaseRef.child("Schemes");

        homeButton = (Button)findViewById(R.id.homeButton);
        findButton = (Button)findViewById(R.id.findButton);
        chatButton = (Button)findViewById(R.id.chatButton);
        schemeButton = (Button)findViewById(R.id.schemeButton);
        searchButton = (Button)findViewById(R.id.schemeSearchButton);
        createButton = (Button)findViewById(R.id.schemeCreateButton);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);
        schemeButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        createButton.setOnClickListener(this);

        fitnessList = (ListView)findViewById(R.id.schemeTopFitList);
        runningList = (ListView)findViewById(R.id.schemeTopRunList);

        fitnessArray = new ArrayList<>();
        runningArray = new ArrayList<>();

        fitnessAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fitnessArray);
        runningAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, runningArray);

        fitnessList.setAdapter(fitnessAdapter);
        runningList.setAdapter(runningAdapter);

        fillListViewBasedOnRating(ref.child("Fitness"), fitnessArray, fitnessAdapter);
        fillListViewBasedOnRating(ref.child("Running"), runningArray, runningAdapter);

        fitnessList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = MainActivity.createNewIntent(SchemeActivity.this, SpecificSchemeActivity.class);
                intent.putExtra("Title", ((TextView)view).getText());
                intent.putExtra("Category", "Fitness");
                startActivity(intent);
            }
        });
        runningList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = MainActivity.createNewIntent(SchemeActivity.this, SpecificSchemeActivity.class);
                intent.putExtra("Title", ((TextView)view).getText().toString());
                intent.putExtra("Category", "Running");
                startActivity(intent);
            }
        });
    }

    void fillListViewBasedOnRating(DatabaseReference reference, final ArrayList<String> array, ArrayAdapter<String> adapter) {
        Query queryRef = reference.orderByChild("Rating").limitToLast(5);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                array.add(0, dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homeButton:
                startActivity(MainActivity.createNewIntent(SchemeActivity.this, MainActivity.class));
                break;
            case R.id.findButton:
                startActivity(MainActivity.createNewIntent(SchemeActivity.this, FindUserActivity.class));
                break;
            case R.id.chatButton:
                startActivity(MainActivity.createNewIntent(SchemeActivity.this, ChatActvity.class));
                break;
            case R.id.schemeButton:
                startActivity(MainActivity.createNewIntent(SchemeActivity.this, SchemeActivity.class));
                break;
            case R.id.schemeSearchButton:
                startActivity(MainActivity.createNewIntent(SchemeActivity.this, SearchSchemeActivity.class));
                break;
            case R.id.schemeCreateButton:
                startActivity(MainActivity.createNewIntent(SchemeActivity.this, CreateSchemeActivity.class));
                break;
        }
    }
}