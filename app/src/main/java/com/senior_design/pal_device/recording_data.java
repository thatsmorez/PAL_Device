package com.senior_design.pal_device;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class recording_data extends AppCompatActivity {
    Button returnHome, record;
    Boolean buttonPressed;
    TextView title;
    HashMap<String, String> data_DB;
    HashMap<String, Statistic_DB> stats_DB;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();
    String patientID, uid, lullabyRecorded, palID;
    int roundCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_data);

        returnHome = (Button) findViewById(R.id.home);
        record = (Button) findViewById(R.id.record);
        buttonPressed = false;
        title = (TextView) findViewById(R.id.title);

        stats_DB = new HashMap<String, Statistic_DB>();

        Bundle bundle = getIntent().getExtras();
        uid = bundle.getString("uid");
        patientID = bundle.getString("patientID");
        lullabyRecorded = bundle.getString("lullabyRecorded");
        palID = bundle.getString("PalID");
        //round = 0;

        record.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(buttonPressed == false){
                    //First time the button is pressed
                    //Change the text of the button and remove the "Return Home" button
                    title.setText("PAL is Recording");
                    returnHome.setVisibility(View.GONE);
                    record.setText("Stop Recording");

                    //Establish connection with bluetooth


                    //Prelimitary Data
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();;
                    String date = dtf.format(now);

                    String graphPath = "null";

                    //Record data and send it to server




                }
                if(buttonPressed == true){
                    //Second time the button is pressed ==> Move to the next page
                    //Disconnect from the Bluetooth


                    //Move to the third page of the sequence
                   // Intent intent = new Intent(recording_data.this, completeRecording.class);
                    //startActivity(intent);
                }
            }
        });

        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(recording_data.this, music_therapist_home.class);
                startActivity(intent);
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

                data_DB = new HashMap<String, String>();
                ChildEventListener childEventListener = myRef.child("Statistics").child(patientID).child(round).child("Data").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot1, String prevChildKey1){
                        String time = (String) dataSnapshot1.getKey();
                        String pressure = (String) dataSnapshot1.child(time).getValue();

                        data_DB.put(time, pressure);
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



                Statistic_DB stat = new Statistic_DB(date, graph, palID, patient, round, data_DB, status, released );
                stats_DB.put(round, stat);
                roundCounter++;
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
