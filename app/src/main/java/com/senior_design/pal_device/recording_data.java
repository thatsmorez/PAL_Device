package com.senior_design.pal_device;



import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.icu.util.Calendar;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class recording_data extends AppCompatActivity {
    Button returnHome, record;
    Boolean buttonPressed;
    TextView title;
    HashMap<String, Statistic_DB> stats_DB;
    HashMap<String, Patient_DB> patients_DB;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();
    String patientID, lullabyRecorded, palID, accountUser;
    Patient_DB selectedPatient;
    int roundCounter;
    String songRet;
    private static final String LOG_TAG = "Record_Log";
    long starttime;


    //Bluetooth Stuff
    private BluetoothAdapter mBluetoothAdapter;
    //final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    int REQUEST_ENABLE_BT = 1;
    private ScanSettings settings;
    BluetoothLeScanner scanner;
    private static final long SCAN_PERIOD = 10000;
    private Handler mHandler;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;

    //Parsing Data Stuff
    int timesBelowThreshold = 0;
    int timesAboveThreshold = 0;
    boolean playingMusic = false;
    final int timeBelow = 400;
    int minThreshold = 200;
    final MediaPlayer mPlayer = new MediaPlayer();
    int timeBeforePlay = 10;
    HashMap<String, Data_DB> data_DBref = new HashMap<String,Data_DB>();
    boolean firsttime = true;
    int suckCounter = 0;

    File localFile;
    private FirebaseAuth mAuth;
    StorageReference mStorage;

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

        mHandler = new Handler();

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            System.out.println("Sign in success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                           System.out.println("Sign in failure");
                        }
                    }
                });

        //PAL_UUID = UUID.fromString(tempUID);

        //Initializes Bluetooth Adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonPressed == false) {

                    title.setText("PAL is Starting Up");
                    returnHome.setVisibility(View.GONE);
                    record.setText("Starting Up");

                    //First time the button is pressed
                    //Download the lullaby
                    buttonPressed = true;
                    String lullabyLocation = songRet;
                    mStorage = FirebaseStorage.getInstance().getReferenceFromUrl(lullabyLocation);
                    try {
                            localFile = load_file();
                            mStorage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    MediaPlayer mPlayer = new MediaPlayer();
                                    try {
                                      mPlayer.setDataSource(localFile.getAbsolutePath());
                                      mPlayer.prepare();
                                      mPlayer.setLooping(true);
                                    //mPlayer.start();
                                    } catch (IOException e) {
                                       Log.e(LOG_TAG, "prepare() failed");
                                    }
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
                    record.setVisibility(View.GONE);


                    //Scan for BLE Devices
                    scanner = mBluetoothAdapter.getBluetoothLeScanner();
                    settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
                    filters = new ArrayList<ScanFilter>();
                    scanLeDevice(true);

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

                Map<String,Data_DB> data_db = (Map<String,Data_DB>)dataSnapshot.child("Data").getValue();



                Statistic_DB stat = new Statistic_DB(date, graph, palID, patient, round, data_db, status, released);
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
                Patient_DB patient_db = new Patient_DB(currentStatus, fname, hospitalID, lname, lullabyRecorded, palID, parentAccountCreated, parentAccount, musicTherapist, doctor);

                patients_DB.put(hospitalID, patient_db);

                if (patient_db.hospitalID.equals(patientID)) {
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
                if (dataSnapshot.getKey().equals(selectedPatient.hospitalID) && selectedPatient.lullabyRecorded.equals("Yes")) {
                    songRet = (String) dataSnapshot.child("path").getValue();
                }
                if (dataSnapshot.getKey().equals("default") && selectedPatient.lullabyRecorded.equals("No")) {

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


    public File load_file() throws IOException {
        return File.createTempFile("lullaby", ".m4a");
    }


    //Bluetooth Methods


    public void createNewDataDBEntry(Map<String, Data_DB> data) {

        mPlayer.stop();
        SimpleDateFormat dtf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date now = new Date();
        String date = dtf.format(now);
        String graph = "null";
        String paltemp = palID;
        String patient = patientID;
        String round = Integer.toString(roundCounter + 1);
        String status = "null";
        String released = "No";

        Statistic_DB stat = new Statistic_DB(date, graph, paltemp, patient, round, data, status, released);

        DatabaseReference usersRef =  myRef.child("Statistics").child(patientID.toString()).child(round.toString());
        usersRef.setValue(stat);

        //Move to the next page
        Intent intent = new Intent(recording_data.this, how_patient_did.class);
        Bundle bundle = new Bundle();
        bundle.putString("user", accountUser);
        bundle.putString("patient", patientID);
        bundle.putString("round", Integer.toString(roundCounter + 1));
        intent.putExtras(bundle);
        startActivity(intent);


    }



    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < 21) {
                        scanner.stopScan(mScanCallback);
                    } else {
                        scanner.stopScan(mScanCallback);

                    }
                }
            }, SCAN_PERIOD);
            if (Build.VERSION.SDK_INT < 21) {
                scanner.startScan(mScanCallback);
            } else {

                scanner.startScan(filters, settings, mScanCallback);
            }
        } else {
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                scanner.stopScan(mScanCallback);
            }
        }
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            BluetoothDevice btDevice = result.getDevice();

            if(btDevice.getName() != null){
                if(btDevice.getName().equals("Pressure Sensor")){
                    connectToDevice(btDevice);
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("onLeScan", device.toString());

                            connectToDevice(device);
                        }
                    });
                }
            };

    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
            scanLeDevice(false);// will stop after first device detection
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();

            BluetoothGattService service = services.get(2);
            BluetoothGattCharacteristic myGatChar = services.get(2).getCharacteristics().get(0);

            List<BluetoothGattDescriptor> descriptors = myGatChar.getDescriptors();

            boolean set = gatt.setCharacteristicNotification(myGatChar,true);
            BluetoothGattDescriptor descriptor = myGatChar.getDescriptor(descriptors.get(0).getUuid());
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);

            starttime = System.currentTimeMillis();
        }

        @Override
        public void onCharacteristicChanged (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
            byte[] val1 = characteristic.getValue();
            int i = unsignedShortToInt(val1);

            parseInfo(i);
            System.out.println("SARAH VALUE: " + i);
            //15 minutes is 900000 milliseconds
            //30 seconds is 30000
            //5 seconds is 5000 milliseconds
            //1 second is 1000 milliseconds
            if(System.currentTimeMillis() - starttime > 30000){
                gatt.disconnect();
                if(gatt == null){
                    return;
                }
                gatt.close();
                gatt = null;
                createNewDataDBEntry(data_DBref);
            }

        }
    };

    public static final int unsignedShortToInt(byte[] b) {
        int i = 0;
        i |= b[0] & 0xFF;
        i <<= 8;
        i |= b[1] & 0xFF;
        return i;
    }

    public void parseInfo(int input){
        if(input > minThreshold && playingMusic == false && timesAboveThreshold > timeBeforePlay){
            if(suckCounter > 3) {
                playingMusic = true;
                if (firsttime) {
                    firsttime = false;
                    mStorage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            try {
                                mPlayer.setDataSource(localFile.getAbsolutePath());
                                mPlayer.setLooping(true);
                                mPlayer.prepare();
                                mPlayer.start();
                            } catch (IOException e) {
                                System.out.println("prepare() failed");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            System.out.println("Song was not loaded");
                        }
                    });
                } else {
                    mPlayer.start();
                }
            } else {
                suckCounter++ ;
            }

        }

        //Increments the timesAboveThreshold
        //Ensures that we don't play the music for outlining data
        if(input >= minThreshold ){
            timesBelowThreshold = 0;
            timesAboveThreshold = timesAboveThreshold + 1 ;
        }


        //input from sensor is below our minThreshold
        //Ensures that we don't stop the music for outlining data
        if(input <= minThreshold ){
            timesBelowThreshold++;
        }

        //Please don't stop the music
        if(playingMusic == true && timesBelowThreshold > timeBelow && input < minThreshold){
            mPlayer.pause();
            playingMusic = false;
            timesAboveThreshold = 0;
            suckCounter = 0;
        }

        //Push data to hashmap to be pushed to the server
        Data_DB temp = new Data_DB(Integer.toString(input));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date now = new Date();
        data_DBref.put(df.format(now), temp);
    }


}