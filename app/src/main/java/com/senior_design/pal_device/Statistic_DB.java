package com.senior_design.pal_device;

import java.util.HashMap;
import java.util.Map;

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
    public Map<String, Data_DB> data;

    public Statistic_DB(){}

    public Statistic_DB(String date, String graph, String palID, String patient, String round, Map<String, Data_DB> data, String status, String released ) {
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
