package com.senior_design.pal_device;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class viewStatistics3 extends AppCompatActivity {
    Button home;
    ListView list;
    public HashMap<String, Patient_DB> patients_DB;
    public HashMap<String, Statistic_DB> statistic_DB;
    ArrayAdapter<String> adapter;
    List<String> itemList;
    String accountUser;
    String patient;
    String inputRound, type;
    Patient_DB selectedPatient;
    TextView patNametxt, patHospIDtxt, roundtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_statistics3);

        home = (Button) findViewById(R.id.home);
        list = (ListView) findViewById(R.id.listView);
        patNametxt = (TextView) findViewById(R.id.name);
        patHospIDtxt = (TextView) findViewById(R.id.hosid);
        roundtxt = (TextView) findViewById(R.id.round);

        itemList = new ArrayList<String>();

        patients_DB = new HashMap<String, Patient_DB>();
        statistic_DB = new HashMap<String,Statistic_DB>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        final Bundle bundle = getIntent().getExtras();
        accountUser = bundle.getString("user");
        patient = bundle.getString("patient");
        inputRound = bundle.getString("round");
        type = bundle.getString("type");

        adapter = new ArrayAdapter<String>(viewStatistics3.this, android.R.layout.simple_list_item_1, itemList);
        list.setAdapter(adapter);


        roundtxt.setText("Round: " + inputRound);
        roundtxt.setTextColor(Color.WHITE);

        selectedPatient = patients_DB.get(patient);



        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("Music Therapist")) {
                    Intent intent = new Intent(viewStatistics3.this, music_therapist_home.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("user", accountUser);
                    bundle1.putString("type","Music Therapist");
                    intent.putExtras(bundle1);
                    startActivity(intent);
                }
                if(type.equals("Doctor")){
                    Intent intent = new Intent(viewStatistics3.this, physician_home.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("user", accountUser);
                    bundle1.putString("type","Doctor");
                    intent.putExtras(bundle1);
                    startActivity(intent);
                }
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
                Patient_DB patient_db = new Patient_DB(currentStatus, fname, hospitalID, lname, lullabyRecorded,  palID,  parentAccountCreated, parentAccount, musicTherapist, doctor);
                patients_DB.put(hospitalID, patient_db);

                if(patient_db.hospitalID.equals(patient)){
                    patNametxt.setText(patient_db.fname + " " + patient_db.lname);
                    patNametxt.setTextColor(Color.WHITE);

                    patHospIDtxt.setText(patient_db.hospitalID);
                    patHospIDtxt.setTextColor(Color.WHITE);
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

        ChildEventListener childEventListener1 = myRef.child("Statistics").child(patient).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String prevChildKey) {
                String date = (String) dataSnapshot.child("Date").getValue();
                String graph = (String) dataSnapshot.child("Graph").getValue();
                String palID = (String) dataSnapshot.child("PalID").getValue();
                String inpatient = (String) dataSnapshot.child("PatientID").getValue();
                final String round =(String) dataSnapshot.child("Round").getValue();
                String status = (String) dataSnapshot.child("Result").getValue();
                String released = (String) dataSnapshot.child("ReleasedToParent").getValue();
                Map<String,Data_DB> data_db = (Map<String,Data_DB>)dataSnapshot.child("Data").getValue();

                System.out.println("SARAH: " + data_db.toString());

                Statistic_DB stats = new Statistic_DB(date, graph, palID, inpatient, round, data_db, status, released);

                if(round.equals(inputRound)) {
                    Set temp = data_db.entrySet();
                    Iterator iter = temp.iterator();

                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        String in = "Time: " + entry.getKey() + "\nValue: " + entry.getValue();
                        System.out.println("SARAH: " + in);
                        adapter.add(in);
                        System.out.println("SARAH: " + entry.getKey() + " -- " + entry.getValue());
                    }
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
