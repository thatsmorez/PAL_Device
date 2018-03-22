package com.senior_design.pal_device;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class recording_data extends AppCompatActivity {
    Button returnHome, record;
    Boolean buttonPressed;
    TextView title;
    HashMap<String, String> data_DB;
    HashMap<String, Statistic_DB> stats_DB;
    HashMap<String, Patient_DB> patients_DB;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();
    String patientID, uid, lullabyRecorded, palID, accountUser;
    Patient_DB selectedPatient;
    int roundCounter;
    String songRet;
    private static final String LOG_TAG = "Record_Log";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_data);

        returnHome = (Button) findViewById(R.id.home);
        record = (Button) findViewById(R.id.record);
        buttonPressed = false;
        title = (TextView) findViewById(R.id.title);

        stats_DB = new HashMap<String, Statistic_DB>();
        patients_DB = new HashMap<String, Patient_DB>();

        Bundle bundle = getIntent().getExtras();
        uid = bundle.getString("uid");
        patientID = bundle.getString("patientID");
        lullabyRecorded = bundle.getString("lullabyRecorded");
        palID = bundle.getString("PalID");
        accountUser = bundle.getString("user");
        roundCounter = 0;


        record.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(buttonPressed == false){
                    //First time the button is pressed
                    //Download the lullaby
                    buttonPressed = true;
                    String lullabyLocation =  songRet;
                    StorageReference mStorage= FirebaseStorage.getInstance().getReferenceFromUrl(lullabyLocation);
                    try {
                        final File localFile = load_file();
                        mStorage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                MediaPlayer mPlayer = new MediaPlayer();
                                //try {
                                  //  mPlayer.setDataSource(localFile.getAbsolutePath());
                                   // mPlayer.prepare();
                                    //mPlayer.start();
                                //} catch (IOException e) {
                                 //   Log.e(LOG_TAG, "prepare() failed");
                                 //}
                           }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.e(LOG_TAG, "Song was not loaded");
                            }
                        });

                    } catch (IOException ie) {
                        Log.e(LOG_TAG, "Invalid location for lullaby");
                    }

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
                    //Change the text of the button and remove the "Return Home" button
                    title.setText("PAL is Recording");
                    returnHome.setVisibility(View.GONE);
                    record.setText("Stop Recording");

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
                Bundle bundle = new Bundle();
                bundle.putString("user", accountUser);
                intent.putExtras(bundle);
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

        ChildEventListener childEventListener1 = myRef.child("Patient").addChildEventListener(new ChildEventListener() {

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

                if(patient_db.hospitalID.equals(patientID)){
                    selectedPatient = patient_db;
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

        ChildEventListener childEventListener2 = myRef.child("Lullaby").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                if(dataSnapshot.getKey().equals(selectedPatient.hospitalID) && selectedPatient.lullabyRecorded.equals("Yes")) {
                    songRet = (String) dataSnapshot.child("path").getValue();
                }
                if(dataSnapshot.getKey().equals("default") && selectedPatient.lullabyRecorded.equals("No")){

                    songRet = (String) dataSnapshot.child("path").getValue();
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



    public File load_file() throws IOException{
        return File.createTempFile("lullaby", ".m4a");
    }
}
