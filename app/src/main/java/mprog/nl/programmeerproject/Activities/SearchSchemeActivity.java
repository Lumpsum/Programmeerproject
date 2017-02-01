package mprog.nl.programmeerproject.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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

/**
 * Acitivty that allows the user to find schemes based on chosen set parameters.
 */
public class SearchSchemeActivity extends AppCompatActivity implements View.OnClickListener {

    // Init variables
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId;
    DatabaseReference databaseRef;
    DatabaseReference ref;

    ImageButton homeButton;
    ImageButton findButton;
    ImageButton chatButton;
    ImageButton schemeButton;
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

        // Assign to the xml elements and init the variables
        assignsButtons();

        fillAndAssignSpinners();

        searchResultsList = (ListView)findViewById(R.id.searchSchemeList);
        searchResults = new ArrayList<String>();
        searchResultsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchResults);

        setAdapters();

        // Sets the spinners to nothing to indicate optional.
        secondKeySpinner.setSelection(optionalKeyAdapter.getPosition(""));
        thirdKeySpinner.setSelection(optionalKeyAdapter.getPosition(""));

        category = categorySpinner.getSelectedItem().toString();

        // Starts a new activity with the found and clicked on result.
        setSearchResultsListClick();
    }

    /**
     * Checks whether the keywords match, if so returns true
     *
     * @param map Map with the given keywords to check with.
     * @return Returns a boolean whether the scheme's keywords correspond with the chosen keywords.
     */
    Boolean checkKey(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (userInputArray.contains(entry.getKey())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Fills an array with the chosen keywords.
     *
     * @return Returns an array with the keywords.
     */
    ArrayList<String> fillArrayUserInput() {
        ArrayList<String> array = new ArrayList<String>();
        array.add(firstKeySpinner.getSelectedItem().toString());
        array.add(secondKeySpinner.getSelectedItem().toString());
        array.add(thirdKeySpinner.getSelectedItem().toString());
        return array;
    }

    // Generic button handler for the bottom menu and all the other buttons.
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

            // Button to start searching for other schemes that mathc the given parameters.
            case R.id.searchSchemeSearchButton:
                category = categorySpinner.getSelectedItem().toString();
                searchResults.clear();
                userInputArray = fillArrayUserInput();

                // Loop through the chosen category.
                findMatchingResults();
                break;
        }
    }

    /**
     * Assigns the buttons to their xml elements.
     */
    void assignsButtons() {
        findButton = (ImageButton)findViewById(R.id.findButton);
        homeButton = (ImageButton)findViewById(R.id.homeButton);
        chatButton = (ImageButton)findViewById(R.id.chatButton);
        schemeButton = (ImageButton)findViewById(R.id.schemeButton);
        searchButton = (Button)findViewById(R.id.searchSchemeSearchButton);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);
        schemeButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
    }

    /**
     * Fills and assigns the spinners with their keywords or categories.
     */
    void fillAndAssignSpinners() {
        categorySpinner = (Spinner)findViewById(R.id.searchSchemeCatSpinner);
        firstKeySpinner = (Spinner)findViewById(R.id.searchSchemeFirstSpinner);
        secondKeySpinner = (Spinner)findViewById(R.id.searchSchemeSecondSpinner);
        thirdKeySpinner = (Spinner)findViewById(R.id.searchSchemeThirdSpinner);

        categorySpinnerArray = MainActivity.createSportArray();
        keyArray = MainActivity.createKeyArray();

        categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorySpinnerArray);
        keyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, keyArray);
        keyArray.add("");
        optionalKeyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, keyArray);

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        keyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    /**
     * Sets the adapters.
     */
    void setAdapters() {
        categorySpinner.setAdapter(categoryAdapter);
        firstKeySpinner.setAdapter(keyAdapter);
        secondKeySpinner.setAdapter(keyAdapter);
        thirdKeySpinner.setAdapter(keyAdapter);
        searchResultsList.setAdapter(searchResultsAdapter);
    }

    /**
     * Sets the on click for the results to start the scheme activity with the chosen scheme.
     */
    void setSearchResultsListClick() {
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

    /**
     * Finds results based on the given criteria and adds them to the listview.
     */
    void findMatchingResults() {
        ref.child(categorySpinner.getSelectedItem().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map = (HashMap<String, Object>) postSnapshot.getValue();
                    map = (HashMap<String, Object>) map.get("Keywords");

                    // Check the keywords
                    if (checkKey(map)) {
                        searchResults.add(postSnapshot.getKey());
                    }
                }
                MainActivity.createToast(SearchSchemeActivity.this, "Results found.").show();
                searchResultsAdapter.notifyDataSetChanged();
                userInputArray.clear();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
