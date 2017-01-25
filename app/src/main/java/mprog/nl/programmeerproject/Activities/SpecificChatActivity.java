package mprog.nl.programmeerproject.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import mprog.nl.programmeerproject.Classes.ChatMessage;
import mprog.nl.programmeerproject.R;

public class SpecificChatActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId;
    DatabaseReference databaseRef;
    DatabaseReference ref;

    Button sendMessageButton;
    Button homeButton;
    Button findButton;
    Button chatButton;
    Button schemeButton;

    TextView titleText;

    EditText sendMessageEdit;

    ListView chatList;

    FirebaseListAdapter<ChatMessage> adapter;

    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_chat);

        // Assign firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        ref = databaseRef.child("Chats");

        Intent intent = getIntent();
        final String otherUserId = intent.getStringExtra("otherUserData");

        sendMessageButton = (Button)findViewById(R.id.specChatMesButton);
        homeButton = (Button)findViewById(R.id.homeButton);
        findButton = (Button)findViewById(R.id.findButton);
        chatButton = (Button)findViewById(R.id.chatButton);
        sendMessageButton.setOnClickListener(this);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);

        titleText = (TextView)findViewById(R.id.specChatTitleText);
        titleText.setText(intent.getStringExtra("otherUserName"));

        sendMessageEdit = (EditText)findViewById(R.id.specChatInputEdit);

        chatList = (ListView)findViewById(R.id.specChatMesList);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId + "," + otherUserId)) {
                    ref = ref.child(userId + "," + otherUserId);
                }
                else {
                    ref = ref.child(otherUserId + "," + userId);
                }
                adapter = new FirebaseListAdapter<ChatMessage>(SpecificChatActivity.this, ChatMessage.class, R.layout.chat_message, ref) {
                    @Override
                    protected void populateView(View v, ChatMessage model, int position) {
                        TextView messageText = (TextView)v.findViewById(R.id.messageText);
                        TextView messageUser = (TextView)v.findViewById(R.id.messageUser);

                        // Set their text
                        messageText.setText(model.getMessageText());
                        messageUser.setText(model.getMessageUser());
                    }
                };

                chatList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseRef.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("FirstName").getValue().toString() +
                        " " + dataSnapshot.child("LastName").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.specChatMesButton:
                String message = sendMessageEdit.getText().toString();

                ref.push().setValue(new ChatMessage(message, userName));

                sendMessageEdit.setText("");
                break;
            case R.id.homeButton:
                startActivity(MainActivity.createNewIntent(SpecificChatActivity.this, MainActivity.class));
                break;
            case R.id.findButton:
                startActivity(MainActivity.createNewIntent(SpecificChatActivity.this, FindUserActivity.class));
                break;
            case R.id.chatButton:
                startActivity(MainActivity.createNewIntent(SpecificChatActivity.this, ChatActvity.class));
                break;
        }
    }
}
