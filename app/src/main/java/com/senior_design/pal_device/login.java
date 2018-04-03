package com.senior_design.pal_device;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class login extends AppCompatActivity {
    Button b1;
    EditText ed1, ed2;
    private FirebaseAuth mAuth;
    public HashMap<String, Login_DB> loginInformation;
    public DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        b1 = (Button) findViewById(R.id.button);
        ed1 = (EditText) findViewById(R.id.editText1);
        ed2 = (EditText) findViewById(R.id.editText2);

        //FirebaseApp.initializeApp(this.getApplicationContext());

        loginInformation = new HashMap<String, Login_DB>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Login");

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usern = ed1.getText().toString();
                String pass = ed2.getText().toString();
                if(loginInformation.get(usern) != null){
                    if(loginInformation.get(usern).username.equals(usern) && loginInformation.get(usern).password.equals(pass)){
                        if(loginInformation.get(usern).access.equals("Parent")){
                            Intent intent = new Intent(login.this, parent_home.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("parentUser", usern);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } if(loginInformation.get(usern).access.equals("MusicTherapist")){
                            Intent intent = new Intent(login.this, music_therapist_home.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("user", usern);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } if(loginInformation.get(usern).access.equals("Physician")){
                            Intent intent = new Intent(login.this, physician_home.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("user", usern);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                } else {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(login.this);
                    dlgAlert.setMessage("Invalid Username or Password.");
                    dlgAlert.setTitle("Error Message...");
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



        final ChildEventListener childEventListener = mRef.orderByChild("Username").addChildEventListener(new ChildEventListener() {

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

