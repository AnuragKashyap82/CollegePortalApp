package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.anurag.eduventure.Adapters.AdapterClassroomPost;
import com.anurag.eduventure.Adapters.AdapterMaterial;
import com.anurag.eduventure.Adapters.AdapterNotice;
import com.anurag.eduventure.Models.ModelClassroom;
import com.anurag.eduventure.Models.ModelClassroomPost;
import com.anurag.eduventure.Models.ModelMaterial;
import com.anurag.eduventure.Models.ModelNotice;
import com.anurag.eduventure.databinding.ActivityClassroomViewBinding;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClassroomViewActivity extends AppCompatActivity {
    ActivityClassroomViewBinding binding;
    private String classCode;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ArrayList<ModelClassroomPost> classroomPostArrayList;
    private AdapterClassroomPost adapterClassroomPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClassroomViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        classCode = getIntent().getStringExtra("classCode");
        loadClassDetails();

        loadAllClassMessage();

        binding.assBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClassroomViewActivity.this, AllAssignmentActivity.class);
                intent.putExtra("classCode", classCode);
                startActivity(intent);
            }
        });
        binding.postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });
    }

    private void loadClassDetails() {

        DocumentReference documentReference  = firebaseFirestore.collection("classroom").document(classCode);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot ds, @Nullable FirebaseFirestoreException error) {
                String className = ""+ds.getString("className");
                String subjectName = ""+ds.getString("subjectName");

                binding.subjectNameTv.setText(subjectName);
            }
        });
    }
    private void loadAllClassMessage() {

        classroomPostArrayList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("classroom").document(classCode).collection("classMsg")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelClassroomPost modelClassroomPost = document.toObject(ModelClassroomPost.class);
                                classroomPostArrayList.add(modelClassroomPost);
                            }
                            Collections.sort(classroomPostArrayList, new Comparator<ModelClassroomPost>() {
                                @Override
                                public int compare(ModelClassroomPost t1, ModelClassroomPost t2) {
                                    return t1.getTimestamp().compareToIgnoreCase(t2.getTimestamp());
                                }
                            });
                            Collections.reverse(classroomPostArrayList);

                            LinearLayoutManager layoutManager = new LinearLayoutManager(ClassroomViewActivity.this, LinearLayoutManager.HORIZONTAL, false);
                            binding.classPostRv.setLayoutManager(layoutManager);

                            binding.classPostRv.setLayoutManager(new LinearLayoutManager(ClassroomViewActivity.this));

                            adapterClassroomPost = new AdapterClassroomPost(ClassroomViewActivity.this, classroomPostArrayList);
                            binding.classPostRv.setAdapter(adapterClassroomPost);
                        }
                    }
                });
    }
    private void showBottomSheet() {

        final Dialog dialog = new Dialog(ClassroomViewActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bs_class_post_options);

        LinearLayout postMsgLl = dialog.findViewById(R.id.postMsgLl);
        LinearLayout postVideoLl = dialog.findViewById(R.id.postVideoLl);
        LinearLayout askDoubtLl = dialog.findViewById(R.id.askDoubtLl);
        LinearLayout addAssLl = dialog.findViewById(R.id.addAssLl);
        LinearLayout addAttachmentLl = dialog.findViewById(R.id.addAttachmentLl);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


        postMsgLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent =new Intent(ClassroomViewActivity.this, PostClassroomMsgActivity.class);
                intent.putExtra("classCode", classCode);
                startActivity(intent);
            }
        });

        postVideoLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        askDoubtLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        addAssLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(ClassroomViewActivity.this, AddAssignmentActivity.class);
                intent.putExtra("classCode", classCode);
                startActivity(intent);
            }
        });
        addAttachmentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
    }
}