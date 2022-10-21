package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivityUpdateTimeTableBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;

public class UpdateTimeTableActivity extends AppCompatActivity {
    ActivityUpdateTimeTableBinding binding;
    private String subName, teacherName, startTime, endTime, day, ongoingTopic, percentSylComp;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private int hour, minute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateTimeTableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.dayTv.setText(getIntent().getStringExtra("text"));

        binding.assClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.startTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStartTimePicker();
            }
        });
        binding.endTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEndTimePicker();
            }
        });
    }

    private void openStartTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                binding.startTimeTv.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, /*style,*/ onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }
    private void openEndTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                binding.endTimeTv.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, /*style,*/ onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private void validateData() {
        subName = binding.subjectEt.getText().toString().trim();
        teacherName = binding.classTeacherNameEt.getText().toString().trim();
        startTime = binding.startTimeTv.getText().toString().trim();
        endTime = binding.endTimeTv.getText().toString().trim();
        day = binding.dayTv.getText().toString().trim();
        ongoingTopic = binding.ongoingTopicEt.getText().toString().trim();
        percentSylComp = binding.syllabusCompletedEt.getText().toString().trim();

        if (TextUtils.isEmpty(subName)) {
            Toast.makeText(UpdateTimeTableActivity.this, "Enter Subject Name..!!!!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(teacherName)) {
            Toast.makeText(UpdateTimeTableActivity.this, "Enter Teacher Name..!!!!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(startTime)) {
            Toast.makeText(UpdateTimeTableActivity.this, "Pick start time..!!!!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(endTime)) {
            Toast.makeText(UpdateTimeTableActivity.this, "Pick End Time..!!!!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(day)) {
            Toast.makeText(UpdateTimeTableActivity.this, "Pick End Time..!!!!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(ongoingTopic)) {
            Toast.makeText(UpdateTimeTableActivity.this, "Enter OnGoing Topic..!!!!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(percentSylComp)) {
            Toast.makeText(UpdateTimeTableActivity.this, "Enter % Syllabus Completed..!!!!", Toast.LENGTH_SHORT).show();
        } else {
            updateTimeTable();
        }
    }

    private void updateTimeTable() {
        binding.progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("subName", "" + subName);
        hashMap.put("teacherName", "" + teacherName);
        hashMap.put("startTime", "" + startTime);
        hashMap.put("endTime", "" + endTime);
        hashMap.put("day", "" + day);
        hashMap.put("ongoingTopic", "" + ongoingTopic);
        hashMap.put("percentSylComp", "" + percentSylComp);

        DocumentReference documentReference = firebaseFirestore.collection("timeTable").document(firebaseAuth.getUid()).collection(day).document(subName);
        documentReference.set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.progressBar.setVisibility(View.GONE);

                        Toast.makeText(UpdateTimeTableActivity.this, "Time Table Updated....", Toast.LENGTH_SHORT).show();
                        clearData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(UpdateTimeTableActivity.this, " Failed to update to db due to" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void clearData() {
        binding.subjectEt.setText("");
        binding.classTeacherNameEt.setText("");
        binding.startTimeTv.setText("");
        binding.endTimeTv.setText("");
        binding.ongoingTopicEt.setText("");
        binding.syllabusCompletedEt.setText("");
    }
}