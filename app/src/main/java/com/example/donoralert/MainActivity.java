package com.example.donoralert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.*;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.*;
import com.google.firebase.*;
import android.util.*;
import android.view.View;
import android.widget.*;
import android.content.*;

import com.google.firebase.auth.*;

//main activity is now the welcome page
public class MainActivity extends AppCompatActivity {

    Button receiver;
    Button donor;
    Intent toLogIn;
    //persistant storage
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Welcome");

        receiver = findViewById(R.id.receiver);
        donor = findViewById(R.id.donor);
        mAuth = FirebaseAuth.getInstance();
        //create an intent
        toLogIn = new Intent(this, RoleSelection.class);
        preferences = this.getSharedPreferences("com.example.donoralert", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("password", "");
        editor.putString("email", "");
        editor.commit();

        receiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("role", false);
                editor.commit();
                toNextView();
            }
        });

        donor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("role", true);
                editor.commit();
                toNextView();
            }
        });
    }

    @Override
    public void onStart () {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            toNextView();
            //go to the next page
        }
    }

    public void toNextView () {
        startActivity(toLogIn);
    }
}
