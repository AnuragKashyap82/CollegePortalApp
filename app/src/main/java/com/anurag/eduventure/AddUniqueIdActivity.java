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

import com.anurag.eduventure.databinding.ActivityAddUniqueIdBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddUniqueIdActivity extends AppCompatActivity {

    private ActivityAddUniqueIdBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private String uniqueId, phone, userType;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUniqueIdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.userTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddUniqueIdActivity.this);
                builder.setTitle("Select User:")
                        .setItems(Constants.userType, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selectedUser = Constants.userType[i];
                                binding.userTv.setText(selectedUser);
                            }
                        }).show();
            }
        });

        binding.addUniqueIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {
        uniqueId = binding.uniqueIdEt.getText().toString().trim();
        phone = binding.phoneEt.getText().toString().trim();
        userType = binding.userTv.getText().toString().trim();

        if (TextUtils.isEmpty(uniqueId)) {
            Toast.makeText(this, "Enter Unique Id Of the Student....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Enter Phone No. Of the Student....!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userType)) {
            Toast.makeText(this, "Select the User Type....!", Toast.LENGTH_SHORT).show();
        } else {
            addUniqueId();
        }
    }

    private void addUniqueId() {
        progressDialog.setMessage("Uploading Unique_Id");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uniqueId", "" + uniqueId);
        hashMap.put("phone", "" + phone);
        hashMap.put("userType", "" + userType);

        DocumentReference documentReference = firebaseFirestore.collection("UniqueId").document(uniqueId);
        documentReference
                .set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        clearText();
                        Toast.makeText(AddUniqueIdActivity.this, "Unique_Id Successfully Uploaded....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddUniqueIdActivity.this, " Failed to upload to db due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearText() {
        binding.uniqueIdEt.setText("");
        binding.phoneEt.setText("");
        binding.userTv.setText("");
    }
}
