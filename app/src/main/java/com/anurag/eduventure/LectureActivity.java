package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.anurag.eduventure.Adapters.AdapterFeed;
import com.anurag.eduventure.Adapters.AdapterLecture;
import com.anurag.eduventure.Models.ModelFeed;
import com.anurag.eduventure.Models.ModelLecture;
import com.anurag.eduventure.databinding.ActivityLectureBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LectureActivity extends AppCompatActivity {
    ActivityLectureBinding binding;

    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelLecture> lectureArrayList;
    private AdapterLecture adapterlecture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLectureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadLectures();

    }
    private void loadLectures() {
        lectureArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("lectures");
        reference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lectureArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelLecture model = ds.getValue(ModelLecture.class);
                            lectureArrayList.add(model);
                        } Collections.sort(lectureArrayList, new Comparator<ModelLecture>() {
                            @Override
                            public int compare(ModelLecture t1, ModelLecture t2) {
                                return t1.getLectureId().compareToIgnoreCase(t2.getLectureId());
                            }
                        });
                        Collections.reverse(lectureArrayList);
                        adapterlecture = new AdapterLecture(LectureActivity.this, lectureArrayList);
                        binding.lecturesRv.setAdapter(adapterlecture);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}