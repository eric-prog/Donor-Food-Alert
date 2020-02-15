package com.example.donoralert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import com.google.firebase.*;
import android.widget.*;

import java.io.InputStream;
import java.util.*;
import android.content.*;
import com.google.android.gms.location.*;

public class FoodConfirmation extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 123;
    private Button button;
    private Location currLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private BitmapFactory.Options options2 = new BitmapFactory.Options();

    EditText foodName;
    EditText description;

    ImageView imageView;
    Button chooseImage;
    Uri selectedImageUri;
    TextView locationLabel;

    Button confirm;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    LocationManager locationManager;
    LocationListener locationListener;
    FirebaseUser user;
    public static final int PICK_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_confirmation);

        foodName = findViewById(R.id.foodNameInputBox);
        description = findViewById(R.id.descriptionInputBox);
        locationLabel = findViewById(R.id.location);
        imageView = findViewById(R.id.imageView);
        chooseImage = findViewById(R.id.chooseImage);
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });


        mAuth = FirebaseAuth.getInstance();

        confirm = findViewById(R.id.confirmButton);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
                openFoodWaitList();
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //calling location listener to get location coordinates on change of location
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currLocation = location;
                for (int x = 0; x < 10; x++)
                {
                    System.out.println (location);
                }
                locationLabel.setText(currLocation.toString());
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);
            return;
        } else {
            //after above check start the method:
            //locationConfiguration();
        }

        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient (this);
        updateLocation();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (data == null)  {return;}
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
            /*String filestring = selectedImageUri.getPath();

            Bitmap thumbnail = BitmapFactory.decodeFile(filestring, options2);

            imageView.setImageBitmap(thumbnail);*/
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    //locationConfiguration();
                return;
        }
    }

    private void updateLocation () {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currLocation = location;
                    String lat = Integer.toString((int)currLocation.getLatitude());
                    String lng = Integer.toString((int)currLocation.getLongitude());
                    locationLabel.setText(lat + ", " + lng);
                }
            }
        });
    }

    public void uploadData () {
        if (user == null) {
            Toast.makeText(FoodConfirmation.this, "Upload Failed", Toast.LENGTH_LONG).show();
            return;
        }
        String id = user.getUid();
        Map <String, Object> content = new HashMap<>();
        String name = foodName.getText().toString().trim();
        String descr = description.getText().toString().trim();
        if (name == null) {
            content.put ("name", "");
        }
        else {
            content.put("name", name);
        }

        if (currLocation != null) {
            GeoPoint point = new GeoPoint(currLocation.getLatitude(), currLocation.getLongitude());
            content.put("location", point);
        } else {
            content.put("location", new GeoPoint(0, 0));
        }
        if (descr == null) {
            content.put ("description", "");
        } else {
            content.put("description", descr);
        }
        if (selectedImageUri != null) {
            content.put("image", selectedImageUri.toString());
        }
        else
        {
            content.put("image", "");
        }

        System.out.println (db.collection(id));
        db.collection(id).add(content);

        foodName.setText("");
        description.setText("");

        db.collection("AllFoods").add(content);
    }

    public void openFoodWaitList () {
        Intent intent = new Intent (this, FoodWaitList.class);
        startActivity(intent);
    }
}