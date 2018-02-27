package com.senior_design.pal_device;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import android.Manifest;

public class record_lullaby_1 extends AppCompatActivity {
    Button returnHome;
    Button record;
    Button stop;
    EditText patientID;
    EditText lullabyName;
    private MediaRecorder mRecorder = null;
    private static String mFileName = null;
    private static final String LOG_TAG = "Record_Log";
    boolean mStartRecording;
    public HashMap<String, Patient_DB> patients_DB;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_lullaby_1);

        returnHome = (Button) findViewById(R.id.button8);
        record = (Button) findViewById(R.id.button9);
        stop = (Button) findViewById(R.id.button10);
        patientID = (EditText) findViewById(R.id.editText1);
        lullabyName = (EditText) findViewById(R.id.editText2);
        mStartRecording = true;
        patients_DB = new HashMap<String, Patient_DB>();

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();


        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p = patientID.getText().toString();
                String l = lullabyName.getText().toString();
                if(patients_DB.get(p) != null) {
                    if(patients_DB.get(p).hospitalID.equals(p.toString())) {
                        if(l != null) {
                                //No lullaby exists of the patient yet
                                if (mStartRecording) {
                                    //Create file path name
                                    String temp = l.replace(" ", "_");
                                    mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                                    mFileName += "/" + temp + ".3gp";
                                    System.out.println("**************************************");
                                    System.out.println(mFileName);
                                    startRecording();
                                    record.setText("NOW RECORDING");
                                    mStartRecording = false;
                            }
                        } else {
                            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(record_lullaby_1.this);
                            dlgAlert.setMessage("Lullaby Name is empty. Please enter in a valid lullaby name.");
                            dlgAlert.setTitle("Invalid Lullaby Name");
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
                } else {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(record_lullaby_1.this);
                    dlgAlert.setMessage("Invalid Patient ID. Please check and try again.");
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
            }
            });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mStartRecording){
                    stopRecording();
                    mStartRecording = !mStartRecording;

                    //Go to the next page.
                    Intent intent = new Intent(record_lullaby_1.this, record_lullaby_2.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("filePath", mFileName);
                    bundle.putString("patientID", patientID.getText().toString());
                    String l = lullabyName.getText().toString();
                    String temp = l.replace(" ", "_");
                    temp += ".3gp";
                    bundle.putString("fileName", temp);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(record_lullaby_1.this, music_therapist_home.class);
                startActivity(intent);
            }
        });

        //Reads in the patient Database while the user is entering in the information
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
        
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }
}
