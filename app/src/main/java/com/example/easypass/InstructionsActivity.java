package com.example.easypass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import java.util.Arrays;


public class InstructionsActivity extends AppCompatActivity {
    private static final String TAG = "InstructionsActivity";
    String case_number = null, case_number_fire = null, firstName = null, lastname = null, birthclient = null, idclient = null, passclient = null, policcerclient = null, fmilyclient = null;
    TextView clientName, show_case_number;
    EditText get_case_num;
    Button BtnConfirm, contact_us;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String[] Documents = null;
    static int click_count = 0;

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
                passclient = snapshot.child("userDocuments").child("Passport").getValue(String.class);
                idclient = snapshot.child("userDocuments").child("Id").getValue(String.class);
                birthclient = snapshot.child("userDocuments").child("Birthdate").getValue(String.class);
                policcerclient = snapshot.child("userDocuments").child("Police Certificate").getValue(String.class);
                fmilyclient = snapshot.child("userDocuments").child("FamilyTree").getValue(String.class);
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
                    setMessage("מספר תיק שגוי, אנא הזנ/י שוב או פני/ה לתמיכה בכפתור יצירת קשר למעלה בצד שמאל על גבי המסך").
                    setPositiveButton("בסדר", new DialogInterface.OnClickListener() {
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

    public void onClick3(View view) {
        if (click_count == 0)
            sendEmail();
        else
            new AlertDialog.Builder(InstructionsActivity.this).
                    setTitle("שגיאה").
                    setMessage("המסמכים נשלחו כבר, לצערנו לא ניתן לשלוח שוב, אנא פנה/י לתמיכה בכפתור יצירת קשר").
                    setPositiveButton("בסדר", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    public void sendEmail() {
        Log.d(TAG, "send email...");
        String[] TO = {"epass876@gmail.com"};
        String[] CC = {"ron96t@gmail.com"};
        String subject = "תיק מסמכים של " + firstName + " " + lastname;
        Documents = new String[]{birthclient, idclient, passclient, policcerclient, fmilyclient};

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "המסמכים: ");
        emailIntent.putExtra(Intent.EXTRA_TEXT, Arrays.toString(Documents));

        try {
            startActivity(Intent.createChooser(emailIntent, "שולח מייל..."));
            click_count++;
            Log.i("מסיים לשלוח את המייל...", "");
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(InstructionsActivity.this,
                    "אימייל לא תקין...", Toast.LENGTH_SHORT).show();
        }
    }
}