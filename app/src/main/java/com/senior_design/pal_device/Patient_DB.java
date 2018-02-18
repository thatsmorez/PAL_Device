package com.senior_design.pal_device;

/**
 * Created by Sarah on 2/14/2018.
 */

public class Patient_DB {
    public String currentStatus;
    public String fname;
    public String hospitalID;
    public String lname;
    public String lullabyRecorded;
    public String palID;
    public String parentAccountCreated;
    public String parentAccount;

    public Patient_DB(String currentStatus, String fname, String hospitalID, String lname, String lullabyRecorded, String palID, String parentAccountCreated, String parentAccount) {
        this.currentStatus = currentStatus;
        this.fname = fname;
        this.hospitalID = hospitalID;
        this.lname = lname;
        this.lullabyRecorded = lullabyRecorded;
        this.palID = palID;
        this.parentAccountCreated = parentAccountCreated;
        this.parentAccount = parentAccount;
    }


}
