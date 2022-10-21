package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivityAddStudentBinding;
import com.anurag.eduventure.databinding.ActivityAddUniqueIdBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddStudentActivity extends AppCompatActivity {
    ActivityAddStudentBinding binding;
    private String name, semester, branch, uniqueId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.semTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddStudentActivity.this);
                builder.setTitle("Select Semester:")
                        .setItems(Constants.semesterCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedUser = Constants.semesterCategories[i];
                                binding.semTv.setText(selectedUser);
                            }
                        }).show();
            }
        });
        binding.branchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddStudentActivity.this);
                builder.setTitle("Select Branch:")
                        .setItems(Constants.branchCategories, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedUser = Constants.branchCategories[i];
                                binding.branchTv.setText(selectedUser);
                            }
                        }).show();
            }
        });
        binding.addStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {
        name = binding.nameEt.getText().toString().trim();
        semester = binding.semTv.getText().toString().trim();
        branch = binding.branchTv.getText().toString().trim();
        uniqueId = binding.uniqueIdEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter Name Of the Student....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(semester)) {
            Toast.makeText(this, "Select Semester Of the Student....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(branch)) {
            Toast.makeText(this, "Select branch of the Student....!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(uniqueId)) {
            Toast.makeText(this, "Enter Unique_Id  of the Student....!", Toast.LENGTH_SHORT).show();
        } else {
            addStudentToDatabase();
        }
    }

    private void addStudentToDatabase() {
        progressDialog.setMessage("Uploading Student Details");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "" + name);
        hashMap.put("Semester", "" + semester);
        hashMap.put("branch", "" + branch);
        hashMap.put("uniqueId", "" + uniqueId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("students");
        ref.child(semester).child(branch).child(name)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        clearText();
                        Toast.makeText(AddStudentActivity.this, "Student details Successfully Uploaded....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddStudentActivity.this, " Failed to upload to db due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearText() {
        binding.nameEt.setText("");
    }
}