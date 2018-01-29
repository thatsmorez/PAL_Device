package com.senior_design.pal_device;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class create_parent_account extends AppCompatActivity {
    Button returnHome, createAccount;
    EditText email, patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_parent_account);


        returnHome = (Button) findViewById(R.id.button8);
        createAccount = (Button) findViewById(R.id.button9);

        patient = (EditText) findViewById(R.id.editText1);
        email = (EditText) findViewById(R.id.editText2);

        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(create_parent_account.this, music_therapist_home.class);
                startActivity(intent);
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send patient I.D.  and email to server
                Intent intent = new Intent(create_parent_account.this, create_parent_account.class);
                startActivity(intent);
            }
        });
    }
}
