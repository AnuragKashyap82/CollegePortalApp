package com.anurag.eduventure;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.anurag.eduventure.Adapters.AdapterNotice;
import com.anurag.eduventure.Models.ModelNotice;
import com.anurag.eduventure.databinding.ActivityNoticeBinding;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

public class NoticeActivity extends AppCompatActivity {

    ActivityNoticeBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ArrayList<ModelNotice> noticeList;
    private AdapterNotice adapterNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        loadAllNotice();

        binding.addNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoticeActivity.this, AddNoticeActivity.class);
                startActivity(intent);
                Toast.makeText(NoticeActivity.this, "Add Notice Btn Clicked....!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.searchNoticeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterNotice.getFilter().filter(charSequence);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

//    private void loadAllNotice() {
//        noticeList =new ArrayList<>();
//        FirebaseFirestore.getInstance().collection("Notice")
//                .get()
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                        noticeList.clear();
//                        for (DataSnapshot ds: snapshot.getChildren()){
//                            ModelNotice modelNotice = ds.getValue(ModelNotice.class);
//                            noticeList.add(modelNotice);
//                        }
//                        Collections.sort(noticeList, new Comparator<ModelNotice>() {
//                            @Override
//                            public int compare(ModelNotice t1, ModelNotice t2) {
//                                return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
//                            }
//                        });
//                        Collections.reverse(noticeList);
//
//                        LinearLayoutManager layoutManager = new LinearLayoutManager(NoticeActivity.this, LinearLayoutManager.HORIZONTAL, false);
//                        binding.noticeRv.setLayoutManager(layoutManager);
//
//                         binding.noticeRv.setLayoutManager(new LinearLayoutManager(NoticeActivity.this));
//
//                        adapterNotice = new AdapterNotice(NoticeActivity.this, noticeList);
//                        binding.noticeRv.setAdapter(adapterNotice);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }

    private void loadAllNotice() {
        noticeList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Notice")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelNotice modelNotice = document.toObject(ModelNotice.class);
                                noticeList.add(modelNotice);
                            }
                            Collections.sort(noticeList, new Comparator<ModelNotice>() {
                                @Override
                                public int compare(ModelNotice t1, ModelNotice t2) {
                                    return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
                                }
                            });
                            Collections.reverse(noticeList);

                            LinearLayoutManager layoutManager = new LinearLayoutManager(NoticeActivity.this, LinearLayoutManager.HORIZONTAL, false);
                            binding.noticeRv.setLayoutManager(layoutManager);

                            binding.noticeRv.setLayoutManager(new LinearLayoutManager(NoticeActivity.this));

                            adapterNotice = new AdapterNotice(NoticeActivity.this, noticeList);
                            binding.noticeRv.setAdapter(adapterNotice);
                        }
                    }
                });
    }
}