package com.senior_design.pal_device;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class associate_pal extends AppCompatActivity {
    Button returnHome, associatePAL;
    EditText patientID, palID;
    HashMap<String, Patient_DB> patients_DB;
    HashMap<String, PALDevice_DB> palDevice_DB;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_associate_pal);

        returnHome = (Button) findViewById(R.id.home);
        associatePAL = (Button) findViewById(R.id.associate);
        patientID = (EditText) findViewById(R.id.editText1);
        palID = (EditText) findViewById(R.id.editText2);
        patients_DB = new HashMap<String, Patient_DB>();
        palDevice_DB = new HashMap<String, PALDevice_DB>();

        associatePAL.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(patientID.getText().toString() != null && palID.getText().toString() != null){
                    //Now validate that the inputted info is in database
                    if(palDevice_DB.get(palID.getText().toString()) != null){
                        if(palDevice_DB.get(patientID.getText().toString()) != null){
                            PALDevice_DB pal = palDevice_DB.get(palID.getText().toString());
                            Patient_DB patient = patients_DB.get(patientID.getText().toString());

                            myRef.child("Patient").child(patientID.getText().toString()).child("PalID").setValue(palID.getText().toString());
                            myRef.child("Bluetooth").child(palID.getText().toString()).child("inUse").setValue("Yes");
                            myRef.child("Bluetooth").child(palID.getText().toString()).child("Patient").setValue(patientID.getText().toString());

                            //Go to the next page.
                            Intent intent = new Intent(associate_pal.this, recording_data.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("uid", pal.uid);
                            bundle.putString("patientID", patient.hospitalID);
                            bundle.putString("lullabyRecorded", patient.lullabyRecorded);
                            bundle.putString("PalID", patient.palID);
                            intent.putExtras(bundle);
                            startActivity(intent);

                        }else{
                            //Invalid Patient ID has been entered
                            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(associate_pal.this);
                            dlgAlert.setMessage("An invalid Patient ID has been entered. Please enter a valid Patient ID and try again.");
                            dlgAlert.setTitle("Invalid Patient ID");
                            dlgAlert.setPositiveButton("OK", null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();

                            dlgAlert.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                        }
                    }else{
                        //Invalid PAL ID has been entered
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(associate_pal.this);
                        dlgAlert.setMessage("An invalid PAL ID has been entered. Please enter a valid PAL ID and try again.");
                        dlgAlert.setTitle("Invalid PAL ID");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();

                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                    }
                }else {
                    //One of the fields didn't get filled out. Throw error...
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(associate_pal.this);
                    dlgAlert.setMessage("Please fill out both the Patient ID and PAL ID fields.");
                    dlgAlert.setTitle("Invalid Information");
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

        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(associate_pal.this, music_therapist_home.class);
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
                Patient_DB patient_db = new Patient_DB(currentStatus, fname, hospitalID, lname, lullabyRecorded,  palID,  parentAccountCreated, parentAccount);
                patients_DB.put(hospitalID, patient_db);
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

        ChildEventListener childEventListener1 = myRef.child("Bluetooth").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                String uid = (String) dataSnapshot.child("UID").getValue();
                String inUse = (String) dataSnapshot.child("inUse").getValue();
                String palID = (String) dataSnapshot.child("palID").getValue();
                String patient = (String) dataSnapshot.child("patient").getValue();
                PALDevice_DB paldevice_db = new PALDevice_DB(uid, inUse, palID, patient);
                palDevice_DB.put(palID, paldevice_db);
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
