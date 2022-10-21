package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivityAddAssignmentBinding;
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

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddAssignmentActivity extends AppCompatActivity {

    ActivityAddAssignmentBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private Uri pdfUri = null;
    private String classCode, assignmentName, dueDate, fullMarks;

    private static final int PDF_PICK_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        classCode = getIntent().getStringExtra("classCode");

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.pickPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });
        binding.uploadAssBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.fullMarksTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddAssignmentActivity.this);
                builder.setTitle("Select Full Marks:")
                        .setItems(Constants.marksCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedMarks = Constants.marksCategories[i];
                                binding.fullMarksTv.setText(selectedMarks);
                            }
                        }).show();
            }
        });
        binding.dueDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickDialog();
            }
        });
    }

    private void datePickDialog() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                DecimalFormat mFormat = new DecimalFormat("00");
                String pDay = mFormat.format(dayOfMonth);
                String pMonth = mFormat.format(monthOfYear);
                String pYear = "" + year;
                String pDate = pDay + "/" + pMonth + "/" + pYear;


                binding.dueDateTv.setText(pDate);
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
        datePickerDialog.getDatePicker();
    }


    private void validateData() {

        assignmentName = binding.assNameEt.getText().toString().trim();
        fullMarks = binding.fullMarksTv.getText().toString().trim();
        dueDate = binding.dueDateTv.getText().toString().trim();

        if (TextUtils.isEmpty(assignmentName)) {
            Toast.makeText(this, "Enter Assignment Name....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(fullMarks)) {
            Toast.makeText(this, "Select Full Marks....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(dueDate)) {
            Toast.makeText(this, "Select Due Date....!", Toast.LENGTH_SHORT).show();
        } else if (pdfUri == null) {
            Toast.makeText(this, "Pick Result Pdf", Toast.LENGTH_SHORT).show();
        } else {
            uploadPdfToStorage();
        }
    }

    private void uploadPdfToStorage() {
        binding.progressBar.setVisibility(View.VISIBLE);

        long timestamp = System.currentTimeMillis();
        String filePathAndName = "Assignment/" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String uploadedAssignmentUrl = "" + uriTask.getResult();

                        uploadAssignmentInfoToDb(uploadedAssignmentUrl, timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddAssignmentActivity.this, "Assignment pdf upload failed due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadAssignmentInfoToDb(String uploadedAssignmentUrl, long timestamp) {

        binding.progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("assignmentId", "" + timestamp);
        hashMap.put("classCode", "" + classCode);
        hashMap.put("assignmentName", "" + assignmentName);
        hashMap.put("fullMarks", "" + fullMarks);
        hashMap.put("dueDate", "" + dueDate);
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("url", "" + uploadedAssignmentUrl);

        DocumentReference documentReference = firebaseFirestore.collection("classroom").document(classCode).collection("assignment").document(""+ timestamp);
        documentReference.set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        binding.progressBar.setVisibility(View.GONE);
                        clearText();
                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/all", "New Assignment Uploaded", assignmentName, AddAssignmentActivity.this, "AssignmentActivity");
                        notificationsSender.SendNotifications();

                        Toast.makeText(AddAssignmentActivity.this, "Assignment Successfully uploaded....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddAssignmentActivity.this, " Failed to upload to db due to" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void clearText() {
        binding.assNameEt.setText("");
        binding.dueDateTv.setText("");
        binding.fullMarksTv.setText("");
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
}