package com.senior_design.pal_device;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class music_therapist_help extends AppCompatActivity {
    Button returnHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_therapist_help);

        returnHome = (Button) findViewById(R.id.button8);

        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(music_therapist_help.this, music_therapist_home.class);
                startActivity(intent);
            }
        });
    }
}
