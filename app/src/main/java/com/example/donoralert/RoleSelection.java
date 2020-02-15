package com.example.donoralert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.*;
import java.util.*;
import android.content.*;

//role activity is now the login page
public class RoleSelection extends AppCompatActivity {

    Button signUp;
    Button logIn;
    EditText email;
    TextInputEditText password;
    RoleSelection selfRef;
    Intent toDonor;
    Intent toReceiver;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);
        getSupportActionBar().setTitle("Log In");
        signUp = findViewById(R.id.signUp);
        logIn = findViewById(R.id.login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        toDonor = new Intent(this, SignUpScreen.class);
        toReceiver = new Intent (this, MapsActivity.class);
        preferences = this.getSharedPreferences("com.example.donoralert", Context.MODE_PRIVATE);
        editor = preferences.edit();

        selfRef = this;

        //initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfRef.signUp();
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfRef.logIn();
            }
        });
    }

    @Override
    public void onStart () {
        super.onStart();
        //Check if user signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            toNextView();
            //go to the next page
        }
    }

    //goes to the next view
    public void toNextView() {
        //false: receiver, true: donor

        if (preferences.getBoolean("role", false))
        {
            startActivity(toDonor);
        }
        else {
            startActivity(toReceiver);
        }
    }

    //for those without an account
    protected void signUp () {
        final String useremail = email.getText().toString().trim();
        final String userpassword = password.getText().toString().trim();
        mAuth.createUserWithEmailAndPassword(useremail, userpassword)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    editor.putString("password", userpassword);
                    editor.putString("email", useremail);
                    editor.commit();
                    //Sign in success, update UI with the signed-in user's information
                    Toast.makeText(RoleSelection.this, "Sign Up Was Successful", Toast.LENGTH_LONG).show();
                    toNextView ();
                } else {
                    Toast.makeText(RoleSelection.this, "Sign Up Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void logIn () {
        final String useremail = email.getText().toString().trim();
        final String userpassword = password.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(useremail, userpassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            editor.putString("password", userpassword);
                            editor.putString("email", useremail);
                            editor.commit();
                            Toast.makeText(RoleSelection.this, "Signed In", Toast.LENGTH_LONG).show();
                            toNextView();
                        } else {
                            Toast.makeText(RoleSelection.this, "Sign In Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
