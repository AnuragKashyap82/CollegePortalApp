package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivityEditTimeTableBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Locale;

public class EditTimeTableActivity extends AppCompatActivity {
    ActivityEditTimeTableBinding binding;
    private FirebaseAuth firebaseAuth;
    private String day, subName, teacherName, startTime, endTime, ongoingTopic, percentSylComp;
    private int hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditTimeTableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        day = getIntent().getStringExtra("day");
        subName = getIntent().getStringExtra("subName");

        loadClassDetails();

        binding.updateTimeTableBtn.setOnClickListener(new View.OnClickListener() {
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

    private void loadClassDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("timeTable");
        ref.child(firebaseAuth.getUid()).child(day).child(subName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        subName = "" + snapshot.child("subName").getValue();
                        teacherName = "" + snapshot.child("teacherName").getValue();
                        startTime = "" + snapshot.child("startTime").getValue();
                        endTime = "" + snapshot.child("endTime").getValue();
                        day = "" + snapshot.child("day").getValue();
                        ongoingTopic = "" + snapshot.child("ongoingTopic").getValue();
                        percentSylComp = "" + snapshot.child("percentSylComp").getValue();

                        binding.subjectEt.setText(subName);
                        binding.classTeacherNameEt.setText(teacherName);
                        binding.startTimeTv.setText(startTime);
                        binding.endTimeTv.setText(endTime);
                        binding.dayTv.setText(day);
                        binding.ongoingTopicEt.setText(ongoingTopic);
                        binding.syllabusCompletedEt.setText(percentSylComp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
            Toast.makeText(EditTimeTableActivity.this, "Enter Subject Name..!!!!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(teacherName)) {
            Toast.makeText(EditTimeTableActivity.this, "Enter Teacher Name..!!!!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(startTime)) {
            Toast.makeText(EditTimeTableActivity.this, "Pick start time..!!!!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(endTime)) {
            Toast.makeText(EditTimeTableActivity.this, "Pick End Time..!!!!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(day)) {
            Toast.makeText(EditTimeTableActivity.this, "Pick End Time..!!!!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(ongoingTopic)) {
            Toast.makeText(EditTimeTableActivity.this, "Enter OnGoing Topic..!!!!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(percentSylComp)) {
            Toast.makeText(EditTimeTableActivity.this, "Enter % Syllabus Completed..!!!!", Toast.LENGTH_SHORT).show();
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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("timeTable");
        ref.child(firebaseAuth.getUid()).child(day).child(subName)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(EditTimeTableActivity.this, "Time Table Updated....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(EditTimeTableActivity.this, " Failed to update to db due to" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
