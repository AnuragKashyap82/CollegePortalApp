package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.anurag.eduventure.Adapters.AdapterClassroom;
import com.anurag.eduventure.Adapters.AdapterNotice;
import com.anurag.eduventure.Models.ModelClassroom;
import com.anurag.eduventure.Models.ModelNotice;
import com.anurag.eduventure.databinding.ActivityClassroomBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClassroomActivity extends AppCompatActivity {
    ActivityClassroomBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ArrayList<ModelClassroom> classroomArrayList;
    private AdapterClassroom adapterClassroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClassroomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        loadAllClassroom();

        binding.createClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ClassroomActivity.this, CreateClassActivity.class));
            }
        });
        binding.joinClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ClassroomActivity.this, JoinClassActivity.class));
            }
        });
    }

    private void loadAllClassroom() {
        classroomArrayList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid()).collection("classroom")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelClassroom modelClassroom = document.toObject(ModelClassroom.class);
                                classroomArrayList.add(modelClassroom);
                            }
                            Collections.sort(classroomArrayList, new Comparator<ModelClassroom>() {
                                @Override
                                public int compare(ModelClassroom t1, ModelClassroom t2) {
                                    return t1.getClassCode().compareToIgnoreCase(t2.getClassCode());
                                }
                            });

                            Collections.reverse(classroomArrayList);

                            LinearLayoutManager layoutManager = new LinearLayoutManager(ClassroomActivity.this, LinearLayoutManager.HORIZONTAL, false);
                            binding.classroomRv.setLayoutManager(layoutManager);

                            binding.classroomRv.setLayoutManager(new LinearLayoutManager(ClassroomActivity.this));

                            adapterClassroom = new AdapterClassroom(ClassroomActivity.this, classroomArrayList);
                            binding.classroomRv.setAdapter(adapterClassroom);
                        }
                    }
                });
    }
}