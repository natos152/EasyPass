package com.example.easypass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdvancedRegisterActivity extends AppCompatActivity {
    EditText id, first_name, last_name, phone, address, apartment, zipCode, country;
    Button btn_Comp_Sign;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

//    @Override
//    protected void onStart() {
//        super.onStart();
//        ProgressDialog dialog = ProgressDialog.show(AdvancedRegisterActivity.this, "",
//                "Loading. Please wait...", true);
//        myRef.child(mAuth.getUid()).child("UserInfo").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
//                String temp = snapshot.child("id").getValue(String.class);
//                if(temp != null){
//                    dialog.dismiss();
//                    startActivity(new Intent(AdvancedRegisterActivity.this, ProcessActivity.class));
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {
//
//            }
//        });
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_register);
        initViews();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://easypass-dcff0-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference("Users");
        btn_Comp_Sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataToDB();
                Intent i = new Intent(AdvancedRegisterActivity.this, ProcessActivity.class);
                startActivity(i);
            }
        });
    }

    private void initViews() {
        id = findViewById(R.id.sign_id);
        first_name = findViewById(R.id.sign_first_name);
        last_name = findViewById(R.id.sign_last_name);
        phone = findViewById(R.id.sign_phone);
        address = findViewById(R.id.sign_address);
        apartment = findViewById(R.id.sign_apartment);
        zipCode = findViewById(R.id.sign_zip_code);
        country = findViewById(R.id.sign_country);
        btn_Comp_Sign = findViewById(R.id.sign_up_btn);
    }

    private void addDataToDB() {
        PersonInfo pi = new PersonInfo(id.getText().toString(), first_name.getText().toString(), last_name.getText().toString(), phone.getText().toString(), address.getText().toString(), apartment.getText().toString(), zipCode.getText().toString(), country.getText().toString());
        myRef.child(mAuth.getUid()).child("UserInfo").setValue(pi);
    }
}