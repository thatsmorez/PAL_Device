package com.senior_design.pal_device;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class parent_help extends AppCompatActivity {
    Button returnHome;
    String accountUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_help);

        returnHome = (Button) findViewById(R.id.button8);
        Bundle bundle = getIntent().getExtras();
        accountUser = bundle.getString("parentUser");

        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_help.this, parent_home.class);
                Bundle bundle = new Bundle();
                bundle.putString("parentUser",accountUser);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
