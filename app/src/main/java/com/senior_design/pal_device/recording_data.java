package com.senior_design.pal_device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;


public class recording_data extends AppCompatActivity {
    Button returnHome, record;
    Boolean buttonPressed;
    TextView title;
    HashMap<String, String> data_DB;
    HashMap<String, Statistic_DB> stats_DB;
    HashMap<String, Patient_DB> patients_DB;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();
    String patientID, lullabyRecorded, palID, accountUser;
    Patient_DB selectedPatient;
    int roundCounter;
    String songRet;
    private static final String LOG_TAG = "Record_Log";


    //Bluetooth Stuff
    BluetoothAdapter mBluetoothAdapter;
    int REQUEST_ENABLE_BT = 1;
    private UUID PAL_UUID;
    private BluetoothDevice foundDevice;
    final int REQUEST_FINE_LOCATION_PERMISSIONS = 2;
    final int ACCESS_COARSE_LOCATION_PERMISSIONS = 3;
    private BluetoothService  mService = null;

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
        String tempUID = bundle.getString("uid");
        patientID = bundle.getString("patientID");
        lullabyRecorded = bundle.getString("lullabyRecorded");
        palID = bundle.getString("PalID");
        accountUser = bundle.getString("user");
        roundCounter = 0;



        //PAL_UUID = UUID.fromString(tempUID);

        //Initializes Bluetooth Adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            //Throwing an error message
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(recording_data.this);
            dlgAlert.setMessage("This device does not support Bluetooth. Please get a new device and try again.");
            dlgAlert.setTitle("Bluetooth Error");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();

            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(recording_data.this, music_therapist_home.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("user", accountUser);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });

        }


        if(mBluetoothAdapter.getScanMode()!= BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }

        record.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(buttonPressed == false){

                    title.setText("PAL is Starting Up");
                    returnHome.setVisibility(View.GONE);
                    record.setText("Starting Up");

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


                    //Enables the Bluetooth
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }


                    // ActivityCompat.requestPermissions(recording_data.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    //  REQUEST_FINE_LOCATION_PERMISSIONS);
                    ActivityCompat.requestPermissions(recording_data.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            ACCESS_COARSE_LOCATION_PERMISSIONS);





                    //Prelimitary Data
                    //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    //LocalDateTime now = LocalDateTime.now();
                    //String date = dtf.format(now);

                    //String graphPath = "null";

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




    //Bluetooth Methods

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                System.out.println("*******************************");
                System.out.println("deviceName: " + deviceName);
                System.out.println("MAC Address: " + deviceHardwareAddress);
                if(deviceName !=  null) {
                    if (deviceName.equals("Pressure Sensor")) {
                        System.out.println("HERE!!!!!!!!!!!!!!!!");
                        BluetoothDevice device1 = mBluetoothAdapter.getRemoteDevice(deviceHardwareAddress);
                        System.out.println("Remote Device: " + device1);
                        BluetoothService fragment = new BluetoothService(mHandler, device1);
                        fragment.start();
                        fragment.connect(device);
                    }
                }
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION_PERMISSIONS: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                break;
            }
            case ACCESS_COARSE_LOCATION_PERMISSIONS: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mBluetoothAdapter.startDiscovery();
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mReceiver, filter);
                }
                break;
            }
        }
    }


    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private static Handler mHandler = new Handler() {
        @Override
        public  void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            System.out.println("STATE_Connected");
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    break;
            }
        }
    };


}