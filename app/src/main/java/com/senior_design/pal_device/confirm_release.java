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

public class confirm_release extends AppCompatActivity {
    Button home, select;
    ListView list;
    public HashMap<String, Patient_DB> patients_DB;
    public HashMap<String, Statistic_DB> statistic_DB;
    ArrayAdapter<String> adapter;
    List<String> itemList;
    String accountUser;
    String patient;
    String stat,round;
    Patient_DB selectedPatient;
    TextView patName, patHospID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_release);

        home = (Button) findViewById(R.id.home);
        select = (Button) findViewById(R.id.select);
        list = (ListView) findViewById(R.id.listView);
        patName = (TextView) findViewById(R.id.name);
        patHospID = (TextView) findViewById(R.id.hosid);

        itemList = new ArrayList<String>();

        patients_DB = new HashMap<String, Patient_DB>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        Bundle bundle = getIntent().getExtras();
        accountUser = bundle.getString("user");
        patient = bundle.getString("patient");

        selectedPatient = patients_DB.get(patient);



        adapter = new ArrayAdapter<String>(confirm_release.this, android.R.layout.simple_list_item_1, itemList);
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
                    myRef.child("Patient").child(patient).child("CurrentStatus").setValue(stat);
                    myRef.child("Statistics").child(patient).child(round).child("ReleasedToParent").setValue("Yes");

                    Intent intent = new Intent(confirm_release.this, music_therapist_home.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user", accountUser);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(confirm_release.this);
                    dlgAlert.setMessage("You must select a statistic in order to release it.");
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
                Intent intent = new Intent(confirm_release.this, music_therapist_home.class);
                Bundle bundle = new Bundle();
                bundle.putString("user", accountUser);
                intent.putExtras(bundle);
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
                final HashMap<String, String> data_db = new HashMap<String, String>();
                myRef.child("Statistics").child(patient).child("Data").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot1, String prevChildKey1){
                        String time = (String) dataSnapshot1.getKey();
                        String data = (String) dataSnapshot1.child(time).getValue();
                        data_db.put(time,data);

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

               Statistic_DB stats = new Statistic_DB(date, graph, palID, inpatient, round, data_db, status, released);

               if(stats.released.equals("No")) {
                   String temp = "Round: " + stats.round + "\nStatus: " + stats.status;
                   adapter.add(temp);
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
