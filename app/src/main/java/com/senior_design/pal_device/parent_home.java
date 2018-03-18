package com.senior_design.pal_device;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class parent_home extends AppCompatActivity {
    Button statistic, help, logout;
    public HashMap<String, Patient_DB> patients_DB;
    String accountUser;
    Patient_DB patient;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_home);

        statistic = (Button) findViewById(R.id.button7);
        help = (Button) findViewById(R.id.button2);
        logout = (Button) findViewById(R.id.button1);

        Bundle bundle = getIntent().getExtras();

        accountUser = bundle.getString("parentUser");

        patients_DB = new HashMap<String, Patient_DB>();

        statistic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_home.this, patient_status_parent.class);
                Bundle bundle = new Bundle();
                System.out.println("**************************");
                System.out.println(patient.hospitalID);
                System.out.println(patient.currentStatus);
                System.out.println("**************************");
                bundle.putString("parentUser",accountUser);
                bundle.putString("patientID", patient.hospitalID);
                bundle.putString("patientStatus", patient.currentStatus);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_home.this, parent_help.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent_home.this, login.class);
                startActivity(intent);
            }
        });


        ChildEventListener childEventListener = myRef.child("Patient").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                String currentStatus = (String) dataSnapshot.child("CurrentStatus").getValue();
                String fname = (String) dataSnapshot.child("FirstName").getValue();
                String hospitalID = (String) dataSnapshot.child("HospitalID").getValue();
                String lname = (String) dataSnapshot.child("LastName").getValue();
                String lullabyRecorded = (String) dataSnapshot.child("LullabyRecorded").getValue();
                String palID = (String) dataSnapshot.child("PalID").getValue();
                String parentAccountCreated = (String) dataSnapshot.child("ParentAccountCreated").getValue();
                String parentAccount = (String) dataSnapshot.child("ParentAccount").getValue();
                String musicTherapist = (String) dataSnapshot.child("musicTherapist").getValue();
                String doctor = (String) dataSnapshot.child("Doctor").getValue();
                Patient_DB patient_db = new Patient_DB(currentStatus, fname, hospitalID, lname, lullabyRecorded,  palID,  parentAccountCreated, parentAccount,musicTherapist,doctor);
                patients_DB.put(hospitalID, patient_db);

                System.out.println("**************************");
                System.out.println(patient_db.parentAccount);
                System.out.println(accountUser);

                if(patient_db.parentAccount.equals(accountUser)) {
                    patient = patient_db;

                    System.out.println("**************************");
                    System.out.println("here");
                    System.out.println("**************************");

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}
