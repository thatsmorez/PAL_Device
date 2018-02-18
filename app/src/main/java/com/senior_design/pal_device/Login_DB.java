package com.senior_design.pal_device;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by Sarah on 2/9/2018.
 */

public class Login_DB {
    public String username;
    public String password;
    public String access;

    public Login_DB(){}

    public Login_DB(String username, String password, String access) {
        this.username = username;
        this.password = password;
        this.access = access;
    }


}
