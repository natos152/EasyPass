package com.example.easypass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    String case_number = null, status = null, firstName = null, lastname = null;
    TextView st1, show_case_number, clientName;
    View upload_files, wait_to_check, analyze_files, create_passport, shipping, line_one, line_two, line_tree, line_four;
    Button contact_us;

    @Override
    public void onBackPressed() {
        logoutUser();
    }

    private void logoutUser() {
        new AlertDialog.Builder(StatusRequestActivity.this).
                setTitle("התנתקות").
                setMessage("אתה בטוח שאתה רוצה להתנתק ?").
                setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        finish();
                        System.exit(0);
                        //startActivity(new Intent(ProcessActivity.this, MainActivity.class));
                    }
                })
                .setNegativeButton(android.R.string.no, null).
                setIcon(android.R.drawable.ic_dialog_info).show();
    }

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
        clientName = findViewById(R.id.client_name2);
        upload_files = findViewById(R.id.statusFirst);
        wait_to_check = findViewById(R.id.statusSecond);
        analyze_files = findViewById(R.id.statusSecond2);
        create_passport = findViewById(R.id.statusSecond3);
        shipping = findViewById(R.id.statusSecond4);
        line_one = findViewById(R.id.line);
        line_two = findViewById(R.id.line2);
        line_tree = findViewById(R.id.line3);
        line_four = findViewById(R.id.line4);
    }

    private void readFromDB() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                firstName = snapshot.child("UserInfo").child("first_name").getValue(String.class);
                lastname = snapshot.child("UserInfo").child("last_name").getValue(String.class);
                clientName.setText("שם הלקוח : " + firstName + " " + lastname);
                case_number = snapshot.child("Case number").getValue(String.class);
                show_case_number.setText("תיק מספר :  " + case_number);
                status = snapshot.child("Status Request").getValue(String.class);
                CheckStatus(status);
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

                Toast.makeText(StatusRequestActivity.this, "בעוד מספר שניות תעבר/י למסך סיום התהליך", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(StatusRequestActivity.this, FinishProcess.class));
                    }
                }, 7000);
                break;
        }
    }

    public void onClick(View view) {
        startActivity(new Intent(StatusRequestActivity.this, ContactUs.class));
    }
}