package com.senior_design.pal_device;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;


public class music_therapist_home extends AppCompatActivity {

    Button statistic, release_info, create_account, addPAL, record, help, logout;
    String accountUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_therapist_home);

        statistic = (Button) findViewById(R.id.button7);
        release_info = (Button) findViewById(R.id.button6);
        create_account = (Button) findViewById(R.id.button5);
        addPAL = (Button) findViewById(R.id.button4);
        record = (Button) findViewById(R.id.button3);
        help = (Button) findViewById(R.id.button2);
        logout = (Button) findViewById(R.id.button1);

        Bundle bundle = getIntent().getExtras();

        accountUser = bundle.getString("user");

        release_info.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent (music_therapist_home.this, release_information.class);
                Bundle bundle = new Bundle();
                bundle.putString("user", accountUser);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        create_account.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent (music_therapist_home.this, create_parent_account.class);
                startActivity(intent);
            }
        });

        addPAL.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(music_therapist_home.this, associate_pal.class);
                startActivity(intent);
            }
        });

        record.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(music_therapist_home.this, record_lullaby_1.class);
                startActivity(intent);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(music_therapist_home.this, music_therapist_help.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(music_therapist_home.this, login.class);
                startActivity(intent);
            }
        });
    }
}
