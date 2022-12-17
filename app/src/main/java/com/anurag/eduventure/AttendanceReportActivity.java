package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.anurag.eduventure.Adapters.AdapterReportAttendance;
import com.anurag.eduventure.Models.ModelAttendanceReport;
import com.anurag.eduventure.databinding.ActivityAttendanceReportBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AttendanceReportActivity extends AppCompatActivity {
    private ActivityAttendanceReportBinding binding;
    private String classCode;
    private ArrayList<ModelAttendanceReport> attendanceReportArrayList;
    private AdapterReportAttendance adapterReportAttendance;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendanceReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore  = FirebaseFirestore.getInstance();

        classCode = getIntent().getStringExtra("classCode");

        loadClassDetails(classCode);
        loadCurrentDate(classCode);

        binding.dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickDialog();
            }
        });
        binding.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttendanceReportActivity.this, MonthlyAttendanceActivity.class);
                intent.putExtra("classCode", ""+classCode);
                startActivity(intent);
            }
        });
    }

    private void loadCurrentDate(String classCode) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());

        binding.dateTv.setText(date);
        loadAllDates(classCode, date);
    }

    private void datePickDialog() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                DecimalFormat mFormat = new DecimalFormat("00");
                String pDay = mFormat.format(dayOfMonth);
                String pMonth = mFormat.format(monthOfYear+1);
                String pYear = "" + year;
                String pDate = pDay + "-" + pMonth + "-" + pYear;

                binding.dateTv.setText(pDate);
                loadAllDates(classCode, pDate);
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
        datePickerDialog.getDatePicker();
    }

    private void loadClassDetails(String classCode) {

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("classroom").document(classCode);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot ds, @Nullable FirebaseFirestoreException error) {
                String className = "" + ds.getString("className");
                String subjectName = "" + ds.getString("subjectName");

                binding.subjectNameTv.setText(subjectName);
                binding.classNameTv.setText(className);
            }
        });
    }
    private void loadAllDates(String classCode, String pDate) {
        binding.progressBar.setVisibility(View.VISIBLE);

        attendanceReportArrayList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("classroom").document(classCode)
                .collection("Attendance").document("Date").collection(""+pDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelAttendanceReport modelAttendanceReport = document.toObject(ModelAttendanceReport.class);
                                attendanceReportArrayList.add(modelAttendanceReport);
                            }

                            LinearLayoutManager layoutManager = new LinearLayoutManager(AttendanceReportActivity.this, LinearLayoutManager.HORIZONTAL, false);
                            binding.dateRv.setLayoutManager(layoutManager);

                            binding.dateRv.setLayoutManager(new LinearLayoutManager(AttendanceReportActivity.this));

                            adapterReportAttendance = new AdapterReportAttendance(AttendanceReportActivity.this, attendanceReportArrayList);
                            binding.dateRv.setAdapter(adapterReportAttendance);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}