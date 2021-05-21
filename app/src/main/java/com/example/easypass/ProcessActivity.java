package com.example.easypass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProcessActivity extends AppCompatActivity {
    TextView welcome_mess;
    Button btnPassport, btnID, btnBirthdate, btnPoliceCertificate, btnFamilyTree;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    public void onBackPressed() {
        logoutUser();
    }

    private void logoutUser() {
        new AlertDialog.Builder(ProcessActivity.this).
                setTitle("התנתקות").
                setMessage("אתה בטוח שאתה רוצה להתנתק ?").
                setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        startActivity(new Intent(ProcessActivity.this, MainActivity.class));
                    }
                })
                .setNegativeButton(android.R.string.no, null).
                setIcon(android.R.drawable.ic_dialog_info).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        initViews();
        initButtons();
        readFromDB();
    }

    private void readFromDB() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                welcome_mess.setText("ברוך הבא " + user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initButtons() {
//        btnPassport.setOnClickListener(this);
//        btnID.setOnClickListener(this);
//        btnBirthdate.setOnClickListener(this);
//        btnPoliceCertificate.setOnClickListener(this);
//        btnFamilyTree.setOnClickListener(this);
    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        welcome_mess = findViewById(R.id.welcome_message);
        myRef = database.getReference("Users").child(mAuth.getUid());
        btnPassport = findViewById(R.id.btn_upload_pass);
        btnID = findViewById(R.id.btn_upload_ID);
        btnBirthdate = findViewById(R.id.btn_upload_birthdate);
        btnPoliceCertificate = findViewById(R.id.btn_upload_police_crteif);
        btnFamilyTree = findViewById(R.id.btn_upload_family_tree);
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload_pass:
                Toast.makeText(getApplicationContext(), "דרכון", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_upload_ID:
                Toast.makeText(getApplicationContext(), "תעודת זהות", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_upload_birthdate:
                Toast.makeText(getApplicationContext(), "תעודת לידה", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_upload_police_crteif:
                Toast.makeText(getApplicationContext(), "תעודת יושר", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_upload_family_tree:
                Toast.makeText(getApplicationContext(), "עץ משפחה", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}