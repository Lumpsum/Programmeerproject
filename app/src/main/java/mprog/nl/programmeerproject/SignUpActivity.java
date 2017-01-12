package mprog.nl.programmeerproject;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    protected TextView logInText;

    protected Button signUpButton;

    protected EditText emailEdit;
    protected EditText passEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Firebase variables
        firebaseAuth = FirebaseAuth.getInstance();

        signUpButton = (Button)findViewById(R.id.signUpNextButton);

        emailEdit = (EditText)findViewById(R.id.signUpEmailEdit);
        passEdit = (EditText)findViewById(R.id.signUpPassEdit);

        logInText = (TextView)findViewById(R.id.signUpLogText);

        logInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainActivity.createNewIntent(SignUpActivity.this, LogInActivity.class));
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailEdit.getText().toString();
                final String pass = passEdit.getText().toString();

                email.trim();
                pass.trim();

                if (email.isEmpty() || pass.isEmpty()) {
                    MainActivity.createAlert("Please enter both an email and password", SignUpActivity.this).show();
                }
                else {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(MainActivity.createNewIntent(SignUpActivity.this, CreateProfileActivity.class));
                                    }
                                    else {
                                        MainActivity.createAlert(task.getException().getMessage(), SignUpActivity.this);
                                    }
                                }
                            });
                }
            }
        });
    }
}
