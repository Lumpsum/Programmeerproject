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

public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    protected EditText emailEdit;
    protected EditText passEdit;

    protected Button logInButton;

    protected TextView signUpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Firebase variables
        firebaseAuth = FirebaseAuth.getInstance();

        // Assign layout elements
        emailEdit = (EditText)findViewById(R.id.logInEmailEdit);
        passEdit = (EditText)findViewById(R.id.logInPassEdit);

        logInButton = (Button)findViewById(R.id.logInButton);

        signUpText = (TextView) findViewById(R.id.logInSignUpText);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = emailEdit.getText().toString();
                String pass = passEdit.getText().toString();

                // Trim the input
                user.trim();
                pass.trim();

                if (user.isEmpty() || pass.isEmpty()) {
                    MainActivity.createAlert("Please, fill in both an email and password.", LogInActivity.this).show();
                }
                else {

                    // Tries to log in, if failed shows the specific error
                    firebaseAuth.signInWithEmailAndPassword(user, pass)
                            .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(MainActivity.createNewIntent(LogInActivity.this, MainActivity.class));
                                    }
                                    else {
                                        MainActivity.createAlert(task.getException().getMessage(), LogInActivity.this).show();
                                    }
                                }
                            });
                }
            }
        });

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainActivity.createNewIntent(LogInActivity.this, SignUpActivity.class));
            }
        });
    }
}
