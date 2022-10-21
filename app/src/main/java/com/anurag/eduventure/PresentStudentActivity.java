package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.anurag.eduventure.Adapters.AdapterAllPresent;
import com.anurag.eduventure.Adapters.AdapterNotice;
import com.anurag.eduventure.Models.ModelAllPresent;
import com.anurag.eduventure.Models.ModelNotice;
import com.anurag.eduventure.databinding.ActivityPresentStudentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PresentStudentActivity extends AppCompatActivity {
    ActivityPresentStudentBinding binding;

    private String branch, semester, date, sub;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelAllPresent> allPresentList;
    private AdapterAllPresent adapterAllPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresentStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        branch = getIntent().getStringExtra("branch");
        semester = getIntent().getStringExtra("semester");
        date = getIntent().getStringExtra("date");
        sub = getIntent().getStringExtra("sub");

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadAllPresentStudents();
        binding.dateTv.setText(date);

        binding.subAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PresentStudentActivity.this, SubjectAttendanceActivity.class));
            }
        });
    }

    private void loadAllPresentStudents() {
        allPresentList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance").child(semester).child(branch)
                .child(sub).child(date).child("present");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                allPresentList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelAllPresent modelAllPresent = ds.getValue(ModelAllPresent.class);
                    allPresentList.add(modelAllPresent);
                }
//                Collections.sort(allPresentList, new Comparator<ModelAllPresent>() {
//                    @Override
//                    public int compare(ModelAllPresent t1, ModelAllPresent t2) {
//                        return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
//                    }
//                });
//                Collections.reverse(allPresentList);

                LinearLayoutManager layoutManager = new LinearLayoutManager(PresentStudentActivity.this, LinearLayoutManager.HORIZONTAL, false);
                binding.presentStudentRv.setLayoutManager(layoutManager);

                binding.presentStudentRv.setLayoutManager(new LinearLayoutManager(PresentStudentActivity.this));

                adapterAllPresent = new AdapterAllPresent(PresentStudentActivity.this, allPresentList);
                binding.presentStudentRv.setAdapter(adapterAllPresent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}