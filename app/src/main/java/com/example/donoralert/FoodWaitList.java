package com.example.donoralert;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.EventListener;
import android.widget.*;

import java.sql.Array;
import java.util.*;

public class FoodWaitList extends AppCompatActivity {

    String id;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ListView listView;

    Food [] mobileArray = new Food [0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_wait_list);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        id = user.getUid();
        db = FirebaseFirestore.getInstance();

        //set up the list view
        listView = findViewById(R.id.listView);
        ArrayAdapter adapter = new CustomListAdaptor(this, mobileArray);
        listView.setAdapter(adapter);

        updateData(id);
    }

    public void updateData (String userId) {

        db.collection(userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(FoodWaitList.this, "Failed to Load Data", Toast.LENGTH_LONG).show();
                    return;
                }
                ArrayList<Food> list = new ArrayList<Food>();
                for (QueryDocumentSnapshot doc: value) {
                    if (doc.get("name") != null && doc.get("description") != null) {
                        Food f = new Food (doc.getString("name"), doc.getString("description"), doc.getGeoPoint("location"), doc.getString("image"));
                        list.add(f);
                    }
                }
                updateUI (list);
            }
        });
    }

    public void updateUI (ArrayList<Food> arr) {
        mobileArray = new Food [arr.size()];
        for (int x = 0; x < arr.size(); x++)
        {
            mobileArray [x] = arr.get(x);
        }
        ArrayAdapter adapter = new CustomListAdaptor(this, mobileArray);
        listView.setAdapter(adapter);
    }
}
