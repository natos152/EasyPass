package com.example.easypass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StatusRequestActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference storageReference;
    String number_status = null;
    TextView st1, show_case_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satuts_request);
        storageReference = FirebaseStorage.getInstance().getReference().child("userDocuments");
        database = FirebaseDatabase.getInstance("https://easypass-dcff0-default-rtdb.europe-west1.firebasedatabase.app/");
        initViews();
        initButtons();
        readFromDB();
    }

    private void initButtons() {

    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference("Users").child(mAuth.getUid());
        st1 = findViewById(R.id.title);
        show_case_number = findViewById(R.id.case_num);

    }
    private void readFromDB() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                number_status = snapshot.child("Status Request").getValue(String.class);
                show_case_number.setText("תיק מספר :  " + number_status);
                CheckStatus(number_status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void CheckStatus(String status){
        switch (status){
            case "1":
                (findViewById(R.id.statusFirst)).setBackgroundResource(R.drawable.shape_status_current);
                (findViewById(R.id.statusSecond)).setBackgroundResource(R.drawable.shape_status_remaining);
                (findViewById(R.id.statusSecond2)).setBackgroundResource(R.drawable.shape_status_remaining);
                (findViewById(R.id.statusSecond3)).setBackgroundResource(R.drawable.shape_status_remaining);
                (findViewById(R.id.statusSecond4)).setBackgroundResource(R.drawable.shape_status_remaining);
                (findViewById(R.id.line)).setBackgroundColor(Color.GREEN);
                (findViewById(R.id.line2)).setBackgroundColor(Color.GREEN);

                break;
            case "2":
                (findViewById(R.id.statusFirst)).setBackgroundResource(R.drawable.shape_status_completed);
                (findViewById(R.id.statusSecond)).setBackgroundResource(R.drawable.shape_status_current);
                (findViewById(R.id.statusSecond2)).setBackgroundResource(R.drawable.shape_status_remaining);
                (findViewById(R.id.statusSecond3)).setBackgroundResource(R.drawable.shape_status_remaining);
                (findViewById(R.id.statusSecond4)).setBackgroundResource(R.drawable.shape_status_remaining);
                break;
        }
    }
}