package com.senior_design.pal_device;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Random;

public class create_parent_account extends AppCompatActivity {
    Button returnHome, createAccount;
    EditText email, patient;
    private FirebaseAuth mAuth;
    public HashMap<String, Patient_DB> patients_DB;
    public HashMap<String, Login_DB> loginInformation;
    String accountUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_parent_account);


        returnHome = (Button) findViewById(R.id.button8);
        createAccount = (Button) findViewById(R.id.button9);

        Bundle bundle = getIntent().getExtras();
        accountUser = bundle.getString("user");

        mAuth = FirebaseAuth.getInstance();
        patients_DB = new HashMap<String, Patient_DB>();
        loginInformation = new HashMap<String, Login_DB>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(create_parent_account.this, music_therapist_home.class);
                Bundle bundle = new Bundle();
                bundle.putString("user", accountUser);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send patient I.D.  and email to server
                patient = (EditText) findViewById(R.id.editText1);
                email = (EditText) findViewById(R.id.editText2);

                String p = patient.getText().toString();
                String e = email.getText().toString();

                    if(patients_DB.get(p) != null){
                        if(patients_DB.get(p).hospitalID.equals(p.toString()) && patients_DB.get(p).parentAccountCreated.equals("No")){
                            //Generates a new password and displays it to the screen
                            String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                            String Small_chars = "abcdefghijklmnopqrstuvwxyz";
                            String numbers = "0123456789";
                            String symbols = "!@#$%^&*_=+-/.?<>)";
                            int len = 7;

                            String values = Capital_chars + Small_chars + numbers + symbols;

                            // Using random method
                            Random random = new Random();

                            char[] password = new char[len];

                            for (int i = 0; i < len; i++) {
                                password[i] = values.charAt(random.nextInt(values.length()));
                            }

                            String finalPass = password.toString();

                            //Parses the information before the @ sign for the username
                            int posA = e.indexOf('@');

                            String finalUser = e.substring(0, posA);

                            //pushes the new parent account to the server
                            Login_DB temp = new Login_DB(finalUser, finalPass, "Parent");

                            loginInformation.put(finalUser, temp);
                            myRef.child("Login").setValue(loginInformation);

                            //Updates patient settings
                            myRef.child("Patient").child(p).child("ParentAccountCreated").setValue("Yes");
                            myRef.child("Patient").child(p).child("ParentAccount").setValue(finalUser);

                            //Displays password to screen for MT to give to parent
                            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(create_parent_account.this);
                            dlgAlert.setMessage("Parent account has been successfully created. \n Username: " + finalUser + "\nPassward: " + finalPass);
                            dlgAlert.setTitle("Account Created");
                            dlgAlert.setPositiveButton("OK", null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();

                            dlgAlert.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                            Intent intent = new Intent(create_parent_account.this, create_parent_account.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("user", accountUser);
                            intent.putExtras(bundle);
                            startActivity(intent);


                    } else {
                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(create_parent_account.this);
                        dlgAlert.setMessage("Parent account has already been created. Please contact IT to get password.");
                        dlgAlert.setTitle("Account Already Created");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();

                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                    }
                } else {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(create_parent_account.this);
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
                String musicTherapist = (String) dataSnapshot.child("musicTherapist").getValue();
                String doctor = (String) dataSnapshot.child("Doctor").getValue();
                Patient_DB patient_db = new Patient_DB(currentStatus, fname, hospitalID, lname, lullabyRecorded,  palID,  parentAccountCreated, parentAccount, musicTherapist, doctor);
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

        ChildEventListener childEventListener1 = myRef.child("Login").orderByChild("Username").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                String username = (String) dataSnapshot.child("username").getValue();
                String password = (String) dataSnapshot.child("password").getValue();
                String level = (String) dataSnapshot.child("access").getValue();
                Login_DB user = new Login_DB(username, password, level);
                System.out.println(dataSnapshot.getKey() + " " + user.username + " " + user.password + " " + user.access);
                loginInformation.put(user.username, user);
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

