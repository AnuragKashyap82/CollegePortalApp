package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.anurag.eduventure.Adapters.AdapterReportAttendance;
import com.anurag.eduventure.Models.ModelAttendanceReport;
import com.anurag.eduventure.databinding.ActivityMyAttendanceBinding;
import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.List;
import java.util.TimeZone;

public class MyAttendanceActivity extends AppCompatActivity {

    private ActivityMyAttendanceBinding binding;
    private String classCode;
    private FirebaseAuth firebaseAuth;
    private int noOfClass, absentCount;
    private String monthYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        classCode = getIntent().getStringExtra("classCode");

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

         monthYear = ""+ currentMonth+currentYear;

        loadClassDetails(classCode);
        loadCurrentDate(classCode);
        loadGraph();

        binding.dateTv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickDialog();
            }
        });
        binding.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MyAttendanceActivity.this, "To be integrated!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCurrentDate(String classCode) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());

        binding.dateTv1.setText(date);
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

                binding.dateTv1.setText(pDate);
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
        DocumentReference documentReference  = FirebaseFirestore.getInstance().collection("classroom").document(classCode);
        documentReference.collection("Attendance").document("Date").collection(pDate).document(firebaseAuth.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()){
                            String attendance = snapshot.getString("Attendance");
                            if (attendance.equals("Present")){
                                binding.attendanceReTv.setText(attendance);
                            }else if (attendance.equals("Absent")){
                                binding.attendanceReTv.setText(attendance);
                            }
                            binding.progressBar.setVisibility(View.GONE);
                        }else {
                            binding.attendanceReTv.setText("No Data Found");
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void loadGraph() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Classroom").child(classCode).child("Students").child(monthYear);
        reference.child(firebaseAuth.getUid())
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    int presentCount = 0;
                    if (snapshot.hasChild("presentCount")) {
                        presentCount = Integer.parseInt(snapshot.child("presentCount").getValue().toString());
                    } else {
                        presentCount = 0;
                    }

                    DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("classroom");
                    databaseReference.child(classCode).child("noOfClass").child(""+monthYear)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                         noOfClass = 0;
                                        if (snapshot.hasChild("noOfClass")) {
                                            noOfClass = Integer.parseInt(snapshot.child("noOfClass").getValue().toString());
                                        } else {
                                            noOfClass = 0;
                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                    absentCount = noOfClass - presentCount;

                    Pie pie = AnyChart.pie();
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Present", presentCount));
                    data.add(new ValueDataEntry("Absent", absentCount));


                    pie.data(data);
                    pie.title("Monthly Attendance");
                    pie.labels().position("inside");
                    pie.legend().title()
                            .text(":Items Spent On")
                            .padding(0d, 0d, 10d, 0d);


                    pie.legend().position("center-bottom")
                            .itemsLayout(LegendLayout.HORIZONTAL)
                            .align(Align.CENTER);

                    binding.anyChartView.setChart(pie);

                } else {
                    Toast.makeText(MyAttendanceActivity.this, "No Attendance for this month!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}