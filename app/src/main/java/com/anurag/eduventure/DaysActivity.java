package com.anurag.eduventure;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.anurag.eduventure.databinding.ActivityDaysBinding;

import java.util.Calendar;

public class DaysActivity extends AppCompatActivity {
    ActivityDaysBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDaysBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mondayRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.monday.getText().toString();
                Intent myIntent = new Intent(view.getContext(), TimeTableActivity.class);
                myIntent.putExtra("text", text);
                startActivity(myIntent);

            }
        });
        binding.tuesdayRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.tuesday.getText().toString();
                Intent myIntent = new Intent(view.getContext(), TimeTableActivity.class);
                myIntent.putExtra("text", text);
                startActivity(myIntent);
            }
        });
        binding.wednesdayRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.wednesday.getText().toString();
                Intent myIntent = new Intent(view.getContext(), TimeTableActivity.class);
                myIntent.putExtra("text", text);
                startActivity(myIntent);
            }
        });
        binding.thurRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.thursday.getText().toString();
                Intent myIntent = new Intent(view.getContext(), TimeTableActivity.class);
                myIntent.putExtra("text", text);
                startActivity(myIntent);
            }
        });
        binding.fridayRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.friday.getText().toString();
                Intent myIntent = new Intent(view.getContext(), TimeTableActivity.class);
                myIntent.putExtra("text", text);
                startActivity(myIntent);
            }
        });
        binding.satRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.saturday.getText().toString();
                Intent myIntent = new Intent(view.getContext(), TimeTableActivity.class);
                myIntent.putExtra("text", text);
                startActivity(myIntent);
            }
        });
    }
}