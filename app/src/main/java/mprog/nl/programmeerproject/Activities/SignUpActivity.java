package mprog.nl.programmeerproject.Activities;

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

import mprog.nl.programmeerproject.R;

/**
 * Activity that handles the first part of the sign up process, with email and password.
 */
public class SignUpActivity extends AppCompatActivity {

    // Init variables
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

        // Assign to the xml elements and init the variables
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

        // Checks whether every field is filled in and create the user in the firebase.
        // Also starts the next activity of the sign up process.
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

                    // Creation of the user in the firebase.
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        MainActivity.createToast(SignUpActivity.this, "First step of the profile creation completed.").show();
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
