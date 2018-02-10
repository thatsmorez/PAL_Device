package com.senior_design.pal_device;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class login extends AppCompatActivity {
    Button b1;
    EditText ed1, ed2;
    private FirebaseAuth mAuth;
    HashMap<String, Login_DB> loginInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        b1 = (Button) findViewById(R.id.button);
        ed1 = (EditText) findViewById(R.id.editText1);
        ed2 = (EditText) findViewById(R.id.editText2);

        mAuth = FirebaseAuth.getInstance();
        loginInformation = new HashMap<String, Login_DB>();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mRef = database.getReference("Login");

                final ChildEventListener childEventListener = mRef.orderByChild("Username").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        String username = (String) dataSnapshot.child("Username").getValue();
                        String password = (String) dataSnapshot.child("Password").getValue();
                        String level = (String) dataSnapshot.child("Access Level").getValue();
                        Login_DB user = new Login_DB(username, password, level);
                        System.out.println(dataSnapshot.getKey() + " " + user.username + " " + user.password + " " + user.access);
                        loginInformation.put(user.username, user);

                        String usern = ed1.getText().toString();
                        String pass = ed2.getText().toString();
                        if (password.equals(pass) && username.equals(usern)) {
                            System.out.println("*********************************");
                            String access = loginInformation.get(usern).access;
                            if (access.equals("Parent")) {
                                Intent intent = new Intent(login.this, parent_home.class);
                                startActivity(intent);
                            }
                            if (access.equals("MusicTherapist")) {
                                Intent intent = new Intent(login.this, music_therapist_home.class);
                                startActivity(intent);
                            }
                            if (access.equals("Physician")) {
                                Intent intent = new Intent(login.this, physician_home.class);
                                startActivity(intent);
                            }
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

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

}

