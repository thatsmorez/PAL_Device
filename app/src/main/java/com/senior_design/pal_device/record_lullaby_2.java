package com.senior_design.pal_device;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class record_lullaby_2 extends AppCompatActivity {
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private static String mFileName = null;
    HashMap<String, Patient_DB> patients_DB;
    Button accept, recordAgain, listen;
    TextView patString, lullabyString;
    private static final String LOG_TAG = "Record_Log";
    MediaPlayer mPlayer;
    String patientID, nameOfFile, accountUser;
    StorageReference filepathStorage;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_lullaby_2);

        mStorage = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);
        accept = (Button) findViewById(R.id.accept);
        recordAgain = (Button) findViewById(R.id.recordAgain);
        listen = (Button) findViewById(R.id.listen);
        patString = (TextView) findViewById(R.id.PatientID);
        lullabyString = (TextView) findViewById(R.id.LullabyName);


        patients_DB = new HashMap<String, Patient_DB>();
        Bundle bundle = getIntent().getExtras();

        nameOfFile = bundle.getString("fileName");
        patientID = bundle.getString("patientID");
        mFileName = bundle.getString("filePath");
        accountUser = bundle.getString("user");

        patString.setText("Patient I.D.: " + patientID);
        lullabyString.setText("Lullaby Name: " + nameOfFile);

        recordAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(record_lullaby_2.this, record_lullaby_1.class);
                Bundle bundle = new Bundle();
                bundle.putString("user", accountUser);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(mFileName);
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "prepare() failed");
                }
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAudio();
                updatePatient();
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(record_lullaby_2.this);
                dlgAlert.setMessage("Lullaby uploaded successfully!");
                dlgAlert.setTitle("Complete");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                Intent intent = new Intent(record_lullaby_2.this, music_therapist_home.class);
                Bundle bundle = new Bundle();
                bundle.putString("user", accountUser);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
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
                System.out.println("patients  " + patients_DB);
                System.out.println(hospitalID);
                System.out.println(patient_db);

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

    }


    public void uploadAudio(){
        mProgress.setMessage("Uploading Audio ...");
        mProgress.show();
        //First child needs to be the patient ID and the second child needs to be the name of the file
        filepathStorage = mStorage.child(patientID).child(nameOfFile);
        Uri uri = Uri.fromFile(new File(mFileName));
        filepathStorage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               mProgress.dismiss();
           }
        });
    }

    public void updatePatient() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("Patient").child(patientID).child("LullabyRecorded").setValue("Yes");
        myRef.child("Lullaby").child(patientID).child("path").setValue(filepathStorage.toString());
    }


}
