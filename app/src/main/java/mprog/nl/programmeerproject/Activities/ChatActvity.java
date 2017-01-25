package mprog.nl.programmeerproject.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

public class ChatActvity extends AppCompatActivity implements View.OnClickListener {

    // Init variables
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId;
    DatabaseReference databaseRef;
    DatabaseReference ref;

    Button homeButton;
    Button findButton;
    Button chatButton;
    Button schemeButton;

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

        homeButton = (Button)findViewById(R.id.homeButton);
        findButton = (Button)findViewById(R.id.findButton);
        chatButton = (Button)findViewById(R.id.chatButton);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);

        chatList = (ListView)findViewById(R.id.chatListView);

        userIds = new ArrayList<String>();
        chatArray = new ArrayList<ListItem>();

        chatAdapter = new ChatListAdapter(ChatActvity.this, chatArray);

        chatList.setAdapter(chatAdapter);

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
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    return true;
                }
            });

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
        }
    }
}
