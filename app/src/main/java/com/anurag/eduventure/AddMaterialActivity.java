package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivityAddMaterialBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddMaterialActivity extends AppCompatActivity {
    ActivityAddMaterialBinding binding;
    private String semester, branch, subName, subTopic;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Uri pdfUri = null;

    private static final int PDF_PICK_CODE = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMaterialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.semTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddMaterialActivity.this);
                builder.setTitle("Select Semester:")
                        .setItems(Constants.semesterCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedSemester = Constants.semesterCategories[i];
                                binding.semTv.setText(selectedSemester);
                            }
                        }).show();
            }
        });
        binding.branchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddMaterialActivity.this);
                builder.setTitle("Select Branch:")
                        .setItems(Constants.branchCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedBranch = Constants.branchCategories[i];
                                binding.branchTv.setText(selectedBranch);
                            }
                        }).show();
            }
        });
        binding.pickPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });
        binding.uploadMaterialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {

        subName = binding.subNameEt.getText().toString().trim();
        subTopic = binding.subTopicEt.getText().toString().trim();
        semester = binding.semTv.getText().toString().trim();
        branch = binding.branchTv.getText().toString().trim();

        if (TextUtils.isEmpty(subName)) {
            Toast.makeText(AddMaterialActivity.this, "Enter Subject Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(subTopic)) {
            Toast.makeText(AddMaterialActivity.this, "Enter Subject Topic", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(semester)) {
            Toast.makeText(AddMaterialActivity.this, "Select Semester", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(branch)) {
            Toast.makeText(AddMaterialActivity.this, "Select Branch", Toast.LENGTH_SHORT).show();
        } else if (pdfUri == null) {
            Toast.makeText(AddMaterialActivity.this, "Pick Material Pdf", Toast.LENGTH_SHORT).show();
        } else {
            uploadPdfToStorage();
        }
    }

    private void uploadPdfToStorage() {
        progressDialog.setMessage("Uploading Material");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();
        String filePathAndName = "Material/" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String uploadedMaterialUrl = "" + uriTask.getResult();

                        uploadPdfInfoToDb(uploadedMaterialUrl, timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddMaterialActivity.this, "Material pdf upload failed due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPdfInfoToDb(String uploadedMaterialUrl, long timestamp) {

        progressDialog.setMessage("Uploading Material Pdf into....");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("materialId", "" + timestamp);
        hashMap.put("subjectName", "" + subName);
        hashMap.put("topicName", "" + subTopic);
        hashMap.put("branch", "" + branch);
        hashMap.put("semester", "" + semester);
        hashMap.put("viewsCount", "0");
        hashMap.put("downloadsCount", "0");
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("url", "" + uploadedMaterialUrl);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Material");
        ref.child(branch).child(semester).child("" + timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();

                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/all", "New Material Uploaded", subTopic, AddMaterialActivity.this, "MaterialActivity");
                        notificationsSender.SendNotifications();

                        Toast.makeText(AddMaterialActivity.this, "Material Successfully uploaded....", Toast.LENGTH_SHORT).show();
                        clearText();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddMaterialActivity.this, " Failed to upload to db due to" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
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
    private void clearText() {
        binding.subNameEt.setText("");
        binding.subTopicEt.setText("");
        binding. branchTv.setText("");
        binding.semTv.setText("");
        pdfUri = null;
    }
}