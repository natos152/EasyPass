package com.example.easypass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private long backPressedTime; // התנתקות
    private Toast backToast;
    EditText Email, Pass;
    Button login_btn, sign_up_btn;
    FirebaseAuth mAuth;

    //    @Override
    //    protected void onStart() {
    //        mAuth = FirebaseAuth.getInstance();
    //
    //        super.onStart();
    //        if(mAuth.getCurrentUser() != null){
    //            startActivity(new Intent(MainActivity.this,WindowLoginSuccess.class));
    //        }
    //    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
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
        if (Email.getText().toString() == null && Pass.getText().toString() == null) {
            Toast.makeText(MainActivity.this, "אנא מלא/י את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(Email.getText().toString(), Pass.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "התחברת בהצלחה", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, ProcessActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "ההתחברות נכשלה", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onBackPressed() { // התנתקות לאחר שתי לחיצות.
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "לחץ שוב כדי לצאת", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}