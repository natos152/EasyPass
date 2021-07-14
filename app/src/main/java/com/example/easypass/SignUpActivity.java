package com.example.easypass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    EditText Email, UserName, Pass, Repass;
    Button btn_Signup;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://easypass-dcff0-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference("Users");
        initViews();
    }

    private void initViews() {
        Email = findViewById(R.id.sign_email);
        UserName = findViewById(R.id.sign_user);
        Pass = findViewById(R.id.sign_pass);
        Repass = findViewById(R.id.sign_confirm_pass);
        btn_Signup = findViewById(R.id.btn_sign_up_cont);
    }

    public void onClick(View view) {
        signUpUser();
    }

    private void signUpUser() {
        if (Email.getText().toString().equals("") || UserName.getText().toString().equals("") || Pass.getText().toString().equals("") || Repass.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "אנא מלא/י את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Pass.getText().toString().equals(Repass.getText().toString())) {
            Toast.makeText(getApplicationContext(), "סיסמא אינה תואמת!", Toast.LENGTH_LONG).show();
            return;
        } else {
            mAuth.createUserWithEmailAndPassword(Email.getText().toString(), Pass.getText().toString())
                    .addOnCompleteListener(SignUpActivity.this, task -> {
                        if (task.isSuccessful()) {
                            User user = new User(Email.getText().toString(), UserName.getText().toString());
                            myRef.child(mAuth.getUid()).setValue(user);
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        } else {
                            //Error
                            Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

}