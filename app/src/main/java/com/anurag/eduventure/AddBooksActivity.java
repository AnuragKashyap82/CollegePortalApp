package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivityAddBooksBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddBooksActivity extends AppCompatActivity {
    private ActivityAddBooksBinding binding;
    private FirebaseAuth firebaseAuth;
    private String subjectName, bookName, authorName, bookId, bookQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityAddBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.uploadBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {
        subjectName = binding.subjectEt.getText().toString().trim();
        bookName = binding.bookNameEt.getText().toString().trim();
        authorName = binding.authorNameEt.getText().toString().trim();
        bookId = binding.bookNoEt.getText().toString().trim();
        bookQuantity = binding.booksQuantityEt.getText().toString().trim();

        if (subjectName.isEmpty()){
            Toast.makeText(this, "Enter Subject Name!!!", Toast.LENGTH_SHORT).show();
        }else if (bookName.isEmpty()){
            Toast.makeText(this, "Enter Book Name!!!", Toast.LENGTH_SHORT).show();
        }else if (authorName.isEmpty()){
            Toast.makeText(this, "Enter Author Name!!!", Toast.LENGTH_SHORT).show();
        }else if (bookId.isEmpty()){
            Toast.makeText(this, "Enter Book Id/Book No.!!!", Toast.LENGTH_SHORT).show();
        }else if (bookQuantity.isEmpty()){
            Toast.makeText(this, "Enter Books Quantity!!!", Toast.LENGTH_SHORT).show();
        }else {
            uploadBookData();
        }
    }

    private void uploadBookData() {
        binding.uploadBookBtn.setVisibility(View.GONE);
        long timestamp = System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("subjectName", ""+subjectName);
        hashMap.put("bookName", ""+bookName);
        hashMap.put("authorName", ""+authorName);
        hashMap.put("bookId", ""+bookId);
        hashMap.put("timestamp", ""+timestamp);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Books").document(""+timestamp);
        documentReference.set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AddBooksActivity.this, "Books Uploaded Successfully!!!", Toast.LENGTH_SHORT).show();
                            binding.bookNameEt.setText("");
                            binding.subjectEt.setText("");
                            binding.authorNameEt.setText("");
                            binding.bookNoEt.setText("");
                            binding.booksQuantityEt.setText("");
                            uploadBooksBooksCount(timestamp);
                        }else {
                            Toast.makeText(AddBooksActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            binding.uploadBookBtn.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddBooksActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.uploadBookBtn.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void uploadBooksBooksCount(long timestamp) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("booksQuantity", bookQuantity);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Books").child(""+timestamp);
        databaseReference.setValue(hashMap);
        binding.uploadBookBtn.setVisibility(View.VISIBLE);
    }
}