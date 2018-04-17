package com.senior_design.pal_device;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.List;
import java.util.Map;

public class viewStatistics2 extends AppCompatActivity {
    Button home, select;
    ListView list;
    public HashMap<String, Patient_DB> patients_DB;
    public HashMap<String, Statistic_DB> statistic_DB;
    ArrayAdapter<String> adapter;
    List<String> itemList;
    String accountUser;
    String patient;
    String stat,round, type;
    Patient_DB selectedPatient;
    TextView patName, patHospID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_statistics2);

        home = (Button) findViewById(R.id.home);
        select = (Button) findViewById(R.id.select);
        list = (ListView) findViewById(R.id.listView);
        patName = (TextView) findViewById(R.id.name);
        patHospID = (TextView) findViewById(R.id.hosid);

        itemList = new ArrayList<String>();

        patients_DB = new HashMap<String, Patient_DB>();
        statistic_DB = new HashMap<String,Statistic_DB>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        Bundle bundle = getIntent().getExtras();
        accountUser = bundle.getString("user");
        patient = bundle.getString("patient");
        type = bundle.getString("type");

        selectedPatient = patients_DB.get(patient);



        adapter = new ArrayAdapter<String>(viewStatistics2.this, android.R.layout.simple_list_item_1, itemList);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) list.getItemAtPosition(position);
                String[] lines = selectedItem.split("\\r?\\n");
                round = lines[0];
                round = round.replace("Round: ", "");
                stat = lines[1];
                stat = stat.replace("Status: ", "");

            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stat != null) {

                    Intent intent = new Intent(viewStatistics2.this, viewStatistics3.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user", accountUser);
                    bundle.putString("patient", patient);
                    bundle.putString("round", round);
                    bundle.putString("type", type);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(viewStatistics2.this);
                    dlgAlert.setMessage("You must select a round in order to view the statistics.");
                    dlgAlert.setTitle("No Selection Made");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();

                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                }
            }
        });






        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("Music Therapist")) {
                    Intent intent = new Intent(viewStatistics2.this, music_therapist_home.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("user", accountUser);
                    bundle1.putString("type","Music Therapist");
                    intent.putExtras(bundle1);
                    startActivity(intent);
                }
                if(type.equals("Doctor")){
                    Intent intent = new Intent(viewStatistics2.this, physician_home.class);
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
                    patName.setText(patient_db.fname + " " + patient_db.lname);
                    patName.setTextColor(Color.WHITE);

                    patHospID.setText(patient_db.hospitalID);
                    patHospID.setTextColor(Color.WHITE);
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
                String round =(String) dataSnapshot.child("Round").getValue();
                String status = (String) dataSnapshot.child("Result").getValue();
                String released = (String) dataSnapshot.child("ReleasedToParent").getValue();
                Map<String,Data_DB> data_db = (Map<String,Data_DB>)dataSnapshot.child("Data").getValue();

                Statistic_DB stats = new Statistic_DB(date, graph, palID, inpatient, round, data_db, status, released);

                    String temp = "Round: " + stats.Round + "\nStatus: " + stats.Result;
                    adapter.add(temp);



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
