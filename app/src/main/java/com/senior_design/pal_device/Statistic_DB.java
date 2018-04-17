package com.senior_design.pal_device;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sarah on 3/5/2018.
 */

public class Statistic_DB {
    public String Date;
    public String Graph;
    public String PalID;
    public String PatientID;
    public String Round;
    public String Result;
    public String ReleasedToParent;
    public Map<String, Data_DB> Data;

    public Statistic_DB(){}

    public Statistic_DB(String Date, String Graph, String PalID, String patient, String round, Map<String, Data_DB> data, String status, String released ) {
        this.Date = Date;
        this.Graph = Graph;
        this.PalID = PalID;
        this.PatientID = patient;
        this.Round = round;
        this.Data= data;
        this.Result = status;
        this.ReleasedToParent = released;
    }
}
