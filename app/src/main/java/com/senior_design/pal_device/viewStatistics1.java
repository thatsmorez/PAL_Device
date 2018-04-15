package com.senior_design.pal_device;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class viewStatistics1 extends AppCompatActivity {
    Button home, select;
    ListView list;
    public HashMap<String, Patient_DB> patients_DB;
    ArrayAdapter<String> adapter;
    List<String> itemList;
    String accountUser, type;
    Patient_DB selectedPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_statistics1);

        home = (Button) findViewById(R.id.home);
        select = (Button) findViewById(R.id.select);
        list = (ListView) findViewById(R.id.listView);
        itemList = new ArrayList<String>();

        patients_DB = new HashMap<String, Patient_DB>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        Bundle bundle = getIntent().getExtras();
        accountUser = bundle.getString("user");
        type = bundle.getString("type");

        adapter = new ArrayAdapter<String>(viewStatistics1.this, android.R.layout.simple_list_item_1, itemList);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) list.getItemAtPosition(position);
                String[] lines = selectedItem.split("\\r?\\n");
                String hospitalident = lines[1];
                hospitalident = hospitalident.replace("Hospital I.D.: ", "");

                selectedPatient = patients_DB.get(hospitalident);

            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedPatient != null) {
                    Intent intent = new Intent(viewStatistics1.this, viewStatistics2.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user", accountUser);
                    bundle.putString("patient", selectedPatient.hospitalID);
                    bundle.putString("type", type);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(viewStatistics1.this);
                    dlgAlert.setMessage("You must select a patient before moving on to the next page.");
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
                    Intent intent = new Intent(viewStatistics1.this, music_therapist_home.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("user", accountUser);
                    bundle1.putString("type","Music Therapist");
                    intent.putExtras(bundle1);
                    startActivity(intent);
                }
                if(type.equals("Doctor")){
                    Intent intent = new Intent(viewStatistics1.this, physician_home.class);
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

                System.out.println(musicTherapist + "    " + accountUser);
                if(musicTherapist.equals(accountUser) || doctor.equals(accountUser)){
                    String temp = "Name: " + patient_db.lname + ", " + patient_db.fname + "\nHospital I.D.: " + patient_db.hospitalID;
                    //itemList.add(temp);
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
