package com.senior_design.pal_device;

/**
 * Created by Sarah on 3/4/2018.
 */

public class PALDevice_DB {
    public String uid;
    public String palID;
    public String patient;
    public String inUse;

    public PALDevice_DB(){}

    public PALDevice_DB(String uid, String palID, String patient, String inUse) {
        this.uid = uid;
        this.palID = palID;
        this.patient = patient;
        this.inUse = inUse;
    }
}
