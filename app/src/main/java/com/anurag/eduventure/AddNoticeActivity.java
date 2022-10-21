package com.anurag.eduventure;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivityAddNoticeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.FirestoreGrpc;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddNoticeActivity extends AppCompatActivity {

    ActivityAddNoticeBinding binding;
    private FirebaseAuth firebaseAuth;
    private Uri pdfUri = null;
    private FirebaseFirestore firebaseFirestore;
    private static final int PDF_PICK_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.pdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });

        binding.uploadNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private String title = "", number = "";
    private void validateData() {

        title = binding.noticeTitleEt.getText().toString().trim();
        number = binding.noticeNoEt.getText().toString().trim();


        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Enter Notice Title....", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Enter Notice Number....", Toast.LENGTH_SHORT).show();
        }else if (pdfUri == null) {
            Toast.makeText(this, "Pick Notice Pdf", Toast.LENGTH_SHORT).show();
        } else {
            uploadPdfToStorage();
        }
    }
    private void uploadPdfToStorage() {
            binding.progressBar.setVisibility(View.VISIBLE);

            long timestamp = System.currentTimeMillis();
            String filePathAndName = "Notice/" + timestamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadedPdfUrl = ""+uriTask.getResult();

                        uploadPdfInfoToDb(uploadedPdfUrl, timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddNoticeActivity.this, "Notice pdf upload failed due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPdfInfoToDb(String uploadedPdfUrl, long timestamp) {

        binding.progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("NoticeId", ""+timestamp);
        hashMap.put("title", ""+title);
        hashMap.put("number", ""+number);
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("url", ""+uploadedPdfUrl);

        DocumentReference documentReference = firebaseFirestore.collection("Notice").document(""+timestamp);
        documentReference.set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.progressBar.setVisibility(View.GONE);

                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/all", "New Notice Uploaded", title, AddNoticeActivity.this, "Notice");
                        notificationsSender.SendNotifications();

                        Toast.makeText(AddNoticeActivity.this, "Notice Successfully uploaded....", Toast.LENGTH_SHORT).show();
                        clearData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddNoticeActivity.this, " Failed to upload to db due to"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notice");
//        ref.child(""+timestamp)
//                .setValue(hashMap)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        binding.progressBar.setVisibility(View.GONE);
//
//                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/all", "New Notice Uploaded", title, AddNoticeActivity.this, "Notice");
//                        notificationsSender.SendNotifications();
//
//                        Toast.makeText(AddNoticeActivity.this, "Notice Successfully uploaded....", Toast.LENGTH_SHORT).show();
//                        clearData();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        binding.progressBar.setVisibility(View.GONE);
//                        Toast.makeText(AddNoticeActivity.this, " Failed to upload to db due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                    }
//                });
    }
    private void pdfPickIntent() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PDF_PICK_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PDF_PICK_CODE) {

                pdfUri = data.getData();

            }
        } else {
            Toast.makeText(this, "Cancelled picking pdf", Toast.LENGTH_SHORT).show();

        }
    }
    private void clearData() {
        pdfUri = null;
        binding.noticeNoEt.setText("");
        binding.noticeTitleEt.setText("");
    }

}