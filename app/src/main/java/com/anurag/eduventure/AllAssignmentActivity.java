package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.anurag.eduventure.Adapters.AdapterAssignment;
import com.anurag.eduventure.Adapters.AdapterNotice;
import com.anurag.eduventure.Models.ModelAssignment;
import com.anurag.eduventure.Models.ModelNotice;
import com.anurag.eduventure.databinding.ActivityAllAssignmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AllAssignmentActivity extends AppCompatActivity {

    ActivityAllAssignmentBinding binding;
    private FirebaseAuth firebaseAuth;
    private String classCode;

    private ArrayList<ModelAssignment> assignmentList;
    private AdapterAssignment adapterAssignment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        classCode = getIntent().getStringExtra("classCode");

        loadAllAssignment();
    }

    private void loadAllAssignment() {
        assignmentList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("classroom").document(classCode).collection("assignment")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelAssignment modelAssignment = document.toObject(ModelAssignment.class);
                                assignmentList.add(modelAssignment);
                            }
                            Collections.sort(assignmentList, new Comparator<ModelAssignment>() {
                                @Override
                                public int compare(ModelAssignment t1, ModelAssignment t2) {
                                    return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
                                }
                            });
                            Collections.reverse(assignmentList);

                            LinearLayoutManager layoutManager = new LinearLayoutManager(AllAssignmentActivity.this, LinearLayoutManager.HORIZONTAL, false);
                            binding.assRv.setLayoutManager(layoutManager);

                            binding.assRv.setLayoutManager(new LinearLayoutManager(AllAssignmentActivity.this));

                            adapterAssignment = new AdapterAssignment(AllAssignmentActivity.this, assignmentList);
                            binding.assRv.setAdapter(adapterAssignment);
                        }
                    }
                });
    }
}