package com.example.powerautomation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendSMS extends AppCompatActivity {
    Spinner spnWaitForHour, spnWaitForMin, spnRunForHour, spnRunForMin;
    Switch swSchedule;
    ConstraintLayout scheduleContainer;
    Button powerOn, powerOff, scheduleStart;
    List<Integer> hours, minutes;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private RequestQueue queue;

    private String username, password;

    TextView tvOnOff;

    private Calendar calendar;
    private SimpleDateFormat dateFormat, timeFormat;
    private String date, time;

    LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setVisibility(View.GONE);

        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        tvOnOff = findViewById(R.id.tvOnOff);

        spnWaitForHour = findViewById(R.id.waitHour);
        spnWaitForMin = findViewById(R.id.waitMin);
        spnRunForHour = findViewById(R.id.runHour);
        spnRunForMin = findViewById(R.id.runMin);

        swSchedule = findViewById(R.id.schedule);

        scheduleContainer = findViewById(R.id.scheduleContainer);

        powerOn = findViewById(R.id.powerOn);
        powerOff = findViewById(R.id.powerOff);
        scheduleStart = findViewById(R.id.scheduleStart);

        username = sharedPreferences.getString("username", "");
        password = sharedPreferences.getString("password", "");

        scheduleStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SendSMS.this, "CLicked", Toast.LENGTH_SHORT).show();
            }
        });

        scheduleContainer.setVisibility(View.INVISIBLE);

        swSchedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(swSchedule.isChecked()) {
                    setPowerButtonsSMS(false);
                    scheduleContainer.setVisibility(View.VISIBLE);
                } else {
                    setPowerButtonsSMS(true);
                    scheduleContainer.setVisibility(View.INVISIBLE);
                }
            }
        });


        fillAdapters();

        powerOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startMotor = MyURLs.setOnOff("1", username, password);

                Log.i("checkOnOff", startMotor);


                calendar = Calendar.getInstance();
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date = dateFormat.format(calendar.getTime());
                timeFormat = new SimpleDateFormat("HH:mm:ss");
                time = timeFormat.format(calendar.getTime());

                String onSMS = "1,0,0,"+date+" "+time;
                Log.i("sendSMSOnOff", onSMS);
//                SmsManager sms=SmsManager.getDefault();
//                sms.sendTextMessage("7016286449", null, onSMS, null,null);
                sendSMS("7874377027", onSMS);

            }
        });

        powerOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String stopMotor = MyURLs.setOnOff("0", username, password);

                Log.i("checkOnOff", stopMotor);

                calendar = Calendar.getInstance();
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date = dateFormat.format(calendar.getTime());
                timeFormat = new SimpleDateFormat("HH:mm:ss");
                time = timeFormat.format(calendar.getTime());

                String onSMS = "0,0,0,"+date+" "+time;
                Log.i("sendSMSOnOff", onSMS);
//                SmsManager sms=SmsManager.getDefault();
//                sms.sendTextMessage("7016286449", null, onSMS, null,null);
                sendSMS("7874377027", onSMS);
            }
        });


        scheduleStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String waitHour = spnWaitForHour.getSelectedItem().toString();
                Log.i("scheduleOnOff-waitHour", waitHour);
                String waitMin = spnWaitForMin.getSelectedItem().toString();
                Log.i("scheduleOnOff-waitMin", waitMin);
                String runHour = spnRunForHour.getSelectedItem().toString();
                Log.i("scheduleOnOff-runHour", runHour);
                String runMin = spnRunForMin.getSelectedItem().toString();
                Log.i("scheduleOnOff-runMin", runMin);

                int waitFor = (Integer.parseInt(waitHour) * 60) + Integer.parseInt(waitMin);
                int runFor = (Integer.parseInt(runHour) * 60) + Integer.parseInt(runMin);

                Log.i("scheduleOnOff", Integer.toString(waitFor) + " " + Integer.toString(runFor));

                String startSchedule = MyURLs.setSchedule(waitFor, runFor, username, password);
                Log.i("scheduleOnOff", startSchedule);

                calendar = Calendar.getInstance();
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date = dateFormat.format(calendar.getTime());
                timeFormat = new SimpleDateFormat("HH:mm:ss");
                time = timeFormat.format(calendar.getTime());

                if(waitFor == 0){
                    String onSMS = "1,0," + runFor + ","+date+" "+time;
                    Log.i("sendSMSOnOff", onSMS);
                    sendSMS("7874377027", onSMS);
                } else {
                    String onSMS = "0," + waitFor + "," + runFor + ","+date+" "+time;
                    Log.i("sendSMSOnOff", onSMS);
                    sendSMS("7874377027", onSMS);
                }
            }
        });
    }

    private void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    private void setPowerButtonsSMS(boolean b) {
        if(b){
            //when toggle is on
            powerOn.setEnabled(true);
            powerOn.setBackgroundColor(Color.parseColor("#4CAF50"));

            powerOff.setEnabled(true);
            powerOff.setBackgroundColor(Color.parseColor("#F44336"));
        } else {
            powerOn.setEnabled(false);
            powerOn.setBackgroundColor(Color.parseColor("#684CAF50"));
            powerOff.setEnabled(false);
            powerOff.setBackgroundColor(Color.parseColor("#88F44336"));

        }
    }

    private void setRunningStatus(boolean b){
        if(b){
            tvOnOff.setText("Running");
            tvOnOff.setTextColor(Color.RED);
        } else {
            tvOnOff.setText("Not Running");
            tvOnOff.setTextColor(Color.GREEN);
        }

    }

    private void fillAdapters() {
        hours = new ArrayList<>();
        for(int i = 0; i <= 24; i++){
            hours.add(i);
        }

        minutes = new ArrayList<>();
        for(int i = 0; i <= 59; i++){
            minutes.add(i);
        }


        ArrayAdapter hoursAdapter = new ArrayAdapter<Integer>(SendSMS.this, R.layout.support_simple_spinner_dropdown_item, hours);
        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnWaitForHour.setAdapter(hoursAdapter);
        spnRunForHour.setAdapter(hoursAdapter);

        ArrayAdapter minsAdapter = new ArrayAdapter<Integer>(SendSMS.this, R.layout.support_simple_spinner_dropdown_item, minutes);
        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnWaitForMin.setAdapter(minsAdapter);
        spnRunForMin.setAdapter(minsAdapter);
    }

    private void enableScheduler(boolean b) {
        spnRunForHour.setEnabled(b);
        spnRunForMin.setEnabled(b);
        spnWaitForHour.setEnabled(b);
        spnWaitForMin.setEnabled(b);
        scheduleStart.setEnabled(b);
    }
}
