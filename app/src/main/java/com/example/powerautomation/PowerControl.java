package com.example.powerautomation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PowerControl extends AppCompatActivity {

    Spinner spnWaitForHour, spnWaitForMin, spnRunForHour, spnRunForMin;

    Switch swSchedule;

    ConstraintLayout scheduleContainer;

    Button powerOn, powerOff, scheduleStart;

    List<Integer> hours, minutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_control);

        spnWaitForHour = findViewById(R.id.waitHour);
        spnWaitForMin = findViewById(R.id.waitMin);
        spnRunForHour = findViewById(R.id.runHour);
        spnRunForMin = findViewById(R.id.runMin);

        swSchedule = findViewById(R.id.schedule);

        scheduleContainer = findViewById(R.id.scheduleContainer);

        powerOn = findViewById(R.id.powerOn);
        powerOff = findViewById(R.id.powerOff);
        scheduleStart = findViewById(R.id.scheduleStart);

        scheduleStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PowerControl.this, "CLicked", Toast.LENGTH_SHORT).show();
            }
        });

        enableScheduler(false);

        swSchedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(swSchedule.isChecked()) {
                    powerOn.setEnabled(false);
                    powerOff.setEnabled(false);

                    enableScheduler(true);
                } else {
                    powerOn.setEnabled(true);
                    powerOff.setEnabled(true);

                    enableScheduler(false);
                }
            }
        });

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
