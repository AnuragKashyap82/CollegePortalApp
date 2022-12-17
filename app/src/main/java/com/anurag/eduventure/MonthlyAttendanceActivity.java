package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.anurag.eduventure.Adapters.AdapterOverAllAttendance;
import com.anurag.eduventure.Models.ModelAtteStud;
import com.anurag.eduventure.databinding.ActivityMonthlyAttendanceBinding;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class MonthlyAttendanceActivity extends AppCompatActivity {

    private ActivityMonthlyAttendanceBinding binding;
    private String classCode;
    private AdapterOverAllAttendance adapterOverAllAttendance;
    private ArrayList<ModelAtteStud> atteStudArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMonthlyAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        String monthYear = ""+ currentMonth+currentYear;

        binding.monthTv.setText(""+currentMonth);
        binding.yearTv.setText(""+currentYear);

        classCode = getIntent().getStringExtra("classCode");
        loadClassDetails(classCode);
        loadAllStudents(classCode, monthYear);
        loadTotalNoClass(classCode);
    }

    private void loadTotalNoClass(String classCode) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        String monthYear = ""+ currentMonth+currentYear;

        DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("classroom");
        databaseReference.child(classCode).child("noOfClass").child(""+monthYear)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String totalNoClass = snapshot.child("noOfClass").getValue().toString();
                        binding.totalClassCountTv.setText(totalNoClass);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
    private void loadAllStudents(String classCode, String monthYear) {
        atteStudArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Classroom").child(classCode).child("Students").child(monthYear);
        reference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        atteStudArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelAtteStud model = ds.getValue(ModelAtteStud.class);
                            atteStudArrayList.add(model);
                        }
                        LinearLayoutManager layoutManager = new LinearLayoutManager(MonthlyAttendanceActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        binding.studentsRv.setLayoutManager(layoutManager);

                        binding.studentsRv.setLayoutManager(new LinearLayoutManager(MonthlyAttendanceActivity.this));

                        adapterOverAllAttendance = new AdapterOverAllAttendance(MonthlyAttendanceActivity.this, atteStudArrayList);
                        binding.studentsRv.setAdapter(adapterOverAllAttendance);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}