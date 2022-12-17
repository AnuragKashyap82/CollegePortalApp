package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivityBookViewActivittyBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import static com.anurag.eduventure.AttendanceActivity.joiningCode;

public class BookViewActivity extends AppCompatActivity {

    private ActivityBookViewActivittyBinding binding;
    private String timestamp;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookViewActivittyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        timestamp = getIntent().getStringExtra("timestamp");
        loadBookDetails(timestamp);
        loadNoQuantityCount(timestamp);

        binding.issueBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                issueBook(timestamp);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAlreadyIssued(timestamp);
        loadNoQuantityCount(timestamp);
    }

    private void checkAlreadyIssued(String timestamp) {
        DocumentReference documentReference  = FirebaseFirestore.getInstance().collection("issuedApplied").document(firebaseAuth.getUid());
        documentReference.collection("Books").document(timestamp)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()){
                            binding.issueBookBtn.setEnabled(false);
                            binding.issueBookBtn.setText("Already Issued");
                        }else {
                            checkAlreadyIssuedDone(timestamp);
                        }
                    }
                });
    }

    private void checkAlreadyIssuedDone(String timestamp) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("issuedBooks").document(firebaseAuth.getUid());
        documentReference.collection("Books").document(timestamp)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()){
                            binding.issueBookBtn.setEnabled(false);
                            binding.issueBookBtn.setText("Already Issued");
                        }else {
                            checkAlreadyIssuedDone(timestamp);
                        }
                    }
                });
    }

    private void loadBookDetails(String timestamp) {
        binding.progressBar.setVisibility(View.VISIBLE);
        DocumentReference documentReference  = FirebaseFirestore.getInstance().collection("Books").document(timestamp);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String subjectName = snapshot.get("subjectName").toString();
                String bookName = snapshot.get("bookName").toString();
                String authorName = snapshot.get("authorName").toString();
                String bookId = snapshot.get("bookId").toString();

                binding.subjectNameTv.setText(subjectName);
                binding.bookNameTv.setText(bookName);
                binding.authorNameTv.setText(authorName);
                binding.bookNoTv.setText(bookId);

            }
        });
    }

    private void loadNoQuantityCount(String timestamp) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Books").child(""+timestamp);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String availableQuantity = snapshot.child("booksQuantity").getValue().toString();
                    binding.quantityTv.setText(""+availableQuantity);
                    binding.progressBar.setVisibility(View.GONE);

                    if (availableQuantity.equals("0")){
                        binding.issueBookBtn.setEnabled(false);
                        binding.issueBookBtn.setText("Not Available");
                    }else {

                    }

                }else {
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void issueBook(String timestamp) {
        binding.issueBookBtn.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        String date = currentDay + "-" + currentMonth + "-" + currentYear;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("appliedDate", ""+date);
        hashMap.put("uid", ""+firebaseAuth.getUid());

        DocumentReference documentReference  =  FirebaseFirestore.getInstance().collection("issuedApplied").document(firebaseAuth.getUid());
        documentReference.collection("Books").document(timestamp)
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            HashMap<String, Object> hashMap1 = new HashMap<>();
                            hashMap1.put("uid", ""+firebaseAuth.getUid());
                            DocumentReference documentReference1 =  FirebaseFirestore.getInstance().collection("issuedApplied").document(firebaseAuth.getUid());
                            documentReference1.set(hashMap1);
                            decreaseBookQuantityCount(timestamp);
                        }else {
                            Toast.makeText(BookViewActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            binding.issueBookBtn.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BookViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.issueBookBtn.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void decreaseBookQuantityCount(String timestamp) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Books");
        databaseReference.child(timestamp)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String booksQuantity = "" + snapshot.child("booksQuantity").getValue();
                            if (booksQuantity.equals("") || booksQuantity.equals("null")) {
                                booksQuantity = "0";
                            }
                            long newBooksQuantity = Long.parseLong(booksQuantity) - 1;
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("booksQuantity", ""+newBooksQuantity);

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
                            reference.child(timestamp).updateChildren(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                binding.issueBookBtn.setVisibility(View.VISIBLE);
                                                binding.progressBar.setVisibility(View.GONE);
                                            }else {

                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}