package com.anurag.eduventure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.anurag.eduventure.databinding.ActivitySubmittedAssViewBinding;
import com.anurag.eduventure.databinding.DialogMarksObtainedBinding;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.util.HashMap;

public class SubmittedAssViewActivity extends AppCompatActivity {

    ActivitySubmittedAssViewBinding binding;
    private FirebaseAuth firebaseAuth;
    private String submittedAssUrl, assignmentId, assignmentName, classCode, dueDate, uid;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = ActivitySubmittedAssViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        submittedAssUrl = getIntent().getStringExtra("submittedAssUrl");
        assignmentId = getIntent().getStringExtra("assignmentId");
        classCode = getIntent().getStringExtra("classCode");
        assignmentName = getIntent().getStringExtra("assignmentName");
        dueDate = getIntent().getStringExtra("dueDate");
        uid = getIntent().getStringExtra("uid");

        binding.assignmentNameTv.setText(assignmentName);

        loadSubmittedAssFromUrl(submittedAssUrl);
        checkUser();
        loadMarks();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.addMarksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogMarksObtainedBinding marksAddBinding = DialogMarksObtainedBinding.inflate(LayoutInflater.from(SubmittedAssViewActivity.this));

                AlertDialog.Builder builder = new AlertDialog.Builder(SubmittedAssViewActivity.this, R.style.CustomDialog);
                builder.setView(marksAddBinding.getRoot());

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                marksAddBinding.backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                marksAddBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String marksObtained = marksAddBinding.marksEt.getText().toString().trim();
                        if (TextUtils.isEmpty(marksObtained)) {
                            Toast.makeText(SubmittedAssViewActivity.this, "Enter His Marks Obtained....!", Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();
                            uploadObtainedMarks(marksObtained);
                        }
                    }
                });
            }
        });
    }

    private void uploadObtainedMarks(String marksObtained) {

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("marksObtained", "" + marksObtained);

        DocumentReference documentReference = firebaseFirestore.collection("classroom").document(classCode).collection("assignment").document(assignmentId).collection("Submission").document(uid);
        documentReference
                .update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(SubmittedAssViewActivity.this, "Marks Uploaded....!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SubmittedAssViewActivity.this, " Failed to upload to db due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadSubmittedAssFromUrl(String submittedAssUrl) {
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(submittedAssUrl);
        reference.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        binding.submittedAssPdfView.fromBytes(bytes)
                                .swipeHorizontal(false)
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        int currentPage = (page + 1);
                                        binding.toolbarSubtitleTv.setText(currentPage + "/" + pageCount);

                                    }
                                })
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Toast.makeText(SubmittedAssViewActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Toast.makeText(SubmittedAssViewActivity.this, "Error on page" + page + " " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .load();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void loadMarks() {
        DocumentReference documentReference = firebaseFirestore.collection("classroom").document(classCode)
                .collection("assignment").document(assignmentId).collection("Submission").document(uid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String fullMarks = "" + value.getString("fullMarks");
                String marksObtained = "" + value.getString("marksObtained");

                binding.fullMarksTv.setText(fullMarks);
                binding.obtainedMarksTv.setText(marksObtained);
            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String userType = "" + snapshot.child("userType").getValue();
                        String name = "" + snapshot.child("name").getValue();

//                        if (userType.equals("user")) {
//                            binding.nameTv.setText(name);
//                        } else if (userType.equals("admin")) {
//                            binding.nameTv.setText(name);
//                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}