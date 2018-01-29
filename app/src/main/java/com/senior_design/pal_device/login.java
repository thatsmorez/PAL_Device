package com.senior_design.pal_device;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class login extends AppCompatActivity {
    Button b1;
    EditText ed1, ed2;
    TextView tx1;
    int counter = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        b1 = (Button) findViewById(R.id.button);
        ed1 = (EditText) findViewById(R.id.editText1);
        ed2 = (EditText) findViewById(R.id.editText2);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed1.getText().toString().equals("music_therapist") && ed2.getText().toString().equals("music_therapist")) {
                    Intent intent = new Intent(login.this, music_therapist_home.class);
                    startActivity(intent);
                } else if (ed1.getText().toString().equals("doctor") && ed2.getText().toString().equals("doctor")) {
                    Intent intent = new Intent(login.this, physician_home.class);
                    startActivity(intent);
                } else if (ed1.getText().toString().equals("parent") && ed2.getText().toString().equals("parent")) {
                    Intent intent = new Intent(login.this, parent_home.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(login.this, login.class);
                    startActivity(intent);
                }
            }
        });
    }
}
