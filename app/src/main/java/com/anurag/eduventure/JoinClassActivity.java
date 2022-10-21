package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivityJoinClassBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.HashMap;

public class JoinClassActivity extends AppCompatActivity {
    ActivityJoinClassBinding binding;
    private String joiningCode;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJoinClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.joinClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {
        joiningCode = binding.joiningCodeEt.getText().toString().trim();

        if (TextUtils.isEmpty(joiningCode)) {
            Toast.makeText(JoinClassActivity.this, "Enter Class Code.....!!!!!!", Toast.LENGTH_SHORT).show();
        } else {
            checkClassCode(joiningCode);
        }
    }

    private void checkClassCode(String joiningCode) {
        progressDialog.setMessage("Checking Class Code");
        progressDialog.show();

        DocumentReference documentReference  = firebaseFirestore.collection("classroom").document(joiningCode);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot.exists()) {
                    checkAlreadyJoined(joiningCode);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(JoinClassActivity.this, "This Class Code does not exist...!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkAlreadyJoined(String joiningCode) {
        progressDialog.setMessage("Joining Class");
        progressDialog.show();

        DocumentReference documentReference  = firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).collection("classroom").document(joiningCode);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot.exists()) {
                    progressDialog.dismiss();
                    Toast.makeText(JoinClassActivity.this, "You have already joined this Class....!!!!", Toast.LENGTH_SHORT).show();
                } else {
                    joinClass(joiningCode);
                }
            }
        });

    }

    private void joinClass(String joiningCode) {
        progressDialog.setMessage("Joining Class");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("classCode", "" + joiningCode);

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).collection("classroom").document(joiningCode);
        documentReference.set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        clearText();
                        Toast.makeText(JoinClassActivity.this, "Class Joined Successfully....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        clearText();
                        Toast.makeText(JoinClassActivity.this, " Failed to Create Class to db due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void clearText() {
        binding.joiningCodeEt.setText("");
    }
}