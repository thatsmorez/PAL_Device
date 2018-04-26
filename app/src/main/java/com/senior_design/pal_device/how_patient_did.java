package com.senior_design.pal_device;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class how_patient_did extends AppCompatActivity {
    Button finishSession;
    RadioGroup radioGroup;
    String patientID,accountUser, round;
    HashMap<String, Statistic_DB> stats_DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_patient_did);

        finishSession = (Button) findViewById(R.id.done);
        radioGroup = (RadioGroup) findViewById(R.id.group);

        Bundle bundle = getIntent().getExtras();
        patientID = bundle.getString("patient");
        accountUser = bundle.getString("user");
        round = bundle.getString("round");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        stats_DB = new HashMap<String, Statistic_DB>();

        finishSession.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    //Invalid Patient ID has been entered
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(how_patient_did.this);
                    dlgAlert.setMessage("Please Select a Radio Button");
                    dlgAlert.setTitle("Invalid Selection");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();

                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                } else {

                    int id = radioGroup.getCheckedRadioButtonId();
                    if(id == 2131165323) {
                        //Above Average
                        myRef.child("Statistics").child(patientID).child(round).child("Result").setValue("Above Average");
                    }
                    if(id == 2131165324){
                        //Average
                        myRef.child("Statistics").child(patientID).child(round).child("Result").setValue("Average");
                    }
                    if(id == 2131165325){
                        //Below Average
                        myRef.child("Statistics").child(patientID).child(round).child("Result").setValue("Below Average");
                    }

                    Intent intent = new Intent(how_patient_did.this, music_therapist_home.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user", accountUser);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        ChildEventListener childEventListener = myRef.child("Statistics").child(patientID).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                String date = (String) dataSnapshot.child("Date").getValue();
                String graph = (String) dataSnapshot.child("Graph").getValue();
                String palID = (String) dataSnapshot.child("PalID").getValue();
                String patient = (String) dataSnapshot.child("PatientID").getValue();
                String round = (String) dataSnapshot.child("Round").getValue();
                String status = (String) dataSnapshot.child("Status").getValue();
                String released = (String) dataSnapshot.child("ReleasedToParent").getValue();

                Map<String,Data_DB> data_db = (Map<String,Data_DB>)dataSnapshot.child("Data").getValue();



                Statistic_DB stat = new Statistic_DB(date, graph, palID, patient, round, data_db, status, released);
                stats_DB.put(round, stat);
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
