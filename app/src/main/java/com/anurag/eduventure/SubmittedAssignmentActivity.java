package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.anurag.eduventure.Adapters.AdapterNotice;
import com.anurag.eduventure.Adapters.AdapterSubmittedAss;
import com.anurag.eduventure.Models.ModelNotice;
import com.anurag.eduventure.Models.ModelSubmittedAss;
import com.anurag.eduventure.databinding.ActivitySubmittedAssignmentBinding;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SubmittedAssignmentActivity extends AppCompatActivity {

    ActivitySubmittedAssignmentBinding binding;
    private String  assignmentId, classCode;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ArrayList<ModelSubmittedAss> assignmentList;
    private AdapterSubmittedAss adapterSubmittedAss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubmittedAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        assignmentId = getIntent().getStringExtra("assignmentId");
        classCode = getIntent().getStringExtra("classCode");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore  = FirebaseFirestore.getInstance();

        loadAllSubmittedAss(classCode, assignmentId);
        countNoOfStudentSubmitted(classCode, assignmentId);

    }

    private void countNoOfStudentSubmitted(String classCode, String assignmentId) {

        DocumentReference documentReference = firebaseFirestore.collection("classroom").document(classCode).collection("assignment").document(assignmentId).collection("Submission").document(firebaseAuth.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                long noOfStudent = value.();
//                float noOfStudents = noOfStudent / 1;
//
//                binding.noOfStudentsTv.setText(String.format("%.0f", noOfStudents));
            }
        });
    }
    private void loadAllSubmittedAss(String classCode, String assignmentId) {
        assignmentList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("classroom").document(classCode).collection("assignment").document(assignmentId).collection("Submission")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelSubmittedAss modelSubmittedAss = document.toObject(ModelSubmittedAss.class);
                                assignmentList.add(modelSubmittedAss);
                            }
                            Collections.sort(assignmentList, new Comparator<ModelSubmittedAss>() {
                                @Override
                                public int compare(ModelSubmittedAss t1, ModelSubmittedAss t2) {
                                    return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
                                }
                            });
                            Collections.reverse(assignmentList);

                            LinearLayoutManager layoutManager = new LinearLayoutManager(SubmittedAssignmentActivity.this, LinearLayoutManager.HORIZONTAL, false);
                            binding.submittedAssignmentRv.setLayoutManager(layoutManager);

                            binding.submittedAssignmentRv.setLayoutManager(new LinearLayoutManager(SubmittedAssignmentActivity.this));

                            adapterSubmittedAss = new AdapterSubmittedAss(SubmittedAssignmentActivity.this, assignmentList);
                            binding.submittedAssignmentRv.setAdapter(adapterSubmittedAss);
                        }
                    }
                });
    }
}