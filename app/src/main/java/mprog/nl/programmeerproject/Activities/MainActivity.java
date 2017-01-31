package mprog.nl.programmeerproject.Activities;

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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mprog.nl.programmeerproject.Classes.User;
import mprog.nl.programmeerproject.R;
import mprog.nl.programmeerproject.Classes.UserReqestItem;
import mprog.nl.programmeerproject.Adapters.UserRequestAdapter;

/**
 * Main Activity that acts as the main hub of the app.
 * Makes the user able to log out, edit his profile, accept and
 * refuse other user request and see your own schemes.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Init variables
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId;
    DatabaseReference databaseRef;
    DatabaseReference ref;

    ImageButton logOutButton;
    ImageButton homeButton;
    ImageButton findButton;
    ImageButton chatButton;
    ImageButton schemeButton;
    ImageButton editProfileButton;

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
    ListView schemeList;

    ArrayList<UserReqestItem> userRequestArray;
    ArrayList<String> schemeArray;

    UserRequestAdapter userRequestAdapter;
    ArrayAdapter schemeAdapter;

    Map<String, String> userMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        ref = databaseRef.child("Users");

        // Check whether the user is logged in, if not start log in activity.
        if (firebaseUser == null) {
            startActivity(createNewIntent(this, LogInActivity.class));
        }
        else {
            userId = firebaseUser.getUid();

            // Assign to the xml elements and init the variables
            assignButtons();
            assignTextViews();
            assignListViews();

            userMap = new HashMap<>();

            new ShowcaseView.Builder(MainActivity.this)
                    .setTarget(new ViewTarget(R.id.mainRequestText, MainActivity.this))
                    .setContentTitle("Tutorial")
                    .withNewStyleShowcase()
                    .setContentText("Your user requests show up here. Long click to decline requests and click once in order to accept the request. " +
                            "In the List below your schemes show up, which you can delete via a long click as well.")
                    .hideOnTouchOutside()
                    .build();

            // Retrieve the user values and set the text accordingly.
            // Furthermore puts that information inside a hashmap to use for the edit profile
            // and reduce the loading of the whole app.
            ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    setUserInfo(user);

                    // Makes the edit profile button only visible if the information is loaded
                    // in order to prevent desyncs.
                    editProfileButton.setVisibility(View.VISIBLE);
                    schemeAdapter.notifyDataSetChanged();

                    // Fills the listview with user requests.
                    if (user.UserRequests != null) {
                        setUserRequests(user);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // Refuses a user request on long click and removes the entry of from the firebase.
            userRequestList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView dataText = (TextView)view.findViewById(R.id.dataText);
                    String newUserId = dataText.getText().toString();
                    ref.child(userId).child("UserRequests").child(newUserId).removeValue();
                    userRequestArray.remove(position);
                    userRequestAdapter.notifyDataSetChanged();
                    createToast(MainActivity.this, "User refused").show();
                    return true;
                }
            });

            // Accepts the user request on single click and adds a chat.
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
                    createToast(MainActivity.this, "User added.").show();
                }
            });

            // Removes a scheme of your own from the firebase.
            schemeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    String listItem = ((TextView)view).getText().toString();
                    String category = listItem.split(":")[0];
                    String title = listItem.split(":")[1].trim();
                    databaseRef.child("Users").child(userId).child("Schemes").child(category).child(title).removeValue();
                    databaseRef.child("Schemes").child(category).child(title).removeValue();
                    schemeArray.remove(listItem);
                    createToast(MainActivity.this, "Scheme removed succesfully.").show();
                    return true;
                }
            });

            // Starts a new activity of the scheme you selected.
            schemeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String listItem = ((TextView)view).getText().toString();
                    String category = listItem.split(":")[0];
                    String title = listItem.split(":")[1].trim();
                    Intent intent = MainActivity.createNewIntent(MainActivity.this, SpecificSchemeActivity.class);
                    intent.putExtra("Title", title);
                    intent.putExtra("Category", category);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * Fills the user request listview with the information of the user that wants to chat.
     *
     * @param data the id of the user that wants to match.
     */
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

    /**
     * Basic method that create a alert dialog with the given information.
     *
     * @param error The caused error text.
     * @param context The context of the activity that calls this method.
     * @return Returns a alert dialog, which can be shown.
     */
    public static AlertDialog createAlert(String error, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(error)
                .setTitle("Error")
                .setPositiveButton(android.R.string.ok,null);
        AlertDialog dialog = builder.create();
        return dialog;
    }

    /**
     * Method to create a Toast with a custom message, which gets displayedfor a short time.
     *
     * @param context Context of the activity that calls the method.
     * @param message Custom message to give the user more information.
     * @return Returns the created toast with the message.
     */
    public static Toast createToast(Context context, String message) {
        return Toast.makeText(context, message, Toast.LENGTH_SHORT);
    }

    /**
     * Creates a new intent based on the parameters.
     * Also clears the activity task completely.
     *
     * @param context Context of the activity that calls the method.
     * @param newClass The new activity that should be called.
     * @return Returns the created intent.
     */
    public static Intent createNewIntent(Context context, Class newClass) {
        Intent intent = new Intent(context, newClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    /**
     * Signs out the current user.
     *
     * @param context Context of the activity that calls the function
     * @param firebaseAuth Variables that contains the information of the current user.
     * @return Returns an intent that signs out the user.
     */
    public static Intent signOut(Context context, FirebaseAuth firebaseAuth) {
        firebaseAuth.signOut();
        return (createNewIntent(context, LogInActivity.class));
    }

    /**
     * Fills an array with basic sports.
     *
     * @return Returns an array containing the sport options.
     */
    static ArrayList<String> createSportArray() {
        ArrayList<String> array = new ArrayList<>();
        array.add("Fitness");
        array.add("Running");
        return array;
    }

    /**
     * Create an array with the different sport levels.
     *
     * @return Returns the created array.
     */
    static ArrayList<String> createLevelArray() {
        ArrayList<String> array = new ArrayList<>();
        array.add("Beginner");
        array.add("Intermediate");
        array.add("Expert");
        return array;
    }

    /**
     * Creates an array with the gender options.
     *
     * @return Returns the created array.
     */
    static ArrayList<String> createGenderArray() {
        ArrayList<String> array = new ArrayList<>();
        array.add("Male");
        array.add("Female");
        return array;
    }

    /**
     * Method that contains all the possible keywords and fills an array with them.
     *
     * @return Returns an array with all the possible keywords.
     */
    static ArrayList<String> createKeyArray() {
        ArrayList<String> array = new ArrayList<>();
        array.add("Strength");
        array.add("Endurance");
        array.add("Cardio");
        array.add("Marathon");
        array.add("Weightloss");
        array.add("Sprint");
        array.add("High Intensity");
        array.add("High Volume");
        array.add("Strict");
        array.add("Beginner");
        array.add("Intermediate");
        array.add("Expert");
        array.add("Explosive");
        return array;
    }

    // Basic button handler for the bottom menu and the other buttons.
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
            case R.id.schemeButton:
                startActivity(MainActivity.createNewIntent(MainActivity.this, SchemeActivity.class));
                break;
            case R.id.mainLogOutButton:
                startActivity(signOut(MainActivity.this, firebaseAuth));
                break;
            case R.id.mainEditProfButton:
                Intent intent = createNewIntent(MainActivity.this, EditProfileActivity.class);
                intent.putExtra("userMap", (Serializable) userMap);
                startActivity(intent);
                break;
        }
    }

    /**
     * Assign all the buttons of the layout.
     */
    void assignButtons() {
        logOutButton = (ImageButton) findViewById(R.id.mainLogOutButton);
        homeButton = (ImageButton)findViewById(R.id.homeButton);
        findButton = (ImageButton)findViewById(R.id.findButton);
        chatButton = (ImageButton)findViewById(R.id.chatButton);
        schemeButton = (ImageButton)findViewById(R.id.schemeButton);
        editProfileButton = (ImageButton)findViewById(R.id.mainEditProfButton);
        logOutButton.setOnClickListener(this);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);
        schemeButton.setOnClickListener(this);
        editProfileButton.setOnClickListener(this);
    }

    /**
     * Assign the textviews to the xml elements.
     */
    void assignTextViews() {
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
    }

    /**
     * Assign the listviews with adapters and the xml elements.
     */
    void assignListViews() {
        userRequestList = (ListView)findViewById(R.id.mainRequestList);
        schemeList = (ListView)findViewById(R.id.mainOwnSchemeList);

        userRequestArray = new ArrayList<UserReqestItem>();
        schemeArray = new ArrayList<String>();

        userRequestAdapter = new UserRequestAdapter(MainActivity.this, userRequestArray);
        schemeAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, schemeArray);

        userRequestList.setAdapter(userRequestAdapter);
        schemeList.setAdapter(schemeAdapter);
    }

    /**
     * Set the text of the mainprofile to the information of the user.
     *
     * @param u User class that contains the information.
     */
    void setUserInfo(User u) {
        welcomeText.setText(welcomeText.getText() + " " + u.FirstName);
        firstNameText.setText(firstNameText.getText() + ": " + u.FirstName);
        userMap.put("FirstName", u.FirstName);

        lastNameText.setText(lastNameText.getText() + ": " + u.LastName);
        userMap.put("LastName", u.LastName);

        genderText.setText(genderText.getText().toString() + u.Gender);
        userMap.put("Gender", u.Gender);

        ageText.setText(ageText.getText() + ": " + u.Age);
        userMap.put("Age", u.Age);

        streetText.setText(streetText.getText() + ": " + u.Street);
        userMap.put("Street", u.Street);

        numText.setText(numText.getText() + ": " + u.Number);
        userMap.put("Number", u.Number);

        cityText.setText(cityText.getText() + ": " + u.City);
        userMap.put("City", u.City);

        sportText.setText(sportText.getText() + " " + u.Sport);
        userMap.put("Sport", u.Sport);

        levelText.setText(levelText.getText() + " " + u.Level);
        userMap.put("Level", u.Level);

        descText.setText(descText.getText().toString() + u.Description);
        userMap.put("Description", u.Description);

        if (!(u.Schemes == null)) {
            for (Map.Entry<String, HashMap<String, String>> entry : u.Schemes.entrySet()) {
                for (Map.Entry<String, String> postEntry : entry.getValue().entrySet()) {
                    schemeArray.add(entry.getKey() + ": " + postEntry.getKey());
                }
            }
        }
    }

    /**
     * Retrieves the requests and puts the correct information in the listview.
     *
     * @param u User class that holds user requests.
     */
    void setUserRequests(User u) {
        for (Map.Entry<String, String> entry : u.UserRequests.entrySet()) {
            addUserRequestItems(entry.getKey());
        }
    }
}
