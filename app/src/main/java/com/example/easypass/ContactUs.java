package com.example.easypass;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class ContactUs extends AppCompatActivity {
    private static final String TAG = "ContactUs";
    EditText Subject, Content;
    Button btnSend;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        initViews();
    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        Subject = findViewById(R.id.subject);
        Content = findViewById(R.id.content_text);
        btnSend = findViewById(R.id.btn_send_request);
    }


    public void sendEmail() {
        Log.d(TAG, "send email...");
        String[] TO = {"epass876@gmail.com"};
        String[] CC = {"ron96t@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");

        String subject = Subject.getText().toString();
        String content = Content.getText().toString();

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, content);


        try {
            startActivity(Intent.createChooser(emailIntent, "שולח מייל..."));
            finish();
            Log.i("מסיים לשלוח את המייל...", "");
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(ContactUs.this,
                    "אימייל לא תקין...", Toast.LENGTH_SHORT).show();
        }
    }


    public void onClick(View view) throws InterruptedException {
        if (Subject.getText().toString().equals("") || Content.getText().toString().equals("")) {
            Toast.makeText(ContactUs.this, "אנא מלא/י את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }
        sendEmail();
    }
}