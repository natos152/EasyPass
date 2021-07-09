package com.example.easypass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    View upload_files, wait_to_check, analyze_files, create_passport, shipping;
    ImageView line_one, line_two, line_tree, line_four, line_five;
    Button contact_us;
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
        contact_us = findViewById(R.id.btn_ContactUs);
    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference("Users").child(mAuth.getUid());
        st1 = findViewById(R.id.title);
        show_case_number = findViewById(R.id.case_num);
        upload_files = findViewById(R.id.statusFirst);
        wait_to_check = findViewById(R.id.statusSecond);
        analyze_files = findViewById(R.id.statusSecond2);
        create_passport = findViewById(R.id.statusSecond3);
        shipping = findViewById(R.id.statusSecond4);
        line_one = findViewById(R.id.line);
        line_two = findViewById(R.id.line1);
        line_tree = findViewById(R.id.line2);
        line_four = findViewById(R.id.line3);
        line_five = findViewById(R.id.line4);
    }

    private void readFromDB() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                number_status = snapshot.child("Case number").getValue(String.class);
                show_case_number.setText("תיק מספר :  " + number_status);
                CheckStatus(number_status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void CheckStatus(String status) {
        switch (status) {
            case "1":
                line_one.setBackgroundColor(Color.GREEN);
                upload_files.setBackgroundResource(R.drawable.shape_status_current);
                wait_to_check.setBackgroundResource(R.drawable.shape_status_remaining);
                analyze_files.setBackgroundResource(R.drawable.shape_status_remaining);
                create_passport.setBackgroundResource(R.drawable.shape_status_remaining);
                shipping.setBackgroundResource(R.drawable.shape_status_remaining);
                break;
            case "2":
                line_one.setBackgroundColor(Color.GREEN);
                upload_files.setBackgroundResource(R.drawable.shape_status_completed);
                wait_to_check.setBackgroundResource(R.drawable.shape_status_current);
                analyze_files.setBackgroundResource(R.drawable.shape_status_remaining);
                create_passport.setBackgroundResource(R.drawable.shape_status_remaining);
                shipping.setBackgroundResource(R.drawable.shape_status_remaining);
                break;
            case "3":
                line_one.setBackgroundColor(Color.GREEN);
                line_two.setBackgroundColor(Color.GREEN);
                upload_files.setBackgroundResource(R.drawable.shape_status_completed);
                wait_to_check.setBackgroundResource(R.drawable.shape_status_completed);
                analyze_files.setBackgroundResource(R.drawable.shape_status_current);
                create_passport.setBackgroundResource(R.drawable.shape_status_remaining);
                shipping.setBackgroundResource(R.drawable.shape_status_remaining);
                break;
            case "4":
                line_one.setBackgroundColor(Color.GREEN);
                line_two.setBackgroundColor(Color.GREEN);
                line_tree.setBackgroundColor(Color.GREEN);
                upload_files.setBackgroundResource(R.drawable.shape_status_completed);
                wait_to_check.setBackgroundResource(R.drawable.shape_status_completed);
                analyze_files.setBackgroundResource(R.drawable.shape_status_completed);
                create_passport.setBackgroundResource(R.drawable.shape_status_current);
                shipping.setBackgroundResource(R.drawable.shape_status_remaining);
                break;
            case "5":
                line_one.setBackgroundColor(Color.GREEN);
                line_two.setBackgroundColor(Color.GREEN);
                line_tree.setBackgroundColor(Color.GREEN);
                line_four.setBackgroundColor(Color.GREEN);
                upload_files.setBackgroundResource(R.drawable.shape_status_completed);
                wait_to_check.setBackgroundResource(R.drawable.shape_status_completed);
                analyze_files.setBackgroundResource(R.drawable.shape_status_completed);
                create_passport.setBackgroundResource(R.drawable.shape_status_completed);
                shipping.setBackgroundResource(R.drawable.shape_status_current);
                break;
            case "6":
                line_one.setBackgroundColor(Color.GREEN);
                line_two.setBackgroundColor(Color.GREEN);
                line_tree.setBackgroundColor(Color.GREEN);
                line_four.setBackgroundColor(Color.GREEN);
                upload_files.setBackgroundResource(R.drawable.shape_status_completed);
                wait_to_check.setBackgroundResource(R.drawable.shape_status_completed);
                analyze_files.setBackgroundResource(R.drawable.shape_status_completed);
                create_passport.setBackgroundResource(R.drawable.shape_status_completed);
                shipping.setBackgroundResource(R.drawable.shape_status_completed);
                break;
        }
    }

    public void onClick(View view) {
        startActivity(new Intent(StatusRequestActivity.this, ContactUs.class));
    }
}