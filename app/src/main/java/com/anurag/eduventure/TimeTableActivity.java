package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.anurag.eduventure.Adapters.AdapterNotice;
import com.anurag.eduventure.Adapters.AdapterTimeTable;
import com.anurag.eduventure.Models.ModelNotice;
import com.anurag.eduventure.Models.ModelTimeTable;
import com.anurag.eduventure.databinding.ActivityTimeTableBinding;
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

public class TimeTableActivity extends AppCompatActivity {
    ActivityTimeTableBinding binding;

    private ArrayList<ModelTimeTable> timeTableArrayList;
    private AdapterTimeTable adapterTimeTable;
    private String day;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimeTableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.dayTv.setText(getIntent().getStringExtra("text"));
        day = binding.dayTv.getText().toString().trim();

        loadAllClasses();

        binding.updateTimeTableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.dayTv.getText().toString();
                Intent myIntent = new Intent(view.getContext(),UpdateTimeTableActivity.class);
                myIntent.putExtra("text",text);
                startActivity(myIntent);
            }
        });
    }

    private void loadAllClasses() {
        timeTableArrayList =new ArrayList<>();

        FirebaseFirestore.getInstance().collection("timeTable").document(firebaseAuth.getUid()).collection(day)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelTimeTable modelTimeTable = document.toObject(ModelTimeTable.class);
                                timeTableArrayList.add(modelTimeTable);
                            }
                            Collections.sort(timeTableArrayList, new Comparator<ModelTimeTable>() {
                                @Override
                                public int compare(ModelTimeTable t1, ModelTimeTable t2) {
                                    return t1.getStartTime().compareToIgnoreCase(t2.getStartTime());
                                }
                            });
                            Collections.reverse(timeTableArrayList);

                            LinearLayoutManager layoutManager = new LinearLayoutManager(TimeTableActivity.this, LinearLayoutManager.VERTICAL, false);
                            binding.timeTableRv.setLayoutManager(layoutManager);

                            binding.timeTableRv.setLayoutManager(new LinearLayoutManager(TimeTableActivity.this));

                            adapterTimeTable = new AdapterTimeTable(TimeTableActivity.this, timeTableArrayList);
                            binding.timeTableRv.setAdapter(adapterTimeTable);
                        }
                    }
                });
    }
}