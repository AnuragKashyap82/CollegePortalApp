package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;

import com.anurag.eduventure.databinding.ActivityClassroomMsgViewBinding;
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
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class ClassroomMsgViewActivity extends AppCompatActivity {
    ActivityClassroomMsgViewBinding binding;
    private String classCode, classMsg, timestamp, url, uid, msgCode;
    private Boolean isAttachmentExist = false;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClassroomMsgViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        classCode = getIntent().getStringExtra("classCode");
        classMsg = getIntent().getStringExtra("classMsg");
        timestamp = getIntent().getStringExtra("timestamp");
        url = getIntent().getStringExtra("url");
        uid = getIntent().getStringExtra("uid");
        isAttachmentExist = getIntent().getBooleanExtra("isAttachmentExist", isAttachmentExist);
        msgCode = getIntent().getStringExtra("msgCode");

        firebaseFirestore = FirebaseFirestore.getInstance();

        loadMsgSenderDetails();
        checkAttachmentExists();
        binding.msgTextTv.setText(classMsg);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateFormat = DateFormat.format("dd/MM/yyyy  K:mm a", calendar).toString();
        binding.dateTv.setText(dateFormat);

        binding.attachPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClassroomMsgViewActivity.this, ClassroomAttachViewActivity.class);
                intent.putExtra("classCode", classCode);
                intent.putExtra("timestamp", timestamp);
                intent.putExtra("classMsg", classMsg);
                intent.putExtra("url", url);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

    }

    private void checkAttachmentExists() {
        if (isAttachmentExist){
            binding.attachmentTv.setVisibility(View.VISIBLE);
            binding.attachPdfBtn.setVisibility(View.VISIBLE);
        }else {
            binding.attachmentTv.setVisibility(View.GONE);
            binding.attachPdfBtn.setVisibility(View.GONE);
        }
    }

    private void loadMsgSenderDetails() {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(uid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot ds, @Nullable FirebaseFirestoreException error) {

                String name = ""+ds.getString("name");
                String profileImage = ""+ds.getString("profileImage");

                binding.nameTv.setText(name);
                try {
                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(binding.profileIv);
                }
                catch (Exception e){
                    binding.profileIv.setImageResource(R.drawable.ic_person_gray);
                }

            }
        });
    }
}