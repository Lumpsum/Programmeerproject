package mprog.nl.programmeerproject;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActvity extends AppCompatActivity implements View.OnClickListener {

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

    ArrayList<String> chatArray;

    ArrayAdapter<String> chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_actvity);

        // Assign firebase variables
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        ref = databaseRef.child("Users").child(userId);

        homeButton = (Button)findViewById(R.id.homeButton);
        findButton = (Button)findViewById(R.id.findButton);
        chatButton = (Button)findViewById(R.id.chatButton);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);

        chatList = (ListView)findViewById(R.id.chatListView);

        chatArray = new ArrayList<>();

        chatAdapter = new ArrayAdapter(this, R.layout.list_item, chatArray);

        ref.child("Chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    chatArray.add(postSnapshot.getKey());
                }
                chatList.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = MainActivity.createNewIntent(ChatActvity.this, SpecificChatActivity.class);
                intent.putExtra("otherUser", ((TextView)view).getText());
                startActivity(intent);
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
