package mprog.nl.programmeerproject.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

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

public class SearchSchemeActivity extends AppCompatActivity implements View.OnClickListener {

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

    Spinner categorySpinner;
    Spinner firstKeySpinner;
    Spinner secondKeySpinner;
    Spinner thirdKeySpinner;

    ListView searchResultsList;

    ArrayList<String> categorySpinnerArray;
    ArrayList<String> keyArray;
    ArrayList<String> userInputArray;
    ArrayList<String> searchResults;

    ArrayAdapter<String> categoryAdapter;
    ArrayAdapter<String> keyAdapter;
    ArrayAdapter<String> optionalKeyAdapter;
    ArrayAdapter<String> searchResultsAdapter;

    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_scheme);

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
        searchButton = (Button)findViewById(R.id.searchSchemeSearchButton);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);
        schemeButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);

        categorySpinner = (Spinner)findViewById(R.id.searchSchemeCatSpinner);
        firstKeySpinner = (Spinner)findViewById(R.id.searchSchemeFirstSpinner);
        secondKeySpinner = (Spinner)findViewById(R.id.searchSchemeSecondSpinner);
        thirdKeySpinner = (Spinner)findViewById(R.id.searchSchemeThirdSpinner);

        searchResultsList = (ListView)findViewById(R.id.searchSchemeList);

        categorySpinnerArray = MainActivity.createSportArray();
        keyArray = MainActivity.createKeyArray();
        searchResults = new ArrayList<String>();

        categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorySpinnerArray);
        keyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, keyArray);
        keyArray.add("");
        optionalKeyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, keyArray);
        searchResultsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchResults);

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        keyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(categoryAdapter);
        firstKeySpinner.setAdapter(keyAdapter);
        secondKeySpinner.setAdapter(keyAdapter);
        thirdKeySpinner.setAdapter(keyAdapter);
        searchResultsList.setAdapter(searchResultsAdapter);

        secondKeySpinner.setSelection(optionalKeyAdapter.getPosition(""));
        thirdKeySpinner.setSelection(optionalKeyAdapter.getPosition(""));

        category = categorySpinner.getSelectedItem().toString();

        searchResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = MainActivity.createNewIntent(SearchSchemeActivity.this, SpecificSchemeActivity.class);
                intent.putExtra("Title", ((TextView) view).getText());
                intent.putExtra("Category", category);
                startActivity(intent);
            }
        });
    }

    Boolean checkKey(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (userInputArray.contains(entry.getKey())) {
                return true;
            }
        }
        return false;
    }

    ArrayList<String> fillArrayUserInput() {
        ArrayList<String> array = new ArrayList<String>();
        array.add(firstKeySpinner.getSelectedItem().toString());
        array.add(secondKeySpinner.getSelectedItem().toString());
        array.add(thirdKeySpinner.getSelectedItem().toString());
        return array;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homeButton:
                startActivity(MainActivity.createNewIntent(SearchSchemeActivity.this, MainActivity.class));
                break;
            case R.id.findButton:
                startActivity(MainActivity.createNewIntent(SearchSchemeActivity.this, FindUserActivity.class));
                break;
            case R.id.chatButton:
                startActivity(MainActivity.createNewIntent(SearchSchemeActivity.this, ChatActvity.class));
                break;
            case R.id.schemeButton:
                startActivity(MainActivity.createNewIntent(SearchSchemeActivity.this, SchemeActivity.class));
                break;
            case R.id.searchSchemeSearchButton:
                category = categorySpinner.getSelectedItem().toString();
                searchResults.clear();
                userInputArray = fillArrayUserInput();
                ref.child(categorySpinner.getSelectedItem().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            map = (HashMap<String, Object>) postSnapshot.getValue();
                            map = (HashMap<String, Object>) map.get("Keywords");
                            if (checkKey(map)) {
                                searchResults.add(postSnapshot.getKey());
                            }
                        }
                        searchResultsAdapter.notifyDataSetChanged();
                        userInputArray.clear();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
        }
    }
}
