package com.senior_design.pal_device;

import android.content.Intent;
import android.graphics.Color;
//import android.support.v4.content.res.FontResourcesParserCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class patient_status_parent extends AppCompatActivity {
    Button returnHome;
    TextView statusText;
    String patientID;
    String status;
    String accountUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_status_parent);

        returnHome = (Button) findViewById(R.id.button8);
        statusText = (TextView) findViewById(R.id.StatusText);
        Bundle bundle = getIntent().getExtras();

        patientID = bundle.getString("patientID");
        status = bundle.getString("patientStatus");
        accountUser = bundle.getString("parentUser");

        if(status.equals("")){
            statusText.setText("No data has been released." + '\n' + "Please see you child's music therapist for" + '\n' + "more information.") ;
            statusText.setTextColor(Color.WHITE);
        }else if(status.equals("Below Average")){
            statusText.setText("Your child's sucking ability is within" + '\n' + "the below average range.") ;
            statusText.setTextColor(Color.WHITE);
        }else if(status.equals("Average")){
            statusText.setText("Your child's sucking ability is within" + '\n' + "the average range.") ;
            statusText.setTextColor(Color.WHITE);
        }else if(status.equals("Above Average")){
            statusText.setText("Your child's sucking ability is proficient" + '\n' + "enough to go home.") ;
            statusText.setTextColor(Color.WHITE);
        } else {
            statusText.setText("There is an error with the database." + '\n' + "Check again later.") ;
            statusText.setTextColor(Color.WHITE);
        }



        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(patient_status_parent.this, parent_home.class);
                Bundle bundle = new Bundle();
                bundle.putString("parentUser",accountUser);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }
}
