package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivityPostClassroomMsgBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class PostClassroomMsgActivity extends AppCompatActivity {
    ActivityPostClassroomMsgBinding binding;
    private String classMsg, classCode;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ProgressDialog progressDialog;
    private Uri pdfUri = null;

    private static final int PDF_PICK_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostClassroomMsgBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        classCode = getIntent().getStringExtra("classCode");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.attachmentPickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });
        binding.pdfPickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });
    }



    private void validateData() {
        classMsg = binding.postTextEt.getText().toString().trim();

        if (TextUtils.isEmpty(classMsg)){
            Toast.makeText(PostClassroomMsgActivity.this, "Enter Your Message....!!!!", Toast.LENGTH_SHORT).show();
        }else if(pdfUri == null) {
            postClassMsg();
        }else {
            uploadAttachmentToStorage(classMsg);
        }

    }

    private void postClassMsg() {
        progressDialog.setMessage("Posting Message in Classroom....");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("classMsg", "" + classMsg);
        hashMap.put("classCode", "" + classCode);
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("attachmentExist", false);

        DocumentReference documentReference = firebaseFirestore.collection("classroom").document(classCode).collection("classMsg").document(""+timestamp);
        documentReference
                .set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(PostClassroomMsgActivity.this, "Message Successfully Posted....", Toast.LENGTH_SHORT).show();
                        clearText();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(PostClassroomMsgActivity.this, " Failed to Post msg due to" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void uploadAttachmentToStorage(String classMsg) {
        progressDialog.setMessage("Uploading Attachment");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();
        String filePathAndName = "Classroom Attachments/" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String uploadedAttachmentUrl = "" + uriTask.getResult();
                        uploadAttachmentToDb(uploadedAttachmentUrl, timestamp, classMsg);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(PostClassroomMsgActivity.this, "Attachment upload failed due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadAttachmentToDb(String uploadedAttachmentUrl, long timestamp, String classMsg) {
        progressDialog.setMessage("Uploading Material Pdf into....");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("classMsg", "" + classMsg);
        hashMap.put("classCode", "" + classCode);
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("attachmentExist", true);
        hashMap.put("url", "" + uploadedAttachmentUrl);

        DocumentReference documentReference = firebaseFirestore.collection("classroom").document(classCode).collection("classMsg").document(""+timestamp);
        documentReference
                .set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(PostClassroomMsgActivity.this, "Attachment Successfully uploaded....", Toast.LENGTH_SHORT).show();
                        clearText();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(PostClassroomMsgActivity.this, " Failed to upload to db due to" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void clearText() {
        binding.postTextEt.setText("");
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

        if(resultCode == Activity.RESULT_OK){
            if (requestCode == PDF_PICK_CODE){

                pdfUri = data.getData();

            }
        }
        else{
            Toast.makeText(this, "Cancelled picking pdf", Toast.LENGTH_SHORT).show();

        }
    }
}