package com.senior_design.pal_device;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class physician_help extends AppCompatActivity {
    Button returnHome;
    String accountUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physician_help);

        Bundle bundle = getIntent().getExtras();

        accountUser = bundle.getString("user");

        returnHome = (Button) findViewById(R.id.button8);

        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(physician_help.this, physician_home.class);
                Bundle bundle = new Bundle();
                bundle.putString("user", accountUser);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
