package com.example.donoralert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.firestore.*;
import com.google.firebase.*;
import java.util.*;

public class SignUpScreen extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        button = findViewById(R.id.finishButton);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openFoodConfirmation();
        }
      });
    }

    public void openFoodConfirmation(){
        Intent intent = new Intent(this, FoodConfirmation.class);
        startActivity(intent);
    }


}
