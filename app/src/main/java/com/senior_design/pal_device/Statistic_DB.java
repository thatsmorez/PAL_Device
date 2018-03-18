package com.senior_design.pal_device;

import java.util.HashMap;

/**
 * Created by Sarah on 3/5/2018.
 */

public class Statistic_DB {
    public String date;
    public String graph;
    public String palID;
    public String patient;
    public String round;
    public String status;
    public String released;
    public HashMap<String, String>  data;

    public Statistic_DB(){}

    public Statistic_DB(String date, String graph, String palID, String patient, String round, HashMap<String, String> data, String status, String released ) {
        this.date = date;
        this.graph = graph;
        this.palID = palID;
        this.patient = patient;
        this.round = round;
        this.data= data;
        this.status = status;
        this.released = released;
    }
}
