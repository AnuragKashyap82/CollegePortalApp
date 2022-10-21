package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.eduventure.Adapters.AdapterPresentStudent;
import com.anurag.eduventure.Adapters.AdapterStudent;
import com.anurag.eduventure.Models.ModelPresentStudent;
import com.anurag.eduventure.Models.ModelStudent;
import com.anurag.eduventure.databinding.ActivityStudentListBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class StudentListActivity extends AppCompatActivity {
    ActivityStudentListBinding binding;
    private String branch, semester, date, name, sub;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelStudent> studentList;
    private AdapterStudent adapterStudent;

    private ArrayList<ModelPresentStudent> presentStudents;
    private AdapterPresentStudent adapterPresentStudent;

    private EasyDB easyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        branch = getIntent().getStringExtra("branch");
        semester = getIntent().getStringExtra("semester");
        date = getIntent().getStringExtra("date");
        sub = getIntent().getStringExtra("sub");

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadAllStudents();
        binding.dateTv.setText(date);

        easyDB = EasyDB.init(this, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Branch", new String[]{"text", "not null"}))
                .addColumn(new Column("Semester", new String[]{"text", "not null"}))
                .addColumn(new Column("uniqueId", new String[]{"text", "unique"}))
                .doneTableColumn();

        deletePresentStudent();
        presentCount();

        binding.presentCountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPresentDialog();
            }
        });
    }

    private void showPresentDialog() {
        presentStudents = new ArrayList<>();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_present, null);

        Button submitAttendanceBtn = view.findViewById(R.id.submitAttendanceBtn);
        RecyclerView presentStudentRv = view.findViewById(R.id.presentStudentRv);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        EasyDB easyDB = EasyDB.init(this, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Branch", new String[]{"text", "not null"}))
                .addColumn(new Column("Semester", new String[]{"text", "not null"}))
                .addColumn(new Column("uniqueId", new String[]{"text", "unique"}))
                .doneTableColumn();

        Cursor res = easyDB.getAllData();
        while (res.moveToNext()) {
            String name = res.getString(1);
            String branch = res.getString(2);
            String semester = res.getString(3);
            String uniqueId = res.getString(4);


            ModelPresentStudent modelPresentStudent = new ModelPresentStudent(
                    "" + name,
                    "" + branch,
                    "" + semester,
                    "" + uniqueId
            );

            presentStudents.add(modelPresentStudent);

        }

        adapterPresentStudent = new AdapterPresentStudent(this, presentStudents);
        presentStudentRv.setAdapter(adapterPresentStudent);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
            }
        });
        submitAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                assert user != null;
                        if (presentStudents.size() == 0) {
                            Toast.makeText(StudentListActivity.this, "No Student Is Present...!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        addPresentStudentInToDb();
                    }

        });
    }

    private void deletePresentStudent() {

        easyDB.deleteAllDataFromTable();
    }

    public void presentCount() {
        int count = easyDB.getAllData().getCount();
        if (count == 0) {
            binding.presentCountTv.setVisibility(View.GONE);
        } else {
            binding.presentCountTv.setVisibility(View.VISIBLE);
            binding.presentCountTv.setText("" + count);
        }
    }

    private void loadAllStudents() {
        studentList =new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("students");
        ref.child(semester).child(branch)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        studentList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelStudent modelStudent = ds.getValue(ModelStudent.class);
                            studentList.add(modelStudent);
                        }
                        Collections.sort(studentList, new Comparator<ModelStudent>() {
                            @Override
                            public int compare(ModelStudent t1, ModelStudent t2) {
                                return t1.getName().compareToIgnoreCase(t2.getName());
                            }
                        });

                        LinearLayoutManager layoutManager = new LinearLayoutManager(StudentListActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        binding.studentRv.setLayoutManager(layoutManager);

                        binding.studentRv.setLayoutManager(new LinearLayoutManager(StudentListActivity.this));

                        adapterStudent = new AdapterStudent(StudentListActivity.this, studentList);
                        binding.studentRv.setAdapter(adapterStudent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void addPresentStudentInToDb() {
        progressDialog.setMessage("Submitting Final Attendance...!!!!");
        progressDialog.show();

        String timestamp = "" + System.currentTimeMillis();


        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("branch", "" + branch);
        hashMap.put("semester", "" + semester);
        hashMap.put("date", "" + date);
        hashMap.put("sub", "" + sub);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Attendance").child(semester).child(branch).child(sub).child(date);
        ref.setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        for (int i = 0; i < presentStudents.size(); i++) {
                            String name = presentStudents.get(i).getName();
                            String branch = presentStudents.get(i).getBranch();
                            String semester = presentStudents.get(i).getSemester();
                            String uniqueId = presentStudents.get(i).getUniqueId();

                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("name", name);
                            hashMap1.put("branch", branch);
                            hashMap1.put("semester", semester);
                            hashMap1.put("sub", sub);
                            hashMap1.put("date", date);
                            hashMap1.put("uniqueId", uniqueId);
                            hashMap1.put("attendance", "Present");
                            hashMap1.put("timestamp", timestamp);

                            ref.child("present").child(uniqueId)
                            .setValue(hashMap1);

                        }
                        progressDialog.dismiss();
                        Toast.makeText(StudentListActivity.this, "Attendance Submitted Successfully...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(StudentListActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}