package mprog.nl.programmeerproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    EditText sendMessageEdit;

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
        String otherUserId = intent.getStringExtra("otherUser");
        if (hasChild(userId + "," + otherUserId)) {
            ref = ref.child(userId + "," + otherUserId);
        }
        else {
            ref = ref.child(otherUserId + "," + userId);
        }

        sendMessageButton = (Button)findViewById(R.id.specChatMesButton);
        homeButton = (Button)findViewById(R.id.homeButton);
        findButton = (Button)findViewById(R.id.findButton);
        chatButton = (Button)findViewById(R.id.chatButton);
        sendMessageButton.setOnClickListener(this);
        homeButton.setOnClickListener(this);
        findButton.setOnClickListener(this);
        chatButton.setOnClickListener(this);

        sendMessageEdit = (EditText)findViewById(R.id.specChatInputEdit);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.specChatMesButton:
                String message
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
