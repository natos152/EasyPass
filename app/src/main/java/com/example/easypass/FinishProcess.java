package com.example.easypass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class FinishProcess extends AppCompatActivity {
    TextView linkTextView;
    ImageButton share;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_process);
        share = findViewById(R.id.share_btn);
        linkTextView = findViewById(R.id.linkApointment);
        linkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webLInks("https://agendamentosonline.mne.pt/AgendamentosOnline/index.jsf");
            }
        });
    }

    private void webLInks(String webUrl) {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.addCategory(Intent.CATEGORY_BROWSABLE);
        i.setData(Uri.parse(webUrl));
        startActivity(i);
    }

    public void onClick2(View view) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "הצטרף אלינו והוצא דרכון פורטוגלי בקלות, הורד בחנות האפלקיציות EasyPass";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "הפצת האפליקציה EasyPass");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}