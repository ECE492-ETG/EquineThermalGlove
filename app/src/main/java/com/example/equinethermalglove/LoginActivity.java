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

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private EditText emailField;
    private EditText passwordField;

    private Button signInButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);

        signInButton = findViewById(R.id.sign_in_button);
        registerButton = findViewById(R.id.register_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignIn();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void attemptSignIn() {
        if(emailIsValid() & passwordIsValid()) {
            String email = emailField.getText().toString().trim().toLowerCase();
            String password = passwordField.getText().toString().trim();
            signIn(email, password);
        }
    }

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

    boolean emailIsValid() {
        String email = emailField.getText().toString().trim();

        if(email.isEmpty()) {
            emailField.setError("Please enter your email.");
            return false;
        }
        else if(!Pattern.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$", email)) {
            emailField.setError("Invalid Email");
            return false;
        }

        emailField.setError(null);
        return true;
    }

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

        emailField.setError(null);
        return true;
    }
}
