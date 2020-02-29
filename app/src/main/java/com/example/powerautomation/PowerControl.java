package com.example.powerautomation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
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

import java.util.ArrayList;
import java.util.List;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class PowerControl extends AppCompatActivity {
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

    static boolean lowMoisture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_control);

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
                Toast.makeText(PowerControl.this, "CLicked", Toast.LENGTH_SHORT).show();
            }
        });

        enableScheduler(false);
        scheduleContainer.setVisibility(View.INVISIBLE);

        swSchedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(swSchedule.isChecked()) {
                    powerOn.setEnabled(false);
                    powerOff.setEnabled(false);
                    enableScheduler(true);
                    scheduleContainer.setVisibility(View.VISIBLE);
                } else {
                    setPowerButtons();
                    enableScheduler(false);
                    scheduleContainer.setVisibility(View.INVISIBLE);

                }
            }
        });


        fillAdapters();


        setPowerButtons();

        powerOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isMoistureLow();
                System.out.println(lowMoisture);
                if(lowMoisture == false){
                    Log.i("moisture", "false");
                    Log.i("moisture", "getT");
                    //Toast.makeText(PowerControl.this, "Moisture is greater then 45. Didnt start system.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String startMotor = MyURLs.setOnOff("1", username, password);

                Log.i("checkOnOff", startMotor);
                queue = Volley.newRequestQueue(PowerControl.this);
                StringRequest request = new StringRequest(Request.Method.GET,
                        startMotor
                        , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("1")){
                            Log.i("checkOnOff", response);

                            editor = sharedPreferences.edit();
                            editor.putString("powerOn", "1");
                            editor.putString("powerOff", "0");
                            editor.commit();

                            Toast.makeText(PowerControl.this, "Set On", Toast.LENGTH_SHORT).show();
                            powerOn.setEnabled(false);
                            powerOn.setBackgroundColor(Color.parseColor("#684CAF50"));
                            powerOff.setEnabled(true);
                            powerOff.setBackgroundColor(Color.parseColor("#F44336"));

                            setRunningStatus(true);

                        } else {
                            Toast.makeText(PowerControl.this, "On Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error",error.toString());
                    }
                });
                queue.add(request);


            }
        });

        powerOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String stopMotor = MyURLs.setOnOff("0", username, password);

                Log.i("checkOnOff", stopMotor);

                queue = Volley.newRequestQueue(PowerControl.this);
                StringRequest request = new StringRequest(Request.Method.GET,
                        stopMotor
                        , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("1")){

                            Log.i("checkOnOff", response);
                            editor = sharedPreferences.edit();
                            editor.putString("powerOff", "1");
                            editor.putString("powerOn", "0");
                            editor.commit();

                            Toast.makeText(PowerControl.this, "Set OFF", Toast.LENGTH_SHORT).show();

                            powerOn.setEnabled(true);
                            powerOn.setBackgroundColor(Color.parseColor("#4CAF50"));
                            powerOff.setEnabled(false);
                            powerOff.setBackgroundColor(Color.parseColor("#88F44336"));

                            setRunningStatus(false);

                        } else {
                            Toast.makeText(PowerControl.this, "Off Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error",error.toString());
                    }
                });
                queue.add(request);
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

                if(waitFor == 0){
                    isMoistureLow();
                    if(lowMoisture == false){
                        Toast.makeText(PowerControl.this,
                                "Moisture is greater then 45. Didnt start system.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Log.i("scheduleOnOff", Integer.toString(waitFor) + " " + Integer.toString(runFor));

                String startSchedule = MyURLs.setSchedule(waitFor, runFor, username, password);
                Log.i("scheduleOnOff", startSchedule);

                queue = Volley.newRequestQueue(PowerControl.this);
                StringRequest request = new StringRequest(Request.Method.GET,
                        startSchedule
                        , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("1"))
                        {

                            Log.i("scheduleOnOff", response);
                            editor = sharedPreferences.edit();
                            editor.putString("schedule", "1");
                            editor.commit();

                            Toast.makeText(PowerControl.this, "Schedule Set Successful", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(PowerControl.this, "Schedule Set Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error",error.toString());
                    }
                });
                queue.add(request);

            }
        });

    }

    private void isMoistureLow() {
        String strMoisture = "http://sgh2020.tonysolutions.co/get_mos.php?id=1";
        queue = Volley.newRequestQueue(PowerControl.this);
        StringRequest request = new StringRequest(Request.Method.GET,
                strMoisture
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("moisture", response);
                if(Integer.parseInt(response) > 45){
                    Log.i("moisture", "inside");
                    lowMoisture = false;
                } else {
                    Log.i("moisture", "setT");
                    lowMoisture = true;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error",error.toString());
                lowMoisture = false;
            }
        });
        queue.add(request);
    }

    private void setPowerButtons() {
        if(sharedPreferences.getString("powerOn", "0").equals("0")){
            //when system is off
            powerOn.setEnabled(true);
            powerOn.setBackgroundColor(Color.parseColor("#4CAF50"));
            powerOff.setEnabled(false);

            powerOff.setBackgroundColor(Color.parseColor("#88F44336"));
            setRunningStatus(false);

        } else {
            powerOn.setEnabled(false);
            powerOn.setBackgroundColor(Color.parseColor("#684CAF50"));
            powerOff.setEnabled(true);
            powerOff.setBackgroundColor(Color.parseColor("#F44336"));
            setRunningStatus(true);
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


        ArrayAdapter hoursAdapter = new ArrayAdapter<Integer>(PowerControl.this, R.layout.support_simple_spinner_dropdown_item, hours);
        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnWaitForHour.setAdapter(hoursAdapter);
        spnRunForHour.setAdapter(hoursAdapter);

        ArrayAdapter minsAdapter = new ArrayAdapter<Integer>(PowerControl.this, R.layout.support_simple_spinner_dropdown_item, minutes);
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
