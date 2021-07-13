package com.example.easypass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private long backPressedTime; // התנתקות
    private Toast backToast;
    EditText Email, Pass;
    Button login_btn, sign_up_btn;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance("https://easypass-dcff0-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference("Users");
        initViews();
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ValidationProcess.class));
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });
    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        Email = findViewById(R.id.emailInput);
        Pass = findViewById(R.id.passInput);
        login_btn = findViewById(R.id.login_btn);
        sign_up_btn = findViewById(R.id.sign_up_btn);
    }

    private void signInUser() {
        if (Email.getText().toString().equals("") && Pass.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "אנא מלא/י את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(Email.getText().toString(), Pass.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "התחברת בהצלחה", Toast.LENGTH_SHORT).show();
                            ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                                    "Loading. Please wait...", true);
                            myRef.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String userExist = snapshot.child("UserInfo").child("id").getValue(String.class);
                                    String bir = snapshot.child("userDocuments").child("Birthdate").getValue(String.class);
                                    String fml = snapshot.child("userDocuments").child("FamilyTree").getValue(String.class);
                                    String id = snapshot.child("userDocuments").child("Id").getValue(String.class);
                                    String passport = snapshot.child("userDocuments").child("Passport").getValue(String.class);
                                    String police = snapshot.child("userDocuments").child("Police Certificate").getValue(String.class);
                                    dialog.dismiss();
                                    if (bir != null && fml != null && id != null && passport != null && police != null) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(new Intent(MainActivity.this, ProcessActivity.class));
                                            }
                                        }, 1500);
                                    } else if (userExist != null) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(new Intent(MainActivity.this, ProcessActivity.class));
                                            }
                                        }, 1500);
                                    } else {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(new Intent(MainActivity.this, AdvancedRegisterActivity.class));
                                            }
                                        }, 1500);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "משתמש אינו תקין, בדוק את פרטיך", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void exitApp() {
        new AlertDialog.Builder(MainActivity.this).
                setTitle("יציאה").
                setMessage("האם את/ה בטוח/ה שאת/ה רוצה לצאת מהאפליקציה ?").
                setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("לא", null).
                setIcon(android.R.drawable.ic_dialog_info).show();
    }

    @Override
    public void onBackPressed() { // התנתקות לאחר שתי לחיצות.
        exitApp();
    }
}