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

import com.anurag.eduventure.databinding.ActivityPostClassroomMsgEditBinding;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class PostClassroomMsgEditActivity extends AppCompatActivity {
    ActivityPostClassroomMsgEditBinding binding;
    private String classCode, msgCode;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ProgressDialog progressDialog;
    private Uri pdfUri = null;
    private String classMsg;
    private static final int PDF_PICK_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostClassroomMsgEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        classCode = getIntent().getStringExtra("classCode");
        msgCode = getIntent().getStringExtra("msgCode");

        loadPostMessage(classCode, msgCode);

        binding.updatePostBtn.setOnClickListener(new View.OnClickListener() {
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

    private void loadPostMessage(String classCode, String msgCode) {
        DocumentReference documentReference  = firebaseFirestore.collection("classroom").document(classCode).collection("classMsg").document(msgCode);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot ds, @Nullable FirebaseFirestoreException error) {

                classMsg = "" + ds.getString("classMsg");
                binding.postTextEt.setText(classMsg);

            }
        });
    }
    private void validateData() {
         classMsg = binding.postTextEt.getText().toString().trim();

        if (TextUtils.isEmpty(classMsg)){
            Toast.makeText(PostClassroomMsgEditActivity.this, "Enter Your Message....!!!!", Toast.LENGTH_SHORT).show();
        }else if(pdfUri == null) {
            postClassMsg(classMsg);
        }else {
            uploadAttachmentToStorage(classMsg);
        }
    }
    private void postClassMsg(String classMsg) {
        progressDialog.setMessage("Updating Message in Classroom....");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("classMsg", "" + classMsg);
        hashMap.put("classCode", "" + classCode);
        hashMap.put("timestamp", "" + msgCode);

        DocumentReference documentReference = firebaseFirestore.collection("classroom").document(classCode).collection("classMsg").document(msgCode);
        documentReference
                .update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(PostClassroomMsgEditActivity.this, "Message Successfully Updated....", Toast.LENGTH_SHORT).show();
                        clearText();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(PostClassroomMsgEditActivity.this, " Failed to Post msg due to" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void uploadAttachmentToStorage(String classMsg) {
        progressDialog.setMessage("Uploading Attachment");
        progressDialog.show();

        String filePathAndName = "Classroom Attachments/" + msgCode;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String uploadedAttachmentUrl = "" + uriTask.getResult();
                        uploadAttachmentToDb(uploadedAttachmentUrl, msgCode, classMsg);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(PostClassroomMsgEditActivity.this, "Attachment upload failed due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadAttachmentToDb(String uploadedAttachmentUrl, String msgCode, String classMsg) {
        progressDialog.setMessage("Updating Material Pdf into....");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("classMsg", "" + classMsg);
        hashMap.put("classCode", "" + classCode);
        hashMap.put("timestamp", "" + msgCode);
        hashMap.put("url", "" + uploadedAttachmentUrl);

        DocumentReference documentReference = firebaseFirestore.collection("classroom").document(classCode).collection("classMsg").document(msgCode);
        documentReference
                .update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(PostClassroomMsgEditActivity.this, "Attachment Successfully Updated....", Toast.LENGTH_SHORT).show();
                        clearText();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(PostClassroomMsgEditActivity.this, " Failed to upload to db due to" + e.getMessage(), Toast.LENGTH_SHORT).show();

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