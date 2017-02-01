package mprog.nl.programmeerproject.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import mprog.nl.programmeerproject.Adapters.ChatListAdapter;
import mprog.nl.programmeerproject.Classes.ListItem;
import mprog.nl.programmeerproject.R;

/**
 * Activity that holds all the chats of the user which are clickable in order for you
 * to navigate to the specific chat. Only shows the name of the person who you are chattign with.
 */
public class ChatActvity extends AppCompatActivity implements View.OnClickListener {

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

    ListView chatList;

    ArrayList<String> userIds;
    ArrayList<ListItem> chatArray;

    ChatListAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_actvity);

        // Assign firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        ref = databaseRef.child("Users");

        // Assign to the xml elements and init the variables
        assignButtons();

        chatList = (ListView)findViewById(R.id.chatListView);

        userIds = new ArrayList<String>();
        chatArray = new ArrayList<ListItem>();

        chatAdapter = new ChatListAdapter(ChatActvity.this, chatArray);

        chatList.setAdapter(chatAdapter);

        // Fill the listview with your chats
        fillChatListView();

        // Removes the chat from the database
        setChatLongClick();

        // Starts a new activity that contains the specific chat with the selected user
        setChatClick();
    }

    /**
     * Adds items to the listview that contain the information of the user.
     * Furtermore makes it so that you have multiple fiels per item in order to easily jump to
     * new activities.
     *
     * @param data The id of the user with whom you have a chat
     */
    void addListItems(final String data) {
        ref.child(data).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("FirstName").getValue().toString() +
                        " " + dataSnapshot.child("LastName").getValue().toString();
                chatArray.add(new ListItem(userName, data));
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Generic button handler that handles the bottom menu and all the other buttons
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homeButton:
                startActivity(MainActivity.createNewIntent(ChatActvity.this, MainActivity.class));
                break;
            case R.id.findButton:
                startActivity(MainActivity.createNewIntent(ChatActvity.this, FindUserActivity.class));
                break;
            case R.id.chatButton:
                startActivity(MainActivity.createNewIntent(ChatActvity.this, ChatActvity.class));
                break;
            case R.id.schemeButton:
                startActivity(MainActivity.createNewIntent(ChatActvity.this, SchemeActivity.class));
        }
    }

    /**
     * Deletes a chat with another user in the database and every trace of it.
     */
    public void setChatLongClick() {
        chatList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView dataText = (TextView) view.findViewById(R.id.dataText);
                final String data = dataText.getText().toString();
                chatArray.remove(position);
                chatAdapter.notifyDataSetChanged();
                ref.child(userId).child("Chats").child(data).removeValue();
                ref.child(data).child("Chats").child(userId).removeValue();
                databaseRef.child("Chats").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DatabaseReference tempRef;
                        if (dataSnapshot.hasChild(userId + "," + data)) {
                            tempRef = databaseRef.child("Chats").child(userId + "," + data);
                        } else {
                            tempRef = databaseRef.child("Chats").child(data + "," + userId);
                        }
                        tempRef.removeValue();
                        MainActivity.createToast(ChatActvity.this, "Chat succesfully deleted.").show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return true;
            }
        });
    }

    /**
     * Starts the chat activity with a specific other user.
     */
    void setChatClick() {
        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView dataText = (TextView) view.findViewById(R.id.dataText);
                TextView nameText = (TextView) view.findViewById(R.id.userNameText);
                Intent intent = MainActivity.createNewIntent(ChatActvity.this, SpecificChatActivity.class);
                intent.putExtra("otherUserData", (dataText.getText().toString()));
                intent.putExtra("otherUserName", (nameText.getText().toString()));
                startActivity(intent);
            }
        });
    }

    /**
     * Assign the buttons to their xml elements.
     */
    void assignButtons() {
        homeButton = (ImageButton)findViewById(R.id.homeButton);
        findButton = (ImageButton)findViewById(R.id.findButton);
        chatButton = (ImageButton)findViewById(R.id.chatButton);
        schemeButton = (ImageButton)findViewById(R.id.schemeButton);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);
        schemeButton.setOnClickListener(this);
    }

    /**
     * Fills the chat listview with your chats.
     */
    void fillChatListView() {
        ref.child(userId).child("Chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    addListItems(postSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
