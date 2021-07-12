package com.example.easypass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class InstructionsActivity extends AppCompatActivity {
    String case_number = null, case_number_fire = null, firstName = null, lastname = null;
    TextView clientName, show_case_number;
    EditText get_case_num;
    Button BtnConfirm, contact_us;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions_activitiy);
        database = FirebaseDatabase.getInstance("https://easypass-dcff0-default-rtdb.europe-west1.firebasedatabase.app/");
        initViews();
        readFromDB();
    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        clientName = findViewById(R.id.client_name);
        myRef = database.getReference("Users").child(mAuth.getUid());
        BtnConfirm = findViewById(R.id.confirmBtn);
        get_case_num = findViewById(R.id.case_num_input);
        show_case_number = findViewById(R.id.case_num_ins);
        contact_us = findViewById(R.id.btn_ContactUs3);
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
                case_number_fire = snapshot.child("Case number").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onClick(View view) {
        validateCaseNum();
    }

    private void validateCaseNum() {
        if (get_case_num.getText().toString().equals("")) {
            Toast.makeText(InstructionsActivity.this, "אנא מלא/י את מספר התיק", Toast.LENGTH_SHORT).show();
            return;
        } else if (case_number_fire.equals(get_case_num.getText().toString())) {
            Toast.makeText(InstructionsActivity.this, "מספר תיק אומת בהצלחה", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(InstructionsActivity.this, StatusRequestActivity.class));
                }
            }, 2000);
        } else {
            new AlertDialog.Builder(InstructionsActivity.this).
                    setTitle("שגיאת אימות").
                    setMessage("מספר תיק שגוי, אנא הזנ/י שוב או פני/ה לתמיכה בכתפור יצירת קשר למעלה בצד שמאל של המסך").
                    setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert).show();
        }
    }

    public void onClickContactUs(View view) {
        startActivity(new Intent(InstructionsActivity.this, ContactUs.class));
    }
}