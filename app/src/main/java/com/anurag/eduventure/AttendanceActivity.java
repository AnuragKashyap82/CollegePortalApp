package com.anurag.eduventure;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.eduventure.Adapters.AdapterAttenStud;
import com.anurag.eduventure.Adapters.AdapterLecture;
import com.anurag.eduventure.Models.ModelAtteStud;
import com.anurag.eduventure.Models.ModelLecture;
import com.anurag.eduventure.databinding.ActivityAttendanceBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import static com.anurag.eduventure.AttendanceActivity.joiningCode;

public class AttendanceActivity extends AppCompatActivity {
    private ActivityAttendanceBinding binding;
    private AdapterAttenStud adapterAttenStud;
    private ArrayList<ModelAtteStud> atteStudArrayList;
    public static String joiningCode;
    private String currentDate, name;
    private boolean isCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        joiningCode = getIntent().getStringExtra("classCode");

        loadClassDetails();

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        currentDate = dateFormat.format(calendar.getTime());

        checkAttendanceCreated();

        binding.reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttendanceActivity.this, AttendanceReportActivity.class);
                intent.putExtra("classCode", ""+joiningCode);
                startActivity(intent);
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        binding.createAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateAttendanceDialog();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAttendanceCreated();
    }
    private void checkAttendanceCreated() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("classroom").child(joiningCode);
        databaseReference.child("classes").child(""+currentDate)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            binding.createAttendanceBtn.setVisibility(View.GONE);
                            loadAllStudents(joiningCode);
                        }else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadClassDetails() {

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("classroom").document(joiningCode);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot ds, @Nullable FirebaseFirestoreException error) {
                String className = "" + ds.getString("className");
                String subjectName = "" + ds.getString("subjectName");
                String uid = "" + ds.getString("uid");

                binding.subjectNameTv.setText(subjectName);
                binding.classNameTv.setText(className);

                loadTeacherDetails(uid);
            }
        });
    }

    private void loadTeacherDetails(String uid) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(uid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot.exists()){
                     name = snapshot.get("name").toString();

                }
            }
        });
    }

    private void loadAllStudents(String joiningCode) {
        binding.progressBar.setVisibility(View.VISIBLE);
        atteStudArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("classroom").child(joiningCode).child("Students");
        reference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        atteStudArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelAtteStud model = ds.getValue(ModelAtteStud.class);
                            atteStudArrayList.add(model);

                            int noOfStudents = atteStudArrayList.size();
                            binding.noOfStudentsTv.setText(String.valueOf("Total No. of Students: " + noOfStudents));
                        }
                        LinearLayoutManager layoutManager = new LinearLayoutManager(AttendanceActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        binding.studentsRv.setLayoutManager(layoutManager);

                        binding.studentsRv.setLayoutManager(new LinearLayoutManager(AttendanceActivity.this));

                        adapterAttenStud = new AdapterAttenStud(AttendanceActivity.this, atteStudArrayList);
                        binding.studentsRv.setAdapter(adapterAttenStud);
                        binding.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void showCreateAttendanceDialog () {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this, R.style.BottomSheetStyle);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view1 = inflater.inflate(R.layout.teacher_attendance_dialog, null);
        myDialog.setView(view1);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(true);

        Button createClassBtn = view1.findViewById(R.id.createClassBtn);
        TextView dateTv = view1.findViewById(R.id.dateTv);
        TextView teacherNameTv = view1.findViewById(R.id.teacherNameTv);

        dateTv.setText(currentDate);
        teacherNameTv.setText(name);

        createClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                binding.progressBar.setVisibility(View.VISIBLE);
                createTodayAttendance();
            }
        });
        dialog.show();
    }

    private void createTodayAttendance() {

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        String monthYear = ""+ currentMonth+currentYear;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("date", ""+currentDate);

        DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("classroom");
            databaseReference.child(joiningCode).child("classes").child(""+currentDate).setValue(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            binding.progressBar.setVisibility(View.GONE);
                            increaseNoOfClasses(joiningCode);
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("classroom");
                            databaseReference.child(joiningCode).child("noOfClass").child(""+monthYear)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                String noOfClass = "" + snapshot.child("noOfClass").getValue();

                                                if (noOfClass.equals("") || noOfClass.equals("null")) {
                                                    noOfClass = "0";
                                                }
                                                long newNoClassCount = Long.parseLong(noOfClass) + 1;
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("noOfClass", newNoClassCount);

                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("classroom");
                                                reference.child(joiningCode).child("noOfClass").child(""+monthYear).updateChildren(hashMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){

                                                                }else {

                                                                }
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });

                                            }else{
                                                long newNoClassCount = 1;
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("noOfClass", newNoClassCount);

                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("classroom");
                                                reference.child(joiningCode).child("noOfClass").child(""+monthYear).updateChildren(hashMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){

                                                                }else {

                                                                }
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }else {
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
    private void increaseNoOfClasses(String joiningCode) {

    }
}