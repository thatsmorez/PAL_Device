package com.senior_design.pal_device;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class physician_home extends AppCompatActivity {
    Button statistic, help, logout;
    String accountUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physician_home);

        statistic = (Button) findViewById(R.id.button7);
        help = (Button) findViewById(R.id.button2);
        logout = (Button) findViewById(R.id.button1);

        Bundle bundle = getIntent().getExtras();

        accountUser = bundle.getString("user");

        statistic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(physician_home.this, viewStatistics1.class);
                Bundle bundle = new Bundle();
                bundle.putString("user", accountUser);
                bundle.putString("type", "Doctor");
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(physician_home.this, physician_help.class);
                Bundle bundle = new Bundle();
                bundle.putString("user", accountUser);
                intent.putExtras(bundle);
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
