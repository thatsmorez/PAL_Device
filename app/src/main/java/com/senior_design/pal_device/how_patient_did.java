package com.senior_design.pal_device;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class how_patient_did extends AppCompatActivity {
    Button finishSession;
    RadioGroup radioGroup;
    String patientID,accountUser, round;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_patient_did);

        finishSession = (Button) findViewById(R.id.done);
        radioGroup = (RadioGroup) findViewById(R.id.group);

        Bundle bundle = getIntent().getExtras();
        patientID = bundle.getString("patientID");
        accountUser = bundle.getString("user");
        round = bundle.getString("round");
        System.out.println("SARAH " + patientID);
        System.out.println("SARAH " + round);


        finishSession.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    //Invalid Patient ID has been entered
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(how_patient_did.this);
                    dlgAlert.setMessage("Please Select a Radio Button");
                    dlgAlert.setTitle("Invalid Selection");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();

                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference();
                    int id = radioGroup.getCheckedRadioButtonId();
                    if(id == 2131165323) {
                        //Above Average
                        myRef.child("Statistics").child(patientID).child(round).child("Result").setValue("Above Average");
                    }
                    if(id == 2131165324){
                        //Average
                        myRef.child("Statistics").child(patientID).child(round).child("Result").setValue("Average");
                    }
                    if(id == 2131165325){
                        //Below Average
                        myRef.child("Statistics").child(patientID).child(round).child("Result").setValue("Below Average");
                    }

                    Intent intent = new Intent(how_patient_did.this, music_therapist_home.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user", accountUser);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

    }
}
