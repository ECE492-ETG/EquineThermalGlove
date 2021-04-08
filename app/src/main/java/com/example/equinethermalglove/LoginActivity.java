package com.example.equinethermalglove;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.regex.Pattern;

/**
 * login activity so that new users can register and existing users can login and modify their profiles
 */
public class LoginActivity extends AppCompatActivity {

    // global variables
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private EditText emailField;
    private EditText passwordField;

    private Button signInButton;
    private Button registerButton;

    /**
     * function called when acitivty is invoked
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // variable initialization
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);

        signInButton = findViewById(R.id.sign_in_button);
        registerButton = findViewById(R.id.register_button);

        // attempt to sign user in on button click
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignIn();
            }
        });

        // register new user on register button clicked
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegistration();
            }
        });

        // return to main activity upon successful login
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * attempt to sign the user in if valid information is presented to the activity
     */
    private void attemptSignIn() {
        if(emailIsValid() & passwordIsValid()) {
            String email = emailField.getText().toString().trim().toLowerCase();
            String password = passwordField.getText().toString().trim();
            signIn(email, password);
        } else {
            Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * sign the user in
     * @param email
     *      user email
     * @param password
     *      user password
     */
    private void signIn(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                    Log.e("FAILED_AUTH", task.getException().toString());
                }
            }
        });
    }

    /**
     * check if email is valid
     * @return
     *      true is valid email, false otherwise
     */
    boolean emailIsValid() {
        String email = emailField.getText().toString().trim();

        if(email.isEmpty()) {
            emailField.setError("Please enter an email.");
            return false;
        }
        else if(!Pattern.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$", email)) {
            emailField.setError("Invalid Email");
            return false;
        }

        emailField.setError(null);
        return true;
    }

    /**
     * check if valid password
     * @return
     *      true if valid password, false otherwise
     */
    boolean passwordIsValid() {
        String password = passwordField.getText().toString().trim();

        if(password.isEmpty()) {
            passwordField.setError("A password is required.");
            return false;
        }
        else if(password.length() < 6) {
            passwordField.setError("The password must be at least 6 characters.");
            return false;
        }

        passwordField.setError(null);
        return true;
    }

    /**
     * attempt to register a new user assuming new information for database
     */
    private void attemptRegistration() {
        if (emailIsValid() & passwordIsValid()) {
            createAccount();
        }
    }

    /**
     * create an account for a new user
     */
    private void createAccount() {

        final String email = emailField.getText().toString().trim().toLowerCase();
        String password = passwordField.getText().toString().trim();

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "You're Signed Up", Toast.LENGTH_SHORT).show();
                } else {

                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthEmailException e) {
                        Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    }
                    catch (FirebaseAuthWeakPasswordException e) {
                        Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    }
                    catch (FirebaseAuthUserCollisionException e) {
                        Toast.makeText(LoginActivity.this, "Email already in use.", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e) {
                        Log.e("EXCEPTION THROWN: ", e.toString());
                    }
                }
            }
        });
    }
}
