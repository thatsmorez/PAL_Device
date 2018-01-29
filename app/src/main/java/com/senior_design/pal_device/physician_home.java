package com.senior_design.pal_device;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class physician_home extends AppCompatActivity {
    Button statistic, help, logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physician_home);

        statistic = (Button) findViewById(R.id.button7);
        help = (Button) findViewById(R.id.button2);
        logout = (Button) findViewById(R.id.button1);

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(physician_home.this, physician_help.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(physician_home.this, login.class);
                startActivity(intent);
            }
        });
    }
}
