package com.example.easypass;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ValidationProcess extends AppCompatActivity {
    private RadioGroup radioYesOrNoGroup;
    private RadioButton radioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation_process);
        TextView linkTextView = findViewById(R.id.linkLastName);
        linkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webLInks("https://www.avotaynu.com/csi/csi-home.htm");
            }
        });
    }

    private void addListenerOnButton() {
        radioYesOrNoGroup = (RadioGroup) findViewById(R.id.radioAnswers);
        int selectID = radioYesOrNoGroup.getCheckedRadioButtonId();
        radioButton = findViewById(selectID);
    }

    private void webLInks(String webUrl) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.addCategory(Intent.CATEGORY_BROWSABLE);
        i.setData(Uri.parse(webUrl));
        startActivity(i);
    }

    public void onClick(View view) {
        addListenerOnButton();
        if (radioButton.getText().equals("כן")) {
            Toast.makeText(getApplicationContext(), "את/ה זכאי להמשיך לשלב הבא ", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(ValidationProcess.this, SignUpActivity.class));
                }
            }, 3000);
        } else {
            new AlertDialog.Builder(ValidationProcess.this).
                    setTitle("שגיאה").
                    setMessage("אינך זכאי לקבל דרכון פורטוגלי").
                    setPositiveButton("בסדר", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).
                    setIcon(android.R.drawable.ic_dialog_alert).show();
        }

    }
}